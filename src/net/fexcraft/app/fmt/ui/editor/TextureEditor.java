package net.fexcraft.app.fmt.ui.editor;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.style.border.SimpleLineBorder;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.FunctionButton;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils.Icon;
import net.fexcraft.app.fmt.ui.field.ColorField;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.common.math.RGB;

public class TextureEditor extends EditorBase {
	
	public static RGB CURRENTCOLOR = new RGB(RGB.WHITE);
	public static ColorPanel[] panels = new ColorPanel[18 * 18]; 
	public static ColorField colorfield;
	public static ColorPanel current;
	public static Icon face, polygon, group, pencil, picker;
	public static FunctionButton current_tool;
	//
	private static PaintMode PMODE;
	private static final int rows = 18;
	
	public TextureEditor(){
		super(); int pass = -20;
		EditorWidget palette = new EditorWidget(this, translate("editor.texture.palette"), 0, 0, 0, 0);
		palette.getContainer().add(new Label(translate("editor.texture.palette.inputfield"), 3, pass += 24, 290, 20));
		palette.getContainer().add(colorfield = new ColorField(palette.getContainer(), (newval, button) -> updateColor(new RGB(newval), button), 4, pass += 24, 290, 20));
		palette.getContainer().add(new Label(translate("editor.texture.palette.large"), 3, pass += 24, 290, 20)); pass += 24;
		byte[] arr = CURRENTCOLOR.toByteArray();
		for(int x = 0; x < rows; x++){
			for(int z = 0; z < rows; z++){
				int y = x * rows + z;
				float e = (1f / (rows * rows)) * y, f = (1f / rows) * z, h = (255 / rows) * x;
				int r = (int)Math.abs((e * (arr[0] + 128)) + ((1 - f) * h));
				int g = (int)Math.abs((e * (arr[1] + 128)) + ((1 - f) * h));
				int l = (int)Math.abs((e * (arr[2] + 128)) + ((1 - f) * h));
				palette.getContainer().add(panels[x + (z * rows)] = new ColorPanel(3 + (x * 16), pass + (z * 16), 16, 16, new RGB(r, g, l), false));
			}
		} pass += 268;
		palette.getContainer().add(new Label(translate("editor.texture.palette.horizontal"), 3, pass += 24, 290, 20)); pass += 24;
		for(int i = 0; i < 36; i++){
			float c = i * (1f / 36);
			int r, g, b;
			//
	        if(c >= 0 && c <= (1/6.f)){
	            r = 255;
	            g = (int)(1530 * c);
	            b = 0;
	        }
	        else if( c > (1/6.f) && c <= (1/3.f) ){
	            r = (int)(255 - (1530 * (c - 1/6f)));
	            g = 255;
	            b = 0;
	        }
	        else if( c > (1/3.f) && c <= (1/2.f)){
	            r = 0;
	            g = 255;
	            b = (int)(1530 * (c - 1/3f));
	        }
	        else if(c > (1/2f) && c <= (2/3f)) {
	            r = 0;
	            g = (int)(255 - ((c - 0.5f) * 1530));
	            b = 255;
	        }
	        else if( c > (2/3f) && c <= (5/6f) ){
	            r = (int)((c - (2/3f)) * 1530);
	            g = 0;
	            b = 255;
	        }
	        else if(c > (5/6f) && c <= 1 ){
	            r = 255;
	            g = 0;
	            b = (int)(255 - ((c - (5/6f)) * 1530));
	        }
	        else{ r = 127; g = 127; b = 127; }
			RGB result = new RGB(r, g, b);
			palette.getContainer().add(new ColorPanel(3 + (i * 8), pass, 8, 20, result, true));
		}
		palette.getContainer().add(new Label(translate("editor.texture.palette.current"), 3, pass += 24, 290, 20));
		palette.getContainer().add(current = new ColorPanel(3, pass += 24, 290, 20, new RGB(), true));
		palette.setSize(296, pass + 52);
        this.addSub(palette); pass = -20;
        //
		EditorWidget brushes = new EditorWidget(this, translate("editor.texture.brushes"), 0, 0, 0, 0);
		//String off = translate("editor.texture.brushes.tool_off");
		pass += 24;
		int off = 55;
		brushes.getContainer().add(pencil = new Icon(off, pass, 0, "./resources/textures/icons/editors/texture/pixel.png", "editor.texture.brushes.pixel_pencil", () -> toggleBucketMode(PaintMode.PIXEL)));
		brushes.getContainer().add(face = new Icon(off, pass, 1, "./resources/textures/icons/editors/texture/face.png", "editor.texture.brushes.face_bucket", () -> toggleBucketMode(PaintMode.FACE)));
		brushes.getContainer().add(polygon = new Icon(off, pass, 2, "./resources/textures/icons/editors/texture/polygon.png", "editor.texture.brushes.polygon_bucket", () -> toggleBucketMode(PaintMode.POLYGON)));
		brushes.getContainer().add(group = new Icon(off, pass, 3, "./resources/textures/icons/editors/texture/group.png", "editor.texture.brushes.group_bucket", () -> toggleBucketMode(PaintMode.GROUP)));
		brushes.getContainer().add(picker = new Icon(off, pass, 4, "./resources/textures/icons/editors/texture/color_picker.png", "editor.texture.brushes.color_picker", () -> toggleBucketMode(PaintMode.COLORPICKER)));
		brushes.getContainer().add(current_tool = new FunctionButton(translate("editor.texture.brushes.current") + " NONE", 3, pass += 24 + 12, 290, 20, () -> toggleBucketMode(null)));
		brushes.setSize(296, pass + 52);
        this.addSub(brushes); pass = -20;
        //
        reOrderWidgets();
	}

