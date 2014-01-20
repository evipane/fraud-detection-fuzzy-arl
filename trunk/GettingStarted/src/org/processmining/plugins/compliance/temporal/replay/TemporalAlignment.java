package org.processmining.plugins.compliance.temporal.replay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinetwithdata.newImpl.DataElement;
import org.processmining.models.graphbased.directed.petrinetwithdata.newImpl.PNWDTransition;
import org.processmining.models.graphbased.directed.petrinetwithdata.newImpl.PetriNetWithData;
import org.processmining.models.guards.Expression;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.DataConformance.ResultReplay;
import org.processmining.plugins.DataConformance.DataAlignment.ControlFlowAlignment;
import org.processmining.plugins.DataConformance.DataAlignment.ControlFlowAlignmentStep;
import org.processmining.plugins.DataConformance.DataAlignment.DataAlignerBuilder;
import org.processmining.plugins.DataConformance.DataAlignment.DataAlignerListener;
import org.processmining.plugins.DataConformance.DataAlignment.DataAlignmentState;
import org.processmining.plugins.DataConformance.DataAlignment.PetriNet.ControlFlowAlignmentConnection;
import org.processmining.plugins.DataConformance.DataAlignment.PetriNet.ResultReplayPetriNetWithData;
import org.processmining.plugins.DataConformance.GUI.MappingPanel;
import org.processmining.plugins.DataConformance.GUI.MatchingActivity;
import org.processmining.plugins.DataConformance.framework.ReplayableActivity;
import org.processmining.plugins.DataConformance.framework.VariableMatchCosts;
import org.processmining.plugins.compliance.align.PNLogReplayer;
import org.processmining.plugins.compliance.align.PNLogReplayer.ReplayParams;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

class ReplayableTransition implements ReplayableActivity
{
	private Transition transition;

	public String getLabel() {
		return transition.getLabel();
	}
	
	public ReplayableTransition(Transition t)
	{
		this.transition=t;
	}
	
	public Transition getTransition() {
		return transition;
	}
	
	public String toString()
	{
		return getLabel();
	}
}

public class TemporalAlignment implements DataAlignerListener {
	private Progress progBar;
	private Map<Transition, XEventClass> activityMapping;
	private Map<String, String> variableMapping;
	private UIPluginContext context;

