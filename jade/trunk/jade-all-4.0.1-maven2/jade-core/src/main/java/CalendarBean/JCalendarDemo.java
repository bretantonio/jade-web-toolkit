/*
 * @(#)JCalendarDemo.java 
 *
 * Copyright 1998 Kai Toedter
 */

package CalendarBean;

import java.beans.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * A demonstration Applet for the JCalendar bean.
 * The demo can also be started as java application.
 *
 * @version 1.0 10/10/98
 * @author  Kai Toedter
 */
public class JCalendarDemo extends JApplet implements PropertyChangeListener
{
   /**
    * Initializes the applet.
    */
   public void init()
   {
      getContentPane().setLayout( new BorderLayout() );
      calendarPanel = new JPanel();
      calendarPanel.setLayout( new BorderLayout() );

      JPanel controlPanel = new JPanel();
      controlPanel.setLayout( new BorderLayout() );
      JLocaleChooser localeChooser = new JLocaleChooser();
      localeChooser.addPropertyChangeListener( this );
      controlPanel.add( localeChooser, BorderLayout.NORTH );
      dateField = new JTextField();
      dateField.setEditable( false );
      controlPanel.add( dateField, BorderLayout.CENTER );

      demoPanel = new JPanel();
      demoPanel.setBorder( new CompoundBorder( new TitledBorder( "JCalendar Demo" ),
					       new EmptyBorder( 10, 10, 10, 10 ) ) );
      demoPanel.setLayout( new BorderLayout() );

      jcalendar = new JCalendar();
      jcalendar.addPropertyChangeListener( this );
      demoPanel.add( jcalendar, BorderLayout.CENTER );

      calendarPanel.add( controlPanel, BorderLayout.NORTH );
      calendarPanel.add( demoPanel, BorderLayout.CENTER );
      getContentPane().add( calendarPanel, BorderLayout.CENTER );

      calendar = Calendar.getInstance();
      jcalendar.setCalendar( calendar );
   }

   /**
    * Removes all components.
    */
   public void stop()
   {
      if( calendarPanel != null ) 
      {
	 getContentPane().remove( calendarPanel );
	 calendarPanel = null;
      }
   }

   /**
    * The applet is a PropertyChangeListener for "locale" and "calendar".
    */
   public void propertyChange( PropertyChangeEvent evt )
   {
      if( calendarPanel != null )
      {
	 if( evt.getPropertyName().equals( "locale" ) )
	 {
	    jcalendar.setLocale( (Locale) evt.getNewValue() );
	    DateFormat df = DateFormat.getDateInstance( DateFormat.LONG, jcalendar.getLocale() );
	    dateField.setText( df.format( calendar.getTime() ) );
	 }
	 else if( evt.getPropertyName().equals( "calendar" ) )
	 {
	    calendar = (Calendar) evt.getNewValue();
	    DateFormat df = DateFormat.getDateInstance( DateFormat.LONG, jcalendar.getLocale() );
	    dateField.setText( df.format( calendar.getTime() ) );
	 }
      }
   }

   /**
    * Creates a JFrame with a JCalendarDemo inside and can be used for testing.
    */
   static public void main( String[] s )
   {
      WindowListener l = new WindowAdapter() {
	 public void windowClosing( WindowEvent e ) { System.exit(0); }
      };

      JFrame frame = new JFrame( "JCalendar Demo" );
      frame.addWindowListener( l );
      JCalendarDemo demo = new JCalendarDemo();
      demo.init();
      frame.getContentPane().add( demo );
      frame.pack();
      frame.setVisible( true );
   }

   private JPanel         calendarPanel;
   private JPanel         demoPanel;
   private JCalendar      jcalendar;
   private JTextField     dateField;
   private JLocaleChooser localeChooser;
   private Calendar       calendar;
}


