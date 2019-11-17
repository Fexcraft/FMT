/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.utils.Backups;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;

/**
 * "Updated New FileChooser"
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FileChooser extends Element implements Dialog {

	public static final AfterTask NOTHING = new AfterTask(){ @Override public void run(){ Print.console(file); return; }};
	private int scroll, selected = -1;
	private File currdir = SaveLoad.getRoot();
	private Button[] files = new Button[12], sel = new Button[3];
	private ArrayList<Setting> settings = new ArrayList<>();
	private AfterTask onfile = NOTHING;
	private ExImPorter porter;
	private TextField cfn;
	private ChooserMode mode;
	private Button root, title;
	
	public FileChooser(){
		super(null, "filechooser", "filechooser"); this.setHoverColor(StyleSheet.WHITE, false).setDraggable(true);
		this.setColor(0xff80adcc).setBorder(0xff000000, 0xfffcba03, 3, true, true, true, true);
		this.setSize(512, 512).setVisible(false).setPosition(0, 0); Dialog.dialogs.add(this);
		this.elements.add(new Element(this, "filechooser:background", "filechooser:background").setSize(480, 372).setPosition(16, 50)
			.setColor(0xff2b4570).setHoverColor(StyleSheet.WHITE, false));
		this.elements.add(sel[0] = new Button(this, "button0", "filechooser:button", 140, 28, 20, 470, StyleSheet.YELLOW, StyleSheet.RED){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				onfile.porter = porter;
				if(cfn.isEnabled() && isValidInput(cfn.getText())){
					onfile.file = new File(currdir, cfn.getText() + (cfn.getText().endsWith(getCurrentSelectedFileExtension(onfile.porter)) ? "" : getCurrentSelectedFileExtension(onfile.porter)));
				}
				else{
					if(selected < 0) return true; onfile.file = getFilteredList()[selected];
				}
				if(onfile.file != null){
					UserInterface.FILECHOOSER.visible = false; applySettingsToAfterTask(onfile);
					boolean ovrd = (mode.exports() || mode.savefile_save()) && onfile.file.exists();
					String override = format("dialog.filechooser.override", "Override existing File?<nl>%s", onfile.file.getName());
					if(onfile.settings.isEmpty()){
						if(ovrd){
							FMTB.showDialogbox(override, translate("dialog.filechooser.override.confirm", "yes"), translate("dialog.filechooser.override.confirm", "no!"), onfile, DialogBox.NOTHING);
						} else{ onfile.run(); }
						UserInterface.FILECHOOSER.reset();
					}
					else{
						if(ovrd){
							FMTB.showDialogbox(override, translate("dialog.filechooser.override.confirm", "yes"), translate("dialog.filechooser.override.confirm", "no!"), new Runnable(){
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
		this.elements.add(sel[1] = new Button(this, "button1", "filechooser:button", 140, 28, 186, 470, StyleSheet.YELLOW, StyleSheet.RED){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				onfile.porter = porter;
				String str = Backups.getSimpleDateFormat(true).format(Time.getDate()); UserInterface.FILECHOOSER.visible = false;
				String ext = getCurrentSelectedFileExtension(onfile.porter);
				onfile.file = new File(currdir, (FMTB.MODEL.name == null ? "unnamed" : FMTB.MODEL.name) + "-(" + str + ")" + ext);
				applySettingsToAfterTask(onfile); onfile.run(); UserInterface.FILECHOOSER.reset(); return true;
			}
		});
		this.elements.add(sel[2] = new Button(this, "button2", "filechooser:button", 140, 28, 352, 470, StyleSheet.YELLOW, StyleSheet.RED){
			@Override public boolean processButtonClick(int x, int y, boolean left){ UserInterface.FILECHOOSER.reset(); return true; }
		});
		for(Button button : sel) button.setBorder(0xff909090, StyleSheet.WHITE, 1, true, true, true, true);
		//
		this.elements.add(root = new Button(this, "fileroot", "filechooser:root", 472, 28, 20, 54, 0xffc8c8c8){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				if(currdir.getParentFile() != null) currdir = currdir.getParentFile().getAbsoluteFile();
				Settings.SETTINGS.get("filedir_last").setValue(currdir.toPath().toString()); ressel(); return true;
			}
		}); root.setColor(0xff81cc82).setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, false, false, true, true);
		for(int i = 0; i < files.length; i++){ int j = i;
			this.elements.add(files[i] = new Button(this, "files" + i, "filechooser:file", 472, 28, 20, 82 + (i * 28), StyleSheet.YELLOW, 0xffff8300){
				@Override public boolean processButtonClick(int x, int y, boolean left){
					File[] fls = getFilteredList();
					if(fls[selected = scroll + j].isDirectory()){
						currdir = fls[selected]; ressel();
					} return true;
				}
				@Override public boolean onScrollWheel(int wheel){ scroll += wheel < 0 ? 8 : -8; if(scroll < 0) scroll = 0; return true; }
			}); files[i].setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, false, false, true, true);
		}
		this.elements.add(cfn = new TextField(this, "customfilename", "filechooser:filename", 468, 22, 430).setTextColor(RGB.BLACK));
		cfn.setColor("hover_sel", new RGB(230, 164, 138)).setColor(0xffcdcdcd).setHoverColor(0xffe8cf89, false).setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true);
		//
		this.elements.add((title = new Button(this, "No Title.", "filechooser:title", 480, 30, 16, 12))
			.setHoverColor(StyleSheet.WHITE, false).setBorder(0xff909090, StyleSheet.WHITE, 1, true, true, true, true));
		//
		//this.show(new String[]{ "test title", "OK" }, FileRoot.SAVES, NOTHING, ChooserMode.SAVEFILE_SAVE, null);
	}
	
	private void applySettingsToAfterTask(AfterTask onfile){
		settings.addAll(onfile.porter.getSettings(mode.exports()));
		onfile.settings.addAll(settings); onfile.mapped_settings = new HashMap<>();
		onfile.settings.forEach(setting -> onfile.mapped_settings.put(setting.getId(), setting));
	}

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
	
	private void ressel(){
		this.selected = -1; this.scroll = 0;
	}
	
	private static final File NONE = new File("no files in directory"), ERROR = new File("error.jvm (see console)");
	private static Stream<File> stream;
	
	private File[] getFilteredList(){
		try{
			if(currdir.listFiles() == null) return new File[]{ NONE };
			switch(mode){
				case EXPORT: case IMPORT:{
					stream = Arrays.asList(currdir.listFiles()).stream().filter(pre -> porter.isValidFile(pre));
					break;
				}
				case PNG:{
					stream = Arrays.asList(currdir.listFiles()).stream().filter(pre -> pre.isDirectory() || pre.getName().toLowerCase().endsWith(".png"));
					break;
				}
				case SAVEFILE_SAVE: case SAVEFILE_LOAD:{
					stream = Arrays.asList(currdir.listFiles()).stream().filter(pre -> pre.isDirectory() || pre.getName().toLowerCase().endsWith(".fmtb"));
					break;
				}
				case HELPFRAMEIMG:{
					stream = Arrays.asList(currdir.listFiles()).stream().filter(pre -> pre.isDirectory() || pre.getName().toLowerCase().endsWith(".png") || pre.getName().toLowerCase().endsWith(".jpg") || pre.getName().toLowerCase().endsWith(".jpeg"));
					break;
				}
				default:{
					stream = Arrays.asList(currdir.listFiles()).stream().filter(pre -> pre.isDirectory());
					break;
				}
			}
			return stream.collect(Collectors.<File>toList()).toArray(new File[0]);
		}
		catch(Exception e){
			e.printStackTrace();
			return new File[]{ ERROR };
		}
	}
	
	@Override
	public Element repos(){
		x = (UserInterface.width - width) / 2 + xrel; y = (UserInterface.height - height) / 2 + yrel;
		clearVertexes(); for(Element elm : elements) elm.repos(); return this;
	}
	
	@Override
	public void renderSelf(int rw, int rh) {
		this.renderSelfQuad();//x, y, width, height, texture);
		root.setText(currdir.getPath(), false); File[] fls = getFilteredList();
		while(scroll + 12 > fls.length && scroll - 1 >= 0) scroll--;
		for(int i = 0; i < files.length; i++){
			files[i].setEnabled(selected < 0 || selected != scroll + i);
			if(scroll + i >= fls.length){ files[i].setVisible(false); }
			else{
				files[i].setVisible(true); files[i].setText(fls[scroll + i].getName() + (fls[scroll + i].isDirectory() ? "/" : ""), false);
			}
		}
		sel[0].setEnabled((selected > -1 && selected < fls.length && !fls[selected].isDirectory()) || (cfn.isEnabled() && this.isValidInput(cfn.getText())));
	}
	
	@Override
	public boolean processButtonClick(int x, int y, boolean left){
		//
		return true;
	}
	
	/** 
	 * 
	 * @param png 
	 * @param b 
	 * @param text 0 - title, 1 - button0, 2 - button1, 3 - button2
	 * */
	public void show(String[] ntext, FileRoot root, AfterTask after, ChooserMode mode, ExImPorter porter){
		this.reset(); FMTB.get().reset(false); this.currdir = root.getFile().getAbsoluteFile();
		//
		this.title.setText(ntext[0], 4, 4); this.visible = true; this.mode = mode; this.porter = porter;
		this.elements.forEach(elm -> { elm.setVisible(after != null); elm.setEnabled(after != null); });
		sel[0].setText(ntext.length < 2 || ntext[1] == null ? Translator.translate("filechooser.default.confirm", "OK") : ntext[1], true);
		sel[1].setText(ntext.length < 3 || ntext[2] == null ? Translator.translate("filechooser.default.suggested", "Suggested") : ntext[2], true);
		sel[2].setText(ntext.length < 4 || ntext[3] == null ? Translator.translate("filechooser.default.cancel", "Cancel") : ntext[3], true);
		sel[1].setEnabled(mode.exports() || mode.savefile_save()); cfn.setEnabled(sel[1].isEnabled()); this.onfile = after;
		if(cfn.isEnabled()) cfn.setText(translate("filechooser.customfile.active", "Choose a file to override or write a custom name here!"), false);
		else cfn.setText(translate("filechooser.customfile.inactive", "Please choose an existing file to proceed."), false);
		//
		Setting[] modesettings = mode.settings(); for(Setting setting : modesettings) this.settings.add(setting);
	}
	
	public void show(String[] ntext, FileRoot root, AfterTask after, ChooserMode mode){
		show(ntext, root, after, mode, null);
	}
	
	public void reset(){
		this.onfile = null; this.currdir = SaveLoad.getRoot(); ressel(); porter = null; mode = ChooserMode.NONE;
		sel[0].setText(null, false); sel[1].setText(null, false); sel[2].setText(null, false); visible = false;
		this.settings.clear();
	}
	
	public static abstract class AfterTask implements Runnable {
		public File file;
		public ExImPorter porter;
		public List<Setting> settings = new ArrayList<>();
		public Map<String, Setting> mapped_settings;
	}
	
	public static enum ChooserMode {
		EXPORT, IMPORT, PNG, HELPFRAMEIMG, SAVEFILE_SAVE, SAVEFILE_LOAD, NONE;
		public boolean exports(){ return EXPORT == this; }
		public boolean imports(){ return IMPORT == this; }//"import" is a reserved keyword...
		public boolean pmgimg(){ return PNG == this; }
		public boolean helpframimg(){ return HELPFRAMEIMG == this; }
		//public boolean eximporter(){ return EXPORT == this || IMPORT == this; }
		//public boolean savefile(){ return SAVEFILE == this; }
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

	@Override
	public boolean visible(){
		return visible;
	}

}
