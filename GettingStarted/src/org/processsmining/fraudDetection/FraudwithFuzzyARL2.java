package org.processsmining.fraudDetection;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.fraud.model.InsertFraudData;

public class FraudwithFuzzyARL2 {
	
	public String[] columnsName = {"SkipS","SkipD","Tmin","Tmax","wResource","wDutySec","wDutyDec","wDutyCom","wPattern","wDecision","Fraud"};
	public Object[][] tableContent;
	/*public Object[][] tableContent = {
			{new Double(2), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(0), new Double(2),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(0), new Double(0),new Double(4), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(0), new Double(0),new Double(0), new Double(5),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(0), new Double(0),new Double(0), new Double(0),new Double(3), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(2),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(2), new Double(0),new Double(0), new Double(0),new Double(0.5)},
			{new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(1),new Double(0), new Double(0),new Double(0.5)},
			{new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(3), new Double(0),new Double(0.75)},
			{new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(2),new Double(0.75)},
			{new Double(2), new Double(1),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.75)},
			{new Double(3), new Double(0),new Double(3), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(1)},
			{new Double(4), new Double(0),new Double(0), new Double(4),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(1)},
			{new Double(4), new Double(0),new Double(0), new Double(0),new Double(2), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.75)},
			{new Double(1), new Double(0),new Double(0), new Double(0),new Double(0), new Double(1),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.75)},
			{new Double(2), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(1), new Double(0),new Double(0), new Double(0),new Double(1)},
			{new Double(3), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(1),new Double(0), new Double(0),new Double(1)},
			{new Double(1), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(2), new Double(0),new Double(1)},
			{new Double(2), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(1),new Double(1)},
			{new Double(0), new Double(5),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(1)}
			
	};
	
	public Object[][] tableContent2 = {
			{new Double(2), new Double(0),new Double(2), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(4), new Double(0),new Double(0), new Double(5),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(9), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(14), new Double(0),new Double(5), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(15), new Double(0),new Double(0), new Double(3),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(0), new Double(0),new Double(9), new Double(5),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.25)},
			{new Double(0), new Double(0),new Double(10), new Double(8),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.5)},
			{new Double(0), new Double(0),new Double(10), new Double(10),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.5)},
			{new Double(0), new Double(0),new Double(4), new Double(0),new Double(2), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.75)},
			{new Double(3), new Double(0),new Double(0), new Double(3),new Double(3), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.75)},
			{new Double(0), new Double(0),new Double(0), new Double(4),new Double(9), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.75)},
			{new Double(2), new Double(2),new Double(0), new Double(8),new Double(2), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(1)},
			{new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(2),new Double(0), new Double(0),new Double(1)},
			{new Double(2), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(3), new Double(0),new Double(0.75)},
			{new Double(0), new Double(4),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0.75)},
			{new Double(4), new Double(2),new Double(3), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(1),new Double(1)},
			{new Double(5), new Double(1),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(1),new Double(1)},
			{new Double(0), new Double(2),new Double(9), new Double(0),new Double(2), new Double(0),new Double(0), new Double(0),new Double(0), new Double(2),new Double(1)},
			{new Double(0), new Double(2),new Double(0), new Double(5),new Double(0), new Double(0),new Double(0), new Double(0),new Double(0), new Double(2),new Double(1)},
			{new Double(2), new Double(1),new Double(10), new Double(0),new Double(12), new Double(0),new Double(0), new Double(0),new Double(0), new Double(5),new Double(1)}
			
	};
	*/
	
