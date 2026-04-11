package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.oui.ConverterUtils;
import net.fexcraft.app.fmt.oui.UVViewer;
import net.fexcraft.app.fmt.oui.workspace.WorkspaceViewer;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.editor.EditorRoot;
import net.fexcraft.app.fmt.oui.ProfileDialog;
import net.fexcraft.app.fmt.oui.SettingsDialog;
import net.fexcraft.app.fmt.ui.tree.TreeRoot;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.FontUtils;
import net.fexcraft.app.fmt.utils.ImageHandler;
import net.fexcraft.app.fmt.utils.SaveHandler;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FMTInterface extends Element {

	public static final int TOOLBAR_HEIGHT = 40;
	public static final int TOOLBAR_WIDTH = 300;
	public static final int EDITOR_WIDTH = 320;
	public static final int EDITOR_CONTENT = EDITOR_WIDTH - 30;
	public static final int MENU_WIDTH = 202;
	public static RGB col_75 = new RGB(0x757575);
	public static RGB col_85 = new RGB(0x858585);
	public static RGB col_bd = new RGB(0xbdbdbd);
	public static RGB col_cd = new RGB(0xcdcdcd);
	public static Dialog DIALOG;
	public static Element toolbar;
	public static Element statusbar;
	public static EditorRoot editor;
	public static TreeRoot tree;
	public static Menu recent;
	private static Long bar_timer;
	private static String bar_text;
	private UpdateHandler.UpdateCompound updcom = new UpdateHandler.UpdateCompound();

	public FMTInterface(){
		super();
		add(toolbar = new Element().size(TOOLBAR_WIDTH, TOOLBAR_HEIGHT).color(col_cd));
		add(statusbar = new Element(){
			@Override
			public void onResize(){
				size(FMT.SCALED_WIDTH - TOOLBAR_WIDTH - EDITOR_WIDTH, TOOLBAR_HEIGHT);
				pos(TOOLBAR_WIDTH, 0);
			}
		}.size(FMT.SCALED_WIDTH - TOOLBAR_WIDTH - EDITOR_WIDTH, TOOLBAR_HEIGHT)
			.pos(TOOLBAR_WIDTH, 0).color(col_cd)
			.text("...")
		);
		toolbar.z += 100;
		toolbar.recompile();
		int iinc = 37;
		int buff = -iinc + 4;
		int yo = 4;
		toolbar.add(new Element(){
				@Override
				public void init(Object... args){
					Menu menu = new Menu(MENU_WIDTH);
					add(menu.pos(0, 32));
					menu.addEntry("toolbar.info.wiki", ci -> FMT.openLink("https://fexcraft.net/wiki/app/fmt"));
					menu.addEntry("toolbar.info.donate", ci -> FMT.openLink("https://fexcraft.net/donate"));
					menu.addEntry("toolbar.file.exit", ci -> FMT.close(0));
					onclick(ci -> menu.toggleVisibility());
				}
			}.pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/info").hoverable(true)
			.hint("toolbar.icon.info"));
		toolbar.add(new Element(){
				@Override
				public void init(Object... args){
					Menu menu = new Menu(MENU_WIDTH);
					add(menu.pos(0, 32));
					menu.addEntry("toolbar.utils.copy_selected", ci -> FMT.MODEL.copySelected());
					Menu clipboard = menu.addEntry("toolbar.utils.clipboard", new Menu(MENU_WIDTH));
					clipboard.addEntry("toolbar.utils.clipboard.copy", ci -> FMT.MODEL.copyToClipboard(false));
					clipboard.addEntry("toolbar.utils.clipboard.paste", ci -> FMT.MODEL.pasteFromClipboard());
					clipboard.addEntry("toolbar.utils.clipboard.copy_grouped", ci -> FMT.MODEL.copyToClipboard(true));
					menu.addEntry("toolbar.utils.workspace", ci -> WorkspaceViewer.show0());
					menu.addEntry("toolbar.utils.reset_camera", ci -> FMT.CAM.reset());
					menu.addEntry("toolbar.utils.create_gif", ci -> ImageHandler.createGif());
					menu.addEntry("toolbar.utils.screenshot", ci -> ImageHandler.takeScreenshot(false));
					menu.addEntry("toolbar.utils.uv_viewer", ci -> UVViewer.addIfAbsent());
					menu.addEntry("toolbar.utils.rescale", ci -> FMT.MODEL.rescale());
					menu.addEntry("toolbar.utils.font_util", ci -> FontUtils.open());
					Menu conv = menu.addEntry("toolbar.utils.converters", new Menu(MENU_WIDTH * 1.5f));
					conv.addEntry("Item Model Texture Location", ci -> ConverterUtils.runIMTJ());
					conv.addEntry("Extract Materials / FVTM Obj", ci -> ConverterUtils.exModelData());
					menu.addEntry("toolbar.utils.settings", ci -> SettingsDialog.open());
					menu.addEntry("toolbar.utils.controls", ci -> {});
					onclick(ci -> {
						if(ci.button() == 0) menu.toggleVisibility();
						else if(ci.button() == 1) SettingsDialog.open();
					});
				}
			}.pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/settings").hoverable(true)
			.hint("toolbar.icon.settings"));
		toolbar.add(new Element().pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/profile").hoverable(true)
			.onclick(ci -> ProfileDialog.open())
			.hint("toolbar.icon.profile"));
		toolbar.add(new Element(){
				@Override
				public void init(Object... args){
					Menu menu = new Menu(MENU_WIDTH);
					add(menu.pos(0, 32));
					menu.addEntry("toolbar.file.save", ci -> SaveHandler.saveDialogByState(null));
					menu.addEntry("toolbar.file.save_as", ci -> SaveHandler.saveAsDialog(null));
					menu.addEntry("toolbar.file.export", ci -> ExportManager.export());
					onclick(ci -> {
						if(ci.button() == 0) menu.toggleVisibility();
						else if(ci.button() == 1) SaveHandler.save(FMT.MODEL, null, null, false, false);
					});
				}
			}.pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/save").hoverable(true)
			.hint("toolbar.icon.save"));
		toolbar.add(new Element(){
				@Override
				public void init(Object... args){
					Menu menu = new Menu(MENU_WIDTH);
					add(menu.pos(0, 32));
					menu.addEntry("toolbar.file.open", ci -> SaveHandler.openDialog(null));
					recent = menu.addEntry("toolbar.file.recent", new Menu(MENU_WIDTH));
					menu.addEntry("toolbar.file.import", ci -> ImportManager._import());
					for(int i = 0; i < 10; i++){
						int idx = i;
						recent.addEntry(Settings.RECENT.get(i).getName(), ci -> Settings.openRecent(idx));
					}
					onclick(ci -> {
						if(ci.button() == 0) menu.toggleVisibility();
						else if(ci.button() == 1) SaveHandler.openDialog(null);
					});
				}
			}.pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/open").hoverable(true)
			.hint("toolbar.icon.open"));
		toolbar.add(new Element().pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/new").hoverable(true)
			.onclick(ci -> SaveHandler.newDialog())
			.hint("toolbar.icon.new"));
		toolbar.add(new Element().pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/editor").hoverable(true)
			.onclick(ci -> editor.toggle())
			.hint("toolbar.icon.editor"));
		toolbar.add(new Element().pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/tree").hoverable(true)
			.onclick(ci -> tree.toggle())
			.hint("toolbar.icon.tree"));
		//
		add((editor = new EditorRoot()));
		add((tree = new TreeRoot()));
		//
		updcom.add(UpdateEvent.PolygonSelected.class, e -> {
			if(Element.SELECTED instanceof DropList){
				Element.select(null);
			}
		});
		UpdateHandler.register(updcom);
	}

	@Override
	public void render(){
		for(Element elm : elements) elm.render();
	}

	@Override
	public void update(){
		if(bar_timer != null && Time.getDate() > bar_timer){
			bar_timer = null;
			bar_text = null;
		}
		statusbar.text("FPS: " + FMT.timer.getFPS() + (bar_text == null ? "" : " | " + bar_text));
	}

	public void click(double x, double y, int b){
		Element elm = getElmAt(x, y);
		if(elm != null) elm.click((int)x, (int)y, b);
	}

	@Override
	public Element recompile(){
		//
		return this;
	}

	@Override
	public void delete(){
		//
	}

	public static void bar(String string, int secs){
		bar_timer = Time.getDate() + Time.SEC_MS * secs;
		bar_text = string;
	}

	public Dialog createDialog(int w, int h, String title){
		if(DIALOG != null) DIALOG.close();
		DIALOG = new Dialog(w, h);
		add(0, DIALOG, title);
		return DIALOG;
	}

}
