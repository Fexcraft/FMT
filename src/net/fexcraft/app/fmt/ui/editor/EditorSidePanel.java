package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.CornerUtil;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.app.fmt.utils.Selector;
import net.fexcraft.lib.common.math.RGB;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorSidePanel extends Element {

	public EditorSidePanel(){
		super();
		pos(EDITOR_WIDTH, 0);
		size(40, 294);
		color(col_cd);
	}

	@Override
	public void init(Object... args){
		int inc = 42, yo = -inc;
		add(new AddPolygon(),0, yo += inc, "icons/panels/quick_add");
		add(new EditorList(),0, yo += inc, "icons/panels/editors");
		add(new Multiplier(),0, yo += inc, "icons/panels/multiplier");
		yo += inc;
		add(new FlipTools(),0, yo += inc, "icons/panels/fliptools");
		add(new MarkerScale(),0, yo += inc, "icons/panels/marker_scale");
		add(new Selection(),0, yo += inc, "icons/panels/selmode");
	}

	public static class Panel extends Element {

		protected Element container;
		protected boolean expanded;
		protected int ew, eh;

		public Panel(){
			super();
			size(32, 32);
			onclick(ci -> toggle());
			hoverable = true;
		}

		@Override
		public void init(Object... args){
			add(container = new Element().pos(w + 4, -4).size(ew, eh).color(col_cd).border(RGB.BLACK).hide());
			pos((int)args[0] + 4, (int)args[1] + 4);
			texture(args[2].toString());
		}

		public void toggle(){
			expanded = !expanded;
			container.visible = expanded;
		}

	}

	public static class EditorList extends Panel {

		@Override
		public void init(Object... args){
			ew = 325;
			eh = 40;
			super.init(args);
			hint("editor.panel.mode");
			int iinc = 35, buff = -iinc + 5, yo = 4;
			for(EditorRoot.EditorMode mode : EditorRoot.EditorMode.values()){
				container.add(new Element().pos(buff += iinc, yo).size(32, 32)
					.texture("icons/editor/" + mode.name().toLowerCase()).hoverable(true)
					.onclick(ci -> EditorRoot.setMode(mode))
					.hint("editor.mode." + mode.name().toLowerCase()));
			}
		}
	}

	public static class AddPolygon extends Panel {

		@Override
		public void init(Object... args){
			ew = 325;
			eh = 40;
			super.init(args);
			hint("editor.panel.add");
			int iinc = 35, buff = -iinc + 5, yo = 4;
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/box").hint("editor.panel.add.box")
				.onclick(ci -> FMT.MODEL.add(null, null, new Box(null)))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/shapebox").hint("editor.panel.add.shapebox")
				.onclick(ci -> FMT.MODEL.add(null, null, new Shapebox(null)))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/cylinder").hint("editor.panel.add.cylinder")
				.onclick(ci -> FMT.MODEL.add(null, null, new Cylinder(null)))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/boundingbox").hint("editor.panel.add.struct")
				.onclick(ci -> FMT.MODEL.add(null, null, new StructBox(null)))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/object").hint("editor.panel.add.object")
				.onclick(ci -> {})
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/marker").hint("editor.panel.add.marker")
				.onclick(ci -> FMT.MODEL.add(null, null, new Marker(null)))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/group").hint("editor.panel.add.group")
				.onclick(ci -> addGroup())
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/voxel").hint("editor.panel.add.voxel")
				.onclick(ci -> {})
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/copy_sel").hint("editor.panel.add.copy")
				.onclick(ci -> FMT.MODEL.copySelected())
			);
		}

		public static void addGroup(){
			Dialog dia = FMT.UI.createDialog(400, 180, "dialog.add_group.title");
			dia.container.add(new TextElm(10, 10, 380, "dialog.add_group.name"));
			Field field = new Field(Field.FieldType.TEXT, 380);
			dia.container.add(field.pos(10, 40));
			field.text("group" + FMT.MODEL.totalGroups());
			dia.container.add(new TextElm(10, 70, 380, "dialog.add_group.pivot"));
			DropList<Pivot> list = new DropList<>(380);
			dia.container.add(list.pos(10, 100));
			for(Pivot pivot : FMT.MODEL.pivots()) list.addEntry(pivot.id, pivot);
			list.selectEntry(0);
			dia.consumer(d -> FMT.MODEL.addGroup(list.getSelKey(), field.get_text()), null);
			dia.buttons(100, Dialog.DialogButton.ADD);
		}

	}

	public static class FlipTools extends Panel {

		@Override
		public void init(Object... args){
			ew = 325;
			eh = 40;
			super.init(args);
			hint("editor.panel.flip");
			int iinc = 35, buff = -iinc + 5, yo = 4;
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/flip_lr").hint("editor.panel.flip.cor_x")
				.onclick(ci -> FMT.MODEL.flipPolygons(null, 0))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/flip_ud").hint("editor.panel.flip.cor_y")
				.onclick(ci -> FMT.MODEL.flipPolygons(null, 1))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/flip_fb").hint("editor.panel.flip.cor_z")
				.onclick(ci -> FMT.MODEL.flipPolygons(null, 2))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/flip_posx").hint("editor.panel.flip.pos_x")
				.onclick(ci -> FMT.MODEL.flipBoxPosition(null, 0))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/flip_posy").hint("editor.panel.flip.pos_y")
				.onclick(ci -> FMT.MODEL.flipBoxPosition(null, 1))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/flip_posz").hint("editor.panel.flip.pos_z")
				.onclick(ci -> FMT.MODEL.flipBoxPosition(null, 2))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/flip_posx_fb").hint("editor.panel.flip.cor_z_pos_x")
				.onclick(ci -> {
					FMT.MODEL.flipPolygons(null, 2);
					FMT.MODEL.flipBoxPosition(null, 0);
				})
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/flip_posy_ud").hint("editor.panel.flip.cor_y_pos_y")
				.onclick(ci -> {
					FMT.MODEL.flipPolygons(null, 1);
					FMT.MODEL.flipBoxPosition(null, 1);
				})
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/polygon/flip_posz_lr").hint("editor.panel.flip.cor_x_pos_z")
				.onclick(ci -> {
					FMT.MODEL.flipPolygons(null, 0);
					FMT.MODEL.flipBoxPosition(null, 2);
				})
			);
		}

	}

	public static class Multiplier extends Panel {

		private SelectorBar[] bars = new SelectorBar[3];
		private Element text;

		@Override
		public void init(Object... args){
			ew = 330;
			eh = 80;
			super.init(args);
			hint("editor.panel.multiplier");
			container.add(text = new Field(Field.FieldType.FLOAT, 90).pos(5, 28).onscroll(si -> {
				float er = Editor.RATE;
				if(si.sy() > 0) er *= 2;
				else er /= 2;
				if(er > 1024) er = 1024;
				if(er < 0.001) er = 0.001f;
				UpdateHandler.update(new UpdateEvent.EditorRate(Editor.RATE = er));
			}).text(Editor.RATE).hoverable(true));
			Consumer<Float> mul = m -> {
				if(Editor.RATE != m) UpdateHandler.update(new UpdateEvent.EditorRate(Editor.RATE = m));
			};
			container.add((bars[0] = new SelectorBar()).pos(90, 10), 200, 1, 16, 1, "1 - 16", mul);
			container.add((bars[1] = new SelectorBar()).pos(90, 35), 200, 0.0625, 1, 0.0625, "0.0625 - 1", mul);
			container.add((bars[2] = new SelectorBar()).pos(90, 60), 200, 0.1, 1, 0.1, "0.1 - 1", mul);
			UpdateHandler.register(com -> {
				com.add(UpdateEvent.EditorRate.class, e -> text.text(e.rate()));
			});
		}

	}

	public static class MarkerScale extends Panel {

		@Override
		public void init(Object... args){
			ew = 325;
			eh = 40;
			super.init(args);
			hint("editor.panel.marker_scale");
			Field field;
			container.add((field = new Field(Field.FieldType.FLOAT, 85)).pos(5, 6).onscroll(si -> {
				Editor.MARKER_SCALE = Editor.MARKER_SCALE == 0f ? 1 : si.sy() > 0 ? Editor.MARKER_SCALE * 2 : Editor.MARKER_SCALE / 2f;
				CornerUtil.compile();
				UpdateHandler.update(new UpdateEvent.MarkerScale(Editor.MARKER_SCALE));
			}).text(Editor.RATE).hoverable(true));
			Consumer<Float> cons = m -> {
				if(Editor.MARKER_SCALE != m){
					Editor.MARKER_SCALE = m;
					CornerUtil.compile();
					UpdateHandler.update(new UpdateEvent.MarkerScale(Editor.MARKER_SCALE));
				}
			};
			SelectorBar bar;
			container.add((bar = new SelectorBar()).pos(85, 10), 200, 0.1f, 1, 0.1f, "0.1 - 1", cons);
			UpdateHandler.register(com -> {
				com.add(UpdateEvent.MarkerScale.class, e -> field.text(e.scale()));
			});
		}

	}

	public static class Selection extends Panel {

		@Override
		public void init(Object... args){
			ew = 285;
			eh = 40;
			super.init(args);
			hint("editor.panel.selection");
			int iinc = 35, buff = -iinc + 5, yo = 4;
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/painter/polygon").hint("editor.panel.selection.polygon")
				.onclick(ci -> Selector.set(Picker.PickType.POLYGON))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/painter/face").hint("editor.panel.selection.face")
				.onclick(ci -> Selector.set(Picker.PickType.FACE))
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/painter/pixel").hint("editor.panel.selection.vertex")
				.onclick(ci -> Selector.set(Picker.PickType.VERTEX))
			);
			container.add(new Element().pos(buff += iinc * 2, yo).size(32, 32)
				.texture("icons/component/visible").hint("editor.panel.selection.visibility")
				.onclick(ci -> Selector.SHOW_VERTICES = !Selector.SHOW_VERTICES)
			);
			container.add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/component/remove").hint("editor.panel.selection.clear")
				.onclick(ci -> FMT.MODEL.clearSelectedVerts())
			);
			container.add(new Element().pos(buff += iinc * 2, yo).size(32, 32)
				.texture("icons/component/move_right").hint("editor.panel.selection.util_move")
				.onclick(ci -> Selector.move())
			);
		}

	}

}
