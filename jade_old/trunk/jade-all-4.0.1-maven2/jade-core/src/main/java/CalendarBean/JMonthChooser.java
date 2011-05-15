/*
 * @(#)JMonthChooser.java 
 *
 * Copyright 1998 Kai Toedter
 */

package CalendarBean;

import java.beans.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.text.*;
import javax.swing.*;

/**
 * JMonthChooser is a bean for choosing a month.
 *
 * @version 1.1 04/09/99
 * @author  Kai Toedter
 * 
 * @history 09/04/99 bug in itemStateChanged fixed (found by Eddie Penninkhof )
 */
public class JMonthChooser extends JComboBox implements ItemListener
{
   /**
    * Default JMonthChooser constructor.
    */
   public JMonthChooser()
   {
      super();
      addItemListener( this );
      dayChooser = null;
      locale = Locale.getDefault();
      initNames();
      setMonth( Calendar.getInstance().get( Calendar.MONTH ) );
   }

   /**
    * Initializes the locale specific month names.
    */
   public void initNames()
   {
      DateFormatSymbols dateFormatSymbols = new DateFormatSymbols( locale );
      String[] monthNames = dateFormatSymbols.getMonths();
      if( getItemCount() == 12 )
	 removeAllItems();
      for( int i=0; i<12; i++ )
	 addItem( monthNames[i] );
      setSelectedIndex( month );      
   }

   /**
    * The ItemListener for the months.
    */
   public void itemStateChanged( ItemEvent iEvt )
   {
      int index = getSelectedIndex();
      if( index >= 0 )
	 setMonth( index, false );
   }

   private void setMonth( int newMonth, boolean select )
   {
      int oldMonth = month;
      month = newMonth;
      if( select )
	 setSelectedIndex( month );      
      if( dayChooser != null )
	 dayChooser.setMonth( month );
      firePropertyChange( "month", oldMonth, month );
   }

   /**
    * Sets the month.
    * This is a bound property.
    * 
    * @see #getMonths
    */
   public void setMonth( int newMonth )
   {
      setMonth( newMonth, true );
   }

   /**
    * Returns the month.
    * 
    * @see #setMonth
    */
   public int getMonth()
   {
      return month;
   }

   /**
    * Convenience method set a day chooser.
    *
    * @param dayChooser the day chooser
    */
   public void setDayChooser( JDayChooser dayChooser )
   {
      this.dayChooser = dayChooser;
   }

   /**
    * Returns the locale.
    * 
    * @see #setLocale
    */
   public Locale getLocale()
   {
      return locale;
   }

   /**
    * Set the locale and initializes the new month names.
    * 
    * @see #getLocale
    */
   public void setLocale( Locale l )
   {
      locale = l;
      initNames();
   }

   /**
    * Creates a JFrame with a JMonthChooser inside and can be used for testing.
    */
   static public void main( String[] s )
   {
      JFrame frame = new JFrame( "MonthChooser" );
      frame.getContentPane().add( new JMonthChooser() );
      frame.pack();
      frame.setVisible( true );
   }

   private Locale      locale;
   private int         month;
   private JDayChooser dayChooser;
}
