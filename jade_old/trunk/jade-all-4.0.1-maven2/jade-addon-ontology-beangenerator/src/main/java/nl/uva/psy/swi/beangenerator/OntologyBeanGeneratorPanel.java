/**
 *  JADE - Java Agent DEvelopment Framework is a framework to develop
 *  multi-agent systems in compliance with the FIPA specifications. Copyright
 *  (C) 2002 TILAB S.p.A. This file is donated by Acklin B.V. to the JADE
 *  project. GNU Lesser General Public License This library is free software;
 *  you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public License as published by the Free Software Foundation, version
 *  2.1 of the License. This library is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 *  General Public License for more details. You should have received a copy of
 *  the GNU Lesser General Public License along with this library; if not, write
 *  to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 *  MA 02111-1307, USA. **************************************************************
 */
package nl.uva.psy.swi.beangenerator;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WaitCursor;

/*
 *  @author     Chris van Aart - Acklin, University of Amsterdam
 *  @created    November 14, 2002
 */
public class OntologyBeanGeneratorPanel extends JPanel {

  private static String getLaunchString(String document) {
    String launchString;
    if (isWinNT() || isWin2000()) {
      launchString = "cmd /c start " + document;
    } else if (isWin9X()) {
      launchString = "start " + document;
    } else {
      launchString = "netscape " + document;
    }

    return launchString;
  }


  private static String getSystemProperty(String property) {
    String value;
    try {
      value = System.getProperty(property);
    } catch (SecurityException e) {
      value = "";
    }
    return value;
  }


  private static boolean isWin2000() {
    return getSystemProperty("os.name").indexOf("Windows 2000") != -1;
  }


  private static boolean isWin95() {
    return getSystemProperty("os.name").indexOf("Windows 95") != -1;
  }


  private static boolean isWin98() {
    return getSystemProperty("os.name").indexOf("Windows 98") != -1;
  }


  private static boolean isWin9X() {
    return isWin95() || isWin98();
  }


  private static boolean isWinNT() {
    return getSystemProperty("os.name").indexOf("Windows NT") != -1;
  }


  private static boolean isWindows() {
    return isWinNT() || isWin95() || isWin98() || isWin2000();
  }


  private void acklin_mouseClicked(MouseEvent e) {
    showHTML("http://www.acklin.nl");
  }


  private void acklin_mouseEntered(MouseEvent e) {
    //    handCursor.show();
  }


  private void acklin_mouseExited(MouseEvent e) {
    //    handCursor.hide();
  }


  private void fillComboBoxModels() {
    this.locationComboBox.setModel(locationBoxModel);
    this.locationComboBox.setRenderer(new SomeLabelRenderer());
    locationComboBox.setBackground(Color.white);

    this.packageComboBox.setModel(packageBoxModel);
    this.packageComboBox.setRenderer(new SomeLabelRenderer());
    packageComboBox.setBackground(Color.white);

    this.ontologyComboBox.setModel(ontologyBoxModel);
    this.ontologyComboBox.setRenderer(new SomeLabelRenderer());
    ontologyComboBox.setBackground(Color.white);

    Properties p = new Properties();
	String propFile =  "beangenerator.properties";
	
	File pluginDir = PluginUtilities.getInstallationDirectory("nl.uva.psy.swi.beangenerator.OntologyBeanGeneratorTab");			
	if (pluginDir != null)
		propFile = pluginDir.getAbsolutePath() + File.separator + propFile;
	
	try {
		p.load(new FileInputStream(new File(propFile)));
	} catch (Exception ex) {
		/*
		JOptionPane.showMessageDialog(null,ex.getMessage()+
				"\nPut beangenerator.properties in the home directory of protege. In you are a windows users, make sure windows did not alter the extension of the file into e.g. .txt.",
				"error", JOptionPane.ERROR_MESSAGE);
		*/
		Log.getLogger().warning("The beangenerator.properties was not found. Path:" + propFile);
		return;
	}
    String key = "location";
    String value = "";
    int i = 1;
    while (i < 6) {
      value = p.getProperty(key + i);
      locationBoxModel.addElement(value);
      i++;
    }
    locationComboBox.setSelectedIndex(0);

    key = "package";
    value = "";
    i = 1;
    while (i < 6) {
      value = p.getProperty(key + i);
      packageBoxModel.addElement(value);
      i++;
    }
    packageComboBox.setSelectedIndex(0);

    key = "ontology";
    value = "";
    i = 1;
    while (i < 6) {
      value = p.getProperty(key + i);
      ontologyBoxModel.addElement(value);
      i++;
    }
    ontologyComboBox.setSelectedIndex(0);

    // get the initial state of the Checkboxes
    value = p.getProperty("support");
    if ((value != null) && value.equalsIgnoreCase("j2me")) {
      support.setSelected(microSupport.getModel(), true);
    } else if ((value != null) && value.equalsIgnoreCase("j2se")) {
      support.setSelected(standardSupport.getModel(), true);
    } else if ((value != null) && value.equalsIgnoreCase("javabeans")) {
      support.setSelected(fullBeanSupport.getModel(), true);
    } else {
      // default

      support.setSelected(standardSupport.getModel(), true);
    }

    // jalopy file
    value = p.getProperty("jalopy");
    if (value != null) {
      doFormatting.getModel().setSelected(true);
      jalopyFile.setText(value);

    } else {
      doFormatting.getModel().setSelected(false);
      jalopyFile.setText("");
    }
  }


