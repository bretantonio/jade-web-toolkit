/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.model.jibx.dependency;

import jade.tools.ascml.absmodel.dependency.IServiceDependency;
import jade.tools.ascml.absmodel.IProvider;
import jade.tools.ascml.absmodel.IServiceDescription;

/**
 * 
 */
public class ServiceDependency extends AbstractDependency implements IServiceDependency
{
	/** The name and address of the launcher, who shall launch this SocietyInstance. */
	protected IProvider provider;

	protected IServiceDescription serviceDescription;

	public ServiceDependency()
	{
		super(SERVICE_DEPENDENCY);
	}

	public ServiceDependency(IServiceDescription serviceDescription)
	{
		super(SERVICE_DEPENDENCY);
		setServiceDescription(serviceDescription);
	}

	/**
	 * Get the description of the service, on which this dependency depends.
	 * @return  The ServiceDescription on which this dependency depends.
	 */
	public IServiceDescription getServiceDescription()
	{
		return serviceDescription;
	}

	/**
	 * Set the description of the service, on which this dependency depends.
	 * @param serviceDescription  The ServiceDescription on which this dependency depends.
	 */
	public void setServiceDescription(IServiceDescription serviceDescription)
	{
		this.serviceDescription = serviceDescription;
	}

	/**
	 * Set the Provider, responsible for providing the specified service.
	 * @param provider  The Provider, responsible for providing the specified service.
	 */
	public void setProvider(IProvider provider)
	{
		this.provider = provider;
	}

    /**
	 * Get the Provider, responsible for providing the specified service.
	 * @return  The Provider, responsible for providing the specified service.
	 */
	public IProvider getProvider()
	{
		return provider;
	}

}
