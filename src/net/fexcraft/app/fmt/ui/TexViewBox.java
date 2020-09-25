package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.ui.UserInterfaceUtils.hide;
import static net.fexcraft.app.fmt.ui.UserInterfaceUtils.show;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.event.MouseDragEvent;
import org.liquidengine.legui.image.BufferedImage;
import org.liquidengine.legui.style.border.SimpleLineBorder;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.UVEditor;
import net.fexcraft.app.fmt.utils.RayCoastAway;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TexViewBox {
	
	private static Widget viewbox;
	private static ScrollablePanel canvas;
	private static boolean borders, nozero, texture;
	private static int scale;
	private static ImageView view;

	public static final void open(String texGroup){
		open(texGroup, 1);
	}

	public static final void open(String texGroup, int ascale){
		Float x = null, y = null, w = null, h = null;
		if(viewbox != null){
			FMTB.frame.getContainer().remove(viewbox);
			x = viewbox.getPosition().x;
			y = viewbox.getPosition().y;
			w = viewbox.getSize().x;
			h = viewbox.getSize().y;
		}
		scale = ascale;
		if(scale < 1) scale = 1;
		if(scale > 8) scale = 8;
		viewbox = new Widget("TexView", x == null ? 320 : x, y == null ? 50 : y, w == null ? FMTB.WIDTH / 2 : w, h == null ? FMTB.HEIGHT / 2 : h);
		viewbox.addWidgetCloseEventListener(listener -> {
			viewbox = null;
			canvas = null;
		});
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
		view = new ImageView(new BufferedImage(group.texture.getFile().toPath().toString()));
		view.setSize(canvas.getContainer().getSize());
		canvas.getContainer().add(view);
		if(!texture) hide(view);
		//
		SelectBox<String> texgroups = new SelectBox<>(4, 4, 120, 24);
		for(TextureGroup texgroup : TextureManager.getGroupsFE()){
			texgroups.addElement(texgroup.group);
		}
		texgroups.addSelectBoxChangeSelectionEventListener(listener -> {
			open(listener.getNewValue(), scale);
		});
		viewbox.getContainer().add(texgroups);
		//
		int pos = 112;
		viewbox.getContainer().add(new ClickListenerButton("+", pos += 24, 4, 24, 24, () -> {
			open(texGroup, scale + 1);
		}).setTooltip(" zoom in ", 28, 0, 60, 20));
		viewbox.getContainer().add(new ClickListenerButton("-", pos += 28, 4, 24, 24, () -> {
			open(texGroup, scale - 1);
		}).setTooltip(" zoom out ", 28, 0, 70, 20));
		viewbox.getContainer().add(new ClickListenerButton("B", pos += 36, 4, 24, 24, () -> {
			borders = !borders;
			update();
		}).setTooltip(" toggle borders ", 28, 0, 100, 20));
		viewbox.getContainer().add(new ClickListenerButton("N0", pos += 28, 4, 24, 24, () -> {
			nozero = !nozero;
			update();
		}).setTooltip(" toggle notex poly ", 28, 0, 120, 20));
		viewbox.getContainer().add(new ClickListenerButton("T", pos += 28, 4, 24, 24, () -> {
			texture = !texture;
			update();
		}).setTooltip(" toggle texture background ", 28, 0, 120, 20));
		//
		/*viewbox.getContainer().add(new ClickListenerButton("<", pos += 36, 4, 24, 24, () -> move(-1, 0)).setTooltip("move left", 28, 0, 100, 20));
		viewbox.getContainer().add(new ClickListenerButton(">", pos += 28, 4, 24, 24, () -> move(1, 0)).setTooltip("move right", 28, 0, 100, 20));
		viewbox.getContainer().add(new ClickListenerButton("^", pos += 28, 4, 24, 24, () -> move(0, -1)).setTooltip("move up", 28, 0, 100, 20));
		viewbox.getContainer().add(new ClickListenerButton("V", pos += 28, 4, 24, 24, () -> move(0, 1)).setTooltip("move down", 28, 0, 100, 20));*/
		//
		boolean isactivegroup = FMTB.MODEL.texgroup != null && FMTB.MODEL.texgroup.group.equals(texGroup);
		for(TurboList list : FMTB.MODEL.getGroups()){
			if(!isactivegroup && list.texgroup == null) continue;
			if(list.texgroup != null && !list.texgroup.group.equals(texGroup)) continue;
			for(PolygonWrapper wrapper : list){
				if(!wrapper.getType().isTexturable()) continue;
				float[][][] coords = wrapper.newTexturePosition(true, false);
				for(int i = 0; i < coords.length; i++){
					if(i > 5) break;
					if(coords[i] == null) continue;
					if(coords[i][1][0] - coords[i][0][0] == 0 || coords[i][1][1] - coords[i][0][1] == 0) continue;
					canvas.getContainer().add(new PolyFace(wrapper, i, coords[i]));
				}
			}
		}
		//
		FMTB.frame.getContainer().add(viewbox);
	}
	
	/*private static void move(int x, int y){
		NumberField field = null;
		boolean valid = UVEditor.getSelection() != NullFace.NONE && UVEditor.getLast() != null;
		if(valid){
			valid = UVEditor.getLast().getUVCoords(UVEditor.getSelection()).type().arraylength == 2;//offset-only/absolute
		}
		if(x != 0){
			field = valid ? UVEditor.oo_tex_x : UVEditor.texture_x;
		}
		else{
			field = valid ? UVEditor.oo_tex_y : UVEditor.texture_y;
		}
		field.onScroll(x == 0 ? y : x);
	}*/

	public static boolean isOpen(){
		return viewbox != null;
	}
	
	public static class PolyFace extends Panel {
		
		private static final ModelRendererTurbo cache = new ModelRendererTurbo(null).setTextured(false);
		private PolygonWrapper wrapper;
		private float[][] coords;
		private Face side;
		private boolean dragged;

		public PolyFace(PolygonWrapper wrapper, int idx, float[][] arr){
			super(0, 0, 0, 0);//wrapper.textureX + arr[0][0], wrapper.textureY + arr[0][1], arr[1][0] - arr[0][0], arr[1][1] - arr[0][1]);
			//this.setPosition(this.getPosition().mul(scale));
			//this.setSize(this.getSize().mul(scale));
			this.wrapper = wrapper.setTexViewComponent(this);
			this.coords = arr;
			this.side = wrapper.getTexturableFaces()[idx];
			this.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() == MouseClickAction.CLICK){
					if(dragged){
						dragged = false;
						return;
					}
					if(!wrapper.selected){
						UVEditor.selface = side;
						UVEditor.uv_face.setSelected(side.id(), true);
						UVEditor.uv_type.setSelected(wrapper.getUVCoords(side).type().name().toLowerCase(), true);
						UVEditor.refreshEntries(wrapper, side);
					}
					RayCoastAway.select(wrapper);
				}
			});
			this.getListenerMap().addListener(MouseDragEvent.class, listener -> {
				if(!wrapper.selected && !wrapper.getTurboList().selected) return;
				int x = (int)(listener.getDelta().x / scale);
				int y = (int)(listener.getDelta().y / scale);
				if(x != 0 || y != 0){
					moveSelection(x, y);
					dragged = true;
				}
			});
			update();
		}

		private void updateColor(){
			this.getStyle().setBorder(borders ? new SimpleLineBorder(new Vector4f(0f, 0f, 0f, 1f), 1) : null);
			this.getStyle().setBorderRadius(0f);
			boolean selected = wrapper.selected || wrapper.getTurboList().selected;
			boolean selface = side == UVEditor.selface;
			Vector4f color = FMTB.rgba(selected ? (selface ? Settings.getSelectedColor() : Settings.getSelectedColor2()) : cache.getColor(side.index()));
			color.w = texture ? 0.25f : 1f;
			this.getStyle().getBackground().setColor(color);
		}

		public void isZero(){
			if(nozero && (wrapper.textureX == -1 || wrapper.textureY == -1) && !wrapper.cuv.get(side).absolute()){
				hide(this);
			}
			else{
				show(this);
			}
		}

		public void updatePos(){
			coords = wrapper.newTexturePosition(true, false)[side.index()];
			if(coords == null || coords.length == 0){
				hide(this);
				return;
			}
			boolean absolute = wrapper.cuv.get(side).absolute();
			float tx = absolute ? 0 : wrapper.textureX, ty = absolute ? 0 : wrapper.textureY;
			this.setPosition(new Vector2f(tx + coords[0][0], ty + coords[0][1]).mul(scale));
			this.setSize(new Vector2f(coords[1][0] - coords[0][0], coords[1][1] - coords[0][1]).mul(scale));
			show(this);
		}

		public void update(){
			updateColor();
			updatePos();
			isZero();
		}
		
	}

	public static Vector2f pos(){
		return viewbox.getPosition();
	}

	public static void moveSelection(int x, int y){
		ArrayList<PolygonWrapper> selected = FMTB.MODEL.getSelected();
		Face selface = UVEditor.getSelection();
		for(PolygonWrapper wrapper : selected){
			if(!wrapper.isFaceActive(selface)) continue;
			UVCoords coord = wrapper.getUVCoords(selface);
			switch(coord.type()){
				case AUTOMATIC:
					wrapper.textureX += x;
					wrapper.textureY += y;
					break;
				case ABSOLUTE:
				case OFFSET_ONLY:
					coord.value()[0] += x;
					coord.value()[1] += y;
					break;
				case ABSOLUTE_ENDS:
				case OFFSET_ENDS:
					coord.value()[0] += x;
					coord.value()[1] += y;
					coord.value()[2] += x;
					coord.value()[3] += y;
					break;
				case ABSOLUTE_FULL:
				case OFFSET_FULL:
					coord.value()[0] += x;
					coord.value()[1] += y;
					coord.value()[2] += x;
					coord.value()[3] += y;
					coord.value()[4] += x;
					coord.value()[5] += y;
					coord.value()[6] += x;
					coord.value()[7] += y;
					break;
			}
		}
		FMTB.MODEL.updateFields();
	}

	public static Vector2f size(){
		return viewbox.getSize();
	}

	public static void update(){
		if(viewbox == null) return;
		if(texture) show(view);
		else hide(view);
		canvas.getContainer().getChildComponents().forEach(com -> {
			if(com instanceof PolyFace){
				((PolyFace)com).update();
			}
		});
	}

}
