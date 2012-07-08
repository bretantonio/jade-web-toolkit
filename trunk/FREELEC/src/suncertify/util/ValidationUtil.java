package suncertify.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dskim
 * 
 */
public class ValidationUtil {

    /**
         * @param input
         * @return boolean
         */
    public static boolean portValidation(final String input) {
	int port = 0;
	boolean flag = false;
	Pattern pattern = Pattern.compile("[0-9]");
	Matcher matcher = pattern.matcher(input);
	flag = matcher.find();
	if (flag) {
	    port = Integer.parseInt(input);
	    if (port <= 1024 && port >= 65535) {
		flag = false;
	    }
	}
	return flag;
    }
}
