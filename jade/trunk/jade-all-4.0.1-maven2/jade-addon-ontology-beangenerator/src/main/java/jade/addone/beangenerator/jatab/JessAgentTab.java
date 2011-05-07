/**
 * JessAgentTab, Protege plugin to convert ontologies into the JESS format.
 * Based partially on the Java ontology Bean generator (http://gaper.swi.psy.uva.nl/beangenerator/content/main.php)
 * Copyright 2003-2004
 *
 *  JADE - Java Agent DEvelopment Framework is a framework to develop
 *  multi-agent systems in compliance with the FIPA specifications. Copyright
 *  (C) 2002 TILAB S.p.A. This file is donated by Y'All B.V. to the JADE
 *  project. GNU Lesser General Public License This library is free software;
 *  you can redistribute it and/or modify it under the terms of the GNU Lesser
     *  General Public License as published by the Free Software Foundation, version
 *  2.1 of the License. This library is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library;
 *  if not, write  to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 *  Boston, MA 02111-1307, USA.
 *  **************************************************************
 */

package jade.addone.beangenerator.jatab;

// Java
import java.io.*;
import java.util.*;

import javax.swing.*;

// Protege
import edu.stanford.smi.protege.model.*;

/**
 * Main class which handles the Ontology conversion.
 *
 * @author Markus Lingner
 * @author Stephan Deininghaus
 * @author Tim Kosse
 */
public class JessAgentTab {

  /**
   * Main entry point from the GUI. Called when the user presses the generate
   * button on the interface tab. Will allso be called from the <code>convertProject</code>
   * method.
   */
  public void doIt() {

    // reset all global fields, fetch properties and JADE specific slot definitions
    reset();

    // Get the classes and all subclasses from the knowledge base
    Cls conceptCls = itsKB.getCls(CONCEPT);
    if (conceptCls == null) {
      printStatus(
          "Warning: No concept with the name '" + CONCEPT + "' found.");
      return;
    }
    Collection conceptCol = new HashSet(conceptCls.getSubclasses());

    // Get instances from knowledge base
    Collection instanceCol = new HashSet(conceptCls.getInstances());

    // Get agent action classes from AgentAction branch of the knowledge base
    Cls agtactRootCls = itsKB.getCls(AGENTACTION);
    if (agtactRootCls == null) { // missing/misplaced AgentAction branch is non-fatal
      printStatus("Warning: No agent action root ('" + AGENTACTION +
                  "') found.");
    }
    else if (!conceptCls.getDirectSubclasses().contains(agtactRootCls)) {
      printStatus("Warning: '" + AGENTACTION +
                  "' is not a direct subclass of '" + CONCEPT + "'.");
    }

    Collection agtactClsCol = null;
    if (agtactRootCls != null) {
      agtactClsCol = new HashSet(agtactRootCls.getSubclasses());
      // don't attempt to convert agent actions as concepts
      conceptCol.remove(agtactRootCls);
      conceptCol.removeAll(agtactClsCol);
    }

    // setup the JProgressBar
    int max = 0;
    if (conceptCol != null) {
      max += conceptCol.size();
    }
    if (instanceCol != null) {
      max += instanceCol.size();
    }
    if (agtactClsCol != null) {
      max += agtactClsCol.size();

      // Convert classes and generate ontology file
    }
    if (conceptCol != null) {
      ConvertClasses(conceptCol);

      // Convert instances and generate instances file
    }
    if (instanceCol != null) {
      ConvertInstances(instanceCol);

      // Convert agent actions and generate actions file
    }
    if (agtactClsCol != null) {
      ConvertActions(agtactClsCol);
    }
  }

  /**
   * Returns the name of the project, or "ontology" if project name can't be retrieved.
   */
  private String getProjectName() {
    if (itsProject != null) {
      return itsProject.getName();
    }

    // Return default value
    return "ontology";
  }

  /**
   * Replaces all spaces in slot and class names
   *
   * @param ClassName class or slot name
   * @return modified class or slot name
   */
  String convertNames(String ClassName) {
    return ClassName.replaceAll(" ", "_");
  }