	@Plugin(name = "Check Temporal Conformance of Log to Data-Aware Petri net",
			parameterLabels = { "Petri Net With Data", "Log"},
			returnLabels = { "Petri Net with Data"},
			returnTypes = { ResultReplay.class },
			userAccessible = true,
			help = "Petri Net Data Alignment")
			@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "M. de Leoni, D. Fahland", email = "m.d.leoni@tue.nl, d.fahland@tue.nl")
	public ResultReplay plugin(UIPluginContext context, PetriNetWithData net, XLog log) 
	{
		XLogInfo summary = XLogInfoFactory.createLogInfo(log);

		EvClassLogPetrinetConnection conn;


		ControlFlowAlignmentConnection connection;
		PNRepResult input = null;
		try {
			connection = context.getConnectionManager().getFirstConnection(ControlFlowAlignmentConnection.class, context, net, log);
			input=connection.getObjectWithRole(ControlFlowAlignmentConnection.PNREPRESULT);
		} catch (ConnectionCannotBeObtained e1) {
			try {
				Marking m_initial;
				try {
					m_initial = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class, InitialMarkingConnection.MARKING, net);
				} catch (ConnectionCannotBeObtained e) {
					return cancel(context, "Found no initial marking for temporal compliance pattern.");
				}
				Marking m_final;
				try {
					m_final = context.tryToFindOrConstructFirstObject(Marking.class, FinalMarkingConnection.class, FinalMarkingConnection.MARKING, net);
				} catch (ConnectionCannotBeObtained e) {
					return cancel(context, "Found no final marking for temporal compliance pattern.");
				}
				
				// check connection in order to determine whether mapping step is needed
				// of not
				TransEvClassMapping mapping;
				try {
					// connection is found, no need for mapping step
					// connection is not found, another plugin to create such connection
					// is automatically
					// executed
					mapping = context.tryToFindOrConstructFirstObject(TransEvClassMapping.class, EvClassLogPetrinetConnection.class, EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING, net, log);
				} catch (Exception e) {
					return cancel(context, "There was no mapping from transitions in the compliance pattern to events in the log.");
				}
				
				ReplayParams par = PNLogReplayer.getReplayerParameters(context, net, log, mapping.getEventClassifier(), m_initial, m_final);
				input = PNLogReplayer.callReplayer(context, net, log, mapping, par);
				
				context.getConnectionManager().addConnection(new ControlFlowAlignmentConnection("Control-Flow Alignment Connection",net,log,input));
			} catch (Exception e) {
				e.printStackTrace();
				cancel(context, "Could not get control-flow alignment for data-aware alignment.");
			}
		}


		try {	
			conn = context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net,
					log);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JPanel(), "No mapping can be constructed between the net and the log");
			context.getFutureResult(0).cancel(true);
			return null;
		}

		// init gui for each step
		TransEvClassMapping activityMapping = (TransEvClassMapping) conn.getObjectWithRole(EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING);
		
		
		HashMap<ReplayableTransition,XEventClass> activityMapping2=new HashMap<ReplayableTransition, XEventClass>();
		for(Entry<Transition, XEventClass> entry : activityMapping.entrySet())
			activityMapping2.put(new ReplayableTransition(entry.getKey()), entry.getValue());
	
		//Variable Mapping
		Collection<String> logAttributes=summary.getEventAttributeInfo().getAttributeKeys();
		TreeSet<String> processAttributes=new TreeSet<String>();
		for(DataElement variable : net.getVariables())
		{
			processAttributes.add(variable.getVarName());
		}

		MappingPanel<String, String> mapVariablePanel=new MappingPanel<String, String>(processAttributes,logAttributes);
		InteractionResult result = context.showConfiguration("Setup Variable Mapping", mapVariablePanel);
		if (result == InteractionResult.CANCEL) {
			context.getFutureResult(0).cancel(true);
			return null;
		}
		variableMapping=mapVariablePanel.getMapping(false);	

		ArrayList<MatchingActivity<? extends ReplayableActivity, XEventClass>> unGuardedTransitions = new ArrayList<MatchingActivity<? extends ReplayableActivity, XEventClass>>();
		for (Transition t : net.getTransitions()) {
			if (t instanceof PNWDTransition) {
				
				// check whether this is an unguarded transition that updates or reads variables
				if  (
						((PNWDTransition) t).getGuard() == null 
					  &&
						(    !((PNWDTransition)t).getReadOperations().isEmpty() 
						  || 
						     !((PNWDTransition)t).getWriteOperations().isEmpty()
						)
					)
				{
					// yes: then add to the list of unguarded transitions that get a special
					// cost-value for data violations
					for (ReplayableTransition rt : activityMapping2.keySet()) {
						if (rt.getTransition() == t) {
							MatchingActivity<? extends ReplayableActivity, XEventClass> ma = new MatchingActivity<ReplayableActivity, XEventClass>(rt, activityMapping2.get(rt));
							unGuardedTransitions.add(ma);
						}
					}
				}
			}
		}
		
		
		//Variable Costs
		VariableMatchCostPanel<XEventClass> variablePanel=new VariableMatchCostPanel<XEventClass>(activityMapping2, unGuardedTransitions, mapVariablePanel.getMapping(true));
		result = context.showConfiguration("Variable Mapping Cost", variablePanel);
		if (result == InteractionResult.CANCEL) {
			context.getFutureResult(0).cancel(true);
			return null;
		}
		VariableMatchCosts variableCost=variablePanel.getCosts();	

		//Build Objects to call the alignment builder
		Map<String, Class> varType=new HashMap<String, Class>();
		Map<String, Object> upperBounds=new HashMap<String, Object>();
		Map<String, Object> lowerBounds=new HashMap<String, Object>();
		
		for(DataElement elem : net.getVariables())
		{
			varType.put(elem.getVarName(),elem.getType());
			Object value=elem.getMinValue();
			if (value!=null)
				lowerBounds.put(elem.getVarName(), value);
			value=elem.getMaxValue();
			if (value!=null)
				upperBounds.put(elem.getVarName(), value);			
		}
		

		
		Collection<ControlFlowAlignment> listOfLists=new ArrayList<ControlFlowAlignment>();
		for(SyncReplayResult alignment : input)
			for(Integer index : alignment.getTraceIndex())
			{
				float controlFlowFitness=alignment.getInfo().get(PNRepResult.TRACEFITNESS).floatValue();
				listOfLists.add(buildControlFlowAlignment(alignment,log.get(index),controlFlowFitness,varType));
			}
		
		this.context = context; 
		progBar = context.getProgress();
		progBar.setMinimum(0);
		progBar.setMaximum(log.size());
		progBar.setValue(0);
		
		Collection<DataAlignmentState> alignments=
			DataAlignerBuilder.buildAlignments(listOfLists, variableCost, varType, upperBounds, lowerBounds, this);
		
		ResultReplay alignment = new ResultReplayPetriNetWithData(alignments, variableCost, net, log);
		context.getConnectionManager().addConnection(new TemporalAlignmentConnection("Temporal Compliance Alignment Connection",net,log,alignment));
		
		return alignment;

	}

	private ControlFlowAlignment buildControlFlowAlignment(SyncReplayResult alignment, XTrace xTrace, float controlFlowFitness, 
			Map<String, Class> varType) {
		Iterator<XEvent> eventIter=xTrace.iterator();
		Iterator<Object> transIter=alignment.getNodeInstance().iterator();;
		Transition transition = null;
		XEvent nextEvent = null;
		int stepType=-1;
		ControlFlowAlignment retValue=new ControlFlowAlignment(XConceptExtension.instance().extractName(xTrace),controlFlowFitness);
		for(StepTypes step : alignment.getStepTypes())
		{
			switch(step)
			{
				case LMGOOD:
				case LMNOGOOD:
					nextEvent=eventIter.next();
					transition=(Transition) transIter.next();
					stepType=ControlFlowAlignmentStep.MOVE_IN_BOTH;
					break;
				case L :
					nextEvent=eventIter.next();
					transition=null;
					transIter.next();
					stepType=ControlFlowAlignmentStep.MOVE_IN_LOG;
					break;
				case MINVI :
					stepType=ControlFlowAlignmentStep.MOVE_IN_MODEL_INVISIBLE;
					nextEvent=null;
					transition=(Transition) transIter.next();
					break;
				case MREAL :
					nextEvent=null;
					transition=(Transition) transIter.next();
					stepType=ControlFlowAlignmentStep.MOVE_IN_MODEL_VISIBLE;
					break;
				default :
					throw new IllegalArgumentException("Inexpected step type "+step+". Expected move in both, in log or (in)visible in process.");
			}
			String activityName;
			if (transition!=null)
				activityName=transition.getLabel();
			else
				activityName=XConceptExtension.instance().extractName(nextEvent);
			Expression expr;
			if (transition!=null)
				expr=((PNWDTransition)transition).getGuard();
			else
				expr=null;
			Map<String, Object> varAssignments=getVariableAssignment(nextEvent,varType);
			Set<String> variablesToWrite=getVariabletoWrite(transition);
			retValue.add(new ControlFlowAlignmentStep(activityName, expr, varAssignments, stepType, variablesToWrite));
		}
		return retValue;
	}

	private Set<String> getVariabletoWrite(Transition transition) {
		if (transition==null)
			return null;
		HashSet<String> varToWrite=new HashSet<String>();
		for(DataElement elem : ((PNWDTransition)transition).getWriteOperations())
			varToWrite.add(elem.getVarName());
		return varToWrite;
	}

	private Map<String, Object> getVariableAssignment(XEvent nextEvent, Map<String, Class> varType) {
		HashMap<String, Object> variableAssignment=new HashMap<String, Object>();
		if (nextEvent!=null)
			for(Entry<String, XAttribute> attrib : nextEvent.getAttributes().entrySet())
			{
				Object value=org.processmining.plugins.DataConformance.Utility.getValue(attrib.getValue());
				String attrName=null;
				for(Entry<String, String> mapping : variableMapping.entrySet())
				{
					if(mapping.getValue().equals(attrib.getKey()))
					{
						attrName=mapping.getKey();
						break;
					}
				}
				if (attrName!=null)
					variableAssignment.put(attrName, value);
			}
		return variableAssignment;
	}

	public void exceptionThrow(String traceName,Exception err) {
		synchronized(this)
		{
			//context.log("Error while aligning the trace "+traceName+": "+err.getMessage(),MessageLevel.ERROR);	
		}
		err.printStackTrace();
	}

	public void newAlignedTrace(DataAlignmentState da) {
		synchronized(this)
		{
			progBar.inc();
		}
	}

	public void startNewAlignment(String traceName) {

	}

	public void exceptionThrow(Exception err) {
		synchronized(this)
		{
			context.log(err.getMessage().toUpperCase(),MessageLevel.ERROR);
		}
	}

	protected static ResultReplay cancel(PluginContext context, String message) {
		System.out.println("[Temporal Alignment]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}

