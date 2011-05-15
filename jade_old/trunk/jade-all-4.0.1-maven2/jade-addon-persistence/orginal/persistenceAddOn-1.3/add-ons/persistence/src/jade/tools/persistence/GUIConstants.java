/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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



package jade.tools.persistence;

/**
 *
 * @author  Giovanni Rimassa - FRAMeTech s.r.l.
 */
interface GUIConstants {

    static final String ACTION_ADDREPOSITORY = "add-repository";
    static final String ACTION_REMOVEREPOSITORY = "remove-repository";
    static final String ACTION_ADDMAPPING = "add-mapping";
    static final String ACTION_REMOVEMAPPING = "remove-mapping";
    static final String ACTION_ADDPROPERTY = "add-property";
    static final String ACTION_REMOVEPROPERTY = "remove-property";
    static final String ACTION_RELOADAGENT = "reload-agent";
    static final String ACTION_SAVEAGENT = "save-agent";
    static final String ACTION_SAVECONTAINER = "save-container";
    static final String ACTION_LOADAGENT = "load-agent";
    static final String ACTION_LOADCONTAINER = "load-container";
    static final String ACTION_DELETEAGENT = "delete-agent";
    static final String ACTION_DELETECONTAINER = "delete-container";
    static final String ACTION_FREEZEAGENT = "freeze-agent";
    static final String ACTION_THAWAGENT = "thaw-agent";
    static final String ACTION_EXIT = "exit";

}
