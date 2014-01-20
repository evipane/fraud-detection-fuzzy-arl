/**
 * 
 */
package org.processmining.plugins.compliance;

import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;
import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * GUI to map event class (with any classifiers) to transitions of Petri net
 * 
 * @author aadrians
 * 
 */
public class LoadComplianceRule_UI extends ProMPropertiesPanel {

	private static final long serialVersionUID = 1L;

	public final static String DIALOG_TITLE = "Select Compliance Rules";
	
	private Map<JCheckBox, File> checkedPatterns;
	
	public LoadComplianceRule_UI(Map<String, List<File>> patterns) {
		super(DIALOG_TITLE);
		
		checkedPatterns = new java.util.HashMap<JCheckBox, File>();
		
		for (String className : patterns.keySet()) {
			
			String label = className+" ("+ patterns.get(className).size()+" rules)";
			JLabel l = addProperty(label, new JLabel());
			// resize the label for a better layout 
			l.getParent().getComponents()[1].setPreferredSize(new Dimension(800,30));
			// remove mouse listeners on this one: passive element
			l.getParent().setBackground(WidgetColors.HEADER_COLOR);
			for (MouseListener ml : l.getParent().getMouseListeners())
				l.getParent().removeMouseListener(ml);
			
			List<File> class_patterns = patterns.get(className);
			for (File pattern : class_patterns) {
				JCheckBox box = SlickerFactory.instance().createCheckBox(null, false);
				String patternName = pattern.getName();
				
				
				String fileName_explanation = pattern.getAbsolutePath()+".html";
				String explanation = readFile(fileName_explanation);
				if (explanation != null) {
					patternName = explanation;
				}
				
				addProperty(patternName, box);
				
				// resize the label for a better layout 
				box.getParent().getComponents()[1].setPreferredSize(new Dimension(800,30));
				checkedPatterns.put(box, pattern);
			}
		}
		
	}
	
	private String readFile(String fileName) {
		StringBuffer sb = new StringBuffer();

		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				sb.append(strLine);
			}
			//Close the input stream
			fstream.close();
		} catch (Exception e){//Catch exception if any
			return null;
		}
		return sb.toString();
	}
	
	
	
	/**
	 * display a dialog to ask user what to do
	 * 
	 * @param context
	 * @return
	 */
	protected InteractionResult getUserChoice(UIPluginContext context) {
		return context.showConfiguration(DIALOG_TITLE, this);
	}
	
	/**
	 * Open UI dialogue to populate the given configuration object with
	 * settings chosen by the user.
	 * 
	 * @param context
	 * @param config
	 * @return result of the user interaction
	 */
	public InteractionResult setParameters(UIPluginContext context, LoadComplianceRule_Plugin.Configuration config) {
		InteractionResult wish = getUserChoice(context);
		if (wish != InteractionResult.CANCEL) setParametersFromUI(config);
		return wish;
	}
	
	protected void setParametersFromUI(LoadComplianceRule_Plugin.Configuration config) {
		for (Map.Entry<JCheckBox, File> pattern : checkedPatterns.entrySet()) {
			if (pattern.getKey().isSelected()) {
				config.compliancePatterns.add(pattern.getValue());
			}
		}
	}
	
}
