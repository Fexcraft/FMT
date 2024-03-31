package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.ProfileDialog;
import net.fexcraft.app.fmt.ui.SettingsDialog;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.app.fmt.utils.SaveHandler;
import net.fexcraft.lib.common.math.RGB;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FMTInterface extends Element {

	public static RGB lightgray = new RGB(0x757575);
	public static Element toolbar;
	public static Element menu_file;

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
		toolbar.add(new Element().pos(174, 2).size(32, 32)
			.texture("icons/toolbar/new").hoverable(true)
			.onclick(() -> SaveHandler.newDialog())
			.tooltip("toolbar.icon.new"));
		toolbar.add(menu_file = new Menu().pos(208, 3).size(200, 30).hoverable(true).color(lightgray));
		menu_file.add(new Element().hoverable(true).color(RGB.random()));
		menu_file.add(new Element().hoverable(true).color(RGB.random()));
		menu_file.add(new Element().hoverable(true).color(RGB.random()));
		menu_file.add(new Element().hoverable(true).color(RGB.random()));
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
