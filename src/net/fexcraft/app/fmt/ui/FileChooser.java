package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Field.FieldType;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.math.RGB;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FileChooser extends Frame {

	public static FileType TYPE_PNG = new FileType("Portable Network Graphics", "*.png");
	public static FileType TYPE_IMG = new FileType("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp");
	public static FileType TYPE_FMTB = new FileType("FMT Save File", "*.fmtb");
	public static FileType TYPE_JSON = new FileType("JSON", "*.json");
	public static FileType TYPE_JARZIP = new FileType("Jar/Zip Archive", "*.jar", "*.zip");
	public static FileType TYPE_FMF = new FileType("FEXs Model Format", "*.fmf");
	public static FileType TYPE_BOB = new FileType("Byte Encoded Object", "*.bob", "*.beo");
	public static FileType TYPE_OBJ = new FileType("Wavefront Object (obj)", "*.obj");
	public static FileType TYPE_BBM = new FileType("BlockBench Model", "*.bbmodel");
	public static FileType TYPE_JAVA = new FileType("Java Model", "*.java");
	public static FileType TYPE_ANY = new FileType("Any", "*.*");
	//
	private static FileChooser instance;
	private static int width = 800, bmw = 300;
	//
	private File root;
	private FileType type;
	private Consumer<File> cons;
	private boolean save;
	//
	private Scrollable fileview;
	private Scrollable bookmarks;
	private Element dir_name;
	private Field selected;
	private File sel_file;

	public FileChooser(File root, FileType type, boolean save, Consumer<File> cons){
		super();
		this.root = root;
		this.type = type;
		this.save = save;
		this.cons = cons;
		size(width, 500);
		pos((FMT.SCALED_WIDTH - w) * 0.5f, (FMT.SCALED_HEIGHT - h) * 0.5f);
		color(GENERIC_BACKGROUND_0.value);
		border(RGB.BLACK);
		if(instance != null) FMT.UI.remElm(instance);
		instance = this;
	}

	@Override
	public void init(Object... args){
		float ls = w - bmw;
		add(new Element().size(ls, 30).color(GENERIC_BACKGROUND_1.value).translate(args[0].toString()));
		add(new Element().size(bmw, 30).pos(ls, 0).color(GENERIC_BACKGROUND_1.value).translate("filechooser.bookmarks"));
		add(fileview = new Scrollable(true, 0));
		add(bookmarks = new Scrollable(true, 0));
		fileview.updateSize(ls, h - 100);
		fileview.pos(0, 60).border(GENERIC_BACKGROUND_2.value);
		bookmarks.updateSize(bmw - 1, h - 70);
		bookmarks.pos(ls, 30).border(GENERIC_BACKGROUND_2.value);
		add(dir_name = new Element().pos(5, 31).size(ls - 40, FS).text(root.toString()).color(GENERIC_FIELD.value)
			.onclick(ci -> setDir(root.getAbsoluteFile().getParentFile())));
		add(new Element().pos(ls - 30, 31).size(FS, FS).texture("icons/toolbar/save")
			.onclick(ci -> addBookmark()).hint("filechooser.save_bookmark"));
		add((selected = new Field(save ? FieldType.TEXT : FieldType.INFO, w - 120)).pos(5, h - 35).size(ls - 10, 30)
			.text(root.toString()).color(GENERIC_FIELD.value));
		fillFileView();
		fillBookmarks();
		add(new RunElm(w - 210, h - 35, 100, "dialog.button.cancel", ci -> close())
			.text_centered(true).size(100, 30));
		add(new RunElm(w - 105, h - 35, 100, "dialog.button." + (save ? "save" : "open"), ci -> {
			close();
			if(save){
				String name = selected.get_filename();
				boolean suf = false;
				for(String ext : type.extensions){
					if(name.endsWith(ext.replace("*", ""))){
						suf = true;
						break;
					}
				}
				if(!suf) name += type.extensions[0].replace("*", "");
				cons.accept(new File(root, name));
			}
			else{
				cons.accept(sel_file);
			}
		}).text_centered(true).size(100, 30));
	}

	private void addBookmark(){
		Settings.BOOKMARKS.add(root);
		fillBookmarks();
	}

	private void fillBookmarks(){
		bookmarks.clear();
		for(File bm : BOOKMARKS){
			bookmarks.add(new Element().pos(5, 0).size(bmw - 30, FS).hoverable(true)
				.text(bm.getName()).color(GENERIC_FIELD.value).check_mode(CheckMode.IN_ROOT)
				.onclick(cons -> setDir(bm)).hint(bm.getAbsolutePath()));
			bookmarks.lastElement().add(new HidingElm().size(FS - 2, FS - 2).pos(bmw - 55, 1)
				.texture("icons/component/remove").onclick(ci -> {
					BOOKMARKS.remove(bm);
					fillBookmarks();
				}).hint("filechooser.remove_bookmark"));
		}
		bookmarks.updateBar();
	}

	private void setDir(File file){
		root = file;
		dir_name.text(root.toString());
		fillFileView();
	}

	private void fillFileView(){
		fileview.clear();
		ArrayList<File> filtered = new ArrayList<>();
		if(root != null && root.listFiles() != null){
			for(File file : root.listFiles()){
				if(file.isDirectory()){
					filtered.add(file);
					continue;
				}
				for(int i = 0; i < type.extensions.length; i++){
					if(file.getName().endsWith(type.extensions[i].replace("*", ""))){
						filtered.add(file);
					}
				}
			}
		}
		else{
			filtered.add(new File("error.no_files_here"));
		}
		filtered.sort((a, b) -> {
			if(a.isDirectory() && !b.isDirectory()) return -1;
			if(b.isDirectory() && !a.isDirectory()) return 1;
			return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
		});
		for(File file : filtered){
			fileview.add(new Element().pos(5, 0).size(fileview.w - 30, FS).hoverable(true)
				.text(file.getName() + (file.isDirectory() ? "/" : "")).color(GENERIC_FIELD.value)
				.check_mode(CheckMode.IN_ROOT).onclick(cons -> {
					if(file.isDirectory()) setDir(file);
					else{
						sel_file = file;
						selected.text(file.getName());
					}
				}));
		}
		fileview.updateBar();
	}

	private void close(){
		FMT.UI.remElm(this);
		instance = null;
	}

	public static void choose(String title, File root, FileType type, boolean save, Consumer<File> cons){
		if(root == null) root = FMT.WORKSPACE.root_folder;
		if(Settings.INTERNAL_CHOOSER.value){
			FMT.UI.add(new FileChooser(root, type, save, cons), title);
			return;
		}
		try(MemoryStack stack = MemoryStack.stackPush()){
			title = Translator.translate(title);
			PointerBuffer buffer = stack.mallocPointer(type.extensions.length);
			String string = "";
			for(int i = 0; i < type.extensions.length; i++){
				buffer.put(stack.UTF8(type.extensions[i]));
			}
			buffer.flip();
			if(save) string = TinyFileDialogs.tinyfd_saveFileDialog(title, root.toPath().toString(), buffer, type.name);
			else string = TinyFileDialogs.tinyfd_openFileDialog(title, root.toPath().toString(), buffer, type.name, false);
			if(string != null && string.trim().length() > 0){
				if(type == TYPE_ANY){
					cons.accept(new File(string));
					return;
				}
				boolean ends = false;
				for(int i = 0; i < type.extensions.length; i++){
					if(string.endsWith(type.extensions[i].replace("*", ""))){
						ends = true;
						break;
					}
				}
				if(!ends) string += type.extensions[0].replace("*", "");
				cons.accept(new File(string));
			}
			else cons.accept(null);
		}
	}

	public static void chooseFolder(String title, File root, Consumer<File> task){
		if(root == null) root = FMT.WORKSPACE.root_folder;
		if(INTERNAL_CHOOSER.value){
			FMT.UI.createDialog(400, 80, null).addText(0, "not-available-yet").buttons(100, Dialog.DialogButton.OK);
			return;
		}
		task.accept(new File(TinyFileDialogs.tinyfd_selectFolderDialog(title, root.getPath().toString())));
	}

	public static class FileType {

		private String name;
		public String[] extensions;

		public FileType(String name, String... extensions){
			this.name = name;
			this.extensions = extensions;
		}

		public boolean supports(File file){
			for(String str : extensions){
				if(file.getName().endsWith(str.replace("*", ""))) return true;
			}
			return false;
		}

	}

}
