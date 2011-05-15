/*
 * @(#)JDayChooserBeanInfo.java 
 *
 * Copyright 1998 Kai Toedter
 */

package CalendarBean;

import java.util.Locale;
import java.awt.Image;
import java.beans.*;
import javax.swing.*;


/**
 * A BeanInfo class for the JDayChooser bean.
 *
 * @version 1.0 10/10/98
 * @author  Kai Toedter
 */
public class JDayChooserBeanInfo extends SimpleBeanInfo
{

   /** 16x16 color icon. */
   Image icon;
   /** 32x32 color icon. */
   Image icon32;
   /** 16x16 mono icon. */
   Image iconM;
   /** 32x32 mono icon. */
   Image icon32M;

   /**
    * Constructs a new BeanInfo class for the JDayChooser bean.
    */
   public JDayChooserBeanInfo()
   {
      icon    = loadImage ("images/JDayChooserColor16.gif");
      icon32  = loadImage ("images/JDayChooserColor32.gif");
      iconM   = loadImage ("images/JDayChooserMono16.gif");
      icon32M = loadImage ("images/JDayChooserMono32.gif");
   }

   /**
    * This method returns an image object that can be used 
    * to represent the bean in toolboxes, toolbars, etc.
    */
   public Image getIcon( int iconKind )
   {
      switch( iconKind )
      {
      case ICON_COLOR_16x16: return icon;
      case ICON_COLOR_32x32: return icon32;
      case ICON_MONO_16x16: return iconM;
      case ICON_MONO_32x32: return icon32M;
      }
      return null;
   }

   /**
    * This method returns an array of PropertyDescriptors 
    * describing the editable properties supported by this bean.
    */
   public PropertyDescriptor[] getPropertyDescriptors()
   {
      try
      {
	 if( PropertyEditorManager.findEditor( Locale.class ) == null )
	 {
	    BeanInfo beanInfo = Introspector.getBeanInfo( JComboBox.class );
	    PropertyDescriptor[] p = beanInfo.getPropertyDescriptors();

	    int length = p.length;
	    PropertyDescriptor[] propertyDescriptors = new PropertyDescriptor[length + 1];
	    for( int i=0; i<length; i++ )
	       propertyDescriptors[i+1] = p[i];

	    propertyDescriptors [0] = new PropertyDescriptor( "locale", JDayChooser.class );
	    propertyDescriptors [0].setBound( true );
	    propertyDescriptors [0].setConstrained( false );
	    propertyDescriptors [0].setPropertyEditorClass( LocaleEditor.class );
	    return propertyDescriptors;
	 }
      }
      catch (IntrospectionException e)
      {
	 e.printStackTrace();
      }
      return null;
   }
}

