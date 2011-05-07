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

// Protege
import edu.stanford.smi.protege.model.*;

/**
 * A class that facilitates the generation of JESS statements which introduce
 * facts into the JESS knowledge base that correspond to given Protege
 * Instance objects. Also provides means for sequentially arranging multiple
 * instances in such a way that the number of potential problems in generated
 * JESS code (arising from dependencies among the instances) are minimal,  
 * and for remedying situations where such conflicts cannot be avoided.
 * 
 * @author Markus Lingner
 * @author Stephan Deininghaus
 * @author Tim Kosse

 */
public class InstanceConversionWrapper {

	//	construction

	/**
	 * Constructor; before using, another call to createDependencies will
	 * be necessary. 
	 * 
	 * @param theInstance	The Protege "Instance" object to convert
	 */
	public InstanceConversionWrapper(Instance theInstance, JessAgentTab tab) {
		this.theInstance = theInstance;
		this.inboundRefs = new HashSet();
		this.outboundRefs = new HashSet();
		this.requiredInstances = new HashSet();
		this.preserveJessFactID = false;
		this.markedForRemoval = false;
		this.tab = tab;
	}

	/**
	 * Translates dependencies between instances in the Protege knowledge
	 * base to dependencies between the corresponding InstanceConversionWrapper
	 * objects. Called once for initialization, after all InstanceConversionWrappers
	 * for required instances have been constructed.
	 * 
	 * @param wrappersIndex		A lookup table to find the appropriate
	 * 										InstanceConversionWrapper for any given Instance object
	 */
	public void createDependencies(AbstractMap wrappersIndex) {
		Collection slots = theInstance.getOwnSlots();
		// for each slot: handle contained references to other instances
		for (Iterator iter = slots.iterator(); iter.hasNext();) {
			Slot slot = (Slot) iter.next();

			if (slot.getName().charAt(0) == ':') // skip system slots
				continue;

			// skip slots that can't possibly hold instance references
			if (slot.getValueType() != ValueType.INSTANCE
				&& slot.getValueType() != ValueType.ANY)
				continue;

			Collection values = theInstance.getOwnSlotValues(slot);
			// for each value in slot:	
			for (Iterator valueIter = values.iterator();
				valueIter.hasNext();
				) {
				Object val = valueIter.next();
				if (val instanceof Instance) {
					InstanceConversionWrapper icw =
						(InstanceConversionWrapper) wrappersIndex.get(val);
					if (icw != null) {
						outboundRefs.add(icw);
						icw.addIncomingEdge(this);
						requiredInstances.add(icw.getInstance());
					}
				}
			} // for each value in slot
		} // for each slot
	}

	// attributes

	/** 
	 * The encapsulated Protege "Instance" object 
	 * */
	private Instance theInstance;

	/**
	 * A collection of InstanceConversionWrapper objects (and their
	 * encapsulated Protege Instances) which reference this object;
	 * modified (cleansed) upon establishing a topological ordering among
	 * the wrappers.
	 */
	private Collection inboundRefs;

	/**
	 * A collection of InstanceConversionWrapper objects this object
	 * depends on; modified (cleansed) upon establishing a topological
	 * ordering among the wrappers.
	 */
	private Collection outboundRefs;

	/**
	 * A collection of Instance objects this object depends on
	 */
	private Collection requiredInstances;

	/**
	 * Indicates that the JESS code generated for the encapsulated
	 * instance should include statements that store the fact-id returned
	 * by the "assert" statement for later use (eg. to reference it from
	 * other instances/facts).
	 */
	private boolean preserveJessFactID;

	/**
	 * Indicates that this instance has already been put into sequence
	 * for output, and that no further re-arranging of this instance should
	 * take place. Thus, it may be removed from any pool of instances
	 * awaiting arrangement.
	 */
	private boolean markedForRemoval;

	private JessAgentTab tab;
	// selectors

	public Instance getInstance() {
		return theInstance;
	}
	public int getInboundRefCount() {
		return inboundRefs.size();
	}
	public int getOutboundRefCount() {
		return outboundRefs.size();
	}

	/**
	 * Retrieves the difference between the number of inbound and outbound
	 * references.
	 * @return signed integer indicating this object's "reference balance"
	 */
	public int getBalance() {
		return inboundRefs.size() - outboundRefs.size();
	}
	public boolean isMarkedForRemoval() {
		return markedForRemoval;
	}

	// implementation

	private void addIncomingEdge(InstanceConversionWrapper from) {
		inboundRefs.add(from);

		// Since other instances rely on this instance, order the generated
		// JESS statements to keep the fact-id for later use.
		preserveJessFactID = true;
	}
	private void removeInboundRef(InstanceConversionWrapper from) {
		inboundRefs.remove(from);
	}
	private void removeOutboundRef(InstanceConversionWrapper to) {
		outboundRefs.remove(to);
	}

	private void propagateRemovalBackward(
		LinkedList zeroOutList,
		Collection removalSchedule) {
		for (Iterator iter = inboundRefs.iterator(); iter.hasNext();) {
			InstanceConversionWrapper icw =
				(InstanceConversionWrapper) iter.next();
			if (!icw.isMarkedForRemoval()) {
				icw.removeOutboundRef(this);
				if (icw.getOutboundRefCount() == 0)
					icw.buildTopologyBackward(zeroOutList, removalSchedule);
			}
		}
	}

