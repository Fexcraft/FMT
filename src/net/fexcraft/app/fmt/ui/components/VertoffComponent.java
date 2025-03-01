package net.fexcraft.app.fmt.ui.components;

import com.spinyowl.legui.component.Label;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.Vertoff;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.PosCopyIcon;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.ValAxe;
import net.fexcraft.app.fmt.update.UpdateEvent.VertexSelected;
import org.apache.commons.lang3.tuple.Pair;

import static net.fexcraft.app.fmt.utils.Translator.translate;

public class VertoffComponent extends EditorComponent {

	private TextField key;
	private NumberField sel;
	private NumberField ox, oy, oz;

	public VertoffComponent(){
		super("vertex", 140, false, true);
		add(new Label(translate(LANG_PREFIX + id + ".index"), L5, row(1), LWI, HEIGHT));
		add(new PosCopyIcon(LPI, row(0) + 4, PolyVal.OFF));
		add(key = new TextField("", F20, row(1), F2S, HEIGHT));
		add(sel = new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, false, cons -> {}));
		add(new Label(translate(LANG_PREFIX + id + ".offset"), L5, row(1), LW, HEIGHT));
		add(ox = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> apply(cons.value(), ValAxe.X)));
		add(oy = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> apply(cons.value(), ValAxe.Y)));
		add(oz = new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> apply(cons.value(), ValAxe.Z)));
		updcom.add(VertexSelected.class, e -> {
			sel.apply(e.selected());
			if(e.selected() <= 0){
				key.getTextState().setText("");
				ox.apply(0);
				oy.apply(0);
				oz.apply(0);
			}
			else{
				Vertoff vo = e.pair().getLeft().vertoffs.get(e.pair().getRight());
				key.getTextState().setText(e.pair().getRight().toString());
				ox.apply(vo.off.x);
				oy.apply(vo.off.y);
				oz.apply(vo.off.z);
			}
		});
	}

	private void apply(float value, ValAxe a){
		Pair<Polygon, Vertoff.VOKey> pair = FMT.MODEL.getSelectedVerts().get(0);
		Vertoff vo = pair.getLeft().vertoffs.get(pair.getRight());
		switch(a){
			case X -> vo.off.x = value;
			case Y -> vo.off.y = value;
			case Z -> vo.off.z = value;
		}
		pair.getLeft().recompile();
	}

}
