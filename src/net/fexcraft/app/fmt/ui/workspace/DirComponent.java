package net.fexcraft.app.fmt.ui.workspace;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.input.Mouse;
import com.spinyowl.legui.style.Style;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.JsonEditor;
import net.fexcraft.app.fmt.utils.fvtm.FVTMConfigEditor;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DirComponent extends Component {

	public static final int xoff = 10;
	private ArrayList<DirComponent> subcom = new ArrayList<>();
	private boolean expanded = false;
	private ViewerFileType type;
	protected DirComponent root;
	private Label label;
	private Icon icon;
	protected File file;

	public DirComponent(ViewerFileType type, WorkspaceViewer folcom, DirComponent root, File file, int row){
		this.type = type;
		this.root = root;
		this.file = file;
		add(label = new Label(WorkspaceViewer.ROWHEIGHT + 2, 0, 5, WorkspaceViewer.ROWHEIGHT));
		label.getTextState().setText(file.getName());
		Settings.applyBorderless(label);
		Settings.applyBorderless(this);
		getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == MouseClickEvent.MouseClickAction.CLICK){
				if(listener.getButton() == Mouse.MouseButton.MOUSE_BUTTON_LEFT){
					if(type.directory){
						expanded = !expanded;
						folcom.resize();
					}
					else if(type.editable){
						switch(type){
							case FVTM_FILE:{
								new JsonEditor(file);
								break;
							}
							case FVTM_CONFIG:{
								new FVTMConfigEditor(file);
								break;
							}
							case JSON:{
								new JsonEditor(file);
								break;
							}
							default:
								break;
						}
					}
				}
				else if(listener.getButton() == Mouse.MouseButton.MOUSE_BUTTON_RIGHT){
					FileEditMenu.show(this, file);
				}
			}
		});
		updateIcon(type, folcom);
	}

	public void addSub(DirComponent com){
		subcom.add(com);
		this.add(com);
	}

	public int resize(int offset, boolean noff){
		int size = WorkspaceViewer.ROWHEIGHT;
		getPosition().set(noff ? 0 : xoff, noff ? offset : size + offset);
		if(expanded){
			int off = 0;
			for(DirComponent com : subcom){
				int siz = com.resize(off, false);
				size += siz;
				off += siz;
				com.getStyle().setDisplay(Style.DisplayType.MANUAL);
			}
		}
		else{
			for(DirComponent com : subcom){
				com.getStyle().setDisplay(Style.DisplayType.NONE);
			}
		}
		getSize().set(300, size);
		return (int)getSize().y;
	}

	public int fullsize(){
		int size = WorkspaceViewer.ROWHEIGHT;
		for(DirComponent com : subcom){
			size += com.fullsize();
		}
		return size;
	}

	public void updateIcon(ViewerFileType type, WorkspaceViewer folcom){
		this.type = type;
		this.remove(icon);
		this.add(icon = new Icon(0, 32, 0, 0, -1, "./resources/textures/icons/filetree/" + type.filename() + ".png", () -> {
			expanded = !expanded;
			folcom.resize();
		}));
	}

}
