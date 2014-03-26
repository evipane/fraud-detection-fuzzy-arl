package org.processmining.pnml.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.processmining.pnml.model.Arc;
import org.processmining.pnml.model.Decision;
import org.processmining.pnml.model.PNML;
import org.processmining.pnml.model.Place;
import org.processmining.pnml.model.Transition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadPNML {
	
	private File fXmlFile;
	private DocumentBuilderFactory dbFactory;
	private Document doc;
	private DocumentBuilder dBuilder;
	
	private List<Transition> OrderedTransitions;
	
	private List<Place> places;
	public List<Transition> transitions;
	private List<Arc> arcs;
	public List<Decision> decisions;
	
	private PNML pnml;
	
	public ReadPNML(String file)
	{
		System.out.println("Masuk read constructor");
		try{
			fXmlFile = new File(file);
			
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			
			doc = dBuilder.parse(fXmlFile);
			
			places = new ArrayList<Place>();
			transitions = new ArrayList<Transition>();
			arcs = new ArrayList<Arc>();
			decisions = new ArrayList<Decision>();
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void readPNML()
	{
		System.out.println("Masuk fungsi read");
		try
		{	
			doc.getDocumentElement().normalize();
			 
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		 
			getPlaces(doc.getElementsByTagName("place"));
			getTransitions(doc.getElementsByTagName("transition"));
			getArcs(doc.getElementsByTagName("arc"));
			getDecisions(doc.getElementsByTagName("rule"));
			
			//System.out.println(doc.getElementsByTagName("transition").getLength());
			
			pnml = new PNML(places, arcs, transitions);
			pnml.setStartPlace();
			
			OrderedTransitions = pnml.orderTrans();
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public PNML getPNML() {
		return pnml;
	}
	
	private void getTransitions(NodeList nList)
	{
		System.out.println("masuk get trans");
		try
		{
			for (int temp = 0; temp < nList.getLength(); temp++) {
				 
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					System.out.println("masuk if");
					Transition transition = new Transition();
					transition.setName(eElement.getElementsByTagName("text").item(0).getTextContent());
					transition.setId(eElement.getAttribute("id"));
					transition.setRole(eElement.getElementsByTagName("transitionResource").item(0).getAttributes().getNamedItem("organizationalUnitName").getNodeValue());
					transition.setResource(eElement.getElementsByTagName("transitionResource").item(0).getAttributes().getNamedItem("roleName").getNodeValue());
					transition.setTime(Integer.parseInt(eElement.getElementsByTagName("time").item(0).getTextContent()));
					//transition.setCoba(eElement.getElementsByTagName("times").item(0).getTextContent());
					//transition.setDecision(eElement.getElementsByTagName("nextElementRef").item(0).getAttributes().getNamedItem("id").getNodeValue(), eElement.getElementsByTagName("nextElementRef").item(0).getAttributes().getNamedItem("atr").getNodeValue(), eElement.getElementsByTagName("nextElementRef").item(0).getTextContent());
					transitions.add(transition);
					//System.out.println("Transition location: " + transition.getLink());
				}
				
			}

			//System.out.println(transitions.size());
		}
		catch (Exception ex)
		{
			ex.getStackTrace();
		}
	}
	
	
	private void getPlaces(NodeList nList)
	{
		try
		{
			for (int temp = 0; temp < nList.getLength(); temp++) {
				 
				Node nNode = nList.item(temp);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
		 
					Place place = new Place();
					place.setName(eElement.getElementsByTagName("text").item(0).getTextContent());
					place.setId(eElement.getAttribute("id"));
					
					places.add(place);
				}
			}
			
			//System.out.println(places.size());
		}
		catch (Exception ex)
		{
			ex.getStackTrace();
		}
	}
	
	private void getArcs(NodeList nList)
	{
		try
		{
			for (int temp = 0; temp < nList.getLength(); temp++) {
				 
				Node nNode = nList.item(temp);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
					
					Arc arc = new Arc();
					arc.setId(eElement.getAttribute("id"));
					arc.setSource(eElement.getAttribute("source"));
					arc.setTarget(eElement.getAttribute("target"));
		 
					arcs.add(arc);
					
					/*System.out.println("Arc id : " + arc.getId()
							+ " source : " + arc.getSource()
							+ " target : " + arc.getTarget());*/
				}
			}

			//System.out.println(arcs.size());
		}
		catch (Exception ex)
		{
			ex.getStackTrace();
		}
	}
	
	private void getDecisions(NodeList nList)
	{
		System.out.println("masuk get Decs");
		try
		{
			for (int temp = 0; temp < nList.getLength(); temp++) {
				 
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					System.out.println("masuk if Decs");
					Decision decision = new Decision();
					decision.setFirstTransition(eElement.getElementsByTagName("firstActivity").item(0).getTextContent());
					decision.setNextTransition(eElement.getElementsByTagName("nextActivity").item(0).getTextContent());
					decision.setAttribute(eElement.getElementsByTagName("attribute").item(0).getTextContent());
					decision.setPredicate(eElement.getElementsByTagName("predicate").item(0).getTextContent());
					decision.setTypeAttribyte(eElement.getElementsByTagName("type").item(0).getTextContent());
					decision.setValue(eElement.getElementsByTagName("value").item(0).getTextContent());
					decisions.add(decision);
				}
			}
		}
		catch (Exception ex)
		{
			ex.getStackTrace();
		}
	}
	
	private Transition getTransition(Transition trans) {
		
		for(int j=0; j<transitions.size(); j++)
		{
			String id = trans.getId();
			Transition transition = transitions.get(j);
			
			if(id.matches(transition.getId())) {
				return transition;
			}
		}
		
		return null;
	}
}