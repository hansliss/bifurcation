package se.pp.liss.bifurcation;

import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import se.pp.liss.bifurcation.BifurcationPanel;

import gnu.getopt.Getopt;

public class BifurcationApp extends Frame  implements WindowListener, ComponentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8859307019836107168L;
	private String conffilename;
	private int width;
	private int height;
	
	private BifurcationPanel _ui;
	
	void run() {
		_ui = new BifurcationPanel(width, height);
		addWindowListener(this);
		addComponentListener(this); 
		add ("Center", _ui);
		setSize(_ui.getWidth(), _ui.getHeight());
		setVisible(true);   
		setLocationRelativeTo(null);
		_ui.start();

	}

	public void usage(String name) {
		System.err.println("Usage: " + name + " [-c <configuration file>] [-w <width> -h <height>]");
	}

	public BifurcationApp(String[] args) {
		int c;
		width = 640;
		height = 640;
		Getopt go = new Getopt("bifurcation", args, "c:");
		while ((c = go.getopt()) != -1) {
			switch (c) {
			case 'c':
				conffilename = go.getOptarg();
				if (!readconf(conffilename)) {
					System.exit(-1);
				}
				break;
			case 'w':
				width = Integer.parseInt(go.getOptarg());
				break;
			case 'h':
				height = Integer.parseInt(go.getOptarg());
				break;
			default:
				usage("bifurcation");
				System.exit(-1);
				break;
			}
		}
	}

	private static List<String> parseList(String ls) {
		List<String> r = new ArrayList<String>();
		for (String s : ls.split(",")) {
			if (s.contains("-")) {
				String[] ivl = s.split("-", 2);
				int start = Integer.parseInt(ivl[0]);
				int end = Integer.parseInt(ivl[1]);
				if (start > end) {
					int t = start;
					start = end;
					end = t;
				}
				for (Integer i = start; i <= end; i++) {
					r.add(i.toString());
				}
			} else {
				r.add(((Integer) Integer.parseInt(s)).toString());
			}
		}
		return r;
	}

	private boolean readconf(String conffilename) {
		Properties config = new Properties();
		InputStream infile;
		try {
			infile = new FileInputStream(conffilename);
			config.load(infile);

			String tmp;
			if ((tmp=config.getProperty("se.pp.liss.bifurcation.app.width")) != null) {
				width = Integer.parseInt(tmp);
			}
			if ((tmp=config.getProperty("se.pp.liss.bifurcation.app.height")) != null) {
				height = Integer.parseInt(tmp);
			}
		} catch (FileNotFoundException e) {
			System.err.println("File " + conffilename + " doesn't exist.");
			return false;
		} catch (IOException e) {
			System.err.println("Error reading configuration from File " + conffilename + ".");
			return false;
		}
		return true;		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		BifurcationApp cmd = new BifurcationApp(args);
		cmd.run();
//		while (cmd.isVisible()) {
//			cmd.repaint();
//			try {
//				Thread.sleep(1000);
//			} catch (Exception e) {}
//		}

	}
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		dispose();
		_ui.poison();
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentResized(ComponentEvent arg0) {
		width=getWidth();
		height=getHeight();
		_ui.setNewSize(width, height);
		_ui.start();
		repaint();
	}

	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
