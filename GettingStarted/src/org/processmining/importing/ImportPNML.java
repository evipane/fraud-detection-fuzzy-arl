package org.processmining.importing;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.Pnml;
import org.processmining.pnml.controller.ReadPNML;

@Plugin(name = "Import Petri net from PNML file", parameterLabels = { "Filename" }, returnLabels = { "Petri Net",
"Marking" }, returnTypes = { Petrinet.class, Marking.class,ReadPNML.class})
@UIImportPlugin(description = "PNML Petri net", extensions = { "pnml" })
public class ImportPNML extends AbstractImportPlugin{

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("PNML files", "pnml");
	}
	
	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		PNMLImportUtils utils = new PNMLImportUtils();
		Pnml pnml = utils.importPnmlFromStream(context, input, filename, fileSizeInBytes);
		//Pnml pnml = utils.importPnmlFromStream(context, input, filename, fileSizeInBytes);
		if (pnml == null) {
			/*
			 * No PNML found in file. Fail.
			 */
			return null;
		}
		/*
		 * PNML file has been imported. Now we need to convert the contents to a
		 * regular Petri net.
		 */
		PetrinetGraph net = PetrinetFactory.newPetrinet(pnml.getLabel() + " (imported from " + filename + ")");
		return utils.connectNet(context, pnml, net, this.getFile().getAbsolutePath());
	}
	

}
