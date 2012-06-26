package com.javadude.articles.vaddmvc1;

/**
 * The bean information class for com.javadude.articles.vaddmvc1.SimpleAddressData.
 *
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 *
 * Creation date: (1/15/00 8:13:08 PM)
 * @author: Scott Stanchfield
 */
public class SimpleAddressDataBeanInfo extends java.beans.SimpleBeanInfo {

	/**
	 * Find the method by comparing (name & parameter size) against the methods in the class.
	 * @return java.lang.reflect.Method
	 * @param aClass java.lang.Class
	 * @param methodName java.lang.String
	 * @param parameterCount int
	 */
	public static java.lang.reflect.Method findMethod(java.lang.Class aClass, java.lang.String methodName, int parameterCount) {
		try {
			/* Since this method attempts to find a method by getting all methods from the class,
		this method should only be called if getMethod cannot find the method. */
			java.lang.reflect.Method methods[] = aClass.getMethods();
			for (int index = 0; index < methods.length; index++){
				java.lang.reflect.Method method = methods[index];
				if ((method.getParameterTypes().length == parameterCount) && (method.getName().equals(methodName))) {
					return method;
				}
			}
		} catch (java.lang.Throwable exception) {
			return null;
		}
		return null;
	}

	/**
	 * Gets the bean class.
	 * @return java.lang.Class
	 */
	public static java.lang.Class getBeanClass() {
		return com.javadude.articles.vaddmvc1.SimpleAddressData.class;
	}

	/**
	 * Gets the bean class name.
	 * @return java.lang.String
	 */
	public static java.lang.String getBeanClassName() {
		return "com.javadude.articles.vaddmvc1.SimpleAddressData";
	}

	/**
	 * Gets the address property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor addressPropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the address property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getAddress", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getAddress", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						java.lang.String.class
					};
					aSetMethod = getBeanClass().getMethod("setAddress", aSetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setAddress", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("address"
				, aGetMethod, aSetMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("address"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("address"); */
			aDescriptor.setShortDescription("the street where the person lives");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
			/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the businessPhone property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor businessPhonePropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the businessPhone property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getBusinessPhone", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getBusinessPhone", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						java.lang.String.class
					};
					aSetMethod = getBeanClass().getMethod("setBusinessPhone", aSetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setBusinessPhone", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("businessPhone"
				, aGetMethod, aSetMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("businessPhone"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			aDescriptor.setDisplayName("business phone");
			aDescriptor.setShortDescription("the person's business phone number");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
			/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the city property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor cityPropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the city property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getCity", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getCity", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						java.lang.String.class
					};
					aSetMethod = getBeanClass().getMethod("setCity", aSetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setCity", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("city"
				, aGetMethod, aSetMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("city"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("city"); */
			aDescriptor.setShortDescription("the city where the person lives");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
			/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the country property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor countryPropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the country property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getCountry", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getCountry", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						java.lang.String.class
					};
					aSetMethod = getBeanClass().getMethod("setCountry", aSetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setCountry", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("country"
				, aGetMethod, aSetMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("country"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("country"); */
			aDescriptor.setShortDescription("the country where the person lives");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
			/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Returns the BeanInfo of the superclass of this bean to inherit its features.
	 * @return java.beans.BeanInfo[]
	 */
	public java.beans.BeanInfo[] getAdditionalBeanInfo() {
		java.lang.Class superClass;
		java.beans.BeanInfo superBeanInfo = null;
	
		try {
			superClass = getBeanDescriptor().getBeanClass().getSuperclass();
		} catch (java.lang.Throwable exception) {
			return null;
		}
	
		try {
			superBeanInfo = java.beans.Introspector.getBeanInfo(superClass);
		} catch (java.beans.IntrospectionException ie) {}
	
		if (superBeanInfo != null) {
			java.beans.BeanInfo[] ret = new java.beans.BeanInfo[1];
			ret[0] = superBeanInfo;
			return ret;
		}
		return null;
	}

	public java.beans.BeanDescriptor getBeanDescriptor() {
		java.beans.BeanDescriptor aDescriptor = null;
		try {
			/* Create and return the SimpleAddressDataBeanInfo bean descriptor. */
			aDescriptor = new java.beans.BeanDescriptor(com.javadude.articles.vaddmvc1.SimpleAddressData.class);
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("hidden-state", Boolean.FALSE); */
		} catch (Throwable exception) {
		};
		return aDescriptor;
	}

	/**
	 * Return the event set descriptors for this bean.
	 * @return java.beans.EventSetDescriptor[]
	 */
	public java.beans.EventSetDescriptor[] getEventSetDescriptors() {
		try {
			java.beans.EventSetDescriptor aDescriptorList[] = {};
			return aDescriptorList;
		} catch (Throwable exception) {
			handleException(exception);
		};
		return null;
	}

	/**
	 * Return the method descriptors for this bean.
	 * @return java.beans.MethodDescriptor[]
	 */
	public java.beans.MethodDescriptor[] getMethodDescriptors() {
		try {
			java.beans.MethodDescriptor aDescriptorList[] = {};
			return aDescriptorList;
		} catch (Throwable exception) {
			handleException(exception);
		};
		return null;
	}

	/**
	 * Return the property descriptors for this bean.
	 * @return java.beans.PropertyDescriptor[]
	 */
	public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
		try {
			java.beans.PropertyDescriptor aDescriptorList[] = {
				addressPropertyDescriptor()
				,businessPhonePropertyDescriptor()
				,cityPropertyDescriptor()
				,countryPropertyDescriptor()
				,homePhonePropertyDescriptor()
				,namePropertyDescriptor()
				,postalCodePropertyDescriptor()
				,statePropertyDescriptor()
			};
			return aDescriptorList;
		} catch (Throwable exception) {
			handleException(exception);
		};
		return null;
	}

	/**
	 * Called whenever the bean information class throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		// exception.printStackTrace(System.out);
	}

	/**
	 * Gets the homePhone property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor homePhonePropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the homePhone property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getHomePhone", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getHomePhone", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						java.lang.String.class
					};
					aSetMethod = getBeanClass().getMethod("setHomePhone", aSetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setHomePhone", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("homePhone"
				, aGetMethod, aSetMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("homePhone"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			aDescriptor.setDisplayName("home phone");
			aDescriptor.setShortDescription("the person's home phone number");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
			/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the name property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor namePropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the name property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getName", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getName", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						java.lang.String.class
					};
					aSetMethod = getBeanClass().getMethod("setName", aSetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setName", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("name"
				, aGetMethod, aSetMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("name"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("name"); */
			aDescriptor.setShortDescription("a person's name");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
			/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the postalCode property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor postalCodePropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the postalCode property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getPostalCode", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getPostalCode", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						java.lang.String.class
					};
					aSetMethod = getBeanClass().getMethod("setPostalCode", aSetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setPostalCode", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("postalCode"
				, aGetMethod, aSetMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("postalCode"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			aDescriptor.setDisplayName("postal code");
			aDescriptor.setShortDescription("the zip/postal code where the person lives");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
			/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the state property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor statePropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the state property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getState", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getState", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						java.lang.String.class
					};
					aSetMethod = getBeanClass().getMethod("setState", aSetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setState", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("state"
				, aGetMethod, aSetMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("state"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("state"); */
			aDescriptor.setShortDescription("the state where the person lives");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
			/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}
}
