package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.awt.Desktop;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

import org.joml.Vector4f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.KeyEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.WindowSizeEvent;
import org.liquidengine.legui.image.BufferedImage;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.listener.FocusEventListener;
import org.liquidengine.legui.listener.KeyEventListener;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Background;
import org.liquidengine.legui.style.Style.DisplayType;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.ImageHelper;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.utils.TextureUpdate;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.wrappers.*;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class UserInterfaceUtils {

	public static final Runnable NOTHING = () -> {};
	public static final MouseClickEventListener NOT_AVAILABLE_YET = event -> {
        Dialog dialog = new Dialog(translate("error.dialog_title"), 300, 100);
        Label label = new Label(translate("error.feature_not_available_yet"), 10, 10, 200, 20);
        Button okbutton = new Button("ok", 10, 50, 50, 20);
        okbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> { if(CLICK == e.getAction()) dialog.close(); });
        dialog.getContainer().add(label); dialog.getContainer().add(okbutton); dialog.show(event.getFrame());
	};
	public static final MouseClickEventListener NOT_REIMPLEMENTED_YET = event -> {
        Dialog dialog = new Dialog(translate("error.dialog_title"), 300, 100);
        Label label = new Label(translate("error.feature_not_reimplemented_yet"), 10, 10, 200, 20);
        Button okbutton = new Button("ok", 10, 50, 50, 20);
        okbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> { if(CLICK == e.getAction()) dialog.close(); });
        dialog.getContainer().add(label); dialog.getContainer().add(okbutton); dialog.show(event.getFrame());
	};
	public static final Runnable SELECT_TEXTURE = () -> {
		FileSelector.select(translate("toolbar.textures.select.dialog"), "./", FileSelector.TYPE_PNG, false, file -> {
			String name = file.getPath(); TextureManager.loadTextureFromFile(name, file); FMTB.MODEL.setTexture(name);
			//
			/*Texture tex = TextureManager.getTexture(name, true); if(tex == null) return;
			if(tex.getWidth() > FMTB.MODEL.textureX) FMTB.MODEL.textureX = tex.getWidth();
			if(tex.getHeight() > FMTB.MODEL.textureY) FMTB.MODEL.textureY = tex.getHeight();*/
		});
	
	};

	@SuppressWarnings("restriction")
	public static void addToolbarButtons(Frame frame){
		Panel toolbar = new Panel(0, 0, FMTB.WIDTH, 30); frame.getContainer().add(toolbar);
		toolbar.getListenerMap().addListener(WindowSizeEvent.class, event -> toolbar.setSize(event.getWidth(), 30));
		frame.getContainer().add(new Icon(0, "./resources/textures/icons/toolbar/info.png", NOT_AVAILABLE_YET));
		frame.getContainer().add(new Icon(1, "./resources/textures/icons/toolbar/new.png", () -> SaveLoad.openNewModel()));
		frame.getContainer().add(new Icon(2, "./resources/textures/icons/toolbar/open.png", () -> SaveLoad.openModel()));
		frame.getContainer().add(new Icon(3, "./resources/textures/icons/toolbar/save.png", () -> SaveLoad.saveModel(false, false)));
		frame.getContainer().add(new Icon(4, "./resources/textures/icons/toolbar/profile.png", NOT_AVAILABLE_YET));
		frame.getContainer().add(new Icon(5, "./resources/textures/icons/toolbar/settings.png", () -> SettingsBox.openFMTSettings()));
		frame.getContainer().add(new MenuEntry(0, translate("toolbar.file"),
			new MenuButton("toolbar.file.new_model", () -> SaveLoad.openNewModel()),
			new MenuButton("toolbar.file.open", () -> SaveLoad.openModel()),
			new MenuButton("toolbar.file.save", () -> SaveLoad.saveModel(false, false)),
			new MenuButton("toolbar.file.save_as", () -> SaveLoad.saveModel(true, false)),
			new MenuButton("toolbar.file.import", () -> PorterManager.handleImport()),
			new MenuButton("toolbar.file.export", () -> PorterManager.handleExport()),
			new MenuButton("toolbar.file.settings", () -> SettingsBox.openFMTSettings()),
			new MenuButton("toolbar.file.exit",  () -> SaveLoad.checkIfShouldSave(true, false))
		));
		frame.getContainer().add(new MenuEntry(1, translate("toolbar.utils"),
			new MenuButton("toolbar.utils.copy_selected", () -> FMTB.MODEL.copyAndSelect()),
			new MenuButton("toolbar.utils.undo", NOT_AVAILABLE_YET),
			new MenuButton("toolbar.utils.redo", NOT_AVAILABLE_YET),
			new MenuButton("toolbar.utils.flip.left_right", () -> FMTB.MODEL.flipShapeboxes(0)),
			new MenuButton("toolbar.utils.flip.up_down", () -> FMTB.MODEL.flipShapeboxes(1)),
			new MenuButton("toolbar.utils.flip.front_back", () -> FMTB.MODEL.flipShapeboxes(2)),
			new MenuButton("toolbar.utils.reset_camera", () -> { FMTB.ggr.pos = new Vec3f(0, 4, 4); FMTB.ggr.rotation = new Vec3f(45, 0, 0); }),
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
			new MenuButton("toolbar.utils.controls", NOT_REIMPLEMENTED_YET)
		));
		frame.getContainer().add(new MenuEntry(2, translate("toolbar.editor"),
			new MenuButton("toolbar.editor.hide_all", () -> Editors.hideAll()),
			new MenuButton("toolbar.editor.general", () -> Editors.show("general")),
			new MenuButton("toolbar.editor.model_group", () -> Editors.show("modelgroup")),
			new MenuButton("toolbar.editor.texture", () -> Editors.show("texture"))
		));
		frame.getContainer().add(new MenuEntry(3, translate("toolbar.shapelist"),
			new MenuButton("toolbar.shapelist.show", () -> Trees.toggle("polygon")),
			new MenuButton("toolbar.shapelist.add_box", () -> FMTB.MODEL.add(new BoxWrapper(FMTB.MODEL), null, true)),
			new MenuButton("toolbar.shapelist.add_shapebox", () -> FMTB.MODEL.add(new ShapeboxWrapper(FMTB.MODEL), null, true)),
			new MenuButton("toolbar.shapelist.add_texrect_b", () -> FMTB.MODEL.add(new TexrectWrapperB(FMTB.MODEL), null, true)),
			new MenuButton("toolbar.shapelist.add_texrect_a", () -> FMTB.MODEL.add(new TexrectWrapperA(FMTB.MODEL), null, true)),
			new MenuButton("toolbar.shapelist.add_cylinder", () -> FMTB.MODEL.add(new CylinderWrapper(FMTB.MODEL), null, true)),
			new MenuButton("toolbar.shapelist.add_quad", () -> FMTB.MODEL.add(new QuadWrapper(FMTB.MODEL), null, true)),
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
			new MenuButton("toolbar.shapelist.add_voxel", () -> FMTB.MODEL.add(new VoxelWrapper(FMTB.MODEL, 16, true), "voxels", true))
		));
		frame.getContainer().add(new MenuEntry(4, translate("toolbar.textures"),
			new MenuButton("toolbar.textures.select", SELECT_TEXTURE),
			new MenuButton("toolbar.textures.edit", () -> {
				if(FMTB.MODEL.texture == null) return;
				Texture texture = TextureManager.getTexture(FMTB.MODEL.texture, true);
				if(texture == null) return;
				try{
					if(System.getProperty("os.name").toLowerCase().contains("windows")) {
						String cmd = "rundll32 url.dll,FileProtocolHandler " + texture.getFile().getCanonicalPath();
						Runtime.getRuntime().exec(cmd);
					}
					else{ Desktop.getDesktop().edit(texture.getFile()); }
				} catch(Exception e){ e.printStackTrace(); }
			}),
			new MenuButton("toolbar.textures.texture_map", NOT_REIMPLEMENTED_YET),
			new MenuButton("toolbar.textures.remove", () -> {
				if(FMTB.MODEL.texture != null && TextureManager.getTexture(FMTB.MODEL.texture, true) != null){
					FMTB.MODEL.setTexture(null); TextureManager.removeTexture(FMTB.MODEL.texture);
				}
			}),
			new MenuButton("toolbar.textures.texpos_reset", () -> {
				FMTB.MODEL.getGroups().forEach(list -> list.forEach(turbo -> {
					turbo.textureX = 0; turbo.textureY = 0; turbo.recompile();
				}));
				DialogBox.showOK(null, null, null, "toolbar.textures.texpos_reset.done");
			}),
			new MenuButton("toolbar.textures.auto_position", () -> TextureUpdate.tryAutoPos(null)),
			new MenuButton("toolbar.textures.generate", () -> {
				String texname = "temp/" + FMTB.MODEL.name;
				FMTB.MODEL.setTexture(texname);
            	TextureManager.newBlankTexture(texname, null);
            	Texture tex = TextureManager.getTexture(texname, true);
            	FMTB.MODEL.textureScale = 1; FMTB.MODEL.updateFields();
            	FMTB.MODEL.getGroups().forEach(elm -> elm.forEach(poly -> poly.burnToTexture(tex.getImage(), null)));
            	TextureManager.saveTexture(texname); tex.reload(); FMTB.MODEL.recompile();
			})
		));
		frame.getContainer().add(new MenuEntry(5, translate("toolbar.helpers"),
			new MenuButton("toolbar.helpers.view", () -> Trees.show("helper")),
			new MenuButton("toolbar.helpers.load_fmtb", () -> {
				FileSelector.select(translate("toolbar.helpers.load_fmtb.dialog"), "./saves", FileSelector.TYPE_FMTB, false, file -> HelperCollector.loadFMTB(file));
			}),
			new MenuButton("toolbar.helpers.load_frame", () -> {
				FileSelector.select(translate("toolbar.helpers.load_frame.dialog"), "./imports", FileSelector.TYPE_IMG, false, file -> HelperCollector.loadFrame(file));
			}),
			new MenuButton("toolbar.helpers.load_imported", () -> {
				FileSelector.select(translate("toolbar.helpers.load_fmtb.dialog"), "./imports", false, (file, porter, settings) -> HelperCollector.load(file, porter, settings));
			}),
			new MenuButton("toolbar.helpers.unload_clear", () -> HelperCollector.LOADED.clear())
		));
		frame.getContainer().add(new MenuEntry(6, translate("toolbar.mod_tools"),
			new MenuButton("toolbar.mod_tools.fvtm_programs", () -> Trees.show("fvtm"))
		));
		frame.getContainer().add(new MenuEntry(7, translate("toolbar.exit")));
	}
	
	public static class Icon extends ImageView {
		
		public Icon(int index, String adress, MouseClickEventListener listener){
			super(new BufferedImage(adress)); this.setPosition(1 + (index * 31), 1); setSize(28, 28);
			this.getListenerMap().addListener(MouseClickEvent.class, listener);
			this.getStyle().setBorderRadius(0);
		}
		
		public Icon(int index, String adress, Runnable run){
			this(index, adress, (MouseClickEventListener)event -> { if(event.getAction() == CLICK){ run.run(); } });
		}
		
	}
	
	public static class MenuEntry extends Panel {
		
		private MenuButton[] buttons;
		private boolean extended;
		public final int index;
		public static int size = 135;
		
		public MenuEntry(int index, String title, MenuButton... buttons){
			super(187 + (index * (size + 2)), 1, size, 28);
			this.getStyle().setBorderRadius(0f);
			Label tatle = new Label(title, 4, 0, 50, 28);
			this.add(tatle); tatle.getStyle().setFontSize(28f);
			Background background = new Background();
			if(!Settings.darktheme()){
				background.setColor(new Vector4f(0.9f, 0.9f, 0.9f, 1));
			}
			else {
				background.setColor(new Vector4f(0.2f, 0.2f, 0.2f, 1));
			}
			this.getStyle().setBackground(background);
	        this.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener)lis -> { if(!lis.isEntered()) this.checkClose(); });
			//
			this.buttons = buttons; this.index = index;
			if(buttons == null || buttons.length == 0){//assumably this is the exit button
				this.buttons = new MenuButton[0];
				tatle.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> { if(event.getAction() == CLICK) SaveLoad.checkIfShouldSave(true, false); });
				this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> { if(event.getAction() == CLICK) SaveLoad.checkIfShouldSave(true, false); });
				return;
			}
			for(int i = 0; i < buttons.length; i++){
				this.add(buttons[i]); buttons[i].hide();
				buttons[i].setEntry(this); buttons[i].setPosition(1, 28 + (i * 26));
				buttons[i].getStyle().setFontSize(20f); buttons[i].setSize(size - 2, 24);
				buttons[i].getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
			}
			tatle.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> { if(event.getAction() == CLICK) toggle(null); });
			this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> { if(event.getAction() == CLICK) toggle(null); });
		}
		
		public void toggle(Boolean bool){
			bool = bool == null ? extended : !bool;
			if(bool){
				for(MenuButton button : buttons){ button.hide(); }
				this.setSize(size, 28); extended = false;
			}
			else{
				this.setSize(size, 26 + (buttons.length * 26));
				for(MenuButton button : buttons){ button.show(); }
				extended = true;
			}
			this.setFocused(false);
		}
		
		public void checkClose(){
			if(this.isHovered()) return; for(MenuButton button : buttons) if(button.isHovered()) return; this.toggle(false);
		}
		
	}
	
	public static class MenuButton extends Button {
		
		private MenuEntry entry;
		
		public MenuButton(String string, Runnable run){
			super(translate(string)); this.getStyle().setBorderRadius(0f);
	        this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
	            if(event.getAction() == CLICK){ run.run(); entry.toggle(false); } else return;
	        });
	        this.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener)lis -> entry.checkClose());
		}
		
		public MenuButton(String string, MouseClickEventListener listener){
			super(translate(string)); this.getStyle().setBorderRadius(0f);
	        this.getListenerMap().addListener(MouseClickEvent.class, listener);
	        this.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener)lis -> entry.checkClose());
		}
		
		public MenuButton(String string){
			this(string, NOTHING);
		}
		
		public MenuButton setEntry(MenuEntry entry){
			this.entry = entry; return this;
		}

		public void toggle(){
			if(isVisible()) hide(); else show();
		}
		
		public void hide(){
			this.getStyle().setDisplay(DisplayType.NONE);
		}
		
		public void show(){
			this.getStyle().setDisplay(DisplayType.MANUAL);
		}
		
	}
	
	public static class TextField extends TextInput {

		public TextField(String string, int x, int y, int w, int h){
			super(string, x, y, w, h); setupHoverCheck(this);
		}

		@SuppressWarnings("unchecked")
		public TextField(Setting setting, int x, int y, int w, int h) {
			this(setting.toString(), x, y, w, h); setupHoverCheck(this);
			this.addTextInputContentChangeEventListener(event -> {
				setting.validateAndApply(validateString(event));
			});
		}
		
	}
	
	public static class NumberField extends TextInput implements Field {

		public NumberField(int x, int y, int w, int h){
			super("0", x, y, w, h); getStyle().setFontSize(20f); setupHoverCheck(this);
		}
		
		public NumberField(Setting setting, int x, int y, int w, int h){
			super(setting.toString(), x, y, w, h); getStyle().setFontSize(20f); setupHoverCheck(this);
			getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
				if(!listener.isFocused()){ setting.validateAndApply(getTextState().getText()); }
			});
			getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
				if(listener.getKey() == GLFW.GLFW_KEY_ENTER){ setting.validateAndApply(getTextState().getText()); }
			});
		}

		private String fieldid;
		private boolean floatfield;
		private float min, max;
		private Float value = null;
		private Runnable update;
		
		@SuppressWarnings("unchecked")
		public NumberField setup(String id, float min, float max, boolean flaot){
			floatfield = flaot; this.min = min; this.max = max; fieldid = id;
			addTextInputContentChangeEventListener(event -> {
				UserInterfaceUtils.validateNumber(event); value = null;
			});
			getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
				if(!listener.isFocused()) FMTB.MODEL.updateValue(this, id);
			});
			getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
				if(listener.getKey() == GLFW.GLFW_KEY_ENTER) FMTB.MODEL.updateValue(this, id);
			});
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public NumberField setup(float min, float max, boolean flaot, Runnable update){
			floatfield = flaot; this.min = min; this.max = max; this.update = update;
			addTextInputContentChangeEventListener(event -> {
				UserInterfaceUtils.validateNumber(event); value = null;
			});
			getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
				if(!listener.isFocused()) update.run();
			});
			getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
				if(listener.getKey() == GLFW.GLFW_KEY_ENTER) update.run();
			});
			return this;
		}
		
		@Override
		public float getValue(){
			if(value != null) return value;
			float newval = 0; String text = this.getTextState().getText();
			try{
				newval = floatfield ? nf.parse(text).floatValue() : nf.parse(text).intValue();
				//newval = floatfield ? Float.parseFloat(text) : Integer.parseInt(text);
			} catch(Exception e){ e.printStackTrace(); }
			if(newval > max) newval = max; else if(newval < min) newval = min;
			if(!(newval + "").equals(text)) apply(newval);
			return value = newval;
		}

		@Override
		public float tryAdd(float flat, boolean positive, float rate){
			flat += positive ? rate : -rate; if(flat > max) flat = max; if(flat < min) flat = min; return floatfield ? flat : (int)flat;
		}

		@Override
		public void apply(float val){
			getTextState().setText((value = val) + ""); setCaretPosition(getTextState().getText().length());
		}

		@Override
		public void onScroll(double yoffset){
			apply(tryAdd(getValue(), yoffset > 0, FMTB.MODEL.rate)); //Print.console(value);
			if(fieldid != null) FMTB.MODEL.updateValue(this, fieldid, yoffset > 0); if(update != null) update.run();
		}

		@Override
		public String id(){
			return fieldid;
		}
		
		@Override
		public Runnable update(){
			return update;
		}
		
	}
	
	public static class BoolButton extends Button implements Field {
		
		private String fieldid;
		
		public BoolButton(String id, int x, int y, int w, int h){
			super("false", x, y, w, h); this.fieldid = id; this.getStyle().setBorderRadius(0f); setupHoverCheck(this);
	        this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
	            if(event.getAction() == CLICK){ toggle(); } else return;
	        });
		}
		
		public BoolButton(Setting setting, int x, int y, int w, int h){
			super(setting.getBooleanValue() + "", x, y, w, h); this.getStyle().setBorderRadius(0f); setupHoverCheck(this);
	        this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
	            if(event.getAction() == CLICK){
	    			setting.toggle(); getTextState().setText(setting.getBooleanValue() + "");
	            } else return;
	        });
		}

		private void toggle(){
			boolean val = Boolean.parseBoolean(getTextState().getText());
			getTextState().setText(!val + ""); FMTB.MODEL.updateValue(this, fieldid);
		}

		@Override
		public float getValue(){
			return Boolean.parseBoolean(getTextState().getText()) ? 1 : 0;
		}

		@Override
		public float tryAdd(float value, boolean positive, float rate){
			return positive ? 1 : 0;
		}

		@Override
		public void apply(float f){
			getTextState().setText((f > .5) + "");
		}

		@Override
		public void onScroll(double yoffset){
			apply(tryAdd(getValue(), yoffset > 0, FMTB.MODEL.rate));
			FMTB.MODEL.updateValue(this, fieldid, true);
		}

		@Override
		public String id(){
			return fieldid;
		}
		
	}
	
	public static class ColorField extends TextInput implements Field {

		private String fieldid;
		private Integer value = null;

		@SuppressWarnings("unchecked")
		public ColorField(Component root, String field, int x, int y, int w, int h){
			super("0xffffff", x, y, root == null ? w : w - 40, h); fieldid = field;setupHoverCheck(this);
			addTextInputContentChangeEventListener(event -> {
				UserInterfaceUtils.validateColorString(event); value = null;
			});
			getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
				if(!listener.isFocused()){
					FMTB.MODEL.updateValue(this, fieldid);
				}
			});
			getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
				if(listener.getKey() == GLFW.GLFW_KEY_ENTER){
					FMTB.MODEL.updateValue(this, fieldid);
				}
			});
			if(root != null){
				Button button = new Button("CP", x + w - 35, y, 30, h);
				button.getListenerMap().addListener(MouseClickEvent.class, event -> {
					if(event.getAction() == CLICK){
	                    try(MemoryStack stack = MemoryStack.stackPush()) {
	                        ByteBuffer color = stack.malloc(3);
	                        String result = TinyFileDialogs.tinyfd_colorChooser("Choose A Color", "#" + Integer.toHexString((int)getValue()), null, color);
							if(result == null) return; this.getTextState().setText(result); value = null; FMTB.MODEL.updateValue(this, fieldid);
	                    }
					}
				}); root.add(button);
			}
		}
		
		public ColorField(Component root, Setting setting, int x, int y, int w, int h){
			super(setting.toString(), x, y, root == null ? w : w - 40, h); getStyle().setFontSize(20f); setupHoverCheck(this);
			getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
				if(!listener.isFocused()){ ((RGB)setting.getValue()).packed = (int)getValue(); }
			});
			getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
				if(listener.getKey() == GLFW.GLFW_KEY_ENTER){ ((RGB)setting.getValue()).packed = (int)getValue(); }
			});
			if(root != null){
				Button button = new Button("CP", x + w - 35, y, 30, h);
				button.getListenerMap().addListener(MouseClickEvent.class, event -> {
					if(event.getAction() == CLICK){
	                    try(MemoryStack stack = MemoryStack.stackPush()) {
	                        ByteBuffer color = stack.malloc(3);
	                        String result = TinyFileDialogs.tinyfd_colorChooser("Choose A Color", "#" + Integer.toHexString((int)getValue()), null, color);
							if(result == null) return; this.getTextState().setText(result); value = null; ((RGB)setting.getValue()).packed = (int)getValue();
	                    }
					}
				}); root.add(button);
			}
		}
		
		public ColorField(Component root, Consumer<Integer> update, int x, int y, int w, int h){
			super("#ffffff", x, y, root == null ? w : w - 40, h); getStyle().setFontSize(20f); setupHoverCheck(this);
			getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
				if(!listener.isFocused()){ update.accept((int)getValue()); }
			});
			getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
				if(listener.getKey() == GLFW.GLFW_KEY_ENTER){ update.accept((int)getValue()); }
			});
			if(root != null){
				Button button = new Button("CP", x + w - 35, y, 30, h);
				button.getListenerMap().addListener(MouseClickEvent.class, event -> {
					if(event.getAction() == CLICK){
	                    try(MemoryStack stack = MemoryStack.stackPush()) {
	                        ByteBuffer color = stack.malloc(3);
	                        String result = TinyFileDialogs.tinyfd_colorChooser("Choose A Color", "#" + Integer.toHexString((int)getValue()), null, color);
							if(result == null) return; this.getTextState().setText(result); value = null; update.accept((int)getValue());
	                    }
					}
				}); root.add(button);
			}
		}
		
		@Override
		public float getValue(){
			if(value != null) return value; float newval = 0;
			String text = this.getTextState().getText().replace("#", "").replace("0x", "");
			try{ newval = Integer.parseInt(text, 16); } catch(Exception e){ e.printStackTrace(); }
			apply(newval); return value = (int)newval;
		}

		@Override
		public float tryAdd(float flat, boolean positive, float rate){
			flat += positive ? rate : -rate; return (int)flat;
		}

		@Override
		public void apply(float val){
			getTextState().setText("#" + Integer.toHexString(value = (int)val));
			setCaretPosition(getTextState().getText().length());
		}

		@Override
		public void onScroll(double yoffset){
			apply(tryAdd(getValue(), yoffset > 0, FMTB.MODEL.rate));
			FMTB.MODEL.updateValue(this, fieldid, true);
		}

		@Override
		public String id(){
			return fieldid;
		}
		
	}
	
	public static interface Field {

		public float getValue();

		public float tryAdd(float value, boolean positive, float rate);

		public void apply(float f);

		public void onScroll(double yoffset);

		public String id();

		public default Runnable update(){ return null; }
		
	}

	private static void setupHoverCheck(Component component){
		component.getListenerMap().addListener(CursorEnterEvent.class, listener -> {
			if(listener.isEntered()) FMTB.context.setFocusedGui(component);
		});
		component.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == CLICK && FMTB.context.getFocusedGui() == component && !component.isFocused())component.setFocused(true);
		});
	}
	
	public static String translate(String str){
		return Translator.translate(str, "no.lang");
	}
	
	public static String format(String str, Object... objs){
		return Translator.format(str, "no.lang.%s", objs);
	}

	public static String validateString(TextInputContentChangeEvent<TextField> event){
		String newtext = event.getNewValue().replaceAll("[^A-Za-z0-9,\\.\\-_ ]", "");
		//Print.console(newtext + " / " + event.getNewValue());
		if(!newtext.equals(event.getNewValue())){
			event.getTargetComponent().getTextState().setText(newtext);
			event.getTargetComponent().setCaretPosition(newtext.length());
		} return newtext;
	}

	public static String validateColorString(TextInputContentChangeEvent<ColorField> event){
		String newtext = event.getNewValue().replaceAll("[^A-Fa-f0-9#x]", "");
		//Print.console(newtext + " / " + event.getNewValue());
		if(!newtext.equals(event.getNewValue())){
			event.getTargetComponent().getTextState().setText(newtext);
			event.getTargetComponent().setCaretPosition(newtext.length());
		} return newtext;
	}
	
	public static final NumberFormat nf = NumberFormat.getInstance(Locale.US);

	public static void validateNumber(TextInputContentChangeEvent<NumberField> event){
		String newtext = event.getNewValue().replaceAll("[^0-9,\\.\\-]", "");
		if(newtext.indexOf("-") > 0) newtext.replace("-", ""); if(newtext.length() == 0) newtext = "0";
		//Print.console(newtext + " / " + event.getNewValue());
		if(!newtext.equals(event.getNewValue())){
			((NumberField)event.getTargetComponent()).getTextState().setText(newtext);
			((NumberField)event.getTargetComponent()).setCaretPosition(newtext.length());
		}
	}

}
