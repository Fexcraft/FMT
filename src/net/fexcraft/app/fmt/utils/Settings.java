package net.fexcraft.app.fmt.utils;

import java.awt.EventQueue;

import javax.swing.JOptionPane;

public class Settings {
	
	private static boolean fullscreen, floor = true, demo, lines = true, cube = true, polygon_marker = true;
	
	public static boolean fullscreen(){ return fullscreen; }

	public static boolean floor(){ return floor; }

	public static boolean lines(){ return lines; }

	public static boolean demo(){ return demo; }

	public static boolean cube(){ return cube; }
	
	public static boolean polygonMarker(){ return polygon_marker; }
	
	//
	
	public static boolean setFullScreen(boolean full){
		return fullscreen = full;
	}

	public static boolean toogleFullscreen(){
		return fullscreen = !fullscreen;
	}

	public static boolean toggleFloor(){
		return floor = !floor;
	}

	public static boolean toggleLines(){
		return lines = !lines;
	}

	public static boolean toggleCube(){
		return cube = !cube;
	}

	public static boolean togglePolygoMarker(){
		return polygon_marker = !polygon_marker;
	}
	
	public static boolean toggleDemo(){
		return demo = !demo;
	}
	
	public static final void showDialog(Object message, String title, int type){
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				JOptionPane.showMessageDialog(null, message, title, type);
			}
		});
	}

}
