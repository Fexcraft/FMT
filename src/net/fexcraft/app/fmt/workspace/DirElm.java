package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DirElm extends FileElm {

	public Element container;

	public DirElm(VFileType type, File file){
		super(type, file);
		onclick(ci -> {
			container.visible = !container.visible;
			FMT.WORKSPACE.packs.updateBar();
		});
	}

	public void updateContainer(){
		if(container.elements == null) return;
		float siz = 2;
		for(Element elm : container.elements){
			elm.pos(5, siz);
			siz += elm.h + 2;
			if(elm instanceof DirElm dir){
				dir.updateContainer();
				siz += dir.container.visible ? dir.container.h : 0;
			}
		}
		container.size(w, siz);
	}

	@Override
	public void init(Object... args){
		super.init(args);
		add(container = new Element().pos(0, h).hide());
		if(!file.isDirectory()) return;
		for(File fl : file.listFiles()){
			var type = VFileType.fromFile(fl);
			container.add(fl.isDirectory() ? new DirElm((VFileType)type, fl) : new FileElm(type, fl));
		}
		updateContainer();
	}

}
