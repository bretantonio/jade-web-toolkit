/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
/*
 * FilterDefinition.java
 * Created on 15 déc. 2004
 * Author : louisvi
 */
package jade.semantics.kbase.filters;

/**
 * Used to gather the filters which deal with the same domain. 
 * @author Vincent Louis - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 */
public class FilterDefinition {
	
	/**
	 * index of the filter
	 */
    private int index;
    
    /**
     * A belief base filter
     */
    private KBFilter filter;
    
    /**
     * Builds a Filter definition with the filter <i>filter</i> at the index 
     * <i>index</i>
     * @param index an index
     * @param filter a <code>KBFilter</code>
     */
    public FilterDefinition(int index, KBFilter filter) {
        super();
        this.index = index;
        this.filter = filter;
    } 

    /**
     * Returns the filter.
     * @return the filter.
     */
    public KBFilter getFilter() {
        return this.filter;
    } 

    /**
     * Returns the index.
     * @return the index.
     */
    public int getIndex() {
        return this.index;
    } 

} 
