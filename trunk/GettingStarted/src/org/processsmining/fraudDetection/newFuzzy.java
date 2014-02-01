package org.processsmining.fraudDetection;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.util.ui.widgets.ProMTable;


public class newFuzzy {
	FraudwithFuzzyARL2 ffa = new FraudwithFuzzyARL2();
	fuzzyAttribute fa = new fuzzyAttribute();
	
	public String[] columnsName2 = {"SkipSL","SkipSM","SkipSH","SkipDL","SkipDM","SkipDH","TminL","TminM","TminH","TmaxL","TmaxM","TmaxH","wResourceL","wResourceM","wResourceH","wDutySecL","wDutySecM","wDutySecH","wDutyDecL","wDutyDecM","wDutyDecH","wDutyComL","wDutyComM","wDutyComH","wPatternL","wPatternM","wPatternH","wDecisionL","wDecisionM","wDecisionH","Fraud"};
	//fungsi fuzzy keanggotaan min
	Object[][] tabel2 = new Object[25][];
	Object[][] tabel3 = new Object[25][];
	public DefaultTableModel tableModelPercent = new DefaultTableModel(tabel2,ffa.columnsName);
	public DefaultTableModel tableModelFuzzy = new DefaultTableModel(tabel2,columnsName2);
	
	public int dummyLength=0;
	
	public Object[][] tableDummy;
	public Object[][] tableFuzzy;

	public Double fuzzyMin(Double value)
	{
		double aMin = fa.getA();
		double bMin = fa.getB();
		double cMin = fa.getC();
		double minValue = 0;
		
		if(value<aMin||value==aMin)
		{
			minValue=0;
		}
		else if(value>aMin && value<bMin||value==bMin)
		{
			minValue = 1;
		}
		else if(value>bMin && value <cMin)
		{
			minValue = (cMin-value)/(cMin-bMin);
		}
		else if(value==cMin ||value > cMin)
		{
			minValue=0;
		}
		
		return minValue;
	}
	
	//fungsi fuzzy keanggotaan mid
	public Double fuzzyMid(Double value)
	{
		double aMid = fa.getB();
		double bMid = fa.getC();
		double cMid = fa.getD();
		double dMid = fa.getE();
		double midValue = 0;
		
		if(value<aMid||value==aMid)
		{
			midValue=0;
		}
		else if(value>aMid && value<bMid)
		{
			midValue = (value-aMid)/(bMid-aMid);
		}
		else if((value>bMid && value<cMid)||value==bMid ||value==cMid)
		{
			midValue = 1;
		}
		else if(value>cMid && value <dMid)
		{
			midValue = (dMid-value)/(dMid-cMid);
		}
		else if(value==dMid ||value > dMid)
		{
			midValue=0;
		}
		
		return midValue;
	}
	
	//fungsi fuzzy keanggotaan high
	public Double fuzzyHigh(Double value)
	{
		double aHigh = fa.getD();
		double bHigh = fa.getE();
		double HighValue = 0;
		
		if(value<aHigh || value==aHigh)
		{
			HighValue=0;
		}
		else if(value>aHigh && value<bHigh)
		{
			HighValue = (value-aHigh)/(bHigh-aHigh);
		}
		else if(value==bHigh ||value > bHigh)
		{
			HighValue=1;
		}
		
		return HighValue;
	}
	
	
	public JPanel FuzzyTabel2()
	{
		JPanel panel3 = new JPanel();
		
		//isi tabel dengan nilai fuzzy
		//PercenTabel();

		tableFuzzy =  new Object[ffa.tableContent.length][columnsName2.length];
	
		for(int i=0;i<ffa.tableContent.length;i++)
		{
			int k=0;
			for(int j=0;j<columnsName2.length;j++)
			{
				if(ffa.tableModel.getColumnName(i)=="SkipS" || ffa.tableModel.getColumnName(i)=="wDutySec")
				{
					fa.fuzzySeq();
				}
				else if(ffa.tableModel.getColumnName(i)=="SkipD" || ffa.tableModel.getColumnName(i)=="wDutyDec"||ffa.tableModel.getColumnName(i)=="wDutyCom" || ffa.tableModel.getColumnName(i)=="wDecision")
				{
					fa.fuzzyDec();
				}
				else if(ffa.tableModel.getColumnName(i)=="Tmin"|| ffa.tableModel.getColumnName(i)=="Tmax"||ffa.tableModel.getColumnName(i)=="wResource" || ffa.tableModel.getColumnName(i)=="wPattern")
				{
					fa.fuzzyAll();
				}
				
				if(j==columnsName2.length-1)
				{
					tableFuzzy[i][j] = ffa.tableContent[i][k];
				}
				else if(j%3==0)
				{
					tableFuzzy[i][j] = fuzzyMin((Double)ffa.tableContent[i][k]);
				}
				else if (j%3==1)
				{
					tableFuzzy[i][j] = fuzzyMid((Double)ffa.tableContent[i][k]);
				}
				else if(j%3==2)
				{
					tableFuzzy[i][j] = fuzzyHigh((Double)ffa.tableContent[i][k]);
					k++;
				}
				
				
			}
		}
		//isi tabel percent ke tabel model
		for(int i=0;i<tableFuzzy.length;i++)
		{
			for(int j=0;j<tableModelFuzzy.getColumnCount();j++)
			{
				tableModelFuzzy.setValueAt(tableFuzzy[i][j], i, j);
			}
		}
		ProMTable Ptabel1 = new ProMTable(tableModelFuzzy);
		Ptabel1.setPreferredSize(new Dimension(1000, 500));
		Ptabel1.setAutoResizeMode(0);
		panel3.add(Ptabel1);
		//context.showConfiguration("Tabel Fraud",panel2);

		return panel3;
	}

}
