package org.agentcommunity.ANSMAS.NSSS;

import jade.gui.GuiEvent;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.agentcommunity.ANSMAS.Utils;
import org.agentcommunity.ANSMAS.NSSS.ontologies.NSVocabulary;
import org.agentcommunity.ANSMAS.NSSS.ontologies.NegotiationStrategy;

public class NSAGUI extends JFrame implements ActionListener, NSVocabulary{
//public class NSAGUI extends JFrame implements TreeSelectionListener, ActionListener, NSVocabulary{

	protected NSA nsa;
	protected TableDataModel tdm;
	protected JPanel contentPane;
	public JTextArea RMjTextArea;
	public JTextArea SMjTextArea;
	public JTextArea SLjTextArea;
    private JEditorPane htmlPane1;
    private JEditorPane htmlPane2;
	public HashMap nso = new HashMap(); 
	HashMap jarInfo = new HashMap(); 
	JTree jarTree;



	public JTextArea jTextArea1;

	public NSAGUI(NSA n){

		nsa = n;
		try {
			jbInit();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


  protected void jbInit() throws Exception {

	contentPane = (JPanel) this.getContentPane();
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.BOTH;
    contentPane.setLayout(gbl);
//	contentPane.setBackground(Color.WHITE);
    this.setSize(new Dimension(600, 700));
	this.setLocation(400, 400); // �������� ��ġ ����
    this.setTitle("NSSS:Negotiation Strategy Shared Server - "+ nsa.getName());
	
// �޴���
	JMenuBar menuBar = createJMenuBar();
	setJMenuBar(menuBar);
// ������� ���̺�
	gbc.weightx = 1.0; 
	gbc.weighty = 1.0; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
	registerSampleDataset();
	tdm = new TableDataModel(nso);
	JTable NSOjTable = new JTable(tdm);
//	NSOjTable.setPreferredSize(new Dimension(600, 80));
	JScrollPane NSOjScrollPane = new JScrollPane(NSOjTable);
	NSOjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	NSOjScrollPane.setPreferredSize(new Dimension(600, 100));
	NSOjScrollPane.setBorder(new TitledBorder("NSO:Negotiation Strategy Ontology"));
	contentPane.add(NSOjScrollPane, gbc);


// Ŭ���� ���̺귯��

//Ŭ���� ���̺귯�� - ���� Ʈ��
	gbc.gridwidth=1; 
	
	//Create the nodes.
	DefaultMutableTreeNode treeTop = new DefaultMutableTreeNode("Negotiation Strategy Library");
	createNodes(treeTop);
	jarTree = new JTree(treeTop);


        jarTree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
	//Listen for when the selection changes.
	jarTree.addTreeSelectionListener(new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			jarTree.getLastSelectedPathComponent();

			if (node == null){
				return;
			}//if

			Object nodeInfo = node.getUserObject();
			if (node.isLeaf()) {
				htmlPane1.setText(node.toString () );
			} else {
				int depth = node.getDepth();
				String uid = null;
				if(depth == 2){
					htmlPane1.setText("<font size='3'><CENTER><B>Negotiation Strategy Library's Jar File Information</B></CENTER></font>");
				}else if (depth == 1){
					StringTokenizer st = new StringTokenizer(node.toString(),".",false);
					uid = st.nextToken();
					NegotiationStrategy ns = (NegotiationStrategy) nso.get(uid);
					String info = (String) jarInfo.get(uid);
					htmlPane1.setText("<CENTER><font size='3'><b>UID " + ns.getUID() + "." + ns.getName() +"</b> - <font size='2'>META-INF/MANIFEST.MF</CENTER><br></font><br><font size='3'>" + info +"</font>");
					//htmlPane1.setText(nso.get(Object  key));
				}//else if
			}//else

		}
	});//jarTree.addTreeSelectionListener(new TreeSelectionListener()


	//jarTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	//Listen for when the selection changes.
	
	// The JTree can get big, so allow it to scroll
    JScrollPane jScrollPane100 = new JScrollPane(jarTree);
	jScrollPane100.setBorder(new TitledBorder("NSL:Negotiation Strategy Library"));
	contentPane.add(jScrollPane100, gbc);

//Ŭ���� ���̺귯�� - ���� ���� META-INF/MANIFEST.MF
	gbc.gridwidth=1; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
    htmlPane1 = new JEditorPane("text/html","<font size='3'><CENTER><B>Negotiation Strategy Library's Jar File Information</B></CENTER></font>");
    htmlPane1.setEditable(false);
	JScrollPane jScrollPane01 = new JScrollPane(htmlPane1);
	jScrollPane01.setBorder(new TitledBorder("META-INF/MANIFEST.MF"));
	contentPane.add(jScrollPane01, gbc);


// ���� �޼���
	gbc.gridwidth=1; 
	RMjTextArea = new JTextArea();
	RMjTextArea.setLineWrap(true);
	JScrollPane RMjScrollPane = new JScrollPane(RMjTextArea);
	RMjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	RMjScrollPane.setBorder(new TitledBorder("Recieve Message Monitor"));
	contentPane.add(RMjScrollPane, gbc);
// �۽� �޼���
	gbc.gridwidth=1; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
	SMjTextArea = new JTextArea();
	SMjTextArea.setLineWrap(true);
	JScrollPane SMjScrollPane = new JScrollPane(SMjTextArea);
	SMjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	SMjScrollPane.setBorder(new TitledBorder("Send Message Monitor"));
	contentPane.add(SMjScrollPane, gbc);
// �ý��� �α�
	gbc.weightx = 1.0; 
	gbc.weighty = 1.0; 
	gbc.gridwidth=GridBagConstraints.REMAINDER;
	SLjTextArea = new JTextArea();
	SLjTextArea.setLineWrap(true);
	JScrollPane SLjScrollPane = new JScrollPane(SLjTextArea);
	SLjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	SLjScrollPane.setBorder(new TitledBorder("NSSS Log Monitor"));
	contentPane.add(SLjScrollPane, gbc);


// �׼� ������
	this.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	GuiEvent ev = new GuiEvent ((Object) this, nsa.EXIT);
	nsa.postGuiEvent(ev);
	}
	});

	SLjTextArea.append(Utils.getSystemDate() + "Negotiation Strategy Shared Server is starting...\n") ;
	SLjTextArea.append(Utils.getSystemDate() + "Negotiation Strategy Agent home address is " + nsa.getHap() + "\n") ;	
	SLjTextArea.append(Utils.getSystemDate() + "Negotiation Strategy Agent complete name(GUID) is  " + nsa.getName()  + "\n") ;	
	SLjTextArea.append(Utils.getSystemDate() + "Negotiation Strategy Shared Server started successfully.\n") ;

  }



   public void actionPerformed(ActionEvent ae) {
// ---------------------------------------------

   }
  /**
   * NSAGUI �޴��� ��
   */

 	protected JMenuBar createJMenuBar(){
	final JMenuBar menuBar = new JMenuBar();

	JMenu mFile = new JMenu("File");
	mFile.setMnemonic('f');
	menuBar.add(mFile);


	/* File �޴��� Exit �޴� ������*/

	mFile.addSeparator(); // �޴� ���м� ����


	/* File �޴��� Exit �޴� ������ - NSA�� AP���� �����Ѵ�*/
	JMenuItem item = new JMenuItem("Exit"); // �޴� ������ �߰�
	item.setMnemonic('x'); // Ű���� ����Ű �Ҵ�
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK)); // Ű���� ���������� ����
	ActionListener lst = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	GuiEvent ev = new GuiEvent ((Object) this, nsa.EXIT);
	nsa.postGuiEvent(ev);
	}
	};//�׼Ǹ����� ����

	item.addActionListener(lst);
	mFile.add(item);
	menuBar.add(mFile);
	/* File �޴��� Exit �޴� ������*/


	return menuBar;
	}

