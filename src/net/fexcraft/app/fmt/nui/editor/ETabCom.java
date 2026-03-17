package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.nui.Element;

import static net.fexcraft.app.fmt.nui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ETabCom extends Element {

	@Override
	public void init(Object... args){
		border(col_bd);
		size(EDITOR_CONTENT, (int)args[1]);
		add(new Element().pos(0, 0).translate(args[0].toString()).color(col_bd).size(EDITOR_CONTENT, 30));
	}

}
