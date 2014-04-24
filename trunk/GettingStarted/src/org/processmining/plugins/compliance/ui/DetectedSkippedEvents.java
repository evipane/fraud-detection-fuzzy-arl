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
import org.processmining.fraud.model.fraud;
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
	public List<String> skipped = new ArrayList<String>();
	public List<String> SyncSTransitions = new ArrayList<String>();
	public List<String> SyncDTransitions = new ArrayList<String>();
	public List<String> SSTransitions = new ArrayList<String>();
	public List<String> SDTransitions = new ArrayList<String>();
	public fraud Fraud = new fraud();
	public List<fraud>frauds = new ArrayList<fraud>();
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
	
	public int countSkip(List<String>skip, List<String>sync)
	{
		skipped.clear();
		String temps="";
		String temps2="";
		int result = 0;
		for(int i=0;i<skip.size();i++)
		{
			boolean cek = false;
			boolean lewat = false;
			if(skipped.isEmpty())
			{
				skipped.add(skip.get(i));
				temps = skip.get(i);
			}
			else
			{
				for(int k=0;k<skipped.size();k++)
				{
					if(skip.get(i).equals(skipped.get(k)))
					{
						lewat=true;
						break;
					}
				}
			}
			if(lewat==true)
			{
				continue;
			}
			else 
			{
				skipped.add(skip.get(i));
				temps = skip.get(i);
			}
			for(int j=0;j<sync.size();j++)
			{
				if(sync.get(j).equals(temps2))
				{
					continue;
				}
				else
				{
					temps2 = sync.get(j);
				}
				
				if(temps.equals(temps2))
				{
					//System.out.println("CEK!!");
					cek=true;
				}
			}
			if(cek==false)
			{
				result++;
			}
		}
		return result;
	}
	
	public int countSkip2(List<String>skip, List<String>sync)
	{
		skipped.clear();
		
		int result = 0;
		//System.out.println("skip: "+skip.size());
		for(int i=0;i<skip.size();i++)
		{
			boolean cek = false;
			boolean lewat = false;
			String temps="";
			String temps2="";
			if(skipped.isEmpty())
			{
				skipped.add(skip.get(i));
				temps = skip.get(i);
			}
			else
			{
				for(int k=0;k<skipped.size();k++)
				{
					if(skip.get(i).equals(skipped.get(k)))
					{
						lewat=true;
						break;
					}
				}
			}
			if(lewat==true)
			{
				continue;
			}
			else 
			{
				skipped.add(skip.get(i));
				temps = skip.get(i);
			}
			for(int j=0;j<sync.size();j++)
			{
				
				temps2 = sync.get(j);
				
				
				if(temps.equals(temps2))
				{
					//System.out.println("CEK!!");
					//System.out.println("temp: "+temps+" -- temp2: "+temps2);
					cek=true;
				}
				
			}
			if(cek==false)
			{
				//System.out.println("temp: "+temps);
				result++;
			}
			
			
		}
		return result;
	}
	
	final DefaultTableModel reliableCasesTModel = new DefaultTableModel() {
		private static final long serialVersionUID = -4303950078200984098L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	public List<fraud> detect_skip(PetrinetGraph net, final XLog log, final PNRepResult logReplayResult,
			Progress progress)
	{

	ModelTabel(net);
	int count = 0;	
	for (SyncReplayResult res : logReplayResult) {
		
		SSTransitions = new ArrayList<String>();
		SDTransitions = new ArrayList<String>();
		SyncDTransitions = new ArrayList<String>();
		SyncSTransitions = new ArrayList<String>();
		skipped = new ArrayList<String>();
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
		List<String>caseID = new ArrayList<String>();
		for (int index : res.getTraceIndex()) {
			String name = ce.extractName(log.get(index));
			if (name == null) {
				name = String.valueOf(index);
			}
			caseIDSets.add(name);
			caseID.add(name);
		}
		int caseIDSize = caseIDSets.size();

		// ALIGNMENT PANEL
		ProcessInstanceConformanceView alignmentPanel = new ProcessInstanceConformanceView("Alignment", result,
				res.getStepTypes(),10);

		// add conformance info
		int index = 0;
		int skips = 0;
		int noskip = 0;
		int simpan = 0;
		boolean k1=false;
		boolean k2=false;
		String tukar1="";
		String tukar2="";
		String temp="";
		int swap = 0;
		int skipDec =0;
		int skipSeq = 0;
		boolean tes = false;
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
						//System.out.println("Skipped Decision: " + str[0]);
						SDTransitions.add(str[0]);
						sumSkippedDec += caseIDSize;
						skips++;
						noskip=0;
						simpan=0;
						
						temp = str[0];
					}
					else
					{
						//System.out.println("Skipped Sequence: " + str[0]);
						SSTransitions.add(str[0]);
						sumSkippedSeq += caseIDSize;
						skips++;
						noskip=0;
						simpan=0;
						temp = str[0];
					}
					
					if(skips>2)
					{
						skips=0;
					}
					
					System.out.println("skip: "+skips+"noskip: "+noskip+" -- k1: "+k1+" -- k2: "+k2+" -- str: "+str[0]+" -- tukar2: "+tukar2);
					if(skips==2 && k1==true && str[0].equals(tukar2))
					{
						System.out.println("Masuk3");
						k2=true;
						System.out.println("skip: "+skips+"noskip: "+noskip+" -- k1: "+k1+" -- k2: "+k2+" -- str: "+str[0]+" -- tukar2: "+tukar2);
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
					//System.out.println("Swapped: " + res.getNodeInstance().get(index));
					break;
				case LMGOOD :
					if (res.isReliable()) {
						numReliableSynchronized += caseIDSize;
					}
					;
					numSynchronized += caseIDSize;
					//System.out.println("Sync: " + res.getNodeInstance().get(index));
					String temp2 = res.getNodeInstance().get(index).toString();
					String[] str2 = temp2.split(" ");
					if(checkDecision(str2[0]))
					{
						SyncDTransitions.add(str2[0]);
					}
					else
					{
						SyncSTransitions.add(str2[0]);
					}
					
					
					noskip++;
					
					if(skips==2 && noskip==2 && k1==false && k2==false)
					{
						System.out.println("Masuk1");
						
						tukar1=temp;
						tukar2=str2[0];
						k1=true;
						System.out.println("skip: "+skips+" -- noskip: "+noskip+" -- k1: "+k1+" -- k2: "+k2);
						noskip=0;
					}
					System.out.println("noskip: "+noskip+" -- k1: "+k1+" -- str2: "+str2[0]+" -- tukar1: "+tukar1);
					if(noskip==2 && k1==true && str2[0].equals(tukar1))
					{
						System.out.println("Masuk2");
						System.out.println("noskip: "+noskip+" -- k1: "+k1+" -- str2: "+str2[0]+" -- tukar1: "+tukar1);
						skips=0;
						tes=true;
					}
					else if(noskip==2 && k1==true && tes==false)
					{
						noskip=0;
						skips=0;
						temp="";
						k1=false;
						k2=false;
						tukar1="";
						tukar2="";
						tes=false;
					}
					
					
			}
			//System.out.println("tes:"+tes+" -- skip: "+skips+" -- noskip: "+noskip);
			index++;
			/*if(tes==true && skips==1)
			{
				swap++;
				skips=0;
				noskip=0;
				simpan=0;
				tes=false;
			}*/
			if(k1==true && k2==true)
			{
				System.out.println("Masuk4");
				swap++;
				noskip=0;
				skips=0;
				temp="";
				k1=false;
				k2=false;
				tukar1="";
				tukar2="";
				tes=false;
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
		
		skipSeq=0;
		skipDec=0;
		
		skipSeq = countSkip2(SSTransitions, SyncSTransitions);
		skipDec = countSkip2(SDTransitions, SyncDTransitions);
		
		//System.out.println("Jumlah case: "+caseID.size()+" -- count: "+count);
		
		System.out.println("Case: "+caseID.get(count)+"-- Swap: "+swap+" -- SkipS: "+skipSeq+" -- SkipD: "+skipDec);
			
		//System.out.println("Case : "+res.getTraceIndex()+" -- Real :"+numModelOnlyReal+" -- Synchron :"+numSynchronized);
		//System.out.println("Case : "+res.getTraceIndex()+" -- Reals :"+real+" -- Synchrons :"+sync);
		swap=0;
		for (int index1 : res.getTraceIndex()) {
			String name = ce.extractName(log.get(index1));
			//System.out.println(name);
			Fraud = new fraud(name, skipSeq, skipDec, 0, 0, 0, 0, 0, 0, swap, 0, 0);
			frauds.add(Fraud);
			}
		
		//skip.put("Case "+res.getTraceIndex()+": ", ((numModelOnlyReal-real)-(numSynchronized-sync))/2);
		skip.put("Case "+res.getTraceIndex()+": ", (sumSkippedDec)/2);
		real = numModelOnlyReal;
		sync = numSynchronized;
		
		
		
	}

	return frauds;
	
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
