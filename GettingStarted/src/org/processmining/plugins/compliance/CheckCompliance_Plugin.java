/**
 * 
 */
package org.processmining.plugins.compliance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.packages.PackageDescriptor;
import org.processmining.framework.packages.PackageManager;
import org.processmining.framework.packages.UnknownPackageException;
import org.processmining.framework.packages.UnknownPackageTypeException;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.InhibitorArc;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.pnml.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

/**
 * @author dfahland
 * 
 */
@Plugin(name = "Check Compliance of a Log",
	returnLabels = { "Compliance Diagnostics", "Abstracted Log" },
	returnTypes = { PNRepResult.class, XLog.class },
	parameterLabels = {"Event Log" }, 
	help = "Check an event log for compliance based on a collection of compliance patterns.", userAccessible = true)
public class CheckCompliance_Plugin {

	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Check Compliance of a Log", requiredParameterLabels = { 0 })
	public Object[] replayLog(UIPluginContext context, XLog log) {
		
		Object[] finalResult = null;
		
		Map<String, List<File>> complianceRules = getComplianceRules();
		if (complianceRules != null) {
			LoadComplianceRule_UI ui = new LoadComplianceRule_UI(complianceRules);
			LoadComplianceRule_Plugin.Configuration config = new LoadComplianceRule_Plugin.Configuration();
			if (ui.setParameters(context, config) != InteractionResult.CANCEL) {
				
				System.out.println(config.compliancePatterns);
				for (File pattern : config.compliancePatterns) {
					
					System.out.println(pattern);
					
					PnmlImportUtils utils = new PnmlImportUtils();
					
					Pnml pnml = null;
					try {
						FileInputStream input = new FileInputStream(pattern);
						pnml = utils.importPnmlFromStream(context, input, pattern.getName(), pattern.length());
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("[Compliance]: could not read "+pattern.getName()+"\n"+e);						
					}
					if (pnml == null) {
						System.out.println("[Compliance]: could not read "+pattern.getName());
						continue;
					}
					/*
					 * PNML file has been imported. Now we need to convert the contents to a
					 * Reset/Inhibitor net.
					 */
					PetrinetGraph net = PetrinetFactory.newResetInhibitorNet(pnml.getLabel() + " (imported from " + pattern.getName() + ")");
					if (net == null) {
						System.out.println("[Compliance]: could not read "+pattern.getName()+" into a Petri net");
						continue;
					}
					// populate array with net (promNet[0]) and marking (promNet[1]) and keep this
					// array in memory when calling the replayer: only this way the marking and
					// the connection are available when the replayer is called, otherwise the
					// initial marking gets lost
					Object[] promNet = connectNet(context, pnml, net);
					
					boolean ir_net = false;
					for (Transition t : net.getTransitions()) {
						for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getInEdges(t)) {
							if (edge instanceof InhibitorArc || edge instanceof ResetArc) {
								ir_net = true;
							}
						}
						for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getOutEdges(t)) {
							if (edge instanceof InhibitorArc || edge instanceof ResetArc) {
								ir_net = true;
							}
						}
					}
					
					if (!ir_net) {
						net = PetrinetFactory.newPetrinet(pnml.getLabel() + " (imported from " + pattern.getName() + ")");
						promNet = connectNet(context, pnml, net);
					}
					
					CheckComplianceReplayer_Plugin plugin = new CheckComplianceReplayer_Plugin();
					Object[] result = null;
					try {
						result = plugin.replayLog(context, (PetrinetGraph)promNet[0], log);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("[Compliance]: could not check compliance of "+pattern.getName()+"\n"+e);
						continue;
					}

					finalResult = result;
				}
				
				
			} else {
				return cancel(context, "Plugin cancelled by user.");
			}
		} else {
			return cancel(context, "Could not find patterns.");
		}
		
		return finalResult;
	}
	
	public Object[] connectNet(PluginContext context, Pnml pnml, PetrinetGraph net) {
		/*
		 * Create a fresh marking.
		 */
		Marking marking = new Marking();

		GraphLayoutConnection layout = new GraphLayoutConnection(net);
		/*
		 * Initialize the Petri net and marking from the PNML element.
		 */
		pnml.convertToNet(net, marking, layout);

		/*
		 * Add a connection from the Petri net to the marking.
		 */
		InitialMarkingConnection c = context.addConnection(new InitialMarkingConnection(net, marking));
		context.addConnection(layout);
		System.out.println("connect initial marking: "+c);

		/*
		 * Return the net and the marking.
		 */
		Object[] objects = new Object[2];
		objects[0] = net;
		objects[1] = marking;
		return objects;
	}
	
	private String getLocalPatternPath(String pluginRoot) {
		return pluginRoot + File.separator + "lib" + File.separator + "genericPatterns";
	}
	
	private String getPatternPath() {
		// first try to read from local directory
		String localRoot = ".";
		String localPath = getLocalPatternPath(localRoot);
		File f = new File(localPath);
		if (f.exists() && f.isDirectory()) return localPath;
		
		// if that fails try to read from compliance package
		PackageManager manager = PackageManager.getInstance();
		try {
			PackageDescriptor[] packages = manager.findOrInstallPackages("Compliance");
			String pluginRoot = packages[0].getLocalPackageDirectory().getAbsolutePath();
			System.err.println("Trying to load patterns from "+pluginRoot);
			String pluginPath = getLocalPatternPath(pluginRoot);
			File fPlugin = new File(pluginPath);
			if (fPlugin.exists() && fPlugin.isDirectory()) {
				return pluginPath;
			} else {
				System.err.println("Could not locate directory "+pluginPath);
				return null;
			}
		} catch (UnknownPackageTypeException e) {
			System.err.println("Could not find package 'Compliance': "+e.getMessage());
			return null;
		} catch (UnknownPackageException e) {
			System.err.println("Could not find package 'Compliance': "+e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not find compliance patterns: "+e.getMessage());
			return null;
		}
	}
	
	private static class PnmlFilenameFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			if (name.lastIndexOf(".pnml") == name.length()-5) return true;
			else return false;
		}
	}
	
	private static final PnmlFilenameFilter pnmlFilenameFilter = new PnmlFilenameFilter(); 

	private Map<String, List<File>> getComplianceRules() {

		String patternPath = getPatternPath();
		if (patternPath == null) return null;
				
		File f = new File(patternPath);
		if (f == null || !f.isDirectory()) {
			patternPath = getLocalPatternPath(".");
		}
		
		f = new File(patternPath);
		if (f == null || !f.isDirectory()) {
			System.err.println(patternPath+" is no directory");
			return null;
		}
		
		Map<String, List<File>> patternClasses = new HashMap<String, List<File>>();
		for (File classes : f.listFiles()) {
			if (!classes.isDirectory()) continue;
			if (classes.getName().charAt(0) == '.') continue;
		
			patternClasses.put(classes.getName(), new LinkedList<File>());
			for (File pattern : classes.listFiles(pnmlFilenameFilter)) {
				if (pattern.isDirectory()) continue;
				patternClasses.get(classes.getName()).add(pattern);
			}
		}
		return patternClasses;
	}
	
	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[Compliance]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
