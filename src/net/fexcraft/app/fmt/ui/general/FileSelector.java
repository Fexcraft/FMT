package net.fexcraft.app.fmt.ui.general;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.Scrollbar.Scrollable;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.lib.common.math.RGB;

public class FileSelector extends Element implements Dialog {

	private ExImPorter porter;
	private ChooserMode mode;
	private Button title;
	private Files files;

	public FileSelector(){
		super(null, "fileselector", "fileselector"); dialogs.add(this);
		this.setSize(512, 512).setPosition(0, 0).setColor(0xffcdcdcd);
		this.setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true);
		this.setHoverColor(StyleSheet.WHITE, false).setDraggable(true);
		//
		this.elements.add((title = new Button(this, "null", "fileselector:title", 480, 30, 16, 12))
			.setHoverColor(StyleSheet.WHITE, false).setBorder(0xff909090, StyleSheet.WHITE, 1, true, true, true, true));
		this.elements.add(files = new Files(this));
		//
		this.show("No Title.", FileRoot.SAVES, ChooserMode.SAVEFILE_SAVE, null);
	}

	private void show(String title, FileRoot root, ChooserMode mode, ExImPorter porter){
		this.reset(); this.setVisible(true); this.title.setText(title, true);
		this.mode = mode; this.porter = porter; files.current = root.getFile().getAbsoluteFile(); files.refresh0();
	}
	
	@Override
	public Element repos(){
		x = (UserInterface.width - width) / 2 + xrel; y = (UserInterface.height - height) / 2 + yrel;
		clearVertexes(); for(Element elm : elements) elm.repos(); return this;
	}

	@Override
	public boolean visible(){
		return visible;
	}

	@Override
	public void reset(){
		this.setVisible(false); porter = null; mode = null;
	}
	
	public static class Files extends Element implements Scrollable {

		private FileSelector selector;
		private FileButton selected;
		private Scrollbar scrollbar;
		private int fullheight;
		private File current;
		private Button rootf;

		public Files(FileSelector root){
			super(root, "fileselector:files", "fileselector:files"); this.setPosition(6, 50).setSize(488, 380);
			this.setColor(0xff81cc82).setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true);
			this.setHoverColor(StyleSheet.WHITE, false); selector = root;
			//
			this.elements.add(scrollbar = new Scrollbar(this, false));
			this.elements.add(rootf = new Button(this, "fileselector:root", "fileselector:root", 480, 26, 4, 4){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					if(current.getParentFile() != null) current = current.getParentFile().getAbsoluteFile();
					Settings.SETTINGS.get("filedir_last").setValue(current.toPath()); return refresh0();
				}
			});
		}
		
		public boolean onButtonClick(int x, int y, boolean left, boolean hovered){
			if(scrollbar.onButtonClick(x, y, left, scrollbar.isHovered())) return true;
			if(rootf.onButtonClick(x, y, left, rootf.isHovered())) return true;
			return super.onButtonClick(x, y, left, hovered);
		}

		@Override
		public boolean processScrollWheel(int wheel){
			int amount = -wheel / (Mouse.isButtonDown(1) ? 1 : 10);
			scrollbar.scrolled += amount; if(scrollbar.scrolled < 0) scrollbar.scrolled = 0; return refresh();
		}
		
		public boolean refresh0(){
			rootf.setText(current.getPath(), false); int height = 30; this.elements.clear();
			File[] files = selector.getFilteredList();
			for(File file : files){
				elements.add(new FileButton(this, file)); height += 26;
			}
			elements.add(scrollbar); elements.add(rootf);
			fullheight = height; scrollbar.scrolled = 0; return refresh();
		}
		
		@Override
		public boolean refresh(){
			int head = 34; head -= scrollbar.scrolled;
			for(Element element : elements){
				if(element == scrollbar || element == rootf) continue;
				element.yrel = head; element.setVisible(true);
				if(head < 4) element.setVisible(false);
				if(head + 28 > height) element.setVisible(false);
				head += 26; element.repos();
			} scrollbar.repos(); return true;
		}

		@Override
		public int getFullHeight(){
			return fullheight;
		}
		
	}
	
	public static class FileButton extends Button {

		private static RGB selected = new RGB("#32a852");
		private static Texture tex;
		private Files files;
		private File file;

		public FileButton(Files root, File file){
			super(root, "fileselector:filebutton", "fileselector:filebutton", 480, 26, 4, 0);
			this.setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, false, false, true, true);
			this.files = root; this.file = file; this.setText(file.getName() + (file.isDirectory() ? "/" : ""), false);
		}
		
		@Override
		public void renderSelfQuad(){
			if(tex == null || tex.rebindQ() || vertexes == null){
				int width = this.width, height = this.height; gentex = true;
				if(top) height += border_width; if(bot) height += border_width;
				if(left) width += border_width; if(right) width += border_width;
				if(tex == null || tex.rebindQ()){
					BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					if(border != null) for(int i = 0; i < width; i++) for(int j = 0; j < height; j++) img.setRGB(i, j, border);
					{
						int xb = left ? border_width : 0, yb = top ? border_width : 0;
						int xe = right ? width - border_width : width, ye = bot ? height - border_width : height;
						runfill(img, xb, xe, yb, ye, fill);
					}
					if(border_width > 2){
						if(top) runfill(img, 1, width - 1, 1, border_width - 1, border_fill);
						if(bot) runfill(img, 1, width - 1, height - border_width + 1, height - 1, border_fill);
						if(left) runfill(img, 1, border_width - 1, 1, height - 1, border_fill);
						if(right) runfill(img, width - border_width + 1, width - 1, 1, height - 1, border_fill);
					}
					if(tex == null) tex = TextureManager.createTexture("elm:" + id, img); else tex.setImage(img);
				}
				//
				float x = this.x, y = this.y; if(top) y -= border_width; if(left) x -= border_width; vertexes = new float[2][][];
				vertexes[0] = new float[][]{ { x, y }, { x + width, y }, { x + width, y + height }, { x, y + height } };
				vertexes[1] = new float[][]{ { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } };
			}
			if(files.selected == this) selected.glColorApply(); else if(hovered) hovercolor.glColorApply();
			TextureManager.bindTexture(tex);
			GL11.glBegin(GL11.GL_QUADS);
			for(int j = 0; j < 4; j++){
				GL11.glTexCoord2f(vertexes[1][j][0], vertexes[1][j][1]);
				GL11.glVertex2f(vertexes[0][j][0], vertexes[0][j][1]);
			}
	        GL11.glEnd();
	        if(hovered || files.selected == this) RGB.glColorReset();
		}
		
		@Override
		public boolean processButtonClick(int x, int y, boolean left){
			if(!left) return false;
			if(file.isDirectory()){ files.current = file.getAbsoluteFile();
				Settings.SETTINGS.get("filedir_last").setValue(file.toPath()); return files.refresh0();
			} else{ files.selected = this; return true; }
		}
		
	}
	
	private static final File NONE = new File("no files in directory"), ERROR = new File("error.jvm (see console)");
	private static Stream<File> stream;
	
	private File[] getFilteredList(){
		try{
			if(files.current.listFiles() == null) return new File[]{ NONE };
			File[] dirs = Arrays.asList(files.current.listFiles()).stream().filter(pre -> pre.isDirectory()).collect(Collectors.<File>toList()).toArray(new File[0]);
			switch(mode){
				case EXPORT: case IMPORT:{
					stream = Arrays.asList(files.current.listFiles()).stream().filter(pre -> porter.isValidFile(pre));
					break;
				}
				case PNG:{
					stream = Arrays.asList(files.current.listFiles()).stream().filter(pre -> pre.isDirectory() || pre.getName().toLowerCase().endsWith(".png"));
					break;
				}
				case SAVEFILE_SAVE: case SAVEFILE_LOAD:{
					stream = Arrays.asList(files.current.listFiles()).stream().filter(pre -> pre.isDirectory() || pre.getName().toLowerCase().endsWith(".fmtb"));
					break;
				}
				case HELPFRAMEIMG:{
					stream = Arrays.asList(files.current.listFiles()).stream().filter(pre -> pre.isDirectory() || pre.getName().toLowerCase().endsWith(".png")
						|| pre.getName().toLowerCase().endsWith(".jpg") || pre.getName().toLowerCase().endsWith(".jpeg"));
					break;
				}
				default:{
					stream = Arrays.asList(files.current.listFiles()).stream().filter(pre -> pre.isDirectory());
					break;
				}
			}
			//
			File[] fils = stream.filter(pre -> !pre.isDirectory()).collect(Collectors.<File>toList()).toArray(new File[0]);
			if(dirs.length == 0) return fils; else if(fils.length == 0) return dirs;
			File[] sorted = new File[dirs.length + fils.length];
			for(int i = 0; i < dirs.length; i++) sorted[i] = dirs[i];
			for(int i = 0; i < fils.length; i++) sorted[i + dirs.length] = fils[i];
			return sorted;
		}
		catch(Exception e){
			e.printStackTrace();
			return new File[]{ ERROR };
		}
	}
	
	public static enum FileRoot {
		
		LAST, SAVES, EXPORT, IMPORT, TEXTURES, HELPERS;
		
		public static FileRoot last;

		public File getFile(){
			if(last == this && this != LAST){ last = this; return LAST.getFile(); } if(this != LAST) last = this;
			switch(this){
				case LAST: return new File(Settings.SETTINGS.get("filedir_last").getStringValue());
				case EXPORT: return new File(Settings.SETTINGS.get("filedir_export").getStringValue());
				case IMPORT: return new File(Settings.SETTINGS.get("filedir_import").getStringValue());
				case HELPERS: return new File(Settings.SETTINGS.get("filedir_helpers").getStringValue());
				case TEXTURES: return new File(Settings.SETTINGS.get("filedir_textures").getStringValue());
				case SAVES: default: return new File(Settings.SETTINGS.get("filedir_saves").getStringValue());
			}
		}
	}
	
	public static enum ChooserMode {
		
		EXPORT, IMPORT, PNG, HELPFRAMEIMG, SAVEFILE_SAVE, SAVEFILE_LOAD, NONE;
		public boolean exports(){ return EXPORT == this; }
		public boolean imports(){ return IMPORT == this; }
		public boolean pmgimg(){ return PNG == this; }
		public boolean helpframimg(){ return HELPFRAMEIMG == this; }
		public boolean savefile_load(){ return SAVEFILE_LOAD == this; }
		public boolean savefile_save(){ return SAVEFILE_SAVE == this; }
		public Setting[] settings(){
			switch(this){
				case EXPORT:
					break;
				case HELPFRAMEIMG:
					break;
				case IMPORT: return new Setting[]{ new Setting(Type.BOOLEAN, "integrate", false) };
				case NONE:
					break;
				case PNG:
					break;
				case SAVEFILE_LOAD:
					break;
				case SAVEFILE_SAVE:
					break;
				default:
					break;
			}
			return new Setting[0];
		}
	}

}