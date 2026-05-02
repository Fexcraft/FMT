package net.fexcraft.app.fmt.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

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
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Workspace extends Frame {

	public static int FILES_PANEL_WIDTH = 400;
	public static int FILES_PANEL_DIR = FILES_PANEL_WIDTH - 30;
	private ArrayList<FvtmPackElm> fvtm_packs = new ArrayList<>();
	private ArrayList<WFileEditor> file_editors = new ArrayList<>();
	private WFileEditor file_editor;
	protected Scrollable opened_files;
	protected Scrollable packs;
	protected CheckMode cm_in_packs;
	protected Element files_open;
	private boolean loaded;
	public String name;
	public File root_folder;

	public Workspace(){
		name = Settings.WORKSPACE_NAME.value;
		root_folder = new File(Settings.WORKSPACE_ROOT.value);
		pos(20, 20);
		size(1200, 800);
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
		//
		add(files_open = new Element().pos(FILES_PANEL_WIDTH, 31).size(w - FILES_PANEL_WIDTH - 1, 30)
			.color(GENERIC_BACKGROUND_1.value).border(GENERIC_BACKGROUND_2.value)
			.translate("workspace.file_editor.none_open").text_autoscale());
		files_open.add(new Element().pos(files_open.w - 185, 2).size(FS, FS)
			.texture("icons/component/list").border(GENERIC_BACKGROUND_0.value)
			.onclick(ci -> opened_files.toggleVisibility())
			.hint("workspace.file_editor.list"));
		files_open.add(new Element().pos(files_open.w - 155, 2).size(FS, FS)
			.texture("icons/component/edit").border(GENERIC_BACKGROUND_0.value)
			.onclick(ci -> { if(file_editor != null) openExternal(file_editor.file); })
			.hint("workspace.file_editor.external"));
		files_open.add(new Element().pos(files_open.w - 125, 2).size(FS, FS)
			.texture("icons/toolbar/save").border(GENERIC_BACKGROUND_0.value)
			.onclick(ci -> { if(file_editor != null) file_editor.save(); })
			.hint("workspace.file_editor.save"));
		files_open.add(new Element().pos(files_open.w - 95, 2).size(FS, FS)
			.texture("icons/component/move_left").border(GENERIC_BACKGROUND_0.value)
			.onclick(ci -> { setActive(file_editor, -1); })
			.hint("workspace.file_editor.prev"));
		files_open.add(new Element().pos(files_open.w - 65, 2).size(FS, FS)
			.texture("icons/component/move_right").border(GENERIC_BACKGROUND_0.value)
			.onclick(ci -> { setActive(file_editor, 1); })
			.hint("workspace.file_editor.next"));
		files_open.add(new Element().pos(files_open.w - 35, 2).size(FS, FS)
			.texture("icons/component/exit").border(GENERIC_BACKGROUND_0.value)
			.onclick(ci -> closeActive())
			.hint("workspace.file_editor.close"));
		files_open.add(opened_files = new Scrollable(true, 0));
		opened_files.updateSize(400, 400);
		opened_files.pos(files_open.w - 440, 30);
		opened_files.border(RGB.BLACK).hide();
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

	public static void run(boolean v12){
		String cmd;
		if(v12){
			cmd = Settings.M12RCMD.value.replace("{JAVA}", Settings.JAVA8_PATH.value + "/bin/java");
		}
		else{
			cmd = Settings.M20RCMD.value.replace("{JAVA}", Settings.JAVA17_PATH.value + "/bin/java");
		}
		try{
			Process pr = Runtime.getRuntime().exec(cmd.split(" "), null, new File(v12 ? Settings.M12PATH.value : Settings.M20PATH.value));
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

	public static void openExternal(File file){
		if(file == null || !file.exists()) return;
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

	public void open(File file, Function<File, WFileEditor> func){
		for(WFileEditor fe : file_editors){
			if(fe.file.equals(file)){
				setActive(fe, 0);
				return;
			}
		}
		WFileEditor edit = func.apply(file);
		edit.size(w - FILES_PANEL_WIDTH - 1, h - 61).pos(FILES_PANEL_WIDTH + 1, 61);
		file_editors.add(edit);
		add(edit);
		setActive(edit, 0);
		opened_files.add(new WFileEditor.WFileEditorEntry(edit).size(opened_files.w - 30, FS));
		opened_files.updateBar();
	}

	protected void setActive(WFileEditor edit, int changeindex){
		if(changeindex != 0){
			int idx = file_editors.indexOf(edit) + changeindex;
			if(idx < 0) idx = file_editors.size() - 1;
			if(idx >= file_editors.size()) idx = 0;
			edit = file_editors.get(idx);
		}
		files_open.text(edit.get_title());
		opened_files.hide();
		for(Element editor : file_editors) editor.hide();
		file_editor = edit;
		edit.show();
	}

	protected void closeActive(){
		if(file_editors.size() < 2){
			file_editor = null;
			file_editors.clear();
			opened_files.clear();
			files_open.translate("workspace.file_editor.none_open");
		}
		else{
			opened_files.remElmIf(elm -> elm instanceof WFileEditor.WFileEditorEntry entry && entry.editor == file_editor);
			file_editors.remove(file_editor);
			file_editor = file_editors.get(file_editors.size() - 1);
			setActive(file_editor, 0);
		}
		opened_files.updateBar();
	}

}
