package org.processmining.plugins.compliance.rules.configure;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.compliance.AbstractLogForCompliance_Plugin;
import org.processmining.plugins.compliance.align.PNLogReplayer;
import org.processmining.plugins.compliance.align.PNLogReplayer.ReplayParams;
import org.processmining.plugins.compliance.rules.configure.ui.ConfigureRule_UI;
import org.processmining.plugins.compliance.rules.select.SelectRuleConfiguration;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.configurable.ConfigurePetriNetCompletely_Plugin;
import org.processmining.plugins.petrinet.manifestreplayer.EvClassPattern;
import org.processmining.plugins.petrinet.manifestreplayer.TransClass2PatternMap;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.DefTransClassifier;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClasses;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

@Plugin(name = "Configure Compliance Rule (experimental)",
	parameterLabels = { "Configurable Compliance Rule", "Configuration Guide", "Initial Configuration", "Log"  }, 
	returnLabels = { "Compliance Rule", "Initial Marking" },
	returnTypes = { ResetInhibitorNet.class, Marking.class },
	help = "Configure a compliance rule guided by questions (experimental plugin under development).", userAccessible = true)

public class ConfigureRule_Plugin {
	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland, Elham Ramezani",
			email = "d.fahland@tue.nl, e.ramezani@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Configure Compliance Rule", requiredParameterLabels = { 0, 1, 3 })
	public Object[] guideConfiguration(UIPluginContext context, ConfigurablePetrinet<? extends PetrinetGraph> net, ConfigurationGuide guide, XLog log) {
		SelectRuleConfiguration config = new SelectRuleConfiguration();
		config.setEmptyConfiguration(net);
		
		try {
			return guideConfiguration(context, net, guide, config, log);
		} catch (ParseException e) {
			e.printStackTrace();
			return cancel(context,"Could not guide configuration: "+e.getMessage());
		}
	}
	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland, Elham Ramezani",
			email = "d.fahland@tue.nl, e.ramezani@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Configure Compliance Rule", requiredParameterLabels = { 0, 1, 2, 3 })
	public Object[] guideConfiguration(UIPluginContext context, ConfigurablePetrinet<? extends PetrinetGraph> net, ConfigurationGuide guide, SelectRuleConfiguration ruleConfig, XLog log) throws ParseException {

		QA_Configuration config = new QA_Configuration();
		config.ruleConfig = ruleConfig;
		
		stripTransitionIDs(net);
		
		List<String> errors = new LinkedList<String>();
		if (!guide.isValidFor(net, errors).isEmpty()) {
			produceErrorLog(context, errors);
			return cancel(context, "Cancelled. Configurable net and configuration guide are incompatible.");
		} else {
			do {
				
			} while (extendConfigurationByAnswer(context, log, config, guide, net));
			
			// we now have a complete configuration, configure the pattern
			
			List<Configuration> configuration = new LinkedList<Configuration>(config.ruleConfig.configuration.values());
			
			ConfigurePetriNetCompletely_Plugin configure = new ConfigurePetriNetCompletely_Plugin();
			return configure.configureNet(context, (ConfigurableResetInhibitorNet) net, configuration);
		}
	}
	
	protected void stripTransitionIDs(PetrinetGraph net) {
		for (Transition t : net.getTransitions()) {
			String label = t.getLabel(); 
			int split = label.indexOf('-');
			if (split >= 0) {
				t.getAttributeMap().put(AttributeMap.LABEL, label.substring(split+1));
			}
		}
	}
	
	
	public static class QA_Configuration {
		public List<RuleQuestion> enabledQuestions = new LinkedList<RuleQuestion>();
		public RuleAnswer chosenAnswer;
		public SelectRuleConfiguration ruleConfig = new SelectRuleConfiguration();
		
		public Map<RuleAnswer,List<SyncReplayResult>> previewTrace_compliant = new HashMap<RuleAnswer, List<SyncReplayResult>>();
		public Map<RuleAnswer,List<SyncReplayResult>> previewTrace_violating = new HashMap<RuleAnswer, List<SyncReplayResult>>();
		
		public void update(QA_Configuration config) {
			this.chosenAnswer = config.chosenAnswer;
			this.ruleConfig.update(config.ruleConfig);
		}
	}
	
	/**
	 * Extends the given configuration with new configuration values by answering one question
	 * from the configuration guide.
	 * 
	 * @param context
	 * @param config
	 * @param guide
	 * @param net
	 * @return true iff the configuration was extended and false
	 * @throws ParseException 
	 */
	private boolean extendConfigurationByAnswer(UIPluginContext context, XLog log, QA_Configuration config, ConfigurationGuide guide, ConfigurablePetrinet<? extends PetrinetGraph> net) throws ParseException {
		
		for (RuleQuestion q : guide.getQuestions()) {
			if (q.isEnabled(config.ruleConfig.configuration) && q.isEnabledForParameters(config.ruleConfig.parameters)) {
				config.enabledQuestions.add(q);
			}
		}
		
		if (config.enabledQuestions.size() == 0) return false;
		
		ConfigureRule_UI rule_ui = new ConfigureRule_UI();
		try {
			
			getCompliancePreview(context, log, config, net);
			
			if (rule_ui.setParameters(context, config, config, false, false) != InteractionResult.CANCEL) {
				RuleAnswer answer = config.chosenAnswer;
				if (answer == null) return false;

				updateConfigurationByAnswer(config.ruleConfig.configuration, answer, config.ruleConfig.parameters);
				return true;
			}
		} catch (Exception e) {
			context.log(e);
		}
		
		return false;
	}
	
	private void updateConfigurationByAnswer(Map<String, Configuration> partialConfig, RuleAnswer answer, Map<String, Integer> parameters) {
		System.out.println("update configuration by answer");
		for (Configuration answerSetConfig : answer.getConfigurations()) {
			String featureGroupId = answerSetConfig.getFeatureGroupId();
			// set all values of the answer configuration in the current configuration
			System.out.println("setting "+answerSetConfig+" for "+featureGroupId);

			// replace variable parameters with their concrete values based on the set parameters
			for (String param : answerSetConfig.keySet()) {
				if (answerSetConfig.get(param) instanceof String && parameters.containsKey(answerSetConfig.get(param))) {
					System.out.println("replacing parameter "+param+" by "+parameters.get(answerSetConfig.get(param)));
					answerSetConfig.put(param, parameters.get(answerSetConfig.get(param)));
				}
			}
			partialConfig.get(featureGroupId).putAll(answerSetConfig);
		}
	}
	
	protected void getCompliancePreview(PluginContext context, XLog log, QA_Configuration config, ConfigurablePetrinet<? extends PetrinetGraph> net) {
		List<RuleAnswer> answers = new LinkedList<RuleAnswer>();
		for (RuleQuestion q : config.enabledQuestions) {
			answers.addAll(q.getAnswers());
		}
		
		for (RuleAnswer a : answers) {
			config.previewTrace_compliant.put(a, new LinkedList<SyncReplayResult>());
			config.previewTrace_violating.put(a, new LinkedList<SyncReplayResult>());
			try {
				
				System.out.println("checking compliance for "+a.getText());
				
				ConfigurableResetInhibitorNet net_a = new ConfigurableResetInhibitorNet(net.getLabel());
				net_a.cloneFrom((ConfigurablePetrinet<ResetInhibitorNet>) net, null, null);
				
				Map<String, Configuration> c = config.ruleConfig.copyConfiguration();
				updateConfigurationByAnswer(c, a, config.ruleConfig.parameters);
				List<Configuration> configuration = new LinkedList<Configuration>(config.ruleConfig.configuration.values());
				
				net_a.configure(configuration);
				
				ResetInhibitorNet configured_a = net_a.getConfiguredNet();
				Marking marking_a = net_a.getConfiguredMarking();
				
				TransEvClassMapping tr2ev = getEventMap(configured_a, config.ruleConfig);
				TransClass2PatternMap patternMap = getPatternMap(configured_a, log, config.ruleConfig);
				HashMap<XEvent, XEvent> abstractToOriginal = new HashMap<XEvent, XEvent>();
				XLog abstractedLog = AbstractLogForCompliance_Plugin.abstractLog(log, configured_a, patternMap, config.ruleConfig.allEventClasses, abstractToOriginal);
				TransEvClassMapping mapping = AbstractLogForCompliance_Plugin.adoptMap(tr2ev, config.ruleConfig.classifier, abstractedLog);
				
				Marking m_final = (Marking)PNLogReplayer.constructFinalMarking(context, configured_a)[1];
				
				ReplayParams par = PNLogReplayer.getReplayerParameters(context, configured_a, abstractedLog, mapping.getEventClassifier(), marking_a, m_final);
				for (XEventClass e : par.mapEvClass2Cost.keySet()) {
					par.mapEvClass2Cost.put(e, 100);
				}
				par.parameters.setCreateConn(false);
				PNRepResult res = PNLogReplayer.callReplayer(context, configured_a, abstractedLog, mapping, par);
				
				for (SyncReplayResult r : res) {
//					int traceIndex = r.getTraceIndex().first();
//					XTrace trace = abstractedLog.get(traceIndex);
//					List<String> str_trace = new LinkedList<String>();
//					for (XEvent e : trace) {
//						String e_name = XConceptExtension.instance().extractName(e);
//						String e_inst = XConceptExtension.instance().extractInstance(e);
//						if (e_inst != null) e_name = e_name+"+"+e_inst; 
//						str_trace.add(e_name);
//					}

					float fitness=r.getInfo().get(PNRepResult.TRACEFITNESS).floatValue();
					if (fitness == 1 && config.previewTrace_compliant.get(a).size() < 2) {
						config.previewTrace_compliant.get(a).add(r); 
					} else if (config.previewTrace_violating.get(a).size() < 2) {
						config.previewTrace_violating.get(a).add(r);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generate the map between Transitions and Event according to the user
	 * selection.
	 * 
	 * @return Map between Transitions and Events.
	 */
	private TransEvClassMapping getEventMap(PetrinetGraph net, SelectRuleConfiguration config) {
		TransEvClassMapping map = new TransEvClassMapping(config.classifier, AbstractLogForCompliance_Plugin.DUMMY);

		// build transition classes
		DefTransClassifier transClassifier = new DefTransClassifier();
		TransClasses tc = new TransClasses(net, transClassifier);
		
		for (Transition t : net.getTransitions()) {
			TransClass tClass = tc.getClassOf(t);
			if (t.isInvisible()) {
				map.put(t, AbstractLogForCompliance_Plugin.DUMMY);
			} else if (tClass.getId().toLowerCase().equals(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION)) {
				map.put(t, AbstractLogForCompliance_Plugin.getOmegaClass(config.classifier));
			} else {
				String name = tClass.getId();
				Object selectedValue = config.activityMapping.get(name);
				if (selectedValue != null && selectedValue instanceof XEventClass) {
					// a real event class
					map.put(t, (XEventClass) selectedValue);
				} else {
					// this is "NONE"
					map.put(t, AbstractLogForCompliance_Plugin.DUMMY);
				}
			}
		}
		return map;
	}
	
	/**
	 * Generate the map between Transitions and Event according to the user
	 * selection.
	 * 
	 * @return Map between Transitions and Events.
	 */
	public TransClass2PatternMap getPatternMap(PetrinetGraph net, XLog log, SelectRuleConfiguration config) {
		XEventClassifier classifier = config.classifier;
		
		// build transition classes
		DefTransClassifier transClassifier = new DefTransClassifier();
		TransClasses tc = new TransClasses(net, transClassifier);
		
		Map<TransClass, Set<EvClassPattern>> mapping = new HashMap<TransClass, Set<EvClassPattern>>();
		
		for (TransClass trans : tc.getTransClasses()) {
			// first map all events which have a visible transition
			if (trans.getId().toLowerCase().equals(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION)) continue;
			
			String name = trans.getId();
			Object selectedValue = config.activityMapping.get(name);
			if (selectedValue instanceof XEventClass) {
				// a real event class
				mapping.put(trans, new HashSet<EvClassPattern>());
				EvClassPattern evClassPattern = new EvClassPattern();
				evClassPattern.add((XEventClass)selectedValue);
				mapping.get(trans).add(evClassPattern);
			}
		}
		
		for (TransClass trans : tc.getTransClasses()) {
			// then map all unmapped events to the omega transition
			if (!mapping.containsKey(trans) && trans.getId().toLowerCase().equals(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION)) {
				// collect all events not mapped to anything
				Set<XEventClass> unused = new HashSet<XEventClass>(config.availableEventClasses);
				for (Set<EvClassPattern> patterns : mapping.values()) {
					for (EvClassPattern pattern : patterns) {
						for (XEventClass ev_class : pattern) {
							unused.remove(ev_class);
						}
					}
				}
				// map these events to omega
				mapping.put(trans, new HashSet<EvClassPattern>());
				for (XEventClass ev_class : unused) {
					EvClassPattern evClassPattern = new EvClassPattern();
					evClassPattern.add(ev_class);
					mapping.get(trans).add(evClassPattern);
				}
			}
		}
		
		return new TransClass2PatternMap(log, net, classifier, tc, mapping);
	}
	
	protected void printErrorLog(PluginContext context, List<String> errors) {
		for (String error : errors) {
			System.err.println("Error: "+error);
		}
	}
	
	protected void produceErrorLog(UIPluginContext context, List<String> errors) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><ul>");
		for (String error : errors) {
			sb.append("<li>Error: "+error+"</li>");
		}
		sb.append("</ul>");
		sb.append("</html>");
		
		String error_log = sb.toString();
					
		context.getProvidedObjectManager().createProvidedObject(
				"error log for configuring compliance rule", error_log, String.class, context);
		context.getGlobalContext().getResourceManager().getResourceForInstance(error_log).setFavorite(true);
	}
	
	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[Configure Compliance Rule]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		return null;
	}

}
