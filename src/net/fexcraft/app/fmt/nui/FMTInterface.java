package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.lib.common.math.RGB;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FMTInterface extends Element {

	public static Element toolbar;

	public FMTInterface(){
		super();
		add(toolbar = new Element().pos(0, 0).size(FMT.WIDTH, 35).color(Settings.THEME_BACKGROUND.value));
		toolbar.add(new Element().pos(2, 2).size(32, 32).texture("icons/toolbar/info").hoverable(true));
		toolbar.add(new Element().pos(0, 40).size(100, 200).color(RGB.BLUE).linecolor(new RGB(256, 256, 0)).rounded(true));
		toolbar.add(new Element().pos(200, 40).size(500, 100).color(RGB.GREEN).linecolor(RGB.BLACK).rounded(true));
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