  void fullBeanSupport_stateChanged(ChangeEvent e) {
    if (fullBeanSupport.isSelected()) {
      this.exampleTextArea.setText(Examples.getFullExample());
    }
  }


  private void itsGenerateButton_actionPerformed(ActionEvent e) {
    this.statusTextArea.setText("");

    Thread processor =
      new Thread() {
        public void run() {
          model.doIt();
        }
      };
    processor.start();
  }


  void itsLocateButton_actionPerformed(ActionEvent e) {
    try {
      thejFileChooser.setApproveButtonText("Select");
      this.thejFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      this.thejFileChooser.showOpenDialog(this);

      File f = this.thejFileChooser.getSelectedFile();
      String theLocation = f.getPath();
      if (locationBoxModel.getIndexOf(theLocation) < 0) {
        locationBoxModel.addElement(theLocation);
      }
      this.locationComboBox.setSelectedItem(theLocation);

    } catch (Exception g) {
      g.printStackTrace();
    }
  }


  void itsOntologyNameButton_actionPerformed(ActionEvent e) {
    String projectName = this.model.getProject().getName();

    if (this.ontologyBoxModel.getIndexOf(projectName) < 0) {
      this.ontologyBoxModel.addElement(projectName);
    }
    this.ontologyComboBox.setSelectedItem(projectName);

  }


  void itsPackageNameButton_actionPerformed(ActionEvent e) {
    String projectName = this.model.getProject().getName();
    if (projectName == null)
    	projectName = "noName";
    projectName = projectName.toLowerCase() + ".ontology";
    if (this.packageBoxModel.getIndexOf(projectName) < 0) {
      this.packageBoxModel.addElement(projectName);
    }
    this.packageComboBox.setSelectedItem(projectName);
  }


