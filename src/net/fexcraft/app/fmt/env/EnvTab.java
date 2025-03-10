package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.event.MouseClickEvent;
import net.fexcraft.app.fmt.ui.Icon;

import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static com.spinyowl.legui.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EnvTab extends Component {

	public final FileViewEntry entry;
	protected EnvContent content;
	private Label label;
	private Icon icon;

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
		content = null;
	}

	public int updateDisplay(int buf){
		setPosition(buf, 0);
		return (int)getSize().x;
	}

	public EnvContent getContent(){
		return content;
	}

}
