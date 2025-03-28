package net.fexcraft.app.fmt.ui.workspace;

import com.google.common.io.Files;
import com.spinyowl.legui.component.*;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.spinyowl.legui.style.color.ColorConstants;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.components.ConfigGeneral;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.ByteUtils;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.fvtm.LangCache;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class WorkspaceViewer extends Widget {

	public static final String[] types = new String[]{
		"Vehicle", "Part", "Material", "Consumable", "Fuel", "Block", "Wire", "Deco", "RailGauge", "Cloth", "Sign"
	};
	public static WorkspaceViewer viewer;
	public static final int ROWHEIGHT = 30;
	public ArrayList<FvtmPack> rootfolders = new ArrayList<FvtmPack>();
	public ScrollablePanel packspanel;
	public RunButton refresh;
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
			refresh.setPosition(vec.x - 110, 5);
			resize();
		});
		Settings.applyBorderless(packspanel);
		getContainer().add(packspanel);
		getContainer().add(refresh = new RunButton("Refresh", getSize().x - 110, 5, 100, 20, () -> genView(false)));
		//
		folder = new File(Settings.WORKSPACE_ROOT.value);
		if(!folder.exists()) folder.mkdirs();
		genView(true);
	}

	public void selectContentTypeDialog(Consumer<String> cons){
		Dialog dialog = new Dialog("Please select a ContentType.", 320, 70);
		SelectBox<String> select = new SelectBox<>(10, 10, 300, 30);
		for(String str : types){
			select.addElement(str);
		}
		select.addSelectBoxChangeSelectionEventListener(event -> {
			dialog.close();
			cons.accept(event.getNewValue());
		});
		select.setVisibleCount(8);
		dialog.getContainer().add(select);
		dialog.setResizable(false);
		dialog.show(FMT.FRAME);
	}

	public void selectPackDialog(Consumer<FvtmPack> cons){
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
			FMT.FRAME.getContainer().add(viewer);
		}
		viewer.show();
	}

	public void genView(){
		genView(false);
	}

	private void genView(boolean initial){
		if(initial) findPacks();
		new Thread(() -> {
			Logging.log("Reloading Workspace");
			refresh.setEnabled(false);
			refresh.getTextState().setText("reloading...");
			//packspanel.getContainer().removeAll(rootfolders);
			//infopanel.getContainer().clearChildComponents();
			//rootfolders.clear();
			findPacks();
			//addFolder(folder, null, 0);
			resize();
			packspanel.getVerticalScrollBar().setScrollStep(0f);
			refresh.setEnabled(true);
			refresh.getTextState().setText("Refresh");
		}, "FolderViewGenerator").start();
	}

	private void findPacks(){
		File assets;
		ArrayList<FvtmPack> npacks = new ArrayList<>();
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
			npacks.add(pack);
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
		rootfolders.clear();
		packspanel.getContainer().removeAll(rootfolders);
		for(FvtmPack pack : npacks){
			rootfolders.add(pack);
			packspanel.getContainer().add(pack);
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
	}

	public static WorkspaceViewer viewer(){
		if(viewer == null){
			viewer = new WorkspaceViewer();
			FMT.FRAME.getContainer().add(viewer);
		}
		return viewer;
	}

}
