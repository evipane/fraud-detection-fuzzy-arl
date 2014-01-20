import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;


public class nandes {
	String out ="" ;
	@Plugin(
			name = "Nandesnaga Plugin",
			parameterLabels= {},
			returnLabels = {"Hello string"},
			returnTypes = {String.class},
			userAccessible = true,
			help = "Produces the string: 'Hello Nandes'"
			)
	@UITopiaVariant(
			affiliation = "Nandesnaga",
			author = "Nandes",
			email = "nandes.02@gmail.com"
			)
	
	
	public String helloNandes(final UIPluginContext context){
		
		JPanel panel = new JPanel(new FlowLayout(5));
		JTextField input1 = new JTextField();
		JTextField input2 = new JTextField();
		Font font = input1.getFont().deriveFont(Font.PLAIN, 50f);
		
		input1.setFont(font);
		input2.setFont(font);
		input1.setColumns(10);
		input2.setColumns(10);
		
		JButton but = new JButton("OK");
		but.setFont(font);
		but.setText("OK");
		but.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		isi(input1,input2);
		panel.add(input1);
		panel.add(input2);
		context.showConfiguration("CobaInput",panel);
		if(out.isEmpty())
		{
			isi(input1,input2);
		}
		return out;
	}
	
	public void isi(JTextField input1,JTextField input2)
	{
		String i1 = input1.getText();
		String i2 = input2.getText();
		out = i1+i2;
	}
}
