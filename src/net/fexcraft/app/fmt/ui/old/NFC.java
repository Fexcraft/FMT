/**
 * 
 */
package net.fexcraft.app.fmt.ui.old;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.utils.Backups;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;

/**
 * "New FileChooser"
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class NFC extends Element implements Dialog {

	public static final AfterTask NOTHING = new AfterTask(){ @Override public void run(){ Print.console(file); return; }};
	private Button exim_next, exim_prev, root;
	private int scroll, eximscroll, selected = -1;
	private File currdir = SaveLoad.getRoot();
	private Button[] files = new Button[12], sel = new Button[3];
	private ArrayList<Setting> settings = new ArrayList<>();
	private String title = "No Title.";
	private AfterTask onfile = NOTHING;
	private TextField eximporter, cfn;
	private ChooserMode mode;
	
	public NFC(){
		super(null, "ui/filechooser"); TextureManager.loadTexture("ui/filechooser", null);
		TextureManager.loadTexture("icons/file_chooser_0", null); TextureManager.loadTexture("icons/file_chooser_1", null);
		TextureManager.loadTexture("icons/file_chooser_2", null); TextureManager.loadTexture("icons/file_chooser_3", null);
		TextureManager.loadTexture("icons/file_chooser_4", null); TextureManager.loadTexture("icons/file_chooser_5", null);
		TextureManager.loadTexture("icons/file_chooser_6", null); TextureManager.loadTexture("icons/file_chooser_7", null);
		this.visible = false; this.z = 80; this.height = 546; this.width = 512; Dialog.dialogs.add(this);
		this.elements.add(sel[0] = new Button(this, "button0", 140, 28, 20, 504/*470*/, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				onfile.porter = PorterManager.getPorters(mode.exports()).get(eximscroll);
				if(cfn.isEnabled() && isValidInput(cfn.getText())){
					onfile.file = new File(currdir, cfn.getText() + (cfn.getText().endsWith(getCurrentSelectedFileExtension(onfile.porter)) ? "" : getCurrentSelectedFileExtension(onfile.porter)));
				}
				else{
					if(selected < 0) return true; onfile.file = getFilteredList()[selected];
				}
				if(onfile.file != null){
					UserInterface.FILECHOOSER.visible = false; applySettingsToAfterTask(onfile);
					boolean ovrd = (mode.exports() || mode.savefile_save()) && onfile.file.exists();
					if(onfile.settings.isEmpty()){
						if(ovrd){
							FMTB.showDialogbox("Override existing File?\n" + onfile.file.getName(), "yes", "no!", onfile, DialogBox.NOTHING);
						} else{ onfile.run(); }
						UserInterface.FILECHOOSER.reset();
					}
					else{
						if(ovrd){
							FMTB.showDialogbox("Override existing File?\n" + onfile.file.getName(), "yes", "no!", new Runnable(){
								private AfterTask task = onfile;
								@Override public void run(){ UserInterface.SETTINGSBOX.show("FileChooser Settings", task); }
							}, DialogBox.NOTHING);
						}
						else{ UserInterface.SETTINGSBOX.show("FileChooser Settings", onfile); }
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
		this.elements.add(sel[1] = new Button(this, "button1", 140, 28, 186, 504/*470*/, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				onfile.porter = PorterManager.getPorters(mode.exports()).get(eximscroll);
				String str = Backups.getSimpleDateFormat(true).format(Time.getDate()); UserInterface.FILECHOOSER.visible = false;
				String ext = getCurrentSelectedFileExtension(onfile.porter);
				onfile.file = new File(currdir, (FMTB.MODEL.name == null ? "unnamed" : FMTB.MODEL.name) + "-(" + str + ")" + ext);
				applySettingsToAfterTask(onfile); onfile.run(); UserInterface.FILECHOOSER.reset(); return true;
			}
		});
		this.elements.add(sel[2] = new Button(this, "button2", 140, 28, 352, 504/*470*/, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ UserInterface.FILECHOOSER.reset(); return true; }
		});
		//
		this.elements.add((eximporter = new TextField(this, "eximporter", 404, 17, 430).setRenderBackground(false).setColor(RGB.BLACK)).setEnabled(false));
		this.elements.add(root = new Button(this, "fileroot", 472, 28, 20, 54, new RGB(200, 200, 200)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				if(currdir.getParentFile() != null) currdir = currdir.getParentFile().getAbsoluteFile(); ressel(); return true;
			}
		}.setBackgroundless(true));
		for(int i = 0; i < files.length; i++){ int j = i;
			this.elements.add(files[i] = new Button(this, "files" + i, 472, 28, 20, 82 + (i * 28), new RGB(255, 255, 0), new RGB("#ff8300")){
				@Override protected boolean processButtonClick(int x, int y, boolean left){
					File[] fls = getFilteredList();
					if(fls[selected = scroll + j].isDirectory()){
						currdir = fls[selected]; ressel();
					} return true;
				}
				@Override public boolean onScrollWheel(int wheel){ scroll += wheel < 0 ? 8 : -8; if(scroll < 0) scroll = 0; return true; }
			}.setBackgroundless(true));
		}
		//
		this.elements.add(exim_prev = new Button(this, "exim-", 26, 26, 436, 429, new RGB(120, 120, 120)){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				switch(mode){
					case EXPORT: case IMPORT:{
						eximscroll++;if(eximscroll >= PorterManager.getPorters(mode.exports()).size()) eximscroll = 0;
						ressel(); return true;
					}
					case PNG: case HELPFRAMEIMG: case SAVEFILE_SAVE: case SAVEFILE_LOAD: default: return true;
				}
			}
		}); exim_prev.setTexPosSize("icons/arrow_decrease", 0, 0, 32, 32);
		this.elements.add(exim_next = new Button(this, "exim+", 26, 26, 464, 429, new RGB(120, 120, 120)){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				switch(mode){
					case EXPORT: case IMPORT:{
						eximscroll--; if(eximscroll < 0) eximscroll = PorterManager.getPorters(mode.exports()).size() - 1;
						ressel(); return true;
					}
					case PNG: case HELPFRAMEIMG: case SAVEFILE_SAVE: case SAVEFILE_LOAD: default: return true;
				}
			}
		}); exim_next.setTexPosSize("icons/arrow_increase", 0, 0, 32, 32);
		this.elements.add(cfn = new TextField(this, "customfilename", 468, 22, 470).setColorOnHover(new RGB(200, 200, 200)).setRenderBackground(false).setColor(RGB.BLACK));
		//
		//this.show(new String[]{ "test title", "OK"}, null, NOTHING, false);
	}
	
	private void applySettingsToAfterTask(AfterTask onfile){
		settings.addAll(onfile.porter.getSettings(mode.exports()));
		onfile.settings.addAll(settings); onfile.mapped_settings = new HashMap<>();
		onfile.settings.forEach(setting -> onfile.mapped_settings.put(setting.getId(), setting));
	}

	protected boolean isValidInput(String text){
		if(text == null || text.length() == 0) return false;
		else if(cfn.getText().contains(DCFNFC[0]) || cfn.getText().contains(DCFNFC[1])
			|| cfn.getText().contains(DCFNFC[2]) || cfn.getText().contains(DCFNFC[3])) return false;
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
					ExImPorter porter = PorterManager.getPorters(mode.exports()).get(eximscroll);
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
	public void renderSelf(int rw, int rh) {
		this.renderQuad(x = (rw / 2) - (width / 2), y = (rh / 2) - (height / 2), width, height, "ui/filechooser");
		sel[0].x = x + 20; sel[0].y = y + 504; sel[1].x = x + 186; sel[1].y = y + 504; sel[2].x = x + 352; sel[2].y = y + 504;
		eximporter.x = x + 22; eximporter.y = y + 430; cfn.x = x + 22; cfn.y = y + 470;
		switch(mode){
			case EXPORT:
				eximporter.setText("Exporter: "+ PorterManager.getPorters(true).get(eximscroll).getName(), false);
				break;
			case IMPORT:
				eximporter.setText("Importer: " + PorterManager.getPorters(false).get(eximscroll).getName(), false);
				break;
			case PNG:
				eximporter.setText("Portable Network Graphics (PNG)", false);
				break;
			case SAVEFILE_SAVE: case SAVEFILE_LOAD:
				eximporter.setText("FMT Save File (FMTB)", false);
				break;
			case HELPFRAMEIMG:
				eximporter.setText("Image File [PNG/JPG/JPEG]", false);
				break;
			default:
				eximporter.setText("Error, No Type Specified.", false);
				break;
		}
		root.x = x + 20; root.y = y + 54; root.setText(currdir.getPath(), false); File[] fls = getFilteredList();
		while(scroll + 12 > fls.length && scroll - 1 >= 0) scroll--;
		for(int i = 0; i < files.length; i++){
			files[i].x = x + 20; files[i].y = y + 82 + (i * 28); files[i].setEnabled(selected < 0 || selected != scroll + i);
			files[i].setTexOnly(files[i].isEnabled() ? "ui/background_light" : "ui/background_dark");
			if(scroll + i >= fls.length){ files[i].setVisible(false); }
			else{
				files[i].setVisible(true); files[i].setText(fls[scroll + i].getName() + (fls[scroll + i].isDirectory() ? "/" : ""), false);
			}
		}
		sel[0].setEnabled((selected > -1 && selected < fls.length && !fls[selected].isDirectory()) || (cfn.isEnabled() && this.isValidInput(cfn.getText())));
		exim_prev.x = x + 436; exim_prev.y = y + 429; exim_next.x = x + 464; exim_next.y = y + 429;
		FontRenderer.drawText(title, this.x + 22, this.y + 17, 1);
	}
	
	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		//
		return true;
	}
	
	/** 
	 * 
	 * @param png 
	 * @param b 
	 * @param text 0 - title, 1 - button0, 2 - button1, 3 - button2
	 * */
	public void show(String[] ntext, File otherroot, AfterTask after, ChooserMode mode){
		this.reset(); FMTB.get().reset(false); this.currdir = (otherroot == null ? SaveLoad.getRoot() : otherroot).getAbsoluteFile();
		//
		this.title = ntext[0]; this.visible = true; this.mode = mode;
		this.elements.forEach(elm -> { elm.setVisible(after != null); elm.setEnabled(after != null); });
		sel[0].setText(ntext.length < 2 || ntext[1] == null ? "OK" : ntext[1], true);
		sel[1].setText(ntext.length < 3 || ntext[2] == null ? "Suggested" : ntext[2], true);
		sel[2].setText(ntext.length < 4 || ntext[3] == null ? "Cancel" : ntext[3], true);
		sel[1].setEnabled(mode.exports() || mode.savefile_save()); cfn.setEnabled(sel[1].isEnabled()); this.onfile = after;
		if(cfn.isEnabled()) cfn.setText(DCFNFC[0] + " " + DCFNFC[1] + " " +  DCFNFC[2] + " " + DCFNFC[3], false);
		else cfn.setText("Please choose an existing file to proceed.", false);
		//
		Setting[] modesettings = mode.settings(); for(Setting setting : modesettings) this.settings.add(setting);
	}
	
	//DEFAULT_CUSTOM_FILE_NAME_FIELD_CONTENT 
	private static final String[] DCFNFC = new String[]{ "Choose a file", "to override or", "write a custom", "name here!" };
	
	public void reset(){
		this.onfile = null; this.currdir = SaveLoad.getRoot(); ressel(); eximscroll = 0; mode = ChooserMode.NONE;
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

	@Override
	public boolean visible(){
		return visible;
	}

}
