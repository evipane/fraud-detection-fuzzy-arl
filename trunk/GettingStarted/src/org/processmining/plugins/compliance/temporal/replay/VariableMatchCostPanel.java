package org.processmining.plugins.compliance.temporal.replay;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.DataConformance.GUI.MatchingActivity;
import org.processmining.plugins.DataConformance.GUI.VariableMatchCostUI;
import org.processmining.plugins.DataConformance.framework.ReplayableActivity;
import org.processmining.plugins.DataConformance.framework.VariableMatchCost;
import org.processmining.plugins.DataConformance.framework.VariableMatchCosts;

import com.fluxicon.slickerbox.components.SlickerButton;

class VariableMatchCostListener<E> implements ActionListener
{
	static final String MOVEUP = "U";
	static final String MOVEDOWN = "D";
	private VariableMatchCostPanel<E> panel;
	static String ADDMATCH="A";
	static String REMOVEMATCH="R";
	
	public VariableMatchCostListener(VariableMatchCostPanel<E> activityMatchCostPanel) {
		panel=activityMatchCostPanel;
	}

	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand()==ADDMATCH)
			panel.variableCostTable.addRow();
		else if (e.getActionCommand()==REMOVEMATCH)
			panel.variableCostTable.removeRow();
		else if (e.getActionCommand()==MOVEUP)
			panel.variableCostTable.moveUp();
		else if (e.getActionCommand()==MOVEDOWN)
			panel.variableCostTable.moveDown();	
	}
	
}


public class VariableMatchCostPanel<E> extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JPanel eastPanel=new JPanel();
	private SlickerButton addButton=new SlickerButton("Add matching");
	private SlickerButton removeButton=new SlickerButton("Remove matching");
	private SlickerButton moveUpButton=new SlickerButton("Move matching up");
	private SlickerButton moveDownButton=new SlickerButton("Move matching down");
	VariableMatchingCostTable<E> variableCostTable=null;
	private VariableMatchCostListener<E> listener= new VariableMatchCostListener<E>(this);
	private Set<? extends ReplayableActivity> activitySet;
	private Set<String> variableSet;
	
	public VariableMatchCostPanel(Map<? extends ReplayableActivity, E> activityMapping, 
			List<MatchingActivity<? extends ReplayableActivity, XEventClass>> unGuardedTransitions, 
			Map<String, String> variableMapping) 
	{
		this.setLayout(new BorderLayout());
		variableCostTable=new VariableMatchingCostTable<E>(activityMapping, unGuardedTransitions, variableMapping);
		activitySet=activityMapping.keySet();
		variableSet=variableMapping.keySet();
		JScrollPane jsp=new JScrollPane(variableCostTable);
		jsp.setPreferredSize(new Dimension(122,187));
		eastPanel.add(addButton);
		addButton.addActionListener(listener);
		addButton.setActionCommand(VariableMatchCostListener.ADDMATCH);
		eastPanel.add(removeButton);
		removeButton.addActionListener(listener);
		removeButton.setActionCommand(VariableMatchCostListener.REMOVEMATCH);		
		eastPanel.add(moveUpButton);
		moveUpButton.addActionListener(listener);
		moveUpButton.setActionCommand(VariableMatchCostListener.MOVEUP);		
		eastPanel.add(moveDownButton);
		moveDownButton.addActionListener(listener);
		moveDownButton.setActionCommand(VariableMatchCostListener.MOVEDOWN);
		this.add(jsp,BorderLayout.CENTER);
		this.add(eastPanel,BorderLayout.SOUTH);
	}

	public VariableMatchCosts getCosts()
	{
		List<VariableMatchCostUI> values=variableCostTable.model.getValues();
		Vector<VariableMatchCost> retValue=new Vector<VariableMatchCost>(values.size());
		for(VariableMatchCostUI value : values)
		{
			VariableMatchCost elem=new VariableMatchCost();
			if (value.getActivity().getNode()!=null)
				elem.setActivity((value.getActivity().getNode()).getLabel());
			if (value.getVariable().getProcessAttribute()!=null)		
				elem.setVariable(value.getVariable().getProcessAttribute());
			elem.setCostFaultyValue(value.getCostFaultyValue());
			elem.setCostNotWriting(value.getCostNotWriting());
			retValue.add(elem);
		}
		return new VariableMatchCosts(retValue, activitySet, variableSet);
	}
	
}
