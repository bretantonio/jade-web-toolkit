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
 * AskFormula.java
 * Created on 17 oct. 2005
 * Author : Vincent Pautret
 */
package jade.tools.DummyAgent;

/**
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */

import jade.semantics.kbase.filters.FilterKBaseImpl;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.Iterator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Windows to specify a formula.
 */
public class AskFormula extends JDialog {
    
    /**
     * The formula
     */
    Formula result;
    
    JPanel page;
    
    JButton okButton;
    /**
     * Le Field permettant de récupérer la chaîne
     */
    JTextField nomProjet;

    JPanel center;
    
    String message;
    
    JPanel buttonPanel;
    DummyAgent myAgent;
    
    DefaultListModel listModel;
/******************************************************************************/
/**                             CONSTRUCTEUR                                ***/
/******************************************************************************/
    
    /**
     * Constructeur de la fenêtre.
     * @param textToBePrompt chaîne correspondant à la demande.
     * @param title le titre de la fenêtre
     */
    public AskFormula(String textToBePrompt, String title, String type, String oldValue, DummyAgent agt, DefaultListModel model) { 
        
        super(new Frame(), false);
        
        myAgent = agt;
        listModel = model;
        nomProjet  = new JTextField();
        nomProjet.setPreferredSize(new Dimension(270,20));
        page = new JPanel(new BorderLayout());
        
        JLabel prompt = new JLabel(textToBePrompt);
        
        JPanel giveName = new JPanel();
        
        initData(type, oldValue);
        
        JPanel topButtonPanel = new JPanel();
        JButton bButton = new JButton("B");
        bButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("B", 3, true);
           }
        });
        JButton iButton = new JButton("I");
        iButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("I", 3, true);
           }
        });
        JButton notButton = new JButton("not");
        notButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("not", 5, true);
           }
        });
        JButton aidButton = new JButton("AID");
        aidButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String name = myAgent.getSemanticCapabilities().getAgentName().toString();
               addText(name, name.length() + 2 , false);
           }
        });

        
        JButton andButton = new JButton("and");
        andButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("and", 5, true);
           }
        });

        JButton orButton = new JButton("or");
        orButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("or", 4, true);
           }
        });
        JButton equalButton = new JButton("=");
        equalButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("=", 3, true);
           }
        });

        JButton clearFormulaButton = new JButton("clear");
        clearFormulaButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               nomProjet.setText("");
           }
        });
        
        topButtonPanel.add(clearFormulaButton);
        topButtonPanel.add(Box.createRigidArea(new Dimension(20,0)));
        topButtonPanel.add(bButton);
        topButtonPanel.add(iButton);
        topButtonPanel.add(aidButton);
        topButtonPanel.add(notButton);
        topButtonPanel.add(andButton);
        topButtonPanel.add(orButton);
        topButtonPanel.add(equalButton);

        
        JButton iotaButton = new JButton("iota");
        iotaButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("iota", 6, true);
           }
        });

        JButton anyButton = new JButton("any");
        anyButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("any", 5, true);
           }
        });

        JButton allButton = new JButton("all");
        allButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("all", 5, true);
           }
        });
        JButton someButton = new JButton("some");
        someButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("some", 6, true);
           }
        });

        JPanel top2ButtonPanel = new JPanel();
        top2ButtonPanel.add(iotaButton);
        top2ButtonPanel.add(anyButton);
        top2ButtonPanel.add(allButton);
        top2ButtonPanel.add(someButton);

        
        JButton setButton = new JButton("set");
        setButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("set", 5, true);
           }
        });
        
        JButton sequenceButton = new JButton("sequence");
        sequenceButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("sequence", 10, true);
           }
        });
        
        JButton existsButton = new JButton("exists");
        existsButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("exists", 8, true);
           }
        });
        
        JButton forallButton = new JButton("forall");
        forallButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("forall", 8, true);
           }
        });
        JPanel top3ButtonPanel = new JPanel();
        top3ButtonPanel.add(setButton);
        top3ButtonPanel.add(sequenceButton);
        top3ButtonPanel.add(existsButton);
        top3ButtonPanel.add(forallButton);
        
        JButton xButton = new JButton("?x");
        xButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("?x", 4, false);
           }
        });
        
        JButton yButton = new JButton("?y");
        yButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("?y", 4, false);
           }
        });
        
        JButton zButton = new JButton("?z");
        zButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("?z", 4, false);
           }
        });
        JButton pButton = new JButton("p");
        pButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("p", 3, true);
           }
        });
        
        JButton qButton = new JButton("q");
        qButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               addText("q", 3, true);
           }
        });
        JPanel top4ButtonPanel = new JPanel();
        
        top4ButtonPanel.add(xButton);
        top4ButtonPanel.add(yButton);
        top4ButtonPanel.add(zButton);
        top4ButtonPanel.add(pButton);
        top4ButtonPanel.add(qButton);
        
        giveName.setLayout(new BoxLayout(giveName, BoxLayout.X_AXIS));
        giveName.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        giveName.add(Box.createHorizontalGlue());
        giveName.add(prompt);
        giveName.add(Box.createRigidArea(new Dimension(10, 0)));
        giveName.add(nomProjet);
        
