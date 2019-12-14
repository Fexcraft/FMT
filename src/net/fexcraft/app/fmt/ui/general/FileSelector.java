package net.fexcraft.app.fmt.ui.general;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.Scrollbar.Scrollable;
import net.fexcraft.app.fmt.utils.Backups;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;

public class FileSelector extends Element implements Dialog {

	public static final AfterTask NOTHING = new AfterTask(){ @Override public void run(){ Print.console(file); return; }};
	//
	private ArrayList<Setting> settings = new ArrayList<>();
	private static ExImPorter[] porters;
	private static int eximscroll;
	private ChooserMode mode;
	private AfterTask onfile;
	private TextField cfn, exim;
	private Button[] sel;
	private Button title;
	private Files files;
	private Icon exim_prev, exim_next;

	public FileSelector(){
		super(null, "fileselector", "fileselector"); dialogs.add(this);
		this.setSize(512, 548).setPosition(0, 0).setColor(0xffcdcdcd);
		this.setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true);
		this.setHoverColor(StyleSheet.WHITE, false).setDraggable(true).setVisible(false);
		//
		this.elements.add((title = new Button(this, "null", "fileselector:title", 480, 30, 16, 12))
			.setHoverColor(StyleSheet.WHITE, false).setBorder(0xff909090, StyleSheet.WHITE, 1, true, true, true, true));
		this.elements.add(files = new Files(this));
		//
		this.elements.add(cfn = new TextField(this, "customfilename", "fileselector:filename", 480, 16, 440).setTextColor(RGB.BLACK));
		cfn.setColor("hover_sel", new RGB(230, 164, 138)).setColor(0xffcdcdcd).setHoverColor(0xffe8cf89, false).setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true);
		//
		this.elements.add(exim = new TextField(this, "testfield", "fileselector:eximporter", 438, 16, 474).setTextColor(RGB.BLACK));
		exim.setColor("hover_sel", new RGB(230, 164, 138)).setColor(0xffcdcdcd).setHoverColor(0xffe8cf89, false);
		exim.setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true).setEnabled(false);
		this.elements.add((exim_prev = new Icon(this, "exim_prev", "fileselector:eximporter_button", "icons/fileselector_prev", 20, 26, 456, 474){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				if(porters == null) return false;
				if(eximscroll <= 0) eximscroll = porters.length - 1; else eximscroll--;
				exim.setText(mode.getPorterTitle(), false); files.refresh0(); return true;
			}
		}).setHoverColor(StyleSheet.WHITE, true).setHoverColor(0xffcdcdcd, false));
		this.elements.add((exim_next = new Icon(this, "exim_next", "fileselector:eximporter_button", "icons/fileselector_next", 20, 26, 476, 474){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				if(porters == null) return false;
				if(eximscroll >= porters.length - 1) eximscroll = 0; else eximscroll++;
				exim.setText(mode.getPorterTitle(), false); files.refresh0(); return true;
			}
		}).setHoverColor(StyleSheet.WHITE, true).setHoverColor(0xffcdcdcd, false));
		//
		sel = new Button[3];
		this.elements.add(sel[0] = new Button(this, "button0", "fileselector:button", 140, 28, 16, 510, StyleSheet.YELLOW, StyleSheet.RED){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				onfile.porter = porters == null ? null : porters[eximscroll];
				if(cfn.isEnabled() && isValidInput(cfn.getText())){
					onfile.file = new File(files.current, cfn.getText() + (cfn.getText().endsWith(getCurrentSelectedFileExtension(onfile.porter)) ? "" : getCurrentSelectedFileExtension(onfile.porter)));
				}
				else{
					if(files.selected == null) return true; onfile.file = files.selected.file;
				}
				if(onfile.file != null){
					if(!onfile.file.getParentFile().exists()) onfile.file.getParentFile().mkdirs();
					UserInterface.FILECHOOSER.visible = false; applySettingsToAfterTask(onfile);
					boolean ovrd = (mode.exports() || mode.savefile_save()) && onfile.file.exists();
					String override = format("dialog.filechooser.override", "Override existing File?<nl>%s", onfile.file.getName());
					if(onfile.settings.isEmpty()){
						if(ovrd){
							FMTB.showDialogbox(override, translate("dialog.filechooser.override.confirm", "yes"), translate("dialog.filechooser.override.cancel", "no!"), onfile, DialogBox.NOTHING);
						} else{ onfile.run(); }
						UserInterface.FILECHOOSER.reset();
					}
					else{
						if(ovrd){
							FMTB.showDialogbox(override, translate("dialog.filechooser.override.confirm", "yes"), translate("dialog.filechooser.override.cancel", "no!"), new Runnable(){
								private AfterTask task = onfile;
								@Override public void run(){ UserInterface.SETTINGSBOX.show(translate("filechooser.settings", "FileChooser Settings"), task); }
							}, DialogBox.NOTHING);
						}
						else{ UserInterface.SETTINGSBOX.show(translate("filechooser.settings", "FileChooser Settings"), onfile); }
						UserInterface.FILECHOOSER.reset();
					}
					return true;
				
				}
				return true;
			}
			@Override
			public void hovered(float mx, float my){
				super.hovered(mx, my); if(hovered) cfn.onReturn();
			}
		});
		this.elements.add(sel[1] = new Button(this, "button1", "fileselector:button", 140, 28, 186, 510, StyleSheet.YELLOW, StyleSheet.RED){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				onfile.porter = porters == null ? null : porters[eximscroll];
				String str = Backups.getSimpleDateFormat(true).format(Time.getDate()); UserInterface.FILECHOOSER.visible = false;
				String ext = getCurrentSelectedFileExtension(onfile.porter);
				onfile.file = new File(files.current, (FMTB.MODEL.name == null ? "unnamed" : FMTB.MODEL.name) + "-(" + str + ")" + ext);
				applySettingsToAfterTask(onfile); onfile.run(); UserInterface.FILECHOOSER.reset(); return true;
			}
		});
		this.elements.add(sel[2] = new Button(this, "button2", "fileselector:button", 140, 28, 512 - 140 - 16, 510, StyleSheet.YELLOW, StyleSheet.RED){
			@Override public boolean processButtonClick(int x, int y, boolean left){ UserInterface.FILECHOOSER.reset(); return true; }
		});
		for(Button button : sel) button.setBorder(0xff909090, StyleSheet.WHITE, 1, true, true, true, true);
		//
		//this.show("No Title.", null, null, null, FileRoot.SAVES, NOTHING, ChooserMode.SAVEFILE_SAVE, null);
	}

	public final void show(String title, String con, String sug, String can, FileRoot root, AfterTask aftertask, ChooserMode mode){
		this.reset(); this.setVisible(true); this.title.setText(title, true); onfile = aftertask;
		this.mode = mode; files.current = root.getFile().getAbsoluteFile(); settings.clear();
		//
		sel[0].setText(con == null ? Translator.translate("filechooser.default.confirm", "OK") : con, true);
		sel[1].setText(sug == null ? Translator.translate("filechooser.default.suggested", "Suggested") : sug, true);
		sel[2].setText(can == null ? Translator.translate("filechooser.default.cancel", "Cancel") : can, true);
		sel[1].setEnabled(mode.exports() || mode.savefile_save()); cfn.setEnabled(sel[1].isEnabled());
		if(cfn.isEnabled()) cfn.setText(translate("filechooser.customfile.active", "Choose a file to override or write a custom name here!"), false);
		else cfn.setText(translate("filechooser.customfile.inactive", "Please choose an existing file to proceed."), false);
		if(mode.hasPorter()){
			porters = PorterManager.getPorters(mode.exports()).toArray(new ExImPorter[0]);
			exim.setText(mode.getPorterTitle(), false); exim.width = 438; exim.repos();
			exim_next.setVisible(true); exim_prev.setVisible(true);
			if(eximscroll >= porters.length) eximscroll = 0; if(eximscroll < 0) eximscroll = 0;
		}
		else{
			exim_next.setVisible(false); exim_prev.setVisible(false);
			exim.width = 480; exim.repos();
		}
		exim.setText(mode.getPorterTitle(), false); files.refresh0();
		//
		Setting[] modesettings = mode.settings(); for(Setting setting : modesettings) this.settings.add(setting);
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
		this.setVisible(false); porters = null; mode = null; files.selected = null; onfile = null;
	}
	
	public static abstract class AfterTask implements Runnable {
		public File file;
		public ExImPorter porter;
		public List<Setting> settings = new ArrayList<>();
		public Map<String, Setting> mapped_settings;
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
			if(file.isDirectory()){ files.current = file.getAbsoluteFile(); files.selected = null;
				Settings.SETTINGS.get("filedir_last").setValue(file.toPath()); return files.refresh0();
			} else{ files.selected = this; return true; }
		}
		
	}
	
	private static final File NONE = new File("no files in directory"), ERROR = new File("error.jvm (see console)"), NOFOLDER = new File("folder does not exists, yet");
	private static Stream<File> stream;
	
	private File[] getFilteredList(){
		try{
			if(!files.current.exists()) return new File[]{ NOFOLDER };
			File[] folder = files.current.listFiles();
			if(folder == null || folder.length == 0) return new File[]{ NONE };
			File[] dirs = Arrays.asList(folder).stream().filter(pre -> pre.isDirectory()).collect(Collectors.<File>toList()).toArray(new File[0]);
			switch(mode){
				case EXPORT: case IMPORT:{
					stream = Arrays.asList(folder).stream().filter(pre -> porters[eximscroll].isValidFile(pre));
					break;
				}
				case PNG:{
					stream = Arrays.asList(folder).stream().filter(pre -> pre.isDirectory() || pre.getName().toLowerCase().endsWith(".png"));
					break;
				}
				case SAVEFILE_SAVE: case SAVEFILE_LOAD:{
					stream = Arrays.asList(folder).stream().filter(pre -> pre.isDirectory() || pre.getName().toLowerCase().endsWith(".fmtb"));
					break;
				}
				case HELPFRAMEIMG:{
					stream = Arrays.asList(folder).stream().filter(pre -> pre.isDirectory() || pre.getName().toLowerCase().endsWith(".png")
						|| pre.getName().toLowerCase().endsWith(".jpg") || pre.getName().toLowerCase().endsWith(".jpeg"));
					break;
				}
				default:{
					stream = Arrays.asList(folder).stream().filter(pre -> pre.isDirectory());
					break;
				}
			}
			//
			File[] fils = stream.filter(pre -> !pre.isDirectory()).collect(Collectors.<File>toList()).toArray(new File[0]);
			if(dirs.length == 0 && fils.length == 0) return new File[]{ NONE };
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
		public boolean hasPorter(){ return exports() || imports(); }
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
		public String getPorterTitle(){
			switch(this){
				case IMPORT: case EXPORT: return porters == null ? "no ex/im-porters" : (this.exports() ? "Exporter: " : "Importer: ") + porters[eximscroll].getName();
				case HELPFRAMEIMG: return "Helpframe / Image Loader";
				case PNG: return "PNG [Portable Network Graphics]";
				case SAVEFILE_LOAD: return "FMTB File Loader";
				case SAVEFILE_SAVE: return "FMTB File Saver";
				case NONE: default: return "CHOOSING MODE NONE";
			}
		}
	}
	
	//

	protected boolean isValidInput(String text){
		if(text == null || text.length() == 0) return false;
		else if(cfn.getText().contains(translate("filechooser.customfile.active"))) return false;
		return true;
	}

	private String getCurrentSelectedFileExtension(ExImPorter porter){
		switch(mode){
			case EXPORT: case IMPORT:{
				return porter.getExtensions()[0].startsWith(".") ? porter.getExtensions()[0] : "." + porter.getExtensions()[0];
			}
			case PNG:{ return ".png"; }
			case SAVEFILE_LOAD: case SAVEFILE_SAVE:{ return ".fmtb"; }
			default: return ".error";
		}
	}
	
	private void applySettingsToAfterTask(AfterTask onfile){
		if(porters == null) return; settings.addAll(onfile.porter.getSettings(mode.exports()));
		onfile.settings.addAll(settings); onfile.mapped_settings = new HashMap<>();
		onfile.settings.forEach(setting -> onfile.mapped_settings.put(setting.getId(), setting));
	}

}