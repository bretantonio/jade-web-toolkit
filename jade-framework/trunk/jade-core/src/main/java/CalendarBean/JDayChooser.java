/*
 * @(#)JDayChooser.java 
 *
 * Copyright 1998 Kai Toedter
 */

package CalendarBean;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;


/**
 * JCalendar is a bean for choosing a day.
 *
 * @version 1.03 07/13/00
 * @author  Kai Toedter
 */
public class JDayChooser extends JPanel implements ActionListener
{
   /**
    * Default JDayChooser constructor.
    */
   public JDayChooser()
   {
      locale = Locale.getDefault();
      days = new JButton[49];
      selectedDay = null;
      Calendar calendar = Calendar.getInstance( locale );
      today = (Calendar) calendar.clone();

      setLayout( new GridLayout( 7, 7 ) );
      
      for( int y=0; y<7; y++ )
      {
	 for( int x=0; x<7; x++ )
	 {
	    int index = x + 7 * y;
	    if( y == 0 )
	    {
	       // Create a button that does'nt react on clicks
	       days[index] = new JButton() {
		  public void addMouseListener(MouseListener l) {}
	       };
	       days[index].setBackground( new Color( 200, 200, 255 ) );
	    }
	    else
	    {
	       days[index] = new JButton( "x" );
	       days[index].addActionListener( this );
	    }

	    days[index].setMargin( new Insets( 0, 0, 0, 0 ) );
	    days[index].setFocusPainted( false );
	    add( days[ index ] );
	 }
      }
      init();
      setDay( Calendar.getInstance().get( Calendar.DAY_OF_MONTH ) );
   }

   /**
    * Initilizes the locale specific names for the days of the week.
    */
   protected void init()
   {
      calendar = Calendar.getInstance( locale );
      int firstDayOfWeek = calendar.getFirstDayOfWeek();
      DateFormatSymbols dateFormatSymbols = new DateFormatSymbols( locale );
      dayNames = dateFormatSymbols.getShortWeekdays();
      int day = firstDayOfWeek;
      for( int i = 0 ; i<7; i++ )
      {
	 days[i].setText( dayNames[day] );
	 if( day == 1 )
	    days[i].setForeground( Color.red );
	 else
	    days[i].setForeground( Color.blue );

	 if( day < 7 )
	    day++;
	 else
	    day -= 6;

      }
      oldDayBackground = (new JButton()).getBackground();
      drawDays();
   }

