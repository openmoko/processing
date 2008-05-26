package processing.core;

import processing.core.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

import javax.imageio.ImageIO;

public class DynLoader extends PApplet {

    static public void load(File jarfile, String args[]) throws Exception {
	boolean external = false;
	int location[] = null;
	int editorLocation[] = null;

	String name = null;
	boolean present = false;
	Color backgroundColor = Color.black; //BLACK;
	Color stopColor = Color.gray; //GRAY;
	GraphicsDevice displayDevice = null;
	boolean hideStop = false;

	String param = null, value = null;

	// try to get the user folder. if running under java web start,
	// this may cause a security exception if the code is not signed.
	// http://processing.org/discourse/yabb_beta/YaBB.cgi?board=Integrate;action=display;num=1159386274
	String folder = null;
	try {
	    folder = System.getProperty("user.dir");
	} catch (Exception e) { }

	int argIndex = 0;
	while (argIndex < args.length) {
	    int equals = args[argIndex].indexOf('=');
	    if (equals != -1) {
		param = args[argIndex].substring(0, equals);
		value = args[argIndex].substring(equals + 1);

		if (param.equals(ARGS_EDITOR_LOCATION)) {
		    external = true;
		    editorLocation = parseInt(split(value, ','));

		} else if (param.equals(ARGS_DISPLAY)) {
		    int deviceIndex = Integer.parseInt(value) - 1;

		    //DisplayMode dm = device.getDisplayMode();
		    //if ((dm.getWidth() == 1024) && (dm.getHeight() == 768)) {

		    GraphicsEnvironment environment =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		    GraphicsDevice devices[] = environment.getScreenDevices();
		    if ((deviceIndex >= 0) && (deviceIndex < devices.length)) {
			displayDevice = devices[deviceIndex];
		    } else {
			System.err.println("Display " + value + " does not exist, " +
					   "using the default display instead.");
		    }

		} else if (param.equals(ARGS_BGCOLOR)) {
		    if (value.charAt(0) == '#') value = value.substring(1);
		    backgroundColor = new Color(Integer.parseInt(value, 16));

		} else if (param.equals(ARGS_STOP_COLOR)) {
		    if (value.charAt(0) == '#') value = value.substring(1);
		    stopColor = new Color(Integer.parseInt(value, 16));

		} else if (param.equals(ARGS_SKETCH_FOLDER)) {
		    folder = value;

		} else if (param.equals(ARGS_LOCATION)) {
		    location = parseInt(split(value, ','));
		}

	    } else {
		if (args[argIndex].equals(ARGS_PRESENT)) {
		    present = true;

		} else if (args[argIndex].equals(ARGS_HIDE_STOP)) {
		    hideStop = true;

		} else if (args[argIndex].equals(ARGS_EXTERNAL)) {
		    external = true;

		} else {
		    name = args[argIndex];
		    break;
		}
	    }
	    argIndex++;
	}

	if (displayDevice == null) {
	    GraphicsEnvironment environment =
		GraphicsEnvironment.getLocalGraphicsEnvironment();
	    displayDevice = environment.getDefaultScreenDevice();
	}

	final Frame frame = new Frame(displayDevice.getDefaultConfiguration());
	/*
	  Frame frame = null;
	  if (displayDevice != null) {
	  frame = new Frame(displayDevice.getDefaultConfiguration());
	  } else {
	  frame = new Frame();
	  }
	*/
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

	// remove the grow box by default
	// users who want it back can call frame.setResizable(true)
	frame.setResizable(false);

	if (! jarfile.exists()) {
	    throw new RuntimeException("jar file doesn't exist");
	}
	name = jarfile.getName();
	name = name.substring(0, name.toLowerCase().lastIndexOf(".jar"));
	URL jarurl = new URL("file:" + jarfile.getAbsolutePath());
	Class c = new URLClassLoader(new URL[] { jarurl }).loadClass(name);

	//Class c = Class.forName(name);
	final PApplet applet = (PApplet) c.newInstance();

	// these are needed before init/start
	applet.frame = frame;
	applet.sketchPath = folder;
	applet.args = PApplet.subset(args, 1);
	applet.external = external;

	applet.init();

	// wait until the applet has figured out its width
	// hoping that this won't hang if the applet has an exception
	while (applet.defaultSize && !applet.finished) {
	    //System.out.println("default size");
	    try {
		Thread.sleep(5);

	    } catch (InterruptedException e) {
		//System.out.println("interrupt");
	    }
	}
	//println("not default size " + applet.width + " " + applet.height);
	//println("  (g width/height is " + applet.g.width + "x" + applet.g.height + ")");

	if (present) {
	    frame.setUndecorated(true);
	    frame.setBackground(backgroundColor);
	    displayDevice.setFullScreenWindow(frame);

	    frame.add(applet);
	    Dimension fullscreen = frame.getSize();
	    applet.setBounds((fullscreen.width - applet.width) / 2,
			     (fullscreen.height - applet.height) / 2,
			     applet.width, applet.height);

	    if (!hideStop) {
		Label label = new Label("stop");
		label.setForeground(stopColor);
		label.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
			    System.exit(0);
			}
		    });
		frame.add(label);

