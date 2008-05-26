package processing.dbus;

import org.freedesktop.dbus.*;

public interface Launcher extends DBusInterface {
    public void load(String args[]);
    public void exit();
}
