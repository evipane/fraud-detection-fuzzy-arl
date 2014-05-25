package org.processsmining.fraudDetection;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.fraud.model.InsertFraudData;
import org.processmining.pnml.controller.ReadPNML;

public class TestingFraud {
	
	public String[] columnsName = {"SkipS","SkipD","Tmin","Tmax","wResource","wDutySec","wDutyDec","wDutyCom","wPattern","wDecision","Fraud","Case"};
	public CountARL2 ca = new CountARL2();
	public Object[][] tableContents;
	public Object[][] tableContents2;
	public CountImportance CI = new CountImportance();
	public String[][] Detect;
	public String[] columname = {"Transition","Role","Resource","Time"};
	public Object[][] tabel2;
	public Object[][] aturan;
	public Object[][] terpilih;
	public Object[][] bobot;
	public String[] rule = {"Aturan","Support","Confidence","Jumlah Kombinasi","Bobot Fraud"};
	public Object[][] tableFuzzy ;
	public List<String> DecTransitions = new ArrayList<String>();
	public List<String> SeqTransitions = new ArrayList<String>();
	public Object[][] tableTransition2;
	public String[] columnsName2 = {"SkipSL","SkipSM","SkipSH","SkipDL","SkipDM","SkipDH","TminL","TminM","TminH","TmaxL","TmaxM","TmaxH","wResourceL","wResourceM","wResourceH","wDutySecL","wDutySecM","wDutySecH","wDutyDecL","wDutyDecM","wDutyDecH","wDutyComL","wDutyComM","wDutyComH","wPatternL","wPatternM","wPatternH","wDecisionL","wDecisionM","wDecisionH","Fraud"};
	public String[] columnsName3 = {"Case","Aturan","Confidence","Bobot Fraud","Kelas Fraud"};
	int seq ;
	int dec ;
	int total ;
	@Plugin(
			name="Detect Fraud with Association Rule Data",
			parameterLabels = {},
			returnLabels ={"Fraud Detect Results"},
			returnTypes = {JPanel.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Fernandes Sinaga",
			author = "Fernandes Sinaga",
			email = "nandes.02@gmail.com"
			)
	
	public JPanel hasilDeteksi(final UIPluginContext context,InsertFraudData fraud, AssociationRule ARL, ReadPNML pnml)
	{
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		tableContents = new Object[fraud.frauds.size()][columnsName.length];
		tableTransition2 = new Object[pnml.transitions.size()][columname.length];
		
		readSeqDec(pnml);
		
		seq = SeqTransitions.size();
		dec = DecTransitions.size();
		total = seq+dec;
		
		
		for(int i=0;i<fraud.frauds.size();i++)
		{
			tableContents[i][0]= fraud.frauds.get(i).getSkipSeq();
			tableContents[i][1]= fraud.frauds.get(i).getSkipDec();
			tableContents[i][2]= fraud.frauds.get(i).getTmin();
			tableContents[i][3]= fraud.frauds.get(i).getTmax();
			tableContents[i][4]= fraud.frauds.get(i).getWResource();
			tableContents[i][5]= fraud.frauds.get(i).getWDutySeq();
			tableContents[i][6]= fraud.frauds.get(i).getWDutyDec();
			tableContents[i][7]= fraud.frauds.get(i).getWDutyCom();
			tableContents[i][8]= fraud.frauds.get(i).getwPattern();
			tableContents[i][9]= fraud.frauds.get(i).getwDecision();
			tableContents[i][10]= fraud.frauds.get(i).getFraud();
			tableContents[i][11]= fraud.frauds.get(i).getCase();
		}
		CI.countWeight(ARL.jumlahPakar, ARL.inputPakar);
		CI.countProb(tableContents, columnsName,seq,dec,total);
		CI.membership();
		CI.countFraud();
		
		for(int i=0;i<tableContents.length;i++)
		{
			tableContents[i][columnsName.length-2] = CI.fraud[i];
		}
		Object[][] tabel = new Object[fraud.frauds.size()][];
		DefaultTableModel tableModel = new DefaultTableModel(tabel,columnsName);
		for(int i=0;i<tableContents.length;i++)
		{
			for(int j=0;j<tableModel.getColumnCount();j++)
			{
				tableModel.setValueAt(tableContents[i][j], i, j);
			}
		}
		System.out.println(tableModel.getValueAt(0, 0));
		
		
		ProMTable Ptabel = new ProMTable(tableModel);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel1.add(Ptabel);
		
		context.showConfiguration("Tabel Pelanggaran"+ "",panel1);
		
		tableFuzzy = new Object[tableContents.length][columnsName2.length];
		
		InteractionResult result2 = context.showConfiguration("Tabel Fuzzy", new Fuzzy().FuzzyTabel(tableContents,columnsName,tableFuzzy));
		if (result2.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		
		tableContents2 = new Object[tableContents.length][columnsName2.length];
		String[][] dummy = new String[tableFuzzy.length][columnsName2.length];
		double temp=0.0;
		String tempss="";
		
		for(int i=0;i<tableFuzzy.length;i++)
		{
			int c=0;
			for(int j=0;j<columnsName2.length;j++)
			{
				if((Double)tableFuzzy[i][j]>temp)
				{
					temp = (Double)tableFuzzy[i][j];
					tempss = columnsName2[j];
				}
				
				if(j%3==2)
				{
					dummy[i][c]=tempss;
					temp=0.0;
					c++;
				}
				
				if(j==columnsName2.length-1)
				{
					dummy[i][c]=tempss;
					temp=0.0;
					c++;
				}
			}
			
			for(int j=0;j<columnsName2.length;j++)
			{
				tableContents2[i][j]=0;
				for(int k=0;k<c;k++)
				{
					if(columnsName2[j].equals(dummy[i][k]))
					{
						tableContents2[i][j]=1;
					}
				}
				
			}
		}
		aturan = new Object[ARL.jumlahRoles][rule.length];
		Detect = new String[tableContents2.length][200];
		terpilih = new Object[tableContents2.length][4];
		bobot = new Object[tableContents2.length][2];
		for(int i=0;i<tableContents2.length;i++)
		{
			int index=0;
			for(int j=0;j<columnsName2.length;j++)
			{
				if((Integer)tableContents2[i][j]>0)
				{
					Detect[i][index]=columnsName2[j];
					System.out.println("detect: "+Detect[i][index]);
					index++;
				}
			}
			
			int jumlah=0;
			for(int j=0;j<ARL.jumlahRoles;j++)
			{
				String[] arl = ((String)ARL.tableARL[j][0]).split("-");
				int count=0;
				for(int l=0;l<arl.length;l++)
				{
					for(int k=0;k<index;k++)
					{
						if(arl[l].equals(Detect[i][k]))
						{
							count++;
						}
					}
				}
				
				if(count==arl.length)
				{
					aturan[jumlah][0]=ARL.tableARL[j][0];
					aturan[jumlah][1]=ARL.tableARL[j][1];
					aturan[jumlah][2]=ARL.tableARL[j][2];
					aturan[jumlah][3]=count;
					aturan[jumlah][4]=CI.fraud[i];
					jumlah++;
				}
				else if(count<arl.length)
				{
					aturan[jumlah][0]="Tidak Ada";
					aturan[jumlah][1]=0.0;
					aturan[jumlah][2]=0.0;
					aturan[jumlah][3]=0;
					aturan[jumlah][4]=CI.fraud[i];
					jumlah++;
					System.out.println("Masuk sini -- jumlah: "+jumlah);
				}
				
			}
			
			int save=0;
			int indeks=0;
			for(int n=0;n<jumlah;n++)
			{
				if((Integer)aturan[n][3]>save)
				{
					save=(Integer)aturan[n][3];
					indeks=n;
					terpilih[i][0] = aturan[n][0];
					terpilih[i][1] = aturan[n][1];
					terpilih[i][2] = aturan[n][2];
					terpilih[i][3] = aturan[n][4];
				}
				else if((Integer)aturan[n][3]==save)
				{
					System.out.println("Masuk sini22 -- save: "+save);
					if((Double)aturan[indeks][2]<(Double)aturan[n][2] || (Double)aturan[indeks][2]==(Double)aturan[n][2] )
					{
						indeks=n;
						terpilih[i][0] = aturan[n][0];
						terpilih[i][1] = aturan[n][1];
						terpilih[i][2] = aturan[n][2];
						terpilih[i][3] = aturan[n][4];
					}
				}
				
			}
			System.out.println("Terpilih: "+terpilih[i][0]);
		}
		grouping();
		tabel2 = new Object[tableContents2.length][];
		DefaultTableModel tableModel2 = new DefaultTableModel(tabel2,columnsName3);
		
		for(int i=0;i<tableContents.length;i++)
		{
			tableModel2.setValueAt(tableContents[i][11], i, 0);
			tableModel2.setValueAt(terpilih[i][0], i, 1);
			tableModel2.setValueAt(terpilih[i][2], i, 2);
			tableModel2.setValueAt(terpilih[i][3], i, 3);
			tableModel2.setValueAt(bobot[i][1], i, 4);
		}
		
		ProMTable Ptabel2 = new ProMTable(tableModel2);
		Ptabel2.setPreferredSize(new Dimension(1000, 500));
		Ptabel2.setAutoResizeMode(0);
		panel2.add(Ptabel2);
		context.showConfiguration("Hasil Deteksi Fraud",panel2);
		
		return panel1;
		
	}
	
	public void grouping()
	{
		for(int i=0;i<tableContents.length;i++)
		{
			if((Double)terpilih[i][2]>0)
			{
				bobot[i][0]=terpilih[i][2];
			}
			else
			{
				bobot[i][0]=terpilih[i][3];
			}
		}
		
		for(int i=0;i<tableContents.length;i++)
		{
			bobot[i][1]=member((Double)bobot[i][0]);
		}
	}
	
	public String member(Double bobotoh)
	{
		double not = notFraud(bobotoh);
		double semi = semiFraud(bobotoh,0.1,0.2,0.3,0.4);
		double fraud = semiFraud(bobotoh,0.3,0.4,0.6,0.7);
		double high = highFraud(bobotoh);
		double temp=0;
		String anggota="";
		if(not>temp)
		{
			anggota="Tidak Fraud";
			temp=not;
		}
		else if(semi>temp)
		{
			anggota="Semi Fraud";
			temp=semi;
		}
		else if(fraud>temp)
		{
			anggota="Fraud";
			temp=fraud;
		}
		else if(high>temp)
		{
			anggota="Yakin Fraud";
			temp=high;
		}
		return anggota;
	}
	
	public double highFraud(double bebet)
	{
		double a=0.6;
		double b=0.7;
		double result=0;
		if(bebet<a || bebet==a)
		{
			result=0;
		}
		else if(bebet>a && bebet<b)
		{
			result = (bebet-a)/(b-a);
		}
		else if(bebet>b || bebet==b)
		{
			result=1;
		}
		
		return result;
	}
	
	public double semiFraud(double bebet,double a1,double b1, double c1, double d1)
	{
		double a=a1;
		double b=b1;
		double c=c1;
		double d=d1;
		double result=0;
		
		if(bebet<a || bebet==a)
		{
			result=0;
		}
		else if(bebet>a && bebet<b)
		{
			result = (bebet-a)/(b-a);
		}
		else if(bebet>b && bebet<c || bebet==b ||bebet==c)
		{
			result = 1;
		}
		else if(bebet>c && bebet<d)
		{
			result = (d-bebet)/(d-c);
		}
		else if(bebet>d || bebet==d)
		{
			result=0;
		}
		
		return result;
	}
	
	public double notFraud(double bebet)
	{
		double a=0.1;
		double b=0.2;
		double result=0;
		if(bebet<a || bebet==a)
		{
			result=1;
		}
		else if(bebet>a && bebet<b)
		{
			result = (b-bebet)/(b-a);
		}
		else if(bebet>b || bebet==b)
		{
			result=0;
		}
		
		return result;
	}
	public void readSeqDec(ReadPNML pnml)
	{
		int count=0;
		int temps;
		boolean flags=false;
		
		for(int i=0;i<pnml.transitions.size();i++)
		{
			flags=false;
			if(tableTransition2.length==0)
			{
				tableTransition2[count][0] = pnml.transitions.get(i).getName();
				tableTransition2[count][1] = 1;
				count++;
			}
			else
			{
				for(int j=0;j<tableTransition2.length;j++)
				{
					if(pnml.transitions.get(i).getName().equals(tableTransition2[j][0]))
					{
						System.out.println("masuk sini");
						temps = (Integer) tableTransition2[j][1];
						tableTransition2[j][1]=temps+1;
						flags=true;
					}
				}
			}
			if(flags==false)
			{
				tableTransition2[count][0] = pnml.transitions.get(i).getName();
				tableTransition2[count][1] = 1;
				count++;
			}
		}
		for(int i=0;i<count;i++)
		{
			String temp1 = (String)tableTransition2[i][0];
			String[] str = temp1.split(" ");
			if((Integer)tableTransition2[i][1]>1 && str[1].equals("Complete"))
			{
				DecTransitions.add(str[0].toString());
				System.out.println("Decision: "+str[0]);
			}
			else if((Integer)tableTransition2[i][1]<2 && str[1].equals("Complete"))
			{
				SeqTransitions.add(str[0].toString());
				System.out.println("Sequence: "+str[0]);
			}
		}
		
		System.out.println("Seq: "+SeqTransitions.size()+" -- Dec: "+DecTransitions.size());
	}
}
