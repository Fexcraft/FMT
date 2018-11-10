/**
 * 
 */
package net.fexcraft.app.fmt.ui.generic;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.Element;
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
public class FileChooser extends Element {

	public static final AfterTask NOTHING = new AfterTask(){ @Override public void run(){ Print.console(file); return; }};
	private Button up, down, desktop, parent, reset, broot, eximp, eximm;
	private Button button0, button1, button2, root;
	private int scroll, eximscroll, selected = -1;
	private File currdir = SaveLoad.getRoot();
	private Button[] files = new Button[12];
	private String title = "No Title.";
	private AfterTask onfile = NOTHING;
	private TextField eximporter;
	private boolean export, png;
	
	public FileChooser(){
		super(null, "ui/filechooser"); TextureManager.loadTexture("ui/filechooser"); TextureManager.loadTexture("icons/file_chooser_0"); TextureManager.loadTexture("icons/file_chooser_1");
		TextureManager.loadTexture("icons/file_chooser_2"); TextureManager.loadTexture("icons/file_chooser_3"); TextureManager.loadTexture("icons/file_chooser_4");
		TextureManager.loadTexture("icons/file_chooser_5"); TextureManager.loadTexture("icons/file_chooser_6"); TextureManager.loadTexture("icons/file_chooser_7");
		this.visible = false; this.z = 80; this.height = this.width = 512;
		this.elements.put("button0", button0 = new Button(this, "button0", 150, 28, 18, 470, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				if(selected < 0) return true; UserInterface.FILECHOOSER.visible = false;
				onfile.file = getFilteredList()[selected]; onfile.porter = PorterManager.getPorters(export).get(eximscroll);
				//FMTB.showDialogbox(export ? "Exporting..." : "Importing...", "Please wait.", "ok!", null, DialogBox.NOTHING, null);
				onfile.run(); UserInterface.FILECHOOSER.reset(); return true;
			}
		});
		this.elements.put("button1", button1 = new Button(this, "button1", 150, 28, 182, 470, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				onfile.porter = PorterManager.getPorters(export).get(eximscroll);
				String str = Backups.getSimpleDateFormat(true).format(Time.getDate()); UserInterface.FILECHOOSER.visible = false;
				String ext = onfile.porter.getExtensions()[0].startsWith(".") ? onfile.porter.getExtensions()[0] : "." + onfile.porter.getExtensions()[0];
				onfile.file = new File(currdir, (FMTB.MODEL.name == null ? "unnamed" : FMTB.MODEL.name) + "-(" + str + ")" + ext);
				//FMTB.showDialogbox(export ? "Exporting..." : "Importing...", "Please wait.", "ok!", null, DialogBox.NOTHING, null);
				onfile.run(); UserInterface.FILECHOOSER.reset(); return true;
			}
		});
		this.elements.put("button2", button2 = new Button(this, "button2", 150, 28, 346, 470, new RGB(255, 255, 0)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ UserInterface.FILECHOOSER.reset(); return true; }
		});
		this.elements.put("eximporter", eximporter = new TextField(this, "eximporter", 18, 440, 442).setRenderBackground(false).setColor(Color.black));
		this.elements.put("fileroot", root = new Button(this, "fileroot", 436, 28, 29, 57, new RGB(200, 200, 200)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				if(currdir.getParentFile() != null) currdir = currdir.getParentFile().getAbsoluteFile(); ressel(); return true;
			}
		});
		for(int i = 0; i < files.length; i++){ int j = i;
			this.elements.put("files" + i, files[i] = new Button(this, "files" + i, 430, 28, 29, 85 + (i * 28), new RGB(255, 255, 0), new RGB(128, 128, 255)){
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
		this.elements.put("root", broot = new Button(this, "root", 16, 16, 475, 57){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ currdir = SaveLoad.getRoot(); ressel(); return true; }
		}.setTexture("icons/file_chooser_0"));
		this.elements.put("reset", reset = new Button(this, "reset", 16, 16, 475, 75){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ ressel(); return true; }
		}.setTexture("icons/file_chooser_1"));
		this.elements.put("parent", parent = new Button(this, "parent", 16, 16, 475, 93){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				if(currdir.getParentFile() != null) currdir = currdir.getParentFile().getAbsoluteFile(); ressel(); return true;
			}
		}.setTexture("icons/file_chooser_2"));
		this.elements.put("dektop", desktop = new Button(this, "desktop", 16, 16, 475, 111){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				currdir = new File(System.getProperty("user.home") + "/Desktop").getAbsoluteFile(); ressel(); return true;
			}
		}.setTexture("icons/file_chooser_3"));
		this.elements.put("up", up = new Button(this, "up", 16, 16, 475, 387){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ scroll--; if(scroll < 0) scroll = 0; return true; }
		}.setTexture("icons/file_chooser_4"));
		this.elements.put("down", down = new Button(this, "down", 16, 16, 475, 405){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ scroll++; return true; }
		}.setTexture("icons/file_chooser_5"));
		this.elements.put("exim-", eximm = new Button(this, "exim-", 18, 28, 464, 436, new RGB(120, 120, 120)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ if(png) return true; eximscroll++; if(eximscroll >= PorterManager.getPorters(export).size()) eximscroll = 0; ressel(); return true; }
		}.setTexture("icons/file_chooser_6"));
		this.elements.put("exim+", eximp = new Button(this, "exim+", 18, 28, 482, 436, new RGB(120, 120, 120)){
			@Override protected boolean processButtonClick(int x, int y, boolean left){ if(png) return true; eximscroll--; if(eximscroll < 0) eximscroll = PorterManager.getPorters(export).size() - 1; ressel(); return true; }
		}.setTexture("icons/file_chooser_7"));
		//
		//this.show(new String[]{ "test title", "OK"}, null, NOTHING, false);
	}
	
	private void ressel(){
		this.selected = -1; this.scroll = 0;
	}
	
	private static final File NONE = new File("no files in directory"), ERROR = new File("error.jvm (see console)");
	private static Stream<File> stream;
	
	private File[] getFilteredList(){
		try{
			if(currdir.listFiles() == null) return new File[]{ NONE };
			if(png){
				stream = Arrays.asList(currdir.listFiles()).stream().filter(pre -> pre.getName().toLowerCase().endsWith(".png"));
			}
			else{
				ExImPorter porter = PorterManager.getPorters(export).get(eximscroll);
				stream = Arrays.asList(currdir.listFiles()).stream().filter(pre -> porter.isValidFile(pre));
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
		button0.x = x +  18; button0.y = y + 470; button1.x = x + 182; button1.y = y + 470; button2.x = x + 346; button2.y = y + 470;
		eximporter.x = x + 16; eximporter.y = y + 438; eximporter.setText(png ? "Portable Network Graphics (PNG)" : (export ? "Exporter: " : "Importer: ") + PorterManager.getPorters(export).get(eximscroll).getName(), false);
		root.x = x + 29; root.y = y + 57; root.setText(currdir.getPath(), false); File[] fls = getFilteredList();
		for(int i = 0; i < files.length; i++){
			files[i].x = x + 29; files[i].y = y + 85 + (i * 28); files[i].enabled = selected < 0 || selected != scroll + i;
			files[i].setTexture(files[i].enabled ? "ui/button_bg" : "ui/background");
			if(scroll + i >= fls.length){ files[i].visible = false; }
			else{
				files[i].visible = true; files[i].setText(fls[scroll + i].getName(), false);
			}
		}
		button0.enabled = selected > -1 && selected < fls.length && !fls[selected].isDirectory();
		broot.x = 475 + x; broot.y = y + 57; reset.x = 475 + x; reset.y = y + 75;
		parent.x = 475 + x; parent.y = y + 93; desktop.x = 475 + x; desktop.y = y + 111;
		up.x = 475 + x; up.y = y + 387; down.x = 475 + x; down.y = y + 405;
		eximm.x = x + 464; eximm.y = y + 436; eximp.x = x + 482; eximp.y = y + 436;
		TextureManager.unbind(); font.drawString(this.x + 18, this.y + 19, title, Color.black); RGB.glColorReset();
	}
	
	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		//
		return false;
	}
	
	/** 
	 * 
	 * @param png 
	 * @param text 0 - title, 1 - button0, 2 - button1, 3 - button2
	 * */
	public void show(String[] ntext, File otherroot, AfterTask after, boolean export, boolean png){
		this.reset(); this.currdir = (otherroot == null ? SaveLoad.getRoot() : otherroot).getAbsoluteFile();
		//
		this.title = ntext[0]; this.visible = true; this.export = export;
		this.elements.values().forEach(elm -> elm.visible = elm.enabled = after != null);
		button0.setText(ntext.length < 2 || ntext[1] == null ? "OK" : ntext[1], true);
		button1.setText(ntext.length < 3 || ntext[2] == null ? "Suggested" : ntext[2], true);
		button2.setText(ntext.length < 4 || ntext[3] == null ? "Cancel" : ntext[3], true);
		button1.enabled = export; this.onfile = after; this.png = png;
	}
	
	public void reset(){
		this.onfile = null; this.currdir = SaveLoad.getRoot(); ressel(); eximscroll = 0;
		button0.setText(null, false); button1.setText(null, false); button2.setText(null, false); visible = false;
	}
	
	public static abstract class AfterTask implements Runnable { public File file; public ExImPorter porter; }

}
