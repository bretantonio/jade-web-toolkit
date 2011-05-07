/*====================================================================
 
    Open Source Copyright Notice and License
 
     1.   The  programs and other works made available to you in
          these files ("the  Programs")  are  Copyright (c) 2001
          Sonera  Corporation,  Teollisuuskatu 15, P.O. Box 970,
          FIN-00051 SONERA, Finland.  
          All rights reserved.

    2.    Your   rights  to  copy,  distribute  and  modify  the
          Programs are as set out in the Sonera Public  License,
          a  copy  of  which  can be found in file "LICENSE". By
          downloading the  files  containing  the  Programs  you
          accept the terms and conditions of the Public License.
          You do not have to accept these terms and  conditions,
          but  unless  you  do  so you have no rights to use the
          Programs.

    The  Original  Code is an implementation of the FIPA Message
    Representation in Bit-Efficient Encoding.

    The  Initial  Developer  of  the  Original  Code  is  Sonera
    Corporation. Portions created by Sonera Corporation  or  its
    subsidiaries  are  Copyright  (c)  Sonera  Corporation.  
    All Rights Reserved.

    Contributor(s):
     (add contributor names here)

====================================================================*/

package cascom.util;
import java.util.Hashtable;
import cascom.util.NoSuchElementException;

/**
 * Simple StringTokenizer for parsing strings.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class StringTokenizer {
    
    class ListItem {
        private int value;
        private ListItem next;

        protected ListItem(int value){
            this.value = value;
        }        
        protected int getValue(){
            return this.value;
        }
        protected ListItem getNext(){
            return this.next;
        }
        protected boolean hasNext(){
            return (this.next != null);
        }
        protected void setNext(ListItem next){
            this.next = next;
        }
    }
    
    private ListItem beginOfList;
    private ListItem currentItem;
    private int nextStartIndex;
    private String line;
    private String delimiters =" \t\n\r\f";
    private int size = 1;
      
    /** 
     * Creates a new instance of StringTokenizer. The delimiters are
     *   " \t\n\r\f" (first is space). Delimiters are not returned as tokens.
     * @param line The string to be tokenized.    
     */
    public StringTokenizer(String line) {
        this(line, null);
    }
    
    /** 
     * Creates a new instance of StringTokenizer with given delimiter chars
     * . Delimiters are not returned as tokens.
     * @param line The string to be tokenized.    
     * @param delimiters string containing delimiter chars
     */
    public StringTokenizer(String line, String delimiters){
        if(delimiters != null){
            this.delimiters = delimiters;
        }
        
        this.line = line;
        this.beginOfList = new ListItem(-1);
        this.currentItem = this.beginOfList;
        this.nextStartIndex = 0;
        for(int i=0; i < line.length(); i++){
            if(this.delimiters.indexOf(line.charAt(i)) > -1){
                ListItem li = new ListItem(i);
                this.currentItem.setNext(li);
                this.currentItem = li;
                this.size++;
            }            
        }
        
        ListItem li = new ListItem(line.length());
        this.currentItem.setNext(li);

        this.currentItem = this.beginOfList;
    }
    
    
    
    /**
     *  Gets the next token.
     *  @return String the next token.
     *  @throws NoSuchElementException if there are not any tokens left.
     *
     */
    public String nextToken() throws NoSuchElementException {
        if(!this.currentItem.hasNext()){
            throw new NoSuchElementException();
        }
        this.currentItem = this.currentItem.getNext();
        
        String ret = line.substring(this.nextStartIndex, this.currentItem.getValue());
        this.nextStartIndex = this.currentItem.getValue()+1;
        this.size--;        
        return ret;
    }
    
    /**
     * Return information on whether there are tokens left.
     * @return True if there are tokens left.
     */
    public boolean hasMoreTokens(){
        return this.currentItem.hasNext();
    }
    
    /**
     * Returns the number of tokens left in this tokenizer.
     * @return Number of tokens left. 
     */
    public int numberOfTokensLeft(){
        return this.size;
    }
    
    /*
    public static void main(String[] args) throws Exception {
        System.out.println();
        for(int i=0; i < args.length; i++){
            System.out.print("ARG "+i+": ");
            StringTokenizer st = new StringTokenizer(args[i]);
            int count = 0;
            while(st.hasMoreTokens()){
                System.out.print(st.nextToken());
                count++;
            }
            System.out.println("  Tokens:"+count);            
        }        
    }
     */
}