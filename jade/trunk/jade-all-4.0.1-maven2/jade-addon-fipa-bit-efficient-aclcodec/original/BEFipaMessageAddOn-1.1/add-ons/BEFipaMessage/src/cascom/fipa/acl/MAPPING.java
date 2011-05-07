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
import jade.lang.acl.ACLMessage;

/**
 * Mapping of constants used in bit-efficient codecs to the actual 
 * values found in jade.lang.acl.ACLMessage. 
 * 
 * @author Heikki Helin, Mikko Laukkanen
 */
public interface MAPPING {
        public static final int ACCEPTPROPOSAL = ACLMessage.ACCEPT_PROPOSAL;
        public static final int AGREE = ACLMessage.AGREE;
        public static final int CANCEL = ACLMessage.CANCEL;
        public static final int CFP = ACLMessage.CFP;
        public static final int CONFIRM = ACLMessage.CONFIRM;
        public static final int DISCONFIRM = ACLMessage.DISCONFIRM;
        public static final int FAILURE = ACLMessage.FAILURE;
        public static final int INFORM = ACLMessage.INFORM;
        public static final int INFORMIF = ACLMessage.INFORM_IF;
        public static final int INFORMREF = ACLMessage.INFORM_REF;
        public static final int NOTUNDERSTOOD = ACLMessage.NOT_UNDERSTOOD;
        public static final int PROPAGATE = ACLMessage.PROPAGATE;
        public static final int PROPOSE = ACLMessage.PROPOSE;
        public static final int PROXY = ACLMessage.PROXY;
        public static final int QUERYIF = ACLMessage.QUERY_IF;
        public static final int QUERYREF = ACLMessage.QUERY_REF;
        public static final int REFUSE = ACLMessage.REFUSE;
        public static final int REJECTPROPOSAL = ACLMessage.REJECT_PROPOSAL;
        public static final int REQUEST = ACLMessage.REQUEST;
        public static final int REQUESTWHEN = ACLMessage.REQUEST_WHEN;
        public static final int REQUESTWHENEVER = ACLMessage.REQUEST_WHENEVER;
        public static final int SUBSCRIBE = ACLMessage.SUBSCRIBE;
}
