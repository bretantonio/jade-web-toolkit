/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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
 * MsgLog.java
 * Created on 7 oct. 2005
 * Author : Vincent Pautret
 */
package jade.tools.DummyAgent;

import java.awt.Color;

/**
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class MsgLog { 
    private String message; 
    private Color color; 
    public MsgLog(String message,Color color){ 
    this.message=message; 
    this.color=color; 
    } 
    public Color getColor(){ 
    return color; 
    } 
    public String getMessage(){ 
    return message; 
    } 
    }