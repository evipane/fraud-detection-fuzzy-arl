import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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


public class XEStoOWL {
	
	public String[] columnsName = {"Case ID","Event Name","Duration","Resource","complete","SID","type","verification","overrate","decision","validation","plafond"};
	public Object[][] tableTime;
	
	public DefaultTableModel tableModelTime ;
	
	@Plugin(
			name="Convert Event Log to Ontology",
			parameterLabels = {},
			returnLabels ={"Ontology File"},
			returnTypes = {JPanel.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Farid Naufal",
			author = "Farid Naufal",
			email = "naufalfarid99@gmail.com"
			)
			
	public JPanel FraudTabel(final UIPluginContext context, XLog log)
	{
		JPanel panel = new JPanel();
		XConceptExtension conceptE = XConceptExtension.instance();
		
		long durasi;
		System.out.println("masuk 1!");
		int totalEvent=0;
		
		String text = "<?xml version=\"1.0\"?>\n\n\n" + 
		"<!DOCTYPE rdf:RDF [\n" +
		"    <!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n" +
	    "    <!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n" +
	    "    <!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n" +
	    "    <!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n" +
	    "    <!ENTITY bc \"http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#\" >\n" +
	    "]>\n\n\n" + 
	    "<rdf:RDF xmlns=\"http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#\"\n" +
	    "    xml:base=\"http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128\"\n" +
	    "	 xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
	    "	 xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
	    "	 xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
	    "	 xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
	    "	 xmlns:bc=\"http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#\">\n" +
	    "	<owl:Ontology rdf:about=\"http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128\"/>\n\n\n" + 
	    "   <!--\n" +
	    "	///////////////////////////////////////////////////////////////////////////////////////\n" +
	    "	//\n" +
	    "	// Object Properties\n" +
	    "	//\n" +
	    "	///////////////////////////////////////////////////////////////////////////////////////\n" +
	    "	-->\n\n\n\n\n" + 
	    "	<!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#has_event -->\n\n" +
	    "	<owl:ObjectProperty rdf:about=\"&bc;has_event\"/>\n\n\n\n" + 
	    "   <!--\n" +
	    "	///////////////////////////////////////////////////////////////////////////////////////\n" +
	    "	//\n" +
	    "	// Data Properties\n" +
	    "	//\n" +
	    "	///////////////////////////////////////////////////////////////////////////////////////\n" +
	    "	-->\n\n\n\n\n";
        
		for(XAttribute attribute : log.getGlobalEventAttributes())
		{
			System.out.println(attribute.getKey());
			if(attribute.getKey().startsWith("concept:name"))
			{
				text = text + "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#" + "has_name -->\n\n" + 
					   "    <owl:DatatypeProperty rdf:about=\"&bc;" + "has_name" + "\"/>\n\n\n";
			}
			else if(attribute.getKey().startsWith("org:resource"))
			{
				text = text + "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#" + "done_by -->\n\n" + 
				   	   "    <owl:DatatypeProperty rdf:about=\"&bc;" + "done_by" + "\"/>\n\n\n";
			}
			else if(attribute.getKey().startsWith("time:timestamp"))
			{
				text = text + "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#" + "done_at -->\n\n" + 
				   	   "    <owl:DatatypeProperty rdf:about=\"&bc;" + "done_at" + "\"/>\n\n\n";
				text = text + "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#" + "has_duration -->\n\n" + 
			   	   	   "    <owl:DatatypeProperty rdf:about=\"&bc;" + "has_duration" + "\"/>\n\n\n";
			}
			else
			{
				text = text + "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#" + "has_" + attribute.getKey() + "-->\n\n" + 
			   	       "    <owl:DatatypeProperty rdf:about=\"&bc;" + "has_" + attribute.getKey() + "\"/>\n\n\n";
			}
		}
		
		text = text + "   <!--\n" +
	    		"	///////////////////////////////////////////////////////////////////////////////////////\n" +
	    		"	//\n" +
	    		"	// Classes\n" +
	    		"	//\n" +
	    		"	///////////////////////////////////////////////////////////////////////////////////////\n" +
	    		"	-->\n\n\n\n\n" + 
			   
	    	   "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#Case -->\n\n" + 
			   "    <owl:Class rdf:about=\"&bc;Case\">\n" + 
			   "        <rdfs:subClassOf rdf:resource=\"&bc;EventLog\"/>\n" +
			   "    </owl:Class>\n\n\n\n" + 
			   "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#Event -->\n\n" + 
			   "    <owl:Class rdf:about=\"&bc;Event\">\n" + 
			   "        <rdfs:subClassOf rdf:resource=\"&bc;Case\"/>\n" +
			   "    </owl:Class>\n\n\n\n" +
			   "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#EventLog -->\n\n" +
			   "    <owl:Class rdf:about=\"&bc;EventLog\"/>\n\n\n\n" + 
			   
			   "	<!--\n" +
			   "	///////////////////////////////////////////////////////////////////////////////////////\n" +
			   "	//\n" +
			   "	// Individuals\n" +
			   "	//\n" +
			   "	///////////////////////////////////////////////////////////////////////////////////////\n" +
			   "	-->\n\n\n\n\n";
		
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
			String traceID = conceptE.extractName(trace);
			System.out.println("trace ID!: "+traceID);
			
			text = text + "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#Case_" + traceID + " -->\n\n" + 
						  "    <owl:NamedIndividual rdf:about=\"&bc;Case_" + traceID + "\">\n"+
						  "	   <rdf:type rdf:resource=\"&bc;Case\"/>\n";
			
			/*for(XEvent event : trace)
			{
				boolean complete = false;
				Collection<XAttribute> attributes = event.getAttributes().values();
				for(XAttribute attribute : attributes)
				{
					System.out.println(complete);
					if(attribute.getKey().startsWith("lifecycle:transition"))
					{
						if(((XAttributeLiteral)attribute).getValue()=="complete")
						{
							complete = true;
						}
					}
					
					if(attribute.getKey().startsWith("concept:name") && complete == false)
					{
						text = text + "        <has_event rdf:resource=\"" + ((XAttributeLiteral)attribute).getValue() + "_" + traceID +"\"/>\n";
					}
				}
			}
			
			text = text + "    </owl:NamedIndividual>\n\n\n\n";*/
			String textEvent = "";
			
			for(XEvent event : trace)
			{
				boolean flag = false;
				tableTime[index][0] = traceID;
				Collection<XAttribute> attributes = event.getAttributes().values();
				for(XAttribute attribute : attributes)
				{
					if(attribute.getKey().startsWith("concept:name") && time == null)
					{
						temp = ((XAttributeLiteral)attribute).getValue();
						tableTime[index][1]=temp;
						System.out.println(temp);
						text = text + "        <has_event rdf:resource=\"&bc;" + temp + "_" + traceID +"\"/>\n";
						textEvent += "    <!-- http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#" + temp + "_" + traceID + " -->\n\n" +
									"    <owl:NamedIndividual rdf:about=\"&bc;" + temp + "_" + traceID + "\">\n" +
									"        <rdf:type rdf:resource=\"&bc;Event\"/>\n";
					}
					
					else if(attribute.getKey().startsWith("time:timestamp"))
					{
						//System.out.println("masuk 6!");
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
							textEvent += "        <done_at>" + ((XAttributeTimestamp)attribute).getValue().getTime() + "</done_at>\n" +
							 			 "        <has_duration>" + durasi + "</has_duration>\n";
						}
						
						//System.out.println("time: " +time);
					}
					
					else if(attribute.getKey().startsWith("lifecycle:transition"))
					{
						if(((XAttributeLiteral)attribute).getValue().equals("complete"))
						{
							flag = true;
						}
					}
					
					else if(attribute.getKey().startsWith("org:resource") && flag == false && time != null)
					{
						textEvent += "        <done_by rdf:datatype=\"&xsd;string\">" + ((XAttributeLiteral)attribute).getValue() + "</done_by>\n";
						
						if(((XAttributeLiteral)attribute).getValue()=="NOT_SET")
						{
							tableTime[index][3]="";
						}
						else
						{
							tableTime[index][3]=((XAttributeLiteral)attribute).getValue();
						}
						
					}
					
					else
					{
						if(flag == false && time != null && !attribute.getKey().startsWith("concept:name") && !attribute.getKey().startsWith("resource"))
						{
							if(!((XAttributeLiteral)attribute).getValue().equals("NOT_SET"))
							{
								System.out.println("clone: " + attribute.clone().toString());
								textEvent += "        <has_" + attribute.getKey() + " rdf:datatype=\"&xsd;string\">" + ((XAttributeLiteral)attribute).getValue() + "</has_" + attribute.getKey() + ">\n";
							}
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
				System.out.println(flag);
				if(flag == true){
					textEvent = textEvent + "    </owl:NamedIndividual>\n\n\n\n";
				}
			}
			text = text + "    </owl:NamedIndividual>\n\n\n\n";
			text += textEvent;
		}
		
		text = text + "</rdf:RDF>";
		
		try {
	          File file = new File("example.owl");
	          BufferedWriter output = new BufferedWriter(new FileWriter(file));
	          output.write(text);
	          output.close();
	        } catch ( IOException e ) {
	           e.printStackTrace();
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
