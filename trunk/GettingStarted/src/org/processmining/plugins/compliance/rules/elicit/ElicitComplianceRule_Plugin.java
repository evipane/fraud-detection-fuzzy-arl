package org.processmining.plugins.compliance.rules.elicit;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.compliance.rules.configure.ConfigurationGuide;
import org.processmining.plugins.compliance.rules.configure.ConfigureRule_Plugin;
import org.processmining.plugins.compliance.rules.select.SelectRuleConfiguration;
import org.processmining.plugins.compliance.rules.select.SelectRule_Plugin;

@Plugin(name = "Elicit Compliance Rule (experimental)",
	parameterLabels = { "Log" }, 
	returnLabels = { "Compliance Rule", "Initial Marking" },
	returnTypes = { ResetInhibitorNet.class, Marking.class },
	help = "Select and configure a compliance rule guided by questions (experimental plugin under development).", userAccessible = true)
public class ElicitComplianceRule_Plugin {

	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland, Elham Ramezani",
			email = "d.fahland@tue.nl, e.ramezani@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Elicit Compliance Rule", requiredParameterLabels = { 0 })
	public Object[] guideConfiguration(UIPluginContext context, XLog log) {
		
		try {
			SelectRule_Plugin selectRule = new SelectRule_Plugin();
			Object[] rule_guide_config = selectRule.run_selectComplianceRule(context, log);
			if (rule_guide_config != null && rule_guide_config.length == 1 && rule_guide_config[0] instanceof String) {
				return cancel(context, (String)rule_guide_config[0]);
			}
			
			ConfigurablePetrinet<?> net = (ConfigurablePetrinet<?>)rule_guide_config[0];
			ConfigurationGuide guide = (ConfigurationGuide)rule_guide_config[1];
			SelectRuleConfiguration config = (SelectRuleConfiguration)rule_guide_config[2];
			
			ConfigureRule_Plugin configureRule = new ConfigureRule_Plugin();
			return configureRule.guideConfiguration(context, net, guide, config, log);
			
		} catch (Exception e) {
			e.printStackTrace();
			return cancel(context, "Unable to select compliance rule: "+e.getMessage());
		}
		
		
	}
	
	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[Elicit Compliance Rule]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		return null;
	}
}
