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

	private static String[] tex40 = new String[]{ "ui/bool_40_false", "ui/bool_40_true" };
	private static int COL_TRUE = 0x38baf3, COL_FALSE = 0xf5a723;
	private Supplier<Boolean> supplier;
	private PolygonValue polyval;
	private String[] texar;

	public BoolElm(float x, float y, float w){
		pos(x, y);
		size(w, FS);
		if(w == 40){
			texar = tex40;
		}
		else{
			text("");
			text_centered(true);
		}
	}

	public BoolElm set(PolygonValue val, UpdateCompound updcom){
		polyval = val;
		updcom.add(UpdateEvent.PolygonValueEvent.class, event -> {
			if(!event.first()) return;
			updtexcol();
		});
		updcom.add(UpdateEvent.PolygonSelected.class, event -> updtexcol());
		updcom.add(UpdateEvent.GroupSelected.class, event -> updtexcol());
		onclick(ci -> toggleBool(null));
		onscroll(si -> toggleBool(si.sy() > 0));
		return this;
	}

	@Override
	public void init(Object... args){
		updtexcol();
	}

	public void updtexcol(){
		updtexcol(null);
	}

	public void updtexcol(Boolean b){
		b = b == null ? bool() : b;
		if(texar != null){
			texture(texar[b ? 1 : 0]);
		}
		else{
			color(b ? COL_TRUE : COL_FALSE);
			text(b);
		}
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
		updtexcol(bool > 0);
		FMT.MODEL.updateValue(polyval, (Field)null, bool, true);
	}

	public PolygonValue polyval(){
		return polyval;
	}

}
