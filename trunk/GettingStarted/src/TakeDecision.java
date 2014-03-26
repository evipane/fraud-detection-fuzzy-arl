import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.pnml.controller.ReadPNML;


public class TakeDecision {
	
	public String[] columname = {"First Acitivity","Next Activity","Attribute","Type","Predicate","Value"};
	public Object[][] tableTransition;
	
	public DefaultTableModel tableModelTransition2 ;
	@Plugin(
			name="Take Decision",
			parameterLabels = {},
			returnLabels ={"Decision Event"},
			returnTypes = {JPanel.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Farid Naufal",
			author = "Farid Naufal",
			email = "naufalfarid99@gmail.com"
			)
	public JPanel ModelTabel(final UIPluginContext context, ReadPNML pnml)
	{
		JPanel panel = new JPanel();
		tableTransition = new Object[pnml.decisions.size()][columname.length];
		
		Object[][] table = new Object[pnml.decisions.size()][];
		int c=0;
		for(int i=0;i<pnml.decisions.size();i++)
		{
			tableTransition[c][0] = pnml.decisions.get(i).getFirstTransition();
			tableTransition[c][1] = pnml.decisions.get(i).getNextTransition();
			tableTransition[c][2] = pnml.decisions.get(i).getAttribute();
			tableTransition[c][3] = pnml.decisions.get(i).getTypeAttribyte();
			tableTransition[c][4] = pnml.decisions.get(i).getPredicate();
			tableTransition[c][5] = pnml.decisions.get(i).getValue();
		}
		
		tableModelTransition2 = new DefaultTableModel(table,columname);
		
		for(int i=0;i<tableTransition.length;i++)
		{
			for(int j=0;j<columname.length;j++)
			{
				tableModelTransition2.setValueAt(tableTransition[i][j], i, j);
			}
		}
		
		ProMTable Ptabel = new ProMTable(tableModelTransition2);
		Ptabel.setPreferredSize(new Dimension(1000, 500));
		Ptabel.setAutoResizeMode(0);
		panel.add(Ptabel);
		context.showConfiguration("Tabel Transisi",panel);
		
		return panel;
	}
}
