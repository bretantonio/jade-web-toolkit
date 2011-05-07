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
// IDL:FIPA/Property:1.0
//
final public class PropertyHelper
{
    public static void
    insert(org.omg.CORBA.Any any, Property val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static Property
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return read(any.create_input_stream());
        else
            throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[2];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "keyword";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "value";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_any);

            typeCode_ = orb.create_struct_tc(id(), "Property", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:FIPA/Property:1.0";
    }

    public static Property
    read(org.omg.CORBA.portable.InputStream in)
    {
        Property _ob_v = new Property();
        _ob_v.keyword = in.read_string();
        _ob_v.value = in.read_any();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, Property val)
    {
        out.write_string(val.keyword);
        out.write_any(val.value);
    }
}