  /**
   * Converts the classes
   *
   * @param theClasses	Collection which contains the classes to convert.
   * 								The classes do not have to be in any specific order.
   */
  private void ConvertClasses(Collection theClasses) {

    Iterator classIter = theClasses.iterator();

    // String to hold the constraints
    StringBuffer constraints = new StringBuffer();

    try {
      File f = new File(dirName + "/" + getProjectName() + ".ont");
      FileOutputStream fos = new java.io.FileOutputStream(f);
      OutputStream bos = new BufferedOutputStream(fos);
      PrintStream ps = new PrintStream(bos);

      printStatus("Converting classes:");
      while (classIter.hasNext()) {
        Cls theCls = (Cls) classIter.next();

        // if it's not a system class and not abstract
        if (theCls.getName().charAt(0) != ':'
            && !theCls.isAbstract()) {
          printStatus("  " + theCls.getName());

          ps.print("(deftemplate " + convertNames(theCls.getName()));

          Collection slots = theCls.getTemplateSlots();
          Iterator slotIter = slots.iterator();

          // For each slot
          while (slotIter.hasNext()) {
            Slot slot = (Slot) slotIter.next();

            // Convert slot name
            if (slot.getAllowsMultipleValues()) {
              ps.print("\n  (multislot " + convertNames(slot.getName()));
            }
            else {
              ps.print("\n  (slot " + convertNames(slot.getName()));

              // Convert default value
            }
            String defValues = ConvertDefaultValues(slot);
            ps.print(defValues);

            // Convert slot type
            String slotType =
                ConvertSlotType(theCls, slot, constraints);
            ps.print(slotType);

            ps.print(")");
          }
          ps.println("\n)\n");
        }
        // end if
        // update the progress bar
      }
      // end while

      // write constraints to file
      ps.print(constraints);

      // Close output file
      ps.flush();
      ps.close();
      ps = null;
      bos.close();
      bos = null;
      fos.close();
      fos = null;
      f = null;
    }
    catch (FileNotFoundException e) {
      printStatus("Error converting classes:\n" + e);
    }
    catch (IOException e) {
      printStatus("Error converting classes:\n" + e);
    }
  }

