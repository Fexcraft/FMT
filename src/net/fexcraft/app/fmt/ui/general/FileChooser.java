/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.utils.Backups;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FileChooser extends Element implements Dialog {

	public static final AfterTask NOTHING = new AfterTask(){ @Override public void run(){ Print.console(file); return; }};
	private Element up, down, desktop, parent, reset, broot, eximp, eximm;
	private Button button0, button1, button2, root;
	private int scroll, eximscroll, selected = -1;
	private File currdir = SaveLoad.getRoot();
	private Button[] files = new Button[12];
	private String title = "No Title.";
	private AfterTask onfile = NOTHING;
	private TextField eximporter, cfn;
	private ChooserMode mode;
	
	public FileChooser(){
		super(null, "ui/filechooser"); TextureManager.loadTexture("ui/filechooser"); TextureManager.loadTexture("icons/file_chooser_0"); TextureManager.loadTexture("icons/file_chooser_1");
		TextureManager.loadTexture("icons/file_chooser_2"); TextureManager.loadTexture("icons/file_chooser_3"); TextureManager.loadTexture("icons/file_chooser_4");
		TextureManager.loadTexture("icons/file_chooser_5"); TextureManager.loadTexture("icons/file_chooser_6"); TextureManager.loadTexture("icons/file_chooser_7");
		this.visible = false; this.z = 80; this.height = 546; this.width = 512; Dialog.dialogs.add(this);
		this.elements.add(button0 = new Button(this, "button0", 150, 28, 18, 504/*470*/, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				onfile.porter = PorterManager.getPorters(mode.exports()).get(eximscroll);
				if(cfn.isEnabled() && isValidInput(cfn.getText())){
					onfile.file = new File(currdir, cfn.getText() + (cfn.getText().endsWith(getCurrentSelectedFileExtension(onfile.porter)) ? "" : getCurrentSelectedFileExtension(onfile.porter)));
				}
				else{
					if(selected < 0) return true; onfile.file = getFilteredList()[selected];
				}
				if(onfile.file != null){
					UserInterface.FILECHOOSER.visible = false;
					if((mode.exports() || mode.savefile_save()) && onfile.file.exists()){
						FMTB.showDialogbox("Override existing File?\n" + onfile.file.getName(), "yes", "no!", onfile, DialogBox.NOTHING);
						UserInterface.FILECHOOSER.reset(); return true;
					}
					else{
						onfile.run(); UserInterface.FILECHOOSER.reset(); return true;
					}
				}
				return true;
			}
			@Override
			public void hovered(int mx, int my){
				super.hovered(mx, my); if(hovered) cfn.onReturn();
			}
		});
		this.elements.add(button1 = new Button(this, "button1", 150, 28, 182, 504/*470*/, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				onfile.porter = PorterManager.getPorters(mode.exports()).get(eximscroll);
				String str = Backups.getSimpleDateFormat(true).format(Time.getDate()); UserInterface.FILECHOOSER.visible = false;
				String ext = getCurrentSelectedFileExtension(onfile.porter);
				onfile.file = new File(currdir, (FMTB.MODEL.name == null ? "unnamed" : FMTB.MODEL.name) + "-(" + str + ")" + ext);
				onfile.run(); UserInterface.FILECHOOSER.reset(); return true;
			}
		});
		this.elements.add(button2 = new Button(this, "button2", 150, 28, 346, 504/*470*/, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ UserInterface.FILECHOOSER.reset(); return true; }
		});
		this.elements.add((eximporter = new TextField(this, "eximporter", 442, 18, 440).setRenderBackground(false).setColor(RGB.BLACK)).setEnabled(false));
		this.elements.add(root = new Button(this, "fileroot", 436, 28, 29, 57, new RGB(200, 200, 200)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				if(currdir.getParentFile() != null) currdir = currdir.getParentFile().getAbsoluteFile(); ressel(); return true;
			}
		});
		for(int i = 0; i < files.length; i++){ int j = i;
			this.elements.add(files[i] = new Button(this, "files" + i, 430, 28, 29, 85 + (i * 28), new RGB(255, 255, 0), new RGB(128, 128, 255)){
				@Override protected boolean processButtonClick(int x, int y, boolean left){
					File[] fls = getFilteredList();
					if(fls[selected = scroll + j].isDirectory()){
						currdir = fls[selected]; ressel();
					} return true;
				}
				@Override public boolean onScrollWheel(int wheel){ scroll += wheel < 0 ? 8 : -8; if(scroll < 0) scroll = 0; return true; }
			});
		}
		//
		this.elements.add(broot = new Button(this, "root", 16, 16, 475, 57){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ currdir = SaveLoad.getRoot(); ressel(); return true; }
		}.setTexPosSize("icons/file_chooser_0", 0, 0, 16, 16));
		this.elements.add(reset = new Button(this, "reset", 16, 16, 475, 75){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ ressel(); return true; }
		}.setTexPosSize("icons/file_chooser_1", 0, 0, 16, 16));
		this.elements.add(parent = new Button(this, "parent", 16, 16, 475, 93){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				if(currdir.getParentFile() != null) currdir = currdir.getParentFile().getAbsoluteFile(); ressel(); return true;
			}
		}.setTexPosSize("icons/file_chooser_2", 0, 0, 16, 16));
		this.elements.add(desktop = new Button(this, "desktop", 16, 16, 475, 111){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				currdir = new File(System.getProperty("user.home") + "/Desktop").getAbsoluteFile(); ressel(); return true;
			}
		}.setTexPosSize("icons/file_chooser_3", 0, 0, 16, 16));
		this.elements.add(up = new Button(this, "up", 16, 16, 475, 387){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ scroll--; if(scroll < 0) scroll = 0; return true; }
		}.setTexPosSize("icons/file_chooser_4", 0, 0, 16, 16));
		this.elements.add(down = new Button(this, "down", 16, 16, 475, 405){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ scroll++; return true; }
		}.setTexPosSize("icons/file_chooser_5", 0, 0, 16, 16));
		this.elements.add(eximm = new Button(this, "exim-", 18, 28, 464, 436, new RGB(120, 120, 120)){
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
		}.setTexPosSize("icons/file_chooser_6", 0, 0, 16, 16));
		this.elements.add(eximp = new Button(this, "exim+", 18, 28, 482, 436, new RGB(120, 120, 120)){
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
		}.setTexPosSize("icons/file_chooser_7", 0, 0, 16, 16));
		this.elements.add(cfn = new TextField(this, "customfilename", 442, 18, 472).setRenderBackground(false).setColor(RGB.BLACK));
		//
		//this.show(new String[]{ "test title", "OK"}, null, NOTHING, false);
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
		button0.x = x +  18; button0.y = y + 504/*470*/; button1.x = x + 182; button1.y = y + 504/*470*/; button2.x = x + 346; button2.y = y + 504/*470*/;
		eximporter.x = x + 16; eximporter.y = y + 438; cfn.x = x + 16; cfn.y = y + 472;
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
		root.x = x + 29; root.y = y + 57; root.setText(currdir.getPath(), false); File[] fls = getFilteredList();
		while(scroll + 12 > fls.length && scroll - 1 >= 0) scroll--;
		for(int i = 0; i < files.length; i++){
			files[i].x = x + 29; files[i].y = y + 85 + (i * 28); files[i].setEnabled(selected < 0 || selected != scroll + i);
			files[i].setTexOnly(files[i].isEnabled() ? "ui/background_light" : "ui/background_dark");
			if(scroll + i >= fls.length){ files[i].setVisible(false); }
			else{
				files[i].setVisible(true); files[i].setText(fls[scroll + i].getName() + (fls[scroll + i].isDirectory() ? "/" : ""), false);
			}
		}
		button0.setEnabled((selected > -1 && selected < fls.length && !fls[selected].isDirectory()) || (cfn.isEnabled() && this.isValidInput(cfn.getText())));
		broot.x = 475 + x; broot.y = y + 57; reset.x = 475 + x; reset.y = y + 75;
		parent.x = 475 + x; parent.y = y + 93; desktop.x = 475 + x; desktop.y = y + 111;
		up.x = 475 + x; up.y = y + 387; down.x = 475 + x; down.y = y + 405;
		eximm.x = x + 464; eximm.y = y + 436; eximp.x = x + 482; eximp.y = y + 436;
		FontRenderer.drawText(title, this.x + 18, this.y + 19, 1);
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
		this.reset(); FMTB.get().reset(); this.currdir = (otherroot == null ? SaveLoad.getRoot() : otherroot).getAbsoluteFile();
		//
		this.title = ntext[0]; this.visible = true; this.mode = mode;
		this.elements.forEach(elm -> { elm.setVisible(after != null); elm.setEnabled(after != null); });
		button0.setText(ntext.length < 2 || ntext[1] == null ? "OK" : ntext[1], true);
		button1.setText(ntext.length < 3 || ntext[2] == null ? "Suggested" : ntext[2], true);
		button2.setText(ntext.length < 4 || ntext[3] == null ? "Cancel" : ntext[3], true);
		button1.setEnabled(mode.exports() || mode.savefile_save()); cfn.setEnabled(button1.isEnabled()); this.onfile = after;
		if(cfn.isEnabled()) cfn.setText(DCFNFC[0] + " " + DCFNFC[1] + " " +  DCFNFC[2] + " " + DCFNFC[3], false);
		else cfn.setText("Please choose an existing file to proceed.", false);
	}
	
	//DEFAULT_CUSTOM_FILE_NAME_FIELD_CONTENT 
	private static final String[] DCFNFC = new String[]{ "Choose a file", "to override or", "write a custom", "name here!" };
	
	public void reset(){
		this.onfile = null; this.currdir = SaveLoad.getRoot(); ressel(); eximscroll = 0; mode = ChooserMode.NONE;
		button0.setText(null, false); button1.setText(null, false); button2.setText(null, false); visible = false;
	}
	
	public static abstract class AfterTask implements Runnable { public File file; public ExImPorter porter; }
	
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
	}

	@Override
	public boolean visible(){
		return visible;
	}

}
