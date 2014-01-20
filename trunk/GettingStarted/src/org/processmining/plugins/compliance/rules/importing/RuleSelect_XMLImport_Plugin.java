package org.processmining.plugins.compliance.rules.importing;


import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilderFactory;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.plugins.compliance.rules.select.RuleSelectGuide;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@Plugin(name = "Import Compliance Guide From XML", 
		parameterLabels = { "Filename" }, 
		returnLabels = { "Guide for Selecting Compliance Rules" },
		returnTypes = { RuleSelectGuide.class })
@UIImportPlugin(description = "Guide for Selecting Compliance Rules", extensions = { "xml" })
public class RuleSelect_XMLImport_Plugin extends AbstractImportPlugin {

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("Compliance Guide files", "xml");
	}

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//input = new ByteArrayInputStream(output.toByteArray());;
		InputSource source = new InputSource(input);
		Document document = factory.newDocumentBuilder().parse(source);
		
		RuleSelect_XMLImport xmlImport = new RuleSelect_XMLImport();
		RuleSelectGuide pattern = xmlImport.visit_document(document);
		
		System.out.println(pattern);
		
		return pattern;
	}
	
	
	
}

