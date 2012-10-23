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
// IDL:FIPA/DateTime:1.0
//
final public class DateTimeHolder implements org.omg.CORBA.portable.Streamable
{
    public DateTime value;

    public
    DateTimeHolder()
    {
    }

    public
    DateTimeHolder(DateTime initial)
    {
        value = initial;
    }

    public void
    _read(org.omg.CORBA.portable.InputStream in)
    {
        value = DateTimeHelper.read(in);
    }

    public void
    _write(org.omg.CORBA.portable.OutputStream out)
    {
        DateTimeHelper.write(out, value);
    }

    public org.omg.CORBA.TypeCode
    _type()
    {
        return DateTimeHelper.type();
    }
}