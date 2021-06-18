package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Panel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.ui.components.QuickAdd;
import net.fexcraft.app.fmt.utils.FontUtils;
import net.fexcraft.app.fmt.utils.ImageHandler;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.SaveHandler;

public class Toolbar extends Panel {
	
	public static final Runnable NOTHING = () -> {};
	private UpdateHolder holder;

	public Toolbar(){
		super(0, 0, FMT.WIDTH, 30);
		this.setFocusable(false);
		Settings.applyBorderless(this);
		holder = new UpdateHolder();
		this.add(new Icon(0, "./resources/textures/icons/toolbar/info.png", () -> Logging.log("test")));
		this.add(new Icon(1, "./resources/textures/icons/toolbar/settings.png", () -> SettingsDialog.open()));
		this.add(new Icon(2, "./resources/textures/icons/toolbar/profile.png", () -> ProfileDialog.open()));
		this.add(new Icon(3, "./resources/textures/icons/toolbar/save.png", () -> SaveHandler.save(FMT.MODEL, null, null)));
		this.add(new Icon(4, "./resources/textures/icons/toolbar/open.png", () -> SaveHandler.openDialog(null)));
		this.add(new Icon(5, "./resources/textures/icons/toolbar/new.png", () -> SaveHandler.newDialog()));
		this.add(new ToolbarMenu(0, "file",
			new MenuButton(0, "file.new", () -> SaveHandler.newDialog()),
			new MenuButton(1, "file.open", () -> SaveHandler.openDialog(null)),
			new ToolbarMenu(-2, "file.recent",
				new MenuButton(0, "file.recent.none", () -> Settings.openRecent(0)),
				new MenuButton(1, "file.recent.none", () -> Settings.openRecent(1)),
				new MenuButton(2, "file.recent.none", () -> Settings.openRecent(2)),
				new MenuButton(3, "file.recent.none", () -> Settings.openRecent(3)),
				new MenuButton(4, "file.recent.none", () -> Settings.openRecent(4)),
				new MenuButton(5, "file.recent.none", () -> Settings.openRecent(5)),
				new MenuButton(6, "file.recent.none", () -> Settings.openRecent(6)),
				new MenuButton(7, "file.recent.none", () -> Settings.openRecent(7)),
				new MenuButton(8, "file.recent.none", () -> Settings.openRecent(8)),
				new MenuButton(9, "file.recent.none", () -> Settings.openRecent(9))
			).setLayerPreShow(layer -> {
				int[] idx = { 0 };
				layer.getMenuComponents().forEach(com -> {
					((MenuButton)com).label.getTextState().setText(Settings.RECENT.get(idx[0]).getName());
					idx[0]++;
				});
			}),
			new MenuButton(3, "file.save", () -> SaveHandler.saveDialogByState(null)),
			new MenuButton(4, "file.save_as", () -> SaveHandler.saveAsDialog(null)),
			new MenuButton(5, "file.import"),
			new MenuButton(6, "file.export"),
			new MenuButton(7, "file.settings", () -> SettingsDialog.open()),
			new MenuButton(8, "file.donate", () -> FMT.openLink("https://fexcraft.net/donate")),
			new MenuButton(9, "file.exit", () -> FMT.close(0))
		));
		this.add(new ToolbarMenu(1, "utils",
			new MenuButton(0, "utils.copy_selected", () -> FMT.MODEL.copySelected()),
			new ToolbarMenu(-1, "utils.clipboard",
				new MenuButton(0, "utils.clipboard.copy", () -> FMT.MODEL.copyToClipboard(false)),
				new MenuButton(1, "utils.clipboard.paste", () -> FMT.MODEL.pasteFromClipboard()),
				new MenuButton(2, "utils.clipboard.copy_grouped", () -> FMT.MODEL.copyToClipboard(true))
			),
			new MenuButton(2, "utils.undo"),
			new MenuButton(3, "utils.redo"),
			new ToolbarMenu(-4, "utils.flip_tools",
				new MenuButton(0, "utils.flip_tools.left_right", () -> FMT.MODEL.flipShapeboxes(null, 0)),
				new MenuButton(1, "utils.flip_tools.up_down", () -> FMT.MODEL.flipShapeboxes(null, 1)),
				new MenuButton(2, "utils.flip_tools.front_back", () -> FMT.MODEL.flipShapeboxes(null, 2)),
				new MenuButton(3, "utils.flip_tools.pos_x", () -> FMT.MODEL.flipBoxPosition(null, 0)),
				new MenuButton(4, "utils.flip_tools.pos_y", () -> FMT.MODEL.flipBoxPosition(null, 1)),
				new MenuButton(5, "utils.flip_tools.pos_z", () -> FMT.MODEL.flipBoxPosition(null, 2)),
				new MenuButton(6, "utils.flip_tools.pos_x_front_back", () -> {
					FMT.MODEL.flipShapeboxes(null, 2);
					FMT.MODEL.flipBoxPosition(null, 0);
				}),
				new MenuButton(7, "utils.flip_tools.pos_y_up_down", () -> {
					FMT.MODEL.flipShapeboxes(null, 1);
					FMT.MODEL.flipBoxPosition(null, 1);
				}),
				new MenuButton(8, "utils.flip_tools.pos_z_left_right", () -> {
					FMT.MODEL.flipShapeboxes(null, 0);
					FMT.MODEL.flipBoxPosition(null, 2);
				})
			),
			new MenuButton(5, "utils.reset_camera", () -> { FMT.CAM.reset(); }),
			new MenuButton(6, "utils.create_gif", () -> ImageHandler.createGif()),
			new MenuButton(7, "utils.screenshot", () -> ImageHandler.takeScreenshot(false)),
			new MenuButton(8, "utils.calc_size"),
			new MenuButton(9, "utils.rescale", () -> FMT.MODEL.rescale()),
			new MenuButton(10, "utils.font_util", () -> FontUtils.open())
		));
		this.add(new ToolbarMenu(2, "editors",
			new MenuButton(0, "editors.new")
		));
		holder.add(UpdateType.EDITOR_CREATED, wrap -> {
			Editor editor = wrap.get(0);
			ToolbarMenu menu = ToolbarMenu.MENUS.get(editor.tree ? "trees" : "editors");
			MenuButton button = new MenuButton(menu.components.size(), editor.id, editor.name);
			button.addListener(() -> editor.toggle());
			menu.components.add(button);
			menu.layer.regComponent(button);
			menu.layer.refreshSize();
		});
		holder.add(UpdateType.EDITOR_REMOVED, wrap -> {
			Editor editor = wrap.get(0);
			if(editor.tree) return;
			//TODO
		});
		this.add(new ToolbarMenu(3, "trees"));
		this.add(new ToolbarMenu(4, "polygons",
			new MenuButton(0, "polygons.add_box", () -> QuickAdd.addBox()),
			new MenuButton(1, "polygons.add_shapebox", () -> QuickAdd.addShapebox()),
			new MenuButton(2, "polygons.add_cylinder", () -> QuickAdd.addCylinder()),
			new MenuButton(3, "polygons.add_boundingbox"),
			new MenuButton(4, "polygons.add_object"),
			new MenuButton(5, "polygons.add_marker", () -> QuickAdd.addMarker()),
			new MenuButton(6, "polygons.add_group", () -> QuickAdd.addGroup()),
			new MenuButton(7, "polygons.add_voxel"),
			new ToolbarMenu(-8, "polygons.special",
				new MenuButton(0, "polygons.special.fvtm_rail"),
				new MenuButton(1, "polygons.special.curve_line")
			)
		));
		this.add(new ToolbarMenu(5, "texture"));
		this.add(new ToolbarMenu(6, "helpers"));
		this.add(new ToolbarMenu(7, "project",
			new MenuButton(0, "project.open"),
			new MenuButton(1, "project.settings"),
			//new MenuButton(2, "project.import"),
			//new MenuButton(3, "project.export"),
			new MenuButton(2, "project.close")
		));
		this.add(new ToolbarMenu(8, "exit", () -> FMT.close(0)));
		UpdateHandler.registerHolder(holder);
	}

}
