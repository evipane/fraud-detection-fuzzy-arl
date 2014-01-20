/**
 * 
 */
package org.processmining.plugins.compliance;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ArrayUtils;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.manifestreplayer.EvClassPattern;
import org.processmining.plugins.petrinet.manifestreplayer.TransClass2PatternMap;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.DefTransClassifier;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClasses;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import com.fluxicon.slickerbox.factory.SlickerFactory;

/**
 * GUI to map event class (with any classifiers) to transitions of Petri net
 * 
 * @author aadrians
 * 
 */
public class MapEvPattern2Trans_Smart_UI extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// internal attributes
	private final XLog log;
	private final PetrinetGraph net;
	private XEventClasses eventClasses;
	
	private Map<TransClass, JComboBox> mapTrans2ComboBox = new HashMap<TransClass, JComboBox>();
	private JComboBox classifierSelectionCbBox;

	public MapEvPattern2Trans_Smart_UI(final XLog log, final PetrinetGraph net, XEventClassifier[] availableClassifier) {
		super();

		// index for row
		int rowCounter = 0;

		// import variable
		this.log = log;
		this.net = net;
		
		this.setTransitionClasses(net);

		// swing factory
		SlickerFactory factory = SlickerFactory.instance();

		// set layout
		double size[][] = { { TableLayoutConstants.FILL, TableLayoutConstants.FILL }, { 80, 70 } };
		TableLayout layout = new TableLayout(size);
		setLayout(layout);

		// label
		add(factory
				.createLabel("<html><h1>Map Transitions to Event Class</h1><p>First, select a classifier. Unmapped transitions will be mapped to a dummy event class.</p></html>"),
				"0, " + rowCounter + ", 1, " + rowCounter);
		rowCounter++;

		// add classifier selection
		classifierSelectionCbBox = factory.createComboBox(availableClassifier);
		classifierSelectionCbBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] boxOptions = extractEventClasses(log,
						(XEventClassifier) classifierSelectionCbBox.getSelectedItem());

				for (TransClass transition : mapTrans2ComboBox.keySet()) {
					JComboBox cbBox = mapTrans2ComboBox.get(transition);
					cbBox.removeAllItems(); // remove all items

					for (Object item : boxOptions) {
						cbBox.addItem(item);
					}
					cbBox.setSelectedIndex(preSelectOption(transition.getId(), boxOptions));
				}
			}
		});
		classifierSelectionCbBox.setSelectedIndex(0);
		classifierSelectionCbBox.setPreferredSize(new Dimension(350, 30));
		classifierSelectionCbBox.setMinimumSize(new Dimension(350, 30));

		add(factory.createLabel("Choose classifier"), "0, " + rowCounter + ", l, c");
		add(classifierSelectionCbBox, "1, " + rowCounter + ", l, c");
		rowCounter++;

		// add mapping between transitions and selected event class 
		Object[] boxOptions = extractEventClasses(log, (XEventClassifier) classifierSelectionCbBox.getSelectedItem());
		TransClasses tc = getTransitionClasses();
		
		LinkedList<TransClass> used_classes = new LinkedList<TransClass>();
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible()) continue;
			if (t.getLabel().toLowerCase().equals(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION)) continue;
			if (!used_classes.contains(tc.getClassOf(t))) {
				used_classes.add(tc.getClassOf(t));
			}
		}
		
		for (TransClass transition : used_classes) {
			layout.insertRow(rowCounter, 30);
			JComboBox cbBox = factory.createComboBox(boxOptions);
			cbBox.setPreferredSize(new Dimension(350, 30));
			cbBox.setMinimumSize(new Dimension(350, 30));
			mapTrans2ComboBox.put(transition, cbBox);
			cbBox.setSelectedIndex(preSelectOption(transition.getId(), boxOptions));

			add(factory.createLabel(transition.getId()), "0, " + rowCounter + ", l, c");
			add(cbBox, "1, " + rowCounter + ", l, c");
			rowCounter++;
		}

	}

	/**
	 * get all available event classes using the selected classifier, add with
	 * NONE
	 * 
	 * @param log
	 * @param selectedItem
	 * @return
	 */
	private Object[] extractEventClasses(XLog log, XEventClassifier selectedItem) {
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, getSelectedClassifier());
		this.eventClasses = summary.getEventClasses();

		// sort event class
		Collection<XEventClass> cl = eventClasses.getClasses();

		// create possible event classes
		Object[] arrEvClass = cl.toArray();
		Arrays.sort(arrEvClass);
		Object[] notMappedAct = { "NONE" };
		Object[] boxOptions = ArrayUtils.concatAll(notMappedAct, arrEvClass);
		
		return boxOptions;
	}

	/**
	 * Returns the Event Option Box index of the most similar event for the
	 * transition.
	 * 
	 * @param transition
	 *            Name of the transitions
	 * @param events
	 *            Array with the options for this transition
	 * @return Index of option more similar to the transition
	 */
	private int preSelectOption(String transition, Object[] events) {
		//The metric to get the similarity between strings
		AbstractStringMetric metric = new Levenshtein();

		int index = 0;
		float simOld = metric.getSimilarity(transition, "none");
		simOld = Math.max(simOld, metric.getSimilarity(transition, "invisible"));
		simOld = Math.max(simOld, metric.getSimilarity(transition, "skip"));
		simOld = Math.max(simOld, metric.getSimilarity(transition, "tau"));

		for (int i = 1; i < events.length; i++) {
			String event = ((XEventClass) events[i]).toString();
			float sim = metric.getSimilarity(transition, event);

			if (simOld < sim) {
				simOld = sim;
				index = i;
			}
		}

		return index;
	}
	
	private TransClasses tc;
	private void setTransitionClasses(PetrinetGraph net) {
		// classify transitions based on their label
		DefTransClassifier transClassifier = new DefTransClassifier();
		// build transition classes
		tc = new TransClasses(net, transClassifier);
	}

	protected TransClasses getTransitionClasses() {
		return tc;
	}
	
	protected XEventClasses getEventClasses() {
		return eventClasses;
	}
	
	/**
	 * Generate the map between Transitions and Event according to the user
	 * selection.
	 * 
	 * @return Map between Transitions and Events.
	 */
	public TransClass2PatternMap getPatternMap() {
		XEventClassifier classifier = (XEventClassifier) this.classifierSelectionCbBox.getSelectedItem();
		

		
		Map<TransClass, Set<EvClassPattern>> mapping = new HashMap<TransClass, Set<EvClassPattern>>();
		
		for (TransClass trans : mapTrans2ComboBox.keySet()) {
			Object selectedValue = mapTrans2ComboBox.get(trans).getSelectedItem();
			if (selectedValue instanceof XEventClass) {
				// a real event class
				mapping.put(trans, new HashSet<EvClassPattern>());
				EvClassPattern evClassPattern = new EvClassPattern();
				evClassPattern.add((XEventClass)selectedValue);
				mapping.get(trans).add(evClassPattern);
				System.out.println("add "+trans.getId()+" -> "+selectedValue);
			}
			/*
			 else {
				// this is "NONE"
				mapping.put(trans, new HashSet<EvClassPattern>());
				EvClassPattern evClassPattern = new EvClassPattern();
				evClassPattern.add(DUMMY);
				mapping.get(trans).add(evClassPattern);
			}*/
		}
		
		for (TransClass trans : tc.getTransClasses()) {
			if (!mapping.containsKey(trans) && trans.getId().toLowerCase().equals(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION)) {
				Set<XEventClass> unused = new HashSet<XEventClass>(eventClasses.getClasses());
				for (Set<EvClassPattern> patterns : mapping.values()) {
					for (EvClassPattern pattern : patterns) {
						for (XEventClass ev_class : pattern) {
							unused.remove(ev_class);
						}
					}
				}
				mapping.put(trans, new HashSet<EvClassPattern>());
				for (XEventClass ev_class : unused) {
					EvClassPattern evClassPattern = new EvClassPattern();
					evClassPattern.add(ev_class);
					mapping.get(trans).add(evClassPattern);
					System.out.println("add "+trans.getId()+" -> "+ev_class);
				}
			}
		}
		
		return new TransClass2PatternMap(log, net, classifier, tc, mapping);
	}
	
	/**
	 * Generate the map between Transitions and Event according to the user
	 * selection.
	 * 
	 * @return Map between Transitions and Events.
	 */
	public TransEvClassMapping getEventMap() {
		TransEvClassMapping map = new TransEvClassMapping(getSelectedClassifier(), AbstractLogForCompliance_Plugin.DUMMY);
		
		for (Transition t : net.getTransitions()) {
			TransClass tClass = tc.getClassOf(t);
			if (t.isInvisible()) {
				map.put(t, AbstractLogForCompliance_Plugin.DUMMY);
			} else if (tClass.getId().toLowerCase().equals(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION)) {
				map.put(t, AbstractLogForCompliance_Plugin.OMEGA_CLASS);
			} else {
				Object selectedValue = mapTrans2ComboBox.get(tClass).getSelectedItem();
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
	 * Get the selected classifier
	 * 
	 * @return
	 */
	public XEventClassifier getSelectedClassifier() {
		return (XEventClassifier) classifierSelectionCbBox.getSelectedItem();
	}

}
