package net.fexcraft.app.fmt.ui.components;

import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.fvtm.VehAttr;

import java.util.function.Supplier;

import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AttrComponent extends EditorComponent {

	public AttrComponent(){
		super("variable", 60, false, true);
		add(new Icon(0, 24, 0, 10, row(1), "./resources/textures/icons/configeditor/text.png", () -> {
			openNewAttrDialog(() -> new VehAttr(VehAttr.Type.STRING, "empty"));
		}).addTooltip("editor.component.variable.add.string"));
		add(new Icon(0, 24, 0, 40, row(0), "./resources/textures/icons/configeditor/bool.png", () -> {
			openNewAttrDialog(() -> new VehAttr(VehAttr.Type.BOOL, false));
		}).addTooltip("editor.component.variable.add.boolean"));
		add(new Icon(0, 24, 0, 70, row(0), "./resources/textures/icons/configeditor/integer.png", () -> {
			openNewAttrDialog(() -> new VehAttr(VehAttr.Type.INT, false));
		}).addTooltip("editor.component.variable.add.integer"));
		add(new Icon(0, 24, 0, 100, row(0), "./resources/textures/icons/configeditor/float.png", () -> {
			openNewAttrDialog(() -> new VehAttr(VehAttr.Type.FLOAT, false));
		}).addTooltip("editor.component.variable.add.float"));
		add(new Icon(0, 24, 0, 130, row(0), "./resources/textures/icons/configeditor/integer.png", () -> {
			openNewAttrDialog(() -> new VehAttr(VehAttr.Type.LONG, false));
		}).addTooltip("editor.component.variable.add.long"));
		add(new Icon(0, 24, 0, 160, row(0), "./resources/textures/icons/configeditor/bool.png", () -> {
			openNewAttrDialog(() -> new VehAttr(VehAttr.Type.TRISTATE, false));
		}).addTooltip("editor.component.variable.add.tristate"));
	}

	private void openNewAttrDialog(Supplier<VehAttr> supp){
		Dialog dialog = new Dialog(translate("variable_add.dialog"), 420, 120);
		dialog.getContainer().add(new Label(translate("variable_add.dialog.name"), 10, 5, 400, 20));
		TextField name = new TextField("var", 10, 30, 400, 30, false);
		dialog.getContainer().add(name);
		dialog.getContainer().add(new RunButton("dialog.button.confirm", 310, 70, 100, 20, () -> {
			FMT.MODEL.vehattrs.put(name.getTextState().getText(), supp.get());
			Editor.VAR_EDITOR.refreshVarData(FMT.MODEL);
			dialog.close();
		}));
		dialog.getContainer().add(new RunButton("dialog.button.cancel", 200, 70, 100, 20, () -> dialog.close()));
		dialog.setResizable(false);
		dialog.show(FMT.FRAME);
	}

}
