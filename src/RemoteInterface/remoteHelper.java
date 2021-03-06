package RemoteInterface;


/**
* RemoteInterface/remoteHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from G:/ASG2/src/remote.idl
* Friday, August 2, 2019 3:19:13 PM EDT
*/

abstract public class remoteHelper
{
  private static String  _id = "IDL:RemoteInterface/remote:1.0";

  public static void insert (org.omg.CORBA.Any a, RemoteInterface.remote that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static RemoteInterface.remote extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (RemoteInterface.remoteHelper.id (), "remote");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static RemoteInterface.remote read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_remoteStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, RemoteInterface.remote value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static RemoteInterface.remote narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof RemoteInterface.remote)
      return (RemoteInterface.remote)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      RemoteInterface._remoteStub stub = new RemoteInterface._remoteStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static RemoteInterface.remote unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof RemoteInterface.remote)
      return (RemoteInterface.remote)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      RemoteInterface._remoteStub stub = new RemoteInterface._remoteStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
