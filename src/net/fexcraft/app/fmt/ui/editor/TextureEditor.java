package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.ui.OldElement;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

public class TextureEditor extends Editor {
	
	public static RGB CURRENTCOLOR = new RGB(RGB.WHITE);
	private static final int rows = 9, colls = 32;
	private static RGB[] pallete = new RGB[rows * rows];
	private static RGB[] hopall = new RGB[36];
	public static boolean BUCKETMODE;
	private static RGB buttonhover;
	private static PaintMode PMODE;

	public TextureEditor(){
		super("texture_editor");
		TextureManager.loadTexture("ui/pbwhite");
		final RGB rgb = new RGB(127, 127, 255);
		//
		for(int i = 0; i < 3; i++){
			final int j = i;
			this.elements.put("rgb" + i + "-", new Button(this, "rgb" + i + "-", 12, 26, 4 + (98 * i), 30, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updateRGB(false, j); }
			}.setText(" < ", true).setTexture("ui/background").setLevel(-1));
			this.elements.put("rgb" + i, new TextField(this, "rgb" + i, 70, 16 + (98 * i), 30){
				@Override public void updateNumberField(){ updateRGB(null, j); }
				@Override protected boolean processScrollWheel(int wheel){ return updateRGB(wheel > 0, j); }
			}.setAsNumberfield(0, 255, true).setLevel(-1));
			this.elements.put("rgb" + i + "+", new Button(this, "rgb" + i + "+", 12, 26, 86 + (98 * i), 30, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updateRGB(true, j); }
			}.setText(" > ", true).setTexture("ui/background").setLevel(-1));
		}
		//
		this.elements.put("large_color_palette", new LargePallette(this, 4, 80));
		this.elements.put("horizontal_color_palette", new HorizontalPallette(this, 4, 410));
		//
		this.elements.put("button0", new Button(this, "button0", 294, 28, 4, 460, buttonhover = new RGB(CURRENTCOLOR)){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				toggleBucketMode(PaintMode.FACE); return true;
			}
		}.setText("(Face) Paint Bucket [OFF]", true).setTexture("ui/pbwhite"));
		this.elements.put("button1", new Button(this, "button1", 294, 28, 4, 490, buttonhover){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				toggleBucketMode(PaintMode.POLYGON); return true;
			}
		}.setText("(Polygon) Paint Bucket [OFF]", true).setTexture("ui/pbwhite"));
		this.elements.put("button2", new Button(this, "button2", 294, 28, 4, 520, buttonhover){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				toggleBucketMode(PaintMode.GROUP); return true;
			}
		}.setText("(Group) Paint Bucket [OFF]", true).setTexture("ui/pbwhite"));
		this.elements.put("button3", new Button(this, "button3", 294, 28, 4, 560, buttonhover){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				toggleBucketMode(PaintMode.PIXEL); return true;
			}
		}.setText("(Pixel) Paint Pencil [OFF]", true).setTexture("ui/pbwhite"));
		this.elements.put("button4", new Button(this, "button4", 294, 28, 4, 600, buttonhover){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				toggleBucketMode(PaintMode.COLORPICKER); return true;
			}
		}.setText("Color Picker [OFF]", true).setTexture("ui/pbwhite"));
		//
		this.updateFields();
	}

	public static void toggleBucketMode(PaintMode mode){
		if(mode == null){ BUCKETMODE = false; } else{ BUCKETMODE = PMODE == mode ? !BUCKETMODE : true; PMODE = mode; } //if(FMTB.get() == null) return;
		Editor.get("texture_editor").getButton("button0").setText("(Face) Paint Bucket [" + (BUCKETMODE && PMODE == PaintMode.FACE ? "ON" : "OFF") + "]", true);
		Editor.get("texture_editor").getButton("button1").setText("(Polygon) Paint Bucket [" + (BUCKETMODE && PMODE == PaintMode.POLYGON ? "ON" : "OFF") + "]", true);
		Editor.get("texture_editor").getButton("button2").setText("(Group) Paint Bucket [" + (BUCKETMODE && PMODE == PaintMode.GROUP ? "ON" : "OFF") + "]", true);
		Editor.get("texture_editor").getButton("button3").setText("(Pixel) Paint Pencil [" + (BUCKETMODE && PMODE == PaintMode.PIXEL ? "ON" : "OFF") + "]", true);
		Editor.get("texture_editor").getButton("button4").setText("Color Picker [" + (BUCKETMODE && PMODE == PaintMode.COLORPICKER ? "ON" : "OFF") + "]", true);
	}
	
	public void updateFields(){
		byte[] arr = CURRENTCOLOR.toByteArray();
		this.getField("rgb0").applyChange(arr[0] + 128);
		this.getField("rgb1").applyChange(arr[1] + 128);
		this.getField("rgb2").applyChange(arr[2] + 128);
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
	
	protected boolean updateRGB(Boolean apply, int j){
		TextField field = (TextField)getElement("rgb" + j);
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
	
	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh); /*TextureManager.unbind();
		font.drawString(4,  40, "Manual RGB Input", Color.black);
		font.drawString(4,  90, "Large Palette", Color.black);
		font.drawString(4, 418, "Horizontal Palette", Color.black);
		RGB.glColorReset();*///TODO
	}
	
	public static class LargePallette extends OldElement {

		public LargePallette(OldElement parent, int x, int y){
			super(parent, "large_color_palette"); this.height = width = 294;
			this.x = parent.x + x; this.y = parent.y + y; z = -1;
		}

		@Override
		public void renderSelf(int rw, int rh){
			super.renderQuad(x, y, width, height, "white");
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < rows; j++){
					pallete[i + (j * rows)].glColorApply();
					super.renderQuad(x + 3 + (i * colls), y + 3 + (j * colls), colls, colls, "white");
					RGB.glColorReset();
				}
			}
		}

		@Override
		protected boolean processButtonClick(int x, int y, boolean left){
			int xx = (x - this.x - 3) / colls, yy = (y - this.y - 3) / colls; int zz = xx + (yy * rows);
			if(zz >= 0 && zz < pallete.length){
				CURRENTCOLOR = pallete[zz]; ((TextureEditor)parent).updateFields();
			} return true;
		}
		
	}
	
	public static class HorizontalPallette extends OldElement {

		public HorizontalPallette(OldElement parent, int x, int y){
			super(parent, "horizontal_color_palette"); this.height = 40; this.width = 294;
			this.x = parent.x + x; this.y = parent.y + y; z = -1;
		}

		@Override
		public void renderSelf(int rw, int rh){
			super.renderQuad(x, y, width, height, "white");
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
				hopall[i].glColorApply(); super.renderQuad(x + 3 + (i * 8), y, 8, 40, "white"); RGB.glColorReset();
			}
		}

		@Override
		protected boolean processButtonClick(int x, int y, boolean left){
			int xx = (x - this.x - 3) / 8;
			if(xx >= 0 && xx < hopall.length){
				CURRENTCOLOR = hopall[xx]; ((TextureEditor)parent).updateFields();
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

	@Override
	protected String[] getExpectedQuickButtons(){ return null; }

}