		Dimension labelSize = label.getPreferredSize();
		// sometimes shows up truncated on mac
		//System.out.println("label width is " + labelSize.width);
		labelSize = new Dimension(100, labelSize.height);
		label.setSize(labelSize);
		label.setLocation(20, fullscreen.height - labelSize.height - 20);
	    }

	    // not always running externally when in present mode
	    if (external) {
		applet.setupExternalMessages();
	    }

	} else {  // if not presenting
	    // can't do pack earlier cuz present mode don't like it
	    // (can't go full screen with a frame after calling pack)
	    frame.pack();  // get insets. get more.
	    Insets insets = frame.getInsets();

	    int windowW = Math.max(applet.width, MIN_WINDOW_WIDTH) +
		insets.left + insets.right;
	    int windowH = Math.max(applet.height, MIN_WINDOW_HEIGHT) +
		insets.top + insets.bottom;

	    frame.setSize(windowW, windowH);

	    if (location != null) {
		// a specific location was received from PdeRuntime
		// (applet has been run more than once, user placed window)
		frame.setLocation(location[0], location[1]);

	    } else if (external) {
		int locationX = editorLocation[0] - 20;
		int locationY = editorLocation[1];

		if (locationX - windowW > 10) {
		    // if it fits to the left of the window
		    frame.setLocation(locationX - windowW, locationY);

		} else {  // doesn't fit
		    // if it fits inside the editor window,
		    // offset slightly from upper lefthand corner
		    // so that it's plunked inside the text area
		    locationX = editorLocation[0] + 66;
		    locationY = editorLocation[1] + 66;

		    if ((locationX + windowW > screen.width - 33) ||
			(locationY + windowH > screen.height - 33)) {
			// otherwise center on screen
			locationX = (screen.width - windowW) / 2;
			locationY = (screen.height - windowH) / 2;
		    }
		    frame.setLocation(locationX, locationY);
		}
	    } else {  // just center on screen
		frame.setLocation((screen.width - applet.width) / 2,
				  (screen.height - applet.height) / 2);
	    }

	    frame.setLayout(null);
	    frame.add(applet);

	    if (backgroundColor == Color.black) {  //BLACK) {
		// this means no bg color unless specified
		backgroundColor = SystemColor.control;
	    }
	    frame.setBackground(backgroundColor);

	    int usableWindowH = windowH - insets.top - insets.bottom;
	    applet.setBounds((windowW - applet.width)/2,
			     insets.top + (usableWindowH - applet.height)/2,
			     applet.width, applet.height);

	    if (external) {
		applet.setupExternalMessages();

	    } else {  // !external
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			    frame.setVisible(false);
			    applet.stop();
			    applet.destroy();
			    frame.dispose();
			    System.gc();
			}
		    });
	    }

	    // handle frame resizing events
	    applet.setupFrameResizeListener();

	    // all set for rockin
	    if (applet.displayable()) {
		frame.setVisible(true);
	    }
	}

	//System.out.println("showing frame");
	//System.out.println("applet requesting focus");
	applet.requestFocus(); // ask for keydowns
	//System.out.println("exiting main()");
    }
}
