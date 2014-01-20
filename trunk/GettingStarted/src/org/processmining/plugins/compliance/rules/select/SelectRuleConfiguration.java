package org.processmining.plugins.compliance.rules.select;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;

public class SelectRuleConfiguration {

	public XEventClassifier classifier;
	public XEventClasses allEventClasses;
	public List<XEventClass> availableEventClasses = new LinkedList<XEventClass>();
	
	public Map<String, XEventClass> activityMapping = new HashMap<String, XEventClass>();
	public Map<String, Integer> parameters = new HashMap<String, Integer>();
	
	public Map<String, Configuration> configuration;
	
	public void update(SelectRuleConfiguration config) {
		activityMapping.putAll(config.activityMapping);
		parameters.putAll(config.parameters);
	}
	
	/**
	 * @param configurableRule
	 * @param initial_config
	 * @return puts the partial configuration as determined by
	 *         {@code initial_config} in a map that holds for each
	 *         {@link ConfigurableFeatureGroup} of {@code configurableRule} a
	 *         partial {@link Configuration}
	 */
	public Map<String, Configuration> setConfigurationForRule(ConfigurablePetrinet<? extends PetrinetGraph> configurableRule, List<Configuration> initial_config) {
		configuration = getEmptyConfiguration(configurableRule);
		for (Configuration c : initial_config) {
			configuration.put(c.getFeatureGroupId(), c);
		}
		return configuration;
	}
	
	/**
	 * @param net
	 * @return initialize {@link #configuration} with the empty configuration, providing for each {@link ConfigurableFeatureGroup} an empty {@link Configuration}
	 */
	public Map<String, Configuration> setEmptyConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> net) {
		configuration = getEmptyConfiguration(net);
		return configuration;
	}

	/**
	 * @param net
	 * @return map holding for each {@link ConfigurableFeatureGroup} of the configurable net an empty {@link Configuration}
	 */
	private static Map<String, Configuration> getEmptyConfiguration(ConfigurablePetrinet<? extends PetrinetGraph> net) {
		Map<String, Configuration> config = new HashMap<String, Configuration>();
		for (ConfigurableFeatureGroup group : net.getConfigurableFeatureGroups()) {
			config.put(group.getId(), new Configuration(group.getId()));
		}
		return config;
	}
	
	/**
	 * @return a deep copy of the {@link #configuration}
	 */
	public Map<String, Configuration> copyConfiguration() {
		Map<String, Configuration> copy = new HashMap<String, Configuration>();
		for (String key : configuration.keySet()) {
			Configuration config = configuration.get(key);
			Configuration c_config = new Configuration(config.getFeatureGroupId());
			c_config.putAll(config);
			copy.put(key, c_config);
		}
		return copy;
	}
}
