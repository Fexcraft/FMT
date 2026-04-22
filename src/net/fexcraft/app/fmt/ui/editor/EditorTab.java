package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Scrollable;
import net.fexcraft.app.fmt.ui.editor.EditorRoot.EditorMode;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.json.JsonMap;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_0;
import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorTab extends Element {

	public final String lang_prefix;
	public final EditorMode mode;
	//
	public static float FF = EDITOR_CONTENT - 10;
	public static float FO = 5;//field offset
	//
	public static float F2S = 137;//2-row field width
	public static float F20 = 5;//
	public static float F21 = 148;//
	//
	public static float F3S = 90;//3-row field width
	public static float F30 = 5;//3-row 1st field offset
	public static float F31 = 100;//3-row 2nd field offset
	public static float F32 = 195;//3-row 3rd field offset
	//
	public static float F4S = 64;//4-row field width
	public static float F40 = 5;//
	public static float F41 = 77;//
	public static float F42 = 149;//
	public static float F43 = 221;//
	//
	public static float F6S = 40;//6-row field width
	public static float F60 = 5;
	public static float F61 = 53;
	public static float F62 = 101;
	public static float F63 = 149;
	public static float F64 = 197;
	public static float F65 = 245;
	//
	public static float FS = 26;//field height
	//
	protected UpdateCompound updcom = new UpdateCompound();
	protected Scrollable container;
	private int next_y_elm_pos = 0;

	public EditorTab(EditorMode emode){
		super();
		mode = emode;
		lang_prefix = "editor." + emode.name().toLowerCase() + ".";
		color(GENERIC_BACKGROUND_0.value);
		resize();
	}

	public static EditorTab create(EditorMode mode){
		switch(mode){
			case POLYGON: return new PolygonEditorTab();
			case GROUP: return new GroupEditorTab();
			case PIVOT: return new PivotEditorTab();
			case MODEL: return new ModelEditorTab();
			case TEXTURE: return new UVEditorTab();
			case PAINTER: return new PainterEditorTab();
			case PREVIEW: return new PreviewEditorTab();
		}
		return new EditorTab(mode);
	}

	@Override
	public void init(Object... args){
		add((container = new Scrollable(true, 0)));
		container.updateSize(w, h);
	}

	@Override
	public void onResize(){
		size(EDITOR_WIDTH, FMT.SCALED_HEIGHT - TOOLBAR_HEIGHT);
		if(container == null) return;
		container.updateSize(w, h);
	}

	public int next_y_pos(float inc){
		if(inc < 0) return next_y_elm_pos = 30;
		return next_y_elm_pos += (int)(inc * 30);
	}

	public void reorderComponents(){
		container.updateBar();
	}

	public void load(JsonMap map){
		if(map.has("minimized")){
			JsonMap min = map.getMap("minimized");
			for(Element elm : container.elements){
				if(elm instanceof ETabCom com){
					com.minimized = min.getBoolean(com.id, false);
					if(com.minimized) com.hide();
					else com.show();
				}
			}
		}
	}

	public JsonMap save(){
		JsonMap map = new JsonMap();
		JsonMap min = new JsonMap();
		for(Element elm : container.elements){
			if(elm instanceof ETabCom com){
				min.add(com.id, com.minimized);
			}
		}
		if(min.not_empty()) map.add("minimized", min);
		return map;
	}

}
