package net.fexcraft.app.fmt.ui.components;

import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.settings.Settings.GROUP_SUFFIX;
import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotGeneral extends EditorComponent {

	private static final String NOPIVOTSEL = "< no pivot directly selected >";
	protected static final String genid = "pivot.general";
	private TextField name;

	public PivotGeneral(){
		super(genid, 240, false, true);
		this.add(new Label(translate(LANG_PREFIX + genid + ".name/id"), L5, row(1), LW, HEIGHT));
		this.add(name = new TextField(NOPIVOTSEL, L5, row(1), LW, HEIGHT, false).accept(con -> rename(con)));
		updcom.add(PivotSelected.class, e -> {
			name.getTextState().setText(e.pivot().id);
		});
		updcom.add(PivotRenamed.class, e -> {
			//
		});
	}

	private void rename(String name){
		Pivot selected = FMT.MODEL.sel_pivot;
		if(selected == null) return;
		selected.id = name;
		UpdateHandler.update(new PivotRenamed(selected, name));
	}

}
