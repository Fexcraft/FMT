package net.fexcraft.app.fmt.ui.components;

import net.fexcraft.app.fmt.animation.Animation;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.fvtm.FvtmTypes;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DynAnimComponent extends EditorComponent {

	public DynAnimComponent(Animation anim, FvtmTypes.ProgRef ref, int index){
		super("animation.dynamic", 60, false, true);
		String[] args = ref.args()[index].split(":");
		boolean num = args[1].equals("i") || args[1].equals("f");
		float min = num && args.length > 2 ? Float.parseFloat(args[2]) : Integer.MIN_VALUE;
		float max = num && args.length > 3 ? Float.parseFloat(args[3]) : Integer.MAX_VALUE;
		label.getTextState().setText(args[0] + (min != Integer.MIN_VALUE || max != Integer.MAX_VALUE ? " (" + min + " ~ " + max + ")" : ""));
		switch(args[1]){
			case "s":{
				add(new TextField(anim.get(args[0]) + "", L5, row(1), LW, HEIGHT, false).accept(text -> {
					anim.set(args[0], text);
				}));
				break;
			}
			case "b":{
				add(new BoolButton(L5, row(1), LW, HEIGHT, (boolean)anim.get(args[0]), bool -> {
					anim.set(args[0], bool);
				}));
				break;
			}
			case "i":
			case "f":{
				add(new NumberField(this, L5, row(1), LW, HEIGHT).setup(min, max, args[1].equals("f"), cons -> {
					anim.set(args[0], cons.value());
				}).apply(((Number)anim.get(args[0])).floatValue()));
				break;
			}
			case "c":{
				add(new ColorField(this, (c, b) -> anim.set(args[0], c), L5, row(1), LW, HEIGHT, "./resources/textures/icons/painter/palette.png").apply((float)anim.get(args[0])));
				break;
			}
		}
	}

}
