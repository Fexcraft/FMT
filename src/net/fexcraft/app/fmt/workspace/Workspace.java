package net.fexcraft.app.fmt.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Dialog.DialogButton;
import net.fexcraft.app.fmt.ui.DropList;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Frame;
import net.fexcraft.app.fmt.ui.Scrollable;
import net.fexcraft.app.fmt.update.UpdateEvent.WorkspaceName;
import net.fexcraft.app.fmt.update.UpdateEvent.WorkspaceRoot;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.lib.common.math.RGB;

import static net.fexcraft.app.fmt.settings.Settings.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Workspace extends Frame {

	public static int FILES_PANEL_WIDTH = 400;
	public static int FILES_PANEL_DIR = FILES_PANEL_WIDTH - 30;
	private ArrayList<FvtmPackElm> fvtm_packs = new ArrayList<>();
	protected Scrollable packs;
	protected CheckMode cm_in_packs;
	private boolean loaded;
	public String name;
	public File root_folder;
	
	public Workspace(){
		name = Settings.WORKSPACE_NAME.value;
		root_folder = new File(Settings.WORKSPACE_ROOT.value);
		pos(20, 20);
		size(1400, 800);
		color(GENERIC_BACKGROUND_0.value);
		border(RGB.BLACK);
	}

	@Override
	public void init(Object... args){
		add(new Element().size(w - 31, 30).color(GENERIC_BACKGROUND_1.value)
			.onclick(ci -> FMT.UI.setFrameOnTop(this))
			.translate("workspace.title", name).text_autoscale());
		add(new Element().size(30, 30).pos(w - 31, 0).texture("icons/component/exit")
			.hoverable(true).onclick(ci -> hide()));
		add((packs = new Scrollable(true, 30)).size(FILES_PANEL_WIDTH, h - 31).pos(0, 31));
		packs.border(RGB.BLACK);
		packs.updateBar();
		cm_in_packs = CheckMode.gen(packs);
	}

	public void update(Setting<?> setting){
		if(setting.id.equals("name")){
			String oname = name;
			name = setting.value();
			FMT.updateTitle();
			UpdateHandler.update(new WorkspaceName(oname, name));
		}
		if(setting.id.equals("root")){
			File oroot = root_folder;
			root_folder = new File((String)setting.value());
			UpdateHandler.update(new WorkspaceRoot(oroot, root_folder));
		}
	}

	@Override
	public Element show(){
		if(!loaded) load(null);
		FMT.UI.setFrameOnTop(this);
		return super.show();
	}

	private void load(Runnable run){
		loaded = true;
		pos((FMT.SCALED_WIDTH - w) * 0.5f, (FMT.SCALED_HEIGHT - h) * 0.5f);
		reloadPacks(run);
	}

	public void reloadPacks(Runnable run){
		Thread thread = new Thread(null, () -> {
			File assets;
			ArrayList<FvtmPackElm> found = new ArrayList<>();
			for(File fold : root_folder.listFiles()){
				if(!fold.isDirectory()) continue;
				if(!(assets = new File(fold, "assets")).exists()) continue;
				FvtmPackElm pack = null;
				for(File sub : assets.listFiles()){
					if(!sub.isDirectory()) continue;
					if(!new File(sub, "addonpack.fvtm").exists()) continue;
					pack = new FvtmPackElm(fold, sub.getName());
				}
				if(pack == null) continue;
				Logging.log("Found pack with id '" + pack.id + "'.");
				found.add(pack);
			}
			FMT.queue(() -> {
				packs.remElmIf(elm -> elm instanceof DirElm);
				for(FvtmPackElm pack : found){
					FMT.queue(() -> {
						packs.add(pack, pack);
						fvtm_packs.add(pack);
					});
				}
				FMT.queue(() -> {
					packs.updateBar();
					if(run != null) run.run();
				});
			});
		});
		thread.setName("Workspace Pack Finder");
		thread.start();
	}

	public void selectContentType(Consumer<FvtmType> cons){
		DropList<FvtmType> list = new DropList<>(490);
		FMT.UI.createDialog(500, 120, "workspace.content_utils")
			.addText(0, "workspace.select_content_type")
			.addRowElm(1, list)
			.consumer(d -> cons.accept(list.getSelVal()), null)
			.buttons(100, DialogButton.CONTINUE);
		for(FvtmType type : FvtmType.values()){
			list.addEntry(type._name, type);
		}
		list.selectEntry(0);
	}

	public void selectPack(Consumer<FvtmPackElm> cons){
		if(!loaded){
			load(() -> selectPack(cons));
			return;
		}
		DropList<FvtmPackElm> list = new DropList<>(490);
		FMT.UI.createDialog(500, 120, "workspace.content_utils")
			.addText(0, "workspace.select_pack")
			.addRowElm(1, list)
			.consumer(d -> cons.accept(list.getSelVal()), null)
			.buttons(100, DialogButton.CONTINUE);
		for(FvtmPackElm pack : fvtm_packs){
			list.addEntry(pack.id, pack);
		}
		list.selectEntry(0);
	}

}