  void jbInit() throws Exception {
    acklin.setIcon(this.acklinIcon);
    acklin.setToolTipText("http://www.acklin.nl");
    acklin.addMouseListener(
                            new java.awt.event.MouseAdapter() {
                              public void mouseClicked(MouseEvent e) {
                                acklin_mouseClicked(e);
                              }


                              public void mouseEntered(MouseEvent e) {
                                acklin_mouseEntered(e);
                              }


                              public void mouseExited(MouseEvent e) {
                                acklin_mouseExited(e);
                              }
                            });
    border1 = BorderFactory.createLineBorder(Color.white, 1);
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(Color.black, 1), "status:");
    this.setLayout(gridBagLayout1);
    rightPanel.setBackground(Color.white);
    rightPanel.addMouseListener(
                                new java.awt.event.MouseAdapter() {
                                  public void mouseExited(MouseEvent e) {
                                    rightPanel_mouseExited(e);
                                  }
                                });
    rightPanel.setLayout(gridBagLayout2);
    jLabel1.setText("packagename (e.g. mypackage.onto)");
    jLabel2.setText("location excl. package (e.g. /home/chris/projects/myproject/src/)");
    itsLocateButton.setBackground(Color.white);
    itsLocateButton.setToolTipText("select a directory");
    itsLocateButton.setText("...");
    itsLocateButton.addActionListener(
                                      new java.awt.event.ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                          itsLocateButton_actionPerformed(e);
                                        }
                                      });
    jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
    jLabel3.setForeground(Color.orange);
    jLabel3.setText("will overwrite all existing files");
    jLabel4.setToolTipText("");
    jLabel4.setText("ontologydomain (e.g. Newspaper)");
    jLabel5.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel5.setForeground(Color.black);
    jLabel5.setToolTipText("<html><b>v. 3.1</b><html>");
    jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel5.setText("Ontology Bean Generator for Jade 3.1");
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane1.getViewport().setBackground(Color.white);
    jScrollPane1.setBorder(titledBorder1);
    jScrollPane1.setOpaque(false);
    statusTextArea.setEnabled(false);
    statusTextArea.setDisabledTextColor(Color.black);
    statusTextArea.setFont(new java.awt.Font("Dialog", 0, 11));
    leftPanel.setLayout(gridBagLayout3);
    leftPanel.setBackground(UIManager.getColor("List.background"));
    jLabel6.setText("This directory will be created if it doesn't already exist");
    packageComboBox.setBackground(Color.white);
    packageComboBox.setFont(new java.awt.Font("Dialog", 0, 11));
    packageComboBox.setEditable(true);
    locationComboBox.setBackground(Color.white);
    locationComboBox.setFont(new java.awt.Font("Dialog", 0, 11));
    locationComboBox.setEditable(true);
    ontologyComboBox.setBackground(Color.white);
    ontologyComboBox.setFont(new java.awt.Font("Dialog", 0, 11));
    ontologyComboBox.setEditable(true);
    jLabel7.setToolTipText("package");
    jLabel7.setIcon(packageIcon);
    jLabel8.setToolTipText("location");
    jLabel8.setIcon(outputIcon);
    jLabel9.setToolTipText("ontology");
    jLabel9.setIcon(inboxIcon);
    uvaLabel.setToolTipText("http://www.swi.psy.uva.nl");
    uvaLabel.setIcon(this.uvaIcon);
    uvaLabel.addMouseListener(
                              new java.awt.event.MouseAdapter() {
                                public void mouseClicked(MouseEvent e) {
                                  uvaLabel_mouseClicked(e);
                                }


                                public void mouseEntered(MouseEvent e) {
                                  uvaLabel_mouseEntered(e);
                                }


                                public void mouseExited(MouseEvent e) {
                                  uvaLabel_mouseExited(e);
                                }
                              });

    // Contributors panel
    JPanel contributors = new JPanel();
    contributors.setLayout(new FlowLayout(FlowLayout.CENTER));
    contributors.setBackground(Color.white);
    jLabel10.setFont(new java.awt.Font("Dialog", 0, 11));
    jLabel10.setText("progress");
    itsGenerateButton.setBackground(Color.white);
    itsGenerateButton.setForeground(Color.blue);
    itsGenerateButton.setToolTipText("Generate beans and optional jade ontology file");
    itsGenerateButton.setIcon(executeIcon);
    itsGenerateButton.setText("Generate Beans");
    itsGenerateButton.addActionListener(
                                        new java.awt.event.ActionListener() {
                                          public void actionPerformed(ActionEvent e) {
                                            itsGenerateButton_actionPerformed(e);
                                          }
                                        });
    jPanel2.setBackground(Color.white);
    jPanel1.setBackground(Color.white);
    jPanel1.setLayout(gridBagLayout4);
    itsPackageNameButton.addActionListener(
                                           new java.awt.event.ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               itsPackageNameButton_actionPerformed(e);
                                             }
                                           });
    itsPackageNameButton.setText("...");
    itsPackageNameButton.setToolTipText("use name of project");
    itsPackageNameButton.setBackground(Color.white);
    itsOntologyNameButton.setBackground(Color.white);
    itsOntologyNameButton.setToolTipText("use name of project");
    itsOntologyNameButton.setText("...");
    itsOntologyNameButton.addActionListener(
                                            new java.awt.event.ActionListener() {
                                              public void actionPerformed(ActionEvent e) {
                                                itsOntologyNameButton_actionPerformed(e);
                                              }
                                            });
    jLabel11.setFont(new java.awt.Font("Dialog", 0, 11));
    jLabel11.setText("example:");
    exampleTextArea.setEditable(false);
    exampleTextArea.setFont(new java.awt.Font("Dialog", 0, 11));
    fullBeanSupport.addChangeListener(
                                      new javax.swing.event.ChangeListener() {
                                        public void stateChanged(ChangeEvent e) {
                                          fullBeanSupport_stateChanged(e);
                                        }
                                      });
    standardSupport.addChangeListener(
                                      new javax.swing.event.ChangeListener() {
                                        public void stateChanged(ChangeEvent e) {
                                          standardSupport_stateChanged(e);
                                        }
                                      });
    microSupport.addChangeListener(
                                   new javax.swing.event.ChangeListener() {
                                     public void stateChanged(ChangeEvent e) {
                                       microSupport_stateChanged(e);
                                     }
                                   });
    /* Modified by J.Picault
       MIDPCheckBox.setText("MIDP workaround");
       MIDPCheckBox.setToolTipText("add(groupSchema, Class.forName(\"com.mot.wps.ontology.Group.class\")); " +
       "instead of add(groupSchema, com.mot.wps.ontology.Group.class);");
       MIDPCheckBox.setBackground(Color.white);
    */
    contributors.add(uvaLabel);
    contributors.add(acklin);
    contributors.add(mleLabel);
    contributors.add(jadeLabel);
    /* modified by J.Picault
       rightPanel.add(MIDPCheckBox, new GridBagConstraints(2, 16, 1, 1, 1.0, 0.0
       , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0)); 
    */
    rightPanel.add(itsOntologyNameButton, new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0
                                                                 , GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(itsPackageNameButton, new GridBagConstraints(4, 2, 1, 2, 0.0, 0.0
                                                                , GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(jPanel1, new GridBagConstraints(1, 11, 1, 9, 1.0, 1.0
                                                   , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
    jPanel1.add(jLabel11, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jScrollPane2, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                                                     , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane2.getViewport().add(exampleTextArea, null);
    rightPanel.add(jPanel2, new GridBagConstraints(0, 9, 5, 1, 0.0, 0.0
                                                   , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 1, 0));
    jPanel2.add(itsGenerateButton, null);
    rightPanel.add(jalopyFileLocator, new GridBagConstraints(3, 15, 1, 1, 0.0, 0.0
                                                             , GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    // MLE label
    mleLabel.setIcon(mleIcon);
    mleLabel.setToolTipText("Media Lab Europe");
    mleLabel.addMouseListener(
                              new MouseAdapter() {
                                public void mouseClicked(MouseEvent e) {
                                  showHTML("http://www.mle.ie");
                                }
                              });

    // generate ontologies and/or beans
    jadeCheckBox.setSelected(true);
    jadeCheckBox.setText("generate jade ontology file");
    jadeCheckBox.setToolTipText("Generate the Ontology file");
    jadeCheckBox.setBackground(Color.white);
    beansCheckBox.setSelected(true);
    beansCheckBox.setText("generate beans");
    beansCheckBox.setToolTipText("Generate the bean classes");
    beansCheckBox.setBackground(Color.white);
    useJadeNamesCheckBox.setSelected(false);//modified by J.Picault
    useJadeNamesCheckBox.setText("use JADE names when specified");
    useJadeNamesCheckBox.setToolTipText("Use the \"JADE Name\" field specified in Protege");
    useJadeNamesCheckBox.setBackground(Color.white);

    // J2ME support
    microSupport.setSelected(false);
    microSupport.setText("J2ME compatible [JADE-LEAP]");
    microSupport.setToolTipText(
                                "Compatible with the Java 2 Micro Edition and the jade.content.onto.MicroIntrospector");
    microSupport.setBackground(Color.white);

    // full bean support
    fullBeanSupport.setSelected(false);
    fullBeanSupport.setText("J2SE JavaBean compatible [JADE]");
    fullBeanSupport.setToolTipText("Compatible with the JavaBeans specification");
    fullBeanSupport.setBackground(Color.white);

    // standard support
    standardSupport.setSelected(false);
    standardSupport.setText("J2SE and Java 1.1 compatible [JADE, JADE-LEAP]");
    standardSupport.setToolTipText("Compatible with Java 2 or Java 1.1");
    standardSupport.setBackground(Color.white);

    // Button group
    support.add(fullBeanSupport);
    support.add(standardSupport);
    support.add(microSupport);
    support.setSelected(standardSupport.getModel(), true);

    // Jalopy UI items
    jalopyFile.setVisible(false);
    doFormatting.setVisible(false);

    doFormatting.setText("Apply formatting");
    doFormatting.setBackground(Color.white);
    doFormatting.getModel().addChangeListener(
                                              new ChangeListener() {
                                                public void stateChanged(ChangeEvent e) {
                                                  if (!doFormatting.isSelected()) {
                                                    jalopyFile.setEnabled(false);
                                                  } else {
                                                    jalopyFile.setEnabled(true);
                                                  }
                                                  jalopyFile.repaint();
                                                }
                                              });
    jalopyFile.setColumns(25);
    jalopyFileLocator.setBackground(Color.white);
    jalopyFileLocator.setToolTipText("select a directory");
    jalopyFileLocator.setText("...");
    jalopyFileLocator.setVisible(false);
    jalopyFileLocator.addActionListener(
                                        new java.awt.event.ActionListener() {
                                          public void actionPerformed(ActionEvent e) {
                                            try {
                                              thejFileChooser.setApproveButtonText("Select");
                                              thejFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                              thejFileChooser.setSelectedFile(new File(jalopyFile.getText()));
                                              thejFileChooser.showOpenDialog(OntologyBeanGeneratorPanel.this);

                                              File f = thejFileChooser.getSelectedFile();

                                              jalopyFile.setText(f.getPath());
                                            } catch (Exception g) {
                                              g.printStackTrace();
                                            }
                                          }
                                        });

    // add the UI omponents to the panel
    jadeLabel.setIcon(this.jadeIcon);
    this.add(mainSplitPane,
             new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    mainSplitPane.setRightComponent(rightPanel);
    mainSplitPane.setLeftComponent(this.leftPanel);
    rightPanel.add(jLabel1, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0
                                                   , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0));
    rightPanel.add(jLabel2, new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0
                                                   , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 5, 0), 0, 0));
    rightPanel.add(jLabel3, new GridBagConstraints(1, 8, 3, 1, 0.0, 0.0
                                                   , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(jLabel4, new GridBagConstraints(1, 6, 3, 1, 0.0, 0.0
                                                   , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 5, 0), 0, 0));
    rightPanel.add(jLabel5, new GridBagConstraints(1, 0, 5, 1, 1.0, 0.0
                                                   , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 0), 0, 0));
    rightPanel.add(jLabel6, new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0
                                                   , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(packageComboBox, new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0
                                                           , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
    rightPanel.add(locationComboBox, new GridBagConstraints(1, 5, 3, 1, 1.0, 0.0
                                                            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
    rightPanel.add(ontologyComboBox, new GridBagConstraints(1, 7, 3, 1, 1.0, 0.0
                                                            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
    rightPanel.add(jLabel7, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                   , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    rightPanel.add(jLabel8, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
                                                   , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    rightPanel.add(jLabel9, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
                                                   , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    rightPanel.add(itsLocateButton, new GridBagConstraints(4, 5, 1, 1, 0.0, 0.0
                                                           , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    rightPanel.add(jadeCheckBox, new GridBagConstraints(2, 11, 1, 1, 0.0, 0.0
                                                        , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(beansCheckBox, new GridBagConstraints(2, 12, 1, 1, 0.0, 0.0
                                                         , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    rightPanel.add(useJadeNamesCheckBox, new GridBagConstraints(2, 13, 1, 1, 0.0, 0.0
                                                                , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    rightPanel.add(doFormatting, new GridBagConstraints(2, 14, 1, 1, 0.0, 0.0
                                                        , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    rightPanel.add(jalopyFile, new GridBagConstraints(2, 15, 1, 1, 0.0, 0.0
                                                      , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    //JEL
    rightPanel.add(fullBeanSupport, new GridBagConstraints(2, 17, 1, 1, 0.0, 0.0
                                                           , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(standardSupport, new GridBagConstraints(2, 18, 1, 1, 0.0, 0.0
                                                           , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(microSupport, new GridBagConstraints(2, 19, 1, 1, 0.0, 0.0
                                                        , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    rightPanel.add(contributors, new GridBagConstraints(0, 21, 4, 1, 0.0, 0.0
                                                        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));

    // end JEL, sort of...

    /*
     *  rightPanel.add(jadeLabel,
     *  new GridBagConstraints(1, 18, 1, 1, 0.0, 0.0,
     *  GridBagConstraints.CENTER,
     *  GridBagConstraints.NONE,
     *  new Insets(0, 0, 0, 0), 0, 0));
     *  rightPanel.add(mleLabel,
     *  new GridBagConstraints(2, 18, 1, 1, 0.0, 0.0,
     *  GridBagConstraints.CENTER,
     *  GridBagConstraints.NONE,
     *  new Insets(0, 0, 0, 0), 0, 0));
     *  rightPanel.add(acklin,
     *  new GridBagConstraints(3, 18, 1, 1, 0.0, 0.0,
     *  GridBagConstraints.CENTER,
     *  GridBagConstraints.NONE,
     *  new Insets(0, 0, 0, 0), 0, 0));
     *  rightPanel.add(uvaLabel,
     *  new GridBagConstraints(4, 18, 1, 1, 0.0, 0.0,
     *  GridBagConstraints.CENTER,
     *  GridBagConstraints.NONE,
     *  new Insets(0, 0, 0, 0), 0, 0));
     */
    mainSplitPane.setDividerLocation(200);
    leftPanel.add(jScrollPane1, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
                                                       , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    leftPanel.add(progress, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
                                                   , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    leftPanel.add(jLabel10, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                   , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(statusTextArea, null);
  }


  void microSupport_stateChanged(ChangeEvent e) {
    if (microSupport.isSelected()) {
      this.exampleTextArea.setText(Examples.getMicroExample());
    }
  }


  private void rightPanel_mouseExited(MouseEvent e) { }


  private void showHTML(String url) {
    WaitCursor wait = new WaitCursor(this);
    String command = getLaunchString(url);
    try {
      Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      e.printStackTrace();
    }
    wait.hide();
  }


  void standardSupport_stateChanged(ChangeEvent e) {
    if (standardSupport.isSelected()) {
      this.exampleTextArea.setText(Examples.getStandardExample());
    }
  }


  private void uvaLabel_mouseClicked(MouseEvent e) {
    this.showHTML("http://www.swi.psy.uva.nl");
  }


  private void uvaLabel_mouseEntered(MouseEvent e) {
    //    handCursor.show();
  }


  private void uvaLabel_mouseExited(MouseEvent e) {
    //    handCursor.hide();
  }


  public OntologyBeanGeneratorPanel(OntologyBeanGeneratorTab model) {
    try {
      this.model = model;
      jbInit();
      fillComboBoxModels();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  private JLabel acklin = new JLabel();
  private ImageIcon acklinIcon = new ImageIcon(this.getClass().getResource("graphics/acklin.jpg"));
  JCheckBox beansCheckBox = new JCheckBox();
  private Border border1;
  JCheckBox doFormatting = new JCheckBox();
  public JTextArea exampleTextArea = new JTextArea();
  private ImageIcon executeIcon = new ImageIcon(this.getClass()
                                                .getResource("graphics/execute.gif"));
  public JRadioButton fullBeanSupport = new JRadioButton();
  // other components
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  private ImageIcon inboxIcon = new ImageIcon(this.getClass().getResource("graphics/inbox.gif"));
  JButton itsGenerateButton = new JButton();
  JButton itsLocateButton = new JButton();
  JButton itsOntologyNameButton = new JButton();
  JButton itsPackageNameButton = new JButton();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel10 = new JLabel();
  JLabel jLabel11 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  private JLabel jLabel7 = new JLabel();
  private JLabel jLabel8 = new JLabel();
  private JLabel jLabel9 = new JLabel();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollPane2 = new JScrollPane();
  // Jamie's things
  JCheckBox jadeCheckBox = new JCheckBox();
  private ImageIcon jadeIcon = new ImageIcon(this.getClass().getResource("graphics/jadelogo.gif"));
  private JLabel jadeLabel = new JLabel();
  JTextField jalopyFile = new JTextField();
  JButton jalopyFileLocator = new JButton();
  JButton jalopyPreferences = new JButton();
  JPanel leftPanel = new JPanel();
  public DefaultComboBoxModel locationBoxModel = new DefaultComboBoxModel();
  JComboBox locationComboBox = new JComboBox();
  JSplitPane mainSplitPane = new JSplitPane();
  public JRadioButton microSupport = new JRadioButton();
  private ImageIcon mleIcon = new ImageIcon(this.getClass().getResource("graphics/mle.gif"));
  JLabel mleLabel = new JLabel();
  private OntologyBeanGeneratorTab model;
  public DefaultComboBoxModel ontologyBoxModel = new DefaultComboBoxModel();
  JComboBox ontologyComboBox = new JComboBox();
  private ImageIcon outputIcon = new ImageIcon(this.getClass().getResource("graphics/output.gif"));
  public DefaultComboBoxModel packageBoxModel = new DefaultComboBoxModel();
  JComboBox packageComboBox = new JComboBox();
  private ImageIcon packageIcon = new ImageIcon(this.getClass()
                                                .getResource("graphics/package.gif"));
  JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL);
  JPanel rightPanel = new JPanel();
  public JRadioButton standardSupport = new JRadioButton();
  JTextArea statusTextArea = new JTextArea();
  public ButtonGroup support = new ButtonGroup();
  JFileChooser thejFileChooser = new JFileChooser();
  private TitledBorder titledBorder1;
  JCheckBox useJadeNamesCheckBox = new JCheckBox();
  private ImageIcon uvaIcon = new ImageIcon(this.getClass().getResource("graphics/uva.gif"));
  private JLabel uvaLabel = new JLabel();
  //  public JCheckBox MIDPCheckBox = new JCheckBox(); //modified by J.Picault


  private class SomeLabelRenderer extends JLabel implements ListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
      if (value != null) {
        String sValue = (String) value;
        setText((String) value);

        //     setIcon(new ImageIcon(getClass().getResource("graphics/start.gif")));
        setText((String) value);
      }
      setBackground(isSelected ? Color.blue : Color.white);
      setForeground(isSelected ? Color.white : Color.black);
      return this;
    }


    public SomeLabelRenderer() {
      setOpaque(true);
      setFont(new java.awt.Font("Dialog", 0, 10));
      // setBackground(Color.blue);
      // setForeground(Color.white);
    }
  }
}

//  ***EOF***
