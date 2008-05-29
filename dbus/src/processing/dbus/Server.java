package processing.dbus;

import processing.core.DynLoader;
import org.freedesktop.dbus.*;
import org.freedesktop.dbus.exceptions.*;
import java.io.*;

/**
   Send the specified jar file (processing app) to dynamic loader.
 */
class Server implements Launcher {

    private boolean run = true;

    public void exit()
    {
	run = false;
	synchronized (this) {
	    notifyAll();
	}
    }

    public void load(String args[]) {
	if (args.length == 0) {
	    throw new DBusExecutionException("must specify arguments: <jarfilename> <arguments to PApplet...>");
	}
	File jarfile = new File(args[0]);
	try {
	    DynLoader.load(jarfile, args);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new DBusExecutionException(e.getMessage());
	}
    }

    public boolean isRemote() {
	return false;
    }

    public static void main(String args[]) {
	DBusConnection c = null;
	Server srv = new Server();
	try {
	    c = DBusConnection.getConnection(DBusConnection.SESSION);
	    c.requestBusName("processing.dbus.Launcher");
	    c.exportObject("/processing/dbus/Launcher", srv);
	    synchronized (srv) {
		while (srv.run) {
		    try {
			System.out.println("Server ready.");
			srv.wait();
		    } catch (InterruptedException e) {}
		}
		c.disconnect();
		System.exit(0);
	    }
	} catch (DBusException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
    }
}
