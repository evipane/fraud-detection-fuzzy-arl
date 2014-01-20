import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.util.ui.widgets.ProMTable;


public class Fuzzy {
	FraudwithFuzzyARL ffa = new FraudwithFuzzyARL();
	private CountPercentage cp = new CountPercentage();
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
		double aMin = 0;
		double bMin = 15;
		double cMin = 25;
		double dMin = 40;
		double minValue = 0;
		
		if(value<aMin||value==aMin)
		{
			minValue=0;
		}
		else if(value>aMin && value<bMin)
		{
			minValue = (value-aMin)/(bMin-aMin);
		}
		else if((value>bMin && value<cMin)||value==bMin ||value==cMin)
		{
			minValue = 1;
		}
		else if(value>cMin && value <dMin)
		{
			minValue = (dMin-value)/(dMin-cMin);
		}
		else if(value==dMin ||value > dMin)
		{
			minValue=0;
		}
		
		return minValue;
	}
	
	//fungsi fuzzy keanggotaan mid
	public Double fuzzyMid(Double value)
	{
		double aMid = 35;
		double bMid = 50;
		double cMid = 60;
		double dMid = 75;
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
		double aHigh = 70;
		double bHigh = 90;
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
	
	@SuppressWarnings("null")
	public JPanel PercenTabel()
	{
		JPanel panel2 = new JPanel();
		
		tableDummy = new Object[ffa.tableContent.length][ffa.columnsName.length];
		
		//isi tabel dengan nilai persentase
		for(int i=0;i<ffa.tableContent.length;i++)
		{
			for(int j=0;j<ffa.columnsName.length;j++)
			{
				//tableDummy[i][j] = cp.countPercen((Double)ffa.tableContent[i][j], ffa.tableModel.getColumnName(j));
				String str = ffa.tableContent[i][j].toString(); 
				String str2 = ffa.tableModel.getColumnName(j);
				double d = Double.valueOf(str).doubleValue();
				
				double percen = cp.countPercen(d, str2);
				
				tableDummy[i][j] =new Double(percen);
			}
		}
		
		//isi tabel percent ke tabel model
		for(int i=0;i<tableDummy.length;i++)
		{
			for(int j=0;j<tableModelPercent.getColumnCount();j++)
			{
				tableModelPercent.setValueAt(tableDummy[i][j], i, j);
			}
		}
		
		dummyLength=tableDummy.length;
		ProMTable Ptabel = new ProMTable(tableModelPercent);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel2.add(Ptabel);
		//context.showConfiguration("Tabel Fraud",panel2);
		
		return panel2;
	}
	
	public JPanel FuzzyTabel()
	{
		JPanel panel3 = new JPanel();
		
		//isi tabel dengan nilai fuzzy
		PercenTabel();

		tableFuzzy =  new Object[tableDummy.length][columnsName2.length];
	
		for(int i=0;i<tableDummy.length;i++)
		{
			int k=0;
			for(int j=0;j<columnsName2.length;j++)
			{
				
				if(j==columnsName2.length-1)
				{
					tableFuzzy[i][j] = tableDummy[i][k];
				}
				else if(j%3==0)
				{
					tableFuzzy[i][j] = fuzzyMin((Double)tableDummy[i][k]);
				}
				else if (j%3==1)
				{
					tableFuzzy[i][j] = fuzzyMid((Double)tableDummy[i][k]);
				}
				else if(j%3==2)
				{
					tableFuzzy[i][j] = fuzzyHigh((Double)tableDummy[i][k]);
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
