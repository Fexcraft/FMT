package net.fexcraft.app.fmt.ui.workspace;

import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Logging;
import org.joml.Vector2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
			packspanel.setSize(310, getSize().y - 30);
			infopanel.setSize(vec.x - 310, getSize().y - 30);
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
		folder = new File(Settings.WORKSPACE_ROOT.value);
		if(!folder.exists()) folder.mkdirs();
		genView();
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
			Process pr = Runtime.getRuntime().exec(new String[]{ cmd }, new String[0], new File(bool ? Settings.M12PATH.value : Settings.M20PATH.value));
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
				infopanel.getContainer().clearChildComponents();
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
		packspanel.getContainer().setSize(310, scrollableheight < height ? height : scrollableheight);
		infopanel.getContainer().setSize(viewer.getSize().x - 310, viewer.getSize().y - 30);
	}

}
