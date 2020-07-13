package net.fexcraft.app.fmt.ui;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.event.MouseDragEvent;
import org.liquidengine.legui.style.border.SimpleLineBorder;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TexViewBox {
	
	private static Widget viewbox;
	private static ScrollablePanel canvas;
	private static boolean borders;

	public static final void open(String texGroup){
		open(texGroup, 1);
	}

	public static final void open(String texGroup, int scale){
		Float x = null, y = null, w = null, h = null;
		if(viewbox != null){
			FMTB.frame.getContainer().remove(viewbox);
			x = viewbox.getPosition().x;
			y = viewbox.getPosition().y;
			w = viewbox.getSize().x;
			h = viewbox.getSize().y;
		}
		if(scale < 1) scale = 1;
		if(scale > 4) scale = 4;
		int[] ascale = { scale };
		viewbox = new Widget("TexView", x == null ? 320 : x, y == null ? 50 : y, w == null ? FMTB.WIDTH / 2 : w, h == null ? FMTB.HEIGHT / 2 : h);
		viewbox.addWidgetCloseEventListener(listener -> viewbox = null);
		TextureGroup group = TextureManager.getGroup(texGroup);
		canvas = new ScrollablePanel(4, 36, viewbox.getSize().x - 8, viewbox.getSize().y - 60);
		canvas.getContainer().setSize(group.texture.getWidth() * scale, group.texture.getHeight() * scale);
		viewbox.getResizeButton().getListenerMap().addListener(MouseDragEvent.class, listener -> {
			canvas.setSize(viewbox.getSize().x - 8, viewbox.getSize().y - 60);
		});
		canvas.getStyle().setBorder(null);
		canvas.getStyle().setBorderRadius(0f);
		canvas.getContainer().getStyle().setBorder(null);
		canvas.getContainer().getStyle().setBorderRadius(0f);
		canvas.setHorizontalScrollBarVisible(true);
		canvas.getContainer().getStyle().getBackground().setColor(new Vector4f(0.5f, 0.5f, 0.5f, 1f));
		canvas.setFocusable(false);
		canvas.getContainer().setFocusable(false);
		viewbox.getContainer().add(canvas);
		//
		SelectBox<String> texgroups = new SelectBox<>(4, 4, 120, 24);
		for(TextureGroup texgroup : TextureManager.getGroupsFE()){
			texgroups.addElement(texgroup.group);
		}
		texgroups.addSelectBoxChangeSelectionEventListener(listener -> {
			open(listener.getNewValue(), ascale[0]);
		});
		viewbox.getContainer().add(texgroups);
		//
		Button buttonplus = new Button("+", 128, 4, 24, 24);
		buttonplus.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == MouseClickAction.CLICK){
				open(texGroup, ascale[0] + 1);
			}
		});
		Button buttonminus = new Button("-", 156, 4, 24, 24);
		buttonminus.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == MouseClickAction.CLICK){
				open(texGroup, ascale[0] - 1);
			}
		});
		Button buttonborders = new Button("B", 184, 4, 24, 24);
		buttonborders.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == MouseClickAction.CLICK){
				borders = !borders;
				update();
			}
		});
		viewbox.getContainer().add(buttonplus);
		viewbox.getContainer().add(buttonminus);
		viewbox.getContainer().add(buttonborders);
		//
		boolean isactivegroup = FMTB.MODEL.texgroup != null && FMTB.MODEL.texgroup.group.equals(texGroup);
		for(TurboList list : FMTB.MODEL.getGroups()){
			if(!isactivegroup && list.texgroup == null) continue;
			if(list.texgroup != null && !list.texgroup.group.equals(texGroup)) continue;
			for(PolygonWrapper wrapper : list){
				if(!wrapper.getType().isTexturable()) continue;
				float[][][] coords = wrapper.newTexturePosition(true);
				for(int i = 0; i < coords.length; i++){
					if(coords[i][1][0] - coords[i][0][0] == 0 || coords[i][1][1] - coords[i][0][1] == 0) continue;
					canvas.getContainer().add(new PolyFace(wrapper, i, coords[i], scale));
				}
			}
		}
		//
		FMTB.frame.getContainer().add(viewbox);
	}
	
	public static boolean isOpen(){
		return viewbox != null;
	}
	
	public static class PolyFace extends Panel {
		
		private static final ModelRendererTurbo cache = new ModelRendererTurbo(null).setTextured(false);
		private PolygonWrapper wrapper;
		private float[][] coords;
		private int index;

		public PolyFace(PolygonWrapper wrapper, int idx, float[][] arr, int scale){
			super(wrapper.textureX + arr[0][0], wrapper.textureY + arr[0][1], arr[1][0] - arr[0][0], arr[1][1] - arr[0][1]);
			this.setPosition(this.getPosition().mul(scale));
			this.setSize(this.getSize().mul(scale));
			this.wrapper = wrapper;
			this.coords = arr;
			this.index = idx;
			updateColor();
		}

		private void updateColor(){
			this.getStyle().setBorder(borders ? new SimpleLineBorder(new Vector4f(0f, 0f, 0f, 1f), 1) : null);
			this.getStyle().setBorderRadius(0f);
			this.getStyle().getBackground().setColor(FMTB.rgba(wrapper.selected || wrapper.getTurboList().selected ? Settings.getSelectedColor() : cache.getColor(index)));
		}
		
	}

	public static Vector2f pos(){
		return viewbox.getPosition();
	}

	public static Vector2f size(){
		return viewbox.getSize();
	}

	public static void update(){
		canvas.getContainer().getChildComponents().forEach(com -> {
			if(com instanceof PolyFace){
				((PolyFace)com).updateColor();
			}
		});
	}

}
