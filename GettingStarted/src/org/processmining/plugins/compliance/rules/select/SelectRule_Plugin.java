package org.processmining.plugins.compliance.rules.select;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.packages.PackageDescriptor;
import org.processmining.framework.packages.PackageManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.compliance.rules.configure.ConfigurationGuide;
import org.processmining.plugins.compliance.rules.importing.ConfigurationGuide_XMLImport_Plugin;
import org.processmining.plugins.compliance.rules.importing.RuleSelect_XMLImport_Plugin;
import org.processmining.plugins.compliance.rules.select.ui.SelectActivities_UI;
import org.processmining.plugins.compliance.rules.select.ui.SelectRule_UI;
import org.processmining.plugins.pnml.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

import de.congrace.exp4j.ExpressionBuilder;

@Plugin(name = "Select Configurable Compliance Rule (experimental)",
	parameterLabels = { "Log" }, 
	returnLabels = { "Configurable Compliance Rule", "Configuration Guide", "Initial Configuration" },
	returnTypes = { ConfigurablePetrinet.class, ConfigurationGuide.class, SelectRuleConfiguration.class },
	help = "Select a configurable compliance rule from the repository guided by questions (experimental plugin under development).", userAccessible = true)

public class SelectRule_Plugin {
	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland, Elham Ramezani",
			email = "d.fahland@tue.nl, e.ramezani@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Select Compliance Rule", requiredParameterLabels = { 0 })
	public Object[] selectComplianceRule(UIPluginContext context, XLog log) {

		try {
			Object[] result = run_selectComplianceRule(context, log);
			if (result != null && result.length == 1 && result[0] instanceof String) {
				return cancel(context, (String)result[0]);
			} else {
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return cancel(context, "Unable to select compliance rule: "+e.getMessage());
		}
	}
	
	public Object[] run_selectComplianceRule(UIPluginContext context, XLog log) throws Exception {
		SelectActivities_UI selectActivites = new SelectActivities_UI();
		Map<String,XEventClass> events = new HashMap<String,XEventClass>();

		if (selectActivites.setParameters(context, log, events, false, false) != InteractionResult.CANCEL) {
			List<XEventClass> eventClasses = selectActivites.getEventClasses();
			XEventClasses allEventClasses = selectActivites.getAllEventClasses();
			XEventClassifier classifier = selectActivites.getEventClassifier();
			
			RuleSelectGuide guide = loadRuleSelectGuide(context, events.size());
			if (guide != null) {
				QA_Configuration config = getInitialConfiguration(guide, events, eventClasses, allEventClasses, classifier);
				return selectComplianceRule(context, log, config, guide);
			} else
				return new Object[] { "No rule selection guide for "+events.size()+" events available" } ;
		} else {
			return new Object[] { "Cancelled by user." };
		}
			
	}
	
	public static class QA_Configuration {
		public List<RuleQuestion> enabledQuestions = new LinkedList<RuleQuestion>();
		public RuleAnswer chosenAnswer;
		public SelectRuleConfiguration ruleConfig = new SelectRuleConfiguration();
		
		public void update(QA_Configuration config) {
			this.chosenAnswer = config.chosenAnswer;
			this.ruleConfig.update(config.ruleConfig);
		}
	}
	
	private String getQuestionsPath(String pluginRoot) {
		return pluginRoot + File.separator + "lib" + File.separator + "ruleElicitation" + File.separator + "select";
	}
	
	private String getModelPath(String pluginRoot) {
		return pluginRoot + File.separator + "lib" + File.separator + "ruleElicitation" + File.separator + "ruleRepository";
	}
	
	private String getPackagePath() {
		PackageManager manager = PackageManager.getInstance();
		try {
			PackageDescriptor[] packages = manager.findOrInstallPackages("Compliance");
			String pluginRoot = packages[0].getLocalPackageDirectory().getAbsolutePath();
			return pluginRoot;
		} catch (Exception e) {
			System.err.println("Could not find compliance patterns");
			return null;
		}
	}
	
	private RuleSelectGuide loadRuleSelectGuideFromPath(String path, PluginContext context, int eventNum) {
		String questionFile = path + File.separator + eventNum + "ActQuestionnaire.xml";
		
		RuleSelect_XMLImport_Plugin ruleImport = new RuleSelect_XMLImport_Plugin();
		try {
			RuleSelectGuide guide = (RuleSelectGuide)ruleImport.importFile(context, questionFile);
			return guide;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private RuleSelectGuide loadRuleSelectGuide(PluginContext context, int eventNum) {
		RuleSelectGuide guide;
		// try to load rule from local path
		guide = loadRuleSelectGuideFromPath(getQuestionsPath("."), context, eventNum);
		if (guide == null) {
			// if not found, try to load from installed package
			guide = loadRuleSelectGuideFromPath(getQuestionsPath(getPackagePath()), context, eventNum);
		}
		return guide;
	}
	
	public Object[] selectComplianceRule(UIPluginContext context, XLog log, QA_Configuration config, RuleSelectGuide guide) {

		do {
			getAnswer(context, config, guide);
		} while (config.enabledQuestions.size() > 0);
		
		// we now have a complete configuration, configure the pattern
		if (config.chosenAnswer != null) {
			String rule = config.chosenAnswer.getConfiguredModel();
			
			ConfigurablePetrinet<? extends PetrinetGraph> configurableRule = loadConfigurableRule(context, rule);
			ConfigurationGuide ruleGuide = loadConfigurationGuide(context, rule);
			
			if (configurableRule == null) {
				return cancel(context, "Could not load "+rule);
			}
			if (ruleGuide == null) {
				return cancel(context, "Could not load configuration guide for "+rule);
			}
			
			// initialize partial rule configuration
			List<Configuration> partialConfig = config.chosenAnswer.getConfigurations();
			instantiateParametersInPartialConfig(partialConfig, config.ruleConfig.parameters);
			config.ruleConfig.setConfigurationForRule(configurableRule, partialConfig);
			
			return new Object[] {configurableRule, ruleGuide, config.ruleConfig};
		} else {
			return cancel(context, "No final answer was chosen.");
		}
	}
	
	private void instantiateParametersInPartialConfig(List<Configuration> partialConfig, Map<String, Integer> parameters) {
		
		// calculate value from a possibly complex expression
		Map<String, Double> dMap = new HashMap<String, Double>();
		for (String p : parameters.keySet()) dMap.put(p, (double)parameters.get(p));
		
		for (Configuration config : partialConfig) {
			for (String feature : config.keySet()) {
				Object value = config.get(feature);
				
				// check if value is an expression
				// first discard all String values that are not expressions
				if (   value == ConfigurableGraphElementOption.ALLOW
					|| value == ConfigurableGraphElementOption.BLOCK
					|| value == ConfigurableGraphElementOption.SKIP)
				{
					continue;
				}

				if (value instanceof String) {
					// then check, if it can be parsed and evaluated as an expression
					try {
						int val = (int)new ExpressionBuilder((String)value).withVariables(dMap).build().calculate();
						// yes, store the value
						config.put(feature, new Integer(val));
					} catch (Exception e) {
						System.out.println("Error when instantiating parameters: "+value+" is not a valid expression");
					}
				}
			}
		}
	}
	
	private Pnml loadRuleFromRepository(PluginContext context, String path) {
		PnmlImportUtils utils = new PnmlImportUtils();
		
		File pattern = new File(path);
		Pnml pnml = null;
		try {
			FileInputStream input = new FileInputStream(pattern);
			pnml = utils.importPnmlFromStream(context, input, pattern.getName(), pattern.length());
		} catch (Exception e) {
			return null;						
		}
		
		if (pnml == null) {
			return null;
		}
		
		return pnml;
	}
	
	private ConfigurationGuide loadConfigurationGuide(PluginContext context, String rule) {
		String rulePath = getModelPath(".") + File.separator + rule;
		rulePath = rulePath.substring(0, rulePath.lastIndexOf('.'));
		String ruleGuidePath = rulePath + ".xml";

		ConfigurationGuide_XMLImport_Plugin ruleConfigImport = new ConfigurationGuide_XMLImport_Plugin();
		ConfigurationGuide ruleGuide = null;
		try {
			ruleGuide = (ConfigurationGuide)ruleConfigImport.importFile(context, ruleGuidePath);
		} catch (Exception e) {}
		
		if (ruleGuide == null) {
			try {
				rulePath = getModelPath(getPackagePath()) + File.separator + rule;
				ruleGuidePath = rulePath + ".xml";
				ruleGuide = (ConfigurationGuide)ruleConfigImport.importFile(context, ruleGuidePath);	
			} catch (Exception e) {}
		}
		return ruleGuide;
	}
	
	private ConfigurablePetrinet<? extends PetrinetGraph> loadConfigurableRule(PluginContext context, String rule) {
		
		String rulePath = getModelPath(".") + File.separator + rule;
		
		Pnml pnml = null;
		try {
			pnml = loadRuleFromRepository(context, rulePath);
			if (pnml == null) {
				rulePath = getModelPath(getPackagePath()) + File.separator + rule;
				pnml = loadRuleFromRepository(context, rulePath);
			}
		} catch (Exception e) {
		}

		
		if (pnml == null) {
			return null;
		}
		
		// convert PNML to configurable petrinet
		ConfigurablePetrinet<? extends PetrinetGraph> net = new ConfigurableResetInhibitorNet(pnml.getLabel() + " (imported from " + rule + ")");
		Marking marking = new Marking();
		GraphLayoutConnection layout = new GraphLayoutConnection(net);
		// Initialize the Petri net and marking from the PNML element.
		pnml.convertToNet(net, marking, layout);

		context.addConnection(new InitialMarkingConnection(net, marking));
		context.addConnection(layout);
		
		return net;
	}

	/**
	 * Show the user a wizard from the enabled questions and let him pick an answer. The
	 * chosen answer is stored in {@link SelectRuleConfiguration#chosenAnswer} and the
	 * follow-up questions are stored in {@link SelectRuleConfiguration#enabledQuestions}.
	 * 
	 * @param context
	 * @param enabledQuestions
	 * @param guide
	 */
	private void getAnswer(UIPluginContext context, QA_Configuration config, RuleSelectGuide guide) {
		
		if (config.enabledQuestions.size() == 0) {
			config.chosenAnswer = null;
			return;
		}
		
		SelectRule_UI rule_ui = new SelectRule_UI();
		try {
			if (rule_ui.setParameters(context, config, config, false, false) != InteractionResult.CANCEL) {
				// update parameters based on settings in the answer
				for (String par : config.chosenAnswer.getParametersToSet().keySet()) {
					String value = config.chosenAnswer.getParametersToSet().get(par);
					
					// calculate value from a possibly complex expression
					Map<String, Double> dMap = new HashMap<String, Double>();
					for (String p : config.ruleConfig.parameters.keySet()) dMap.put(p, (double)config.ruleConfig.parameters.get(p));
					int val = (int)new ExpressionBuilder(value).withVariables(dMap).build().calculate();
					
					config.ruleConfig.parameters.put(par, val);
				}
				config.enabledQuestions = getFollowUpQuestion(config, guide);
				return;
			}
		} catch (Exception e) {
			context.log(e);
		}
		
		config.chosenAnswer = null;
		config.enabledQuestions.clear();
	}
	
	private QA_Configuration getInitialConfiguration(RuleSelectGuide guide, Map<String,XEventClass> events, List<XEventClass> eventClasses, XEventClasses allEventClasses, XEventClassifier classifier) {
		QA_Configuration config = new QA_Configuration();
		config.enabledQuestions.add(guide.getQuestions().get(0));

		config.ruleConfig.classifier = classifier;
		config.ruleConfig.allEventClasses = allEventClasses;
		config.ruleConfig.availableEventClasses.addAll(eventClasses);
		config.ruleConfig.activityMapping.putAll(events);
		
		int eventClassNum = 0;
		for (String activity : guide.getGlobalActivityNames()) {
			XEventClass preConfigured = events.get(activity);
			if (preConfigured == null) preConfigured = config.ruleConfig.availableEventClasses.get(eventClassNum);
			config.ruleConfig.activityMapping.put(activity, preConfigured);
			eventClassNum++;
		}
		return config;
	}
	
	private List<RuleQuestion> getFollowUpQuestion(QA_Configuration config, RuleSelectGuide guide) {
		List<RuleQuestion> questions = new LinkedList<RuleQuestion>();
		for (String id : config.chosenAnswer.getFollowUpQuestionID()) {
			for (RuleQuestion q : guide.getQuestions()){
				if (q.getId() != null && q.getId().equals(id)) {
					if (q.isEnabled(config.ruleConfig.parameters))
						questions.add(q);
				}
			}
		}
		return questions;
	}
	
	

	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[Select Compliance Rule]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		context.getFutureResult(2).cancel(true);
		return null;
	}

}
