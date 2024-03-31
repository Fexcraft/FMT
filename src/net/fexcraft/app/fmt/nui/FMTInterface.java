package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.ProfileDialog;
import net.fexcraft.app.fmt.ui.SettingsDialog;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.app.fmt.utils.SaveHandler;
import net.fexcraft.lib.common.math.RGB;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FMTInterface extends Element {

	private static UpdateHandler.UpdateCompound updcom = new UpdateHandler.UpdateCompound();
	public static RGB g75 = new RGB(0x757575);
	public static RGB g85 = new RGB(0x858585);
	public static Element toolbar;
	public static Element menu_file;
	public static Element menu_recent;

	public FMTInterface(){
		super();
		add(toolbar = new Element().pos(0, 0).size(FMT.WIDTH, 35).color(Settings.THEME_BACKGROUND.value));
		toolbar.add(new Element().pos(2, 2).size(32, 32)
			.texture("icons/toolbar/info").hoverable(true)
			.onclick(() -> FMT.openLink("https://fexcraft.net/wiki/app/fmt"))
			.tooltip("toolbar.icon.info"));
		toolbar.add(new Element().pos(36, 2).size(32, 32)
			.texture("icons/toolbar/settings").hoverable(true)
			.onclick(() -> SettingsDialog.open())
			.tooltip("toolbar.icon.settings"));
		toolbar.add(new Element().pos(70, 2).size(32, 32)
			.texture("icons/toolbar/profile").hoverable(true)
			.onclick(() -> ProfileDialog.open())
			.tooltip("toolbar.icon.profile"));
		toolbar.add(new Element().pos(104, 2).size(32, 32)
			.texture("icons/toolbar/save").hoverable(true)
			.onclick(() -> SaveHandler.save(FMT.MODEL, null, null, false, false))
			.tooltip("toolbar.icon.save"));
		toolbar.add(new Element().pos(138, 2).size(32, 32)
			.texture("icons/toolbar/open").hoverable(true)
			.onclick(() -> SaveHandler.openDialog(null))
			.tooltip("toolbar.icon.open"));
		toolbar.add(new Element().pos(172, 2).size(32, 32)
			.texture("icons/toolbar/new").hoverable(true)
			.onclick(() -> SaveHandler.newDialog())
			.tooltip("toolbar.icon.new"));
		toolbar.add(menu_file = new Menu().translate("toolbar.file").pos(208, 3).size(200, 30).color(g75));
		menu_file.add(new Element().translate("toolbar.file.new").color(g85).onclick(() -> SaveHandler.newDialog()));
		menu_file.add(new Element().translate("toolbar.file.open").color(g85).onclick(() -> SaveHandler.openDialog(null)));
		menu_file.add(menu_recent = new Menu().onhover(menu -> {
			for(int i = 0; i < 10; i++) menu.elements.get(i).text(Settings.RECENT.get(i).getName().replace(".fmtb", ""));
		}).translate("toolbar.file.recent").color(g85));
		for(int i = 0; i < 10; i++){
			int j = i;
			menu_recent.add(new Element().translate("file.recent.none").color(g85).onclick(() -> Settings.openRecent(j)));
		}
		menu_file.add(new Element().translate("toolbar.file.save").color(g85).onclick(() -> SaveHandler.saveDialogByState(null)));
		menu_file.add(new Element().translate("toolbar.file.save_as").color(g85).onclick(() -> SaveHandler.saveAsDialog(null)));
		menu_file.add(new Element().translate("toolbar.file.import").color(g85).onclick(() -> ImportManager._import()));
		menu_file.add(new Element().translate("toolbar.file.export").color(g85).onclick(() -> ExportManager.export()));
		menu_file.add(new Element().translate("toolbar.file.donate").color(g85).onclick(() -> FMT.openLink("https://fexcraft.net/donate")));
		menu_file.add(new Element().translate("toolbar.file.exit").color(g85).onclick(() -> FMT.close(0)));
		//toolbar.add(new Element().pos(10, 40).size(100, 200).color(RGB.BLUE).linecolor(new RGB(256, 256, 0)).rounded(true));
		//toolbar.add(new Element().pos(200, 40).size(500, 100).color(RGB.GREEN).linecolor(RGB.BLACK).rounded(true));
	}

	@Override
	public void render(Picker.PickTask picker){
		for(Element elm : elements) elm.render(picker);
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

}
