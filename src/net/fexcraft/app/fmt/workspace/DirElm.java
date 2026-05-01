package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;

import java.io.File;
import java.util.Arrays;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DirElm extends FileElm {

	public Element container;

	public DirElm(VFileType type, File file){
		super(type, file);
		render_sub_even_if_invisible = true;
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
		File[] files = file.listFiles();
		if(files == null || files.length == 0) return;
		Arrays.sort(files, (a, b) -> {
			if(a.isDirectory() && !b.isDirectory()) return -1;
			if(b.isDirectory() && !a.isDirectory()) return 1;
			return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
		});
		for(File fl : files){
			if(fl.getName().startsWith(".")) continue;
			var type = VFileType.fromFile(fl);
			if(!fl.isDirectory()){
				VFileType typ = type.getLeft();
				FileElm nelm = new FileElm(typ, fl);
				if(typ != VFileType.FILE){
					FvtmPackElm pack = (FvtmPackElm)args[0];
					if(type.getRight() != null){
						pack.content.get(type.getRight()).add(nelm);
					}
					if(typ == VFileType.PNG){
						pack.textures.add(nelm);
					}
					if(typ.model()){
						pack.models.add(nelm);
					}
				}
				container.add(nelm, args);
			}
			else{
				container.add(new DirElm(type.getLeft(), fl), args);
			}
		}
		updateContainer();
	}

}
