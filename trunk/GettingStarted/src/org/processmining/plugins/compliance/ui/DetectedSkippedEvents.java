package org.processmining.plugins.compliance.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.util.collection.AlphanumComparator;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

public class DetectedSkippedEvents {
	int numSynchronized = 0;
	int numModelOnlyInvi = 0;
	int numModelOnlyReal = 0;
	int numLogOnly = 0;
	int numViolations = 0;

	int numReliableSynchronized = 0;
	int numReliableModelOnlyInvi = 0;
	int numReliableModelOnlyReal = 0;
	int numReliableLogOnly = 0;
	int numReliableViolations = 0;

	int numCaseInvolved = 0;
	
	int RELIABLEMIN = 0;
	int RELIABLEMAX = 1;
	int MIN = 2;
	int MAX = 3;

	// standard deviation is calculated based on http://mathcentral.uregina.ca/QQ/database/QQ.09.02/carlos1.html
	int SVAL = 4;
	int MVAL = 6;
	int SVALRELIABLE = 5;
	int MVALRELIABLE = 7;
	int PERFECTCASERELIABLECOUNTER = 8;

	// this value has to be stored because it is used by actionListener
	int numReliableCaseInvolved = 0; 
	int real = 0;
	int sync = 0;
		
	
	// total calculated values
	Map<String, Double[]> calculations = new HashMap<String, Double[]>();
	Map<String, Integer> skip = new HashMap<String, Integer>();
	final DefaultTableModel reliableCasesTModel = new DefaultTableModel() {
		private static final long serialVersionUID = -4303950078200984098L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};


	public Map<String, Integer> detect_skip(PetrinetGraph net, final XLog log, final PNRepResult logReplayResult,
			Progress progress)
	{

	for (SyncReplayResult res : logReplayResult) {
		
		

		// reformat node instance list
		List<Object> result = new LinkedList<Object>();
		for (Object obj : res.getNodeInstance()) {
			if (obj instanceof Transition) {
				result.add(((Transition) obj).getLabel());
			} else if (obj instanceof String) {
				result.add(obj);
			} else {
				result.add(obj.toString());
			}
		}

		// create combobox
		SortedSet<String> caseIDSets = new TreeSet<String>(new AlphanumComparator());
		XConceptExtension ce = XConceptExtension.instance();
		for (int index : res.getTraceIndex()) {
			String name = ce.extractName(log.get(index));
			if (name == null) {
				name = String.valueOf(index);
			}
			caseIDSets.add(name);
		}
		int caseIDSize = caseIDSets.size();

		

		// add conformance info
		
		for (StepTypes stepType : res.getStepTypes()) {
			switch (stepType) {
				case L :
					numLogOnly += caseIDSize;
					if (res.isReliable()) {
						numReliableLogOnly += caseIDSize;
					}
					;
					break;
				case MINVI :
					if (res.isReliable()) {
						numReliableModelOnlyInvi += caseIDSize;
					}
					;
					numModelOnlyInvi += caseIDSize;
					break;
				case MREAL :
					if (res.isReliable()) {
						numReliableModelOnlyReal += caseIDSize;
					}
					;
					numModelOnlyReal += caseIDSize;
					break;
				case LMNOGOOD :
				case LMREPLACED : 
				case LMSWAPPED : 
					if (res.isReliable()) {
						numReliableViolations += caseIDSize;
					}
					;
					numViolations += caseIDSize;
					break;
				case LMGOOD :
					if (res.isReliable()) {
						numReliableSynchronized += caseIDSize;
					}
					;
					numSynchronized += caseIDSize;
			}
		}
		
		

		// to be shown in right side of case
		Map<String, Double> mapInfo = res.getInfo();
		Set<String> keySetMapInfo = mapInfo.keySet();

		// create table for map info
		Object[][] infoSingleTrace = new Object[keySetMapInfo.size() + 2][2];
		int propCounter = 0;
		infoSingleTrace[propCounter++] = new Object[] { "Num. Cases", caseIDSize };
		infoSingleTrace[propCounter++] = new Object[] { "Is Alignment Reliable?", res.isReliable() ? "Yes" : "No" };
		for (String property : keySetMapInfo) {
			

			// use it to calculate property
			Double[] oldValues = calculations.get(property);
			if (oldValues == null) {
				oldValues = new Double[] { Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE,
						0.00000, 0.00000, 0.00000, 0.00000, 0.00000 };
				calculations.put(property, oldValues);
			}

			
			
		}

		numCaseInvolved += caseIDSize;
		if (res.isReliable()) {
			numReliableCaseInvolved += caseIDSize;
		}
		;

		// ALIGNMENT STATISTICS PANEL
		DefaultTableModel tableModel = new DefaultTableModel(infoSingleTrace, new Object[] { "Property", "Value" }) {
			private static final long serialVersionUID = -4303950078200984098L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		
		System.out.println("Case : "+res.getTraceIndex()+" -- Real :"+numModelOnlyReal+" -- Synchron :"+numSynchronized);
		System.out.println("Case : "+res.getTraceIndex()+" -- Reals :"+real+" -- Synchrons :"+sync);
		skip.put("Case "+res.getTraceIndex()+": ", ((numModelOnlyReal-real)-(numSynchronized-sync))/2);
		real = numModelOnlyReal;
		sync = numSynchronized;
	}

		

	return skip;
	
	}

}
