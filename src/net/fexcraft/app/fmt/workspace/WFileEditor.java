package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Scrollable;

import java.io.File;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_FIELD;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class WFileEditor extends Element {

	protected Scrollable container;
	protected File file;

	public WFileEditor(File file){
		super();
		this.file = file;
	}

	@Override
	public void init(Object... args){
		add(container = new Scrollable(true, 0));
		container.updateSize(w, h);
	}

	protected String get_editor_name(){
		return "Generic Editor";
	}

	public String get_title(){
		return get_editor_name() + " - " + file.getName();
	}

	public void save(){

	}

	public static class WFileEditorEntry extends Element {

		public final WFileEditor editor;

		public WFileEditorEntry(WFileEditor edit){
			super();
			editor = edit;
			text(edit.get_title());
			text_autoscale();
			hint(edit.file.getAbsolutePath());
			check_mode(CheckMode.IN_ROOT);
			color(GENERIC_FIELD.value);
			hoverable(true);
			pos(5, 0);
			onclick(ci -> FMT.WORKSPACE.setActive(edit, 0));
		}

	}

}
