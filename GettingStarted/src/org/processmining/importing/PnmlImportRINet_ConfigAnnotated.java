package org.processmining.importing;

import java.io.InputStream;
import java.util.List;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurationUtils;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.configurable.CreateConfigurableNet_Plugin;
import org.processmining.plugins.pnml.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

@Plugin(name = "Import Configurable Petri net from annotated PNML file", 
		parameterLabels = { "Filename" }, 
		returnLabels = { "Configurable Reset/Inhibitor Net" },
		returnTypes = { ConfigurableResetInhibitorNet.class })
@UIImportPlugin(description = "PNML Reset/Inhibitor net files (configuration annotated)", extensions = { "pnml" })
public class PnmlImportRINet_ConfigAnnotated extends AbstractImportPlugin {

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("PNML files", "pnml");
	}
	
	private Object[] connectNet(PluginContext context, Pnml pnml, PetrinetGraph net) {
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
		 * Return net, marking, and layout.
		 */
		Object[] objects = new Object[3];
		objects[0] = net;
		objects[1] = marking;
		objects[2] = layout;
		return objects;
	}

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		PnmlImportUtils utils = new PnmlImportUtils();
		Pnml pnml = utils.importPnmlFromStream(context, input, filename, fileSizeInBytes);
		if (pnml == null) {
			/*
			 * No PNML found in file. Fail.
			 */
			return null;
		}
		/*
		 * PNML file has been imported. Now we need to convert the contents to a
		 * Reset/Inhibitor net.
		 */
		PetrinetGraph net = PetrinetFactory.newResetInhibitorNet(pnml.getLabel() + " (imported from " + filename + ")");

		Object[] net_and_marking_and_layout = connectNet(context, pnml, net);
		
		ResetInhibitorNet rinet = (ResetInhibitorNet)net_and_marking_and_layout[0];
		Marking m_rinet = (Marking)net_and_marking_and_layout[1];
		GraphLayoutConnection ri_layout = (GraphLayoutConnection)net_and_marking_and_layout[2];
		
		List<ConfigurableFeatureGroup> configs = ConfigurationUtils.stripFeaturesFromLabels(rinet);
		
		Object[] configurable_net_and_layout = CreateConfigurableNet_Plugin.createConfigurableNet(rinet, m_rinet, ri_layout, configs);
		ConfigurableResetInhibitorNet configurableNet = (ConfigurableResetInhibitorNet)configurable_net_and_layout[0];
		GraphLayoutConnection configurableLayout = (GraphLayoutConnection)configurable_net_and_layout[1];
		
		// register layout with ProM
		context.addConnection(configurableLayout);
		
		return configurableNet;
	}
}
