package org.processmining.plugins.compliance.rules.ui.widgets;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;

public abstract class ProMWizardPanel<INPUT,OUTPUT> extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public ProMWizardPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	/**
	 * display a dialog to ask user what to do
	 * 
	 * @param context
	 * @return
	 */
	protected InteractionResult getUserChoice(UIPluginContext context, INPUT input, boolean first, boolean last) {
		initializeUIFromParameters(input);
		return context.showWizard(getTitle(), first, last, this);
	}
	
	/**
	 * Open UI dialogue to populate the given configuration object with
	 * settings chosen by the user.
	 * 
	 * @param context
	 * @param net
	 * @return result of the user interaction
	 * @throws Exception 
	 */
	public InteractionResult setParameters(UIPluginContext context, INPUT input, OUTPUT output, boolean first, boolean last) throws Exception {
		InteractionResult wish = getUserChoice(context, input, first, last);
		if (wish != InteractionResult.CANCEL) getParametersFromUI(output);
		return wish;
	}
	
	protected abstract void initializeUIFromParameters(INPUT input);
	
	protected abstract void getParametersFromUI(OUTPUT output) throws Exception;
	
	protected abstract String getTitle();

}

