package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.ProfileDialog;
import net.fexcraft.app.fmt.ui.SettingsDialog;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.app.fmt.utils.SaveHandler;
import net.fexcraft.lib.common.math.RGB;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FMTInterface extends Element {

	public static final int TOOLBAR_HEIGHT = 35;
	public static RGB col_75 = new RGB(0x757575);
	public static RGB col_85 = new RGB(0x858585);
	public static RGB col_cd = new RGB(0xcdcdcd);
	public static Element toolbar;
	public static Element menu_file;
	public static Element menu_recent;
 	//
	public static EditorRoot editor;

	public FMTInterface(){
		super();
		add(toolbar = new Element().pos(0, 0).size(FMT.WIDTH, TOOLBAR_HEIGHT).color(col_cd));
		toolbar.add(new Element().pos(2, 2).size(32, 32)
			.texture("icons/toolbar/info").hoverable(true)
			.onclick(ci -> FMT.openLink("https://fexcraft.net/wiki/app/fmt"))
			.hint("toolbar.icon.info"));
		toolbar.add(new Element().pos(36, 2).size(32, 32)
			.texture("icons/toolbar/settings").hoverable(true)
			.onclick(ci -> SettingsDialog.open())
			.hint("toolbar.icon.settings"));
		toolbar.add(new Element().pos(70, 2).size(32, 32)
			.texture("icons/toolbar/profile").hoverable(true)
			.onclick(ci -> ProfileDialog.open())
			.hint("toolbar.icon.profile"));
		toolbar.add(new Element().pos(104, 2).size(32, 32)
			.texture("icons/toolbar/save").hoverable(true)
			.onclick(ci -> SaveHandler.save(FMT.MODEL, null, null, false, false))
			.hint("toolbar.icon.save"));
		toolbar.add(new Element().pos(138, 2).size(32, 32)
			.texture("icons/toolbar/open").hoverable(true)
			.onclick(ci -> SaveHandler.openDialog(null))
			.hint("toolbar.icon.open"));
		toolbar.add(new Element().pos(172, 2).size(32, 32)
			.texture("icons/toolbar/new").hoverable(true)
			.onclick(ci -> SaveHandler.newDialog())
			.hint("toolbar.icon.new"));
		toolbar.add(menu_file = new Menu().translate("toolbar.file").pos(208, 3).size(200, 30).color(col_75));
		menu_file.add(new Element().translate("toolbar.file.new").color(col_85).onclick(ci -> SaveHandler.newDialog()));
		menu_file.add(new Element().translate("toolbar.file.open").color(col_85).onclick(ci -> SaveHandler.openDialog(null)));
		menu_file.add(menu_recent = new Menu().onhover(menu -> {
			for(int i = 0; i < 10; i++) menu.elements.get(i).text(Settings.RECENT.get(i).getName().replace(".fmtb", ""));
		}).translate("toolbar.file.recent").color(col_85));
		for(int i = 0; i < 10; i++){
			int j = i;
			menu_recent.add(new Element().translate("file.recent.none").color(col_85).onclick(ci -> Settings.openRecent(j)));
		}
		menu_file.add(new Element().translate("toolbar.file.save").color(col_85).onclick(ci -> SaveHandler.saveDialogByState(null)));
		menu_file.add(new Element().translate("toolbar.file.save_as").color(col_85).onclick(ci -> SaveHandler.saveAsDialog(null)));
		menu_file.add(new Element().translate("toolbar.file.import").color(col_85).onclick(ci -> ImportManager._import()));
		menu_file.add(new Element().translate("toolbar.file.export").color(col_85).onclick(ci -> ExportManager.export()));
		menu_file.add(new Element().translate("toolbar.file.donate").color(col_85).onclick(ci -> FMT.openLink("https://fexcraft.net/donate")));
		menu_file.add(new Element().translate("toolbar.file.exit").color(col_85).onclick(ci -> FMT.close(0)));
		//toolbar.add(new Element().pos(10, 40).size(100, 200).color(RGB.BLUE).linecolor(new RGB(256, 256, 0)).rounded(true));
		//toolbar.add(new Element().pos(200, 40).size(500, 100).color(RGB.GREEN).linecolor(RGB.BLACK).rounded(true));
		add((editor = new EditorRoot()).root(this).color(col_cd));
	}

	@Override
	public void render(Picker.PickTask picker){
		for(Element elm : elements) elm.render(picker);
	}

	@Override
	public void update(){
		
		super.update();
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

	@Override
	public void onResize(){
		for(Element elm : elements) elm.onResize();
	}

}
