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
 * JCalendar is a bean for entering a date by choosing the year, month and day.
 *
 * @version 1.0 10/10/98
 * @author  Kai Toedter
 */
public class JCalendar extends JPanel implements PropertyChangeListener
{
   /**
    * Default JCalendar constructor.
    */
   public JCalendar()
   {
      // needed for setFont() etc.
      dayChooser = null;
      monthChooser = null;
      yearChooser = null;
      locale = Locale.getDefault();
      calendar = Calendar.getInstance();

      setLayout( new BorderLayout() );
      JPanel myPanel = new JPanel();
      myPanel.setLayout( new GridLayout( 1, 2 ) );
      monthChooser = new JMonthChooser();
      myPanel.add( monthChooser );
      yearChooser = new JYearChooser();
      myPanel.add( yearChooser );
      dayChooser = new JDayChooser();
      dayChooser.addPropertyChangeListener( this );
      monthChooser.setDayChooser( dayChooser );
      monthChooser.addPropertyChangeListener( this );
      yearChooser.setDayChooser( dayChooser );
      yearChooser.addPropertyChangeListener( this );
      add( myPanel, BorderLayout.NORTH );
      add( dayChooser, BorderLayout.CENTER );
   }

   private void setCalendar( Calendar c, boolean update )
   {
      Calendar oldCalendar = calendar;
      calendar = c;
      if( update )
      {
	 dayChooser.setDay( c.get( Calendar.DATE ) );
	 monthChooser.setMonth( c.get( Calendar.MONTH ) );
	 yearChooser.setYear( c.get( Calendar.YEAR ) );
      }
      firePropertyChange( "calendar", oldCalendar, calendar );
   }

   /**
    * Sets the calendar property.
    * This is a bound property.
    *
    * @see #getCalendar
    * @param c the new calendar
    */
   public void setCalendar( Calendar c )
   {
      setCalendar( c, true );
   }

    /**
     * Returns the calendar property.
     *
     * @return the value of the calendar property.
     * @see #setCalendar
     */
   public Calendar getCalendar()
   {
      return calendar;
   }

   /**
    * Sets the locale property.
    * This is a bound property.
    * 
    * @see #getLocale
    */
   public void setLocale( Locale l )
   {
      Locale oldLocale = locale;
      locale = l;
      dayChooser.setLocale( locale );
      monthChooser.setLocale( locale );
      firePropertyChange( "locale", oldLocale, locale );
   }

   /**
    * Returns the locale.
    * 
    * @return the value of the locale property.
    * @see #setLocale
    */
   public Locale getLocale()
   {
      return locale;
   }

   /**
    * Sets the font property.
    * 
    * @param font the new font
    */
   public void setFont( Font font )
   {
      super.setFont( font );
      if( dayChooser != null )
      {
	 dayChooser.setFont( font );
	 monthChooser.setFont( font );
	 yearChooser.setFont( font );
      }
   }

   
   /**
    * Sets the foreground color.
    * 
    * @param fg the new foreground
    */
   public void setForeground( Color fg )
   {
      super.setForeground( fg );
      if( dayChooser != null )
      {
	 dayChooser.setForeground( fg );
	 monthChooser.setForeground( fg );
	 yearChooser.setForeground( fg );
      }
   }
     
   /**
    * Sets the background color.
    * 
    * @param bg the new background
    */
   public void setBackground( Color bg)
   {
      super.setBackground( bg);
      if( dayChooser != null )
	 dayChooser.setBackground( bg);
   }
   
   /**
    * JCalendar is a PropertyChangeListener, for its day, month and year chooser.
    */
   public void propertyChange( PropertyChangeEvent evt )
   {
      if( calendar != null )
      {
	 Calendar c = (Calendar) calendar.clone();
	 if( evt.getPropertyName().equals( "day" ) )
	 {
	    c.set( Calendar.DAY_OF_MONTH, ((Integer)evt.getNewValue()).intValue() );
	    setCalendar( c, false );
	 }
	 else if( evt.getPropertyName().equals( "month" ) )
	 {
	    c.set( Calendar.MONTH, ((Integer)evt.getNewValue()).intValue() );
	    setCalendar( c, false );
	 }
	 else if( evt.getPropertyName().equals( "year" ) )
	 {
	    c.set( Calendar.YEAR, ((Integer)evt.getNewValue()).intValue() );
	    setCalendar( c, false );
	 }
      }
   }

   /**
    * Returns "JCalendar".
    */
   public String getName()
   {
      return "JCalendar";
   }

   /**
    * Creates a JFrame with a JCalendar inside and can be used for testing.
    */
   static public void main( String[] s )
   {
      JFrame frame = new JFrame( "JCalendar" );
      frame.getContentPane().add( new JCalendar() );
      frame.pack();
      frame.setVisible( true );
   }

   private JYearChooser  yearChooser;
   private JMonthChooser monthChooser;
   private JDayChooser   dayChooser;
   private Calendar      calendar;
   private Locale        locale;
}
