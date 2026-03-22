package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;

import java.util.function.Supplier;

import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class BoolElm extends Element {

	private static String[] tex40 = new String[]{ "oui/bool_40_false", "oui/bool_40_true" };
	private Supplier<Boolean> supplier;
	private PolygonValue polyval;
	private String[] texar;

	public BoolElm(float x, float y, float w){
		pos(x, y);
		size(w, FS);
		if(w == 40){
			texar = tex40;
		}
	}

	public BoolElm set(PolygonValue val, UpdateCompound updcom){
		polyval = val;
		updcom.add(UpdateEvent.PolygonValueEvent.class, event -> {
			if(!event.first()) return;
			if(event.value().equals(val)){
				texture(texar[event.polygon().getValue(val) > 0 ? 1 : 0]);
			}
		});
		updcom.add(UpdateEvent.PolygonSelected.class, event -> {
			if(event.prevselected() < 0) return;
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() == 0)){
				texture(texar[FMT.MODEL.first_selected().getValue(val) > 0 ? 1 : 0]);
			}
			else if(event.selected() == 0) texture(texar[0]);
		});
		updcom.add(UpdateEvent.GroupSelected.class, event -> {
			if(event.prevselected() < 0) return;
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() == 0 && FMT.MODEL.first_selected() != null)){
				texture(texar[FMT.MODEL.first_selected().getValue(val) > 0 ? 1 : 0]);
			}
			else if(event.selected() == 0) texture(texar[0]);
		});
		onclick(ci -> toggleBool(null));
		onscroll(si -> toggleBool(si.sy() > 0));
		return this;
	}

	@Override
	public void init(Object... args){
		texture(texar[bool() ? 1 : 0]);
	}

	public boolean bool(){
		if(polyval != null){
			if(FMT.MODEL.first_selected() == null) return false;
			return FMT.MODEL.first_selected().getValue(polyval) > 0;
		}
		if(supplier != null){
			return supplier.get();
		}
		return false;
	}

	private void toggleBool(Boolean input){
		int bool = (input == null ? !bool() : input) ? 1 : 0;
		texture(texar[bool]);
		FMT.MODEL.updateValue(polyval, (Field)null, bool, true);
	}

}
