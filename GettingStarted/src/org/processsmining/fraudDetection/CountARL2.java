package org.processsmining.fraudDetection;



import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.util.ui.widgets.ProMTable;

public class CountARL2 {
	
	//newFuzzy fuzzy = new newFuzzy();
	public Object[] tableSupport;
	public Object[] tableSupport2;
	public Object[] tableSupport3;
	public Object[] tableSupport4;
	public Object[] tableSupport5;
	public String[] columnsNameS;
	public String[] columnsNameS2;
	public String[] columnsNameS3;
	public String[] columnsNameS4;
	public String[] columnsNameS5;
	public Integer[] indexPass;
	public Object[] tablePass;
	Object[][] tabel ; 
	public DefaultTableModel tableModelItems1;
	
	public void countSupport(Object[][] tableFuzzy,String[] columName)
	{
		//fuzzy.FuzzyTabel2();
		tableSupport = new Object[columName.length];
		for(int i=0;i<columName.length;i++)
		{
			double supp=0;
			for(int j=0;j<tableFuzzy.length;j++)
			{
				supp+=(Double)tableFuzzy[j][i];
			}
			tableSupport[i] = supp/tableFuzzy.length;
		}
	}
	
	
	public int selection(double param,String[] columName1,Object[][] tableFuzzy,Object[][] tableARL,int jumlahRole,int next)
	{
		if(next==1)
		{
			select(tableSupport,columName1,param);
			System.out.println("jumlah role1: "+jumlahRole);
			setColumName(columnsNameS);
			countSupport2(tableFuzzy);
			int index=0;
			for(int i=0;i<columnsNameS2.length;i++)
			{
				String[] str = columnsNameS2[i].split("-");
				if(str[1].equals("Fraud"))
				{
					//System.out.println("ISI DONK!");
					String temp = str[0];
					tableARL[jumlahRole][0] = columnsNameS2[i];
					index=searchIndex(columName1, temp);
					tableARL[jumlahRole][1] = tableSupport2[i];
					tableARL[jumlahRole][2] = (Double)tableSupport2[i]/(Double)tableSupport[index];
					System.out.println("Aturan: "+tableARL[jumlahRole][0]+" -- Supp: "+tableARL[jumlahRole][1]+" -- Conf: "+tableARL[jumlahRole][2]);
					jumlahRole++;
					
				}
			}
			
		}
		
		else if(next==2)
		{
			select(tableSupport2,columnsNameS2,param);
			setColumnName2(columnsNameS, columnsNameS);
			countSupport3(tableFuzzy,columName1);
			System.out.println("jumlah role2: "+jumlahRole);
			int index1=0;
			for(int i=0;i<columnsNameS3.length;i++)
			{
				String[] str = columnsNameS3[i].split("-");
				if(str[2].equals("Fraud"))
				{
					//System.out.println("ISI DONK!");
					String temp = str[0]+str[1];
					tableARL[jumlahRole][0] = columnsNameS3[i];
					index1=searchIndex(columnsNameS, temp);
					//System.out.println("index: "+index1);
					tableARL[jumlahRole][1] = tableSupport3[i];
					tableARL[jumlahRole][2] = (Double)tableSupport3[i]/(Double)tableSupport2[index1];
					System.out.println("Aturan: "+tableARL[jumlahRole][0]+" -- Supp: "+tableARL[jumlahRole][1]+" -- Conf: "+tableARL[jumlahRole][2]);
					jumlahRole++;
					
				}
			}
		}
		
		else if(next==3)
		{
			select(tableSupport3,columnsNameS3,param);
			setColumnName3(columnsNameS, columnsNameS);
			countSupport4(tableFuzzy,columName1);
			int index1=0;
			for(int i=0;i<columnsNameS4.length;i++)
			{
				String[] str = columnsNameS4[i].split("-");
				if(str[3].equals("Fraud"))
				{
					//System.out.println("ISI DONK!");
					String temp = str[0]+str[1]+str[2];
					tableARL[jumlahRole][0] = columnsNameS4[i];
					index1=searchIndex(columnsNameS, temp);
					//System.out.println("index: "+index1);
					tableARL[jumlahRole][1] = tableSupport4[i];
					tableARL[jumlahRole][2] = (Double)tableSupport4[i]/(Double)tableSupport3[index1];
					System.out.println("Aturan: "+tableARL[jumlahRole][0]+" -- Supp: "+tableARL[jumlahRole][1]+" -- Conf: "+tableARL[jumlahRole][2]);
					jumlahRole++;
					
				}
			}
		}
		else if(next==4)
		{
			select(tableSupport4,columnsNameS4,param);
			setColumnName4(columnsNameS, columnsNameS);
			countSupport5(tableFuzzy,columName1);
			int index1=0;
			for(int i=0;i<columnsNameS5.length;i++)
			{
				String[] str = columnsNameS5[i].split("-");
				if(str[4].equals("Fraud"))
				{
					//System.out.println("ISI DONK!");
					String temp = str[0]+str[1]+str[2]+str[3];
					tableARL[jumlahRole][0] = columnsNameS5[i];
					index1=searchIndex(columnsNameS, temp);
					//System.out.println("index: "+index1);
					tableARL[jumlahRole][1] = tableSupport5[i];
					tableARL[jumlahRole][2] = (Double)tableSupport5[i]/(Double)tableSupport4[index1];
					System.out.println("Aturan: "+tableARL[jumlahRole][0]+" -- Supp: "+tableARL[jumlahRole][1]+" -- Conf: "+tableARL[jumlahRole][2]);
					jumlahRole++;
					
				}
			}
		}
		System.out.println("jumlah role3: "+jumlahRole);
		return jumlahRole;
	}
	
