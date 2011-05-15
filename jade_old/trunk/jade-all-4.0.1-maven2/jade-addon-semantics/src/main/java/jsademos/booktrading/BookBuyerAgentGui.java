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

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSequence;
import jade.semantics.lang.sl.grammar.TermSequenceNode;
import jade.semantics.lang.sl.tools.SL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import jsademos.booktrading.BookBuyerAgent.BookBuyerCapabilities;

public class BookBuyerAgentGui extends JFrame implements IBookBuyerAgentGui {
	
	//---------------------------------------------------------------
	//                      CONSTANTS 
	//---------------------------------------------------------------
	static final String[] BOOKS_FOR_SALES_HEADERS = new String[] {"ISBN", "Title"};

	static final String[] BOUGHT_BOOKS_HEADERS = new String[] {"ISBN", "Title", "Price", "Agent"};

	static final IdentifyingExpression BOOKS_FOR_SALE_IRE = (IdentifyingExpression)
    	SL.term("(some (sequence ?i ?t) (exists ?a (and (for_sale ?i ?a) (title ?i ?t))))");

	static final JLabel CART_LABEL = new JLabel(new ImageIcon("./cart.gif"), SwingConstants.RIGHT);

	//---------------------------------------------------------------
	//                      INNER CLASSES 
	//---------------------------------------------------------------
	class BooksForSaleTableModel implements TableModel {
		
		public int getColumnCount() {
			return 2;
		}
		
		
		public Class getColumnClass(int columnIndex) {
			return Term.class;
		}
		
		public String getColumnName(int columnIndex) {
			return BOOKS_FOR_SALES_HEADERS[columnIndex];
		}
		
		public int getRowCount() {
			return booksForSaleList != null ? booksForSaleList.size() : 0;
		}
		
		public Object getValueAt(int rowIndex,int columnIndex) {
			Constant c = (Constant)((TermSequence)booksForSaleList.get(rowIndex)).getTerm(columnIndex);
			return SL.word(c.stringValue());
		}
		
		public boolean isCellEditable(int rowIndex,int columnIndex) {return false;}
		public void addTableModelListener(TableModelListener l) {}
		public void removeTableModelListener(TableModelListener l) {}
		public void setValueAt(Object aValue,int rowIndex,int columnIndex) {}

	}
	
	class BoughtBooksTableModel implements TableModel {
		
		public int getColumnCount() {
			return 4;
		}
		
		
		public Class getColumnClass(int columnIndex) {
			return Term.class;
		}
		
		public String getColumnName(int columnIndex) {
			return BOUGHT_BOOKS_HEADERS[columnIndex];
		}
		
		public int getRowCount() {
			return boughtBooksList != null ? boughtBooksList.size() : 0;
		}
		
		public Object getValueAt(int rowIndex,int columnIndex) {
			if ( columnIndex == 0 || columnIndex == 1 || columnIndex == 2) {
				Constant c = (Constant)((TermSequence)boughtBooksList.get(rowIndex)).getTerm(columnIndex);
				return SL.word(c.stringValue());
			}
			else {
				FunctionalTermParamNode tagent = (FunctionalTermParamNode)((TermSequence)boughtBooksList.get(rowIndex)).getTerm(columnIndex);
				return tagent.getParameter("name");
			}
		}
		
		public boolean isCellEditable(int rowIndex,int columnIndex) {return false;}
		public void addTableModelListener(TableModelListener l) {}
		public void removeTableModelListener(TableModelListener l) {}
		public void setValueAt(Object aValue,int rowIndex,int columnIndex) {}

	}

	//---------------------------------------------------------------
	//                      FIELDS 
	//---------------------------------------------------------------
	BookBuyerCapabilities myCapabilities;

	JTextField isbnField = new JTextField("ISBN");
	JTextField titleField = new JTextField("Title");
	JTextField priceField = new JTextField("Price");
	
	ListOfTerm booksForSaleList = new ListOfTerm();
	ListOfTerm boughtBooksList = new ListOfTerm();
	
	JTable booksForSaleTable;
	JTable boughtBooksTable;

