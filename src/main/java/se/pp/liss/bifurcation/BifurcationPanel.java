package se.pp.liss.bifurcation;
/*
 *   May's Bifurcation Graph
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version
 *   2 of the License, or (at your option) any later version.
 *
 *   Hans Liss <Hans@Liss.pp.se>
 *   http://hans.liss.pp.se
 *
 *   The file LICENSE must accompany this package when redistributed.
 *   Please refer to it for specific acknowledgements.
 *
 */

import java.awt.*;
import java.awt.event.*;

public class BifurcationPanel extends Panel implements Runnable,MouseListener,MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tmpBuffer[];
	private Image ofsImage;
	private Graphics ofsG;
	private volatile Thread paintThread = null;

	private int width;
	private int height;
	private int wh;

	private int MAXITER=500, MINITER=50;

	private double x0;
	private double x1;
	private double y0;
	private double y1;

	private int select_x0;
	private int select_y0;
	private int select_x1;
	private int select_y1;

	private boolean poisoned=false;
	
	public BifurcationPanel(int w, int h)
	{
		x0=2.5;
		x1=4;
		y0=0;
		y1=1;

		setNewSize(w, h);
		
		addMouseMotionListener( this );
		addMouseListener( this );
	}
	
	public void setNewSize(int w, int h) {
		width=w;
		height=h;
		setSize(width, height);
	}

	public void start() {
		select_x0=0; select_y0=0; select_x1=0; select_y1=0;

		stop();
		
		poisoned = false;
		
		wh = width * height;

		tmpBuffer = new int[wh];
		ofsImage = createImage (width, height);
		ofsG = ofsImage.getGraphics();

		for (int y = 0 ; y < height; y++) {
			for (int x = 0 ; x < width; x++) {
				int r = (int)(Math.random() * 0);
				int g = (int)(Math.random() * 128);
				int b = (int)(Math.random() * 256);

				ofsG.setColor(new Color(r, g, b));
				ofsG.drawLine(x, y, x, y);
				tmpBuffer[x + y*width] = 0;
			}
		}

		paintThread = new Thread(this, "Paint");
		paintThread.start();
	}


	public void poison() {
		poisoned=true;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		int x=e.getX();
		int y=e.getY();
		select_x0=x;
		select_y0=y;
		select_x1=x;
		select_y1=y;
		e.consume();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		int x=e.getX();
		int y=e.getY();
		int x0, y0, dx, dy;
		Graphics g = getGraphics();
		g.setXORMode(getBackground());
		if ((x != select_x1) || (y != select_y1)) {
			x0 = Math.min(select_x0, select_x1);
			y0 = Math.min(select_y0, select_y1);
			dx=Math.abs(select_x1 - select_x0);
			dy=Math.abs(select_y1 - select_y0);
			if (dx>0 || dy>0) g.drawRect(x0, y0, dx, dy);
			select_x1=x;
			select_y1=y;
			x0 = Math.min(select_x0, select_x1);
			y0 = Math.min(select_y0, select_y1);
			dx=Math.abs(select_x1 - select_x0);
			dy=Math.abs(select_y1 - select_y0);
			if (dx>0 || dy>0) g.drawRect(x0, y0, dx, dy);
		}
		e.consume();
	}

	public void mouseReleased(MouseEvent e)
	{
		int x=e.getX();
		int y=e.getY();
		select_x1=x;
		select_y1=y;
		if (select_x0 == select_x1 || select_y0 == select_y1) {
			x0=2.5;
			x1=4;
			y0=0;
			y1=1;
		} else {
			double dx=x1-x0;
			double dy=y1-y0;
			int nx0 = Math.min(select_x0, select_x1);
			int ny0 = Math.min(select_y0, select_y1);
			int nx1 = Math.max(select_x0, select_x1);
			int ny1 = Math.max(select_y0, select_y1);
			x0 += dx * ((double)(nx0) / (double)width);
			y0 += dy * ((double)(height - ny1 - 1) / (double)height);
			x1 -= dx * ((double)(width - nx1 - 1) / (double)width);
			y1 -= dy * ((double)(ny0) / (double)height);
		}
		for (int i = 0 ; i < wh; i++) {
			tmpBuffer[i] = 0;
		}
		select_x0=0; select_y0=0; select_x1=0; select_y1=0;
		paintThread = new Thread(this, "Paint");
		paintThread.start();
		e.consume();
	}

	public void paint(Graphics g)
	{
		int i;
		if (ofsG == null) return;
		Color bgcolour=new Color(0xdc, 0xdc, 0xff);
		if (paintThread != null && paintThread.isAlive()) bgcolour=new Color(0xff, 0xdc, 0xdc);
		for (i = 0; i < wh; i++) {
			if (tmpBuffer[i] == 0) {
				ofsG.setColor(bgcolour);
				ofsG.drawLine(i % width, (int)(i / width), i % width, (int)(i / width));
			}
			else {
				int v=120-(((120*tmpBuffer[i])/(MAXITER-MINITER)));
				ofsG.setColor(new Color(v, v, v));
				ofsG.drawLine(i % width, (int)(i / width), i % width, (int)(i / width));
			}
		}
		if (select_x1 != select_x0 && select_y1 != select_y0) {
			int x0 = Math.min(select_x0, select_x1);
			int x1 = Math.max(select_x0, select_x1);
			int y0 = Math.min(select_y0, select_y1);
			int y1 = Math.max(select_y0, select_y1);
			int dx=x1-x0;
			int dy=y1-y0;
			ofsG.setColor(Color.black);
			ofsG.drawRect(x0, y0, dx, dy);
		}
		g.drawImage( ofsImage, 0, 0, this);
	}

	public void run() {
		Thread myThread = Thread.currentThread();
		for (int x=0; !poisoned && (x < width); x++) {
			if (paintThread != myThread) return;
			double xv = x0 + (x1 - x0) * (double) x / (double) width;
			double yv = 0.5;
			for (int i=0; i<MAXITER; i++) {
				yv = xv * yv * (1 - yv);
				if (i >= MINITER) {
					int y = height - ((int)((yv - y0) * (double)height / (y1 - y0)));
					if (x >=0 && x < width && y >= 0 && y < height) {
						tmpBuffer[y * width + x]++;
						repaint();
					}
				}
			}
		}
		poisoned=false;
	}

	public void stop() {
		if (paintThread != null && paintThread.isAlive()) {
			poison();
			try {
				paintThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		paintThread = null;
	}

	public void update ( Graphics g ){
		paint(g);
	}
}

