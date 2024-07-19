package net.fexcraft.app.fmt.ui.workspace;

import com.spinyowl.legui.component.Component;
import net.fexcraft.app.fmt.ui.JsonEditor;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuLayer;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.Logging;
import org.apache.commons.io.FileUtils;
import org.joml.Vector2f;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FileEditMenu {
	
	private static MenuLayer layer;
	private static DirComponent dircom;
	private static File file;
	static {
		ArrayList<Component> components = new ArrayList<>();
		components.add(new MenuButton(0, "fileeditmenu.asjson", () -> {
			new JsonEditor(file);
			layer.hide();
		}));
		components.add(new MenuButton(1, "fileeditmenu.rename", () -> {
			//
			layer.hide();
		}));
		components.add(new MenuButton(2, "fileeditmenu.copy", () -> {
			File nf = new File(file.getParentFile(), file.getName() + "0");
			try{
				FileUtils.copyFile(file, nf);
			}
			catch(IOException e){
				Logging.log(e);
			}
			WorkspaceViewer.viewer.genView();
			layer.hide();
		}));
		components.add(new MenuButton(3, "fileeditmenu.delete", () -> {
			file.delete();
			WorkspaceViewer.viewer.genView();
			layer.hide();
		}));
		layer = new MenuLayer(null, new Vector2f((float)GGR.posx, (float)GGR.posy), components, null){
			@Override
			public boolean timed(){
				return true;
			}
		};
	}

	public static void show(DirComponent com, File fl){
		dircom = dircom;
		file = fl;
		layer.setPosition((float)GGR.posx, (float)GGR.posy);
		layer.show();
	}

}
