package org.agentcommunity.ANSMAS;

import java.util.Calendar;
import java.util.*;

public class Utils
{

	
		public static String getSystemDate(){
			Calendar cal = new GregorianCalendar();

			/* 
			// Get the components of the date
			int era = cal.get(Calendar.ERA);               // 0=BC, 1=AD
			int year = cal.get(Calendar.YEAR);             // 2002
			int month = cal.get(Calendar.MONTH);           // 0=Jan, 1=Feb, ...
			int day = cal.get(Calendar.DAY_OF_MONTH);      // 1...
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=Sunday, 2=Monday, ...

			// Get the components of the time
			int hour12 = cal.get(Calendar.HOUR);            // 0..11
			int hour24 = cal.get(Calendar.HOUR_OF_DAY);     // 0..23
			int min = cal.get(Calendar.MINUTE);             // 0..59
			int sec = cal.get(Calendar.SECOND);             // 0..59
			int ms = cal.get(Calendar.MILLISECOND);         // 0..999
			int ampm = cal.get(Calendar.AM_PM);             // 0=AM, 1=PM 
			*/

			String systemDate = "[" + cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.MILLISECOND) + "] ";

			return systemDate;
		}
}
