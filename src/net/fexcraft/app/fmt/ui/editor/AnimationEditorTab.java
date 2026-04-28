package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.animation.Animation;
import net.fexcraft.app.fmt.ui.BoolElm;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.ui.TextElm;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.utils.fvtm.FvtmTypes;
import net.fexcraft.lib.common.math.V3D;

import static net.fexcraft.app.fmt.ui.Field.FieldType.*;
import static net.fexcraft.app.fmt.ui.editor.EditorRoot.NOANIMATIONSEL;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AnimationEditorTab extends EditorTab {

	public AnimationEditorTab(){
		super(EditorRoot.EditorMode.ANIMATION);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		updcom.add(UpdateEvent.ModelLoad.class, e -> refill(null));
		updcom.add(UpdateEvent.ModelUnload.class, e -> refill(null));
		updcom.add(UpdateEvent.GroupAnimationSelected.class, e -> refill(e.anim()));
		refill(null);
	}

	private void refill(Animation anim){
		container.remElmIf(elm -> elm instanceof ETabCom);
		addAnimCom(anim);
		reorderComponents();
	}

	private void addAnimCom(Animation anim){
		ETabCom com = new ETabCom("general");
		next_y_elm_pos = 0;
		if(anim == null){
			container.add(com, "editor.animation.general", 65);
			com.add(new TextElm(FO, next_y_pos(1), FF, NOANIMATIONSEL));
			return;
		}
		FvtmTypes.ProgRef ref = FvtmTypes.getProgRef(anim.id());
		container.add(com, "editor.animation.general", ref.args().length * 60 + 35);
		Field field;
		for(String arg : ref.args()){
			String[] args = arg.split(":");
			boolean num = args[1].equals("i") || args[1].equals("f");
			float min = num && args.length > 2 ? Float.parseFloat(args[2]) : Integer.MIN_VALUE;
			float max = num && args.length > 3 ? Float.parseFloat(args[3]) : Integer.MAX_VALUE;
			String info = args[0] + (min != Integer.MIN_VALUE || max != Integer.MAX_VALUE ? " (" + min + " ~ " + max + ")" : "");
			com.add(new TextElm(FO, next_y_pos(1), FF, info));
			switch(args[1]){
				case "s":{
					com.add((field = new Field(TEXT, FF)).consumer(f -> {
						anim.set(args[0], f.get_text());
					}).pos(FO, next_y_pos(1)));
					field.text(anim.get(args[0]));
					break;
				}
				case "b":{
					com.add(new BoolElm(FO, next_y_pos(1), FF)
						.set(() -> (Boolean)anim.get(args[0]), b -> anim.set(args[0], b)));
					break;
				}
				case "i":{
					com.add((field = new Field(INT, FF).consumer(f -> {
						anim.set(args[0], f.parse_int());
					})).pos(FO, next_y_pos(1)));
					field.set(((Number)anim.get(args[0])).intValue());
					break;
				}
				case "f":{
					com.add((field = new Field(FLOAT, FF).consumer(f -> {
						anim.set(args[0], f.parse_float());
					})).pos(FO, next_y_pos(1)));
					field.set(((Number)anim.get(args[0])).floatValue());
					break;
				}
				case "c":{
					com.add((field = new Field(COLOR, FF).consumer(f -> {
						anim.set(args[0], f.parse_int());
					})).pos(FO, next_y_pos(1)));
					field.set(((Number)anim.get(args[0])).intValue());
					break;
				}
				case "v":{
					com.add((field = new Field(FLOAT, F3S).consumer(f -> {
						anim.set(args[0] + ".x", f.parse_float());
					})).pos(F30, next_y_pos(1)));
					field.set((float)((V3D)anim.get(args[0])).x);
					com.add((field = new Field(FLOAT, F3S).consumer(f -> {
						anim.set(args[0] + ".y", f.parse_float());
					})).pos(F31, next_y_pos(0)));
					field.set((float)((V3D)anim.get(args[0])).y);
					com.add((field = new Field(FLOAT, F3S).consumer(f -> {
						anim.set(args[0] + ".z", f.parse_float());
					})).pos(F32, next_y_pos(0)));
					field.set((float)((V3D)anim.get(args[0])).z);
					break;
				}
			}
		}
	}

}
