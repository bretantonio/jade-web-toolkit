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
import jade.util.Logger;
import jade.lang.acl.ACLCodec;
import cascom.fipa.acl.BitEffACLCodec;
import cascom.fipa.acl.ContentCompressor;
import java.util.Hashtable;
import jade.lang.acl.ACLMessage;
import java.io.IOException;

/**
 * ACL codec which compresses the ACL message content. The default
 * encoding is bit-efficient using cascom.fipa.acl.BitEffACLCodec. However,
 * using different constructor the user may use any ACLCodec. By default
 * the codec does not compress outgoing message content, user may turn
 * compression on using setCompressor -method.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class BitEffACLCodecCmp implements ACLCodec {
    private static Logger logger = Logger.getMyLogger("BitEffACLCodecCmp");
    private ACLCodec baseCodec = null;
    
    /**
     * ContentCompressor is not part of bit-efficient ACL standard. It is
     * optional to use compressor (e.g. gzip) to compress the message content.
     */
    protected static ContentCompressor compressor = null;
    
    /**
     * ContentCompressors available for decoder so it can decode
     * messages with differently compressed message content.
     */
    protected static Hashtable compressors = new Hashtable(3);
    
    
    /**
     * Try to load current available compression codecs if
     * those can be found. There is currently no way to
     * pass Profile-arguments to ACL codecs in JADE so this
     * is probably only (dirty) way to make codecs available.
     * Property file cannot be used because this class must be
     * MIDP compatible and Profile cannot be asked from
     * core classes (?).
     *
     * User still must set the default codec using
     * setCompressor() - method if content compression wanted to be used
     * for outgoing messages.
     */
    static {
        loadCompressor("cascom.util.SrankCompressor", false);
        loadCompressor("cascom.util.ZlibCompressor", false);
        loadCompressor("cascom.util.JazzlibCompressor", false);
    }
    
    private static void loadCompressor(String classname, boolean setDefault){
        try {
            ContentCompressor cc = (ContentCompressor) Class.forName(classname).newInstance();
            if(setDefault){
                setCompressor(cc);
                if(logger.isLoggable(Logger.INFO)){
                    logger.log(Logger.INFO,"ACL Content compression method "+cc.getName()+" set to default");
                }
                
            } else {
                addCompressor(cc);
                if(logger.isLoggable(Logger.INFO)){
                    logger.log(Logger.INFO,"ACL Content compression method "+cc.getName()+" loaded");
                }
            }
        } catch (Exception e){
            if(logger.isLoggable(Logger.WARNING)){
                logger.log(Logger.WARNING,"Error loading "+classname+":"+e.getMessage());
            }
        }
    }
    
    /**
     * Constructs new BitEffACLCodecCmp with bit-efficient
     * base codec.
     */
    public BitEffACLCodecCmp(){
        this.baseCodec = new BitEffACLCodec();
    }
    
    
    /**
     * Constucts new codec using given codec as base codec.
     */
    public BitEffACLCodecCmp(ACLCodec basecodec){
        this.baseCodec = basecodec;
    }
    
    
    /**
     * Sets optional content compressor to be available
     * for decoder so that messages with compressed content
     * can be decoded.
     */
    public static void addCompressor(ContentCompressor cmp) {
        if(cmp != null){
            compressors.put((Object)cmp.getName(),(Object)cmp);
        }
    }
    
    /**
     * Decodes the given byte sequence to ACLMessage.
     */
    public ACLMessage decode(byte[] msg, String charset) throws ACLCodec.CodecException {
        ACLMessage aclm = this.baseCodec.decode(msg,charset);
        if(aclm.getUserDefinedParameter(ContentCompressor.CMP_METHOD) != null){
            ContentCompressor cc = (ContentCompressor) this.compressors.get(aclm.getUserDefinedParameter(ContentCompressor.CMP_METHOD));
            if(cc != null){
                try {
                    if(ContentCompressor.BYTE_CONTENT.equals(aclm.getUserDefinedParameter(ContentCompressor.C_TYPE))){
                        aclm.setByteSequenceContent(cc.uncompress(aclm.getByteSequenceContent()));
                    } else {
                        aclm.setContent(new String(cc.uncompress(aclm.getByteSequenceContent())));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    logger.log(Logger.SEVERE,"Cannot uncompress msg content:"+e.getMessage());
                }
            } else {
                throw new ACLCodec.CodecException("Compression not supported:"+aclm.getUserDefinedParameter(ContentCompressor.CMP_METHOD),new Exception());
            }
        }
        return aclm;
    }
    
    
    /**
     * Encodes the given ACLMessage using the base ACLCodec and
     * compressing the content if the compression is turned on.
     */
    public byte[] encode(ACLMessage aclm, String charset)  {
        if(this.compressor != null){
            if(aclm.hasByteSequenceContent() && aclm.getByteSequenceContent().length > this.compressor.minLength()){
                try {
                    aclm.setByteSequenceContent(this.compressor.compress(aclm.getByteSequenceContent()));
                    aclm.addUserDefinedParameter(ContentCompressor.CMP_METHOD,this.compressor.getName());
                    aclm.addUserDefinedParameter(ContentCompressor.C_TYPE,ContentCompressor.BYTE_CONTENT);
                } catch (Exception e){
                    e.printStackTrace();
                    logger.log(Logger.SEVERE,"Error compressing content with "+this.compressor.getName()+":"+e.getMessage());
                }
                
            } else if (aclm.getContent().length() > this.compressor.minLength()) {
                // should be marked that content have been string
                try {
                    aclm.setByteSequenceContent(this.compressor.compress(aclm.getContent().getBytes()));
                    aclm.addUserDefinedParameter(ContentCompressor.CMP_METHOD,this.compressor.getName());
                    aclm.addUserDefinedParameter(ContentCompressor.C_TYPE,ContentCompressor.STRING_CONTENT);
                } catch (Exception e){
                    e.printStackTrace();
                    logger.log(Logger.SEVERE,"Error compressing content with "+this.compressor.getName()+":"+e.getMessage());
                }
            }
        }
        return this.baseCodec.encode(aclm, charset);
    }
    
    
    /**
     * Sets the content compressor to be used for encoded messages or
     * disables the option if the parameter is null.
     *
     * The compressor will also be added to available codecs using
     * addCompressor -method.
     *
     * @param cmp The ContentCompressor or null if compressing should be
     * disabled.
     */
    public static void setCompressor(ContentCompressor cmp){
        compressor = cmp;
        if(compressor != null){
            addCompressor(cmp);
        }
    }

    
    /**
     * Returns name of the current ACL encoding, returned 
     * by the base codec getName() -method.
     */
    public String getName(){
        return this.baseCodec.getName();
    }
}
