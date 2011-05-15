/*
 * @(#)JMonthChooser.java 
 *
 * Copyright 1998 Kai Toedter
 */

package CalendarBean;

import java.util.Locale;
import java.awt.Image;
import java.beans.*;
import javax.swing.*;


/**
 * A BeanInfo class for the JYearChooser bean.
 *
 * @version 1.00
 */
public class JYearChooserBeanInfo extends SimpleBeanInfo
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
    * Constructs a new BeanInfo class for the JYearChooser bean.
    */
   public JYearChooserBeanInfo()
   {
      icon    = loadImage ("images/JYearChooserColor16.gif");
      icon32  = loadImage ("images/JYearChooserColor32.gif");
      iconM   = loadImage ("images/JYearChooserMono16.gif");
      icon32M = loadImage ("images/JYearChooserMono32.gif");
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
    * This method returns an array of PropertyDescriptors describing 
    * the editable properties supported by this bean.
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

	    propertyDescriptors [0] = new PropertyDescriptor( "locale", JYearChooser.class );
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