	public void select(Object[] support, String[] name,double param)
	{
		int k=1;
		
		for(int i=0;i<support.length;i++)
		{
			if(param<(Double)support[i] || param==(Double)support[i])
			{
				k++;
			}
		}
		columnsNameS = new String[k-1];
		indexPass = new Integer[k-1];
		int l=0;
		for(int i=0;i<support.length;i++)
		{
			if(param<(Double)support[i] || param==(Double)support[i])
			{
				
				columnsNameS[l] = name[i];
				//System.out.println("column :"+columnsNameS[l]);
				indexPass[l]=i;
				l++;
			}
		}
		
		tablePass = new Object[indexPass.length];
		for(int i=0;i<indexPass.length;i++)
		{
			tablePass[i] = support[indexPass[i]];
		}
	}
	
	public int searchIndex(String[] columName,String att)
	{
		int index = 0;
		for(int i=0;i<columName.length;i++)
		{
			if(att.equals(columName[i]))
			{
				index=i;
				break;
			}
		}
		
		return index;
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
		int count=0;
		columnsNameS2 = new String[combination(columname)];
		//System.out.println("panjang: "+columnsNameS.length);
		for(int i=0;i<columname.length;i++)
		{
			for(int j=i+1;j<columname.length;j++)
			{
				columnsNameS2[count] = columnsNameS[i]+"-"+columnsNameS[j]; 
				count++;
			}
		}
	}
	
	public void countSupport2(Object[][] tableFuzzy)
	{
		//fuzzy.FuzzyTabel2();
		tableSupport2 = new Object[columnsNameS2.length];
		int count = 0;
		
		for(int i=0; i<columnsNameS.length;i++)
		{
			
			for(int j=i+1;j<columnsNameS.length;j++)
			{
				double supp=0;
				for(int k=0;k<tableFuzzy.length;k++)
				{
					supp+=Math.min((Double)tableFuzzy[k][indexPass[i]],(Double)tableFuzzy[k][indexPass[j]]);
				}
				tableSupport2[count]=supp/tableFuzzy.length;
				count++;
			}
			
		}
	}
	
	public void setColumnName2(String[] name1, String[] name2)
	{
		int result=1;
		System.out.println("MASUK KE SINI!");
		for(int i=0;i<name1.length;i++)
		{
			for(int j=i+1;j<name2.length;j++)
			{
				String[] str = name1[i].split("-");
				String[] str1 = name2[j].split("-");
				
				if(str[0].equals(str1[0]))
				{
					result++;
				}
			}
		}
		int count=0;
		columnsNameS3 = new String[result-1];
		for(int i=0;i<name1.length;i++)
		{
			for(int j=i+1;j<name2.length;j++)
			{
				String[] str = name1[i].split("-");
				String[] str1 = name2[j].split("-");
				if(str[0].equals(str1[0]))
				{
					columnsNameS3[count]=str[0]+"-"+str[1]+"-"+str1[1];
					System.out.println(columnsNameS3[count]);
					count++;
				}
			}
		}
		
		
	}
	
	public void setColumnName3(String[] name1, String[] name2)
	{
		int result=1;
		System.out.println("MASUK KE SINI!");
		for(int i=0;i<name1.length;i++)
		{
			for(int j=i+1;j<name2.length;j++)
			{
				String[] str = name1[i].split("-");
				String[] str1 = name2[j].split("-");
				
				if(str[0].equals(str1[0])&&str[2].equals(str1[2]))
				{
					result++;
				}
			}
		}
		int count=0;
		columnsNameS4 = new String[result-1];
		for(int i=0;i<name1.length;i++)
		{
			for(int j=i+1;j<name2.length;j++)
			{
				String[] str = name1[i].split("-");
				String[] str1 = name2[j].split("-");
				if(str[0].equals(str1[0])&&str[2].equals(str1[2]))
				{
					columnsNameS4[count]=str[0]+"-"+str[1]+"-"+str1[1]+"-"+str1[2];
					System.out.println(columnsNameS4[count]);
					count++;
				}
			}
		}
		
		
	}
	
