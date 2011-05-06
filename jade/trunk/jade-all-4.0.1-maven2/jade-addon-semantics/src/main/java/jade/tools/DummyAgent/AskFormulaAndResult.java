/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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

/*
 * AskFormulaAndResult.java
 * Created on 18 oct. 2005
 * Author : Vincent Pautret
 */
package jade.tools.DummyAgent;

import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.filters.FilterKBaseImpl;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class AskFormulaAndResult extends AskFormula {

    JTextArea answerArea = new JTextArea(5,80);
    
    
    public AskFormulaAndResult(String textToBePrompt, String title, String type, String oldValue, DummyAgent agt, DefaultListModel model) {
        super(textToBePrompt, title, type, oldValue, agt, model);
        
        JScrollPane answerPane = new JScrollPane();
        answerPane.getViewport().setView(answerArea);   
        answerArea.setEditable(false);

        answerPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);    
        answerPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        center.add(answerPane, BorderLayout.CENTER);
    }
    
    @Override
	public ActionListener getOkAction() {
        return new OkAct();
    }
    
    @Override
	public void putOtherButtons() {
        
        JButton queryRefButton = new JButton("QueryRef");
        queryRefButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    IdentifyingExpression result;
                    if (nomProjet.getText() != null && !nomProjet.getText().trim().equals("")) {
                        result = (IdentifyingExpression)SL.term(nomProjet.getText());
                    } else {
                        result = null;
                    }
                    if (result != null) {
                           ListOfTerm list = ((FilterKBaseImpl)myAgent.getSemanticCapabilities().getMyKBase()).queryRef(result);
                           answerArea.setText("");
                           if (list != null && list.size() !=0) {
                               answerArea.setText(list.toString());
                           }
                           else if (list != null && list.size() ==0) answerArea.setText("list of terms empty");
                           else answerArea.setText("null");
                    } else {
                        answerArea.setText("Problem");
                    }
                } catch (NullPointerException npe) {
                    answerArea.setText("Check the formula");
                }
            }
        });     
        buttonPanel.add(queryRefButton);
        
        JButton executeQuery = new JButton();
        executeQuery.setText("Query-File");
        executeQuery.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    JFileChooser chooser = new JFileChooser();
                    int returnVal = chooser.showOpenDialog(AskFormulaAndResult.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        String line;
                        try {
                            java.io.BufferedReader reader = new BufferedReader(new FileReader(file));
                            answerArea.setText("");
                            while ( (line = reader.readLine()) != null ) {
                                
                                if ( !line.equals("") && !line.startsWith("//")) {
                                    try {
                                        if (line != null && !line.trim().equals("")) {
                                            answerArea.append("Query on : " + line +"\n");
                                            Formula f = SL.formula(line);
                                            QueryResult result = myAgent.getSemanticCapabilities().getMyKBase().query(f);
                                            if (result != null) {
                                                answerArea.append(result.toString()+"\n");
                                            } else {
                                                answerArea.append("null\n");
                                            }
                                            
                                        } else {
                                            answerArea.append("Check the formula\n");
                                        }
                                        answerArea.append("----------------------\n");
                                    } catch (NullPointerException npe) {
                                        answerArea.append("Check the formula\n");
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(AskFormulaAndResult.this,
                                    "Bad file!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                    }
                }
        });
        buttonPanel.add(executeQuery);
        
        JButton executeQueryRef = new JButton();
        executeQueryRef.setText("QueryRef-File");
        executeQueryRef.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    JFileChooser chooser = new JFileChooser();
                    int returnVal = chooser.showOpenDialog(AskFormulaAndResult.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        String line;
                        try {
                            java.io.BufferedReader reader = new BufferedReader(new FileReader(file));
                            answerArea.setText("");
                            while ( (line = reader.readLine()) != null ) {
                                
                                if ( !line.equals("") && !line.startsWith("//")) {
                                    try {
                                        IdentifyingExpression result;
                                        if (line != null && !line.trim().equals("")) {
                                            result = (IdentifyingExpression)SL.term(line);
                                        } else {
                                            result = null;
                                        }
                                        if (result != null) {
                                            answerArea.append("QueryRef on : " + line + "\n");
                                               ListOfTerm list = ((FilterKBaseImpl)myAgent.getSemanticCapabilities().getMyKBase()).queryRef(result);
                                               if (list != null && list.size() != 0) {
                                                   answerArea.append(list.toString()+"\n");
                                               }
                                               else if (list != null && list.size() ==0) answerArea.append("list of terms empty\n");
                                               else answerArea.append("null\n");
                                        } else {
                                            answerArea.append("Check the formula\n");
                                        }
                                    } catch (NullPointerException npe) {
                                        answerArea.append("Check the formula\n");
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(AskFormulaAndResult.this,
                                    "Bad file!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                    }
                }
        });
        buttonPanel.add(executeQueryRef);
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                answerArea.setText(""); 
            }
        });     
        buttonPanel.add(clearButton);
    }

    
    public class OkAct implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                answerArea.setText("");
                if (nomProjet.getText() != null && !nomProjet.getText().trim().equals("")) {
                    Formula f = SL.formula(nomProjet.getText());
                    ArrayList reasons = new ArrayList();
                    QueryResult result = myAgent.getSemanticCapabilities().getMyKBase().query(f, reasons);
                    if (result != null) {
                        answerArea.setText(result.toString());
                    }
                    else {
                    	answerArea.setText("UNKOWN, because:\n" + reasons.toString());
                    }
                    
                } else {
                    answerArea.setText("Problem");
                }
            } catch (NullPointerException npe) {
                answerArea.setText("Check the formula");
            }
        }
    }
    
    @Override
	public void addFile() {
    }
    @Override
	public void setOKButtonTitle() {
        okButton.setText("Query");
    }
}