  /**
   * Converts the slot type into Jess code and creates slot constraints.
   * @param cls				The class which contains the slot.
   * @param slot				The slot which should be converted.
       * @param constraints	This <code>StringBuffer</code> will contain any constraints
   * 								for the slot created by this method.
   * @return					Returns the slot type in JESS code.
   */
  String ConvertSlotType(Cls cls, Slot slot, StringBuffer constraints) {
    String type = new String();

    // Get slot type
    ValueType vt = slot.getValueType();

    // Convert slot type (if possible)
    if (vt == ValueType.INTEGER) {
      if (slot.getAllowsMultipleValues()) {
        type += ") ;";
      }
      type += " (type INTEGER)";

      // Add constraints
      Number min = slot.getMinimumValue();
      if (min != null) {
        constraints.append(
            "\n\n(defrule constraint."
            + convertNames(cls.getName())
            + "."
            + convertNames(slot.getName())
            + ".min\n");
        constraints.append(
            "  ?fact <- ("
            + convertNames(cls.getName())
            + " ("
            + convertNames(slot.getName())
            + " ?slotvalue))\n");
        constraints.append(
            "  (test (< ?slotvalue " + min.intValue() + "))\n");
        constraints.append(" =>\n  (retract ?fact)\n)");
      }

      Number max = slot.getMaximumValue();
      if (max != null) {
        constraints.append(
            "\n\n(defrule constraint."
            + convertNames(cls.getName())
            + "."
            + convertNames(slot.getName())
            + ".max\n");
        constraints.append(
            "  ?fact <- ("
            + convertNames(cls.getName())
            + " ("
            + convertNames(slot.getName())
            + " ?slotvalue))\n");
        constraints.append(
            "  (test (> ?slotvalue " + max.intValue() + "))\n");
        constraints.append(" =>\n  (retract ?fact)\n)");
      }
    }
    else if (vt == ValueType.STRING) {

      if (slot.getAllowsMultipleValues()) {
        type += ") ;";
      }
      type += " (type STRING)";
    }
    else if (vt == ValueType.FLOAT) {
      if (slot.getAllowsMultipleValues()) {
        type += ") ;";
      }
      type += " (type FLOAT)";

      // Add constraints
      Number min = slot.getMinimumValue();
      if (min != null) {
        constraints.append(
            "\n\n(defrule constraint."
            + convertNames(cls.getName())
            + "."
            + convertNames(slot.getName())
            + ".min\n");
        constraints.append(
            "  ?fact <- ("
            + convertNames(cls.getName())
            + " ("
            + convertNames(slot.getName())
            + " ?slotvalue))\n");
        constraints.append(
            "  (test (< ?slotvalue " + min.floatValue() + "))\n");
        constraints.append(" =>\n  (retract ?fact)\n)");
      }

      Number max = slot.getMaximumValue();
      if (min != null) {
        constraints.append(
            "\n\n(defrule constraint."
            + convertNames(cls.getName())
            + "."
            + convertNames(slot.getName())
            + ".max\n");
        constraints.append(
            "  ?fact <- ("
            + convertNames(cls.getName())
            + " ("
            + convertNames(slot.getName())
            + " ?slotvalue))\n");
        constraints.append(
            "  (test (> ?slotvalue " + max.floatValue() + "))\n");
        constraints.append(" =>\n  (retract ?fact)\n)");
      }
    }
    else if (vt == ValueType.INSTANCE) {
      printStatus(
          "    Warning: Slot type 'INSTANCE' for slot '"
          + convertNames(slot.getName())
          + "' not fully supported by JESS yet");

      Collection slotTypes = slot.getAllowedClses();
      if (slotTypes.size() >= 1) {
        Collection nonAbstractSlotTypes = new LinkedList();
        Iterator slotTypesIter = slotTypes.iterator();
        while (slotTypesIter.hasNext()) {
          Cls slotType = (Cls) slotTypesIter.next();
          if (!slotType.isAbstract()) {
            nonAbstractSlotTypes.add(slotType);
          }
        }
        if (nonAbstractSlotTypes.size() == 1) {
          Iterator nonAbstractSlotTypesIter =
              nonAbstractSlotTypes.iterator();
          Cls nonAbstractSlotType =
              (Cls) nonAbstractSlotTypesIter.next();
          type += ") ; (type " + nonAbstractSlotType.getName();
        }
      }
    }
    else if (vt == ValueType.SYMBOL) {

      type += "); (type SYMBOL";

      // Add constraints
      Collection allowedValues = slot.getAllowedValues();

      if (allowedValues != null) {
        constraints.append(
            "\n\n(defrule constraint."
            + convertNames(cls.getName())
            + "."
            + convertNames(slot.getName())
            + ".symbolChk\n");
        constraints.append(
            "  ?fact <- ("
            + convertNames(cls.getName())
            + " ("
            + convertNames(slot.getName())
            + " ?slotvalue))\n");
        constraints.append(" =>\n  "
                           + " (bind $?allowedSymbols (create$");
        Iterator myIterator = allowedValues.iterator();
        while (myIterator.hasNext()) {
          constraints.append(" " + myIterator.next());
        }
        constraints.append(
            "))\n	(if (not (subsetp (create$ ?slotvalue) $?allowedSymbols))\n"
            + "		then\n"
            + "			(retract ?fact)\n"
            + "	)\n)\n");
      }

    }
    return type;
  }

