/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.tools.persistence;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.Collections;

import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JFileChooser;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;


import jade.core.ServiceException;
import jade.core.persistence.PersistenceManager;
import jade.core.persistence.Repository;

import jade.util.leap.List;
import jade.util.leap.LinkedList;

/**
 *
 * @author  Giovanni Rimassa - FRAMeTech s.r.l.
 */
public class MetaDBWindow extends javax.swing.JInternalFrame implements GUIConstants {

    private class RepositoryDataModel {

        public TableModel getMappingsTableModel() {
            return mappingsModel;
        }

        public TableModel getPropertiesTableModel() {
            return propertiesModel;
        }

        public void setData(Repository rep) {
            data = rep;
            showPropertyTable();

            // Trigger table models change
            mappingsModel.fireTableDataChanged();
            propertiesModel.fireTableDataChanged();
        }
        
        public Repository getData() {
            return data;
        }

        public void addMapping(String mapping) {
            data.getMappings().add(mapping);
            mappingsModel.fireTableDataChanged();
        }

        public void removeMapping(int idx) {
            data.getMappings().remove(idx);
            mappingsModel.fireTableDataChanged();
        }

        public boolean hasInternalProperties() {
            Repository.Properties props = data.getProperties();
            return props instanceof Repository.StoredProperties;
        }
        
        public void addProperty(String name, String value) {
            Repository.StoredProperties props = (Repository.StoredProperties)data.getProperties();
            java.util.Map values = props.getValues();

            values.put(name, value);
            propertiesModel.fireTableDataChanged();
        }

        public void removeProperty(int idx) {
            String key = (String)propertiesModel.getValueAt(idx, 0);
            Repository.StoredProperties props = (Repository.StoredProperties)data.getProperties();
            java.util.Map values = props.getValues();
            
            values.remove(key);
            propertiesModel.fireTableDataChanged();
        }

        public void replaceProperties(Repository.Properties props) {
            showPropertyTable();
            data.setProperties(props);
            propertiesModel.fireTableDataChanged();
        }

        public void replaceProperties(String newURL) {
            showPropertyTable();
            Repository.ExternalProperties props = (Repository.ExternalProperties)data.getProperties();
            if(!newURL.equals(props.getURL())) {
                props.setURL(newURL);
                propertiesModel.fireTableDataChanged();
            }
        }

        public String getPropertiesURL() {
            Repository.Properties props = data.getProperties();
            if(props instanceof Repository.ExternalProperties) {
                Repository.ExternalProperties extProps = (Repository.ExternalProperties)props;
                return extProps.getURL();
            }
            else {
                return "";
            }
        }
        
        private Repository data;

        private AbstractTableModel mappingsModel = new AbstractTableModel() {

            public int getRowCount() {
                if(data == null) {
                    return 0;
                }
                else {
                    return data.getMappings().size();
                }
            }
            
            public int getColumnCount() {
                return 1;
            }
            
            public String getColumnName(int column) {
                return "Mapping URL";
            }

            public Class getColumnClass(int columnIndex) {
                return String.class;
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            public Object getValueAt(int row, int column) {
                switch(column) {
                case 0: 
                    return (String)data.getMappings().get(row);
                default:
                    return null;
                }
            }

            public void setValueAt(Object value, int row, int column) {
                if(column == 0) {
                    data.getMappings().set(row, value);
                }
            }

        };

        private AbstractTableModel propertiesModel = new AbstractTableModel() {

            public int getRowCount() {
                return sortedKeys.size();
            }

            public int getColumnCount() {
                return 2;
            }
            
            public String getColumnName(int column) {
                switch(column) {
                    case 0:
                        return "Property Name";
                    case 1:
                        return "Property Value";
                    default:
                        return null;
                }
            }

            public Class getColumnClass(int columnIndex) {
                return String.class;
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if(data == null) {
                    return false;
                }
                else {
                    return data.getProperties() instanceof Repository.StoredProperties;
                }
            }

            public Object getValueAt(int row, int column) {
                String key = (String)sortedKeys.get(row);

                try {
                    switch(column) {
                    case 0:
                        return key;
                    case 1:
                        java.util.Properties props = data.getProperties().getProperties();
                        return (String)props.getProperty(key);
                    default:
                        return null;
                    }
                }
                catch(IOException ioe) {
                    showPropertyLoadError();
                    return null;
                }
            }

            public void setValueAt(Object value, int row, int column) {
                String key = (String)sortedKeys.get(row);
                Repository.StoredProperties props = (Repository.StoredProperties)data.getProperties();
                java.util.Map values = props.getValues();

                if(column == 0) {
                    String propValue = (String)values.get(key);
                    values.remove(key);
                    values.put(value, propValue);
                }
                else if(column == 1) {
                    values.put(key, value);
                }
                else {
                    return;
                }

                fireTableDataChanged();
            }

            public void fireTableDataChanged() {
                // Recompute the sorted keys
                Repository.Properties props = data.getProperties();
                if(props == null) {
                    sortedKeys = Collections.EMPTY_LIST;
                }
                else {
                    try {
                        sortedKeys.clear();
                        sortedKeys.addAll(props.getProperties().keySet());
                        Collections.sort(sortedKeys);
                    }
                    catch(IOException ioe) {
                        showPropertyLoadError();
                    }
                }

                // Notify listeners
                super.fireTableDataChanged();
            }

            private java.util.List sortedKeys = new java.util.LinkedList();

        };


    } // End of RepositoryTableModel class


