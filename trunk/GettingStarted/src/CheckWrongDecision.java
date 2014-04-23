import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.fraud.model.InsertFraudData;
import org.processmining.fraud.model.fraud;
import org.processmining.owl.model.Ontologies;
import org.processmining.pnml.controller.ReadPNML;


public class CheckWrongDecision {
	public String[] columname = {"First Acitivity","Next Activity","Attribute","Type","Predicate","Value"};
	public Object[][] tableTransition;
	
	public DefaultTableModel tableModelTransition2 ;
	@Plugin(
			name="Check Wrong Decision",
			parameterLabels = {},
			returnLabels = { "Fraud Data" },
			returnTypes = { InsertFraudData.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Farid Naufal",
			author = "Farid Naufal",
			email = "naufalfarid99@gmail.com"
			)
	public InsertFraudData checkDecision(final UIPluginContext context, ReadPNML pnml, InsertFraudData fraud1, Ontologies owlFiles)
	{
		JPanel panel = new JPanel();
		tableTransition = new Object[pnml.decisions.size()][columname.length];
		QueryFraudDetection wrongDec = new QueryFraudDetection();
		List<String> caseFrauds = new ArrayList<String>();
		
		Object[][] table = new Object[pnml.decisions.size()][];
		int c=0;
		for(int i=0;i<pnml.decisions.size();i++)
		{
			tableTransition[i][0] = pnml.decisions.get(i).getFirstTransition();
			tableTransition[i][1] = pnml.decisions.get(i).getNextTransition();
			tableTransition[i][2] = pnml.decisions.get(i).getAttribute();
			tableTransition[i][3] = pnml.decisions.get(i).getTypeAttribyte();
			tableTransition[i][4] = pnml.decisions.get(i).getPredicate();
			tableTransition[i][5] = pnml.decisions.get(i).getValue();
		}
		
		tableModelTransition2 = new DefaultTableModel(table,columname);
		
		for(int i=0;i<tableTransition.length;i++)
		{
			for(int j=0;j<columname.length;j++)
			{
				tableModelTransition2.setValueAt(tableTransition[i][j], i, j);
			}
		}
		
		ProMTable Ptabel = new ProMTable(tableModelTransition2);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel.add(Ptabel);
		
		context.showConfiguration("Tabel Transisi",panel);
		for(int i = 0; i < pnml.decisions.size(); i++)
		{
			wrongDec.checkDecision(pnml.decisions.get(i).getFirstTransition(), pnml.decisions.get(i).getNextTransition(), pnml.decisions.get(i).getAttribute(), pnml.decisions.get(i).getTypeAttribyte(), pnml.decisions.get(i).getPredicate(), pnml.decisions.get(i).getValue(), caseFrauds, owlFiles.getPath());
		}
		
		//Mapping untuk banyaknya wrong decision dalam sebuah case
		System.out.println("Hasil");
		Map<String, Integer> countMapDec = new HashMap<String, Integer>();
		for(int i = 0; i < caseFrauds.size(); i++){
	        String myNum = caseFrauds.get(i);
	        if(countMapDec.get(myNum)!= null){
	             Integer currentCount = countMapDec.get(myNum);
	             currentCount = currentCount.intValue()+1;
	             countMapDec.put(myNum,currentCount);
	        }else{
	            countMapDec.put(myNum,1);
	        }
	    }
		
		Set<String> keys = countMapDec.keySet();
		
		boolean flag = false;
		
		for(String Case: keys){
		       System.out.println("Case " + Case + " Count " + countMapDec.get(Case).intValue());
		       //Check apakah ada case yang sudah terdeteksi fraud sebelumnya
		       for(int i=0; i < fraud1.frauds.size(); i++)
		       {
		    	   System.out.println("One");
		    	   if(Case.equals(fraud1.frauds.get(i).getCase()))
		    	   {
		    		   fraud1.frauds.get(i).setwDecision(countMapDec.get(Case).intValue() + fraud1.frauds.get(i).getSkipDec());
		    		   flag = true;
		    		   break;
		    	   }
		       }
		       if(flag == false)
		       {
		    	  System.out.println("Two");
		    	  fraud Fraud = new fraud(Case, 0, 0, 0, 0, 0, 0, 0, 0, 0, countMapDec.get(Case).intValue(), 0);
		    	  fraud1.frauds.add(Fraud);
		       }
		}
		System.out.println(owlFiles.getPath());
		
		/*for(int i = 0; i < caseFrauds.size(); i++)
		{
			System.out.println(caseFrauds.get(i));
		}*/
		
		return fraud1;
	}
}
