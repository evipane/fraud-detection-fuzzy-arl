package org.processmining.pnml.model;

import java.util.ArrayList;
import java.util.List;

public class PNML {

	private List<Place> places;
	private List<Arc> arces;
	private List<Transition> transitions;
	
	private String startPlace;
	
	private List<Transition> seqTransition;

	public PNML(List<Place> places, List<Arc> arces, List<Transition> transitions) {
		super();
		this.places = places;
		this.arces = arces;
		this.transitions = transitions;
		
		seqTransition = new ArrayList<Transition>();
	}
	
	public void setStartPlace()
	{
		List<Place> start = places;
		
		for(int i=0;i<arces.size();i++)
		{
			Arc edge = arces.get(i);
			
			if(edge.getSource().contains("trans"))
			{
				for(int j=0;j<places.size();j++)
				{
					if(places.get(j).getId().contains(edge.getDestination()))
					{
						start.remove(j);
						break;
					}
				}
			}
		}
		
		for(int i=0;i<start.size();i++)
		{
			startPlace = start.get(i).getId();
			
			System.out.println("start place: "+startPlace);
		}
	}
	
	public List<Transition> orderTrans()
	{
		String from;
		String to;
		
		for(int i=0;i<arces.size();i++)
		{
			for(int j=0;j<arces.size();j++)
			{
				Arc edge = arces.get(j);
				from = edge.getSource();
				
				if(from.matches(startPlace))
				{
					to = edge.getDestination();
					
					startPlace = to;
					
					if(to.contains("trans"))
					{
						Transition transition = new Transition();
						transition.setId(to);
						seqTransition.add(transition);
						transition = searchTransition(to);
						break;
					}
				}
			}
		}
		
		return seqTransition;
	}
	
	public List<Transition> getTransition() {
		return transitions;
	}
	
	private Transition searchTransition(String id) {
		for(int i=0;i<transitions.size();i++) {
			Transition transition = transitions.get(i);
			if(transition.getId().equals(id)) {
				return transition;
			}
		}
		return null;
	}
	
}
