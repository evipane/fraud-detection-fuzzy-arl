package org.processmining.plugins.compliance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.fraud.model.InsertFraudData;
import org.processmining.fraud.model.fraud;
import org.processmining.pnml.controller.ReadPNML;
public class ConformanceCheckAtribut {
	
	public Object[][] tableTransition;
	public Object[][] tableTransition2;
	public String[] columname = {"Transition","Role","Resource","Time"};
	public Object[][] tableLog;
	public String[] columname2 = {"Case ID","Event Name","Duration","Resource"};
	public String[] columname3 = {"Resource","Role"};
	public Object[][] tableRole;
	public List<String> DecTransitions = new ArrayList<String>();
	public List<String> SeqTransitions = new ArrayList<String>();
	public InsertFraudData fraudData = new InsertFraudData();
	public fraud Fraud = new fraud();
	public List<fraud>frauds = new ArrayList<fraud>();
	int c=0;
	int jumlahCase=0;
	@Plugin(
			name="Conformance Checking for Attribut",
			parameterLabels = {"Pnml Extended","Event log","Fraud Data"},
			returnLabels ={"Fraud Data"},
			returnTypes = {InsertFraudData.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Fernandes Sinaga",
			author = "Fernandes Sinaga",
			email = "nandes.02@gmail.com"
			)
	public InsertFraudData CheckConform (UIPluginContext context,ReadPNML pnml, XLog log, InsertFraudData fraud1)
	{
		tableTransition = new Object[pnml.transitions.size()][columname.length];
		tableTransition2 = new Object[pnml.transitions.size()][columname.length];
		tableRole = new Object[pnml.transitions.size()][columname.length];
		readPnml(pnml);
		readSeqDec(pnml);
		for(int i=0;i<c+1;i++)
		{
			System.out.println("Resource: "+tableRole[i][0]+" -- Role: "+tableRole[i][1]);
			//System.out.println("Transition: "+tableTransition[i][0]+" -- Role: "+tableTransition[i][1]+" -- Resource: "+tableTransition[i][2]+" -- Time: "+tableTransition[i][3]);
		}
		readLog(log);
		
		for(int i=0;i<tableLog.length-1;i++)
		{
			System.out.println("Case ID: "+tableLog[i][0]+" -- Event Name: "+tableLog[i][1]+" -- Duration: "+tableLog[i][2]+" -- Resource: "+tableLog[i][3]);
		}
		
		System.out.println("JumlahCase: "+jumlahCase);
		
		checkTime(fraud1);
		fraudData.insert(frauds);
		return fraudData;
	}
	
	
	//fungsi membaca event log
	public void readLog(XLog log)
	{
		XConceptExtension conceptE = XConceptExtension.instance();
		long durasi;
		int totalEvent=0;
		for(XTrace trace : log)
		{
			for(XEvent event : trace)
			{
				totalEvent++;
			}
		}
		tableLog = new Object[totalEvent/2+1][columname2.length];
		
		int index=0;
		for(XTrace trace : log)
		{
			System.out.println("masuk 2!");
			jumlahCase++;
			String temp = null ;
			String name = null;
			Date time = null;
			for(XEvent event : trace)
			{
				String traceID = conceptE.extractName(trace);
				System.out.println("trace ID!: "+traceID);
				boolean flag=false;
				tableLog[index][0]=traceID;
				Collection<XAttribute> attributes = event.getAttributes().values();
				for(XAttribute attribute : attributes)
				{
					if(attribute.getKey().startsWith("concept:name"))
					{
						System.out.println(temp);
						temp = ((XAttributeLiteral)attribute).getValue();
						tableLog[index][1]=temp;
					}
					
					if(attribute.getKey().startsWith("time:timestamp"))
					{
						System.out.println("masuk 6!");
						if(time==null)
						{
							time = ((XAttributeTimestamp)attribute).getValue();
						}
						else
						{
							durasi = (((XAttributeTimestamp)attribute).getValue().getTime()-time.getTime())/60000;
							if(durasi<0)
							{
								durasi = durasi*(-1);
							}
							time=null;
							tableLog[index][2]=durasi;
							index++;
							System.out.println("Durasi: "+durasi);
							
						}
						//System.out.println("time: " +time);
					}
					
					if(attribute.getKey().startsWith("lifecycle:transition"))
					{
						if(((XAttributeLiteral)attribute).getValue()=="complete")
						{
							flag=true;
						}
					}
					if(attribute.getKey().startsWith("org:resource")&&flag==false)
					{
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableLog[index][3]="";
						}
						else
						{
							tableLog[index][3]=((XAttributeLiteral)attribute).getValue().toLowerCase();
						}
						
					}
				}
			}
		}
	}
	//fungsi mendapatkan sequence dan decision event
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
	}
	//fungsi membaca pnml extended
	public void readPnml(ReadPNML pnml)
	{
		
		for(int i=0;i<pnml.transitions.size();i++)
		{
			boolean flag=false;
			String [] str = pnml.transitions.get(i).getName().split(" ");
			if(i==0)
			{
				tableTransition[c][0] = str[0];
				tableTransition[c][1] = pnml.transitions.get(i).getRole().toLowerCase();
				tableTransition[c][2] = pnml.transitions.get(i).getResource().toLowerCase();
				tableTransition[c][3] = pnml.transitions.get(i).getTime();
				tableRole[c][0] = pnml.transitions.get(i).getResource().toLowerCase();
				tableRole[c][1] = pnml.transitions.get(i).getRole().toLowerCase();
				continue;
			}
			else
			{
				for(int j=0;j<tableTransition.length;j++)
				{
					if(str[0].equals(tableTransition[j][0]))
					{
						flag=true;
					}
				}
			}
			if(flag==false)
			{
				c++;
				tableTransition[c][0] = str[0];
				tableTransition[c][1] = pnml.transitions.get(i).getRole().toLowerCase();
				tableTransition[c][2] = pnml.transitions.get(i).getResource().toLowerCase();
				tableTransition[c][3] = pnml.transitions.get(i).getTime();
				tableRole[c][0] = pnml.transitions.get(i).getResource().toLowerCase();
				tableRole[c][1] = pnml.transitions.get(i).getRole().toLowerCase();
			}
		}
	}
	//fungsi conformance
	public void checkTime(InsertFraudData fraud1)
	{
		int counter=0;
		boolean flag=false;
		String temp="";
		int tmin=0;
		int tmax=0;
		String role="";
		int wresource=0;
		int dutyS=0;
		int dutyD=0;
		int wDutyS=0;
		int wDutyD=0;
		int wDutyC=0;
		String CaseID="";
		//System.out.println("Resource: "+tableRole[0][0]+" -- Role: "+tableRole[0][1]);
		System.out.println("Total event: "+tableLog.length);
		for(int j=0;j<tableLog.length-1;j++)
		{
			//fungsi mendapatkan role
			boolean cek = false;
			for(int i=0;i<c+1;i++)
			{
				if(tableLog[j][3].equals(tableRole[i][0]))
				{
					role=(String)tableRole[i][1];
					//System.out.println("Resource1: "+tableLog[j][3]+" -- Resource2: "+tableRole[i][0]+" -- Role: "+role);
					cek=true;
				}
				//System.out.println("Resource1: "+tableLog[j][3]+" -- Resource2: "+tableRole[i][0]+" -- Role: "+role);
			}
			if(cek==false)
			{
				role="Unidentified";
			}
			dutyD=0;
			dutyS=0;
			//fungsi menghitung duty
			String simpan="";
			boolean dec = checkDecision((String)tableLog[j][1]);
			//fungsi menghitung wrong duty
			for(int p=j;p<tableLog.length-1;p++)
			{
				
				if(simpan=="")
				{
					simpan=(String)tableLog[p][0];
				}
				System.out.println("case: "+simpan);
				if(tableLog[p][0].equals(simpan))
				{
					if(tableLog[j][3].equals(tableLog[p][3])&&!(tableLog[j][1].equals(tableLog[p][1])))
					{
						System.out.println("Resource1: "+tableLog[j][3]+" -- Resource2: "+tableLog[p][3]);
						boolean dec2 = checkDecision((String)tableLog[p][1]);
						if(dec2==true && dec==true)
						{
							wDutyD++;
						}
						else if((dec2==true && dec==false) || (dec2==false && dec==true))
						{
							wDutyC++;
						}
						else if(dec2==false && dec==false)
						{
							wDutyS++;
						}
					}
				}
				else
				{
					break;
				}
						
			}
			//System.out.println("D: "+dutyD+" -- S: "+dutyS);
			
			if(temp=="")
			{
				temp=(String) tableLog[j][0];
				
				
				for(int k=0;k<tableTransition.length;k++)
				{
					if(tableLog[j][1].equals(tableTransition[k][0]))
					{
						//fungsi menghitung tmin
						if((Long)tableLog[j][2]<((Integer)tableTransition[k][3]-2))
						{
							tmin++;
						}
						//fungsi menghitung tmax
						else if((Long)tableLog[j][2]>((Integer)tableTransition[k][3]+2))
						{
							tmax++;
						}
						//fungsi menghitung wresource
						//System.out.println("Role1: "+role+" -- Role2: "+tableTransition[k][1]);
						if(role.equals(tableTransition[k][1]))
						{
							continue;
						}
						else
						{
							wresource++;
						}
					}

				}
			}
			else 
			{
				if(temp.equals(tableLog[j][0]))
				{
					for(int k=0;k<tableTransition.length;k++)
					{
						if(tableLog[j][1].equals(tableTransition[k][0]))
						{
							//fungsi menghitung tmin
							if((Long)tableLog[j][2]<((Integer)tableTransition[k][3]-2))
							{
								tmin++;
							}
							//fungsi menghitung tmax
							else if((Long)tableLog[j][2]>((Integer)tableTransition[k][3]+2))
							{
								tmax++;
							}
							//fungsi menghitung wresource
							//System.out.println("Role1: "+role+" -- Role2: "+tableTransition[k][1]);
							if(role.equals(tableTransition[k][1]))
							{
								continue;
							}
							else
							{
								wresource++;
							}
						}
					}
					
					
				}
				else
				{
					//fungsi menggabungkan data fraud
					for(int z=0;z<fraud1.frauds.size();z++)
					{
						if(tableLog[j-1][0].equals(fraud1.frauds.get(z).getCase()))
						{
							Fraud = new fraud(fraud1.frauds.get(z).getCase(), fraud1.frauds.get(z).getSkipSeq(), fraud1.frauds.get(z).getSkipDec(), tmin, tmax, wresource, wDutyS, wDutyD, wDutyC, fraud1.frauds.get(z).getwPattern(), 0, 0);
						}
					}
					frauds.add(Fraud);
					System.out.println("Tmin: "+tmin+" -- Tmax: "+tmax+" -- WR: "+wresource+" -- WDutyS: "+wDutyS+" -- WDutyD: "+wDutyD+" -- WDutyC: "+wDutyC);
					temp="";
					tmin=0;
					tmax=0;
					wresource=0;
					wDutyC=0;
					wDutyD=0;
					wDutyS=0;
					counter++;
				}
			}
			CaseID = (String) tableLog[j][0];
		}
		//fungsi menggabungkan data fraud
		for(int z=0;z<fraud1.frauds.size();z++)
		{
			if(CaseID.equals(fraud1.frauds.get(z).getCase()))
			{
				Fraud = new fraud(fraud1.frauds.get(z).getCase(), fraud1.frauds.get(z).getSkipSeq(), fraud1.frauds.get(z).getSkipDec(), tmin, tmax, wresource, wDutyS, wDutyD, wDutyC, fraud1.frauds.get(z).getwPattern(), 0, 0);
			}
		}
		frauds.add(Fraud);
		System.out.println("Tmin: "+tmin+" -- Tmax: "+tmax+" -- WR: "+wresource+" -- WDutyS: "+wDutyS+" -- WDutyD: "+wDutyD+" -- WDutyC: "+wDutyC);
		temp="";
		tmin=0;
		tmax=0;
		wresource=0;
		wDutyC=0;
		wDutyD=0;
		wDutyS=0;
	}
	
	public boolean checkDecision(String Transition)
	{
		for(String str: DecTransitions) {
		    if(str.trim().contains(Transition))
		       return true;
		}
		return false;
	}
}
