package org.processmining.framework.models.logabstraction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.processmining.analysis.log.scale.ProcessInstanceScale;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.ProcessInstance;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class PartialPrefixAbstractionFactory implements LogAbstractionFactory {
	public PartialPrefixAbstractionFactory() {
		super();
	}

	public List<LogAbstraction> getAbstractions(LogReader log,
			ProcessInstanceScale scale) {
		List<LogAbstraction> list = new ArrayList<LogAbstraction>(log
				.getLogSummary().getNumberOfProcessInstances());
		Iterator it = log.instanceIterator();
		while (it.hasNext()) {
			ProcessInstance pi = (ProcessInstance) it.next();
			list.add(new PartialPrefixAbstraction(log, pi, scale, pi
					.getAuditTrailEntryList().size()));
		}
		return list;
	}

	public List<LogAbstraction> getAbstractions(LogReader log,
			ProcessInstanceScale scale, int partialPrefixSize) {
		List<LogAbstraction> list = new ArrayList<LogAbstraction>(log
				.getLogSummary().getNumberOfProcessInstances());
		Iterator it = log.instanceIterator();
		while (it.hasNext()) {
			ProcessInstance pi = (ProcessInstance) it.next();
			list.add(new PartialPrefixAbstraction(log, pi, scale,
					partialPrefixSize));
		}
		return list;
	}

	public LogAbstraction getAbstraction(LogReader log, ProcessInstance pi,
			ProcessInstanceScale scale) {
		return new PartialPrefixAbstraction(log, pi, scale, pi
				.getAuditTrailEntryList().size());
	}

	public LogAbstraction getAbstraction(LogReader log, ProcessInstance pi,
			ProcessInstanceScale scale, int partialPrefixSize) {
		return new PartialPrefixAbstraction(log, pi, scale, partialPrefixSize);
	}

}
