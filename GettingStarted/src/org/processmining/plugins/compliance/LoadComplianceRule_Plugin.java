/**
 * 
 */
package org.processmining.plugins.compliance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
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
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.elements.InhibitorArc;
import org.processmining.models.graphbased.directed.petrinet.elements.ResetArc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.compliance.temporal.CreateTemporalPattern_Plugin;
import org.processmining.plugins.pnml.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

/**
 * @author dfahland
 * 
 */
@Plugin(name = "Load Compliance Rule from Repository",
	returnLabels = { "Compliance Pattern", "Initial Marking" },
	returnTypes = { ResetInhibitorNet.class, Marking.class },
	parameterLabels = { }, 
	help = "Check an event log for compliance based on a collection of compliance patterns.", userAccessible = true)
public class LoadComplianceRule_Plugin {

	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Check Compliance of a Log", requiredParameterLabels = { })
	public Object[] replayLog(UIPluginContext context) {

		Map<String, List<File>> complianceRules = getComplianceRules();
		if (complianceRules != null) {
			LoadComplianceRule_UI ui = new LoadComplianceRule_UI(complianceRules);
			Configuration config = new Configuration();
			if (ui.setParameters(context, config) != InteractionResult.CANCEL) {
				
				File pattern = config.compliancePatterns.get(0);
				
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
					return cancel(context, "Could not read "+pattern.getName());
				}
				/*
				 * PNML file has been imported. Now we need to convert the contents to a
				 * Reset/Inhibitor net.
				 */
				ResetInhibitorNet net = PetrinetFactory.newResetInhibitorNet(pnml.getLabel() + " (imported from " + pattern.getName() + ")");
				if (net == null) {
					return cancel(context, "Could not read "+pattern.getName()+" into a Petri net");
				}
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
				
				for (Transition t : net.getTransitions()) {
					if (t.getLabel().toLowerCase().equalsIgnoreCase("tau") || t.getLabel().toLowerCase().startsWith("final") || t.getLabel().toLowerCase().startsWith("f")
						|| t.getLabel().toLowerCase().equals(CreateTemporalPattern_Plugin.T_INSTANCE_START.toLowerCase())
						|| t.getLabel().toLowerCase().equals(CreateTemporalPattern_Plugin.T_INSTANCE_COMPLETE.toLowerCase())
						|| t.getLabel().toLowerCase().equals(CreateTemporalPattern_Plugin.T_START.toLowerCase())
						|| t.getLabel().toLowerCase().equals(CreateTemporalPattern_Plugin.T_END.toLowerCase()))
					{
						t.setInvisible(true);
					}
				}
				
//				if (!ir_net) {
//					net = PetrinetFactory.newPetrinet(pnml.getLabel() + " (imported from " + pattern.getName() + ")");
//					promNet = connectNet(context, pnml, net);
//				}
				
				return promNet;

			} else {
				return cancel(context, "Plugin cancelled by user.");
			}
		} else {
			return cancel(context, "Could not find patterns.");
		}
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
	
	public static class Configuration {
		public List<File> compliancePatterns = new ArrayList<File>();
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
