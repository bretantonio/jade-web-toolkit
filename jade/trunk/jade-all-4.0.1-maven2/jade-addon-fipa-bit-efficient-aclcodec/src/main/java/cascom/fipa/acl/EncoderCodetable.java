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
 * Implementation of codetable for fipa-bitefficient-std.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class EncoderCodetable {
    /** ct Hash table */
    private Entry[] ct;
    /* The beginning and the end of LRU list */
    private Entry first;
    /* The beginning and the end of LRU list */
    private Entry last;
    /** primes Prime numbers for hashtable size */
    private final int primes[]=
    {263, 523, 1049, 2087, 4157, 8291, 16481, 32801, 65677};
    /** codes Available codes for new nodes */
    private int[] codes;
    private int size;
    private int current;
    private int cleanSize;
    private int nextCode;
    private int maxCode;
    private int hashSize;
    private int sizeInBits;
    /**
     * useTableCodes Indicates whether LRU algorithm has been
     * used at least once.
     */
    private boolean useTableCodes = false;
    /**
     * Initialize the code table with given size.
     * @param sz Size of the codetable in bits. According to FIPA
     * specs, this shall be a number between 8 and 16.
     */
    public EncoderCodetable(int sz) {
                /*
                 * Okay, this is not Java. We probably should throw
                 * an exception if the user is so stupid that he cannot
                 * give proper size for the codetable.
                 */
        initialize((sz>16) ? 16 : (((sz)<8) ? 8 : sz));
    }
    /**
     * Initialize the codetable
     */
    private void initialize(int sz) {
        sizeInBits = sz;
                /*
                 * Count the actual size (i.e., how many entries we can store)
                 */
        size = (2 << (sz-1));
                /*
                 * Count the number of entries we shall remove when the
                 * table is full.
                 */
        cleanSize = (size >> 3);
        /* Update funny pointer */
        current = cleanSize;
        /* First available code */
        nextCode = 0;
        /* Max. code */
        maxCode = size;
        codes = new int[size];
        first = last = null;
        /* Calculate the size of the hash table and initialize it. */
        hashSize = primes[sz-8];
        ct = new Entry[hashSize];
        for (int i = 0; i < hashSize; ++i) {
            ct[i] = null;
        }
    }
    public int getSize() { return sizeInBits; }
    /**
     * Returns next available code.
     *
     * There are basically two cases, either we haven't use LRU ever
     * (so we return nextCode) or we have use it (and we return first
     * available code from "codes" table.
     */
    private int nextAvailableCode() {
        if (useTableCodes == false && nextCode <= maxCode)
            return (nextCode++);
        if (current == cleanSize) doLRU();
        return (codes[current++]);
    }
    /**
     * Removes (size>>3) entries from codetable. The entries are
     * removed from the end of LRU list.
     */
    private void doLRU() {
        Entry p, n;
        int i;
        n = first;
        for (i = 0; i < cleanSize; ++i) {
            codes[i] = n.code;
            n.inUse = false;
            n.str = null;
            n.bstr = null;
            p = n;
            n.prev = null;
            n = n.next;
            p.prev = p.next = null;
            first = n;
        }
        useTableCodes = true;
        current = 0;
    }
    /**
     * Moves an entry to the end of lru list
     */
    private void moveLast(Entry e) {
        if (e == last) return;
        if (e == first) first = e.next;
        else if (e.prev != null) e.prev.next = e.next;
        if (e.next != null) e.next.prev = e.prev;
        last.next = e;
        e.prev = last;
        e.next = null;
        last = e;
    }
    /**
     * Inserts a string to codetable. If the string is already in
     * code table, its code is returned, and the string is moved
     * to the end of LRU list.
     *
     * @param s String to insert
     * @return Code number for string.
     */
    public int insert(String s) {
        int chain = hash(s);
        Entry e;
        if ((e = pLookup(s, chain)) != null) {
            /* Found... */
            moveLast(e);
            return e.code;
        }
        for (e = ct[chain]; e != null; e = e.hNext) {
            if (e.inUse == false) break;
        }
        if (e == null) {
            e = new Entry(s, nextAvailableCode());
            e.hNext = ct[chain];
            ct[chain] = e;
        } else {
            e.code = nextAvailableCode();
            e.str = s;
            e.byteSeq = false;
            e.inUse = true;
            e.prev = e.next = null;
        }
        if (last == null) last = first = e;
        else moveLast(e);
        return e.code;
    }
    /**
     * Inserts a byte array to codetable. If the byte array is already in
     * code table, its code is returned, and the byte array is moved
     * to the end of LRU list.
     *
     * @param b byte array to insert
     * @return Code number for byte array.
     */
    public int insert(byte [] b) {
        int chain = hash(b);
        Entry e;
        if ((e = pLookup(b, chain)) != null) {
            /* Found... */
            moveLast(e);
            return e.code;
        }
        for (e = ct[chain]; e != null; e = e.hNext) {
            if (e.inUse == false) break;
        }
        if (e == null) {
            e = new Entry(b, nextAvailableCode());
            e.hNext = ct[chain];
            ct[chain] = e;
        } else {
            e.code = nextAvailableCode();
            e.bstr = b;
            e.byteSeq = true;
            e.inUse = true;
            e.prev = e.next = null;
        }
        if (last == null) last = first = e;
        else moveLast(e);
        return e.code;
    }
    /**
     *
     * @param s The string to lookup
     * @return The code number for the string if found, -1 otherwise
     */
    public int lookup(String s) {
        Entry e = pLookup(s, hash(s));
        if (e != null) {
            moveLast(e);
            return e.code;
        }
        return -1;
    }
    /**
     *
     * @param b The byte array to lookup
     * @return The code number for the byte array if found, -1 otherwise
     */
    public int lookup(byte [] b) {
        Entry e = pLookup(b, hash(b));
        if (e != null) {
            moveLast(e);
            return e.code;
        }
        return -1;
    }
    private Entry pLookup(String s, int h) {
        Entry e;
        for (e = ct[h]; e != null; e = e.hNext) {
            if (e.inUse == true && e.byteSeq == false && s.compareTo(e.str)==0)
                return e;
        }
        return null;
    }
    private Entry pLookup(byte [] b, int h) {
        Entry e;
        for (e = ct[h]; e != null; e = e.hNext) {
            if (e.inUse == true && e.byteSeq == true && compare(b,e.bstr)==0)
                return e;
        }
        return null;
    }
    private int compare(byte [] x, byte [] y) {
        if (x.length != y.length) return -1;
        for (int i = x.length; i-- > 0; ) {
            if (x[i] != y[i]) return -1;
        }
        return 0;
    }
    /**
     * Returns the hash value for a string
     */
    private int hash(String s) {
        return (s.hashCode() & 0x7FFFFFFF) % hashSize;
    }
    private int hash(byte [] x) {
        int h = x[0];
        for (int i = x.length; i-- > 2; ) {
            h = ((h*32)+x[i]);
        }
        return ((h & 0x7FFFFFFF)%hashSize);
    }
        /*
         * Linkage node
         */
    private class Entry {
        int code; // The code number for this entry
        boolean inUse; // true if this node is really in use
        String str; // The string in this entry
        byte [] bstr; // ByteSeq in this entry
        Entry next;
        Entry prev;
        Entry hNext;
        boolean byteSeq; // String or byte[]
        public Entry(String s, int c) {
            str = s;
            bstr = null;
            byteSeq = false;
            init(c);
        }
        public Entry(byte [] b, int c) {
            str = null;
            bstr = b;
            byteSeq = true;
            init(c);
        }
        private void init(int c) {
            code = c;
            inUse = true;
            next = prev = hNext = null;
        }
    }
}