  /**
   * Converts the default value for a given slot
   * @param slow	The default values of this slot will be converted
   * @return 			Default slot values in JESS code
   */
  String ConvertDefaultValues(Slot slot) {
    String def = new String();
    // Get slot type
    ValueType vt = slot.getValueType();

    //  Add default values
    Collection defValues = slot.getDefaultValues();
    if (defValues != null) {
      Iterator defIter = defValues.iterator();

      if (defValues.size() > 1) {
        if (!slot.getAllowsMultipleValues()) {
          printStatus(
              "    Warning: Multiple default values on single value slot "
              + slot.getName()
              + ", skipping additional default values");
        }
        else {
          def = " (default (create $";

          while (defIter.hasNext()) {
            if (vt == ValueType.STRING) {
              def += " \"" + defIter.next().toString() + "\"";
            }
            else if (vt != ValueType.INSTANCE) {
              def += " " + defIter.next().toString();
            }
            else {
              printStatus(
                  "    Warning: Skipping unsupported default value type: "
                  + vt.toString()
                  + " with value "
                  + defIter.next().toString()
                  + " on slot "
                  + slot.getName());
              continue;
            }
          }
          def += "))";
          return def;
        }
      }
      if (defValues.size() > 0) {
        if (vt == ValueType.STRING) {
          def = " (default \"" + defIter.next().toString() + "\")";
        }
        else if (vt != ValueType.INSTANCE) {
          def = " (default " + defIter.next().toString() + ")";
        }
        else {
          printStatus(
              "    Warning: Skipping unsupported default value type: "
              + vt.toString()
              + " with value "
              + defIter.next().toString()
              + " on slot "
              + slot.getName());
          return "";
        }
      }
    }
    return def;
  }

  /**
   * Convert the Protege "Instance" objects to JESS code. Conversion is a
   * three-stage process:
   * First, an encapsulating InstanceConversionWrapper object is created and
   * initialized for each instance in the theInstances collection.
   * Then, these wrappers are arranged sequentially so that interdependencies
   * between instances are resolved by putting the declaration of dependent
   * instances after the declaration of the instances they require.
   * Finally, this sequence is written to the output file, and if necessary,
   * statements are appended that resolve cases where step 2 failed to find a
   * working order (ie. cyclic interdependencies).
   *
   * @param theInstances	Instances to include in output file
   */
  private void ConvertInstances(Collection theInstances) {

    try {
      // create output file
      File f = new File(dirName + "/" + getProjectName() + ".ins");
      FileOutputStream fos = new java.io.FileOutputStream(f);
      OutputStream bos = new BufferedOutputStream(fos);
      PrintStream ps = new PrintStream(bos);

      printStatus("Converting instances:");

      HashSet wrappers = new HashSet();

      // wrappersIndex: Instance-to-InstanceConversionWrapper lookup, needed
      // for construction of the edges between the individual wrappers
      HashMap wrappersIndex = new HashMap();

      // for each instance:
      for (Iterator iter = theInstances.iterator(); iter.hasNext(); ) {
        Instance inst = (Instance) iter.next();

        // do not handle AgentActions instances
        if (itsKB.getCls(AGENTACTION) != null &&
            inst.getDirectType().hasDirectSuperclass(itsKB.getCls(AGENTACTION))) {
          continue;
        }

        InstanceConversionWrapper icw =
            new InstanceConversionWrapper(inst, this);
        wrappers.add(icw);
        wrappersIndex.put(inst, icw);
      }

      // for each instance:
      for (Iterator iter = wrappers.iterator(); iter.hasNext(); ) {
        InstanceConversionWrapper icw =
            (InstanceConversionWrapper) iter.next();
        icw.createDependencies(wrappersIndex);
      }

      LinkedList zeroInList = new LinkedList();
      LinkedList zeroOutList = new LinkedList();

      // generate topological order
      while (!wrappers.isEmpty()) {
        Collection removalSchedule = new LinkedList();

        for (Iterator iter = wrappers.iterator(); iter.hasNext(); ) {
          InstanceConversionWrapper icw =
              (InstanceConversionWrapper) iter.next();
          if (icw.isMarkedForRemoval()) {
            continue;
          }

          if (icw.getOutboundRefCount() == 0) {

            // does not depend on other (unhandled) instances
            icw.buildTopologyBackward(zeroOutList, removalSchedule);
          }
          else if (icw.getInboundRefCount() == 0) {

            // no other (unhandled) instances depend on this
            icw.buildTopologyForward(zeroInList, removalSchedule);
          }
        }

        // if no elements have been marked for removal from the graph, it
        // must contain a cyclic dependency somewhere. choose an element,
        // remove it from the graph, and iterate strategy.
        if (removalSchedule.isEmpty()) {
          // choose an element with maximal difference between the
          // numbers of incoming edges and outgoing edges
          InstanceConversionWrapper maxImbalancedWrapper = null;
          for (Iterator iter = wrappers.iterator();
               iter.hasNext();
               ) {
            InstanceConversionWrapper icw =
                (InstanceConversionWrapper) iter.next();
            if (maxImbalancedWrapper == null
                || Math.abs(icw.getBalance())
                > Math.abs(maxImbalancedWrapper.getBalance())) {
              maxImbalancedWrapper = icw;
            }
          }

          // forcibly remove this element
          maxImbalancedWrapper.breakCyclicDependency(
              zeroOutList,
              zeroInList,
              removalSchedule);
        }

        // clean graph to speed up further iterations
        if (removalSchedule.size() == wrappers.size()) {
          wrappers.clear();
        }
        else {
          wrappers.removeAll(removalSchedule);
        }
      }

      // now we're set up to actually produce some JESS code

      // each instance that has been sucessfully converted will be added to this set
      HashSet assertedInstances = new HashSet();

      // "modify" statements that need to be executed after all "assert"
      // statements have been executed
      StringBuffer postAssertionCmds = new StringBuffer();

      LinkedList[] lists = {
          zeroOutList, zeroInList};
      for (int i = 0; i < 2; i++) {
        for (Iterator iter = lists[i].iterator(); iter.hasNext(); ) {
          InstanceConversionWrapper icw =
              (InstanceConversionWrapper) iter.next();

          icw.toJessCode(ps, assertedInstances, postAssertionCmds);
          assertedInstances.add(icw.getInstance());

          // update the progress bar
        }
      } // for

      ps.print(postAssertionCmds);

      // Close instance file
      ps.flush();
      ps.close();
      ps = null;
      bos.close();
      bos = null;
      fos.close();
      fos = null;
      f = null;
    }
    catch (Exception e) {
      printStatus("Error converting instances:\n" + e);
    }
  }

