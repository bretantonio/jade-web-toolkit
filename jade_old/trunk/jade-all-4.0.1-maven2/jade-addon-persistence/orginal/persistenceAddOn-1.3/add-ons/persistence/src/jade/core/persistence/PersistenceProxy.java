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

package jade.core.persistence;

//#MIDP_EXCLUDE_FILE

import jade.core.Node;
import jade.core.Service;
import jade.core.Filter;
import jade.core.GenericCommand;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.IMTPException;
import jade.core.ServiceException;
import jade.core.NotFoundException;
import jade.core.NameClashException;

import jade.mtp.MTPDescriptor;

import jade.security.JADESecurityException;

import jade.util.leap.List;

/**

   The remote proxy for the JADE kernel-level service managing
   saving and retrieving agents and containers to persistent storage.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public class PersistenceProxy extends Service.SliceProxy implements PersistenceSlice {

    public void saveAgent(AID agentID, String repository) throws IMTPException, NotFoundException {
	try {
	    GenericCommand cmd = new GenericCommand(H_SAVEAGENT, PersistenceSlice.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);

	    Node n = getNode();
	    Object result = n.accept(cmd);
	    if((result != null) && (result instanceof Throwable)) {
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		else if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}
		else {
		    throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
		}
	    }
	}
	catch(ServiceException se) {
	    throw new IMTPException("Unable to access remote node", se);
	}
    }


    public void loadAgent(AID agentID, String repository) throws IMTPException, NotFoundException, NameClashException {
	try {
	    GenericCommand cmd = new GenericCommand(H_LOADAGENT, PersistenceSlice.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);

	    Node n = getNode();
	    Object result = n.accept(cmd);
	    if((result != null) && (result instanceof Throwable)) {
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		else if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}
		else if(result instanceof NameClashException) {
		    throw (NameClashException)result;
		}
		else {
		    throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
		}
	    }
	}
	catch(ServiceException se) {
	    throw new IMTPException("Unable to access remote node", se);
	}
    }

    public void reloadAgent(AID agentID, String repository) throws IMTPException, NotFoundException {
	try {
	    GenericCommand cmd = new GenericCommand(H_RELOADAGENT, PersistenceSlice.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);

	    Node n = getNode();
	    Object result = n.accept(cmd);
	    if((result != null) && (result instanceof Throwable)) {
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		else if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}
		else {
		    throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
		}
	    }
	}
	catch(ServiceException se) {
	    throw new IMTPException("Unable to access remote node", se);
	}
    }

    public void deleteAgent(AID agentID, String repository) throws IMTPException, NotFoundException {
	try {
	    GenericCommand cmd = new GenericCommand(H_DELETEAGENT, PersistenceSlice.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);

	    Node n = getNode();
	    Object result = n.accept(cmd);
	    if((result != null) && (result instanceof Throwable)) {
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		else if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}
		else {
		    throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
		}
	    }
	}
	catch(ServiceException se) {
	    throw new IMTPException("Unable to access remote node", se);
	}
    }

    public void deleteFrozenAgent(AID agentID, String repository, Long agentFK) throws IMTPException, NotFoundException {
	try {
	    GenericCommand cmd = new GenericCommand(H_DELETEFROZENAGENT, PersistenceSlice.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);
	    cmd.addParam(agentFK);

	    Node n = getNode();
	    Object result = n.accept(cmd);
	    if((result != null) && (result instanceof Throwable)) {
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		else if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}
		else {
		    throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
		}
	    }
	}
	catch(ServiceException se) {
	    throw new IMTPException("Unable to access remote node", se);
	}
    }

    public void freezeAgent(AID agentID, String repository, ContainerID bufferContainer) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_FREEZEAGENT, PersistenceSlice.NAME, null);
	cmd.addParam(agentID);
	cmd.addParam(repository);
	cmd.addParam(bufferContainer);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}
    }

    public void thawAgent(AID agentID, String repository, ContainerID newContainer) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_THAWAGENT, PersistenceSlice.NAME, null);
	cmd.addParam(agentID);
	cmd.addParam(repository);
	cmd.addParam(newContainer);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}
    }

    public Long setupFrozenAgent(AID agentID, Long agentFK, ContainerID cid, String repository) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_SETUPFROZENAGENT, PersistenceSlice.NAME, null);
	cmd.addParam(agentID);
	cmd.addParam(agentFK);
	cmd.addParam(cid);
	cmd.addParam(repository);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}
	return (Long)result;
    }

    public void setupThawedAgent(AID agentID, Long agentFK, ContainerID cid, String repository, List bufferedMessages) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_SETUPTHAWEDAGENT, PersistenceSlice.NAME, null);
	cmd.addParam(agentID);
	cmd.addParam(agentFK);
	cmd.addParam(cid);
	cmd.addParam(repository);
	cmd.addParam(bufferedMessages);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}
    }

    public void frozenAgent(AID agentID, ContainerID home, ContainerID buffer) throws IMTPException, NotFoundException {
	try {
	    GenericCommand cmd = new GenericCommand(H_FROZENAGENT, PersistenceSlice.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(home);
	    cmd.addParam(buffer);

	    Node n = getNode();
	    Object result = n.accept(cmd);
	    if((result != null) && (result instanceof Throwable)) {
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		else if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}
		else {
		    throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
		}
	    }
	}
	catch(ServiceException se) {
	    throw new IMTPException("Unable to access remote node", se);
	}
    }

    public void thawedAgent(AID agentID, ContainerID buffer, ContainerID home) throws IMTPException, NotFoundException {
	try {
	    GenericCommand cmd = new GenericCommand(H_THAWEDAGENT, PersistenceSlice.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(buffer);
	    cmd.addParam(home);

	    Node n = getNode();
	    Object result = n.accept(cmd);
	    if((result != null) && (result instanceof Throwable)) {
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		else if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}
		else {
		    throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
		}
	    }
	}
	catch(ServiceException se) {
	    throw new IMTPException("Unable to access remote node", se);
	}
    }

    public void saveContainer(String repository) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_SAVECONTAINER, PersistenceSlice.NAME, null);
	cmd.addParam(repository);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}
    }

    public void loadContainer(String repository) throws ServiceException, IMTPException, NotFoundException, NameClashException {
	GenericCommand cmd = new GenericCommand(H_LOADCONTAINER, PersistenceSlice.NAME, null);
	cmd.addParam(repository);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else if(result instanceof NameClashException) {
		throw (NameClashException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}
    }

    public void deleteContainer(ContainerID cid, String repository) throws ServiceException, IMTPException, NotFoundException {
        GenericCommand cmd = new GenericCommand(H_DELETECONTAINER, PersistenceSlice.NAME, null);
        cmd.addParam(cid);
        cmd.addParam(repository);

        Node n = getNode();
        Object result = n.accept(cmd);
        if((result != null) && (result instanceof Throwable)) {
            if(result instanceof ServiceException) {
                throw (ServiceException)result;
            }
            else if(result instanceof IMTPException) {
                throw (IMTPException)result;
            }
            else if(result instanceof NotFoundException) {
                throw (NotFoundException)result;
            }
            else {
                throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
            }
        }
    }

    public MTPDescriptor[] getInstalledMTPs(ContainerID cid) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_GETINSTALLEDMTPS, PersistenceSlice.NAME, null);
	cmd.addParam(cid);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}

	return (MTPDescriptor[])result;
    }

    public AID[] getAgentIDs(ContainerID cid) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_GETAGENTIDS, PersistenceSlice.NAME, null);
	cmd.addParam(cid);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}

	return (AID[])result;
    }

    public String[] getRepositories() throws ServiceException, IMTPException {
	GenericCommand cmd = new GenericCommand(H_GETREPOSITORIES, PersistenceSlice.NAME, null);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}

	return (String[])result;
    }    

    public String[] getSavedAgents(String repository) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_GETSAVEDAGENTS, PersistenceSlice.NAME, null);
	cmd.addParam(repository);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}

	return (String[])result;
    }

    public String[] getFrozenAgents(String repository) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_GETFROZENAGENTS, PersistenceSlice.NAME, null);
	cmd.addParam(repository);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}

	return (String[])result;
    }    

    public String[] getSavedContainers(String repository) throws ServiceException, IMTPException, NotFoundException {
	GenericCommand cmd = new GenericCommand(H_GETSAVEDCONTAINERS, PersistenceSlice.NAME, null);
	cmd.addParam(repository);

	Node n = getNode();
	Object result = n.accept(cmd);
	if((result != null) && (result instanceof Throwable)) {
	    if(result instanceof ServiceException) {
		throw (ServiceException)result;
	    }
	    else if(result instanceof IMTPException) {
		throw (IMTPException)result;
	    }
	    else if(result instanceof NotFoundException) {
		throw (NotFoundException)result;
	    }
	    else {
		throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
	    }
	}

	return (String[])result;
    }
    
}