protected void registerSampleDataset(){
                                        


		//Negotiation Strategy Ontology - Sample Dataset
		String[] uid = {"1","3","4","2"};
		String[] name = {"MAUT","AW","MAUT1","AW1"};
		String[] author = {"DSKIM","ADAMS","DSKIM","ADAMS"};
		String[] version = {"v1.6 2004/04/01","v1.6 2004/04/01","v1.6 2004/04/01","v1.6 2004/04/01"};
		String[] algorithm = {"game theory", "adjust-winner","game theory", "adjust-winner"};
		String[] issues = {"price,quility","price","price,quility","price"};
		String[] description = {"test","test2","test","test2"};
		String[] relativeURI = {"./org/agentcommunity/ANSMAS/NSSS/ontologies/NSL/maut.jar","./org/agentcommunity/ANSMAS/NSSS/ontologies/NSL/aw.jar","./org/agentcommunity/ANSMAS/NSSS/ontologies/NSL/maut1.jar","./org/agentcommunity/ANSMAS/NSSS/ontologies/NSL/aw.jar"};

		
		System.out.println(Utils.getSystemDate() + nsa.getLocalName() + ": Loading NSA Ontology Sample Dataset...ok");	
	    for (int i=0; i<uid.length; i++){

		NegotiationStrategy ns = new NegotiationStrategy();

		ns.setUID(uid[i]);
		ns.setName(name[i]);
		ns.setAuthor(author[i]);
		ns.setVersion(version[i]);
		ns.setAlgorithm(algorithm[i]);
		ns.setIssues(issues[i]);
		ns.setDescription(description[i]);
		ns.setRelativeURI(relativeURI[i]);
		nso.put(uid[i], ns);


    }//for

  }//NSARegisterData()

	private void createNodes(DefaultMutableTreeNode nsl) {

		DefaultMutableTreeNode category = null; // UID + ���
		DefaultMutableTreeNode uri = null;// ���ϰ��

		int rowLength =  nso.size();
		Set set = nso.keySet();
		Object [] nsoKeys = set.toArray();
		NegotiationStrategy ns = new NegotiationStrategy();

		for(int i = 0; i < nsoKeys.length; i++){
			String key = (String)nsoKeys[i];
			ns = (NegotiationStrategy) nso.get(key);
			category = new DefaultMutableTreeNode(ns.getUID() +"." + ns.getRelativeURI());
			nsl.add(category);
			///uri = new DefaultMutableTreeNode(ns.getRelativeURI());
			//category.add(uri);
 //****************************************************************************************88
			try{
			//JarEntry 
				JarFile jarFile = new JarFile(ns.getRelativeURI());
				Enumeration<JarEntry> enums = jarFile.entries();
				//System.out.println("Jar File:" + relativeURI);
				while (enums.hasMoreElements()){
					JarEntry entry = (JarEntry)enums.nextElement();
					String name = entry.getName();
					String size = new Long(entry.getSize()).toString();
					String compressedSize = new Long(entry.getCompressedSize()).toString();
				category.add(new DefaultMutableTreeNode(name + " [size:" + size + "k / " + "compressed size:" + compressedSize +"k]"));
				//	System.out.println("-->Name:" + name + "\n-->" + "Size:" + size + "\n-->" + "Compressed Size:" + compressedSize);
				}//while

			//MANIFEST.MF
				JarEntry entry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
				InputStream input = jarFile.getInputStream(entry);
				InputStreamReader isr = new InputStreamReader(input);
				BufferedReader reader = new BufferedReader(isr);
				String line;
				StringBuffer sb = new StringBuffer();  
				while ((line = reader.readLine()) != null){
							sb.append(line).append("<br>");
				}//while
			//	System.out.println(sb);
				jarInfo.put(ns.getUID(), sb.toString());
				reader.close();




 
			}catch(IOException e){
				SLjTextArea.append("ERROR:" + ns.getRelativeURI() + " Jar file loading fail\n" + e);
			}
 //****************************************************************************************88


		}//for

	}//createNodes(DefaultMutableTreeNode nsl)


 
}