  /**
   * Convert agent actions to JESS code.
   *
   * Arguments are passed to the generated JESS functions by supplying a single
   * fact, the slot values of which are treated as parameters. For example,
   * in the case of a function <code>MyFunc</code> that expects two parameters
   * <code>a</code> and <code>b</code>, the call to MyFunc would look like
   * <code>(MyFunc (assert (MyFunc (a 10) (b 42))))</code>.
   *
   * For each agent action MyAction, the following items are created:
       * - a deftemplate statement defining a MyAction fact template, with the action's
   * parameters as slots;
       * - a deffunction statement defining a MyAction function which takes a single
       * fact of type MyAction as parameter. The slot values of this fact are treated
   * as parameters to the function.
   * Additionally, a block of marshalling code is placed in each function that
   * extracts the slot values from the supplied fact and <code>bind</code>s
   * them to local variables of the same name. After that, the fact used to
   * encapsulate the parameters is <code>retract</code>ed.
   * This approach allows for greater flexibility in calling functions, making
   * parameter default values and such things come into reach.
   *
   * @param theActions	a collection of classes to be treated as agent actions.
   * 								Class slots are assumed to be arguments.
   */
  private void ConvertActions(Collection theActions) {
    try {
      printStatus("Converting actions:");

      Slot jessNameSlot = itsKB.getSlot(JESSNAMESLOT);
      if (jessNameSlot == null) {
        printStatus("  Warning: no such slot '" + JESSNAMESLOT +
            "'; you're missing out on the ability to specify JESS function names.");
      }

      Slot jessCodeSlot = itsKB.getSlot(JESSCODESLOT);
      if (jessCodeSlot == null) {
        printStatus("  Warning: no such slot '" + JESSCODESLOT +
            "'; you're missing out on the ability to specify JESS function bodies.");
      }

      // create file
      File f = new File(dirName + "/" + getProjectName() + ".act");
      FileOutputStream fos = new java.io.FileOutputStream(f);
      OutputStream bos = new BufferedOutputStream(fos);
      PrintStream ps = new PrintStream(bos);

      // for each action:
      for (Iterator iter = theActions.iterator(); iter.hasNext(); ) {
        Cls act = (Cls) iter.next();
        Collection slots = act.getTemplateSlots();

        String jessName = (jessNameSlot == null) ? null :
            (String) act.getOwnSlotValue(jessNameSlot);
        // if no JESS function name has been specified, default to Protege name
        if (jessName == null) {
          jessName = act.getName();

        }
        printStatus("  action: " + act.getName() + " (JESS name: " + jessName +
                    ")");

        // step 1: create template definition used to transport the action's parameters
        // (shameless rip from ConvertClasses, modified to suit ConvertAction's special needs.)
        ps.print("(deftemplate " + jessName);

        // For each slot
        for (Iterator slotIter = slots.iterator(); slotIter.hasNext(); ) {
          Slot slot = (Slot) slotIter.next();

          // Convert slot name
          if (slot.getAllowsMultipleValues()) {
            ps.print("\n  (multislot " + slot.getName());
          }
          else {
            ps.print("\n  (slot " + slot.getName());

            // Convert default value
          }
          String defValues = ConvertDefaultValues(slot);
          ps.print(defValues);

          // Convert slot type
          StringBuffer constraints = new StringBuffer();
          String slotType = ConvertSlotType(act, slot, constraints);
          ps.print(slotType);

          ps.print(")");
        }
        ps.println("\n)\n");

        // step 2: create the actual function of the same name as the template
        ps.print("(deffunction " + jessName + " (?jat_arg_fact)\n");

        // prepend parameter extraction stub
        for (Iterator slotIter = slots.iterator(); slotIter.hasNext(); ) {
          Slot slot = (Slot) slotIter.next();
          ps.print("  (bind ?" + slot.getName() +
                   " (fact-slot-value ?jat_arg_fact " + slot.getName() + "))\n");
        }
        ps.print("  (retract ?jat_arg_fact)\n\n");

        // user-defined function body
        String jessCode = (jessCodeSlot == null) ? null :
            (String) act.getOwnSlotValue(jessCodeSlot);
        if (jessCode != null) {
          // indent code
          String[] lines = jessCode.split("\\r\\n|\\n");
          for (int i = 0; i < lines.length; i++) {
            ps.print("  " + lines[i] + "\n");
          }
        }

        ps.print(")\n\n");

      } // end for

      // Close actions file
      ps.flush();
      ps.close();
      ps = null;
      bos.close();
      bos = null;
      fos.close();
      fos = null;
      f = null;
    }
    catch (Exception e) {
      printStatus("Error converting actions:\n" + e);
    }
  }

