package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.event.CursorEnterEvent;
import com.spinyowl.legui.event.MouseClickEvent;
import net.fexcraft.app.fmt.env.con.JsonContent;
import net.fexcraft.app.fmt.ui.Icon;

import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static com.spinyowl.legui.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static com.spinyowl.legui.style.Style.DisplayType.MANUAL;
import static com.spinyowl.legui.style.Style.DisplayType.NONE;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EnvTab extends Component {

	protected static Icon vit;
	protected static Icon sit;
	public final FileViewEntry entry;
	protected EnvContent content;
	private Label label;
	private Icon icon;
	private Icon save;
	private Icon exit;

	public EnvTab(FileViewEntry ent){
		setSize(230, 30);
		entry = ent;
		add(icon = new Icon(0, 30, 0, 0, 0, entry.getIconLoc(), () -> {}));
		add(label = new Label(entry.file.getName(), 30, 0, 200, 30));
		label.getListenerMap().addListener(MouseClickEvent.class, event -> {
			if(event.getAction() == CLICK && event.getButton() == MOUSE_BUTTON_LEFT){
				entry.root.setContent(content);
			}
		});
		add(exit = new Icon(0, 20, 0, 205, 5, "./resources/textures/icons/configeditor/remove.png", () -> entry.root.remTab(entry)));
		add(save = new Icon(0, 20, 0, 180, 5, "./resources/textures/icons/toolbar/save.png", () -> entry.root.remTab(entry)));
		exit.getStyle().setDisplay(NONE);
		save.getStyle().setDisplay(NONE);
		label.getListenerMap().addListener(CursorEnterEvent.class, e -> {
			if(!e.isEntered()) return;
			if(vit != null) vit.getStyle().setDisplay(NONE);
			(vit = exit).getStyle().setDisplay(MANUAL);
			if(sit != null) sit.getStyle().setDisplay(NONE);
			(sit = save).getStyle().setDisplay(MANUAL);
		});
		content = new JsonContent(this, entry.file);
	}

	public int updateDisplay(int buf){
		setPosition(buf, 0);
		return (int)getSize().x;
	}

	public EnvContent getContent(){
		return content;
	}

}
