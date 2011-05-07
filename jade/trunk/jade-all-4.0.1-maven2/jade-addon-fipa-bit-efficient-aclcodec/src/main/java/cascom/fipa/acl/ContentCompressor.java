/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package cascom.fipa.acl;

/**
 * This interface makes possible to use compression (e.g gzip) with
 * ACL message content. All ContentCompressors MUST BE thread safe.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public interface ContentCompressor {
    /**
     * The identifier which starts the content field in ACL message if
     * the content is compressed.
     */
    //public static final String contentID = "FF0";

    /**
     * The user defined ACL header indicating the compression method
     */
    public static final String CMP_METHOD = "x-ccmp";
       
    /**
     * The delimiter ending the name of the compressor
     */
    //public static final byte nameDelimiter= (byte)'|';
    
    
    /**
     * The user defined ACL header indicating whether the content 
     * should be bytesequence or string. This is needed when 
     * compression is used for message content so that the decoder knows
     * whether the decoded content should be put to bytesequence or string 
     * content to ACL message.
     */
    public static final String C_TYPE = "x-ctp";
    
    /**
     * Identifier proceeding contentID which tells that the content 
     * has originally been byte sequence.
     */
    public static final String BYTE_CONTENT = "b";
 
    /**
     * Identifier proceeding contentID which tells that the content 
     * has originally been String content.
     */
    public static final String STRING_CONTENT = "s";

    
    
    /**
     * Compresses the given String
     */
    public byte[] compress(byte[] bytes) throws Exception;    

    /**
     * Uncompresses the given String
     */
    public byte[] uncompress(byte[] bytes) throws Exception;    
    
    
    /**
     * Returns the name of the compression method.
     */
    public String getName();
        
    /**
     * Gets the minumum length for compressable data. If the length of 
     * data is less, it is not recommendable to compress it with
     * this compression method (some another method should be used or the data
     * should be left uncompressed).
     */            
    public int minLength();
}