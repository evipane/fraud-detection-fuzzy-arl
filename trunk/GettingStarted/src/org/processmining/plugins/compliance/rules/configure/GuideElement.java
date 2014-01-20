package org.processmining.plugins.compliance.rules.configure;

import java.util.LinkedList;
import java.util.List;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableFeatureGroup;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurableParameter;
import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterGraphElement;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableParameterInteger;


public abstract class GuideElement {

	public final static String PROPERTY_NOT_SET = "notset";
	public final static String PROPERTY_IS_SET = "isset";
	
	private String text;
	
	private String name;
	
	public GuideElement(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	private List<Configuration> configurations = new LinkedList<Configuration>();
	
	public List<Configuration> getConfigurations() {
		return configurations;
	}
	
	public void addConfiguration(Configuration config) {
		configurations.add(config);
	}
	
	public void setText(String t) {
		text = t;
	}
	
	public String getText() {
		return text;
	}

	private void isValueValidForParameter(Object value, ConfigurableParameter<? extends Object> param, List<String> errors) {
		if (value != PROPERTY_NOT_SET && value != PROPERTY_IS_SET) {

			if (param instanceof ConfigurableParameterInteger) {
				if (value instanceof Integer) {
					if (!((ConfigurableParameterInteger)param).isValidValue((Integer)value)) {
						errors.add(value+" is no valid value for '"+param.getId()+"' in "+getName());
					}
				} else if (value instanceof String) {
					// is a parameter to set an integer value
				}
			} else if (param instanceof ConfigurableParameterGraphElement && value instanceof ConfigurableGraphElementOption) {
				if (!((ConfigurableParameterGraphElement)param).isValidValue((ConfigurableGraphElementOption)value)) {
					errors.add(value+" is no valid value for '"+param.getId()+"' in "+getName());
				}
			} else {
				errors.add("Could not match type of "+value+" to '"+param.getId()+"' in "+getName());
			}
		}
	}
	
	private void isConfigurationValidForGroup(Configuration config, ConfigurableFeatureGroup group, List<String> errors) {
		List<ConfigurableParameter<? extends Object>> parameters = group.getInputParametersByName();
		for (String param_id : config.keySet()) {
			boolean param_id_found = false;
			
			// find the correct parameter by the parameter id and check for compatibility of values
			for (ConfigurableParameter<? extends Object> param : parameters) {
				if (param.getId().equals(param_id)) {
					param_id_found = true;
					isValueValidForParameter(config.get(param_id), param, errors);
				}
			}
			
			if (!param_id_found) {
				errors.add("Parameter '"+param_id+"' is undefined for group '"+group.getId()+"' in "+getName());
			}
		}
	}
	
	protected void isConfigurationListValidForNet(ConfigurablePetrinet<?> net, List<String> errors) {
		for (Configuration config : configurations) {
			String groupID = config.getFeatureGroupId();
			boolean group_found = false;
			
			// find the correct feature group by the group id and check for compatibility of parameters and values
			for (ConfigurableFeatureGroup group : net.getConfigurableFeatureGroups()) {
				if (group.getId().equals(groupID)) {
					group_found = true;
					
					isConfigurationValidForGroup(config, group, errors);
				} 
			} // finished searching configurable feature groups
			if (!group_found) {
				errors.add("Group '"+groupID+"' is undefined in "+getName());
			}
		}

	}
	
}
