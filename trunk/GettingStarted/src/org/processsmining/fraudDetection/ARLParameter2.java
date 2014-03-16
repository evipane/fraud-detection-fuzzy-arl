package org.processsmining.fraudDetection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class ARLParameter2 {
	
public double parameter;
	

	public JTextField inputParam = new JTextField("0.15");
	public JPanel ARLParam(final Double[] param)
	{
		JPanel panel4 = new JPanel();
		JButton jButton1 = SlickerFactory.instance().createButton("Input");
		final JLabel label1 = SlickerFactory.instance().createLabel("Support Threshold (0 - 1)");
		
		
        GroupLayout layout = new GroupLayout(panel4);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label1)
                    .addComponent(jButton1)
                    .addComponent(inputParam, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(269, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(label1)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(inputParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(216, Short.MAX_VALUE))
        );
        
        jButton1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				param[0] = Double.parseDouble(inputParam.getText());
			}
		});
        /*
        parameter = Double.parseDouble(inputParam.getText());
        param=parameter;
        System.out.println("Parameter: "+param);
        */
		return panel4;
	}

	public double getParameter() {
		return parameter;
	}

	public void setParameter(double parameter) {
		this.parameter = parameter;
	}

}
