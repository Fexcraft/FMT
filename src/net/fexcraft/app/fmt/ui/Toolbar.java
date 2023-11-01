package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;

import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Panel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.ui.components.QuickAdd;
import net.fexcraft.app.fmt.utils.FontUtils;
import net.fexcraft.app.fmt.utils.ImageHandler;
import net.fexcraft.app.fmt.utils.SaveHandler;

public class Toolbar extends Panel {
	
	public static final Runnable NOTHING = () -> {};
	private UpdateCompound updcom = new UpdateCompound();

	public Toolbar(){
		super(0, 0, FMT.WIDTH, 30);
		this.setFocusable(false);
		Settings.applyBorderless(this);
		this.add(new Icon(0, "./resources/textures/icons/toolbar/info.png", () -> FMT.openLink("https://fexcraft.net/wiki/app/fmt")).addTooltip("toolbar.icon.info"));
		this.add(new Icon(1, "./resources/textures/icons/toolbar/settings.png", () -> SettingsDialog.open()).addTooltip("toolbar.icon.settings"));
		this.add(new Icon(2, "./resources/textures/icons/toolbar/profile.png", () -> ProfileDialog.open()).addTooltip("toolbar.icon.profile"));
		this.add(new Icon(3, "./resources/textures/icons/toolbar/save.png", () -> SaveHandler.save(FMT.MODEL, null, null, false, false)).addTooltip("toolbar.icon.save"));
		this.add(new Icon(4, "./resources/textures/icons/toolbar/open.png", () -> SaveHandler.openDialog(null)).addTooltip("toolbar.icon.open"));
		this.add(new Icon(5, "./resources/textures/icons/toolbar/new.png", () -> SaveHandler.newDialog()).addTooltip("toolbar.icon.new"));
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
				ArrayList<Component> list = new ArrayList<>();
				list.addAll(layer.getMenuComponents());
				for(int  i = 0; i < list.size(); i++){
					((MenuButton)list.get(i)).label.getTextState().setText(Settings.RECENT.get(i).getName());
				}
			}),
			new MenuButton(3, "file.save", () -> SaveHandler.saveDialogByState(null)),
			new MenuButton(4, "file.save_as", () -> SaveHandler.saveAsDialog(null)),
			new MenuButton(5, "file.import", () -> ImportManager._import()),
			new MenuButton(6, "file.export", () -> ExportManager.export()),
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
			new MenuButton(8, "utils.uv_viewer", () -> UVViewer.addIfAbsent()),
			new MenuButton(9, "utils.rescale", () -> FMT.MODEL.rescale()),
			new MenuButton(10, "utils.font_util", () -> FontUtils.open()),
			new ToolbarMenu(-11, "utils.converters",
					new MenuButton(0, "utils.converter.itemmodeltexjson", () -> ConverterUtils.runIMTJ())
			)
		));
		this.add(new ToolbarMenu(2, "editors",
			new MenuButton(0, "editors.polygon", () -> Editor.POLYGON_EDITOR.toggle()),
			new MenuButton(1, "editors.group", () -> Editor.GROUP_EDITOR.toggle()),
			new MenuButton(2, "editors.model", () -> Editor.MODEL_EDITOR.toggle()),
			new MenuButton(3, "editors.texture", () -> Editor.TEXTURE_EDITOR.toggle()),
			new MenuButton(4, "editors.uv", () -> Editor.UV_EDITOR.toggle()),
			new MenuButton(5, "editors.preview", () -> Editor.PREVIEW_EDITOR.toggle())
		));
		this.add(new ToolbarMenu(3, "trees",
			new MenuButton(0, "trees.polygon", () -> Editor.POLYGON_TREE.toggle()),
			new MenuButton(1, "trees.texture", () -> Editor.TEXTURE_TREE.toggle())
		));
		this.add(new ToolbarMenu(4, "polygons",
			new MenuButton(0, "polygons.add_box", () -> QuickAdd.addBox()),
			new MenuButton(1, "polygons.add_shapebox", () -> QuickAdd.addShapebox()),
			new MenuButton(2, "polygons.add_cylinder", () -> QuickAdd.addCylinder()),
			new MenuButton(3, "polygons.add_marker", () -> QuickAdd.addMarker()),
			new MenuButton(4, "polygons.add_group", () -> QuickAdd.addGroup()),
			new MenuButton(5, "polygons.add_boundingbox", () -> QuickAdd.addScructBox()),
			new MenuButton(6, "polygons.add_rect_curve", () -> QuickAdd.addCurve(false)),
			new MenuButton(7, "polygons.add_cyl_curve", () -> QuickAdd.addCurve(true)),
			new MenuButton(8, "polygons.add_object"),
			new MenuButton(9, "polygons.add_voxel")
		));
		this.add(new ToolbarMenu(5, "helpers"));
		this.add(new ToolbarMenu(6, "exit", () -> FMT.close(0)));
		UpdateHandler.register(updcom);
	}

}
