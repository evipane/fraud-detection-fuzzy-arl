/*
 * Copyright (c) 2007 Christian W. Guenther (christian@deckfour.org)
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * License to link and use is also granted to open source programs which
 * are not licensed under the terms of the GPL, given that they satisfy one
 * or more of the following conditions:
 * 1) Explicit license is granted to the ProM and ProMimport programs for
 *    usage, linking, and derivative work.
 * 2) Carte blance license is granted to all programs developed at
 *    Eindhoven Technical University, The Netherlands, or under the
 *    umbrella of STW Technology Foundation, The Netherlands.
 * For further exemptions not covered by the above conditions, please
 * contact the author of this code.
 * 
 */
package org.processmining.mining.organizationmining.algorithm;

import javax.swing.JComponent;

import org.processmining.framework.log.LogReader;
import org.processmining.framework.models.orgmodel.OrgModel;
import org.processmining.mining.MiningResult;
import org.processmining.mining.organizationmining.model.ClusterSet;

/**
 * Base class for log / trace clustering algorithms in the trace clustering
 * framework.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public abstract class ClusteringAlgorithm implements MiningResult {

	/**
	 * Name of the clustering algorithm
	 */
	protected String name;
	/**
	 * Description (1-2 sentences) of the clustering algorithm
	 */
	protected String description;
	/**
	 * Input for the clustering algorithm. The input is set by the framework as
	 * soon as it becomes available, before the algorithm gains visibility.
	 */
	protected ClusteringInput input;

	/**
	 * Constructor for the clustering algorithm
	 * 
	 * @param aName
	 *            Name of the algorithm
	 * @param aDescription
	 *            Short description
	 */
	protected ClusteringAlgorithm(String aName, String aDescription) {
		this.name = aName;
		this.description = aDescription;
	}

	/**
	 * Called by the framework to retrieve the user interface component of the
	 * clustering plugin. The user interface should display the results of
	 * clustering, as well as provide the necessary controls to influence the
	 * way traces are clustered (if applicable).
	 * <p>
	 * Clustering should be triggered from this user interface either explicitly
	 * (i.e., dedicated button) or implicitly (i.e., when control values are
	 * modified).
	 * <p>
	 * At the time the user interface is requested by the framework through
	 * calling this method, the algorithm will have the clustering input set by
	 * the framework, so that clustering may start immediately. A result cluster
	 * set will only be requested by the framework after calling this method.
	 * Thus, it is also a good place to perform initialization of your
	 * algorithm.
	 * 
	 * @return The user interface of the clustering plugin
	 */
	public abstract JComponent getUI();

	public JComponent getVisualization() {
		return getUI();
	}

	public LogReader getLogReader() {
		return null;
	}

	/**
	 * The framework calls this method to retrieve the result of clustering,
	 * i.e. a set of clusters, from the plugin. This is used for making these
	 * results available to the rest of the framework.
	 * 
	 * @return the set of clusters generated by this algorithm
	 */
	public abstract ClusterSet getClusters();

	/**
	 * The framework calls this method to provide the clustering algorithm with
	 * the input (log, profile, distance metric) that may be used to generate
	 * clusters of the provided log.
	 * 
	 * @param anInput
	 *            clustering input, providing references to the log, profiles,
	 *            and distance metric.
	 */
	public void setInput(ClusteringInput anInput) {
		input = anInput;
	}

	/**
	 * Returns the name of the clustering algorithm
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a description (1-2 sentences) about the algorithm
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	public abstract OrgModel getOrgModel();

	/**
	 * Returns the name of this algorithm
	 * 
	 * @return
	 */
	public String toString() {
		return name;
	}

}
