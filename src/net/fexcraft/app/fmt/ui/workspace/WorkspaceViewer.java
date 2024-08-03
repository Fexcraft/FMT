package net.fexcraft.app.fmt.ui.workspace;

import com.spinyowl.legui.component.*;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import org.joml.Vector2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class WorkspaceViewer extends Widget {

	public static WorkspaceViewer viewer;
	public static final int ROWHEIGHT = 30;
	public ArrayList<FvtmPack> rootfolders = new ArrayList<FvtmPack>();
	public ScrollablePanel packspanel;
	public ScrollablePanel infopanel;
	public RunButton refresh;
	public RunButton run12;
	public RunButton run20;
	public File folder;
	public int scrollableheight;

	public WorkspaceViewer(){
		super(Settings.WORKSPACE_NAME.value);
		setSize(620, 500);
		getContainer().setSize(getSize());
		setResizable(true);
		setPosition(20, 20);
		packspanel = new ScrollablePanel();
		packspanel.setPosition(0, 30);
		packspanel.setSize(310, getSize().y - 30);
		infopanel = new ScrollablePanel();
		infopanel.setPosition(310, 30);
		infopanel.setSize(310, getSize().y - 30);
		getListenerMap().addListener(ChangeSizeEvent.class, event -> {
			Vector2f vec = new Vector2f();
			event.getNewSize().get(vec);
			if(vec.x < 620){
				setSize(620, vec.y);
				return;
			}
			if(vec.y < 300){
				setSize(vec.x, 300);
				return;
			}
			getContainer().setSize(vec);
			packspanel.setSize(vec.x - 310, getSize().y - 30);
			infopanel.setSize(310, getSize().y - 30);
			infopanel.setPosition(vec.x - 310, 30);
			refresh.setPosition(vec.x - 110, 5);
			run12.setPosition(vec.x - 220, 5);
			run20.setPosition(vec.x - 330, 5);
			resize();
		});
		Settings.applyBorderless(packspanel);
		Settings.applyBorderless(infopanel);
		getContainer().add(packspanel);
		getContainer().add(infopanel);
		getContainer().add(refresh = new RunButton("Refresh", getSize().x - 110, 5, 100, 20, () -> genView()));
		getContainer().add(run12 = new RunButton("Run 1.12", getSize().x - 220, 5, 100, 20, () -> run(true)));
		getContainer().add(run20 = new RunButton("Run 1.20", getSize().x - 330, 5, 100, 20, () -> run(false)));
		//
		folder = new File(Settings.WORKSPACE_ROOT.value);
		if(!folder.exists()) folder.mkdirs();
		infopanel.setVerticalScrollBarVisible(false);
		infopanel.getContainer().add(new RunButton("Create a new Pack", 10, 10, 290, 30, () -> {
			Dialog dialog = new Dialog("Pack Creation Settings", 420, 190);
			dialog.getContainer().add(new Label("Pack Name:", 10, 10, 400, 30));
			TextField name = new TextField("pack name", 10, 40, 400, 30);
			dialog.getContainer().add(name);
			dialog.getContainer().add(new Label("Pack ID:", 10, 70, 400, 30));
			TextField pid = new TextField("pack_id", 10, 100, 400, 30);
			dialog.getContainer().add(pid);
			dialog.getContainer().add(new RunButton("dialog.button.confirm", 310, 140, 100, 20, () -> {
				File folder = new File(Settings.WORKSPACE_ROOT.value);
				File pkfd = new File(folder, name.getTextState().getText() + "/assets/" + pid.getTextState().getText() + "/");
				pkfd.mkdirs();
				JsonMap map = new JsonMap();
				map.add("ID", pid.getTextState().getText());
				map.add("Name", name.getTextState().getText());
				map.add("Version", "1.0.0");
				map.add("License", "All Rights Reserved");
				map.add("Dependencies", new JsonArray("gep"));
				map.add("Authors", SessionHandler.isLoggedIn() ? new JsonArray(SessionHandler.getUserName()) : new JsonArray());
				map.add("#info", "File generated via FMT.");
				JsonHandler.print(new File(pkfd, "addonpack.fvtm"), map, JsonHandler.PrintOption.DEFAULT);
				dialog.close();
				genView();
			}));
			dialog.setResizable(false);
			dialog.show(FMT.FRAME);
		}));
		infopanel.getContainer().add(new RunButton("Generate Config/Asset Directories", 10, 50, 290, 30, () -> {
			selectPackDialog(pack -> {
				Dialog dialog = new Dialog("Please select Config Types.", 320, 250);
				HashMap<String, CheckBox> map = new HashMap<>();
				map.put("vehicles", new CheckBox("vehicles", 10, 10, 300, 20));
				map.put("parts", new CheckBox("parts", 10, 30, 300, 20));
				map.put("materials", new CheckBox("materials", 10, 50, 300, 20));
				map.put("consumables", new CheckBox("consumables", 10, 70, 300, 20));
				map.put("blocks", new CheckBox("blocks", 10, 90, 300, 20));
				map.put("wires", new CheckBox("wires", 10, 110, 300, 20));
				map.put("decos", new CheckBox("decos", 10, 130, 300, 20));
				map.put("railgauges", new CheckBox("rail gauges", 10, 150, 300, 20));
				map.put("clothes", new CheckBox("clothes", 10, 170, 300, 20));
				for(CheckBox box : map.values()){
					box.getStyle().setPadding(0, 0, 0, 5);
				}
				dialog.getContainer().addAll(map.values());
				dialog.getContainer().add(new RunButton("dialog.button.confirm", 10, 200, 100, 20, () -> {
					dialog.close();
					File fl;
					for(Map.Entry<String, CheckBox> entry : map.entrySet()){
						if(!entry.getValue().isChecked()) continue;
						fl = new File(pack.file, "/assets/" + pack.id + "/config/" + entry.getKey());
						fl.mkdirs();
						fl = new File(pack.file, "/assets/" + pack.id + "/textures/" + entry.getKey().substring(0, entry.getKey().length() - 1));
						fl.mkdirs();
						fl = new File(pack.file, "/assets/" + pack.id + "/models/" + entry.getKey().substring(0, entry.getKey().length() - 1));
						fl.mkdirs();
					}
					genView();
				}));
				dialog.setResizable(false);
				dialog.show(FMT.FRAME);
			});
		}));
		genView();
	}

	private void selectPackDialog(Consumer<FvtmPack> cons){
		Dialog dialog = new Dialog("Please select a pack.", 320, 70);
		SelectBox<String> select = new SelectBox<>(10, 10, 300, 30);
		for(FvtmPack pack : rootfolders){
			select.addElement(pack.id);
		}
		select.addSelectBoxChangeSelectionEventListener(event -> {
			dialog.close();
			FvtmPack pack = getPack(event.getNewValue());
			if(pack != null) cons.accept(pack);
		});
		select.setVisibleCount(8);
		dialog.getContainer().add(select);
		dialog.setResizable(false);
		dialog.show(FMT.FRAME);
	}

	private FvtmPack getPack(String id){
		for(FvtmPack pack : rootfolders){
			if(pack.id.equals(id)) return pack;
		}
		return null;
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

	public static void show0(){
		if(viewer == null){
			viewer = new WorkspaceViewer();
			FMT.FRAME.getContainer().add(viewer);
		}
		viewer.show();
	}

	public void genView(){
		new Thread("FolderViewGenerator"){
			@Override
			public void run(){
				Logging.log("Reloading Workspace");
				packspanel.getContainer().removeAll(rootfolders);
				//infopanel.getContainer().clearChildComponents();
				rootfolders.clear();
				findPacks();
				//addFolder(folder, null, 0);
				resize();
				packspanel.getVerticalScrollBar().setScrollStep(0f);
			}
		}.start();
	}

	private void findPacks(){
		File assets;
		for(File fold : folder.listFiles()){
			if(!fold.isDirectory()) continue;
			if(!(assets = new File(fold, "assets")).exists()) continue;
			FvtmPack pack = null;
			for(File sub : assets.listFiles()){
				if(!sub.isDirectory()) continue;
				if(!new File(sub, "addonpack.fvtm").exists()) continue;
				pack = new FvtmPack(ViewerFileType.FVTM_FOLDER, this, null, fold, sub.getName());
			}
			if(pack == null) continue;
			Logging.log("Found pack with id '" + pack.id + "'.");
			rootfolders.add(pack);
			packspanel.getContainer().add(pack);
			for(File file : fold.listFiles()){
				addFolder(file, pack, pack, 0);
			}
			for(FvtmType ft : FvtmType.values()){
				if(pack.content.get(ft).size() > 0){
					Logging.log("  "  + ft.name().toLowerCase() + "s: " + pack.content.get(ft).size());
				}
			}
			Logging.log("  textures: " + pack.textures.size());
		}
	}

	private int addFolder(File folder, FvtmPack pack, DirComponent root, int rrow){
		if(!folder.isDirectory()) return rrow;
		if(folder.getName().startsWith(".")) return rrow;
		DirComponent com = null;
		if(folder.listFiles().length == 0){
			com = new DirComponent(ViewerFileType.EMPTY_FOLDER, this, root, folder, rrow);
		}
		else{
			com = new DirComponent(ViewerFileType.NORMAL_FOLDER, this, root, folder,  rrow);
			int row = 1;
			for(File file : folder.listFiles()){
				if(file.isDirectory()) row += addFolder(file, pack, com, row);
			}
			for(File file : folder.listFiles()){
				if(!file.isDirectory()){
					Object tipo = ViewerFileType.fromFile(file);
					boolean ext = tipo instanceof  ViewerFileType == false;
					ViewerFileType type = (ViewerFileType)(ext ? ((Object[])tipo)[0] : tipo);
					if(type == ViewerFileType.FILE) continue;
					DirComponent dircom = new DirComponent(type, this, root, file, row++);
					if(ext){
						pack.content.get((FvtmType)((Object[])tipo)[1]).add(dircom);
					}
					if(type == ViewerFileType.PNG){
						pack.textures.add(dircom);
					}
					if(type.model()){
						pack.models.add(dircom);
					}
					com.addSub(dircom);
				}
			}
		}
		root.addSub(com);
		return folder.list().length;
	}

	protected void resize(){
		float height = viewer.getSize().y - 30;
		scrollableheight = 0;
		for(DirComponent com : rootfolders){
			scrollableheight += com.resize(scrollableheight, true);
		}
		scrollableheight = 0;
		for(DirComponent com : rootfolders){
			scrollableheight += com.fullsize();
		}
		packspanel.getContainer().setSize(packspanel.getSize().x, scrollableheight < height ? height : scrollableheight);
		infopanel.getContainer().setSize(310, viewer.getSize().y - 30);
	}

}