	public void buildTopologyBackward(
		LinkedList zeroOutList,
		Collection removalSchedule) {
		markedForRemoval = true;
		removalSchedule.add(this);
		zeroOutList.addLast(this);

		propagateRemovalBackward(zeroOutList, removalSchedule);
	}

	private void propagateRemovalForward(
		LinkedList zeroInList,
		Collection removalSchedule) {
		for (Iterator iter = outboundRefs.iterator(); iter.hasNext();) {
			InstanceConversionWrapper icw =
				(InstanceConversionWrapper) iter.next();
			if (!icw.isMarkedForRemoval()) {
				icw.removeInboundRef(this);
				if (icw.getInboundRefCount() == 0)
					icw.buildTopologyForward(zeroInList, removalSchedule);
			}
		}
	}

	public void buildTopologyForward(
		LinkedList zeroInList,
		Collection removalSchedule) {
		markedForRemoval = true;
		removalSchedule.add(this);
		zeroInList.addFirst(this);

		propagateRemovalForward(zeroInList, removalSchedule);
	}

	public void breakCyclicDependency(
		LinkedList zeroOutList,
		LinkedList zeroInList,
		Collection removalSchedule) {
		markedForRemoval = true;
		removalSchedule.add(this);

		if (getBalance() >= 0)
			zeroOutList.addLast(this);
		else
			zeroInList.addFirst(this);

		propagateRemovalBackward(zeroOutList, removalSchedule);
		propagateRemovalForward(zeroInList, removalSchedule);
	}

	/**
	 * Generate a (hopefully) unique name for a JESS variable which will
	 * be used to store the fact-id of the JESS fact representing the
	 * supplied Protege "Instance" object. Generated names take the form
	 * of <code>?jat_ + &lt;Protege-internal instance name&gt;</code>.
	 *  
	 * @param inst		The instance for which a name needs to be generated
	 * @return			A string containing the name of the JESS variable
	 */
	private String getDecoratedJessVariableName(Instance inst) {
		return "?jat_" + inst.getName();
	}

	/**
	 * Converts a Protege slot value object into a textual representation
	 * that is fit for use in JESS code. Conversion is done with regard to
	 * the type of the value object (ie. quotes and backslashes in strings
	 * are escaped, references to instances are replaced with named
	 * variables where possible, etc.).
	 * 
	 * @param val	The value which needs to be converted 	
	 * @return		A string representing the supplied value
	 */
	private String escapeJessValue(Object val) {
		if (val instanceof Cls) {
			return ((Cls) val).getName();
		} else if (val instanceof Instance) {
			return getDecoratedJessVariableName((Instance) val);
		} else if (val instanceof Boolean) {
			return ((Boolean) val).booleanValue() ? "TRUE" : "FALSE";
		} else if (val instanceof String) {
			val = ((String) val).replaceAll("\\\\", "\\\\\\\\"); // backslashes
			val = ((String) val).replaceAll("\"", "\\\\\""); // quotes
			return "\"" + val + "\"";
		} else {
			return val.toString();
		}
	}

	/**
	 * Main method to handle the conversion of the encapsulated Protege
	 * "Instance" object (this.theInstance) to JESS code.
	 * 
	 * @param ps	An output stream object to write the generated statement to
	 * @param assertedInstances	A collection of Protege Instances that have
	 * already been converted to JESS and whose "assert" statements have
	 * been written to the output stream
	 * @param postAssertionCmds	JESS commands ("modify" statements) that
	 * need to be executed when the "assert" statements have finished, ie. all
	 * instances (=facts) have been created (=asserted). Writing this
	 * buffer to the output stream lies within the responsibilities of the
	 * calling method.
	 */
	public void toJessCode(
		PrintStream ps,
		Collection assertedInstances,
		StringBuffer postAssertionCmds) {
		Cls theCls = theInstance.getDirectType();

		tab.printStatus(
			"  Instance '"
				+ theInstance.getName()
				+ "' of type: "
				+ theCls.getName());

		if (preserveJessFactID)
			ps.print(
				"(bind " + getDecoratedJessVariableName(theInstance) + " ");

		ps.print("(assert (" + tab.convertNames(theCls.getName()));

		// for each slot:
		Collection slots = theInstance.getOwnSlots();
		for (Iterator slotIter = slots.iterator(); slotIter.hasNext();) {
			Slot slot = (Slot) slotIter.next();
			Collection valueCol = theInstance.getOwnSlotValues(slot);

			if (slot.getName().charAt(0) == ':') // skip system slots
				continue;
			if (valueCol.isEmpty()) // skip empty slots
				continue;

			// can this slot and its values be initialized in the "assert"
			// statement, or do we need to "modify" it into being?
			boolean canInline = true;
			StringBuffer temp = new StringBuffer();

			// "(slotname value1 value2...)"
			temp.append("(" + tab.convertNames(slot.getName()));
			for (Iterator valueIter = valueCol.iterator();
				valueIter.hasNext();
				) {
				Object val = valueIter.next();
				temp.append(" " + escapeJessValue(val));
				if (requiredInstances.contains(val)
					&& !assertedInstances.contains(val))
					canInline = false;
			}
			temp.append(")");

			if (canInline)
				ps.print("\n  " + temp);
			else
				postAssertionCmds.append(
					"(modify "
						+ getDecoratedJessVariableName(theInstance)
						+ " "
						+ temp
						+ ")\n");
		} // end for

		if (preserveJessFactID)
			ps.print(")");

		ps.print("))\n\n");
	}
}
