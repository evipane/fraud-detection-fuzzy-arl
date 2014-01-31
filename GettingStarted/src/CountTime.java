import java.awt.Dimension;
import java.util.Collection;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

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
import org.processmining.framework.util.ui.widgets.ProMTable;


public class CountTime {
	
	public String[] columnsName = {"Case ID","Event Name","Duration","Resource","complete","SID","type","verification","overrate","decision","validation","plafond"};
	public Object[][] tableTime;
	
	public DefaultTableModel tableModelTime ;
	@Plugin(
			name="Calculate Duration Time Log",
			parameterLabels = {},
			returnLabels ={"Duration Time"},
			returnTypes = {JPanel.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Fernandes Sinaga",
			author = "Fernandes Sinaga",
			email = "nandes.02@gmail.com"
			)
	public JPanel FraudTabel(final UIPluginContext context, XLog log)
	{
		JPanel panel = new JPanel();
		XConceptExtension conceptE = XConceptExtension.instance();
		
		long durasi;
		System.out.println("masuk 1!");
		int totalEvent=0;
		for(XTrace trace : log)
		{
			for(XEvent event : trace)
			{
				totalEvent++;
			}
		}
		tableTime = new Object[totalEvent/2+1][columnsName.length];
		//System.out.println("total event!: "+totalEvent);
		Object[][] table = new Object[totalEvent/2+1][];
		tableModelTime = new DefaultTableModel(table,columnsName);
		int index=0;
		for(XTrace trace : log)
		{
			System.out.println("masuk 2!");
			
			
			
			String temp = null ;
			String name = null;
			Date time = null;
			for(XEvent event : trace)
			{
				String traceID = conceptE.extractName(trace);
				System.out.println("trace ID!: "+traceID);
				boolean flag=false;
				tableTime[index][0]=traceID;
				Collection<XAttribute> attributes = event.getAttributes().values();
				for(XAttribute attribute : attributes)
				{
					if(attribute.getKey().startsWith("concept:name"))
					{
						System.out.println(temp);
						temp = ((XAttributeLiteral)attribute).getValue();
						tableTime[index][1]=temp;
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
							tableTime[index][2]=durasi;
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
							tableTime[index][3]="";
						}
						else
						{
							tableTime[index][3]=((XAttributeLiteral)attribute).getValue();
						}
						
					}
					
					if(attribute.getKey().startsWith("complete")&&flag==false)
					{
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableTime[index][4]="";
						}
						else
						{
							tableTime[index][4]=((XAttributeLiteral)attribute).getValue();
						}
					}
					
					if(attribute.getKey().startsWith("SID")&&flag==false)
					{
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableTime[index][5]="";
						}
						else
						{
							tableTime[index][5]=((XAttributeLiteral)attribute).getValue();
						}
					}
					
					
					if(attribute.getKey().startsWith("type")&&flag==false)
					{
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableTime[index][6]="";
						}
						else
						{
							tableTime[index][6]=((XAttributeLiteral)attribute).getValue();
						}
					}
					
					if(attribute.getKey().startsWith("verification")&&flag==false)
					{
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableTime[index][7]="";
						}
						else
						{
							tableTime[index][7]=((XAttributeLiteral)attribute).getValue();
						}
					}
					
					if(attribute.getKey().startsWith("overrate")&&flag==false)
					{
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableTime[index][8]="";
						}
						else
						{
							tableTime[index][8]=((XAttributeLiteral)attribute).getValue();
						}
					}
					
					if(attribute.getKey().startsWith("decision")&&flag==false)
					{
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableTime[index][9]="";
						}
						else
						{
							tableTime[index][9]=((XAttributeLiteral)attribute).getValue();
						}
					}
					
					if(attribute.getKey().startsWith("validation")&&flag==false)
					{
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableTime[index][10]="";
						}
						else
						{
							tableTime[index][10]=((XAttributeLiteral)attribute).getValue();
						}
					}
					
					if(attribute.getKey().startsWith("plafond")&&flag==false)
					{
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableTime[index][11]="";
						}
						else
						{
							tableTime[index][11]=((XAttributeLiteral)attribute).getValue();
						}
					}
					
					//System.out.println("temp: "+temp+" -- time: "+time+" -- flag: "+flag+" -- name: "+name);
					
					
				}
			}
		}
		
		for(int i=0;i<tableTime.length;i++)
		{
			for(int j=0;j<tableModelTime.getColumnCount();j++)
			{
				tableModelTime.setValueAt(tableTime[i][j], i, j);
			}
		}
		ProMTable Ptabel = new ProMTable(tableModelTime);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel.add(Ptabel);
		context.showConfiguration("Tabel Durasi",panel);
		return panel;
	}
}
