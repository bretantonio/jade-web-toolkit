package org.logica.ctis.security;


import jade.core.AID;
import jade.security.JADESecurityException;

/**
 * Implementors can for example follow a login procedure to retreive a token (sessionid,...) for the name argument. For now the name will be the
 * {@link AID#getName() local name} of an agent.
 * @see SecurityService
 * @author Eduard Drenth: Logica, 6-feb-2010
 *
 */
public interface TokenProvider {

    /**
     *
     * @param name The ({@link AID#getName() name} to provide a token for.
     * @return The token to be used for this agent name.
     * @throws JADESecurityException
     */
    public String getToken(String name) throws JADESecurityException;
}
