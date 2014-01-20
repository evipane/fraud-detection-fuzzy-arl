package org.processmining.plugins.compliance.temporal.visualization;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.PNMatchInstancesRepResultConnection;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.plugins.DataConformance.Alignment;
import org.processmining.plugins.DataConformance.ResultReplay;
import org.processmining.plugins.DataConformance.framework.ExecutionStep;
import org.processmining.plugins.DataConformance.visualization.DataAwareStepTypes;
import org.processmining.plugins.compliance.AbstractLogForCompliance_Connection;
import org.processmining.plugins.compliance.AbstractLogForCompliance_Plugin;
import org.processmining.plugins.compliance.logprepare.GetLogFromAlignment_Connection;
import org.processmining.plugins.compliance.logprepare.GetLogFromBestMatchingAlignments_Connection;
import org.processmining.plugins.compliance.logprepare.GetLogFromBestMatchingAlignments_Plugin;
import org.processmining.plugins.compliance.temporal.ComplianceDiagnostics;
import org.processmining.plugins.compliance.temporal.EnrichLog_Connection;
import org.processmining.plugins.compliance.temporal.replay.TemporalAlignmentConnection;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

@Plugin(name = "Get Compliance Diagnostics From Data-Aware Alignment",
	returnLabels = { "compliance diagnostics", "compliance statistics" },
	returnTypes = { ResultReplay.class, ComplianceDiagnostics.class },
	parameterLabels = {"data-aware alignment" }, 
	help = "Translates the data-aware alignment into compliance diagnostics.", userAccessible = true
	)
