package org.processmining.plugins.compliance.rules.configure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;


@Plugin(name = "Visualize Configuration Guide",
		parameterLabels = { "Configuration Guide" }, 
		returnLabels = { "Question Tree" },
		returnTypes = { Petrinet.class },
		help = "Visualize the question space defined by a configuration guide.", userAccessible = true)
public class VisualizeGuide_Plugin {
		
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland, Elham Ramezani",
			email = "d.fahland@tue.nl, e.ramezani@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Visualize Configuration Guide", requiredParameterLabels = { 0 })
	public Petrinet guideConfiguration(UIPluginContext context, ConfigurationGuide guide) {

		Petrinet net = PetrinetFactory.newPetrinet("guide graph");
		
		Config c = getEmptyConfiguration(guide);
		HashMap<Config, Place> seen = new HashMap<Config, Place>();
		LinkedList<Config> queue = new LinkedList<Config>();
		seen.put(c, net.addPlace(c.toString()));
		queue.add(c);
		
		int count = 0;
		
		while (!queue.isEmpty()) {
			
			if (count > 100) break;	// FIXME: added break condition to fix bug in non-terminating loop
			count++;
			
			c = queue.removeFirst();
			
			Place pc = seen.get(c);
			
			List<RuleQuestion> enabledQuestions = new LinkedList<RuleQuestion>();
			for (RuleQuestion q : guide.getQuestions()) {
				if (q.isEnabled(c)) {
					enabledQuestions.add(q);
				}
			}
			
			for (RuleQuestion q : enabledQuestions) {
				
				Place pq = null;
				Transition tq = null;
				
				for (RuleAnswer a : q.getAnswers()) {
					
					// copy current configuration
					Config c2 = new Config();
					for (String id : c.keySet()) {
						c2.put(id, new Configuration(id));
						c2.get(id).putAll(c.get(id));
					}
					
					// and apply changes due to question
					for (Configuration ca : a.getConfigurations()) {
						c2.get(ca.getFeatureGroupId()).putAll(ca);
					}
					
					if (seen.containsKey(c2)) continue;
					
					if (pq == null) {
						pq = net.addPlace(q.getName());
						tq = net.addTransition(q.getText());
						net.addArc(pc, tq);
						net.addArc(tq, pq);
					}
					
					Place pc2 = net.addPlace(c2.toString());
					seen.put(c2, pc2);
					queue.addLast(c2);
					
					Transition t = net.addTransition(a.getText());
					net.addArc(pq, t);
					net.addArc(t, pc2);
				}
			}
		}
		
		return net;
	}
	
	private static class Config extends HashMap<String, Configuration> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean equals(Object o) {
			if (o instanceof Config) {
				Config other = (Config)o;
				if (!this.keySet().equals(other.keySet())) return false;
				for (String key : this.keySet()) {
					Configuration _c1 = this.get(key);
					Configuration _c2 = other.get(key);
					
					if (_c1 == null && _c2 == null) return true;
					if (_c1 == null && _c2 != null) return false;
					if (_c1 != null && _c2 == null) return true;
					
					if (!_c1.keySet().equals(_c2.keySet())) return false;
					if (!_c1.getFeatureGroupId().equals(_c2.getFeatureGroupId())) return false;
					
					for (String key2 : _c1.keySet()) {
						if (!_c1.get(key2).equals(_c2.get(key2))) return false;
					}
				}
				return true;
			} else {
				return super.equals(o);
			}
		}
	};

	private Config getEmptyConfiguration(ConfigurationGuide guide) {
		Config config = new Config();
		for (RuleQuestion question : guide.getQuestions()) {
			for (Configuration c : question.getConfigurations()) {
				config.put(c.getFeatureGroupId(), new Configuration(c.getFeatureGroupId()));
			}
			for (RuleAnswer answer : question.getAnswers()) {
				for (Configuration c : answer.getConfigurations()) {
					config.put(c.getFeatureGroupId(), new Configuration(c.getFeatureGroupId()));
				}
				
			}
		}
		return config;
	}
	
}
