// **********************************************************************
//
// Generated by the ORBacus IDL to Java Translator
//
// Copyright (c) 2000
// Object Oriented Concepts, Inc.
// Billerica, MA, USA
//
// All Rights Reserved
//
// **********************************************************************

// Version: 4.0.5

package FIPA;

//
// IDL:FIPA/TransportBehaviourType:1.0
//
final public class TransportBehaviourTypeHolder implements org.omg.CORBA.portable.Streamable
{
    public Property[] value;

    public
    TransportBehaviourTypeHolder()
    {
    }

    public
    TransportBehaviourTypeHolder(Property[] initial)
    {
        value = initial;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        value = TransportBehaviourTypeHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        TransportBehaviourTypeHelper.write(out, value);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return TransportBehaviourTypeHelper.type();
    }
}
