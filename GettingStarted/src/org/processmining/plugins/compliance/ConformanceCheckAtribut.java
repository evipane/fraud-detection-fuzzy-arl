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
	public Object[][] tableTransition3;
	public Object[][] tableTransition4;
	public String[] columname = {"Transition","Role","Resource","Time"};
	public String[] columname4 = {"Transition","Role","Resource","Time","Sebelum","Status"};
	public String[] columname5 = {"Transition","Role","Resource","Time","Sesudah","Status"};
	public Object[][] tableLog;
	public String[] columname2 = {"Case ID","Event Name","Duration","Resource"};
	public String[] columname3 = {"Resource","Role"};
	public Object[][] tableRole;
	public List<String> DecTransitions = new ArrayList<String>();
	public List<String> SeqTransitions = new ArrayList<String>();
	public InsertFraudData fraudData = new InsertFraudData();
	public fraud Fraud = new fraud();
	public List<fraud>frauds = new ArrayList<fraud>();
	int c2=0;
	int c3=0;
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
		tableTransition3 = new Object[pnml.transitions.size()][columname4.length];
		tableTransition4 = new Object[pnml.transitions.size()][columname5.length];
		tableRole = new Object[pnml.transitions.size()][columname.length];
		readPnml(pnml);
		readPnml2(pnml);
		readPnml3(pnml);
		readSeqDec(pnml);
		for(int i=0;i<c+1;i++)
		{
			//System.out.println("Resource: "+tableRole[i][0]+" -- Role: "+tableRole[i][1]);
			//System.out.println("Transition: "+tableTransition[i][0]+" -- Role: "+tableTransition[i][1]+" -- Resource: "+tableTransition[i][2]+" -- Time: "+tableTransition[i][3]);
		}
		readLog(log);
		
		for(int i=0;i<tableLog.length-1;i++)
		{
			//System.out.println("Case ID: "+tableLog[i][0]+" -- Event Name: "+tableLog[i][1]+" -- Duration: "+tableLog[i][2]+" -- Resource: "+tableLog[i][3]);
		}
		
		//System.out.println("JumlahCase: "+jumlahCase);
		
		checkConformance(fraud1);
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
				//System.out.println("trace ID!: "+traceID);
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
		
		System.out.println("Seq: "+SeqTransitions.size()+" -- Dec: "+DecTransitions.size());
	}
	
	//fungsi membaca pnml untuk pattern sebelum
	public void readPnml2(ReadPNML pnml)
	{
		
		for(int i=0;i<pnml.transitions.size();i++)
		{
			boolean flag=false;
			String [] str = pnml.transitions.get(i).getName().split(" ");
			if(str[1].equals("Start"))
			{
				tableTransition3[c2][0] = str[0];
				tableTransition3[c2][1] = pnml.transitions.get(i).getRole();
				tableTransition3[c2][2] = pnml.transitions.get(i).getResource();
				tableTransition3[c2][3] = pnml.transitions.get(i).getTime(); 
				tableTransition3[c2][4] = pnml.transitions.get(i).getSebelum();
				tableTransition3[c2][5] =str[1];
				c2++;
			}
				
			if(i>0)
			{
				for(int j=0;j<tableTransition3.length;j++)
				{
						//System.out.println("str1: "+str[1]);
					if(str[0].equals(tableTransition3[j][0]) && str[1].equals(tableTransition3[c2][5]))
					{
						//System.out.println("trans: "+tableTransition3[j][0]+" -- str1: "+str[1]);
						tableTransition3[j][4] = tableTransition3[j][4]+"-"+pnml.transitions.get(i).getSebelum();
						
					}
					
				}
			}
			
					
		}
		
		for(int i=0;i<c2;i++)
		{
			//System.out.println("trans: "+tableTransition3[i][0]+" -- sebelum: "+tableTransition3[i][4]);
		}
	}
	
	//fungsi membaca pnml untuk pattern sesudah
	public void readPnml3(ReadPNML pnml)
	{
		
		for(int i=0;i<pnml.transitions.size();i++)
		{
			boolean flag=false;
			String [] str = pnml.transitions.get(i).getName().split(" ");
			if(str[1].equals("Complete"))
			{
				tableTransition4[c3][0] = str[0];
				tableTransition4[c3][1] = pnml.transitions.get(i).getRole();
				tableTransition4[c3][2] = pnml.transitions.get(i).getResource();
				tableTransition4[c3][3] = pnml.transitions.get(i).getTime(); 
				tableTransition4[c3][4] = pnml.transitions.get(i).getSesudah();
				tableTransition4[c3][5] =str[1];
				c3++;
			}
				
			if(i>0)
			{
				for(int j=0;j<tableTransition4.length;j++)
				{
						//System.out.println("str1: "+str[1]);
					if(str[0].equals(tableTransition4[j][0]) && str[1].equals(tableTransition4[c3][5]))
					{
						//System.out.println("trans: "+tableTransition4[j][0]+" -- str1: "+str[1]);
						tableTransition4[j][4] = tableTransition4[j][4]+"-"+pnml.transitions.get(i).getSebelum();
						
					}
					
				}
			}
			
					
		}
		
		for(int i=0;i<c3;i++)
		{
			//System.out.println("trans: "+tableTransition4[i][0]+" -- sesudah: "+tableTransition4[i][4]);
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
	public void checkConformance(InsertFraudData fraud1)
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
		int wPattern=0;
		String CaseID = "";
		String next = "";
		List<String> checkedEvents = new ArrayList<String>();
		//System.out.println("Resource: "+tableRole[0][0]+" -- Role: "+tableRole[0][1]);
		
		//System.out.println("Total event: "+tableLog.length);
		System.out.println("Seq: "+SeqTransitions.size()+" -- Dec: "+DecTransitions.size());
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
			
			////////////////////////////////////////fungsi menghitung duty
			
			String simpan="";
			//System.out.println(">>>>case: " + tableLog[j][0] + " " + tableLog[j][3] + " " + tableLog[j][1]);
			if(!checkedEvents.contains(tableLog[j][3]))
			{
				//System.out.println("Durung Onok " + tableLog[j][3]);
				//checkedEvents.add((String)tableLog[j][3]);
				for(int i = 0; i < checkedEvents.size(); i++)
				{
					//System.out.print(checkedEvents.get(i) + " ");
				}
				//System.out.println();
				//System.out.println(" ");
			//}
				boolean dec = checkDecision((String)tableLog[j][1]);
				int count = 0;
				//System.out.println("case: " + simpan + " " + tableLog[j][3] + " " + tableLog[j][1]);
				//fungsi menghitung wrong duty
				for(int p=j;p<tableLog.length-1;p++)
				{
					count++;
					//System.out.println("Case Saiki: " + next + " Next: " + simpan + " " + (String)tableLog[p][0]);
					if(simpan=="")
					{
						simpan=(String)tableLog[p][0];
						/*if(!next.equals(simpan) && !next.equals(""))
						{
							System.out.println("Ganti Case " + next + " dadi " + simpan);
							checkedEvents.clear();
						}*/
						//System.out.println("Ganti Event");
						//next = simpan;
					}
					
					//System.out.println("---case: " + simpan + " - " + tableLog[p][0] + " " + tableLog[p][3] + " " + tableLog[p][1]);
					
					if(tableLog[p][0].equals(simpan))
					{
						if(tableLog[j][3].equals(tableLog[p][3])&&!(tableLog[j][1].equals(tableLog[p][1])))
						{
							//System.out.println("Resource1: "+tableLog[j][3]+" -- Resource2: "+tableLog[p][3]);
							boolean dec2 = checkDecision((String)tableLog[p][1]);
							
							if(dec2==true && dec==true)
							{
								wDutyD++;
								//System.out.println("WdutyDec: " + wDutyD);
							}
							
							else if((dec2==true && dec==false) || (dec2==false && dec==true))
							{
								wDutyC++;
								//System.out.println("WdutyCom: " + wDutyC);
							}
							
							else if(dec2==false && dec==false)
							{
								wDutyS++;
								//System.out.println("WdutySeq: " + wDutyS);
							}
						}
					}
					else
					{
						break;
					}
				}
				checkedEvents.add((String)tableLog[j][3]);
				if(count == 2)
				{
					//System.out.println("Ganti Case");
					checkedEvents.clear();
				}
			}
			else System.out.println("Wes Onok");
			////////////////////////////////////////fungsi menghitung duty
			//System.out.println("D: "+dutyD+" -- S: "+dutyS);
			//System.out.println("temp: " + temp);
			if(temp=="")
			{
				temp=(String) tableLog[j][0];
				//System.out.println("Masuk temp: " + temp + " " + tableLog[j][3] + " " + tableLog[j][1]);
				
				for(int k=0;k<tableTransition.length;k++)
				{
					if(tableLog[j][1].equals(tableTransition[k][0]))
					{
						//fungsi menghitung tmin
						//System.out.println("T1: "+(Long)tableLog[j][2]+" -- T2: "+(Integer)tableTransition[k][3]);
						if((Long)tableLog[j][2]<((Integer)tableTransition[k][3]-2))
						{
							tmin++;
							//System.out.println("tmin: " + tmin);
						}
						//fungsi menghitung tmax
						else if((Long)tableLog[j][2]>((Integer)tableTransition[k][3]+2))
						{
							tmax++;
							//System.out.println("tmax: " + tmax);
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
							//System.out.println("WResource: " + role + " " + tableTransition[k][0] + " " + wresource);
						}
					}

				}
			}
			else 
			{
				//System.out.println("Masuk else temp: " + temp + " " + tableLog[j][3] + " " + tableLog[j][1] + " " + tableLog[j][0]);
				//if(temp.equals(tableLog[j+1][0]))
				//{
					if(j<tableLog.length-2)
					{
						boolean cekPattern=false;
						boolean cekPattern2=false;
						boolean cekPattern3=false;
						String[] belum = new String[5];
						String[] sudah = new String[5];
						int ss=0;
						int sb=0;
						//hitung wrong pattern
						for(int x=0;x<c2;x++)
						{
							if(tableLog[j][1].equals(tableTransition3[x][0]))
							{
								belum[sb]=(String) tableTransition3[x][4];
								if(tableLog[j-1][1].equals(tableTransition3[x][4]))
								{
									cekPattern=true;
									
									break;
								}
								
							}
						}
						
						for(int x=0;x<c3;x++)
						{
							if(tableLog[j][1].equals(tableTransition4[x][0]))
							{
								belum[ss]=(String) tableTransition4[x][4];
								if(tableLog[j+1][1].equals(tableTransition4[x][4]))
								{
									cekPattern2=true;
									break;
								}
								//System.out.println("Log: "+tableLog[j][1]+" -- Pola2: "+cekPattern2);
							}
						}
						
						//System.out.println("Log: "+tableLog[j][1]+" -- Pola1: "+cekPattern+" -- Pola2: "+cekPattern2);
						if(cekPattern==false)
						{
							wPattern++;
						}
					}
					
					
					for(int k=0;k<tableTransition.length;k++)
					{
						//System.out.println("Log1: "+tableLog[j][1]+" -- Trans: "+tableTransition[k][0]);
						if(tableLog[j][1].equals(tableTransition[k][0]))
						{
							//fungsi menghitung tmin
							//System.out.println("T1: "+(Long)tableLog[j][2]+" -- T2: "+(Integer)tableTransition[k][3]);
							if((Long)tableLog[j][2]<((Integer)tableTransition[k][3]-2))
							{
								tmin++;
								//System.out.println("tmin: " + tmin);
							}
							//fungsi menghitung tmax
							else if((Long)tableLog[j][2]>((Integer)tableTransition[k][3]+2))
							{
								tmax++;
								//System.out.println("tmax: " + tmax);
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
								//System.out.println("WResource: " + role + " " + tableTransition[k][1] + " " + wresource);
							}
						}
					}
				//}
				if(!temp.equals(tableLog[j+1][0]))
				{
					//fungsi menggabungkan data fraud
					for(int z=0;z<fraud1.frauds.size();z++)
					{
						if(tableLog[j-1][0].equals(fraud1.frauds.get(z).getCase()))
						{
							Fraud = new fraud(fraud1.frauds.get(z).getCase(), fraud1.frauds.get(z).getSkipSeq(), fraud1.frauds.get(z).getSkipDec(), tmin, tmax, wresource, wDutyS, wDutyD, wDutyC, wPattern, 0, 0);
							//Fraud = new fraud(fraud1.frauds.get(z).getCase(), fraud1.frauds.get(z).getSkipSeq(), fraud1.frauds.get(z).getSkipDec(), tmin, tmax, wresource, wDutyS, wDutyD, wDutyC, fraud1.frauds.get(z).getwPattern(), 0, 0);
						}
					}
					frauds.add(Fraud);
					//System.out.println("//Tmin: "+tmin+" -- Tmax: "+tmax+" -- WR: "+wresource+" -- WDutyS: "+wDutyS+" -- WDutyD: "+wDutyD+" -- WDutyC: "+wDutyC);
					temp="";
					tmin=0;
					tmax=0;
					wresource=0;
					wDutyC=0;
					wDutyD=0;
					wDutyS=0;
					counter++;
					wPattern=0;
					checkedEvents.clear();
				}
			}
			CaseID = (String) tableLog[j][0];
		}
		//fungsi menggabungkan data fraud
		/*for(int z=0;z<fraud1.frauds.size();z++)
		{
			if(CaseID.equals(fraud1.frauds.get(z).getCase()))
			{
				Fraud = new fraud(fraud1.frauds.get(z).getCase(), fraud1.frauds.get(z).getSkipSeq(), fraud1.frauds.get(z).getSkipDec(), tmin, tmax, wresource, wDutyS, wDutyD, wDutyC, wPattern, 0, 0);
				//Fraud = new fraud(fraud1.frauds.get(z).getCase(), fraud1.frauds.get(z).getSkipSeq(), fraud1.frauds.get(z).getSkipDec(), tmin, tmax, wresource, wDutyS, wDutyD, wDutyC, fraud1.frauds.get(z).getwPattern(), 0, 0);
			}
		}*/
		//frauds.add(Fraud);
		//System.out.println("Tmin: "+tmin+" -- Tmax: "+tmax+" -- WR: "+wresource+" -- WDutyS: "+wDutyS+" -- WDutyD: "+wDutyD+" -- WDutyC: "+wDutyC);
		temp="";
		tmin=0;
		tmax=0;
		wresource=0;
		wDutyC=0;
		wDutyD=0;
		wDutyS=0;
		wPattern=0;
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
