package org.processmining.importing;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.pnml.controller.ReadPNML;

@Plugin(name = "Import Petri net from PNML file", parameterLabels = { "Filename" }, returnLabels = { "ReadPNML" }, returnTypes = { ReadPNML.class})
@UIImportPlugin(description = "PNML Petri net Extended", extensions = { "pnml" })
public class ImportPNML2 extends AbstractImportPlugin {
	
	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("PNML files", "pnml");
	}

	protected ReadPNML importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		
		ReadPNML read = new ReadPNML(this.getFile().getAbsolutePath());
		read.readPNML();
		
		
		return read;
	}
	

}