	public void setColumnName4(String[] name1, String[] name2)
	{
		int result=1;
		System.out.println("MASUK KE SINI!");
		for(int i=0;i<name1.length;i++)
		{
			for(int j=i+1;j<name2.length;j++)
			{
				String[] str = name1[i].split("-");
				String[] str1 = name2[j].split("-");
				
				if(str[0].equals(str1[0])&&str[1].equals(str1[1])&&str[3].equals(str1[3]))
				{
					result++;
				}
			}
		}
		int count=0;
		columnsNameS5 = new String[result-1];
		for(int i=0;i<name1.length;i++)
		{
			for(int j=i+1;j<name2.length;j++)
			{
				String[] str = name1[i].split("-");
				String[] str1 = name2[j].split("-");
				if(str[0].equals(str1[0])&&str[2].equals(str1[2]))
				{
					columnsNameS5[count]=str[0]+"-"+str[1]+"-"+str1[2]+"-"+str1[2]+"-"+str1[3];
					System.out.println(columnsNameS5[count]);
					count++;
				}
			}
		}
		
		
	}
	
	public void countSupport3(Object[][] tableFuzzy, String[] columnName)
	{
		//fuzzy.FuzzyTabel2();
		tableSupport3 = new Object[columnsNameS3.length];
		
		for(int i=0; i<columnsNameS3.length;i++)
		{
			double supp=0;
			String[] str = columnsNameS3[i].split("-");
			for(int k=0;k<tableFuzzy.length;k++)
			{
				
				int a = searchIndex(columnName, str[0]);
				int b = searchIndex(columnName, str[1]);
				int c = searchIndex(columnName, str[2]);
				//System.out.println(a);
				supp+=Math.min((Math.min((Double)tableFuzzy[k][a],(Double)tableFuzzy[k][b])),(Double)tableFuzzy[k][c]);
			}
			//System.out.println("supp: "+supp);
			tableSupport3[i]=supp/tableFuzzy.length;
		}
	}
	
	public void countSupport4(Object[][] tableFuzzy, String[] columnName)
	{
		//fuzzy.FuzzyTabel2();
		tableSupport4 = new Object[columnsNameS4.length];
		
		for(int i=0; i<columnsNameS4.length;i++)
		{
			double supp=0;
			String[] str = columnsNameS4[i].split("-");
			for(int k=0;k<tableFuzzy.length;k++)
			{
				
				int a = searchIndex(columnName, str[0]);
				int b = searchIndex(columnName, str[1]);
				int c = searchIndex(columnName, str[2]);
				int d = searchIndex(columnName, str[3]);
				//System.out.println(a);
				supp+=Math.min(Math.min((Math.min((Double)tableFuzzy[k][a],(Double)tableFuzzy[k][b])),(Double)tableFuzzy[k][c]),(Double)tableFuzzy[k][d]);
			}
			//System.out.println("supp: "+supp);
			tableSupport4[i]=supp/tableFuzzy.length;
		}
	}
	
	public void countSupport5(Object[][] tableFuzzy, String[] columnName)
	{
		//fuzzy.FuzzyTabel2();
		tableSupport5 = new Object[columnsNameS5.length];
		
		for(int i=0; i<columnsNameS5.length;i++)
		{
			double supp=0;
			String[] str = columnsNameS5[i].split("-");
			for(int k=0;k<tableFuzzy.length;k++)
			{
				
				int a = searchIndex(columnName, str[0]);
				int b = searchIndex(columnName, str[1]);
				int c = searchIndex(columnName, str[2]);
				int d = searchIndex(columnName, str[3]);
				int e = searchIndex(columnName, str[4]);
				//System.out.println(a);
				supp+=Math.min(Math.min(Math.min((Math.min((Double)tableFuzzy[k][a],(Double)tableFuzzy[k][b])),(Double)tableFuzzy[k][c]),(Double)tableFuzzy[k][d]),(Double)tableFuzzy[k][e]);
			}
			//System.out.println("supp: "+supp);
			tableSupport5[i]=supp/tableFuzzy.length;
		}
	}
	
	public JPanel ARLTable()
	{
		JPanel panel3 = new JPanel();
		ARLParameter2 arlp = new ARLParameter2();
		//arlp.ARLParam();
		int next=1;
		//selection(arlp.parameter,fuzzy.columnsName2,next);
		
		if(tablePass.length>1)
		{
			next++;
			setColumName(columnsNameS);
			//selection(arlp.parameter,columnsNameS2,next);
		}
		
		next++;
		setColumnName2(columnsNameS, columnsNameS);
		//selection(arlp.parameter,columnsNameS3,next);
		
		tabel = new Object[1][];
		tableModelItems1 = new DefaultTableModel(tabel,columnsNameS);
		
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
