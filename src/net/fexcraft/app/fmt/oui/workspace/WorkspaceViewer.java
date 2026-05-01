package net.fexcraft.app.fmt.oui.workspace;

import com.spinyowl.legui.component.*;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Logging;
import org.joml.Vector2f;

import java.io.*;
import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class WorkspaceViewer extends Widget {

	public static WorkspaceViewer viewer;
	public static final int ROWHEIGHT = 30;
	public ArrayList<FvtmPack> rootfolders = new ArrayList<FvtmPack>();
	public ScrollablePanel packspanel;
	//public RunButton refresh;
	public File folder;
	public int scrollableheight;
	private int wv_height = 500;
	private int wv_width = 320;

	public WorkspaceViewer(){
		super(Settings.WORKSPACE_NAME.value);
		setSize(wv_width, wv_height);
		getContainer().setSize(getSize());
		setResizable(true);
		setPosition(20, 20);
		packspanel = new ScrollablePanel();
		packspanel.setPosition(0, 30);
		packspanel.setSize(wv_width, getSize().y - 30);
		getListenerMap().addListener(ChangeSizeEvent.class, event -> {
			Vector2f vec = new Vector2f();
			event.getNewSize().get(vec);
			if(vec.x < wv_width){
				setSize(wv_width, vec.y);
				return;
			}
			if(vec.y < wv_height){
				setSize(vec.x, wv_height);
				return;
			}
			getContainer().setSize(vec);
			packspanel.setSize(vec.x, getSize().y - 30);
			//refresh.setPosition(vec.x - 110, 5);
			//resize();
		});
		//Settings.applyBorderless(packspanel);
		getContainer().add(packspanel);
		//getContainer().add(refresh = new RunButton("Refresh", getSize().x - 110, 5, 100, 20, () -> genView(false)));
		//
		folder = new File(Settings.WORKSPACE_ROOT.value);
		if(!folder.exists()) folder.mkdirs();
		//genView(true);
	}

	public static void run(boolean bool){
		String cmd;
		if(bool){
			cmd = Settings.M12RCMD.value.replace("{JAVA}", Settings.JAVA8_PATH.value + "/bin/java");
		}
		else{
			cmd = Settings.M20RCMD.value.replace("{JAVA}", Settings.JAVA17_PATH.value + "/bin/java");
		}
		try{
			Process pr = Runtime.getRuntime().exec(cmd.split(" "), null, new File(bool ? Settings.M12PATH.value : Settings.M20PATH.value));
			new Thread(() -> {
				Logging.log("=================");
				Logging.log("RUN CMD LOG START");
				BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line = null;
				try{
					while((line = input.readLine()) != null) Logging.log(line);
				}
				catch(IOException e){
					Logging.log(e);
				}
				Logging.log("RUN CMD LOG END");
				Logging.log("=================");
			}).start();
		}
		catch(IOException e){
			Logging.log(e);
			Logging.log("RUN FAILED");
			Logging.log("=================");
		}
	}

	public static void open(File file){
		String cmd = Settings.TEXT_EDITOR.value.formatted(file.getPath());
		try{
			Process pr = Runtime.getRuntime().exec(cmd.split(" "), null, file.getParentFile());
			new Thread(() -> {
				Logging.log("Opening external editor for file: " + file);
				BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line = null;
				try{
					while((line = input.readLine()) != null) Logging.log(line);
				}
				catch(IOException e){
					Logging.log(e);
				}
				Logging.log("=================");
			}).start();
		}
		catch(IOException e){
			Logging.log(e);
			Logging.log("Opening external editor failed.");
			Logging.log("=================");
		}
	}

	public static void show0(){
		if(viewer == null){
			viewer = new WorkspaceViewer();
			//FMT.FRAME.getContainer().add(viewer);
		}
		viewer.show();
	}

	public static WorkspaceViewer viewer(){
		if(viewer == null){
			viewer = new WorkspaceViewer();
			//FMT.FRAME.getContainer().add(viewer);
		}
		return viewer;
	}

}