  /**
   * Initialisation code. Only called once during start up if using the gui.
   * Not called if using the <code>convertProject</code> method.
   */
  public void initialize(Project project, String dirname,
                         JTextArea statusTextArea) {
    // initialize the tab text
    itsProject = project;
    itsKB = project.getKnowledgeBase();
    this.dirName = dirname;
    itsStatusTextArea = statusTextArea;

  }

  /**
   *  Reset all global fields and read options
   */
  private void reset() {

    itsStatusTextArea.setText("");

    // if the output dir doesn't exist, create it and all its parents
    File outputDir = new File(dirName);
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

  }

  /**
   * Prints a status message. If using the gui, the status message will be
       * print on the status field on the tab, if using <code>convertProject</code>,
   * the message will be appended to the output collection.
   *
   * @param status	The status message to print
   */
  public void printStatus(String status) {
    itsStatusTextArea.append(status+"\n");
  }

  /**
   * Convert a project. Can be called from other packages to
   * convert a project without using the gui.
   *
   * @param projectFile	The name of the project file to convert
   * @param outputDir		Location at which to store the converted files
   * @param output			Since no gui is used, all output will be appended to this collection
   */

  private String dirName;
  // the knowledge base and project references
  private KnowledgeBase itsKB;
  private Project itsProject;
  // references to the jade specific slots
  private Slot jadeIgnoredSlot;
  private Slot jadeUnnamedSlot;
  private Slot javaJavaCodeSlot;
  private Slot javaSuperClassSlot;
  // options, taken from the GUI
  private StringBuffer nameMapping;
  private boolean noGui;
  private Collection noGuiOutput;
  private JTextArea itsStatusTextArea;

  // Constants for Protege
  private final static String CONCEPT = "Concept";
  private final static String AGENTACTION = "AgentAction";
  private final static String JESSNAMESLOT = ":JESS-NAME";
  private final static String JESSCODESLOT = ":JESS-CODE";
}
//  ***EOF***