	public static void updateColor(byte[] col, Boolean refresh){
		updateColor(col == null ? RGB.WHITE.copy() : new RGB(col), refresh);
	}

	public static void updateColor(RGB rgb, Boolean refresh){
		if(rgb == null) rgb = RGB.WHITE.copy();
		CURRENTCOLOR = rgb;
		//
		if(refresh == null || !refresh){
			byte[] arr = CURRENTCOLOR.toByteArray();
			for(int x = 0; x < rows; x++){
				for(int z = 0; z < rows; z++){
					int y = x * rows + z;
					float e = (1f / (rows * rows)) * y, f = (1f / rows) * z, h = (255 / rows) * x;
					int r = (int)Math.abs((e * (arr[0] + 128)) + ((1 - f) * h));
					int g = (int)Math.abs((e * (arr[1] + 128)) + ((1 - f) * h));
					int l = (int)Math.abs((e * (arr[2] + 128)) + ((1 - f) * h));
					panels[x + (z * rows)].setColor(new RGB(r, g, l));
				}
			}
			if(ColorPanel.box != null) ColorPanel.box.reset();
		}
		current.setColor(CURRENTCOLOR);
		colorfield.apply(CURRENTCOLOR.packed);
	}
	
	public static class ColorPanel extends Panel {
		
		private static ColorPanel old, box;
		private RGB color;

		public ColorPanel(int x, int y, int w, int h, RGB rgb, boolean hori){
			super(x, y, w, h); setColor(color = rgb);
			this.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() == MouseClickAction.CLICK){
					updateColor(color.copy(), hori ? false : listener.getButton() == MouseButton.MOUSE_BUTTON_LEFT);
					if(hori){
						if(old != null) old.reset();
						this.getStyle().setBorder(new SimpleLineBorder(FMTB.rgba(color.packed), 2));
						this.getStyle().setBorderRadius(4f);
						old = this;
					}
					else{
						if(box != null) box.reset();
						//this.getStyle().setBorder(new SimpleLineBorder(FMTB.rgba(opposite(color)), 1));
						this.getStyle().setBorderRadius(10f);
						box = this;
					}
					this.setFocused(false);
				}
			});
	        Settings.THEME_CHANGE_LISTENER.add(bool -> {
				this.reset();
				setColor(color);
	        });
		}

		private void reset(){
			this.getStyle().setBorder(null);
			this.getStyle().setBorderRadius(0f);
		}

		public void setColor(RGB rgb){
			this.getStyle().getBackground().setColor(FMTB.rgba(color.packed = rgb.packed));
		}
		
	}
	
	public static int opposite(RGB rgb){
		return 0xFFFFFF - rgb.packed;
	}

	public static void toggleBucketMode(PaintMode mode){
		PMODE = mode == null || PMODE == mode ? null : mode;
		current_tool.getTextState().setText(translate("editor.texture.brushes.current") + " " + (PMODE == null ? "none" : PMODE.lang()));
	}

	public static enum PaintMode {
		PIXEL, FACE, POLYGON, GROUP, COLORPICKER;

		public String lang(){
			String lang = "editor.texture.brushes.";
			switch(this){
				case COLORPICKER: lang += "color_picker"; break;
				case FACE: lang += "face_bucket"; break;
				case GROUP: lang += "group_bucket"; break;
				case PIXEL: lang += "pixel_pencil"; break;
				case POLYGON: lang += "polygon_bucket"; break;
				default: lang += "none"; break;
			}
			return translate(lang);
		}
	}

	public static boolean pixelMode(){
		return PMODE != null && PMODE == PaintMode.PIXEL;
	}

	public static boolean faceMode(){
		return PMODE != null && PMODE == PaintMode.FACE;
	}

	public static boolean polygonMode(){
		return PMODE != null && PMODE == PaintMode.POLYGON;
	}

	public static boolean groupMode(){
		return PMODE != null && PMODE == PaintMode.GROUP;
	}
	
	public static boolean colorPicker(){
		return PMODE != null && PMODE == PaintMode.COLORPICKER;
	}
	
	public static PaintMode paintMode(){ return PMODE; }
	
	public static boolean isPaintActive(){
		return PMODE != null;
	}

	public static void reset(){
		toggleBucketMode(null); PMODE = null;
	}
	
}
