package net.fexcraft.app.fmt.ui.editorold;

import static net.fexcraft.app.fmt.utils.StyleSheet.BLACK;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.lib.common.math.RGB;

public class TextureEditor extends Editor {
	
	private Container palette, brushes;
	//
	public static RGB CURRENTCOLOR = new RGB(RGB.WHITE);
	private static final int rows = 9, colls = 32;
	private static RGB[] pallete = new RGB[rows * rows];
	private static RGB[] hopall = new RGB[36];
	public static boolean BUCKETMODE;
	private static RGB buttonhover;
	private static PaintMode PMODE;
	//
	private static Button button0, button1, button2, button3, button4;

	public TextureEditor(){
		super("texture_editor", "editor"); this.setVisible(false);
		this.elements.add((palette = new Container(this, "attributes", width - 4, 28, 4, 0, null)).setText(translate("editor.texture.palette.title", "Palette / Color"), false));
		this.elements.add((brushes = new Container(this, "shape", width - 4, 28, 4, 0, null)).setText(translate("editor.texture.brushes.title", "Brushes / Tools"), false));
		//
		int passed = 0;
		{//palette
			palette.getElements().add(new Button(palette, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.texture.palette.color_input", "R-G-B / HEX Color Input"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
			palette.getElements().add(new TextField(palette, "texture_rgb" + i, "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updateRGB(null, j); }
					@Override public boolean processScrollWheel(int wheel){ return updateRGB(wheel > 0, j); }
				}.setAsNumberfield(0, 255, true, true));
			}
			palette.getElements().add(new TextField(palette, "texture_hex", "editor:field", 300, 4, passed += 30){
				@Override
				public void updateTextField(){
					try{ CURRENTCOLOR = new RGB(this.getTextValue()); updateFields(); }
					catch(Exception e){ e.printStackTrace(); }
				}
			}.setText("null", true));
			palette.getElements().add(new Button(palette, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.texture.palette.large", "Large Color Palette"), false));
			palette.getElements().add(new LargePallette(palette, 4, passed += 24));
			palette.getElements().add(new Button(palette, "text2", "editor:title", 290, 20, 4, passed += 298, BLACK).setBackgroundless(true)
				.setText(translate("editor.texture.palette.horizontal", "Horizontal Color Palette"), false));
			palette.getElements().add(new HorizontalPallette(palette, 4, passed += 24));
			palette.getElements().add(new Button(palette, "text3_current_color", "editor:title", 300, 28, 4, passed += 44, (buttonhover = new RGB(CURRENTCOLOR)).packed){
				@Override
				public void renderSelf(int rw, int rh){
					buttonhover.glColorApply(); this.renderSelfQuad(); RGB.glColorReset();
					super.renderSelf(rw, rh);
				}
			}.setText(translate("editor.texture.palette.current_color", "[ Current Color ]"), true).setBackgroundless(true));
			//
			updateFields(); palette.setExpanded(false); passed = 0;
		}
		{//brushes
			translate("editor.texture.brushes.tool_on", "ON"); String off = translate("editor.texture.brushes.tool_off", "OFF");
			button0 = new Button(brushes, "button0", "texture_editor:button", 290, 28, 4, passed += 32, buttonhover.packed){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					toggleBucketMode(PaintMode.FACE); return true;
				}
			};
			brushes.getElements().add(button0.setText(format("editor.texture.brushes.face_paintbucket", "(Face) Paint Bucket [%s]", off), true));
			button1 = new Button(brushes, "button1", "texture_editor:button", 290, 28, 4, passed += 32, buttonhover.packed){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					toggleBucketMode(PaintMode.POLYGON); return true;
				}
			};
			brushes.getElements().add(button1.setText(format("editor.texture.brushes.polygon_paintbucket", "(Polygon) Paint Bucket [%s]", off), true));
			button2 = new Button(brushes, "button2", "texture_editor:button", 290, 28, 4, passed += 32, buttonhover.packed){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					toggleBucketMode(PaintMode.GROUP); return true;
				}
			};
			brushes.getElements().add(button2.setText(format("editor.texture.brushes.group_paintbucket", "(Group) Paint Bucket [%s]", off), true));
			button3 = new Button(brushes, "button3", "texture_editor:button", 290, 28, 4, passed += 32, buttonhover.packed){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					toggleBucketMode(PaintMode.PIXEL); return true;
				}
			}.setIcon("icons/pencil", 32);
			brushes.getElements().add(button3.setText(format("editor.texture.brushes.pixel_pencil", "(Pixel) Paint Pencil [%s]", off), true));
			button4 = new Button(brushes, "button4", "texture_editor:button", 290, 28, 4, passed += 32, buttonhover.packed){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					toggleBucketMode(PaintMode.COLORPICKER); return true;
				}
			};
			brushes.getElements().add(button4.setText(format("editor.texture.brushes.color_picker", "Color Picker [%s]", off), true));
			//
			brushes.setExpanded(false); passed = 0;
		}
		this.containers = new Container[]{ palette, brushes }; this.repos();
	}
	
	protected boolean updateRGB(Boolean apply, int j){
		TextField field = TextField.getFieldById("texture_rgb" + j);
		if(apply != null) field.applyChange(field.tryChange(apply, 8));
		if(CURRENTCOLOR == null) CURRENTCOLOR = new RGB(RGB.WHITE);
		byte[] arr = CURRENTCOLOR.toByteArray();
		byte colorr = (byte)(field.getIntegerValue() - 128);
		switch(j){
			case 0: CURRENTCOLOR = new RGB(colorr, arr[1], arr[2]); break;
			case 1: CURRENTCOLOR = new RGB(arr[0], colorr, arr[2]); break;
			case 2: CURRENTCOLOR = new RGB(arr[0], arr[1], colorr); break;
		} this.updateFields(); return true;
	}
	
	public void updateFields(){
		byte[] arr = CURRENTCOLOR.toByteArray();
		TextField.getFieldById("texture_rgb0").applyChange(arr[0] + 128);
		TextField.getFieldById("texture_rgb1").applyChange(arr[1] + 128);
		TextField.getFieldById("texture_rgb2").applyChange(arr[2] + 128);
		TextField.getFieldById("texture_hex").setText(Integer.toHexString(CURRENTCOLOR.packed), true);
		//
		for(int x = 0; x < rows; x++){
			for(int z = 0; z < rows; z++){
				int y = x * rows + z;
				float e = (1f / (rows * rows)) * y, f = (1f / rows) * z, h = (255 / rows) * x;
				int r = (int)Math.abs((e * (arr[0] + 128)) + ((1 - f) * h));
				int g = (int)Math.abs((e * (arr[1] + 128)) + ((1 - f) * h));
				int l = (int)Math.abs((e * (arr[2] + 128)) + ((1 - f) * h));
				pallete[x + (z * rows)] = new RGB(r, g, l);
			}
		}
		//
		buttonhover.packed = CURRENTCOLOR.packed;
	}

	public static void toggleBucketMode(PaintMode mode){
		if(mode == null){ BUCKETMODE = false; } else{ BUCKETMODE = PMODE == mode ? !BUCKETMODE : true; PMODE = mode; } //if(FMTB.get() == null) return;
		String on = translate("editor.texture.brushes.tool_on", "ON"), off = translate("editor.texture.brushes.tool_off", "OFF");
		button0.setText(format("editor.texture.brushes.face_paintbucket", "(Face) Paint Bucket [%s]", BUCKETMODE && PMODE == PaintMode.FACE ? on : off), true);
		button1.setText(format("editor.texture.brushes.polygon_paintbucket", "(Polygon) Paint Bucket [%s]", BUCKETMODE && PMODE == PaintMode.POLYGON ? on : off), true);
		button2.setText(format("editor.texture.brushes.group_paintbucket", "(Group) Paint Bucket [%s]", BUCKETMODE && PMODE == PaintMode.GROUP ? on : off), true);
		button3.setText(format("editor.texture.brushes.pixel_pencil", "(Pixel) Paint Pencil [%s]", BUCKETMODE && PMODE == PaintMode.PIXEL ? on : off), true);
		button4.setText(format("editor.texture.brushes.color_picker", "Color Picker [%s]", BUCKETMODE && PMODE == PaintMode.COLORPICKER ? on : off), true);
	}
	
	public static class LargePallette extends Element {

		public LargePallette(Element root, int x, int y){
			super(root, "large_color_palette", "large_color_palette");
			this.setSize(294, 294).setPosition(x, y);
		}

		@Override
		public void renderSelf(int rw, int rh){
			super.renderQuad(x, y, width, height, "blank");
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < rows; j++){
					pallete[i + (j * rows)].glColorApply();
					super.renderQuad(x + 3 + (i * colls), y + 3 + (j * colls), colls, colls, "blank");
					RGB.glColorReset();
				}
			}
		}

		@Override
		public boolean processButtonClick(int x, int y, boolean left){
			int xx = (x - this.x - 3) / colls, yy = (y - this.y - 3) / colls; int zz = xx + (yy * rows);
			if(zz >= 0 && zz < pallete.length){
				CURRENTCOLOR = pallete[zz]; ((TextureEditor)root.getRoot()).updateFields();
			} return true;
		}
		
	}
	
	public static class HorizontalPallette extends Element {

		public HorizontalPallette(Element parent, int x, int y){
			super(parent, "horizontal_color_palette", "horizontal_color_palette");
			this.setSize(294, 40).setPosition(x, y);
		}

		@Override
		public void renderSelf(int rw, int rh){
			super.renderQuad(x, y, width, height, "blank");
			if(hopall[0] == null){
				for(int i = 0; i < hopall.length; i ++){
					float c = i * (1f / hopall.length);
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
					hopall[i] = new RGB(r, g, b);
				}
			}
			for(int i = 0; i < hopall.length; i++){
				hopall[i].glColorApply(); super.renderQuad(x + 3 + (i * 8), y, 8, 40, "blank"); RGB.glColorReset();
			}
		}

		@Override
		public boolean processButtonClick(int x, int y, boolean left){
			int xx = (x - this.x - 3) / 8;
			if(xx >= 0 && xx < hopall.length){
				CURRENTCOLOR = hopall[xx]; ((TextureEditor)root.getRoot()).updateFields();
			} return true;
		}
		
	}
	
	public static enum PaintMode {
		PIXEL, FACE, POLYGON, GROUP, COLORPICKER;
	}

	public static boolean pixelMode(){
		return PMODE == null ? false : PMODE == PaintMode.PIXEL || PMODE == PaintMode.COLORPICKER;
	}

	public static boolean faceMode(){
		return PMODE == null ? false : PMODE == PaintMode.FACE;
	}

	public static boolean polygonMode(){
		return PMODE == null ? false : PMODE == PaintMode.POLYGON;
	}

	public static boolean groupMode(){
		return PMODE == null ? false : PMODE == PaintMode.GROUP;
	}
	
	public static boolean colorPicker(){
		return PMODE == null ? false : PMODE == PaintMode.COLORPICKER;
	}
	
	public static PaintMode paintMode(){ return PMODE; }

	public static void reset(){
		toggleBucketMode(null); PMODE = null; BUCKETMODE = false;
	}

}
