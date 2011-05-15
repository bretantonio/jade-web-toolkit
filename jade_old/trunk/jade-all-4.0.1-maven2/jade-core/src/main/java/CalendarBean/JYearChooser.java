/*
 * @(#)JCalendar.java 
 *
 * Copyright 1998 Kai Toedter
 */

package CalendarBean;

import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * JYearChooser is a bean for choosing a year.
 *
 * @version 1.0 10/10/98
 * @author  Kai Toedter
 */
public class JYearChooser extends JSpinField 
{
   /**
    * Default JCalendar constructor.
    */
   public JYearChooser()
   {
      Calendar calendar = Calendar.getInstance();
      dayChooser = null;
      setMinimum( calendar.getMinimum( Calendar.YEAR ) );
      setMaximum( calendar.getMaximum( Calendar.YEAR ) );
      setValue( calendar.get( Calendar.YEAR ) );
   }

   protected void setValue( int newValue, boolean updateTextField, boolean updateScrollbar )
   {
      int oldYear = year;
      year = newValue;
      super.setValue( newValue, updateTextField, updateScrollbar );
      if( dayChooser != null )
	 dayChooser.setYear( newValue );
      firePropertyChange( "year", oldYear, year );
   }

   /**
    * Sets the year.
    * This is a bound property.
    *
    * @see #getYear
    * @param y the new year
    */
   public void setYear( int y )
   {
      super.setValue( y );
   }

   /**
    * Returns the year.
    */
   public int getYear()
   {
      return year;
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
    * Creates a JFrame with a JYearChooser inside and can be used for testing.
    */
   static public void main( String[] s )
   {
      JFrame frame = new JFrame( "JYearChooser" );
      frame.getContentPane().add( new JYearChooser() );
      frame.pack();
      frame.setVisible( true );
   }

   private JDayChooser dayChooser;
   private int         year;
}
