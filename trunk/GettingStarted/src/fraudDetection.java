import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.importing.PnmlImportRINet_ConfigAnnotated;
import org.processmining.importing.log.LogFilterImportPlugin;
import org.processmining.importing.pnml.PnmlImport;
import org.processmining.mining.MiningResult;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.plugins.pnml.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class fraudDetection {
	private List<MiningResult> filePnml = new ArrayList<MiningResult>();
	private List<Pnml> filePnml2 = new ArrayList<Pnml>();
	private LogFilterImportPlugin log = new LogFilterImportPlugin();
	private ArrayList<PetrinetGraph> model = new ArrayList<PetrinetGraph>();
	//private CheckComplianceReplayer_Plugin compliance = new CheckComplianceReplayer_Plugin();
	private Object[] complianceResult ;
	private PnmlImportRINet_ConfigAnnotated pnmlImport = new PnmlImportRINet_ConfigAnnotated();
	MiningResult eventlog = new MiningResult() {
		
		public JComponent getVisualization() {
			// TODO Auto-generated method stub
			return null;
		}
		
		public LogReader getLogReader() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	private ArrayList<File> filemodel;
	private File filelog ;
	private String dir;
	private XLog logHasil;
	private UIPluginContext context ;
	boolean cek = false;
	
	PnmlImport pnml = new PnmlImport();
	@Plugin(
			name="Fraud Detection Plugin",
			parameterLabels = {},
			returnLabels ={"Fraud Results"},
			returnTypes = {JPanel.class},
			userAccessible = true
			)
	@UITopiaVariant(
			affiliation = "Fernandes Sinaga",
			author = "Fernandes Sinaga",
			email = "nandes.02@gmail.com"
			)
	
	
	public JPanel Conformance(final UIPluginContext context)
	{
		//context = context;
		final JTextArea inputfile = new JTextArea(); 
		final JPanel panel = new JPanel();
		final JButton browseModel = SlickerFactory.instance().createButton("Open Model File");
		final JButton browseLog = SlickerFactory.instance().createButton("Open Log File");
		final JButton conformance = SlickerFactory.instance().createButton("Conformance");
		final JTextField inputlog = new JTextField();
		final JLabel label1 = SlickerFactory.instance().createLabel("Import Model");
	    final JLabel label2 = SlickerFactory.instance().createLabel("Import Log");
		
		//panel.add(input);
		inputfile.setColumns(20);
		inputfile.setRows(5);
		
		JScrollPane jsc1 = new JScrollPane();
		
		jsc1.setViewportView(inputfile);
		
		GroupLayout layout = new GroupLayout(panel);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(conformance))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(inputlog)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(label2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(browseLog))
                            .addComponent(jsc1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(label1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(browseModel)))))
                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label1)
                    .addComponent(browseModel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsc1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label2)
                    .addComponent(browseLog))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inputlog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(conformance)
                .addGap(29, 29, 29))
        );
		
		
		browseModel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser fc = new JFileChooser();
				
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
				{
					dir = fc.getSelectedFile().getPath();
					
					File folder = new File(dir);
					File[] listFile = folder.listFiles();
					filemodel = new ArrayList<File>();
					for(File file : listFile)
					{
						if(file.getAbsolutePath().endsWith("pnml"))
						{
							filemodel.add(file);
							inputfile.setText(inputfile.getText()+"\n"+file.getName());
							System.out.println(file.getAbsolutePath());
							
						}
					}
					System.out.println(filemodel.size());
				}
				importPNML2();
			}
		});
		
		browseLog.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser fc2 = new JFileChooser();
				
				fc2.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(fc2.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
				{
					
					
						if(fc2.getSelectedFile().getAbsolutePath().endsWith("xes")||fc2.getSelectedFile().getAbsolutePath().endsWith("mxml"))
						{
							filelog = fc2.getSelectedFile();
							inputlog.setText(filelog.getName());
							System.out.println(filelog.getAbsolutePath());
							
						}
					
					
				}
				importLog();
			}
		});
		this.context = context;
		conformance.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				for(int i=0;i<filePnml.size();i++)
				{
					//conformance(filePnml.get(i),logHasil);
				}
				
				//conformance(model.get(0), logHasil);
				
				cek=true;
			}
		});
		
		if(cek==true)
		{
			conformance();
		}
			
		
		panel.setLayout(layout);
		context.showConfiguration("Conformance Checking",panel);
		return panel;
	}
	
	public void conformance()
	{
		//complianceResult = compliance.replayLog(context,model, log);
		XEventClassifier[] availableEventClass = new XEventClassifier[4];
		availableEventClass[0] = XLogInfoImpl.STANDARD_CLASSIFIER; 
		availableEventClass[1] = XLogInfoImpl.NAME_CLASSIFIER; 
		availableEventClass[2] = XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER; 
		availableEventClass[3] = XLogInfoImpl.RESOURCE_CLASSIFIER; 
		//MapEvPattern2Trans_Smart_UI ui = new MapEvPattern2Trans_Smart_UI(logHasil, model.get(0), availableEventClass);
		
		//InteractionResult result = context.showConfiguration("Conformance", ui.UIMap(context, logHasil, model.get(0), availableEventClass));
	}
	
	public void importPNML2()
	{
		for(int i=0;i<filemodel.size();i++)
		{
			InputStream is2 = null;
			try {
				is2 = new FileInputStream(filemodel.get(i).getAbsolutePath());
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			PnmlImportUtils utils = new PnmlImportUtils();
			Pnml pnml = new Pnml();
			PetrinetGraph net;
			try {
				pnml = utils.importPnmlFromStream(context, is2, filemodel.get(i).getName(), filemodel.get(i).length());
				filePnml2.add(pnml);
				net = PetrinetFactory.newResetInhibitorNet(pnml.getLabel() + " (imported from " + filemodel.get(i).getName() + ")");
				model.add(net);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("File Pnml ada sebanyak:"+filePnml2.size()+"  dan model PNgraph ada: "+model.size());
		
	}
	
	public void importPNML()
	{
		
		for(int i=0; i<filemodel.size();i++)
		{
			try {
				InputStream is = new FileInputStream(filemodel.get(i).getAbsolutePath());
				
				try {
					filePnml.add(pnml.importFile(is));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(filePnml.get(1));
	}
	
	public void importLog()
	{
		//System.out.println(filelog.getAbsolutePath());
		String filename = filelog.getName();
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		try {
			InputStream input = new FileInputStream(filelog.getAbsolutePath());
			
			
			XParser parser;
			if (filename.toLowerCase().endsWith(".xes") || filename.toLowerCase().endsWith(".xez")
					|| filename.toLowerCase().endsWith(".xes.gz")) {
				parser = new XesXmlParser(factory);
			} else {
				parser = new XMxmlParser(factory);
			}
			Collection<XLog> logs = null;
			try {
				logs = parser.parse(input);
			} catch (Exception e) {
				logs = null;
			}
			if (logs == null) {
				// try any other parser
				for (XParser p : XParserRegistry.instance().getAvailable()) {
					if (p == parser) {
						continue;
					}
					try {
						logs = p.parse(input);
						if (logs.size() > 0) {
							break;
						}
					} catch (Exception e1) {
						// ignore and move on.
						logs = null;
					}
				}
			}

			// log sanity checks;
			// notify user if the log is awkward / does miss crucial information
			
			XLog log = logs.iterator().next();
			if (XConceptExtension.instance().extractName(log) == null) {
				XConceptExtension.instance().assignName(log, "Anonymous log imported from " + filename);
			}
			logHasil= log;

			System.out.println("isi log "+logHasil.size());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
