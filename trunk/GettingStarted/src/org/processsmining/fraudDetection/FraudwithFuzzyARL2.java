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
	
	public DefaultTableModel tableModel = new DefaultTableModel(tabel,columnsName);
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
		
		
		InteractionResult result2 = context.showConfiguration("Fuzzy Table", new newFuzzy().FuzzyTabel2());
		if (result2.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		
		
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