public class GetComplianceDiagnosticsFromDataAlignment_Plugin {
	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Get Compliance Diagnostics From Data-Aware Alignment", requiredParameterLabels = { 0 })
	public Object[] getLogFromAlignment(UIPluginContext context, ResultReplay result) {
		
		TemporalAlignmentConnection connection;
		try {
			connection = context.getConnectionManager().getFirstConnection(TemporalAlignmentConnection.class, context, result);
		} catch (ConnectionCannotBeObtained e1) {
			return cancel(context, "Could not find log used for data-aware compliance checking");
		}
		XLog enrichedLog = connection.getObjectWithRole(TemporalAlignmentConnection.LOG);
		
		EnrichLog_Connection connection2;
		try {
			connection2 = context.getConnectionManager().getFirstConnection(EnrichLog_Connection.class, context, enrichedLog);
		} catch (ConnectionCannotBeObtained e1) {
			return cancel(context, "Could not find log extracted from control-flow checking");
		}
		XLog extractedLog = connection2.getObjectWithRole(EnrichLog_Connection.ORIGINAL_LOG);
		
		XLog abstractedLog;
		
		try {
			GetLogFromAlignment_Connection connection3 = context.getConnectionManager().getFirstConnection(GetLogFromAlignment_Connection.class, context, extractedLog);
			PNRepResult cfAlignment = connection3.getObjectWithRole(GetLogFromAlignment_Connection.PNREPRESULT);
			
			PNRepResultAllRequiredParamConnection connection4;
			try {
				connection4 = context.getConnectionManager().getFirstConnection(PNRepResultAllRequiredParamConnection.class, context, cfAlignment);
			} catch (ConnectionCannotBeObtained e1) {
				return cancel(context, "Could not find abstracted log used for control-flow checking");
			}
			abstractedLog = connection4.getObjectWithRole(PNRepResultAllRequiredParamConnection.LOG);
		} catch (ConnectionCannotBeObtained e1) {
			
			try {
				GetLogFromBestMatchingAlignments_Connection connection3 = context.getConnectionManager().getFirstConnection(GetLogFromBestMatchingAlignments_Connection.class, context, extractedLog);
				PNMatchInstancesRepResult cfAlignment = connection3.getObjectWithRole(GetLogFromBestMatchingAlignments_Connection.PNREPRESULT);
				
				PNMatchInstancesRepResultConnection connection4;
				try {
					connection4 = context.getConnectionManager().getFirstConnection(PNMatchInstancesRepResultConnection.class, context, cfAlignment);
				} catch (ConnectionCannotBeObtained e2) {
					return cancel(context, "Could not find abstracted log used for control-flow checking");
				}
				abstractedLog = connection4.getObjectWithRole(PNMatchInstancesRepResultConnection.LOG);
			} catch (ConnectionCannotBeObtained e2) {
				return cancel(context, "Could not find alignment used for control-flow checking");				
			}
		}
		
		AbstractLogForCompliance_Connection connection5;
		try {
			connection5 = context.getConnectionManager().getFirstConnection(AbstractLogForCompliance_Connection.class, context, abstractedLog);
		} catch (ConnectionCannotBeObtained e1) {
			return cancel(context, "Could not find original log for data-aware compliance checking");
		}
		XLog originalLog = connection5.getObjectWithRole(AbstractLogForCompliance_Connection.ORIGINAL_LOG);
		
		Collection<Alignment> alignment = result.labelStepArray;
		
		XFactory f = XFactoryRegistry.instance().currentDefault();
		XLog compliant = f.createLog(originalLog.getAttributes());
		XConceptExtension.instance().assignName(compliant, XConceptExtension.instance().extractName(compliant)+" (compliant traces)");
		
		XLog cf_violation = f.createLog(originalLog.getAttributes());
		XConceptExtension.instance().assignName(cf_violation, XConceptExtension.instance().extractName(compliant)+" (control-flow violations)");
		
		XLog temp_violation = f.createLog(originalLog.getAttributes());
		XConceptExtension.instance().assignName(temp_violation, XConceptExtension.instance().extractName(compliant)+" (temporal violations)");
		
		Map<String, Alignment> bestAlignment = new HashMap<String, Alignment>();
		Map<String, Float> bestFitness = new HashMap<String, Float>();
		
		List<Alignment> toRemove = new LinkedList<Alignment>();
		
		for (Alignment a : alignment) {
			// get the log trace that was aligned here
			String traceName = a.getTraceName();
			String originalTraceName = GetLogFromBestMatchingAlignments_Plugin.traceVariant_getOriginalName(traceName);
			
			// for each trace in the original log (identified by trace name),
			// only keep the trace with the best data fitness and drop the rest
			if (!bestFitness.containsKey(originalTraceName)) {
				bestFitness.put(originalTraceName, a.getFitness());
				bestAlignment.put(originalTraceName, a);
			} else {
				if (bestFitness.get(originalTraceName) > a.getFitness()) {
					toRemove.add(a);
					System.out.println("remove "+traceName+" because "+originalTraceName);
					continue;
				} else {
					toRemove.add(bestAlignment.get(originalTraceName));
					bestFitness.put(originalTraceName, a.getFitness());
					bestAlignment.put(originalTraceName, a);
					System.out.println("remove "+originalTraceName+" because "+traceName);
				}
			}
		}
		alignment.removeAll(toRemove);
			
		ComplianceDiagnostics diagnostics = new ComplianceDiagnostics();
		
		for (Alignment a : alignment) {
			String traceName = a.getTraceName();
			XTrace enrichedTrace = getTrace(enrichedLog, traceName);
			
			String originalTraceName = GetLogFromBestMatchingAlignments_Plugin.traceVariant_getOriginalName(traceName);
			XTrace originalTrace = getTrace(originalLog, originalTraceName);
			
			if (enrichedTrace == null) {
				System.out.println("Error! No trace with name "+traceName+" in log.");
				continue;
			}
			
			List<? extends Object> moves = a.getStepLabels();
			List<DataAwareStepTypes> moveTypes = a.getStepTypes();
			
			boolean has_cf_violation = false;
			boolean has_temp_violation = false;

			int ot_eventNum = 0;
			for (int i=0;i<a.getLogTrace().size(); i++) {
				XEvent e = null;
				if (a.getLogTrace().get(i) != ExecutionStep.bottomStep) {
					e = enrichedTrace.get(ot_eventNum);
					ot_eventNum++;
				}
				
				if (e.getAttributes().containsKey(org.processmining.plugins.compliance.logprepare.GetLogFromControlFlowAlignment_Plugin.KEY_COMPLIANCE_MOVETYPE)) {
					XAttributeLiteral moveTypeLit = (XAttributeLiteral)e.getAttributes().get(org.processmining.plugins.compliance.logprepare.GetLogFromControlFlowAlignment_Plugin.KEY_COMPLIANCE_MOVETYPE);
					String moveType = moveTypeLit.getValue();
					
					if (StepTypes.valueOf(moveType) == StepTypes.MREAL) {
						a.getStepTypes().set(i, DataAwareStepTypes.MREAL);
					} else if (StepTypes.valueOf(moveType) == StepTypes.L) {
						a.getStepTypes().set(i, DataAwareStepTypes.L);
					} else if (StepTypes.valueOf(moveType) == StepTypes.MINVI) {
						a.getStepTypes().set(i, DataAwareStepTypes.MINVI);
					}
				}
				
				String move_activity = a.getLogTrace().get(i).getActivity();
				
				// set all non-compliance related moves to invisible
				if (   move_activity.equals(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION)
					|| move_activity.equals(AbstractLogForCompliance_Plugin.OMEGA_CLASS_NAME)
					|| move_activity.equals(AbstractLogForCompliance_Plugin.OMEGA_CLASS.getId()))
				{
					a.getStepTypes().set(i, DataAwareStepTypes.MINVI);
				}
				
				if (a.getStepTypes().get(i) == DataAwareStepTypes.LMNOGOOD) {
					has_temp_violation = true;
				}
				if (a.getStepTypes().get(i) == DataAwareStepTypes.L || a.getStepTypes().get(i) == DataAwareStepTypes.MREAL) {
					has_cf_violation = true;
				}
			}
			
			if (has_temp_violation) temp_violation.add(originalTrace);
			if (has_cf_violation) cf_violation.add(originalTrace);
			if (!has_temp_violation && !has_cf_violation) compliant.add(originalTrace);
			
			Set<String> involvedResources = new HashSet<String>();
			
			Map<String, Integer> executedEventsPerResource = new HashMap<String, Integer>();
			String mostImportantResource = null;
			int maxEventsOfResource = 0;
			
			String lastResource = null;
			int handOvers = -1;

			for (XEvent e : originalTrace) {
				String r = XOrganizationalExtension.instance().extractResource(e);
				involvedResources.add(r);
				
				if (!executedEventsPerResource.containsKey(r)) executedEventsPerResource.put(r, 0);
				executedEventsPerResource.put(r, executedEventsPerResource.get(r)+1);
				
				if (executedEventsPerResource.get(r) > maxEventsOfResource) {
					maxEventsOfResource = executedEventsPerResource.get(r);
					mostImportantResource = r;
				}
				
				if (lastResource != r) {
					lastResource = r;
					handOvers++;
				}
			}
			
			
			for (String r : involvedResources) {
				if (!diagnostics.resourceInCase.containsKey(r)) diagnostics.resourceInCase.put(r, 0);
				diagnostics.resourceInCase.put(r, diagnostics.resourceInCase.get(r)+1);
				
				if (has_temp_violation) {
					if (!diagnostics.resourceInViolatingCase.containsKey(r)) diagnostics.resourceInViolatingCase.put(r, 0);
					diagnostics.resourceInViolatingCase.put(r, diagnostics.resourceInViolatingCase.get(r)+1);
				}
			}

			if (!diagnostics.mostImportantResourceInCase.containsKey(mostImportantResource)) diagnostics.mostImportantResourceInCase.put(mostImportantResource, 0);
			diagnostics.mostImportantResourceInCase.put(mostImportantResource, diagnostics.mostImportantResourceInCase.get(mostImportantResource)+1);
			if (has_temp_violation) {
				if (!diagnostics.mostImportantResourceInViolatedCase.containsKey(mostImportantResource)) diagnostics.mostImportantResourceInViolatedCase.put(mostImportantResource, 0);
				diagnostics.mostImportantResourceInViolatedCase.put(mostImportantResource, diagnostics.mostImportantResourceInViolatedCase.get(mostImportantResource)+1);
			}
			
			if (!diagnostics.casesPerHandoversInCase.containsKey(handOvers)) diagnostics.casesPerHandoversInCase.put(handOvers, 0);
			diagnostics.casesPerHandoversInCase.put(handOvers, diagnostics.casesPerHandoversInCase.get(handOvers)+1);
			if (has_temp_violation) {
				if (!diagnostics.violationsPerHandoversInCase.containsKey(handOvers)) diagnostics.violationsPerHandoversInCase.put(handOvers, 0);
				diagnostics.violationsPerHandoversInCase.put(handOvers, diagnostics.violationsPerHandoversInCase.get(handOvers)+1);
			}
			
			if (!diagnostics.casesPerResourceNumInCase.containsKey(involvedResources.size())) diagnostics.casesPerResourceNumInCase.put(involvedResources.size(), 0);
			diagnostics.casesPerResourceNumInCase.put(involvedResources.size(), diagnostics.casesPerResourceNumInCase.get(involvedResources.size())+1);
			if (has_temp_violation) {
				if (!diagnostics.violationsPerResourceNumInCase.containsKey(involvedResources.size())) diagnostics.violationsPerResourceNumInCase.put(involvedResources.size(), 0);
				diagnostics.violationsPerResourceNumInCase.put(involvedResources.size(), diagnostics.violationsPerResourceNumInCase.get(involvedResources.size())+1);
			}
		}

		
		context.getProvidedObjectManager().createProvidedObject(
				"compliant traces of "+XConceptExtension.instance().extractName(originalLog), compliant, XLog.class, context);
		context.getGlobalContext().getResourceManager().getResourceForInstance(compliant).setFavorite(true);

		context.getProvidedObjectManager().createProvidedObject(
				"control-flow compliance violating traces of "+XConceptExtension.instance().extractName(originalLog), cf_violation, XLog.class, context);
		context.getGlobalContext().getResourceManager().getResourceForInstance(cf_violation).setFavorite(true);

		context.getProvidedObjectManager().createProvidedObject(
				"temporal compliance violating traces of "+XConceptExtension.instance().extractName(originalLog), temp_violation, XLog.class, context);
		context.getGlobalContext().getResourceManager().getResourceForInstance(temp_violation).setFavorite(true);

		return new Object[] { result, diagnostics };
	}
	
	public static final XTrace getTrace(XLog log, String name) {
		for (XTrace t : log) {
			if (XConceptExtension.instance().extractName(t).equals(name)) {
				return t;
			}
		}
		return null;
	}
	
	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[get compliance diagnostics]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
