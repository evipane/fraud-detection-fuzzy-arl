package org.processmining.plugins.compliance.ui;

import java.awt.Dimension;
import java.util.ArrayList;
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
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.petrinet.replayresult.visualization.ProcessInstanceConformanceView;
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
	
	int sumSkippedSeq = 0;
	int sumSkippedDec = 0;
	
	public String[] columname = {"Transition","Jumlah"};
	public String[] columname2 = {"Decision","Sequence"};
	public Object[][] tableTransition;
	public Object[][] tableTransition2;
	public Object[][] tableTransition3;
	public DefaultTableModel tableModelTransition ;
	public List<String> decTransitions = new ArrayList<String>();
	public List<String> seqTransitions = new ArrayList<String>();
	
	// total calculated values
	Map<String, Double[]> calculations = new HashMap<String, Double[]>();
	Map<String, Integer> skip = new HashMap<String, Integer>();
	
	public String skipResult;
	
	public void ModelTabel(PetrinetGraph net)
	{
		//JPanel panel = new JPanel();
		
		tableTransition = new Object[net.getTransitions().size()][columname.length];
		Object[][] table = new Object[net.getTransitions().size()][];
		tableModelTransition = new DefaultTableModel(table,columname2);
		int c=0;
		int temp;
		boolean flag=false;
		for(Transition t : net.getTransitions())
		{
			flag=false;
			if(tableTransition.length==0)
			{
				tableTransition[c][0] = t.getLabel();
				tableTransition[c][1] = 1;
				c++;
			}
			else
			{
				for(int i=0;i<tableTransition.length;i++)
				{
					if(t.getLabel().equals(tableTransition[i][0]))
					{
						System.out.println("masuk sini");
						temp = (Integer) tableTransition[i][1];
						tableTransition[i][1]=temp+1;
						flag=true;
					}
				}
			}
			if(flag==false)
			{
				tableTransition[c][0] = t.getLabel();
				tableTransition[c][1] = 1;
				c++;
			}
		}
		
		tableTransition2 = new Object[c][columname2.length];
		tableTransition3 = new Object[c][columname2.length];
		c=0;
		int d=0;
		for(int i=0;i<tableTransition2.length;i++)
		{
			String temp1 = (String)tableTransition[i][0];
			String[] str = temp1.split(" ");
			if((Integer)tableTransition[i][1]>1 && str[1].equals("Complete"))
			{
				System.out.println("masuk sini donk");
				tableTransition2[c][0]=str[0];
				decTransitions.add(str[0].toString());
				c++;
			}
			else if((Integer)tableTransition[i][1]<2 && str[1].equals("Complete"))
			{
				tableTransition3[d][0]=str[0];
				seqTransitions.add(str[0].toString());
				d++;
			}
		}
		
		System.out.println("table2: "+c);
		System.out.println("table3: "+d);
		
		for(int i=0;i<tableTransition2.length;i++)
		{
			tableModelTransition.setValueAt(tableTransition2[i][0], i, 0);
		}
		
		for(int i=0;i<tableTransition3.length;i++)
		{
			tableModelTransition.setValueAt(tableTransition3[i][0], i, 1);
		}
				
		ProMTable Ptabel = new ProMTable(tableModelTransition);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		//panel.add(Ptabel);
		//context.showConfiguration("Tabel Transisi",panel);
		
		//return panel;
	}
	
	
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

	ModelTabel(net);
		
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

		// ALIGNMENT PANEL
		ProcessInstanceConformanceView alignmentPanel = new ProcessInstanceConformanceView("Alignment", result,
				res.getStepTypes(),10);

		// add conformance info
		int index = 0;
		for (StepTypes stepType : res.getStepTypes()) {
			switch (stepType) {
				case L :
					numLogOnly += caseIDSize;
					if (res.isReliable()) {
						numReliableLogOnly += caseIDSize;
					}
					;
					System.out.println("L:" + res.getNodeInstance().get(index));
					break;
				case MINVI :
					if (res.isReliable()) {
						numReliableModelOnlyInvi += caseIDSize;
					}
					;
					numModelOnlyInvi += caseIDSize;
					System.out.println("MINVI: " + res.getNodeInstance().get(index));
					break;
				case MREAL :
					if (res.isReliable()) {
						numReliableModelOnlyReal += caseIDSize;
						
					}
					;
					numModelOnlyReal += caseIDSize;
					String temp1 = res.getNodeInstance().get(index).toString();
					String[] str = temp1.split(" ");
					if(checkDecision(str[0]))
					{
						System.out.println("Skipped Decision: " + str[0]);
						sumSkippedDec += caseIDSize;
					}
					else
					{
						System.out.println("Skipped Sequence: " + str[0]);
						sumSkippedSeq += caseIDSize;
					}
					break;
				case LMNOGOOD :
				case LMREPLACED : 
				case LMSWAPPED : 
					if (res.isReliable()) {
						numReliableViolations += caseIDSize;
					}
					;
					numViolations += caseIDSize;
					System.out.println("Swapped: " + res.getNodeInstance().get(index));
					break;
				case LMGOOD :
					if (res.isReliable()) {
						numReliableSynchronized += caseIDSize;
					}
					;
					numSynchronized += caseIDSize;
					System.out.println("Sync: " + res.getNodeInstance().get(index));
			}
			index++;
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
		//System.out.println(decTransitions.get(0));
		//skip.put("Case "+res.getTraceIndex()+": ", ((numModelOnlyReal-real)-(numSynchronized-sync))/2);
		skip.put("Case "+res.getTraceIndex()+": ", (sumSkippedDec)/2);
		real = numModelOnlyReal;
		sync = numSynchronized;
	}

	return skip;
	
	}
	
	public boolean checkDecision(String Transition)
	{
		for(String str: decTransitions) {
		    if(str.trim().contains(Transition))
		       return true;
		}
		return false;
	}
}
