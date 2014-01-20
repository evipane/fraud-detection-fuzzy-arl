import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.util.ui.widgets.ProMTable;


public class CountARL {
	
	Fuzzy fuzzy = new Fuzzy();
	public Object[] tableSupport ;
	public String[] columnsNameS;
	public String[] columnsNameS2;
	public Integer[] indexPass;
	public Object[] tablePass;
	Object[][] tabel ; 
	public DefaultTableModel tableModelItems1;
	public void countSupport(String[] columName)
	{
		fuzzy.FuzzyTabel();
		tableSupport = new Object[columName.length];
		for(int i=0;i<columName.length;i++)
		{
			double supp=0;
			for(int j=0;j<fuzzy.tableFuzzy.length;j++)
			{
				supp+=(Double)fuzzy.tableFuzzy[j][i];
			}
			tableSupport[i] = supp/20;
		}
	}
	
	
	public void selection(double param,String[] columName1, int next)
	{
		System.out.println("MASUK!");
		if(next==1)
		{
			countSupport(columName1);
		}
		else if(next==2)
		{
			countSupport2(columName1);
		}
		
		int k=1;
		
		for(int i=0;i<tableSupport.length;i++)
		{
			if(param<(Double)tableSupport[i] || param==(Double)tableSupport[i])
			{
				k++;
			}
		}
		columnsNameS = new String[k-1];
		indexPass = new Integer[k-1];
		int l=0;
		for(int i=0;i<tableSupport.length;i++)
		{
			if(param<(Double)tableSupport[i] || param==(Double)tableSupport[i])
			{
				
				columnsNameS[l] = columName1[i];
				//System.out.println("column :"+columnsNameS[l]);
				indexPass[l]=i;
				l++;
			}
		}
		
		tablePass = new Object[indexPass.length];
		for(int i=0;i<indexPass.length;i++)
		{
			tablePass[i] = tableSupport[indexPass[i]];
		}
		
		//setColumName();
		
	}
	
	public int combination(String[] columname)
	{
		int result=0;
		
		for(int i=1;i<columname.length;i++)
		{
			result+=columname.length-i;
		}
		
		return result;
	}
	
	public void setColumName(String[] columname)
	{
		System.out.println("panjang: "+columnsNameS.length);
		int count=0;
		columnsNameS2 = new String[combination(columname)];
		//System.out.println("panjang: "+columnsNameS.length);
		for(int i=0;i<columname.length;i++)
		{
			for(int j=i+1;j<columname.length;j++)
			{
				System.out.println("i: "+columnsNameS[i]+" -- j: "+columnsNameS[j]);
				columnsNameS2[count] = columnsNameS[i]+"-"+columnsNameS[j]; 
				count++;
				//System.out.println("hasil: "+columnsNameS2[count]);
			}
		}
	}
	
	public void countSupport2(String[] columnName)
	{
		fuzzy.FuzzyTabel();
		tableSupport = new Object[columnsNameS2.length];
		int count = 0;
		
		for(int i=0; i<columnsNameS.length;i++)
		{
			
			for(int j=i+1;j<columnsNameS.length;j++)
			{
				double supp=0;
				for(int k=0;k<fuzzy.tableFuzzy.length;k++)
				{
					supp+=Math.min((Double)fuzzy.tableFuzzy[k][indexPass[i]],(Double)fuzzy.tableFuzzy[k][indexPass[j]]);
				}
				tableSupport[count]=supp;
				count++;
			}
			
		}
	}
	
	public JPanel ARLTable()
	{
		JPanel panel3 = new JPanel();
		ARLParameter arlp = new ARLParameter();
		arlp.ARLParam();
		int next=1;
		selection(arlp.parameter,fuzzy.columnsName2,next);
		
		if(tablePass.length>1)
		{
			next++;
			setColumName(columnsNameS);
			selection(arlp.parameter,columnsNameS2,next);
		}
		tabel = new Object[1][];
		tableModelItems1 = new DefaultTableModel(tabel,columnsNameS2);
		
		//isi tabel dengan nilai support
		for(int i=0;i<tablePass.length;i++)
		{
			tableModelItems1.setValueAt(tablePass[i], 0, i);
		}
		
		ProMTable Ptabel1 = new ProMTable(tableModelItems1);
		Ptabel1.setPreferredSize(new Dimension(1000, 500));
		Ptabel1.setAutoResizeMode(0);
		panel3.add(Ptabel1);
		//context.showConfiguration("Tabel Fraud",panel2);

		return panel3;
	}

}