//        JPanel textPanel = new JPanel();
//        textPanel.setLayout(new BoxLayout(textPanel,BoxLayout.X_AXIS));
//        textPanel.add(Box.createRigidArea(new Dimension(0,10)));
//        textPanel.add(giveName);
//        text.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        okButton = new JButton();
        setOKButtonTitle();
        okButton.addActionListener(getOkAction());
        
        //nomProjet.addActionListener(getOkAction());
        JButton cancelButton = new JButton("Close");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                result = null;
                AskFormula.this.dispose(); 
            }
        });     

        buttonPanel = new JPanel();
       // buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
        buttonPanel.add(okButton);
        addFile();
        putOtherButtons();
        buttonPanel.add(cancelButton);
        
        
        JPanel bo = new JPanel();
        //bo.setLayout(new BoxLayout(bo, BoxLayout.X_AXIS));
        bo.add(giveName);
        bo.add(buttonPanel);
        
        center = new JPanel(new BorderLayout());
        center.add(giveName, BorderLayout.NORTH);
        center.add(buttonPanel,BorderLayout.SOUTH);
        
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(topButtonPanel);
        north.add(top2ButtonPanel);
        north.add(top3ButtonPanel);
        north.add(top4ButtonPanel);
        page.add(north, BorderLayout.NORTH);
        page.add(center,BorderLayout.CENTER);
        
       // page.add(Box.createRigidArea(new Dimension(20,10)),BorderLayout.CENTER);
        //page.add(buttonPanel,BorderLayout.SOUTH);
        
        this.setTitle(title + " - " + myAgent.getSemanticCapabilities().getAgentName());

        
    } // Fin du constructeur AskName/1

    public void showWindow() {
        Container contentPaneFrame = this.getContentPane();
        contentPaneFrame.add(page, BorderLayout.CENTER);
        this.setLocation(0,300);
        this.pack();
        this.setVisible(true);
    }
    
    /**
     * Récupère le nom entré
     * @return le nom entré par l'utilisateur
     */
    public Formula getResult() {
        return result;
    } // Fin de la méthode getResult/0

    private void initData(String type, String oldValue) {
        if (oldValue != null) {
            nomProjet.setText(oldValue);
        }
        message = "a formula";
    }
    
    public ActionListener getOkAction() {
        return new OkAction();
    }
    
    
    public class OkAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (nomProjet.getText() != null && !nomProjet.getText().trim().equals("")) {
                result = SL.formula(nomProjet.getText());
            } else {
                result = null;
            }
            if (result != null) {
                   ((FilterKBaseImpl)myAgent.getSemanticCapabilities().getMyKBase()).assertFormula(result);
                   updateGuiKBList();
            }
            //AskFormula.this.dispose();
        }
    }
    
//    public void updateGuiKBList() {
//        String[] base = myAgent.getSemanticCapabilities().getMyKBase().toStrings();
//        listModel.clear();
//        for (int i = 0; i < base.length; i++) {
//            if (myAgent.getSemanticCapabilities().getMyKBase().isClosed(SL.formula(base[i]),null)) {
//                listModel.addElement(new MsgLog(base[i],Color.red));
//            } else {
//                listModel.addElement(new MsgLog(base[i],Color.black));
//            }
//        }
//    }
    // modified 29/01/08 Carole Adam
    public void updateGuiKBList() {
        listModel.clear();
        for (Iterator i = myAgent.getSemanticCapabilities().getMyKBase().toStrings().iterator(); i.hasNext(); ) {
        	String formula = (String)i.next();
            if (myAgent.getSemanticCapabilities().getMyKBase().isClosed(SL.formula(formula),null)) {
                listModel.addElement(new MsgLog(formula,Color.red));
            } else {
                listModel.addElement(new MsgLog(formula,Color.black));
            }
        }
    }
    
    
    public void addFile() {
        JButton fileButton = new JButton("File");
        fileButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    int returnVal = chooser.showOpenDialog(AskFormula.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        String line;
                        try {
                            java.io.BufferedReader reader = new BufferedReader(new FileReader(file));
                            while ( (line = reader.readLine()) != null ) {
                                if ( !line.equals("") && !line.startsWith("//")) {
                                    Formula f = SL.formula(line);
                                    ((FilterKBaseImpl)myAgent.getSemanticCapabilities().getMyKBase()).assertFormula(f);
                                }
                            }
                            updateGuiKBList();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(AskFormula.this,
                                    "Bad file!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                    }
                }
        });
        buttonPanel.add(fileButton);
    }
    
    public void setOKButtonTitle() {
        okButton.setText("Assert");
    }
    
    private void addText(String text, int delta, boolean bracket) {
        int pos = nomProjet.getCaretPosition();
        if (bracket)
            nomProjet.setText(nomProjet.getText().substring(0,pos) + "("+ text + "  )" + nomProjet.getText().substring(pos, nomProjet.getText().length()));
        else 
            nomProjet.setText(nomProjet.getText().substring(0,pos) + " " + text + " " + nomProjet.getText().substring(pos, nomProjet.getText().length()));
        nomProjet.setCaretPosition(pos + delta);
        nomProjet.requestFocus();
    }
    
    public void putOtherButtons() {
        
    }
} // Fin de la classe AskName

