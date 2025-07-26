package net.fexcraft.app.fmt.ui;

import java.util.HashMap;

import net.fexcraft.app.fmt.ui.editors.PolygonEditor;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonMap;

public class EditorComponent extends Component {

	public static final float F20 = 8, F21 = 154f, F2S = 138;
	public static final float F30 = 7.5f, F31 = 105f, F32 = 202.5f, F3S = 90;
	public static final float F40 = 8f, F41 = 79f, F42 = 150f, F43 = 221, F4S = 65;
	public static final float F60 = 6, F61 = 55, F62 = 104, F63 = 153, F64 = 202, F65 = 251, F6S = 43;
	public static final float L5 = 5f;
	public static final float LW = Editor.CWIDTH - (L5 * 2);
	public static final float LWI = LW - 20;
	public static final float LWS = LW - 10;
	public static final float LPI = Editor.CWIDTH - 24;
	public static final int HEIGHT = 24;
	//
	public static final HashMap<Integer, EditorComponent> COMPONENTS = new HashMap<>();
	public static final String LANG_PREFIX = "editor.component.";
	protected UpdateCompound updcom = new UpdateCompound();
	protected boolean minimized, unpinned, tree;
	protected Label label;
	protected Icon size;
	protected int uid;
	protected int fullheight;
	protected int minheight = HEIGHT;
	protected int row;
	public Editor editor;
	protected String id;
	public int index;
	
	public EditorComponent(String key){
		this(key, 0, 0, false, true);
	}

	public EditorComponent(String key, int fullHeight, boolean tree, boolean resizeable){
		this(key, 0, fullHeight, tree, resizeable);
	}
	
	public EditorComponent(String key, int subwidth, int fullHeight, boolean tree, boolean resizeable){
		while(COMPONENTS.containsKey(uid)) uid++;
		setSize(Editor.CWIDTH - subwidth, fullheight = fullHeight > 0 ? fullHeight : HEIGHT * 2);
		add(label = new Label(Translator.translate(LANG_PREFIX + (id = key) + ".name"), 4, 0, 296, 24));
		label.getStyle().setFontSize(18f);
		Settings.applyComponentTheme(this);
		add(size = new Icon(this, 1, "./resources/textures/icons/component/" + (tree ? "minimize" : "size") + ".png", () -> minimize(null)));
		if(!resizeable) size.getStyle().setDisplay(DisplayType.NONE);
	}

	public void minimize(Boolean bool){
		this.minimized = bool == null ? !minimized : bool;
		setSize(getSize().x, minimized ? PolygonEditor.shrink(this) ? 0 : minheight : fullheight);
		if(editor != null) editor.alignComponents();
	}

	protected boolean move(int dir){
		if(unpinned) return false;
		int nidx = index + dir;
		if(nidx < 0 || nidx >= editor.components.size()) return false;
		editor.swap(index, index + dir);
		return true;
	}

	public UpdateCompound getUpdCom(){
		return updcom;
	}

	protected void pin(){
		
	}

	protected void rem(){
		if(editor == null) return;
		editor.removeComponent(this);
	}
	
	public int row(int next){
		if(next > 0) row += next * 25;
		return row;
	}
	
	public int row(){
		return row;
	}

	public EditorComponent load(JsonMap map){
		return this;
	}

	public JsonMap save(){
		return null;
	}

	public boolean minimized(){
		return minimized;
	}

}
