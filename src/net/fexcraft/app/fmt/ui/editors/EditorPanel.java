package net.fexcraft.app.fmt.ui.editors;

import com.spinyowl.legui.component.Component;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.UIUtils;
import net.fexcraft.app.fmt.ui.panels.FlipToolsPanel;
import net.fexcraft.app.fmt.ui.panels.MarkerPanel;
import net.fexcraft.app.fmt.ui.panels.MultiplierPanel;
import net.fexcraft.app.fmt.ui.panels.QuickAddPanel;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorPanel extends Component {

	public static ArrayList<EditorPanel> PANELS = new ArrayList<>();
	protected static int I_SIZE = 30;
	//
	protected String lang_prefix;
	protected UpdateCompound updcom = new UpdateCompound();
	protected boolean expanded;
	protected int ex_x;
	protected int ex_y;

	public EditorPanel(String id, String icon, String tooltip){
		lang_prefix = "editor.component." + id;
		setSize(I_SIZE, I_SIZE);
		Settings.applyComponentTheme(this);
		add(new Icon(0, I_SIZE, 0, 0, 0, "./resources/textures/icons/" + icon + ".png", () -> expand())
			.addTooltip(translate(tooltip)));
		UpdateHandler.register(updcom);
	}

	public static void load(){
		PANELS.add(new MultiplierPanel());
		PANELS.add(new QuickAddPanel());
		PANELS.add(new FlipToolsPanel());
		PANELS.add(new MarkerPanel());
	}

	private void expand(){
		expand(!expanded);
		//for(EditorPanel panel : PANELS) if(panel != this) panel.expand(false);
	}

	private void expand(boolean bool){
		expanded = bool;
		if(bool){
			setSize(ex_x, ex_y);
		}
		else{
			setSize(I_SIZE, I_SIZE);
		}
	}

	public static void hideAll(){
		for(EditorPanel panel : PANELS) UIUtils.hide(panel);
	}

	public static void showAll(){
		for(EditorPanel panel : PANELS) UIUtils.show(panel);
	}

	public static boolean isOverPanel(double x, double y){
		for(EditorPanel panel : PANELS){
			if(panel.expanded){
				if(x >= panel.getPosition().x && x <= panel.getPosition().x + panel.ex_x
					&& y >= panel.getPosition().y && y <= panel.getPosition().y + panel.ex_y)
					return true;
			}
			else{
				if(x >= panel.getPosition().x && x <= panel.getPosition().x + I_SIZE
					&& y >= panel.getPosition().y && y <= panel.getPosition().y + I_SIZE)
					return true;
			}
		}
		return false;
	}

}
