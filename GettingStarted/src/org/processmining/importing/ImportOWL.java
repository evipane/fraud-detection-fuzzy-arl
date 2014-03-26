package org.processmining.importing;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.owl.model.Ontologies;

@Plugin(name = "Import Ontology File", parameterLabels = { "Filename" }, returnLabels = { "OntologyFiles" }, returnTypes = { Ontologies.class})
@UIImportPlugin(description = "Ontology File", extensions = { "owl" })
public class ImportOWL extends AbstractImportPlugin
{
	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("OWL files", "owl");
	}
	
	protected Ontologies importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
	{
		System.out.println("Path Asli: " + this.getFile().getPath());
		Ontologies ont = new Ontologies(this.getFile().getPath());
		return ont;
	}
}
