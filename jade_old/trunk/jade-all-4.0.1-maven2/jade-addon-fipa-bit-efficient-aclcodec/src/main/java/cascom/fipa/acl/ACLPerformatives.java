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
 * Mapping between JADE performative codes and bitefficient ones.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class ACLPerformatives implements ACLConstants {
    private static class pair {
        public byte be;
        public int orig;
        public String name;
        public pair(byte a, int b, String n) { be = a; orig = b; name = n;}
    }
    private static final pair x[] = {
        new pair((byte)0,-1, ""),
                new pair(ACL_ACCEPTPROPOSAL, MAPPING.ACCEPTPROPOSAL, "accept-proposal"),
                new pair(ACL_AGREE, MAPPING.AGREE, "agree"),
                new pair(ACL_CANCEL, MAPPING.CANCEL, "cancel"),
                new pair(ACL_CFP, MAPPING.CFP, "cfp"),
                new pair(ACL_CONFIRM, MAPPING.CONFIRM, "confirm"),
                new pair(ACL_DISCONFIRM, MAPPING.DISCONFIRM, "disconfirm"),
                new pair(ACL_FAILURE, MAPPING.FAILURE, "failure"),
                new pair(ACL_INFORM, MAPPING.INFORM, "inform"),
                new pair(ACL_INFORMIF, MAPPING.INFORMIF, "inform-if"),
                new pair(ACL_INFORMREF, MAPPING.INFORMREF, "inform-ref"),
                new pair(ACL_NOTUNDERSTOOD, MAPPING.NOTUNDERSTOOD, "not-understood"),
                new pair(ACL_PROPAGATE, MAPPING.PROPAGATE, "propagate"),
                new pair(ACL_PROPOSE, MAPPING.PROPOSE, "propose"),
                new pair(ACL_PROXY, MAPPING.PROXY, "proxy"),
                new pair(ACL_QUERYIF, MAPPING.QUERYIF, "query-if"),
                new pair(ACL_QUERYREF, MAPPING.QUERYREF, "query-ref"),
                new pair(ACL_REFUSE, MAPPING.REFUSE, "refuse"),
                new pair(ACL_REJECTPROPOSAL, MAPPING.REJECTPROPOSAL, "reject-proposal"),
                new pair(ACL_REQUEST, MAPPING.REQUEST, "request"),
                new pair(ACL_REQUESTWHEN, MAPPING.REQUESTWHEN, "request-when"),
                new pair(ACL_REQUESTWHENEVER,MAPPING.REQUESTWHENEVER, "request-whenever"),
                new pair(ACL_SUBSCRIBE, MAPPING.SUBSCRIBE, "subscribe"),
    };
    /**
     * Returns FIPAOS code for performative
     */
    public int getCA(byte b) {
        return (b < ACL_ACCEPTPROPOSAL || b > ACL_SUBSCRIBE)
        ? -1 : (x[b].orig);
    }
    public int getCA(String b) {
        for (int i = 1; i <= ACL_SUBSCRIBE; ++i) {
            if (x[i].name.equals(b)) {
                return x[i].orig;
            }
        }
        return -1;
    }
    /**
     * Returns performative string
     */
    public String getCAString(byte b) {
        return (b < ACL_ACCEPTPROPOSAL || b > ACL_SUBSCRIBE)
        ? "" : (x[b].name);
    }
    /**
     * Returns bit-efficient code for performative
     * @param b JADE code for performative
     */
    public byte getCACode(int b) {
        for (int i = 1; i <= ACL_SUBSCRIBE; ++i) {
            if (x[i].orig == b) return x[i].be;
        }
        return (byte)-1;
    }
    public byte getCACode(String b) {
        for (int i = 1; i <= ACL_SUBSCRIBE; ++i) {
            if (x[i].name.equals(b)) {
                return x[i].be;
            }
        }
        return (byte) -1;
    }
}
