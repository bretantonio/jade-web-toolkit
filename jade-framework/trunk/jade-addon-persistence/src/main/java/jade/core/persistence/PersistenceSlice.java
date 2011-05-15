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


import jade.core.Service;
import jade.core.Filter;
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

   The horizontal interface for the JADE kernel-level service managing
   saving and retrieving agents and containers to persistent storage.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public interface PersistenceSlice extends Service.Slice {


    /**
       The name of this service.
    */
    public static final String NAME = "jade.core.persistence.Persistence";


    // Constants for the names of horizontal commands associated to methods
    static final String H_SAVEAGENT = "1";
    static final String H_LOADAGENT = "2";
    static final String H_RELOADAGENT = "3";
    static final String H_DELETEAGENT = "4";
    static final String H_DELETEFROZENAGENT = "5";
    static final String H_FREEZEAGENT = "6";
    static final String H_THAWAGENT = "7";
    static final String H_SETUPFROZENAGENT = "8";
    static final String H_SETUPTHAWEDAGENT = "9";
    static final String H_FROZENAGENT = "10";
    static final String H_THAWEDAGENT = "11";
    static final String H_SAVECONTAINER = "12";
    static final String H_LOADCONTAINER = "13";
    static final String H_DELETECONTAINER = "14";
    static final String H_GETINSTALLEDMTPS = "15";
    static final String H_GETAGENTIDS = "16";
    static final String H_GETREPOSITORIES = "17";
    static final String H_GETSAVEDAGENTS = "18";
    static final String H_GETFROZENAGENTS = "19";
    static final String H_GETSAVEDCONTAINERS = "20";


    void saveAgent(AID agentID, String repository) throws IMTPException, NotFoundException;
    void loadAgent(AID agentID, String repository) throws IMTPException, NotFoundException, NameClashException;
    void reloadAgent(AID agentID, String repository) throws IMTPException, NotFoundException;
    void deleteAgent(AID agentID, String repository) throws IMTPException, NotFoundException;
    void deleteFrozenAgent(AID agentID, String repository, Long agentFK) throws IMTPException, NotFoundException;
    void freezeAgent(AID agentID, String repository, ContainerID bufferContainer) throws ServiceException, IMTPException, NotFoundException;
    void thawAgent(AID agentID, String repository, ContainerID newContainer) throws ServiceException, IMTPException, NotFoundException;
    Long setupFrozenAgent(AID agentID, Long agentFK, ContainerID cid, String repository) throws ServiceException, IMTPException, NotFoundException;
    void setupThawedAgent(AID agentID, Long agentFK, ContainerID cid, String repository, List bufferedMessages) throws ServiceException, IMTPException, NotFoundException;
    void frozenAgent(AID agentID, ContainerID home, ContainerID buffer) throws ServiceException, IMTPException, NotFoundException;
    void thawedAgent(AID agentID, ContainerID buffer, ContainerID home) throws ServiceException, IMTPException, NotFoundException;
    void saveContainer(String repository) throws ServiceException, IMTPException, NotFoundException;
    void loadContainer(String repository) throws ServiceException, IMTPException, NotFoundException, NameClashException;
    void deleteContainer(ContainerID cid, String repository) throws ServiceException, IMTPException, NotFoundException;

    MTPDescriptor[] getInstalledMTPs(ContainerID cid) throws ServiceException, IMTPException, NotFoundException;
    AID[] getAgentIDs(ContainerID cid) throws ServiceException, IMTPException, NotFoundException;

    String[] getRepositories() throws ServiceException, IMTPException;
    String[] getSavedAgents(String repository) throws ServiceException, IMTPException, NotFoundException;
    String[] getFrozenAgents(String repository) throws ServiceException, IMTPException, NotFoundException;
    String[] getSavedContainers(String repository) throws ServiceException, IMTPException, NotFoundException;

}