   /**
    * Hides and shows the day buttons.
    */ 
   protected void drawDays()
   {
      Calendar tmpCalendar = (Calendar) calendar.clone();
      int firstDayOfWeek = tmpCalendar.getFirstDayOfWeek();
      tmpCalendar.set( Calendar.DAY_OF_MONTH, 1 );

      int firstDay = tmpCalendar.get( Calendar.DAY_OF_WEEK ) - firstDayOfWeek;
      if( firstDay < 0 )
	 firstDay += 7;

      int i;

      for( i=0; i< firstDay; i++ )
      {
	 days[ i+7 ].setVisible( false );
	 days[ i+7 ].setText( "" );
      }

      tmpCalendar.add( Calendar.MONTH, 1 );
      Date firstDayInNextMonth = tmpCalendar.getTime();
      tmpCalendar.add( Calendar.MONTH, -1 );

      Date day = tmpCalendar.getTime();
      int n = 0;
      Color foreground = getForeground();
      while( day.before( firstDayInNextMonth ) )
      {
	 days[i+n+7].setText( Integer.toString( n+1 ) );
	 days[i+n+7].setVisible( true );
	 if( tmpCalendar.get( Calendar.DAY_OF_YEAR ) == today.get( Calendar.DAY_OF_YEAR ) &&
	     tmpCalendar.get( Calendar.YEAR ) == today.get( Calendar.YEAR ) )
	 {
	    days[i+n+7].setForeground( Color.red );
	 }
	 else
	    days[i+n+7].setForeground( foreground );

	 if( n+1 == this.day )
	 {
	    days[i+n+7].setBackground( Color.gray );
	    selectedDay = days[i+n+7];
	 }
	 else
	    days[i+n+7].setBackground( oldDayBackground );
	    
	 n++;
	 tmpCalendar.add( Calendar.DATE, 1 );
	 day = tmpCalendar.getTime();
      }

      for( int k=n+i+7; k<49; k++ )
      {
	 days[k].setVisible( false );
	 days[k].setText( "" );
      }
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
    * Sets the locale.
    * 
    * @see #getLocale
    */
   public void setLocale( Locale l )
   {
      locale = l;
      init();
   }

   /**
    * Sets the day.
    * This is a bound property.
    * 
    * @param d the day
    * @see #getDay
    */
   public void setDay( int d )
   {
      if( d < 1 )
	 d = 1;

      Calendar tmpCalendar = (Calendar) calendar.clone();
      tmpCalendar.set( Calendar.DAY_OF_MONTH, 1 );
      tmpCalendar.add( Calendar.MONTH, 1 );
      tmpCalendar.add( Calendar.DATE, -1 );
      int maxDaysInMonth = tmpCalendar.get( Calendar.DATE );

      if( d > maxDaysInMonth )
	 d = maxDaysInMonth;

      int oldDay = day;
      day = d;

      if( selectedDay != null )
      {
	 selectedDay.setBackground( oldDayBackground );
	 selectedDay.repaint(); // Bug: needed for Swing 1.0.3
      }

      for( int i=7; i<49; i++ )
      {
	 if( days[i].getText().equals( Integer.toString( day ) ) )
	 {
	    selectedDay = days[i];
	    selectedDay.setBackground( Color.gray );
	    break;
	 }
      }
      firePropertyChange( "day", oldDay, day );
   }

   /**
    * Returns the selected day.
    *
    * @see #setDay
    */
   public int getDay()
   {
      return day;
   }

   /**
    * Sets a specific month. This is needed for correct graphical
    * representation of the days.
    *
    * @param month the new month
    */
   public void setMonth( int month )
   {
      calendar.set( Calendar.MONTH, month );
      setDay( day );
      drawDays();
   }

   /**
    * Sets a specific year. This is needed for correct graphical
    * representation of the days.
    *
    * @param year the new year
    */
   public void setYear( int year )
   {
      calendar.set( Calendar.YEAR, year );
      drawDays();
   }

   /**
    * Sets a specific calendar. This is needed for correct graphical
    * representation of the days.
    *
    * @param c the new calendar
    */
   public void setCalendar( Calendar c )
   {
      calendar = c;
      drawDays();
   }

   /**
    * Sets the font property.
    * 
    * @param font the new font
    */
   public void setFont( Font font )
   {
      if( days != null )
      {
	 for( int i=0; i<49; i++ )
	    days[i].setFont( font );
      }
   }

   /**
    * Sets the foreground color.
    * 
    * @param fg the new foreground
    */
   public void setForeground( Color fg)
   {
      super.setForeground( fg);
      if( days != null )
      {
	 for( int i=7; i<49; i++ )
	    days[i].setForeground( fg);
	 drawDays();
      }
   }

   /**
    * Returns "JDayChooser".
    */
   public String getName()
   {
      return "JDayChooser";
   }

   /**
    * JDayChooser is the ActionListener for all day buttons.
    */
   public void actionPerformed( ActionEvent e )
   {
      JButton button = (JButton) e.getSource();
      int day = ( new Integer( button.getText() ) ).intValue();
      setDay( day );
   }

   /**
    * Creates a JFrame with a JDayChooser inside and can be used for testing.
    */
   static public void main( String[] s )
   {
      JFrame frame = new JFrame( "JDayChooser" );
      frame.getContentPane().add( new JDayChooser() );
      frame.pack();
      frame.setVisible( true );
   }

   private JButton  days[];
   private JButton  selectedDay;
   private int      day;
   private Color    oldDayBackground;
   private String   dayNames[];
   private Calendar calendar;
   private Calendar today;
   private Locale   locale;
}


