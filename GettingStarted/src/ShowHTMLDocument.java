

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.w3c.dom.Document;

/**
 * Adaptation from Christian W. Guenther's LogDialog package LogInfoUI class
 * 
 * @author Wiebe E. Nauta (wiebenauta@gmail.com)
 */
@Plugin(name = "Show HTML document", returnLabels = { "HTML document" }, returnTypes = { JComponent.class }, parameterLabels = "HTML document")
@Visualizer
public class ShowHTMLDocument {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Wiebe E. Nauta", email = "wiebenauta@gmail.com")
	@PluginVariant(requiredParameterLabels = { 0 })
	public static JComponent showHTMLDocument(PluginContext context, Document document) {
		return createPanel(context, document);
	}

	private static JComponent createPanel(final PluginContext context, Document document) {

		String title;
		if (document.getElementsByTagName("title").getLength() != 0)
			title = document.getElementsByTagName("title").item(0).getTextContent();
		else
			title = "Unnamed HTML document";

		final JTextPane summaryPane = new JTextPane();
		summaryPane.setBorder(BorderFactory.createEmptyBorder());
		summaryPane.setContentType("text/html");
		// pre-populate the text pane with some teaser message
		summaryPane.setText("<html><body bgcolor=\"#888888\" text=\"#333333\">"
				+ "<br><br><br><br><br><center><font face=\"helvetica,arial,sans-serif\" size=\"4\">"
				+ "Please wait while the summary is created...</font></center></body></html>");
		summaryPane.setEditable(false);
		summaryPane.setCaretPosition(0);

		JScrollPane scrollPane = new JScrollPane(summaryPane);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		//		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		//		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(20, 20, 20), new Color(60, 60, 60), 4,
		//				12));
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		//		RoundedPanel scrollEnclosure = new RoundedPanel(10, 0, 0);
		//		JPanel scrollEnclosure = new JPanel();
		//		scrollEnclosure.setBackground(Color.decode("#888888"));
		//		scrollEnclosure.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//		scrollEnclosure.setLayout(new BorderLayout());
		//		scrollEnclosure.add(scrollPane, BorderLayout.CENTER);

		JLabel header = new JLabel(title);
		header.setBackground(new Color(0, 0, 0));
		header.setForeground(new Color(200, 200, 200, 180));
		header.setFont(header.getFont().deriveFont(15f));

		//		JButton saveButton = new SlickerButton("save HTML...");
		JButton saveButton = new JButton("save HTML...");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser saveDialog = new JFileChooser();
				saveDialog.setSelectedFile(new File("HTMLDocument.html"));
				if (saveDialog.showSaveDialog(((UIPluginContext) context).getGlobalContext().getUI()) == JFileChooser.APPROVE_OPTION) {
					File outFile = saveDialog.getSelectedFile();
					try {
						BufferedWriter outWriter = new BufferedWriter(new FileWriter(outFile));
						outWriter.write(summaryPane.getText());
						outWriter.flush();
						outWriter.close();
						JOptionPane.showMessageDialog(((UIPluginContext) context).getGlobalContext().getUI(),
								"HTML document saved", "HTML document saved", JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(0, 0, 0));
		headerPanel.setForeground(new Color(0, 0, 0));
		headerPanel.setBorder(BorderFactory.createEmptyBorder());
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.add(header);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.add(saveButton);

		JPanel panel = new JPanel();
		//		panel.setBorder(BorderFactory.createEmptyBorder(3, 10, 5, 10));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(new Color(0, 0, 0));
		panel.setForeground(new Color(0, 0, 0));
		panel.add(headerPanel);
		//		panel.add(Box.createVerticalStrut(7));
		panel.add(scrollPane);
		//		panel.add(scrollEnclosure);

		summaryPane.setText(getStringFromDocument(document));
		summaryPane.setCaretPosition(0);

		return panel;
	}

	private static String getStringFromDocument(Document document) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			Source source = new DOMSource(document);
			Result result = new StreamResult(outputStream);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputStream.toString();
	}

	//	private static String getStringFromDocument2(Document document) {
	//		DOMImplementationRegistry registry = null;
	//		try {
	//			registry = DOMImplementationRegistry.newInstance();
	//		} catch (Exception e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
	//		LSSerializer writer = impl.createLSSerializer();
	//		String str = writer.writeToString(document);
	//		return str;
	//	}
}
