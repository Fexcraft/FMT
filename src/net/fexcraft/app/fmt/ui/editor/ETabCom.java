package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.ui.Element;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_2;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_TEXT_1;
import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ETabCom extends Element {

	protected boolean minimized = false;
	private int fullheight;
	protected String id;

	public ETabCom(String id){
		this.id = id;
	}

	@Override
	public void init(Object... args){
		border(GENERIC_BACKGROUND_2.value);
		size(EDITOR_CONTENT, fullheight = (int)args[1]);
		add(new Element().translate(args[0].toString()).color(GENERIC_BACKGROUND_2.value).size(EDITOR_CONTENT, 30));
		elements.get(0).text.color(GENERIC_TEXT_1.value);
		elements.get(0).add(new Element().hoverable(true).texture("icons/component/size").size(28, 28).pos(EDITOR_CONTENT - 29, 1).onclick(ci -> {
			if(minimized) show();
			else hide();
		}));

	}

	@Override
	public Element hide(){
		minimized = true;
		size(EDITOR_CONTENT, 30);
		for(int i = 1; i < elements.size(); i++){
			elements.get(i).hide();
		}
		recompile();
		((EditorTab)root.root).reorderComponents();
		return this;
	}

	@Override
	public Element show(){
		minimized = false;
		size(EDITOR_CONTENT, fullheight);
		for(int i = 1; i < elements.size(); i++){
			elements.get(i).show();
		}
		recompile();
		((EditorTab)root.root).reorderComponents();
		return this;
	}

}
