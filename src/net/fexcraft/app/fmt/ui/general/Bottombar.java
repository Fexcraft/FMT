package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.tree.ModelTree;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.RGB;

public class Bottombar extends Element {
	
	private static TextField netfield;
	private int[] fields = new int[]{ 6, 6, 3, 6, 6};
	public static long fps;

	public Bottombar(){
		super(null, "bottombar", "bottombar"); hovercolor = RGB.WHITE;
		this.setPosition(0, 0).setSize(100, 26).setColor(0xff8f8f8f).setBorder(0xff32a852, 0xffeb4034, 3, true, false);
		this.elements.add(new BottomField(this, "polygons", "bottombar:field", UserInterface.width / 6, 0, 0, "Polygons: "){
			@Override
			public void renderSelf(int rw, int rh){
				this.setText(translation + ModelTree.COUNT, true);
				super.renderSelf(rw, rh);
			}
		}.setEnabled(false));
		this.elements.add(new BottomField(this, "selected", "bottombar:field", UserInterface.width / 6, 0, 0, "Selected: "){
			@Override
			public void renderSelf(int rw, int rh){
				if(GroupCompound.SELECTED_POLYGONS < 0) GroupCompound.SELECTED_POLYGONS = 0;
				this.setText(translation + GroupCompound.SELECTED_POLYGONS, true);
				super.renderSelf(rw, rh);
			}
		}.setEnabled(false));
		this.elements.add((netfield = new TextField(this, "netfield", "bottombar:field", UserInterface.width / 3, 0, 0)).setEnabled(false));
		this.elements.add(new BottomField(this, "groups", "bottombar:field", UserInterface.width / 6, 0, 0, "Groups: "){
			@Override
			public void renderSelf(int rw, int rh){
				this.setText(translation + FMTB.MODEL.getGroups().size(), true);
				super.renderSelf(rw, rh);
			}
		}.setEnabled(false));
		this.elements.add(new BottomField(this, "frames", "bottombar:field", UserInterface.width / 6, 0, 0, "FPS:"){
			@Override
			public void renderSelf(int rw, int rh){
				this.setText(translation + fps, true);
				super.renderSelf(rw, rh);
			}
		}.setEnabled(false));
		this.repos();
	}
	
	@Override
	public Element repos(){
		width = UserInterface.width; x = 0; y = UserInterface.height - height; int buff = 0; Element elm;
		for(int i = 0; i < elements.size(); i++){
			elm = elements.get(i); elm.xrel = buff; buff += (elm.width = UserInterface.width / fields[i]); elm.repos();
		} return this.clearVertexes();
	}
	
	public static void updateLoginState(String string){
		if(netfield != null) netfield.setText(string, true);
	}
	
	private static class BottomField extends TextField {
		
		protected String translation;

		public BottomField(Element root, String id, String style, int width, int x, int y, String translation){
			super(root, id, style, width, x, y); this.translation = translate("bottombar:" + id, translation);
			this.setHoverColor(StyleSheet.WHITE, false).setHoverColor(StyleSheet.WHITE, true);
		}
		
	}

}
