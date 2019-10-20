package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.lib.common.math.RGB;

public class TextureEditor extends Editor {
	
	private static ContainerButton palette, brushes;
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
		super("texture_editor");
	}

	@Override
	protected ContainerButton[] setupSubElements(){
		//final RGB rgb = new RGB(127, 127, 255);
		brushes = new ContainerButton(this, id, 300, 28, 4, y, new int[]{ 1, 1, 1, 1, 1 }){
			@Override
			public void addSubElements(){
				button0 = new Button(this, "button0", 290, 28, 0, 0, buttonhover = new RGB(CURRENTCOLOR)){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						toggleBucketMode(PaintMode.FACE); return true;
					}
				};
				this.elements.add(button0.setText("(Face) Paint Bucket [OFF]", true).setTexPosSize("ui/background_white", 0, 0, 32, 32).setRowCol(0, 0));
				button1 = new Button(this, "button1", 290, 28, 0, 0, buttonhover){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						toggleBucketMode(PaintMode.POLYGON); return true;
					}
				};
				this.elements.add(button1.setText("(Polygon) Paint Bucket [OFF]", true).setTexPosSize("ui/background_white", 0, 0, 32, 32).setRowCol(1, 0));
				button2 = new Button(this, "button2", 290, 28, 0, 0, buttonhover){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						toggleBucketMode(PaintMode.GROUP); return true;
					}
				};
				this.elements.add(button2.setText("(Group) Paint Bucket [OFF]", true).setTexPosSize("ui/background_white", 0, 0, 32, 32).setRowCol(2, 0));
				button3 = new Button(this, "button3", 290, 28, 0, 0, buttonhover){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						toggleBucketMode(PaintMode.PIXEL); return true;
					}
				}.setIcon("icons/pencil", 32);
				this.elements.add(button3.setText("(Pixel) Paint Pencil [OFF]", true).setTexPosSize("ui/background_white", 0, 0, 32, 32).setRowCol(3, 0));
				button4 = new Button(this, "button4", 290, 28, 0, 0, buttonhover){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						toggleBucketMode(PaintMode.COLORPICKER); return true;
					}
				};
				this.elements.add(button4.setText("Color Picker [OFF]", true).setTexPosSize("ui/background_white", 0, 0, 32, 32).setRowCol(4, 0));
			}
		};
		brushes.setText("Brushes / Tools", false);
		palette = new ContainerButton(this, id, 300, 28, 4, y, new int[]{ 1, 3, 1, 1, 1, 1, 1, 1 }){
			@Override
			public void addSubElements(){
				this.elements.add(new Button(this, "text0", 290, 20, 0, 0, RGB.WHITE).setText("R-G-B / HEX Color Input", false).setRowCol(0, 0));
				for(int i = 0; i < 3; i++){
					final int j = i;
					this.elements.add(new TextField(this, "texture_rgb" + i, 0, 0, 0){
						@Override public void updateNumberField(){ updateRGB(null, j); }
						@Override protected boolean processScrollWheel(int wheel){ return updateRGB(wheel > 0, j); }
					}.setAsNumberfield(0, 255, true).setRowCol(1, i));
				}
				this.elements.add(new TextField(this, "texture_hex", 0, 0, 0){
					@Override
					public void updateTextField(){
						try{ CURRENTCOLOR = new RGB(this.getTextValue()); updateFields(); }
						catch(Exception e){ e.printStackTrace(); }
					}
				}.setText("null", true).setRowCol(2, 0));
				this.elements.add(new Button(this, "text1", 290, 20, 0, 0, RGB.WHITE).setText("Large Color Palette", false).setRowCol(3, 0));
				this.elements.add(new LargePallette(this, 4, 80).setRowCol(4, 0));
				this.elements.add(new Button(this, "text2", 290, 20, 0, 0, RGB.WHITE).setText("Horizontal Color Palette", false).setRowCol(5, 0));
				this.elements.add(new HorizontalPallette(this, 4, 410).setRowCol(6, 0));
				this.elements.add(new Button(this, "text3_current_color", 290, 28, 0, 0, buttonhover){
					@Override
					public void renderSelf(int rw, int rh){
						buttonhover.glColorApply(); this.renderSelfQuad(); RGB.glColorReset();
						super.renderSelf(rw, rh);
					}
				}.setText("[ Current Color ]", true).setBackgroundless(true).setTexPosSize("ui/background_white", 0, 0, 32, 32).setRowCol(7, 0));
				updateFields();
			}
		};
		palette.setText("Palette / Color", false);
		return new ContainerButton[]{ palette, brushes };
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
		button0.setText("(Face) Paint Bucket [" + (BUCKETMODE && PMODE == PaintMode.FACE ? "ON" : "OFF") + "]", true);
		button1.setText("(Polygon) Paint Bucket [" + (BUCKETMODE && PMODE == PaintMode.POLYGON ? "ON" : "OFF") + "]", true);
		button2.setText("(Group) Paint Bucket [" + (BUCKETMODE && PMODE == PaintMode.GROUP ? "ON" : "OFF") + "]", true);
		button3.setText("(Pixel) Paint Pencil [" + (BUCKETMODE && PMODE == PaintMode.PIXEL ? "ON" : "OFF") + "]", true);
		button4.setText("Color Picker [" + (BUCKETMODE && PMODE == PaintMode.COLORPICKER ? "ON" : "OFF") + "]", true);
	}
	
	public static class LargePallette extends Element {

		public LargePallette(Element root, int x, int y){
			super(root, "large_color_palette"); this.height = width = 294;
			this.x = root.x + x; this.y = root.y + y; z = -1;
		}

		@Override
		public void renderSelf(int rw, int rh){
			super.renderQuad(x, y, width, height, "ui/background_white");
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < rows; j++){
					pallete[i + (j * rows)].glColorApply();
					super.renderQuad(x + 3 + (i * colls), y + 3 + (j * colls), colls, colls, "ui/background_white");
					RGB.glColorReset();
				}
			}
		}

		@Override
		protected boolean processButtonClick(int x, int y, boolean left){
			int xx = (x - this.x - 3) / colls, yy = (y - this.y - 3) / colls; int zz = xx + (yy * rows);
			if(zz >= 0 && zz < pallete.length){
				CURRENTCOLOR = pallete[zz]; ((TextureEditor)root.getRoot()).updateFields();
			} return true;
		}
		
	}
	
	public static class HorizontalPallette extends Element {

		public HorizontalPallette(Element parent, int x, int y){
			super(parent, "horizontal_color_palette"); this.height = 40; this.width = 294;
			this.x = parent.x + x; this.y = parent.y + y; z = -1;
		}

		@Override
		public void renderSelf(int rw, int rh){
			super.renderQuad(x, y, width, height, "ui/background_white");
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
				hopall[i].glColorApply(); super.renderQuad(x + 3 + (i * 8), y, 8, 40, "ui/background_white"); RGB.glColorReset();
			}
		}

		@Override
		protected boolean processButtonClick(int x, int y, boolean left){
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