	//---------------------------------------------------------------
	//                      METHODS 
	//---------------------------------------------------------------
	public void refreshBooksForSaleList() {
		if ( myCapabilities != null && myCapabilities.getMyKBase() != null ) {
			ListOfTerm l = myCapabilities.getMyKBase().queryRef(BOOKS_FOR_SALE_IRE);
			booksForSaleList.removeAll();
			if ( l != null ) {
				for (int i=0; i<l.size(); i++) {
					if ( !booksForSaleList.contains(l.get(i)) ) {
						booksForSaleList.add(l.get(i));
					}
				}
			}
		}
		booksForSaleTable.revalidate();
		repaint();
	}
	
	public void revisePrice(Double price)
	{
		priceField.setText(price.toString());
	}
		
	public void addToBoughtBooksList(Constant isbn, Constant title, Constant price, Term agent)
	{
		TermSequence s = new TermSequenceNode();
		s.addTerm(isbn);
		s.addTerm(title);
		s.addTerm(price);
		s.addTerm(agent);
		boughtBooksList.add(s);
		boughtBooksTable.revalidate();
		CART_LABEL.setVisible(false);
		priceField.setText("0");
		priceField.setEditable(true);
		booksForSaleTable.setRowSelectionInterval(0,0);
		repaint();
	}

	//---------------------------------------------------------------
	//                      CONSTRUCTOR 
	//---------------------------------------------------------------
	public BookBuyerAgentGui(BookBuyerCapabilities sc) {
		
		super("Buyer "+sc.getAgent().getLocalName());

		myCapabilities = sc;
		
		// Frame panel
		//------------
		JPanel panel = new JPanel(new GridLayout(3,1));
		getContentPane().add(panel);
		
		// PANEL 1 : list of books for sale
		JScrollPane	sp1 = new JScrollPane(booksForSaleTable = new JTable(new BooksForSaleTableModel()));
		sp1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"Books for sale:"));
		panel.add(sp1);
		booksForSaleTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( booksForSaleList != null ) {
					int index = booksForSaleTable.getSelectedRow();
					isbnField.setText(((Constant)((TermSequence)booksForSaleList.get(index)).getTerm(0)).stringValue());
					titleField.setText(((Constant)((TermSequence)booksForSaleList.get(index)).getTerm(1)).stringValue());
				}
			}
		});
		booksForSaleTable.getColumn(BOOKS_FOR_SALES_HEADERS[0]).setMaxWidth(40);
		
		// PANEL 2 : list of bought books
		JScrollPane	sp2 = new JScrollPane(boughtBooksTable = new JTable(new BoughtBooksTableModel()));
		sp2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),"Bought books:"));
		panel.add(sp2);
		boughtBooksTable.getColumn(BOUGHT_BOOKS_HEADERS[0]).setMaxWidth(40);
		boughtBooksTable.getColumn(BOUGHT_BOOKS_HEADERS[2]).setMaxWidth(60);
		
		// PANEL 3 : purchase panel
		JPanel purchasePanel = new JPanel(new BorderLayout());
		panel.add(purchasePanel);
		JPanel fieldsPanel = new JPanel(new GridLayout(4,2));
		purchasePanel.add(fieldsPanel);
		fieldsPanel.add(new JLabel("ISBN"));
		fieldsPanel.add(isbnField);
		isbnField.setEditable(false);
		fieldsPanel.add(new JLabel("Title"));
		fieldsPanel.add(titleField);
		titleField.setEditable(false);
		fieldsPanel.add(new JLabel("Price"));
		fieldsPanel.add(priceField);
		fieldsPanel.add(CART_LABEL);
		CART_LABEL.setVisible(false);
		JPanel buttonPanel = new JPanel();
		purchasePanel.add(BorderLayout.SOUTH, buttonPanel);
		buttonPanel.add(new JButton(new AbstractAction("Purchase") {
			public void actionPerformed(java.awt.event.ActionEvent e) {	
				Constant isbn = SL.string(isbnField.getText());
				Constant price = SL.real(priceField.getText());
				Constant title = SL.string(titleField.getText());
				priceField.setEditable(false);
				CART_LABEL.setVisible(true);
				myCapabilities.purchase(isbn, title, price);
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
		setSize(400, 400);
		setVisible(true);
	}

}
