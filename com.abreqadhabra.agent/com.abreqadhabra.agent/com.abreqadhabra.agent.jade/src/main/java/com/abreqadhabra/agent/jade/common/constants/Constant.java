package com.abreqadhabra.agent.jade.common.constants;

public class Constant {

	public static final String STRING_COLON = ":";
	public static final String STRING_SEMICOLON = ";";
	public static final String STRING_COMMA = ",";
	public static final String STRING_PERIOD = ".";
	public static final String STRING_SLASH = "/";
	public static final String STRING_OPEN_PARENTHESIS = "(";
	public static final String STRING_CLOSE_PARENTHESIS = ")";
	public static final String STRING_SPACE = " ";
	public static final String STRING_HYPHEN = "-";
	public static String CLASSPATH_IDENTIFIER = "classpath:";

	public static final class SPRING_STANDALONE_APPLICATION {
		public static final String CONTEXT_CONFIG_LOCATION = "classpath:com/abreqadhabra/agent/jade/common/conf/applicationContext.xml";
		public static final String JADE_PLATFORM_SERVICE_BEAN_NAME = "jadePlatformService";
	}

	public static final class BOOT_PROFILE_UTIL {
		public static String DEFAULT_PROPERTIES_LOCATION = "classpath:com/abreqadhabra/agent/jade/common/conf/jade.properties ";

	}
}
