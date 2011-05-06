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
 * Digit.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */

package jsademos.temperature;

import java.awt.Color;
import java.awt.Graphics;
/**
 * Defines a digit 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/12/17 Revision: 1.0
 */
public class Digit {
    
    /**
     * The choosen digit
     */
    private int digit;  
    
    /**
     * The color of the digit when it is light on
     */
    private Color lightColor; 
    
    /**
     * The color of the digit when it is light off
     */
    private Color darkColor;  
    
    /**
     * The tab of all known digits
     */
    private static int digitMap[][] = {
        {1,1,1,1,1,1,0,0},  //0
        {0,0,0,0,1,1,0,0},  //1
        {1,0,1,1,0,1,1,0},  //2
        {1,0,0,1,1,1,1,0},  //3
        {0,1,0,0,1,1,1,0},  //4
        {1,1,0,1,1,0,1,0},  //5
        {1,1,1,1,1,0,1,0},  //6
        {1,0,0,0,1,1,0,0},  //7
        {1,1,1,1,1,1,1,0},  //8
        {1,1,0,1,1,1,1,0},  //9
        {1,1,0,0,0,1,1,0},  // round character
        {0,0,0,0,0,0,1,0},  // "-" character
        {1,1,1,1,0,0,0,0},  // "c" character
        {0,0,0,0,0,0,0,1}   // dot character
    }; 
    
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor of a digit
     * @param digit the digit 
     * @param lightColor the color for the visible parts of the digit
     * @param darkColor the color for the not visible parts of the digit
     */
    public Digit(int digit, Color lightColor, Color darkColor) {
        this.digit = digit;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    } // End of Digit/3
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Draws a digit 
     * @param graphic graphic support
     * @param positionX the X position for the digit
     * @param positionY the Y position for the digit
     */
    public void draw(Graphics graphic, int positionX, int positionY) {
        
        // Segment No 0.
        if (digitMap[digit][0]==1) graphic.setColor(lightColor);
        else graphic.setColor(darkColor);
        graphic.fillRect(positionX, positionY, 40, 10);
        
        // Segment No 1.
        if (digitMap[digit][1]==1) graphic.setColor(lightColor);
        else graphic.setColor(darkColor);
        graphic.fillRect((positionX-10), (positionY +15), 10,50);
        
        // Segment No 2.
        if (digitMap[digit][2]==1) graphic.setColor(lightColor);
        else graphic.setColor(darkColor);
        graphic.fillRect(positionX-10, positionY + 80, 10,50);
        
        // Segment No 3.
        if (digitMap[digit][3]==1) graphic.setColor(lightColor);
        else graphic.setColor(darkColor);
        graphic.fillRect(positionX, positionY + 135, 40, 10);
        
        // Segment No 4.
        if (digitMap[digit][4]==1) graphic.setColor(lightColor);
        else graphic.setColor(darkColor);
        graphic.fillRect(positionX + 40, positionY + 80, 10,50);
        
        // Segment No 5.
        if (digitMap[digit][5]==1) graphic.setColor(lightColor);
        else graphic.setColor(darkColor);
        graphic.fillRect(positionX + 40, positionY + 15, 10,50);
        
        // Segment No 6.
        if (digitMap[digit][6]==1) graphic.setColor(lightColor);
        else graphic.setColor(darkColor);
        graphic.fillRect(positionX, positionY + 68, 40, 10);
        
        if (digitMap[digit][7]==1) graphic.setColor(lightColor);
        else graphic.setColor(Color.BLACK);
        graphic.fillRoundRect(positionX+10, positionY + 115, 20, 20, 40 , 40);
        
        
    } // End of draw/3
    
    /**
     * @return Returns the string representation of a digit
     */
    @Override
	public String toString() { 	
        return "Digit : " + digit;
    } // End of toString/0
} // End of class Digit 