	Object[][] tabel2 = new Object[100][];
	String[] tabelName = {"Tabel Fraud"};
	public String[][] simpan;
	public Integer[] jumlahPakar;
	public String[] columnsName2 = {"SkipSL","SkipSM","SkipSH","SkipDL","SkipDM","SkipDH","TminL","TminM","TminH","TmaxL","TmaxM","TmaxH","wResourceL","wResourceM","wResourceH","wDutySecL","wDutySecM","wDutySecH","wDutyDecL","wDutyDecM","wDutyDecH","wDutyComL","wDutyComM","wDutyComH","wPatternL","wPatternM","wPatternH","wDecisionL","wDecisionM","wDecisionH","Fraud"};
	public String[] aturan = {"Aturan","Support","Confidence"};
	public Object[][] tableFuzzy ;
	public DefaultTableModel tableModel2 = new DefaultTableModel(tabel2,aturan);
	public CountImportance CI = new CountImportance();
	public CountARL2 CA = new CountARL2();
	public Double[] param;
	public AssociationRule ARL = new AssociationRule();
	
	public int jumlahRole=0;
	//JTable tabel = new JTable(tableContent,columnsName);
	
	
	
	@Plugin(
			name="Fraud Detection with Fuzzy Association Rule Learning Plugin versi 2",
			parameterLabels = {},
			returnLabels ={"Fraud Results"},
			returnTypes = {AssociationRule.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Fernandes Sinaga",
			author = "Fernandes Sinaga",
			email = "nandes.02@gmail.com"
			)
	
	//UI untuk menampilkan tabel fraud
	public AssociationRule FraudTabel(final UIPluginContext context,InsertFraudData fraud)
	{
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();
		JPanel panel5 = new JPanel();
		JPanel panel6 = new JPanel();
		JPanel panel7 = new JPanel();
		
		tableContent = new Object[fraud.frauds.size()][columnsName.length];
		
		Object[][] tabel = new Object[fraud.frauds.size()][];
		DefaultTableModel tableModel = new DefaultTableModel(tabel,columnsName);
		for(int i=0;i<fraud.frauds.size();i++)
		{
			tableContent[i][0]= fraud.frauds.get(i).getSkipSeq();
			tableContent[i][1]= fraud.frauds.get(i).getSkipDec();
			tableContent[i][2]= fraud.frauds.get(i).getTmin();
			tableContent[i][3]= fraud.frauds.get(i).getTmax();
			tableContent[i][4]= fraud.frauds.get(i).getWResource();
			tableContent[i][5]= fraud.frauds.get(i).getWDutySeq();
			tableContent[i][6]= fraud.frauds.get(i).getWDutyDec();
			tableContent[i][7]= fraud.frauds.get(i).getWDutyCom();
			tableContent[i][8]= fraud.frauds.get(i).getwPattern();
			tableContent[i][9]= fraud.frauds.get(i).getwDecision();
			tableContent[i][10]= fraud.frauds.get(i).getFraud();
		}
		
		for(int i=0;i<tableContent.length;i++)
		{
			for(int j=0;j<tableModel.getColumnCount();j++)
			{
				tableModel.setValueAt(tableContent[i][j], i, j);
			}
		}
		System.out.println(tableModel.getValueAt(0, 0));
		
		
		ProMTable Ptabel = new ProMTable(tableModel);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel.add(Ptabel);
		
		context.showConfiguration("Tabel Fraud",panel);
		
		jumlahPakar = new Integer[1];
		jumlahPakar[0]=0;
		
		InteractionResult result1 = context.showConfiguration("Input Jumlah Pakar", new countFraud().InputJumlahPakar(jumlahPakar));
		if (result1.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		
		simpan = new String[jumlahPakar[0]][10];
		
		System.out.println("Pakar: "+jumlahPakar);
		for(int i=0;i<jumlahPakar[0];i++)
		{
			System.out.println("Masuk lah");
			InteractionResult result7 = context.showConfiguration("Input Kepentingan Pakar "+(i+1), new countFraud().InputKepentingan(simpan[i]));
			if (result7.equals(InteractionResult.CANCEL)) {
				context.getFutureResult(0).cancel(true);
			}
		}
		
		ARL.jumlahPakar=jumlahPakar[0];
		ARL.inputPakar = new String[jumlahPakar[0]][10];
		ARL.inputPakar=simpan;
		
		countFraudMADM();
		
		
		for(int i=0;i<tableContent.length;i++)
		{
			tableContent[i][columnsName.length-1] = CI.fraud[i];
		}

		for(int i=0;i<tableContent.length;i++)
		{
			for(int j=0;j<tableModel.getColumnCount();j++)
			{
				tableModel.setValueAt(tableContent[i][j], i, j);
			}
		}
		
		ProMTable Ptabel2 = new ProMTable(tableModel);
		Ptabel2.setPreferredSize(new Dimension(1000, 500));
		Ptabel2.setAutoResizeMode(0);
		panel.add(Ptabel2);
		
		context.showConfiguration("Tabel Fraud Baru",panel);
		
		tableFuzzy = new Object[tableContent.length][columnsName2.length];
		
		InteractionResult result2 = context.showConfiguration("Fuzzy Table", new Fuzzy().FuzzyTabel(tableContent,columnsName,tableFuzzy));
		if (result2.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		
		param = new Double[1];
		param[0] = 0.0;
		boolean flag=true;
		int count=1;
		System.out.println("fuzzy: "+tableFuzzy[0][0]);
		ARL.tableARL = new Object[100][aturan.length];
		
		while(flag==true)
		{
			if(count==1)
			{
				CA.countSupport(tableFuzzy,columnsName2);
				Object[][] tabels = new Object[1][];
				DefaultTableModel tableModels = new DefaultTableModel(tabels,columnsName2);
				for(int j=0;j<tableModels.getColumnCount();j++)
				{
					tableModels.setValueAt(CA.tableSupport[j], 0, j);
				}
				
				ProMTable Ptabel4 = new ProMTable(tableModels);
				Ptabel4.setPreferredSize(new Dimension(1000, 500));
				Ptabel4.setAutoResizeMode(0);
				panel3.add(Ptabel4);
				context.showConfiguration("Support 1-itemset",panel3);
				
				InteractionResult result3 = context.showConfiguration("Threshold 1-itemset", new ARLParameter2().ARLParam(param));
				if (result3.equals(InteractionResult.CANCEL)) {
					context.getFutureResult(0).cancel(true);
				}
				
				System.out.println("Param: "+param[0]);
				jumlahRole=CA.selection(param[0],columnsName2,tableFuzzy,ARL.tableARL,jumlahRole,count);
				count++;
			}
			else if(count==2)
			{
				Object[][] tabels = new Object[1][];
				DefaultTableModel tableModels = new DefaultTableModel(tabels,CA.columnsNameS2);
				for(int j=0;j<tableModels.getColumnCount();j++)
				{
					tableModels.setValueAt(CA.tableSupport2[j], 0, j);
				}
				
				ProMTable Ptabel5 = new ProMTable(tableModels);
				Ptabel5.setPreferredSize(new Dimension(1000, 500));
				Ptabel5.setAutoResizeMode(0);
				panel4.add(Ptabel5);
				context.showConfiguration("Support 2-itemset",panel4);
				
				if(CA.columnsNameS2.length>0)
				{
					InteractionResult result3 = context.showConfiguration("Threshold 2-itemsets", new ARLParameter2().ARLParam(param));
					if (result3.equals(InteractionResult.CANCEL)) {
						context.getFutureResult(0).cancel(true);
					}
					
					System.out.println("Param: "+param[0]);
					jumlahRole=CA.selection(param[0],columnsName2,tableFuzzy,ARL.tableARL,jumlahRole,count);
					count++;
				}
				else
				{
					flag=false;
				}
				//flag=false;
			}
			else if(count==3)
			{
				Object[][] tabels = new Object[1][];
				DefaultTableModel tableModels = new DefaultTableModel(tabels,CA.columnsNameS3);
				for(int j=0;j<tableModels.getColumnCount();j++)
				{
					tableModels.setValueAt(CA.tableSupport3[j], 0, j);
				}
				
				ProMTable Ptabel5 = new ProMTable(tableModels);
				Ptabel5.setPreferredSize(new Dimension(1000, 500));
				Ptabel5.setAutoResizeMode(0);
				panel5.add(Ptabel5);
				context.showConfiguration("Support 3-itemset",panel5);
				
				if(CA.columnsNameS3.length>0)
				{
					InteractionResult result3 = context.showConfiguration("Threshold 3-itemsets", new ARLParameter2().ARLParam(param));
					if (result3.equals(InteractionResult.CANCEL)) {
						context.getFutureResult(0).cancel(true);
					}
					
					
					jumlahRole=CA.selection(param[0],columnsName2,tableFuzzy,ARL.tableARL,jumlahRole,count);
					count++;
				}
				else
				{
					flag=false;
				}
				
			}
			
			else if(count==4)
			{
				Object[][] tabels = new Object[1][];
				DefaultTableModel tableModels = new DefaultTableModel(tabels,CA.columnsNameS4);
				for(int j=0;j<tableModels.getColumnCount();j++)
				{
					tableModels.setValueAt(CA.tableSupport4[j], 0, j);
				}
				
				ProMTable Ptabel5 = new ProMTable(tableModels);
				Ptabel5.setPreferredSize(new Dimension(1000, 500));
				Ptabel5.setAutoResizeMode(0);
				panel6.add(Ptabel5);
				context.showConfiguration("Support 4-itemset",panel6);
				
				if(CA.columnsNameS4.length>0)
				{
					InteractionResult result3 = context.showConfiguration("Threshold 4-itemsets", new ARLParameter2().ARLParam(param));
					if (result3.equals(InteractionResult.CANCEL)) {
						context.getFutureResult(0).cancel(true);
					}
					
					
					jumlahRole=CA.selection(param[0],columnsName2,tableFuzzy,ARL.tableARL,jumlahRole,count);
					count++;
				}
				else
				{
					flag=false;
				}
				
			}
			else if(count==5)
			{
				Object[][] tabels = new Object[1][];
				DefaultTableModel tableModels = new DefaultTableModel(tabels,CA.columnsNameS5);
				for(int j=0;j<tableModels.getColumnCount();j++)
				{
					tableModels.setValueAt(CA.tableSupport5[j], 0, j);
				}
				
				ProMTable Ptabel5 = new ProMTable(tableModels);
				Ptabel5.setPreferredSize(new Dimension(1000, 500));
				Ptabel5.setAutoResizeMode(0);
				panel6.add(Ptabel5);
				context.showConfiguration("Support 5-itemset",panel6);
				
				if(CA.columnsNameS5.length>0)
				{
					InteractionResult result3 = context.showConfiguration("Threshold 5-itemsets", new ARLParameter2().ARLParam(param));
					if (result3.equals(InteractionResult.CANCEL)) {
						context.getFutureResult(0).cancel(true);
					}
					
					
					jumlahRole=CA.selection(param[0],columnsName2,tableFuzzy,ARL.tableARL,jumlahRole,count);
					count++;
				}
				else
				{
					flag=false;
				}
				
			}
		}
		ARL.jumlahRoles=jumlahRole;
		System.out.println("jumlah role: "+jumlahRole);
		for(int i=0;i<jumlahRole;i++)
		{
			System.out.println("Aturan: "+ARL.tableARL[i][0]+" -- Supp: "+ARL.tableARL[i][1]+" -- Conf: "+ARL.tableARL[i][2]);
		}
		
		
		for(int i=0;i<ARL.tableARL.length;i++)
		{
			for(int j=0;j<tableModel2.getColumnCount();j++)
			{
				tableModel2.setValueAt(ARL.tableARL[i][j], i, j);
			}
		}
		
		ProMTable Ptabel3 = new ProMTable(tableModel2);
		Ptabel3.setPreferredSize(new Dimension(1000, 500));
		Ptabel3.setAutoResizeMode(0);
		panel2.add(Ptabel3);
		
		context.showConfiguration("Aturan Asosiasi",panel2);
		
		return  ARL;
	}
	
	public FraudwithFuzzyARL2() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void countFraudMADM()
	{
		CI.countWeight(jumlahPakar[0], simpan);
		CI.countProb(tableContent, columnsName);
		CI.membership();
		CI.countFraud();
	}

}
