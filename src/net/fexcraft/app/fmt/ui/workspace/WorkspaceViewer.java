package net.fexcraft.app.fmt.ui.workspace;

import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.Logging;
import org.joml.Vector2f;

import java.io.File;
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
	public RunButton button;
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
			button.setPosition(vec.x - 110, 5);
			resize();
		});
		Settings.applyBorderless(packspanel);
		Settings.applyBorderless(infopanel);
		getContainer().add(packspanel);
		getContainer().add(infopanel);
		getContainer().add(button = new RunButton("refresh", getSize().x - 110, 5, 100, 20, () -> genView()));
		folder = new File(Settings.WORKSPACE_ROOT.value);
		if(!folder.exists()) folder.mkdirs();
		genView();
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
