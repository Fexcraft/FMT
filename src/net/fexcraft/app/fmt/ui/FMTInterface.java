package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.TextElm.BottomInfoText;
import net.fexcraft.app.fmt.ui.editor.EditorRoot;
import net.fexcraft.app.fmt.ui.tree.TreeRoot;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.*;
import net.fexcraft.app.fmt.workspace.Workspace;
import net.fexcraft.lib.common.math.Time;

import java.util.ArrayList;
import java.util.Collections;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FMTInterface extends Element {

	public static final int TOOLBAR_HEIGHT = 40;
	public static final int TOOLBAR_WIDTH = 300;
	public static final int EDITOR_WIDTH = 320;
	public static final int EDITOR_CONTENT = EDITOR_WIDTH - 30;
	public static final int MENU_WIDTH = 202;
	public static ArrayList<Frame> FRAMES = new ArrayList<>();
	public static SettingsUI settings;
	public static UVViewer uvviewer;
	public static Dialog DIALOG;
	public static Element toolbar;
	public static Element statusbar;
	public static EditorRoot editor;
	public static TreeRoot tree;
	public static Menu recent;
	public static BottomInfoText info_fps;
	public static BottomInfoText info_position;
	public static BottomInfoText info_rotation;
	public static BottomInfoText info_selected;
	public static BottomInfoText info_field;
	private static Long bar_timer;
	private static String bar_text;
	private UpdateHandler.UpdateCompound updcom = new UpdateHandler.UpdateCompound();

	public FMTInterface(){
		super();
		add(toolbar = new Element().size(TOOLBAR_WIDTH, TOOLBAR_HEIGHT).color(GENERIC_BACKGROUND_0.value).zi(100));
		add(statusbar = new Element(){
			@Override
			public void onResize(){
				size(FMT.SCALED_WIDTH - TOOLBAR_WIDTH - EDITOR_WIDTH, TOOLBAR_HEIGHT);
				pos(TOOLBAR_WIDTH, 0);
			}
		}.size(FMT.SCALED_WIDTH - TOOLBAR_WIDTH - EDITOR_WIDTH, TOOLBAR_HEIGHT)
			.pos(TOOLBAR_WIDTH, 0).color(GENERIC_BACKGROUND_0.value).text("..."));
		statusbar.text_pos(5, 5);
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
					menu.addEntry("toolbar.utils.workspace", ci -> FMT.WORKSPACE.show());
					menu.addEntry("toolbar.utils.reset_camera", ci -> FMT.CAM.reset());
					menu.addEntry("toolbar.utils.create_gif", ci -> ImageHandler.createGif());
					menu.addEntry("toolbar.utils.screenshot", ci -> ImageHandler.takeScreenshot(false));
					menu.addEntry("toolbar.utils.uv_viewer", ci -> uvviewer.show());
					menu.addEntry("toolbar.utils.rescale", ci -> FMT.MODEL.rescale());
					menu.addEntry("toolbar.utils.font_util", ci -> FontUtils.open());
					Menu conv = menu.addEntry("toolbar.utils.converters", new Menu(MENU_WIDTH * 1.5f));
					conv.addEntry("Item Model Texture Location", ci -> ConverterUtils.runIMTJ());
					conv.addEntry("Extract Materials / FVTM Obj", ci -> ConverterUtils.exModelData());
					menu.addEntry("toolbar.utils.settings", ci -> settings.show());
					menu.addEntry("toolbar.utils.controls", ci -> {});
					onclick(ci -> {
						if(ci.button() == 0) menu.toggleVisibility();
						else if(ci.button() == 1) settings.show();
					});
				}
			}.pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/settings").hoverable(true)
			.hint("toolbar.icon.settings"));
		toolbar.add(new Element().pos(buff += iinc, yo).size(32, 32)
			.texture("icons/toolbar/profile").hoverable(true)
			.onclick(ci -> ProfileInfo.open())
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
		add((uvviewer = new UVViewer()).hide());
		add((settings = new SettingsUI()).hide());
		add((FMT.WORKSPACE = new Workspace()).hide());
		add((editor = new EditorRoot()));
		add((tree = new TreeRoot()));
		add(info_fps = new BottomInfoText(0, 0, 1000));
		add(info_position = new BottomInfoText(0, 0, 1000));
		add(info_rotation = new BottomInfoText(0, 0, 1000));
		add(info_selected = new BottomInfoText(0, 0, 1000));
		add(info_field = new BottomInfoText(0, 0, 1000));
		positionInfoText();
		//
		updcom.add(UpdateEvent.PolygonSelected.class, e -> {
			if(Element.SELECTED instanceof DropList){
				Element.select(null);
			}
		});
		UpdateHandler.register(updcom);
	}

	public void positionInfoText(){
		float x = editor.visible ? editor.w + 5 : 5;
		float off = 5.2f;
		info_field.pos(x, FMT.SCALED_HEIGHT - FS * off--).text_color(INFO_TEXT_COLOR.value.packed);
		info_fps.pos(x, FMT.SCALED_HEIGHT - FS * off--).text_color(INFO_TEXT_COLOR.value.packed);
		info_rotation.pos(x, FMT.SCALED_HEIGHT - FS * off--).text_color(INFO_TEXT_COLOR.value.packed);
		info_position.pos(x, FMT.SCALED_HEIGHT - FS * off--).text_color(INFO_TEXT_COLOR.value.packed);
		info_selected.pos(x, FMT.SCALED_HEIGHT - FS * off).text_color(INFO_TEXT_COLOR.value.packed);
	}

	@Override
	public void onResize(){
		positionInfoText();
	}

	@Override
	public void render(){
		for(Element elm : elements) elm.render();
	}

	@Override
	public void update0(){
		update();
		if(elements == null) return;
		for(Element elm : elements){
			if(elm instanceof Frame && !elm.visible) continue;
			elm.update0();
		}
	}

	@Override
	public void update(){
		if(bar_timer != null && Time.getDate() > bar_timer){
			bar_timer = null;
			bar_text = null;
		}
		info_fps.text("f/s: " + FMT.timer.getFPS());
		info_position.text("pos: " + FMT.CAM.pos.x + ", " + FMT.CAM.pos.y + ", " + FMT.CAM.pos.z);
		info_rotation.text("h/v: " + FMT.CAM.hor + ", " + FMT.CAM.ver);
		info_selected.text(genSelText());
		info_field.text(genFieldText());
		statusbar.text((bar_text == null ? "" : bar_text));
	}

	private Object genSelText(){
		StringBuilder str = new StringBuilder("sel: " + Selector.TYPE._short);
		if(FMT.MODEL.sel_pivot != null){
			str.append(" / r: ").append(FMT.MODEL.sel_pivot.id);
		}
		if(FMT.MODEL.selected_groups().size() > 0){
			if(str.length() > 0) str.append(" / ");
			if(FMT.MODEL.selected_groups().size() == 1) str.append("g: ").append(FMT.MODEL.selected_groups().get(0).id);
			else str.append("g: (").append(FMT.MODEL.selected_groups().size()).append(")");
		}
		if(FMT.MODEL.selected().size() > 0){
			if(str.length() > 0) str.append(" / ");
			if(FMT.MODEL.selected().size() == 1) str.append("p: ").append(FMT.MODEL.selected().get(0).name());
			else str.append("p: (").append(FMT.MODEL.selected().size()).append(")");
		}
		if(FMT.MODEL.getSelectedVerts().size() > 0){
			if(str.length() > 0) str.append(" / ");
			if(FMT.MODEL.getSelectedVerts().size() == 1) str.append("v: ").append(FMT.MODEL.getSelectedVerts().get(0).key().type());
			else str.append("v: (").append(FMT.MODEL.getSelectedVerts().size()).append(")");
		}
		return str.toString();
	}

	private Object genFieldText(){
		StringBuilder str = new StringBuilder();
		if(SELECTED instanceof Field field){
			if(field.polyval() != null) str.append(field.polyval());
		}
		if(SELECTED instanceof BoolElm bool){
			if(bool.polyval() != null) str.append(bool.polyval());
		}
		if(HOVERED instanceof Field field){
			if(field.polyval() != null){
				if(str.length() > 0) str.append(" | ");
				str.append("h: ").append(field.polyval());
			}
		}
		if(HOVERED instanceof BoolElm bool){
			if(bool.polyval() != null){
				if(str.length() > 0) str.append(" | ");
				str.append("h: ").append(bool.polyval());
			}
		}
		return str.toString();
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
		add(DIALOG, title);
		return DIALOG;
	}

	@Override
	public void remElm(Element elm){
		super.remElm(elm);
		if(elm instanceof Frame){
			FRAMES.remove(elm);
			resortFrames();
		}
	}

	@Override
	public void add(Element elm, Object... args){
		if(elm instanceof Frame frm){
			FRAMES.add(frm);
			resortFrames();
			super.add(0, frm, args);
		}
		else super.add(elm, args);
	}

	private void resortFrames(){
		for(int i = 0; i < FRAMES.size(); i++){
			FRAMES.get(i).zoff = i * 20 + 200;
			FRAMES.get(i).rRecompile();
		}
	}

	public void setFrameOnTop(Frame frame){
		int idx = FRAMES.indexOf(frame);
		if(idx < 0) return;
		Collections.swap(FRAMES, idx, FRAMES.size() - 1);
		elements.remove(frame);
		elements.add(0, frame);
		resortFrames();
	}

}
