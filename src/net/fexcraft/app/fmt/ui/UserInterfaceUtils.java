package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.Tooltip;
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.WindowSizeEvent;
import org.liquidengine.legui.image.BufferedImage;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.ui.MenuButton.MenuSubButton;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.ui.field.ColorField;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.FontUtils;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.HideUtils;
import net.fexcraft.app.fmt.utils.ImageHelper;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.utils.texture.TextureUpdate;
import net.fexcraft.app.fmt.wrappers.BBWrapper;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.CylinderWrapper;
import net.fexcraft.app.fmt.wrappers.MarkerWrapper;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.app.fmt.wrappers.VoxelWrapper;

public class UserInterfaceUtils {

	//public static final Runnable NOTHING = () -> {};
	public static final MouseClickEventListener NOT_AVAILABLE_YET = event -> {
        Dialog dialog = new Dialog(Translator.translate("error.dialog_title"), 300, 100);
        Label label = new Label(Translator.translate("error.feature_not_available_yet"), 10, 10, 200, 20);
        Button okbutton = new Button("ok", 10, 50, 50, 20);
        okbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> { if(CLICK == e.getAction()) dialog.close(); });
        dialog.getContainer().add(label); dialog.getContainer().add(okbutton); dialog.show(event.getFrame());
	};
	public static final MouseClickEventListener NOT_REIMPLEMENTED_YET = event -> {
        Dialog dialog = new Dialog(Translator.translate("error.dialog_title"), 300, 100);
        Label label = new Label(Translator.translate("error.feature_not_reimplemented_yet"), 10, 10, 200, 20);
        Button okbutton = new Button("ok", 10, 50, 50, 20);
        okbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> { if(CLICK == e.getAction()) dialog.close(); });
        dialog.getContainer().add(label); dialog.getContainer().add(okbutton); dialog.show(event.getFrame());
	};

	@SuppressWarnings("restriction")
	public static void addToolbarButtons(Frame frame){
		Panel toolbar = new Panel(0, 0, FMTB.WIDTH, 30); frame.getContainer().add(toolbar);
		toolbar.getListenerMap().addListener(WindowSizeEvent.class, event -> toolbar.setSize(event.getWidth(), 30));
		frame.getContainer().add(new Icon(0, "./resources/textures/icons/toolbar/info.png", NOT_AVAILABLE_YET));
		frame.getContainer().add(new Icon(1, "./resources/textures/icons/toolbar/new.png", () -> SaveLoad.openNewModel()));
		frame.getContainer().add(new Icon(2, "./resources/textures/icons/toolbar/open.png", () -> SaveLoad.openModel()));
		frame.getContainer().add(new Icon(3, "./resources/textures/icons/toolbar/save.png", () -> SaveLoad.saveModel(false, false)));
		frame.getContainer().add(new Icon(4, "./resources/textures/icons/toolbar/profile.png", () -> ProfileBox.open()));
		frame.getContainer().add(new Icon(5, "./resources/textures/icons/toolbar/settings.png", () -> SettingsBox.openFMTSettings()));
		frame.getContainer().add(new MenuEntry(0, Translator.translate("toolbar.file"),
			new MenuButton("toolbar.file.new_model", () -> SaveLoad.openNewModel()),
			new MenuButton("toolbar.file.open", () -> SaveLoad.openModel()),
			new MenuButton("toolbar.file.save", () -> SaveLoad.saveModel(false, false)),
			new MenuButton("toolbar.file.save_as", () -> SaveLoad.saveModel(true, false)),
			new MenuButton("toolbar.file.import", () -> PorterManager.handleImport()),
			new MenuButton("toolbar.file.export", () -> PorterManager.handleExport()),
			new MenuButton("toolbar.file.settings", () -> SettingsBox.openFMTSettings()),
			new MenuButton("toolbar.file.donate", () -> FMTB.openLink("https://fexcraft.net/donate")),
			new MenuButton("toolbar.file.exit",  () -> SaveLoad.checkIfShouldSave(true, false))
		));
		frame.getContainer().add(new MenuEntry(1, Translator.translate("toolbar.utils"),
			new MenuButton("toolbar.utils.copy_selected", () -> FMTB.MODEL.copyAndSelect()),
			new MenuButton("toolbar.utils.clipboard", null,
				new MenuSubButton("toolbar.utils.clipboard.copy", () -> FMTB.MODEL.copyToClipboard()),
				new MenuSubButton("toolbar.utils.clipboard.paste", () -> FMTB.MODEL.pasteFromClipboard())
			),
			new MenuButton("toolbar.utils.undo", NOT_AVAILABLE_YET),
			new MenuButton("toolbar.utils.redo", NOT_AVAILABLE_YET),
			new MenuButton("toolbar.utils.flip", null,
				new MenuSubButton("toolbar.utils.flip.left_right", () -> FMTB.MODEL.flipShapeboxes(null, 0)),
				new MenuSubButton("toolbar.utils.flip.up_down", () -> FMTB.MODEL.flipShapeboxes(null, 1)),
				new MenuSubButton("toolbar.utils.flip.front_back", () -> FMTB.MODEL.flipShapeboxes(null, 2)),
				new MenuSubButton("toolbar.utils.flip.pos_x", () -> FMTB.MODEL.flipBoxPosition(null, 0)),
				new MenuSubButton("toolbar.utils.flip.pos_y", () -> FMTB.MODEL.flipBoxPosition(null, 1)),
				new MenuSubButton("toolbar.utils.flip.pos_z", () -> FMTB.MODEL.flipBoxPosition(null, 2)),
				new MenuSubButton("toolbar.utils.flip.pos_x_front_back", () -> { FMTB.MODEL.flipShapeboxes(null, 2); FMTB.MODEL.flipBoxPosition(null, 0); }),
				new MenuSubButton("toolbar.utils.flip.pos_y_up_down", () -> { FMTB.MODEL.flipShapeboxes(null, 1); FMTB.MODEL.flipBoxPosition(null, 1); }),
				new MenuSubButton("toolbar.utils.flip.pos_z_left_right", () -> { FMTB.MODEL.flipShapeboxes(null, 0); FMTB.MODEL.flipBoxPosition(null, 2); }),
				new MenuSubButton("toolbar.utils.rectify", () -> FMTB.MODEL.rectify()),
				new MenuSubButton("toolbar.utils.mirror_sel", () -> FMTB.MODEL.mirrorLRSelected())
			),
			new MenuButton("toolbar.utils.hide", null,
				new MenuSubButton("toolbar.utils.hide.equal", () -> HideUtils.hideEqual()),
				new MenuSubButton("toolbar.utils.hide.covered", () -> HideUtils.hideCovered()),
				new MenuSubButton("toolbar.utils.hide.reset", () -> HideUtils.reset())
			),
			new MenuButton("toolbar.utils.reset_camera", () -> { FMTB.ggr.reset(); }),
			new MenuButton("toolbar.utils.create_gif", () -> ImageHelper.createGif(false)),
			new MenuButton("toolbar.utils.screenshot", () -> ImageHelper.takeScreenshot(true)),
			new MenuButton("toolbar.utils.calc_size", (MouseClickEventListener)event -> {
				long l = 0, ll = 0;
				for(TurboList list : FMTB.MODEL.getGroups()){
					for(PolygonWrapper wrapper : list){
						l += jdk.nashorn.internal.ir.debug.ObjectSizeCalculator.getObjectSize(wrapper.getTurboObject(0));
					}
				}
				ll = jdk.nashorn.internal.ir.debug.ObjectSizeCalculator.getObjectSize(FMTB.MODEL);
                Dialog dialog = new Dialog("Results:", 300, 100);
                Label label = new Label("Size (MC / Editor): " + Settings.byteCountToString(l, true) + " // " + Settings.byteCountToString(ll, true), 10, 10, 200, 20);
                Button okbutton = new Button("ok", 10, 50, 50, 20);
                okbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> { if(CLICK == e.getAction()) dialog.close(); });
                dialog.getContainer().add(label); dialog.getContainer().add(okbutton); dialog.show(event.getFrame());
			}),
			new MenuButton("toolbar.utils.rescale", () -> FMTB.MODEL.rescale()),
			new MenuButton("toolbar.utils.font_util", () -> FontUtils.open()),
			new MenuButton("toolbar.utils.controls", () -> KeyAdjuster.open())
		));
		frame.getContainer().add(new MenuEntry(2, Translator.translate("toolbar.editor"),
			new MenuButton("toolbar.editor.hide_all", () -> Editors.hideAll()),
			new MenuButton("toolbar.editor.general", () -> Editors.show("general")),
			new MenuButton("toolbar.editor.group", () -> Editors.show("group")),
			new MenuButton("toolbar.editor.model", () -> Editors.show("model")),
			new MenuButton("toolbar.editor.uv", () -> Editors.show("uv")),
			new MenuButton("toolbar.editor.texture", () -> Editors.show("texture")),
			new MenuButton("toolbar.editor.preview", () -> Editors.show("preview"))
		));
		frame.getContainer().add(new MenuEntry(3, Translator.translate("toolbar.shapelist"),
			new MenuButton("toolbar.shapelist.show", () -> Trees.toggle("polygon")),
			new MenuButton("toolbar.shapelist.add_box", () -> FMTB.MODEL.add(new BoxWrapper(FMTB.MODEL), null, true)),
			new MenuButton("toolbar.shapelist.add_shapebox", () -> FMTB.MODEL.add(new ShapeboxWrapper(FMTB.MODEL), null, true)),
			//new MenuButton("toolbar.shapelist.add_texrect_b", () -> FMTB.MODEL.add(new TexrectWrapperB(FMTB.MODEL), null, true)),
			//new MenuButton("toolbar.shapelist.add_texrect_a", () -> FMTB.MODEL.add(new TexrectWrapperA(FMTB.MODEL), null, true)),
			new MenuButton("toolbar.shapelist.add_cylinder", () -> FMTB.MODEL.add(new CylinderWrapper(FMTB.MODEL), null, true)),
			new MenuButton("toolbar.shapelist.add_boundingbox", () -> FMTB.MODEL.add(new BBWrapper(FMTB.MODEL), "boundingboxes", true)),
			new MenuButton("toolbar.shapelist.add_group", () -> {
				String string = "group" + FMTB.MODEL.getGroups().size();
				if(FMTB.MODEL.getGroups().contains(string)){
					string = "group0" + FMTB.MODEL.getGroups().size();
					FMTB.MODEL.getGroups().add(new TurboList(string));
				}
				else{
					FMTB.MODEL.getGroups().add(new TurboList(string));
				}
				Trees.show("polygon");
			}),
			new MenuButton("toolbar.shapelist.add_marker", () -> FMTB.MODEL.add(new MarkerWrapper(FMTB.MODEL), "markers", true)),
			new MenuButton("toolbar.shapelist.add_voxel", () -> FMTB.MODEL.add(new VoxelWrapper(FMTB.MODEL, 16, 16, 16, true), "voxels", true))
		));
		frame.getContainer().add(new MenuEntry(4, Translator.translate("toolbar.textures"),
			new MenuButton("toolbar.textures.manage", () -> Trees.show("textures")),
			new MenuButton("toolbar.textures.addnew", () -> TextureManager.addNewGroup(null, true)),
			new MenuButton("toolbar.textures.texpos_reset", () -> TextureUpdate.tryResetPos()),
			new MenuButton("toolbar.textures.texpostype_reset", () -> TextureUpdate.tryResetPosType()),
			new MenuButton("toolbar.textures.auto_position", () -> TextureUpdate.tryAutoPos())
		));
		frame.getContainer().add(new MenuEntry(5, Translator.translate("toolbar.helpers"),
			new MenuButton("toolbar.helpers.view", () -> Trees.show("helper")),
			new MenuButton("toolbar.helpers.load_fmtb", () -> {
				FileSelector.select(Translator.translate("toolbar.helpers.load_fmtb.dialog"), "./saves", FileSelector.TYPE_FMTB, false, file -> HelperCollector.loadFMTB(file));
			}),
			new MenuButton("toolbar.helpers.load_frame", () -> {
				FileSelector.select(Translator.translate("toolbar.helpers.load_frame.dialog"), "./imports", FileSelector.TYPE_IMG, false, file -> HelperCollector.loadFrame(file));
			}),
			new MenuButton("toolbar.helpers.load_imported", () -> {
				FileSelector.select(Translator.translate("toolbar.helpers.load_fmtb.dialog"), "./imports", false, (file, porter, settings) -> HelperCollector.load(file, porter, settings));
			}),
			new MenuButton("toolbar.helpers.unload_clear", () -> HelperCollector.LOADED.clear())
		));
		frame.getContainer().add(new MenuEntry(6, Translator.translate("toolbar.mod_tools"),
			new MenuButton("toolbar.mod_tools.fvtm_programs", () -> Trees.show("fvtm"))
		));
		frame.getContainer().add(new MenuEntry(7, Translator.translate("toolbar.exit")));
	}
	
	public static class Icon extends ImageView {
		
		public Icon(int index, String adress, MouseClickEventListener listener){
			super(new BufferedImage(adress));
			this.setPosition(1 + (index * 31), 1);
			this.setSize(28, 28);
			this.getListenerMap().addListener(MouseClickEvent.class, listener);
			Settings.THEME_CHANGE_LISTENER.add(bool -> {
				this.getStyle().setBorderRadius(0);
			});
		}
		
		public Icon(int index, String adress, Runnable run){
			this(index, adress, (MouseClickEventListener)event -> { if(event.getAction() == CLICK){ run.run(); } });
		}

		public Icon(int x, int y, int index, String string, String tooltip, Runnable run){
			super(new BufferedImage(string));
			this.setPosition(x + (index * 36), 4);
			this.setSize(34, 34);
			this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
				if(event.getAction() == CLICK) run.run();
			});
			Tooltip tool = new Tooltip(Translator.translate(tooltip));
			tool.setSize(200, 20);
			tool.setPosition(0, 34);
			this.setTooltip(tool);
			Settings.THEME_CHANGE_LISTENER.add(bool -> {
				this.getStyle().setBorderRadius(0);
				tool.getStyle().getBackground().setColor(bool ? FMTB.rgba(85, 125, 95, 0.8f) : FMTB.rgba(161, 194, 169, 0.8f));
				tool.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
				tool.getStyle().setFont("roboto-bold");
				getStyle().setBorderRadius(0);
				getStyle().getBorder().setEnabled(false);
			});
		}
		
	}

	public static void setupHoverCheck(Component component){
		component.getListenerMap().addListener(CursorEnterEvent.class, listener -> {
			if(listener.isEntered()) FMTB.context.setFocusedGui(component);
		});
		component.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == CLICK && FMTB.context.getFocusedGui() == component && !component.isFocused()) component.setFocused(true);
		});
	}
	
	public static final String STRING_VALIDATOR_EXTENDED = "[^A-Za-z0-9,\\.\\-_ \\(\\)\\[\\]\\{\\}\\&\\%\\$\\#\\@\\!\\?\\;\\:\\+\\=\\*\\^\\\"\\'\\>\\<\\\\\\/\\~\\`\\|]";
	public static final String STRING_VALIDATOR_BASIC = "[^A-Za-z0-9,\\.\\-_ ]";
	public static final String STRING_VALIDATOR_JAVA = "[^A-Za-z0-9,_]";
	
	public static String validateString(TextInputContentChangeEvent<TextField> event){
		return validateString(event, false);
	}

	public static String validateString(TextInputContentChangeEvent<TextField> event, boolean basic){
		String newtext = event.getNewValue().replaceAll(basic ? STRING_VALIDATOR_BASIC : STRING_VALIDATOR_EXTENDED, "");
		//log(newtext + " / " + event.getNewValue());
		if(!newtext.equals(event.getNewValue())){
			event.getTargetComponent().getTextState().setText(newtext);
			event.getTargetComponent().setCaretPosition(newtext.length());
		} return newtext;
	}

	public static String validateColorString(TextInputContentChangeEvent<ColorField> event){
		String newtext = event.getNewValue().replaceAll("[^A-Fa-f0-9#x]", "");
		//log(newtext + " / " + event.getNewValue());
		if(!newtext.equals(event.getNewValue())){
			event.getTargetComponent().getTextState().setText(newtext);
			event.getTargetComponent().setCaretPosition(newtext.length());
		} return newtext;
	}

	public static void validateNumber(TextInputContentChangeEvent<NumberField> event){
		String newtext = event.getNewValue().replaceAll("[^0-9,\\.\\-]", "");
		if(newtext.indexOf("-") > 0) newtext.replace("-", ""); if(newtext.length() == 0) newtext = "0";
		//log(newtext + " / " + event.getNewValue());
		if(!newtext.equals(event.getNewValue())){
			((NumberField)event.getTargetComponent()).getTextState().setText(newtext);
			((NumberField)event.getTargetComponent()).setCaretPosition(newtext.length());
		}
	}

	public static final void hide(Component com){
		com.getStyle().setDisplay(DisplayType.NONE);
	}

	public static final void show(Component com){
		com.getStyle().setDisplay(DisplayType.MANUAL);
	}

}
