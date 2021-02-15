package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Panel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.utils.Logging;

public class Toolbar extends Panel {
	
	public static final Runnable NOTHING = () -> {};

	public Toolbar(){
		super(0, 0, FMT.WIDTH, 30);
		this.setFocusable(false);
		Settings.THEME_CHANGE_LISTENERS.add(bool -> {
			getStyle().setBorderRadius(0);
			getStyle().setBorder(null);
		});
		this.add(new Icon(0, "./resources/textures/icons/toolbar/info.png", () -> Logging.log("test")));
		this.add(new Icon(1, "./resources/textures/icons/toolbar/settings.png", NOTHING));
		this.add(new Icon(2, "./resources/textures/icons/toolbar/profile.png", NOTHING));
		this.add(new Icon(3, "./resources/textures/icons/toolbar/save.png", NOTHING));
		this.add(new Icon(4, "./resources/textures/icons/toolbar/open.png", NOTHING));
		this.add(new Icon(5, "./resources/textures/icons/toolbar/new.png", NOTHING));
		this.add(new ToolbarMenu(0, "file",
			new MenuButton(0, "file.new"),
			new MenuButton(1, "file.open"),
			new ToolbarMenu(-2, "file.recent",
				new MenuButton(0, "file.recent.none"),
				new MenuButton(1, "file.recent.none"),
				new MenuButton(2, "file.recent.none"),
				new MenuButton(3, "file.recent.none"),
				new MenuButton(4, "file.recent.none"),
				new MenuButton(5, "file.recent.none"),
				new MenuButton(6, "file.recent.none"),
				new MenuButton(7, "file.recent.none"),
				new MenuButton(8, "file.recent.none"),
				new MenuButton(9, "file.recent.none")
			).setLayerPreShow(layer -> {
				//TODO
			}),
			new MenuButton(3, "file.save"),
			new MenuButton(4, "file.save_as"),
			new MenuButton(5, "file.import"),
			new MenuButton(6, "file.export"),
			new MenuButton(7, "file.settings"),
			new MenuButton(8, "file.donate"),
			new MenuButton(9, "file.exit", () -> FMT.close())
		));
		this.add(new ToolbarMenu(1, "editors").setLayerPreShow(layer -> {
			/*while(layer.getChildComponents().size() > Editor.EDITORS.size()) layer.getChildComponents().remove(layer.getChildComponents().size() - 1);
			while(layer.getChildComponents().size() < Editor.EDITORS.size()) layer.getChildComponents().add(new MenuButton(layer.getChildComponents().size()));
			for(int i = 0; i < Editor.EDITORLIST.size(); i++){
				((MenuButton)layer.getChildComponents().get(i)).getLabel().getTextState().setText(Editor.EDITORLIST.get(i).name);
			}*/
		}));
		this.add(new ToolbarMenu(2, "component",
			new MenuButton(0, "component.list"),
			new ToolbarMenu(-1, "component.polygon",
				new MenuButton(0, "component.polygon.quick"),
				new MenuButton(1, "component.polygon.list"),
				new MenuButton(2, "component.polygon.group"),
				new MenuButton(3, "component.polygon.general"),
				new MenuButton(4, "component.polygon.general_box"),
				new MenuButton(5, "component.polygon.general_cyl"),
				new MenuButton(6, "component.polygon.general_obj"),
				new MenuButton(7, "component.polygon.position"),
				new MenuButton(8, "component.polygon.offset"),
				new MenuButton(9, "component.polygon.rotation"),
				new MenuButton(10, "component.polygon.texture"),
				new MenuButton(11, "component.polygon.size_box"),
				new MenuButton(12, "component.polygon.size_cyl"),
				new MenuButton(13, "component.polygon.shapebox"),
				new MenuButton(14, "component.polygon.cylinder"),
				new MenuButton(15, "component.polygon.visibility"),
				new MenuButton(16, "component.polygon.object")
			),
			new ToolbarMenu(-2, "component.group",
				new MenuButton(0, "component.group.general"),
				new MenuButton(1, "component.group.general"),
				new MenuButton(2, "component.group.general"),
				new MenuButton(3, "component.group.general"),
				new MenuButton(4, "component.group.general")
			),
			new ToolbarMenu(-3, "component.model",
				new MenuButton(0, "component.model.general"),
				new MenuButton(1, "component.model.general"),
				new MenuButton(2, "component.model.general"),
				new MenuButton(3, "component.model.general"),
				new MenuButton(4, "component.model.general")
			),
			new ToolbarMenu(-4, "component.texture",
				new MenuButton(0, "component.texture.general"),
				new MenuButton(1, "component.texture.general"),
				new MenuButton(2, "component.texture.general"),
				new MenuButton(3, "component.texture.general"),
				new MenuButton(4, "component.texture.general")
			),
			new ToolbarMenu(-5, "component.helpers",
				new MenuButton(0, "component.helpers.general"),
				new MenuButton(1, "component.helpers.general"),
				new MenuButton(2, "component.helpers.general"),
				new MenuButton(3, "component.helpers.general"),
				new MenuButton(4, "component.helpers.general")
			),
			new ToolbarMenu(-6, "component.project",
				new MenuButton(0, "component.project.general"),
				new MenuButton(1, "component.project.general"),
				new MenuButton(2, "component.project.general"),
				new MenuButton(3, "component.project.general"),
				new MenuButton(4, "component.project.general")
			)
		));
		this.add(new ToolbarMenu(3, "utils"));
		this.add(new ToolbarMenu(4, "polygons"));
		this.add(new ToolbarMenu(5, "texture"));
		this.add(new ToolbarMenu(6, "helpers"));
		this.add(new ToolbarMenu(7, "project",
			new MenuButton(0, "project.open"),
			new MenuButton(1, "project.settings"),
			new MenuButton(2, "project.import"),
			new MenuButton(3, "project.export"),
			new MenuButton(4, "project.close")
		));
		//this.add(new ToolbarMenu(8, "THEME",  () -> { Settings.SELTHEME = !Settings.SELTHEME; Settings.applyTheme(); }));
		this.add(new ToolbarMenu(8, "exit", () -> FMT.close()));
	}

}
