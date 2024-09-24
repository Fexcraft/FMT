package net.fexcraft.app.fmt.updater;

import java.awt.Button;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Updater extends Frame {

	private static final int W = 480, H = 300;
	private static String log = new String();
	private static TextArea area;
	private static Button update, reload, exit;

	public static void main(String[] args){
		new Updater();
		log("Starting updater...");
		File laufile = new File("./update.fmt");
		if(!laufile.exists()) log("No update.fmt file found... Welcome to FMT Updater!");
		JsonMap lau = JsonHandler.parse(laufile);
		boolean loaded = Catalog.load(true);
		if(loaded){
			Catalog.check(true);
		}
		reload.setEnabled(true);
		update.setEnabled(loaded);
		JsonHandler.print(laufile, lau, PrintOption.FLAT);
	}

	public Updater(){
		setTitle("FMT Updater");
		area = new TextArea();
		area.setBounds(10, 35, W - 20, H - 80);
		//area.addTextListener(lis -> area.setText(log));
		add(area);
		//
		Thread thread = new Thread(() -> {
			try{
				Files.writeString(new File("./updater.log").toPath(), log);
			}
			catch(IOException e){
				e.printStackTrace();
			}
		});
		thread.setName("LogWriter");
		Runtime.getRuntime().addShutdownHook(thread);
		//
		update = new Button("Update");
		update.setBounds(10, H - 40, 150, 30);
		update.addActionListener(event -> Catalog.update(() -> Catalog.check(true)));
		update.setEnabled(false);
		add(update);
		//
		reload = new Button("Refresh Catalog");
		reload.setBounds(165, H - 40, 150, 30);
		reload.addActionListener(event -> reload());
		reload.setEnabled(false);
		add(reload);
		//
		exit = new Button("Exit");
		exit.setBounds(320, H - 40, 150, 30);
		exit.addActionListener(event -> {
			log("Exiting the Updater...");
			System.exit(0);
		});
		add(exit);
		//
		setLocationRelativeTo(null);
		setSize(W, H);
		setLayout(null);
		setVisible(true);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
	}

	private void reload(){
		Catalog.fetch(true);
		boolean loaded = Catalog.load(true);
		boolean check = false;
		if(loaded){
			check = !Catalog.check(true);
		}
		update.setEnabled(loaded);
	}

	public static void log(Object obj){
		log += obj + "\n";
		area.setText(log);
		area.setCaretPosition(log.length() - 1);
		System.out.println(obj);
	}

}