// =========================== External class ============================//

/* TableDataModel:
*  --------------
*  External class for the definition of the tables data model, used to
*  control the display of data within the different tables
**/
   class TableDataModel extends AbstractTableModel {
// -------------------------------------------------

		//Negotiation Strategy Ontology - uid name author version algorithm issues description relativeURI
		private  String[] uid;
		private  String[] name;
		private  String[] author;
		private  String[] version;
		private  String[] algorithm;
		private  String[] issues;
		private  String[] description;
		private  String[] relativeURI;
		private  String[] names = {"UID", "Name", "Author", "Version", "Algorithm", "Issues", "Description", "Relative URI"};
		int rowLength;
		NegotiationStrategy ns = new NegotiationStrategy();

   public TableDataModel(HashMap tdm) {
// ----------------------------------------------------------  Constructor
	  	rowLength =  tdm.size();
		Set set = tdm.keySet();
        Object [] tdmKeys = set.toArray();
		
		uid = new String[tdmKeys.length];
		name = new String[tdmKeys.length];
		author = new String[tdmKeys.length];
		version = new String[tdmKeys.length];
		algorithm = new String[tdmKeys.length];
		issues = new String[tdmKeys.length];
		description = new String[tdmKeys.length];
		relativeURI = new String[tdmKeys.length];

        for(int i = 0; i < tdmKeys.length; i++)
        {
			String key = (String)tdmKeys[i];
            ns = (NegotiationStrategy) tdm.get(key);
			uid[i] = ns.getUID();
			name[i] = ns.getName();
			author[i] = ns.getAuthor();
			version[i] = ns.getVersion();
			algorithm[i] = ns.getAlgorithm();
			issues[i] = ns.getIssues();
			description[i] = ns.getDescription();
			relativeURI[i] = ns.getRelativeURI();

					

	    }//for
   }//TableDataModel(HashMap hm)

  /** Returns number of columns - used for displaying table - MANDATORY */
  public int getColumnCount() {
    return names.length;
  }

  /** Returns number of rows - used for displaying table - MANDATORY */
  public int getRowCount() {
    return rowLength;
  }

  /** Returns value at cell [row,col] - used for displaying table - MANDATORY */
  public Object getValueAt(int row, int col) {
    switch (col) {
		case 0:
		return uid[row];
		case 1:
		return name[row];
		case 2:
		return author[row];
		case 3:
		return version[row];
		case 4:
		return algorithm[row];
		case 5:
		return issues[row];
		case 6:
		return description[row];
		case 7:
		return relativeURI[row];
	}
    return "";
  }


  public String getColumnName(int col) {
    return names[col];
  }

}// end TableDataModel


