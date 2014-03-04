package org.processsmining.fraudDetection;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMTable;

public class FraudwithFuzzyARL2 {
	
	public String[] columnsName = {"SkipS","SkipD","Tmin","Tmax","wResource","wDutySec","wDutyDec","wDutyCom","wPattern","wDecision","Fraud"};
	public Object[][] tableContent = {
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
	
	Object[][] tabel = new Object[25][];
	String[] tabelName = {"Tabel Fraud"};
	public String[][] simpan;
	public Integer[] jumlahPakar;
	public DefaultTableModel tableModel = new DefaultTableModel(tabel,columnsName);
	public CountImportance CI = new CountImportance();
	//JTable tabel = new JTable(tableContent,columnsName);
	
	@Plugin(
			name="Fraud Detection with Fuzzy Association Rule Learning Plugin versi 2",
			parameterLabels = {},
			returnLabels ={"Fraud Results"},
			returnTypes = {JPanel.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Fernandes Sinaga",
			author = "Fernandes Sinaga",
			email = "nandes.02@gmail.com"
			)
	
	//UI untuk menampilkan tabel fraud
	public JPanel FraudTabel(final UIPluginContext context)
	{
		JPanel panel = new JPanel();
		
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
			
			//System.out.println("C1: "+simpan[i][0]+" -- C2: "+simpan[i][1]+" -- C3: "+simpan[i][2]+" -- C4: "+simpan[i][3]+" -- C5: "+simpan[i][4]+" -- C6: "+simpan[i][5]+" -- C7: "+simpan[i][6]+" -- C8: "+simpan[i][7]+" -- C9: "+simpan[i][8]+" -- C10: "+simpan[i][9]);
		}
		
		CI.countWeight(jumlahPakar[0], simpan);
		CI.countProb(tableContent, columnsName);
		CI.membership();
		CI.countFraud();
		
		InteractionResult result2 = context.showConfiguration("Fuzzy Table", new newFuzzy().FuzzyTabel2());
		if (result2.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		/*
		InteractionResult result8 = context.showConfiguration("Daftar Kepentingan", new countFraud().DerajatKepentingan(simpan));
		if (result8.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		
		InteractionResult result5 = context.showConfiguration("Fuzzy Table2", new countFraud().TabelFuzzyMADM());
		if (result5.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		
		InteractionResult result9 = context.showConfiguration("Daftar Kepentingan", new countFraud().TabelBobotFraud(simpan));
		if (result9.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		
		InteractionResult result10 = context.showConfiguration("Daftar Kepentingan", new countFraud().HasilBobotFraud(simpan));
		if (result10.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		*/
		InteractionResult result3 = context.showConfiguration("ARL Parameter", new ARLParameter2().ARLParam());
		if (result3.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		
		InteractionResult result4 = context.showConfiguration("Selection 1Itemsets", new CountARL2().ARLTable());
		if (result4.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		
		return  panel;
	}

}
