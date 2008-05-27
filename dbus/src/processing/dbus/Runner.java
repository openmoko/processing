package processing.dbus;

import org.freedesktop.dbus.*;
import org.freedesktop.dbus.exceptions.*;
import java.io.*;

public class Runner {
    public static void main(String args[]) {
	DBusConnection c = null;
	try {
	    c = DBusConnection.getConnection(DBusConnection.SESSION);
	    File f = new File(args[0]);
	    if (!f.exists()) {
		System.exit(-1);
	    }
	    args[0] = f.getAbsolutePath();
	    Launcher l = (Launcher) c.getRemoteObject("processing.dbus.Launcher", "/processing/dbus/Launcher");
	    l.load(args);
	    c.disconnect();
	    System.exit(0);
	} catch (DBusException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
    }
}
