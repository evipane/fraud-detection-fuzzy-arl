package org.processmining.plugins.compliance.temporal.replay;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.DataConformance.GUI.MatchingActivity;
import org.processmining.plugins.DataConformance.GUI.MatchingXAttribute;
import org.processmining.plugins.DataConformance.framework.ReplayableActivity;

public class VariableMatchingCostTable<E> extends JTable //ProMTable
{
	private static final long serialVersionUID = 1L;
	
	VariableMatchingTableModel model;
	
	@SuppressWarnings("unchecked")
	private Vector<MatchingActivity<? extends ReplayableActivity, E>> getActivityList(Map<? extends ReplayableActivity, E> activityMapping) {
		Vector<MatchingActivity<? extends ReplayableActivity, E>> activities=
			new Vector<MatchingActivity<? extends ReplayableActivity, E>>();
		for(Entry<? extends ReplayableActivity, E> entry : activityMapping.entrySet())
		{
			if (entry.getValue()!=null) {
				MatchingActivity<? extends ReplayableActivity, E> matchNode=
					new MatchingActivity<ReplayableActivity, E>(entry.getKey(),entry.getValue());
				activities.add(matchNode);
			}
		}
		activities.add(MatchingActivity.ALLFLEXNODES);
		return activities;
	}


	private Vector<MatchingXAttribute> getVariableList(Map<String, String> variableMapping)
			{
		Vector<MatchingXAttribute> variables=new Vector<MatchingXAttribute>();
		for(Entry<String, String> entry : variableMapping.entrySet())
			if (entry.getValue()!=null)
				variables.add(new MatchingXAttribute(entry.getValue(), entry.getKey()));
			else
				variables.add(new MatchingXAttribute("", entry.getKey()));
		variables.add(MatchingXAttribute.ALLATTRIBUTES);
		return variables;
			}

	public VariableMatchingCostTable(Map<? extends ReplayableActivity, E> activityMapping,
			List<MatchingActivity<? extends ReplayableActivity, XEventClass>> unGuardedTransitions,
			Map<String, String> variableMapping) {
		super(new VariableMatchingTableModel(unGuardedTransitions));
		model=(VariableMatchingTableModel) this.getModel();
		this.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox(getActivityList(activityMapping))));
		this.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(getVariableList(variableMapping))));
	}
	
	public void addRow()
	{
		if(editingRow>0)
			return;
		int selectedRow=this.getSelectedRow();
		if (selectedRow<0) selectedRow=0;
		model.addRow(selectedRow);
		model.fireTableDataChanged();
	}
	
	public void removeRow()
	{
		if(editingRow>0)
			return;
		int[] indexes=this.getSelectedRows();
		model.removeRow(indexes);
		model.fireTableDataChanged();
	}
	
	public void moveUp()
	{
		if(editingRow>0)
			return;
		int selectedRow=this.getSelectedRow();
		model.moveUp(selectedRow);
		model.fireTableDataChanged();
		if (selectedRow>0) 
			this.addRowSelectionInterval(selectedRow-1, selectedRow-1);
	}
	
	public void moveDown()
	{
		if(editingRow>0)
			return;
		int selectedRow=this.getSelectedRow();
		model.moveDown(selectedRow);
		model.fireTableDataChanged();
		if (selectedRow<model.getRowCount()-1)
			this.addRowSelectionInterval(selectedRow+1, selectedRow+1);		
	}
}
