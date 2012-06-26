package com.abreqadhabra.freelec.java.demo.addressbook.mvc.controller;

import com.abreqadhabra.freelec.java.demo.addressbook.mvc.bin.AddressBookPatternSelectionListUI;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.model.AddressBookModel;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.model.AddressDataModel;
import com.abreqadhabra.freelec.java.demo.addressbook.mvc.view.AddressDataSelectionModel;

/**
 * The bean information class for AddressBookPatternSelectionListUI.
 */
public class AddressBookPatternSelectionListUIBeanInfo extends java.beans.SimpleBeanInfo {

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
		return AddressBookPatternSelectionListUI.class;
	}

	/**
	 * Gets the bean class name.
	 * @return java.lang.String
	 */
	public static java.lang.String getBeanClassName() {
		return "AddressBookPatternSelectionListUI";
	}

	/**
	 * Gets the addPropertyChangeListener(java.beans.PropertyChangeListener) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor addPropertyChangeListener_javabeansPropertyChangeListenerMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the addPropertyChangeListener(java.beans.PropertyChangeListener) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					java.beans.PropertyChangeListener.class
				};
				aMethod = getBeanClass().getMethod("addPropertyChangeListener", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "addPropertyChangeListener", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("listener");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("addPropertyChangeListener(java.beans.PropertyChangeListener)"); */
			/* aDescriptor.setShortDescription("addPropertyChangeListener(java.beans.PropertyChangeListener)"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the addressBookModel property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor addressBookModelPropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the addressBookModel property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getAddressBookModel", aGetMethodParameterTypes);
				} catch (java.lang.Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getAddressBookModel", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						AddressBookModel.class
					};
					aSetMethod = getBeanClass().getMethod("setAddressBookModel", aSetMethodParameterTypes);
				} catch (java.lang.Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setAddressBookModel", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("addressBookModel"
				, aGetMethod, aSetMethod);
			} catch (java.lang.Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("addressBookModel"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("addressBookModel"); */
			/* aDescriptor.setShortDescription("addressBookModel"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
			/* aDescriptor.setValue("ivjDesignTimeProperty", new java.lang.Boolean(true)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the addressDataSelectionModel property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor addressDataSelectionModelPropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the addressDataSelectionModel property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getAddressDataSelectionModel", aGetMethodParameterTypes);
				} catch (java.lang.Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getAddressDataSelectionModel", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						AddressDataSelectionModel.class
					};
					aSetMethod = getBeanClass().getMethod("setAddressDataSelectionModel", aSetMethodParameterTypes);
				} catch (java.lang.Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setAddressDataSelectionModel", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("addressDataSelectionModel"
				, aGetMethod, aSetMethod);
			} catch (java.lang.Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("addressDataSelectionModel"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("addressDataSelectionModel"); */
			/* aDescriptor.setShortDescription("addressDataSelectionModel"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
			/* aDescriptor.setValue("ivjDesignTimeProperty", new java.lang.Boolean(true)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the changeSelection(AddressDataModel, java.awt.List) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor changeSelection_comjavadudearticlesvaddmvc1AddressDataModel_javaawtListMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the changeSelection(AddressDataModel, java.awt.List) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					AddressDataModel.class,
					java.awt.List.class
				};
				aMethod = getBeanClass().getMethod("changeSelection", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "changeSelection", 2);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("data");
				java.beans.ParameterDescriptor aParameterDescriptor2 = new java.beans.ParameterDescriptor();
				aParameterDescriptor2.setName("arg2");
				aParameterDescriptor2.setDisplayName("list");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1,
					aParameterDescriptor2
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("changeSelection(AddressDataModel, java.awt.List)"); */
			/* aDescriptor.setShortDescription("changeSelection(AddressDataModel, java.awt.List)"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor firePropertyChange_javalangString_javalangObject_javalangObjectMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					java.lang.String.class,
					java.lang.Object.class,
					java.lang.Object.class
				};
				aMethod = getBeanClass().getMethod("firePropertyChange", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "firePropertyChange", 3);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("propertyName");
				java.beans.ParameterDescriptor aParameterDescriptor2 = new java.beans.ParameterDescriptor();
				aParameterDescriptor2.setName("arg2");
				aParameterDescriptor2.setDisplayName("oldValue");
				java.beans.ParameterDescriptor aParameterDescriptor3 = new java.beans.ParameterDescriptor();
				aParameterDescriptor3.setName("arg3");
				aParameterDescriptor3.setDisplayName("newValue");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1,
					aParameterDescriptor2,
					aParameterDescriptor3
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)"); */
			/* aDescriptor.setShortDescription("firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
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
	 * Gets the getAddressBookModel() method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor getAddressBookModelMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the getAddressBookModel() method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {};
				aMethod = getBeanClass().getMethod("getAddressBookModel", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "getAddressBookModel", 0);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptors[] = {};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("getAddressBookModel()"); */
			/* aDescriptor.setShortDescription("getAddressBookModel()"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the getAddressDataSelectionModel() method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor getAddressDataSelectionModelMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the getAddressDataSelectionModel() method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {};
				aMethod = getBeanClass().getMethod("getAddressDataSelectionModel", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "getAddressDataSelectionModel", 0);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptors[] = {};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("getAddressDataSelectionModel()"); */
			/* aDescriptor.setShortDescription("getAddressDataSelectionModel()"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	public java.beans.BeanDescriptor getBeanDescriptor() {
		java.beans.BeanDescriptor aDescriptor = null;
		try {
			/* Create and return the AddressBookPatternSelectionListUIBeanInfo bean descriptor. */
			aDescriptor = new java.beans.BeanDescriptor(AddressBookPatternSelectionListUI.class);
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
			java.beans.EventSetDescriptor aDescriptorList[] = {
				propertyChangeEventSetDescriptor()
			};
			return aDescriptorList;
		} catch (java.lang.Throwable exception) {
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
				addPropertyChangeListener_javabeansPropertyChangeListenerMethodDescriptor()
				,changeSelection_comjavadudearticlesvaddmvc1AddressDataModel_javaawtListMethodDescriptor()
				,firePropertyChange_javalangString_javalangObject_javalangObjectMethodDescriptor()
				,getAddressBookModelMethodDescriptor()
				,getAddressDataSelectionModelMethodDescriptor()
				,getPatternedThingMethodDescriptor()
				,main_javalangString__MethodDescriptor()
				,removePropertyChangeListener_javabeansPropertyChangeListenerMethodDescriptor()
				,setAddressBookModel_comjavadudearticlesvaddmvc1AddressBookModelMethodDescriptor()
				,setAddressDataSelectionModel_comjavadudearticlesvaddmvc2AddressDataSelectionModelMethodDescriptor()
				,setPatternedThing_comjavadudearticlesvaddmvc2PatternedMethodDescriptor()
			};
			return aDescriptorList;
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return null;
	}

	/**
	 * Gets the getPatternedThing() method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor getPatternedThingMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the getPatternedThing() method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {};
				aMethod = getBeanClass().getMethod("getPatternedThing", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "getPatternedThing", 0);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptors[] = {};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("getPatternedThing()"); */
			/* aDescriptor.setShortDescription("getPatternedThing()"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Return the property descriptors for this bean.
	 * @return java.beans.PropertyDescriptor[]
	 */
	public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
		try {
			java.beans.PropertyDescriptor aDescriptorList[] = {
				addressBookModelPropertyDescriptor()
				,addressDataSelectionModelPropertyDescriptor()
				,patternedThingPropertyDescriptor()
			};
			return aDescriptorList;
		} catch (java.lang.Throwable exception) {
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
	 * Gets the main(java.lang.String[]) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor main_javalangString__MethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the main(java.lang.String[]) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					java.lang.String[].class
				};
				aMethod = getBeanClass().getMethod("main", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "main", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("args");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("main(java.lang.String[])"); */
			/* aDescriptor.setShortDescription("main(java.lang.String[])"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the patternedThing property descriptor.
	 * @return java.beans.PropertyDescriptor
	 */
	public java.beans.PropertyDescriptor patternedThingPropertyDescriptor() {
		java.beans.PropertyDescriptor aDescriptor = null;
		try {
			try {
				/* Using methods via getMethod is the faster way to create the patternedThing property descriptor. */
				java.lang.reflect.Method aGetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aGetMethodParameterTypes[] = {};
					aGetMethod = getBeanClass().getMethod("getPatternedThing", aGetMethodParameterTypes);
				} catch (java.lang.Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aGetMethod = findMethod(getBeanClass(), "getPatternedThing", 0);
				};
				java.lang.reflect.Method aSetMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aSetMethodParameterTypes[] = {
						Patterned.class
					};
					aSetMethod = getBeanClass().getMethod("setPatternedThing", aSetMethodParameterTypes);
				} catch (java.lang.Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aSetMethod = findMethod(getBeanClass(), "setPatternedThing", 1);
				};
				aDescriptor = new java.beans.PropertyDescriptor("patternedThing"
				, aGetMethod, aSetMethod);
			} catch (java.lang.Throwable exception) {
				/* Since we failed using methods, try creating a default property descriptor. */
				handleException(exception);
				aDescriptor = new java.beans.PropertyDescriptor("patternedThing"
				, getBeanClass());
			};
			aDescriptor.setBound(true);
			/* aDescriptor.setConstrained(false); */
			/* aDescriptor.setDisplayName("patternedThing"); */
			/* aDescriptor.setShortDescription("patternedThing"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
			/* aDescriptor.setValue("ivjDesignTimeProperty", new java.lang.Boolean(true)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the propertyChange event set descriptor.
	 * @return java.beans.EventSetDescriptor
	 */
	public java.beans.EventSetDescriptor propertyChangeEventSetDescriptor() {
		java.beans.EventSetDescriptor aDescriptor = null;
		try {
			try {
				/* Try using method descriptors to create the propertyChange event set descriptor. */
				java.beans.MethodDescriptor eventMethodDescriptors[] = {
					propertyChangepropertyChange_javabeansPropertyChangeEventMethodEventDescriptor()			};
				java.lang.reflect.Method anAddMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class anAddMethodParameterTypes[] = {
						java.beans.PropertyChangeListener.class
					};
					anAddMethod = getBeanClass().getMethod("addPropertyChangeListener", anAddMethodParameterTypes);
				} catch (java.lang.Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					anAddMethod = findMethod(getBeanClass(), "addPropertyChangeListener", 1);
				};
				java.lang.reflect.Method aRemoveMethod = null;
				try {
					/* Attempt to find the method using getMethod with parameter types. */
					java.lang.Class aRemoveMethodParameterTypes[] = {
						java.beans.PropertyChangeListener.class
					};
					aRemoveMethod = getBeanClass().getMethod("removePropertyChangeListener", aRemoveMethodParameterTypes);
				} catch (java.lang.Throwable exception) {
					/* Since getMethod failed, call findMethod. */
					handleException(exception);
					aRemoveMethod = findMethod(getBeanClass(), "removePropertyChangeListener", 1);
				};
				aDescriptor = new java.beans.EventSetDescriptor(
							"propertyChange", 
							java.beans.PropertyChangeListener.class, 
							eventMethodDescriptors, anAddMethod, aRemoveMethod);
			} catch (java.lang.Throwable exception) {
				/* Using method descriptors failed, try using the methods names. */
				handleException(exception);
				java.lang.String eventMethodNames[] = {
					"propertyChange"			};
				aDescriptor = new java.beans.EventSetDescriptor(getBeanClass(), 
							"propertyChange", 
							java.beans.PropertyChangeListener.class, 
							eventMethodNames, 
							"addPropertyChangeListener", 
							"removePropertyChangeListener");
			};
			/* aDescriptor.setUnicast(false); */
			/* aDescriptor.setDisplayName("propertyChange"); */
			/* aDescriptor.setShortDescription("propertyChange"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the propertyChange.propertyChange(java.beans.PropertyChangeEvent) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor propertyChangepropertyChange_javabeansPropertyChangeEventMethodEventDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the propertyChange.propertyChange(java.beans.PropertyChangeEvent) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					java.beans.PropertyChangeEvent.class
				};
				aMethod = (java.beans.PropertyChangeListener.class).getMethod("propertyChange", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod((java.beans.PropertyChangeListener.class), "propertyChange", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("evt");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("propertyChange"); */
			/* aDescriptor.setShortDescription("propertyChange"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the removePropertyChangeListener(java.beans.PropertyChangeListener) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor removePropertyChangeListener_javabeansPropertyChangeListenerMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the removePropertyChangeListener(java.beans.PropertyChangeListener) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					java.beans.PropertyChangeListener.class
				};
				aMethod = getBeanClass().getMethod("removePropertyChangeListener", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "removePropertyChangeListener", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("listener");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("removePropertyChangeListener(java.beans.PropertyChangeListener)"); */
			/* aDescriptor.setShortDescription("removePropertyChangeListener(java.beans.PropertyChangeListener)"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the setAddressBookModel(AddressBookModel) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor setAddressBookModel_comjavadudearticlesvaddmvc1AddressBookModelMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the setAddressBookModel(AddressBookModel) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					AddressBookModel.class
				};
				aMethod = getBeanClass().getMethod("setAddressBookModel", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "setAddressBookModel", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("arg1");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("setAddressBookModel(AddressBookModel)"); */
			/* aDescriptor.setShortDescription("setAddressBookModel(AddressBookModel)"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the setAddressDataSelectionModel(AddressDataSelectionModel) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor setAddressDataSelectionModel_comjavadudearticlesvaddmvc2AddressDataSelectionModelMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the setAddressDataSelectionModel(AddressDataSelectionModel) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					AddressDataSelectionModel.class
				};
				aMethod = getBeanClass().getMethod("setAddressDataSelectionModel", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "setAddressDataSelectionModel", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("arg1");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("setAddressDataSelectionModel(AddressDataSelectionModel)"); */
			/* aDescriptor.setShortDescription("setAddressDataSelectionModel(AddressDataSelectionModel)"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}

	/**
	 * Gets the setPatternedThing(Patterned) method descriptor.
	 * @return java.beans.MethodDescriptor
	 */
	public java.beans.MethodDescriptor setPatternedThing_comjavadudearticlesvaddmvc2PatternedMethodDescriptor() {
		java.beans.MethodDescriptor aDescriptor = null;
		try {
			/* Create and return the setPatternedThing(Patterned) method descriptor. */
			java.lang.reflect.Method aMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aParameterTypes[] = {
					Patterned.class
				};
				aMethod = getBeanClass().getMethod("setPatternedThing", aParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aMethod = findMethod(getBeanClass(), "setPatternedThing", 1);
			};
			try {
				/* Try creating the method descriptor with parameter descriptors. */
				java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
				aParameterDescriptor1.setName("arg1");
				aParameterDescriptor1.setDisplayName("arg1");
				java.beans.ParameterDescriptor aParameterDescriptors[] = {
					aParameterDescriptor1
				};
				aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
			} catch (java.lang.Throwable exception) {
				/* Try creating the method descriptor without parameter descriptors. */
				handleException(exception);
				aDescriptor = new java.beans.MethodDescriptor(aMethod);
			};
			/* aDescriptor.setDisplayName("setPatternedThing(Patterned)"); */
			/* aDescriptor.setShortDescription("setPatternedThing(Patterned)"); */
			/* aDescriptor.setExpert(false); */
			/* aDescriptor.setHidden(false); */
			/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		} catch (java.lang.Throwable exception) {
			handleException(exception);
		};
		return aDescriptor;
	}
}