    /** Creates new form MetaDBWindow */
    public MetaDBWindow(String alias, String url, ActionProcessor ap) {
        myAlias = alias;
        myURL = url;
        myActionProcessor = ap;
        currentSelection = new MetaDBWindow.RepositoryDataModel();

        glass = new javax.swing.JComponent() {
            public void paint(Graphics g) {
                g.setColor(glassColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        glass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Toolkit.getDefaultToolkit().beep();
            }
        });

        originalGlassPane = getRootPane().getGlassPane();

        initComponents();

        // Remove the text from the toolbar buttons
        Component[] buttons = repositoryToolBar.getComponents();
        for(int i = 0; i < buttons.length; i++) {
            Component c = buttons[i];
            if(c instanceof JButton) {
                JButton b = (JButton)c;
                b.setText(null);
            }
        }

        DefaultCellEditor mappingsCellEditor = new DefaultCellEditor(new JTextField());
        mappingsCellEditor.setClickCountToStart(1);

        DefaultCellEditor propertiesCellEditor = new DefaultCellEditor(new JTextField());
        propertiesCellEditor.setClickCountToStart(1);

        mappingsCellEditor.addCellEditorListener(new CellEditorListener() {

            public void editingCanceled(ChangeEvent e) {
                // Do nothing
            }
            
            public void editingStopped(ChangeEvent e) {
                try {
                    myPersistenceManager.saveRepository(currentSelection.getData());
                }
                catch(ServiceException se) {
                    notifyException(se, "Error during repository saving");
                }
            }
        });

        propertiesCellEditor.addCellEditorListener(new CellEditorListener() {

            public void editingCanceled(ChangeEvent e) {
                // Do nothing
            }

            public void editingStopped(ChangeEvent e) {
                try {
                    myPersistenceManager.saveRepository(currentSelection.getData());
                }
                catch(ServiceException se) {
                    notifyException(se, "Error during repository saving");
                }
            }
        });

        mappingsTable.setDefaultEditor(String.class, mappingsCellEditor);
        propertiesTable.setDefaultEditor(String.class, propertiesCellEditor);


        // Show no repository
        showRepository(null);

        // Observe the selection of the repository list
        repositoriesList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                String sel = (String)repositoriesList.getSelectedValue();
                showRepository(sel);
            }
        });

    }

    public void activate() {
        setVisible(true);
        connect();
    }

    public void deactivate() {
        if(myPersistenceManager != null) {
            myPersistenceManager.destroy();
        }
        setVisible(false);
        dispose();
    }

    public void addRepository(String name) {
        try {
            myPersistenceManager.addRepository(name);
            fillRepositoryList();
        }
        catch(Exception e) {
            notifyException(e, "Error during repository addition");
        }
    }

    public void removeRepository() {
        try {
            String name = (String)repositoriesList.getSelectedValue();
            if(name == null) {
                throw new IllegalArgumentException("No repository is selected");
            }
            else {
                myPersistenceManager.removeRepository(name);
                fillRepositoryList();
            }
        }
        catch(Exception e) {
            notifyException(e, "Error during repository removal");
        }
    }

    public void addNewMapping() {
        currentSelection.addMapping("");
    }

    public void removeSelectedMapping() {
        int idx = mappingsTable.getSelectedRow();
        if(idx != -1) {
            currentSelection.removeMapping(idx);
        }
    }

    public void addNewProperty() {
        String name = "Name-0";
        int i = 0;
        Repository.StoredProperties props = (Repository.StoredProperties)currentSelection.getData().getProperties();
        java.util.Map values = props.getValues();
        while(values.containsKey(name)) {
            i++;
            name = "Name-" + i;
        }

        currentSelection.addProperty(name, "");
    }

    public void removeSelectedProperty() {
        int idx = propertiesTable.getSelectedRow();
        if(idx != -1) {
            currentSelection.removeProperty(idx);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        javax.swing.JPanel bottomSpace;
        javax.swing.JPanel topSpace;

        propertiesStorageGroup = new javax.swing.ButtonGroup();
        repositoryToolBar = new javax.swing.JToolBar();
        addRepositoryButton = new javax.swing.JButton();
        removeRepositoryButton = new javax.swing.JButton();
        addMappingButton = new javax.swing.JButton();
        removeMappingButton = new javax.swing.JButton();
        addPropertyButton = new javax.swing.JButton();
        removePropertyButton = new javax.swing.JButton();
        bodySplit = new javax.swing.JSplitPane();
        repositoriesPanel = new javax.swing.JPanel();
        repositoriesScroll = new javax.swing.JScrollPane();
        repositoriesList = new javax.swing.JList();
        selectionDeck = new javax.swing.JPanel();
        repositoryPanel = new javax.swing.JPanel();
        repositorySplitPane = new javax.swing.JSplitPane();
        mappingsTableScroll = new javax.swing.JScrollPane();
        mappingsTable = new javax.swing.JTable();
        propertiesPanel = new javax.swing.JPanel();
        propKindPanel = new javax.swing.JPanel();
        firstRowPanel = new javax.swing.JPanel();
        storedRadio = new javax.swing.JRadioButton();
        secondRowPanel = new javax.swing.JPanel();
        externalRadio = new javax.swing.JRadioButton();
        propsUrlPanel = new javax.swing.JPanel();
        topSpace = new javax.swing.JPanel();
        propsUrlField = new javax.swing.JTextField();
        bottomSpace = new javax.swing.JPanel();
        browseButtonPanel = new javax.swing.JPanel();
        browseButton = new javax.swing.JButton();
        propertiesDeck = new javax.swing.JPanel();
        propertiesTableScroll = new javax.swing.JScrollPane();
        propertiesTable = new javax.swing.JTable();
        loadErrorLabel = new javax.swing.JLabel();
        nothingLabel = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(myAlias + " - [" + myURL + "]");
        repositoryToolBar.setFloatable(false);
        repositoryToolBar.setRollover(true);
        addRepositoryButton.setAction(myActionProcessor.getAction(ACTION_ADDREPOSITORY));
        addRepositoryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/addRepository.gif")));
        repositoryToolBar.add(addRepositoryButton);

        removeRepositoryButton.setAction(myActionProcessor.getAction(ACTION_REMOVEREPOSITORY));
        removeRepositoryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/removeRepository.gif")));
        repositoryToolBar.add(removeRepositoryButton);

        addMappingButton.setAction(myActionProcessor.getAction(ACTION_ADDMAPPING));
        addMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/addMapping.gif")));
        repositoryToolBar.add(addMappingButton);

        removeMappingButton.setAction(myActionProcessor.getAction(ACTION_REMOVEMAPPING));
        removeMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/removeMapping.gif")));
        repositoryToolBar.add(removeMappingButton);

        addPropertyButton.setAction(myActionProcessor.getAction(ACTION_ADDPROPERTY));
        addPropertyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/addProperty.gif")));
        repositoryToolBar.add(addPropertyButton);

        removePropertyButton.setAction(myActionProcessor.getAction(ACTION_REMOVEPROPERTY));
        removePropertyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/removeProperty.gif")));
        repositoryToolBar.add(removePropertyButton);

        getContentPane().add(repositoryToolBar, java.awt.BorderLayout.NORTH);

        bodySplit.setDividerLocation(125);
        bodySplit.setDividerSize(2);
        bodySplit.setContinuousLayout(true);
        repositoriesPanel.setLayout(new java.awt.BorderLayout());

        repositoriesPanel.setBorder(new javax.swing.border.TitledBorder(null, "Defined Repositories", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10)));
        repositoriesScroll.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        repositoriesList.setModel(repositoryListModel);
        repositoriesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        repositoriesScroll.setViewportView(repositoriesList);

        repositoriesPanel.add(repositoriesScroll, java.awt.BorderLayout.CENTER);

        bodySplit.setLeftComponent(repositoriesPanel);

        selectionDeck.setLayout(new java.awt.CardLayout());

        repositoryPanel.setLayout(new java.awt.BorderLayout());

        repositoryPanel.setBorder(new javax.swing.border.TitledBorder(null, "Name of the selected repository", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        repositorySplitPane.setDividerLocation(50);
        repositorySplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        repositorySplitPane.setContinuousLayout(true);
        repositorySplitPane.setOneTouchExpandable(true);
        mappingsTableScroll.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mappingsTableScroll.setViewportBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        mappingsTable.setModel(currentSelection.getMappingsTableModel());
        mappingsTableScroll.setViewportView(mappingsTable);

        repositorySplitPane.setTopComponent(mappingsTableScroll);

        propertiesPanel.setLayout(new java.awt.BorderLayout());

        propKindPanel.setLayout(new javax.swing.BoxLayout(propKindPanel, javax.swing.BoxLayout.Y_AXIS));

        propKindPanel.setBorder(new javax.swing.border.TitledBorder(null, "Properties Storage", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
        firstRowPanel.setLayout(new java.awt.GridLayout(1, 1));

        storedRadio.setFont(new java.awt.Font("Dialog", 1, 10));
        storedRadio.setSelected(true);
        storedRadio.setText("Internally Stored");
        storedRadio.setToolTipText("Properties are read from the Meta-DB itself");
        propertiesStorageGroup.add(storedRadio);
        storedRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                storedRadioClicked(evt);
            }
        });

        firstRowPanel.add(storedRadio);

        propKindPanel.add(firstRowPanel);

        secondRowPanel.setLayout(new javax.swing.BoxLayout(secondRowPanel, javax.swing.BoxLayout.X_AXIS));

        externalRadio.setFont(new java.awt.Font("Dialog", 1, 10));
        externalRadio.setText("Stored at URL");
        externalRadio.setToolTipText("Properties are read from an external resource");
        propertiesStorageGroup.add(externalRadio);
        externalRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                externalRadioClicked(evt);
            }
        });

        secondRowPanel.add(externalRadio);

        propsUrlPanel.setLayout(new javax.swing.BoxLayout(propsUrlPanel, javax.swing.BoxLayout.Y_AXIS));

        propsUrlPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(2, 2, 2, 2)));
        propsUrlPanel.add(topSpace);

        propsUrlField.setColumns(40);
        propsUrlField.setFont(new java.awt.Font("Dialog", 0, 10));
        propsUrlField.setEnabled(false);
        propsUrlField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propsUrlFieldActionPerformed(evt);
            }
        });
        propsUrlField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                propsUrlFieldFocusLost(evt);
            }
        });

        propsUrlPanel.add(propsUrlField);

        propsUrlPanel.add(bottomSpace);

        secondRowPanel.add(propsUrlPanel);

        browseButtonPanel.setLayout(new javax.swing.BoxLayout(browseButtonPanel, javax.swing.BoxLayout.X_AXIS));

        browseButtonPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(2, 5, 2, 2)));
        browseButton.setFont(new java.awt.Font("Dialog", 1, 10));
        browseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jade/tools/persistence/images/open.gif")));
        browseButton.setToolTipText("Browse Files...");
        browseButton.setBorder(new javax.swing.border.EtchedBorder());
        browseButton.setEnabled(false);
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        browseButtonPanel.add(browseButton);

        secondRowPanel.add(browseButtonPanel);

        propKindPanel.add(secondRowPanel);

        propertiesPanel.add(propKindPanel, java.awt.BorderLayout.NORTH);

        propertiesDeck.setLayout(new java.awt.CardLayout());

        propertiesTableScroll.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        propertiesTable.setModel(currentSelection.getPropertiesTableModel());
        propertiesTableScroll.setViewportView(propertiesTable);

        propertiesDeck.add(propertiesTableScroll, "Properties");

        loadErrorLabel.setFont(new java.awt.Font("Dialog", 0, 14));
        loadErrorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loadErrorLabel.setText("<Error loading properties>");
        loadErrorLabel.setEnabled(false);
        propertiesDeck.add(loadErrorLabel, "Error");

        propertiesPanel.add(propertiesDeck, java.awt.BorderLayout.CENTER);

        repositorySplitPane.setRightComponent(propertiesPanel);

        repositoryPanel.add(repositorySplitPane, java.awt.BorderLayout.CENTER);

        selectionDeck.add(repositoryPanel, "Repository");

        nothingLabel.setFont(new java.awt.Font("Dialog", 0, 14));
        nothingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nothingLabel.setText("<No Repository Selected>");
        nothingLabel.setEnabled(false);
        selectionDeck.add(nothingLabel, "Nothing");

        bodySplit.setRightComponent(selectionDeck);

        getContentPane().add(bodySplit, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed

        int returnVal = propsFileChooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            propsUrlField.setText(propsFileChooser.getSelectedFile().toURI().toString());
            updatePropertiesURL();
        }

    }//GEN-LAST:event_browseButtonActionPerformed

    private void propsUrlFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propsUrlFieldActionPerformed
        updatePropertiesURL();
    }//GEN-LAST:event_propsUrlFieldActionPerformed

    private void externalRadioClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_externalRadioClicked
        if(currentSelection.hasInternalProperties()) {

            int choice = JOptionPane.showInternalConfirmDialog(this,
                new String[] { "<html><u>This will clear all the stored properties</u>.</html>", "Do you want to continue?" },
                "Confirm a destructive operation",
                JOptionPane.YES_NO_OPTION);
            if(choice == JOptionPane.YES_OPTION) {
                // Actually perform the action and clear the properties
                propsUrlField.setEnabled(true);
                browseButton.setEnabled(true);
                currentSelection.replaceProperties(new Repository.ExternalProperties());
                try {
                    myPersistenceManager.saveRepository(currentSelection.getData());
                }
                catch(ServiceException se) {
                    notifyException(se, "Error during repository storage");
                }
            }
            else {
                // Abort the action and restore the previous selected state,
                // but avoid executing the listener again
                storedRadio.setSelected(true);
            }
        }
    }//GEN-LAST:event_externalRadioClicked

    private void storedRadioClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storedRadioClicked
        if(!currentSelection.hasInternalProperties()) {
            propsUrlField.setText("");
            propsUrlField.setEnabled(false);
            browseButton.setEnabled(false);
            Repository.StoredProperties props = new Repository.StoredProperties();
            props.setValues(myPersistenceManager.getDefaultPropertyValues());
            currentSelection.replaceProperties(props);
            try {
                myPersistenceManager.saveRepository(currentSelection.getData());
            }
            catch(ServiceException se) {
                notifyException(se, "Error during repository storage");
            }
        }
    }//GEN-LAST:event_storedRadioClicked

    private void propsUrlFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_propsUrlFieldFocusLost
        updatePropertiesURL();
    }//GEN-LAST:event_propsUrlFieldFocusLost

    private void updatePropertiesURL() {
        currentSelection.replaceProperties(propsUrlField.getText());
        try {
            myPersistenceManager.saveRepository(currentSelection.getData());
        }
        catch(ServiceException se) {
            notifyException(se, "Error during repository storage");
        }
    }
    
    private void shade() {
        getRootPane().setGlassPane(glass);
        glass.setVisible(true);
    }
    
    private void unshade() {
        getRootPane().setGlassPane(originalGlassPane);
        originalGlassPane.setVisible(false);
    }

    private void connect() {

        Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    myPersistenceManager = new PersistenceManager(myURL, null);

                    // OK -- Run the completion in the Event Dispatcher thread
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            fillRepositoryList();
                            unshade();
                        }
                    });
                }
                catch(final Exception e) {

                    // KO -- Run the exception notification in the Event Dispatcher thread
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            glassColor = errorGlassColor;
                            notifyException(e, "Error during connection");
                            deactivate();
                        }
                    });
                }
            }
        });

        glassColor = busyGlassColor;
        shade();
        t.start();
    }
    
    private void fillRepositoryList() {
        try {
            Repository[] repos = myPersistenceManager.getRepositories();
            // Fill the list model with the data
            repositoryListModel.clear();
            for(int i = 0; i < repos.length; i++) {
                repositoryListModel.addElement(repos[i].getName());
            }
        }
        catch(ServiceException se) {
            notifyException(se, "Error during repositories loading");
        }
    }

    private void showRepository(String name) {
        try {
            CardLayout cl = (CardLayout)selectionDeck.getLayout();
            if(name != null) {
                Repository rep = myPersistenceManager.getRepository(name);
                currentSelection.setData(rep);
                TitledBorder tb = (TitledBorder)repositoryPanel.getBorder();
                tb.setTitle(" " + name + " ");

                boolean internal = currentSelection.hasInternalProperties();
                storedRadio.setSelected(internal);
                externalRadio.setSelected(!internal);

                propsUrlField.setEnabled(!internal);
                browseButton.setEnabled(!internal);
                propsUrlField.setText(currentSelection.getPropertiesURL());
                
                cl.show(selectionDeck, "Repository");
                repositoryPanel.repaint();
            }
            else {
                cl.show(selectionDeck, "Nothing");
            }
        }
        catch(Exception e) {
            notifyException(e, "Error during repository retrieval");
        }
    }

    private void showPropertyTable() {
        CardLayout layout = (CardLayout)propertiesDeck.getLayout();
        layout.show(propertiesDeck, "Properties");
    }

    private void showPropertyLoadError() {
        CardLayout layout = (CardLayout)propertiesDeck.getLayout();
        layout.show(propertiesDeck, "Error");
    }

    private void notifyException(Throwable t, String title) {
        String exceptionMsg = (t.getMessage() != null) ? t.getMessage() : "";
        List messageRows = new LinkedList();
        messageRows.add("Error message:");
        messageRows.add(t.getClass().getName() + " [");
        int ROW_LENGTH = 80;
        int rows = (exceptionMsg.length() / ROW_LENGTH) + 1;
        for(int i = 0; i < rows; i++) {
            int endIdx = Math.min(ROW_LENGTH*(i+1), exceptionMsg.length());
            messageRows.add("  " + exceptionMsg.substring(ROW_LENGTH*i, endIdx));
        }
        messageRows.add("]");
        JOptionPane.showInternalMessageDialog(this, messageRows.toArray(), title, JOptionPane.ERROR_MESSAGE);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMappingButton;
    private javax.swing.JButton addPropertyButton;
    private javax.swing.JButton addRepositoryButton;
    private javax.swing.JSplitPane bodySplit;
    private javax.swing.JButton browseButton;
    private javax.swing.JPanel browseButtonPanel;
    private javax.swing.JRadioButton externalRadio;
    private javax.swing.JPanel firstRowPanel;
    private javax.swing.JLabel loadErrorLabel;
    private javax.swing.JTable mappingsTable;
    private javax.swing.JScrollPane mappingsTableScroll;
    private javax.swing.JLabel nothingLabel;
    private javax.swing.JPanel propKindPanel;
    private javax.swing.JPanel propertiesDeck;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.ButtonGroup propertiesStorageGroup;
    private javax.swing.JTable propertiesTable;
    private javax.swing.JScrollPane propertiesTableScroll;
    private javax.swing.JTextField propsUrlField;
    private javax.swing.JPanel propsUrlPanel;
    private javax.swing.JButton removeMappingButton;
    private javax.swing.JButton removePropertyButton;
    private javax.swing.JButton removeRepositoryButton;
    private javax.swing.JList repositoriesList;
    private javax.swing.JPanel repositoriesPanel;
    private javax.swing.JScrollPane repositoriesScroll;
    private javax.swing.JPanel repositoryPanel;
    private javax.swing.JSplitPane repositorySplitPane;
    private javax.swing.JToolBar repositoryToolBar;
    private javax.swing.JPanel secondRowPanel;
    private javax.swing.JPanel selectionDeck;
    private javax.swing.JRadioButton storedRadio;
    // End of variables declaration//GEN-END:variables

    private DefaultListModel repositoryListModel = new DefaultListModel();
    private JFileChooser propsFileChooser = new JFileChooser();

    // The semi-transparent glass pane used to disable the window during long operations
    private JComponent glass;
    private Color glassColor;
    private Color busyGlassColor = new Color(0, 128, 128, 64);
    private Color errorGlassColor = new Color(255, 0 , 0, 64);

    // The original, transparent glass pane
    private Component originalGlassPane;

    // The action processor used to execute tasks
    private ActionProcessor myActionProcessor;
    
    // The alias used for this Meta-DB
    private String myAlias;

    // The actual URL for this Meta-DB
    private String myURL;

    // The Data Model used to display the currently selected Repository
    private MetaDBWindow.RepositoryDataModel currentSelection;
    
    // The persistence manager engine
    private PersistenceManager myPersistenceManager;

}
