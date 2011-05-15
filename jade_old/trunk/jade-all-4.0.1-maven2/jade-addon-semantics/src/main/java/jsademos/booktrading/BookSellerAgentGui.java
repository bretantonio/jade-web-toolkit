/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

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

package jsademos.booktrading;

import jade.core.behaviours.OneShotBehaviour;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSequence;
import jade.semantics.lang.sl.tools.SL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import jsademos.booktrading.BookSellerAgent.BookSellerCapabilities;

public class BookSellerAgentGui  extends JFrame implements IBookSellerAgentGui {
	
	//---------------------------------------------------------------
	//                      CONSTANTS 
	//---------------------------------------------------------------
	static String[] COLUMN_TITLES = new String[] {"ISBN", "Title", "Price", "Min price", "Delay"};

	static final IdentifyingExpression BOOKS_IRE = (IdentifyingExpression)
		SL.term("(some (sequence ?i ?t ?p ?m ?d) " +
				"          (and (for_sale ?i ??myself) " +
				"               (selling_delay ?i ?d)" +
				"               (selling_price ?i ?p ??agent)" +
				"				(title ?i ?t)" +
				"               (min_selling_price ?i ?m ??agent)))");
	
	public static final Formula BOOK_FORMULA 
	   = new AndNode(Ontology.FOR_SALE_PREDICATE, 
			   new AndNode(Ontology.SELLING_PRICE_PREDICATE,
					   new AndNode(Ontology.MIN_SELLING_PRICE_PREDICATE,
							       Ontology.SELLING_DELAY_PREDICATE)));

	//---------------------------------------------------------------
	//                      INNER CLASSES 
	//---------------------------------------------------------------
	class BookSellerTableModel implements TableModel {
		
		public int getColumnCount() {
			return 5;
		}
		
		
		public Class getColumnClass(int columnIndex) {
			return Term.class;
		}
		
		public String getColumnName(int columnIndex) {
			return COLUMN_TITLES[columnIndex];
		}
		
		public int getRowCount() {
			return bookList != null ? bookList.size() : 0;
		}
		
		public Object getValueAt(int rowIndex,int columnIndex) {
			Constant c = (Constant)((TermSequence)bookList.get(rowIndex)).getTerm(columnIndex);
			if ( columnIndex == 4 ) {
				long millisec = c.intValue().longValue();
				long sec = millisec/1000;
				if ( sec > 60 ) {
					return SL.word(new Long((sec/60)+1).toString()+" min");
				}
				else {
					return SL.word(new Long(sec).toString()+" sec");
				}
			}
			else if ( columnIndex == 2 || columnIndex == 3 ) {
				return SL.word(c.realValue().toString()+" ¤");
			}
			else if ( columnIndex == 0 || columnIndex == 1 ) {
				return SL.word(c.stringValue());
			}
			return ((TermSequence)bookList.get(rowIndex)).getTerm(columnIndex);
		}
		
		public boolean isCellEditable(int rowIndex,int columnIndex) {return false;}
		public void addTableModelListener(TableModelListener l) {}
		public void removeTableModelListener(TableModelListener l) {}
		public void setValueAt(Object aValue,int rowIndex,int columnIndex) {}

	}

	class AddBookDialog extends JFrame
	{
		JTextField isbnField = new JTextField();
		JTextField priceField = new JTextField();
		JTextField minPriceField = new JTextField();
		JTextField delayField = new JTextField();
		
		public AddBookDialog() {
			super("Add a book for sale");
			
			JPanel panel = new JPanel(new GridLayout(5,2));
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(BorderLayout.NORTH, panel);
			panel.add(new JLabel("ISBN"));
			panel.add(isbnField);
			panel.add(new JLabel("Price"));
			panel.add(priceField);
			panel.add(new JLabel("Min price"));
			panel.add(minPriceField);
			panel.add(new JLabel("Delay"));
			panel.add(delayField);
			panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"Book description:"));
			
			JPanel buttonPanel = new JPanel();
			getContentPane().add(buttonPanel);
					
			buttonPanel.add(new JButton(new AbstractAction("Cancel") {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					hide();
				}
			}));
			
			buttonPanel.add(new JButton(new AbstractAction("Add") {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if ( !isbnField.getText().equals("") &&
						 !priceField.getText().equals("") &&
						 !minPriceField.getText().equals("") &&
						 !delayField.getText().equals("") ) {
						myCapabilities.getAgent().addBehaviour(
								new OneShotBehaviour(myCapabilities.getAgent()) {
									@Override
									public void action() {
										Formula bF = BOOK_FORMULA
										.instantiate("isbn", SL.string(isbnField.getText()))
										.instantiate("price", SL.real(priceField.getText()))
										.instantiate("min_price", SL.real(minPriceField.getText()))
										.instantiate("delay", SL.integer(new Long(delayField.getText())))
										.instantiate("agent", myCapabilities.getAgentName());
										myCapabilities.interpret(bF);
									}});
						hide();
					}
				}
			}));

			setSize(300,190);
		}
	}
	
	//---------------------------------------------------------------
	//                      FIELDS 
	//---------------------------------------------------------------
	BookSellerCapabilities myCapabilities;
	
	ListOfTerm bookList = null;
	
	JTable bookTable;

	//---------------------------------------------------------------
	//                      METHODS 
	//---------------------------------------------------------------
	public void refreshBookList() {
		if ( myCapabilities != null && myCapabilities.getMyKBase() != null ) {
			bookList = myCapabilities.getMyKBase().queryRef(BOOKS_IRE);
		}
		bookTable.revalidate();
		repaint();
	}
	
	//---------------------------------------------------------------
	//                      CONSTRUCTOR 
	//---------------------------------------------------------------
	public BookSellerAgentGui(BookSellerCapabilities sc) {
		
		super("Seller "+sc.getAgent().getLocalName());

		myCapabilities = sc;
		
		// Frame panel
		//------------
		JPanel panel = new JPanel(new BorderLayout());
		getContentPane().add(panel);
		panel.add(new JScrollPane(bookTable = new JTable(new BookSellerTableModel())));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"Books for sale:"));
		bookTable.getColumn(COLUMN_TITLES[0]).setMaxWidth(40);
		bookTable.getColumn(COLUMN_TITLES[2]).setMaxWidth(60);
		bookTable.getColumn(COLUMN_TITLES[3]).setMaxWidth(60);
		bookTable.getColumn(COLUMN_TITLES[4]).setMaxWidth(60);
		JPanel buttonPanel = new JPanel();
		panel.add(BorderLayout.SOUTH, buttonPanel);
		buttonPanel.add(new JButton(new AbstractAction("Remove") {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				int index = bookTable.getSelectedRow();
				myCapabilities.interpret(Ontology.NOT_FOR_SALE_PREDICATE
						.instantiate("isbn", ((TermSequence)bookList.get(index)).getTerm(0))
						.instantiate("agent", myCapabilities.getAgentName()));
				myCapabilities.getAgent().addBehaviour(
						new OneShotBehaviour(myCapabilities.getAgent()) {
							@Override
							public void action() {refreshBookList();}});
			}
		}));
		buttonPanel.add(new JButton(new AbstractAction("Add") {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				new AddBookDialog().show();
			}
		}));
		buttonPanel.add(new JButton(new AbstractAction("Quit") {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				myCapabilities.getAgent().doDelete();
				hide();
			}
		}));
	
		// Show frame
		//-----------
		setSize(400, 200);
		setVisible(true);
	}
}
