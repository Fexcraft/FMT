package net.fexcraft.app.fmt.ui.components;

import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.*;
import net.fexcraft.app.fmt.utils.fvtm.VehAttr;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DynAttrComponent extends EditorComponent {

	public DynAttrComponent(String key, VehAttr attr){
		super("variable.dynamic", 60, false, true);
		label.getTextState().setText(key);
		switch(attr.type){
			case STRING:{
				add(new TextField(attr.value.toString(), L5, row(1), LW, HEIGHT, false).accept(text -> {
					attr.value = text;
				}));
				break;
			}
			case BOOL:{
				add(new BoolButton(L5, row(1), LW, HEIGHT, (boolean)attr.value, bool -> {
					attr.value = bool;
				}));
				break;
			}
			case TRISTATE:{
				add(new RunButton("true", F30, row(1), F3S, HEIGHT, () -> attr.value = true));
				add(new RunButton("false", F30, row(0), F3S, HEIGHT, () -> attr.value = false));
				add(new RunButton("null", F30, row(0), F3S, HEIGHT, () -> attr.value = null));
				break;
			}
			case INT:
			case FLOAT:
			case LONG:{
				add(new NumberField(this, L5, row(1), LW, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, attr.type == VehAttr.Type.FLOAT, cons -> {
					if(attr.type == VehAttr.Type.FLOAT) attr.value = cons.value();
					if(attr.type == VehAttr.Type.INT) attr.value = (int)cons.value();
					if(attr.type == VehAttr.Type.LONG) attr.value = (long)cons.value();
				}).apply(((Number)attr.value).floatValue()));
				break;
			}
		}
	}

}
