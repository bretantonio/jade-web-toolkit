package com.javadude.articles.vaddmvc1;

/**
 * The bean information class for com.javadude.articles.vaddmvc1.HashAddressBook.
 *
 * This sample code is provided "as is" and is
 * intended for demonstration purposes only.
 * 
 * Neither Scott Stanchfield nor IBM shall be
 * held liable for any damages resulting from your
 * use of this code.
 * 
 * Creation date: (1/15/00 8:13:21 PM)
 * @author: Scott Stanchfield
 */
public class HashAddressBookBeanInfo extends java.beans.SimpleBeanInfo {

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
		return com.javadude.articles.vaddmvc1.HashAddressBook.class;
	}

	/**
	 * Gets the bean class name.
	 * @return java.lang.String
	 */
	public static java.lang.String getBeanClassName() {
		return "com.javadude.articles.vaddmvc1.HashAddressBook";
	}

	/**
	 * Gets the add(com.javadude.articles.vaddmvc1.AddressDataModel) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor add_comjavadudearticlesvaddmvc1AddressDataModelMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the add(com.javadude.articles.vaddmvc1.AddressDataModel) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					com.javadude.articles.vaddmvc1.AddressDataModel.class
				};
				aMethod = getBeanClass().getMethod("add", aParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "add", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("address");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("add(com.javadude.articles.vaddmvc1.AddressDataModel)"); */
			aDescriptor.setShortDescription("add an address entry to the phone book");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the addressDataFactory property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor addressDataFactoryPropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the addressDataFactory property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getAddressDataFactory", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getAddressDataFactory", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						com.javadude.articles.vaddmvc1.AddressDataFactoryModel.class
					};
					aSetMethod = getBeanClass().getMethod("setAddressDataFactory", aSetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setAddressDataFactory", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("addressDataFactory"
				, aGetMethod, aSetMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("addressDataFactory"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("addressDataFactory"); */
			aDescriptor.setShortDescription("a factory that we use to make copies of address data passed in");
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
	 * Gets the addresses property descriptor.
	 * @return java.beans.IndexedPropertyDescriptor
	 */
	public java.beans.IndexedPropertyDescriptor addressesPropertyDescriptor() {
		java.beans.IndexedPropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the addresses property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getAddresses", aGetMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getAddresses", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				java.lang.reflect.Method aGetIndexedMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetIndexedMethodParameterTypes[] = {
						int.class
					};
					aGetIndexedMethod = getBeanClass().getMethod("getAddresses", aGetIndexedMethodParameterTypes);
				} catch (Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetIndexedMethod = findMethod(getBeanClass(), "getAddresses", 1);
				};
				java.lang.reflect.Method aSetIndexedMethod = null;
				aDescriptor = new java.beans.IndexedPropertyDescriptor("addresses"
				, aGetMethod, aSetMethod, aGetIndexedMethod, aSetIndexedMethod);
			} catch (Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.IndexedPropertyDescriptor("addresses"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("addresses"); */
			aDescriptor.setShortDescription("list of all addresses");
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
	 * Gets the find(java.lang.String[]) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor find_javalangString__MethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the find(java.lang.String[]) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					java.lang.String[].class
				};
				aMethod = getBeanClass().getMethod("find", aParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "find", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("name");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("find(java.lang.String[])"); */
			aDescriptor.setShortDescription("search for an address record");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the find(java.lang.String) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor find_javalangStringMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the find(java.lang.String) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					java.lang.String.class
				};
				aMethod = getBeanClass().getMethod("find", aParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "find", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("name");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("find(java.lang.String)"); */
			aDescriptor.setShortDescription("search for an address record");
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
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

	/**
	 * Gets the getAddresses(int) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor getAddresses_intMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the getAddresses(int) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					int.class
				};
				aMethod = getBeanClass().getMethod("getAddresses", aParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "getAddresses", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("index");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("getAddresses(int)"); */
			/* aDescriptor.setShortDescription("getAddresses(int)"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			aDescriptor.setValue("preferred", new Boolean(true));
		} catch (Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	public java.beans.BeanDescriptor getBeanDescriptor() {
		java.beans.BeanDescriptor aDescriptor = null;
		try {
			/* Create and return the HashAddressBookBeanInfo bean descriptor. */
			aDescriptor = new java.beans.BeanDescriptor(com.javadude.articles.vaddmvc1.HashAddressBook.class);
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
			java.beans.MethodDescriptor aDescriptorList[] = {
				add_comjavadudearticlesvaddmvc1AddressDataModelMethodDescriptor()
				,find_javalangStringMethodDescriptor()
				,getAddresses_intMethodDescriptor()
			};
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
				addressDataFactoryPropertyDescriptor()
				,addressesPropertyDescriptor()
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
}
