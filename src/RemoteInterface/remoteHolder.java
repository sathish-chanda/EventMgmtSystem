package RemoteInterface;

/**
* RemoteInterface/remoteHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from G:/ASG2/src/remote.idl
* Friday, August 2, 2019 3:19:13 PM EDT
*/

public final class remoteHolder implements org.omg.CORBA.portable.Streamable
{
  public RemoteInterface.remote value = null;

  public remoteHolder ()
  {
  }

  public remoteHolder (RemoteInterface.remote initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = RemoteInterface.remoteHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    RemoteInterface.remoteHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return RemoteInterface.remoteHelper.type ();
  }

}
