package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.utils.fvtm.VehAttr;

import java.util.Map;
import java.util.function.Supplier;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.Field.FieldType.*;
import static net.fexcraft.app.fmt.ui.Field.FieldType.FLOAT;
import static net.fexcraft.app.fmt.ui.Field.FieldType.INT;
import static net.fexcraft.app.fmt.ui.editor.EditorRoot.NOVARIABLES;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VariableEditorTab extends EditorTab {

	private ETabCom addattr;

	public VariableEditorTab(){
		super(EditorRoot.EditorMode.VARIABLE);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add(addattr = new ETabCom("add_new"), "editor.variable.add_new", 70);
		addattr.add(new Element().pos(FO, 35).size(32, 32).texture("icons/configeditor/text")
			.onclick(ci -> openNewAttr(() -> new VehAttr(VehAttr.Type.STRING, "empty")))
			.hint("editor.variable.add.string"));
		addattr.add(new Element().pos(FO + 35, 35).size(32, 32).texture("icons/configeditor/bool")
			.onclick(ci -> openNewAttr(() -> new VehAttr(VehAttr.Type.BOOL, false)))
			.hint("editor.variable.add.boolean"));
		addattr.add(new Element().pos(FO + 70, 35).size(32, 32).texture("icons/configeditor/integer")
			.onclick(ci -> openNewAttr(() -> new VehAttr(VehAttr.Type.INT, 0)))
			.hint("editor.variable.add.integer"));
		addattr.add(new Element().pos(FO + 105, 35).size(32, 32).texture("icons/configeditor/float")
			.onclick(ci -> openNewAttr(() -> new VehAttr(VehAttr.Type.FLOAT, 0f)))
			.hint("editor.variable.add.float"));
		addattr.add(new Element().pos(FO + 140, 35).size(32, 32).texture("icons/configeditor/integer")
			.onclick(ci -> openNewAttr(() -> new VehAttr(VehAttr.Type.LONG, 0l)))
			.hint("editor.variable.add.long"));
		addattr.add(new Element().pos(FO + 175, 35).size(32, 32).texture("icons/configeditor/bool")
			.onclick(ci -> openNewAttr(() -> new VehAttr(VehAttr.Type.TRISTATE, false)))
			.hint("editor.variable.add.tristate"));
		//
		updcom.add(UpdateEvent.ModelLoad.class, e -> refill());
		updcom.add(UpdateEvent.ModelUnload.class, e -> refill());
		refill();
	}

	private void openNewAttr(Supplier<VehAttr> supp){
		Field field = new Field(TEXT, 390);
		FMT.UI.createDialog(400, 120, "editor.variable.add_title")
			.addText(0, "editor.variable.add_name")
			.addRowElm(1, field)
			.consumer(d -> {
				FMT.MODEL.vehattrs.put(field.get_text(), supp.get());
				refill();
			}, null)
			.buttons(100, Dialog.DialogButton.ADD, Dialog.DialogButton.CANCEL);
	}

	private void refill(){
		container.remElmIf(elm -> elm instanceof ETabCom && elm != addattr);
		addVarComs();
		reorderComponents();
	}

	private void addVarComs(){
		ETabCom com = new ETabCom("general");
		next_y_elm_pos = 0;
		if(FMT.MODEL.vehattrs.isEmpty()){
			container.add(com, "editor.variable.general", 65);
			com.add(new TextElm(FO, next_y_pos(1), FF, NOVARIABLES));
			return;
		}
		Field field;
		container.add(com, "editor.variable.general", FMT.MODEL.vehattrs.size() * 60 + 35);
		for(Map.Entry<String, VehAttr> entry : FMT.MODEL.vehattrs.entrySet()){
			com.add(new TextElm(FO, next_y_pos(1), FF - 35, entry.getKey()));
			com.add(new HidingElm().pos(FO + FF - 35, next_y_pos(0)).size(30, 30).texture("icons/component/remove")
				.onclick(ci -> {
					Runnable run = () -> {
						FMT.MODEL.vehattrs.remove(entry.getKey());
						refill();
					};
					if(ASK_VARIABLE_REMOVAL.value){
						FMT.UI.createDialog(500, 120, "editor.variable.general")
							.addText(0, "editor.variable.removal")
							.addText(1, entry.getKey() + " / " + entry.getValue().value)
							.consumer(d -> run.run(), null)
							.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
					}
					else run.run();
				}).hint("editor.variable.remove"));
			switch(entry.getValue().type){
				case STRING:{
					com.add((field = new Field(TEXT, FF)).consumer(f -> {
						entry.getValue().value = f.get_text();
					}).pos(FO, next_y_pos(1)));
					field.text(entry.getValue().value);
					break;
				}
				case BOOL:{
					com.add(new BoolElm(FO, next_y_pos(1), FF)
						.set(() -> (Boolean)entry.getValue().value, b -> entry.getValue().value = b));
					break;
				}
				case INT:{
					com.add((field = new Field(INT, FF).consumer(f -> {
						entry.getValue().value = f.parse_int();
					})).pos(FO, next_y_pos(1)));
					field.set(((Number)entry.getValue().value).intValue());
					break;
				}
				case FLOAT:{
					com.add((field = new Field(FLOAT, FF).consumer(f -> {
						entry.getValue().value = f.parse_float();
					})).pos(FO, next_y_pos(1)));
					field.set(((Number)entry.getValue().value).floatValue());
					break;
				}
				case LONG:{
					com.add((field = new Field(INT, FF).consumer(f -> {
						entry.getValue().value = f.parse_long();
					})).pos(FO, next_y_pos(1)));
					field.set(((Number)entry.getValue().value).longValue());
					break;
				}
				case TRISTATE:{
					com.add(new Element().pos(F30, next_y_pos(1)).size(F3S, FS).hoverable(true).text("true")
						.onclick(ci -> entry.getValue().value = true).color(GENERIC_FIELD.value).text_centered(true));
					com.add(new Element().pos(F31, next_y_pos(0)).size(F3S, FS).hoverable(true).text("false")
						.onclick(ci -> entry.getValue().value = false).color(GENERIC_FIELD.value).text_centered(true));
					com.add(new Element().pos(F32, next_y_pos(0)).size(F3S, FS).hoverable(true).text("null")
						.onclick(ci -> entry.getValue().value = null).color(GENERIC_FIELD.value).text_centered(true));
					break;
				}
			}
		}
	}

}
