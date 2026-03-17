package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.nui.Element;
import net.fexcraft.lib.common.math.RGB;

import static net.fexcraft.app.fmt.nui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ETabCom extends Element {

	@Override
	public void init(Object... args){
		border(col_85);
		size(EDITOR_CONTENT, (int)args[1]);
		add(new Element().translate(args[0].toString()).color(col_85).size(EDITOR_CONTENT, 30));
		elements.get(0).text.color(col_cd);
	}

}
