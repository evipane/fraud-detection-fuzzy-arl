package org.processmining.analysis.epcmerge;

/***********************************************************
 *      This software is part of the ProM package          *
 *             http://www.processmining.org/               *
 *                                                         *
 *            Copyright (c) 2003-2008 TU/e Eindhoven       *
 *                and is licensed under the                *
 *            Common Public License, Version 1.0           *
 *        by Eindhoven University of Technology            *
 *           Department of Information Systems             *
 *                 http://is.tm.tue.nl                     *
 *                                                         *
 **********************************************************/

import org.processmining.analysis.Analyzer;
import org.processmining.framework.models.epcpack.ConfigurableEPC;

/**
 * <p>
 * Title: EPC Merge
 * </p>
 * <p>
 * Description: Plugin that merges two EPCs into one EPC representing at least
 * the behaviour of both the EPCs
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author fgottschalk
 * @version 1.0
 */
public class EpcMerge {
	@Analyzer(name = "EPC Merge", names = { "EPC 1", "EPC 2" }, connected = false, help = "http://www.floriangottschalk.de/255.html")
	public static EPCMergeSettings analyze(ConfigurableEPC net,
			ConfigurableEPC net2) {
		return new EPCMergeSettings(net2, net, true);
	}

	/**
	 * Provides user documentation for the plugin.
	 * 
	 * @return a URL that will be opened in the default browser of the user
	 */
	public String getHtmlDescription() {
		return "http://www.floriangottschalk.de/255.html";
	}

}
