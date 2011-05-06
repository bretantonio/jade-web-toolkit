package org.logica.ctis.security;

import jade.core.AID;
import jade.core.management.AgentManagementSlice;
import jade.security.JADESecurityException;

/**
 * Implementors can for example call a isValid(token) function of an external LDAP. For now the commandName will be 
 * {@link AgentManagementSlice#INFORM_CREATED}, objectName will be {@link AID#getName() }.
 * @see SecurityService
 * @author Eduard Drenth: Logica, 6-feb-2010
 *
 */
public interface TokenValidator {

    /**
     *
     * @param token The token to be validated
     * @param commandName the command to be validated
     * @param objectName the name ({@link AID#getName() }) to be validated
     * @return
     * @throws JADESecurityException
     */
    public abstract boolean isValid(String token, String commandName, String objectName) throws JADESecurityException;

}
