package org.processmining.plugins.compliance.temporal.replay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.DataConformance.GUI.MatchingActivity;
import org.processmining.plugins.DataConformance.GUI.MatchingXAttribute;
import org.processmining.plugins.DataConformance.GUI.VariableMatchCostUI;
import org.processmining.plugins.DataConformance.framework.ReplayableActivity;

public class VariableMatchingTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	private Vector<VariableMatchCostUI> values=new Vector<VariableMatchCostUI>();
	
	public VariableMatchingTableModel(List<MatchingActivity<? extends ReplayableActivity, XEventClass>> unGuardedTransitions)
	{
		for (MatchingActivity<? extends ReplayableActivity, XEventClass> a : unGuardedTransitions) {
			System.out.println("setting pre-defined value for "+a);
			VariableMatchCostUI elem = new VariableMatchCostUI();
			elem.setActivity(a);
			elem.setCostFaultyValue(10);
			elem.setCostNotWriting(10);
			values.add(elem);
		}
		
		VariableMatchCostUI elem=new VariableMatchCostUI();
		elem.setCostFaultyValue(1);
		elem.setCostNotWriting(10);
		values.add(elem);
	}
	
	public int getColumnCount() {
		return 4;
	}

	public int getRowCount() {
		return values.size();
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return true;
	}
	
	public String getColumnName(int column)
	{
		switch(column)
		{
			case 0:
				return "Activity";
			case 1:
				return "Attribute";
			case 2:
				return "Non-writing Cost";
			case 3:
				return "Faulty-value Cost";
			default:
				return "";
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		VariableMatchCostUI value=values.get(rowIndex);
		switch(columnIndex)
		{
			case 0:
				return value.getActivity();
			case 1:
				return value.getVariable();
			case 2:	
				return value.getCostNotWriting();
			case 3:
				return value.getCostFaultyValue();
		}
		return "";
	}
	
	public void addRow(int index)
	{
		values.add(index, new VariableMatchCostUI());
	}
	
	public void moveUp(int index)
	{
		if (index>0)
		{
			VariableMatchCostUI elem=values.get(index);
			values.remove(index);
			values.add(index-1,elem);
		}
	}
	
	public void moveDown(int index)
	{
		if (index<values.size()-1)
		{
			VariableMatchCostUI elem=values.get(index);
			values.add(index+2,elem);
			values.remove(index);
		}
	}	
	 
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		VariableMatchCostUI value=values.get(rowIndex);
		switch(columnIndex)
		{
			case 0:
				value.setActivity((MatchingActivity)aValue);
				break;
			case 1:
				value.setVariable((MatchingXAttribute)aValue);
				break;
			case 2:
				value.setCostNotWriting(Float.parseFloat((String)aValue));
				break;
			case 3:
				value.setCostFaultyValue(Float.parseFloat((String)aValue));
		}		
	}

	public List<VariableMatchCostUI> getValues() 
	{
		return Collections.unmodifiableList(values);
	}

	public void removeRow(int[] indexes) 
	{
		Arrays.sort(indexes);
		for(int i=indexes.length-1;i>=0;i--)
		{
			if (indexes[i]>=0) values.remove(indexes[i]);
		}
	}
}
