/*
 * @(#)JSpinFieldBeanInfo.java 
 *
 * Copyright 1998 Kai Toedter
 */

package CalendarBean;

import java.util.Locale;
import java.awt.Image;
import java.beans.*;
import javax.swing.*;


/**
 * A BeanInfo class for the JSpinField bean.
 *
 * @version 1.0 10/10/98
 * @author  Kai Toedter
 */
public class JSpinFieldBeanInfo extends SimpleBeanInfo
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
    * Constructs a new BeanInfo class for the JSpinField bean.
    */
   public JSpinFieldBeanInfo()
   {
      icon    = loadImage ("images/JSpinFieldColor16.gif");
      icon32  = loadImage ("images/JSpinFieldColor32.gif");
      iconM   = loadImage ("images/JSpinFieldMono16.gif");
      icon32M = loadImage ("images/JSpinFieldMono32.gif");
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
}

