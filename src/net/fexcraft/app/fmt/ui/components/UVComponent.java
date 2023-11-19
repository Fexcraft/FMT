package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.ui.UVViewer.SELECTED;
import static net.fexcraft.app.fmt.utils.Translator.translate;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.polygon.uv.UVType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.CenteredLabel;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.UIUtils;
import net.fexcraft.app.fmt.ui.UVViewer;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.PickFace;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonSelected;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonUVType;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Logging;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;
import com.spinyowl.legui.style.border.SimpleLineBorder;

public class UVComponent extends EditorComponent {

	private NumberField tx, ty;
	private SelectBox<String> face, type;
	public static UVViewer.UVFields[] fields = new UVViewer.UVFields[4];

	public UVComponent(){
		super("polygon.texuv", 480, false, true);
		add(new Label(translate(LANG_PREFIX + id + ".root"), L5, row(1), LW, HEIGHT));
		NumberField tx, ty;
		add(tx = new NumberField(this, F30, row(1), F3S, 20).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, PolyVal.ValAxe.X)));
		add(ty = new NumberField(this, F31, row(), F3S, 20).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, PolyVal.ValAxe.Y)));
		add(new RunButton(LANG_PREFIX + id + ".root_reset", F32, row(0), F3S, 20, () -> {
			FMT.MODEL.updateValue(tx.polyval(), tx.apply(-1), 0);
			FMT.MODEL.updateValue(ty.polyval(), ty.apply(-1), 0);
		}));
		//
		add(new Label(translate(LANG_PREFIX + id + ".face"), L5, row(1), LW, HEIGHT));
		face = new SelectBox<>(L5, row(1), LW, HEIGHT);
		updcom.add(PolygonSelected.class, e -> {
			while(face.getElements().size() > 0) face.removeElement(0);
			face.addElement("none");
			Polygon poly = FMT.MODEL.first_selected();
			if(poly != null){
				poly.cuv.keySet().forEach(key -> face.addElement(key));
				if(poly.isValidUVFace(SELECTED)) face.setSelected(SELECTED.id(), true);
				else face.setSelected((SELECTED = poly.getUVFaces()[0]).id(), true);
			}
			else face.setSelected((SELECTED = NoFace.NONE).id(), true);
			updateSelFace(poly);
		});
		updcom.add(PickFace.class, e -> {
			while(face.getElements().size() > 0) face.removeElement(0);
			face.addElement("none");
			Polygon poly = e.polygon();
			poly.cuv.keySet().forEach(key -> face.addElement(key));
			face.setSelected((SELECTED = e.face()).id(), true);
			updateSelFace(poly);
		});
		face.addSelectBoxChangeSelectionEventListener(lis -> {
			SELECTED = Face.get(lis.getNewValue(), true);
			Polygon poly = FMT.MODEL.first_selected();
			updateSelFace(poly);
		});
		face.setVisibleCount(8);
		add(face);
		//
		add(new Label(translate(LANG_PREFIX + id + ".type"), L5, row(1), LW, HEIGHT));
		type = new SelectBox<>(L5, row(1), LW, HEIGHT);
		for(UVType uvt : UVType.values()){
			type.addElement(uvt.name().toLowerCase());
		}
		type.setVisibleCount(8);
		type.addSelectBoxChangeSelectionEventListener(lis -> {
			UVType uvt = UVType.from(lis.getNewValue());
			int idx = uvt.automatic() ? 0 : -1;
			if(idx == -1){
				idx = uvt.ordinal() > 3 ? uvt.ordinal() - 3 : uvt.ordinal();
			}
			for(Polygon poly : FMT.MODEL.selected()){
				UVCoords cor = poly.cuv.get(SELECTED);
				if(cor != null && cor.type() != uvt){
					cor.set(uvt);
					float[][][] poss = poly.newUV(false, false);
					//if(poss == null || poss.length == 0) return;
					float[][] pos = poss[SELECTED.index()];
					//if(pos == null) return;
					switch(cor.type()){
						case AUTOMATIC: break;
						case OFFSET:{
							cor.value(new float[]{ poly.textureX + pos[0][0], poly.textureY + pos[0][1] });
							break;
						}
						case OFFSET_ENDS:{
							cor.value()[0] = poly.textureX + pos[0][0];
							cor.value()[1] = poly.textureY + pos[0][1];
							cor.value()[2] = poly.textureX + pos[1][0];
							cor.value()[3] = poly.textureY + pos[1][1];
							break;
						}
						case OFFSET_FULL:{
							float w = pos[1][0] - pos[0][0];
							cor.value()[0] = poly.textureX + pos[0][0] + w;
							cor.value()[1] = poly.textureY + pos[0][1];
							cor.value()[2] = poly.textureX + pos[0][0];
							cor.value()[3] = poly.textureY + pos[0][1];
							cor.value()[4] = poly.textureX + pos[1][0] - w;
							cor.value()[5] = poly.textureY + pos[1][1];
							cor.value()[6] = poly.textureX + pos[1][0];
							cor.value()[7] = poly.textureY + pos[1][1];
							break;
						}
						case DETACHED:{
							cor.value(new float[]{ pos[0][0], pos[0][1] });
							break;
						}
						case DETACHED_ENDS:{
							cor.value()[0] = pos[0][0];
							cor.value()[1] = pos[0][1];
							cor.value()[2] = pos[1][0];
							cor.value()[3] = pos[1][1];
							break;
						}
						case DETACHED_FULL:{
							float w = pos[1][0] - pos[0][0];
							cor.value()[0] = pos[0][0] + w;
							cor.value()[1] = pos[0][1];
							cor.value()[2] = pos[0][0];
							cor.value()[3] = pos[0][1];
							cor.value()[4] = pos[1][0] - w;
							cor.value()[5] = pos[1][1];
							cor.value()[6] = pos[1][0];
							cor.value()[7] = pos[1][1];
							break;
						}
					}
					UpdateHandler.update(new PolygonUVType(poly, uvt));
				}
			}
			UpdateHandler.update(new PolygonSelected(FMT.MODEL.first_selected(), 0, 0));
			showField(idx, null);
		});
		add(type);
		//
		int r = row(1) + 10;
		for(int i = 0; i < 4; i++){
			int w = 294;
			fields[i] = new UVViewer.UVFields(3, r, w, 20);
			if(i == 0){
				fields[i].setSize(w, 40);
				fields[i].add(new CenteredLabel(translate(LANG_PREFIX + id + ".type.automatic"), 7, 10, 270, 20));
			}
			else if(i == 1){
				fields[i].setSize(w, 70);
				fields[i].add(new Label(translate(LANG_PREFIX + id + ".type.offset"), 7, 10, 270, 20));
				fields[i].add(new NumberField(updcom, F20 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV, PolyVal.ValAxe.X)));
				fields[i].add(new NumberField(updcom, F21 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV, PolyVal.ValAxe.Y)));
			}
			else if(i == 2){
				fields[i].setSize(w, 130);
				fields[i].add(new Label(translate(LANG_PREFIX + id + ".type.start"), 7, 10, 270, 20));
				fields[i].add(new NumberField(updcom, F20 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_START, PolyVal.ValAxe.X)));
				fields[i].add(new NumberField(updcom, F21 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_START, PolyVal.ValAxe.Y)));
				fields[i].add(new Label(translate(LANG_PREFIX + id + ".type.end"), 7, 70, 270, 20));
				fields[i].add(new NumberField(updcom, F20 - 3, 100, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_END, PolyVal.ValAxe.X)));
				fields[i].add(new NumberField(updcom, F21 - 3, 100, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_END, PolyVal.ValAxe.Y)));
			}
			else{
				fields[i].setSize(w, 250);
				fields[i].add(new Label(translate(LANG_PREFIX + id + ".type.top_right"), 7, 10, 270, 20));
				fields[i].add(new NumberField(updcom, F20 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_TR, PolyVal.ValAxe.X)));
				fields[i].add(new NumberField(updcom, F21 - 3, 40, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_TR, PolyVal.ValAxe.Y)));
				fields[i].add(new Label(translate(LANG_PREFIX + id + ".type.top_left"), 7, 70, 270, 20));
				fields[i].add(new NumberField(updcom, F20 - 3, 100, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_TL, PolyVal.ValAxe.X)));
				fields[i].add(new NumberField(updcom, F21 - 3, 100, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_TL, PolyVal.ValAxe.Y)));
				fields[i].add(new Label(translate(LANG_PREFIX + id + ".type.bot_left"), 7, 130, 270, 20));
				fields[i].add(new NumberField(updcom, F20 - 3, 160, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_BL, PolyVal.ValAxe.X)));
				fields[i].add(new NumberField(updcom, F21 - 3, 160, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_BL, PolyVal.ValAxe.Y)));
				fields[i].add(new Label(translate(LANG_PREFIX + id + ".type.bot_right"), 7, 190, 270, 20));
				fields[i].add(new NumberField(updcom, F20 - 3, 220, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_BR, PolyVal.ValAxe.X)));
				fields[i].add(new NumberField(updcom, F21 - 3, 220, F2S, 20).setup(-4096, 4096, true, new PolygonValue(PolyVal.CUV_BR, PolyVal.ValAxe.Y)));
			}
			UIUtils.hide(fields[i]);
			add(fields[i]);
			//
			add(new RunButton(LANG_PREFIX + id + ".viewer", L5, fullheight - 35, LW, HEIGHT, () -> UVViewer.addIfAbsent()));
		}
	}

	public void updateSelFace(Polygon poly){
		if(poly == null) showField(0, poly);
		else{
			UVCoords cuv = poly.cuv.get(SELECTED);
			if(cuv == null || cuv.automatic()){
				showField(0, poly);
				type.setSelected(UVType.AUTOMATIC.name().toLowerCase(), true);
			}
			else if(cuv.detached()){
				showField(cuv.type().ordinal() - 3, poly);
				type.setSelected(cuv.type().name().toLowerCase(), true);
			}
			else{
				showField(cuv.type().ordinal(), poly);
				type.setSelected(cuv.type().name().toLowerCase(), true);
			}
		}
	}

	private void showField(int idx, Polygon poly){
		for(UVViewer.UVFields field : fields) UIUtils.hide(field);
		UIUtils.show(fields[idx]);
		fields[idx].getChildComponents().forEach(com -> {
			if(com instanceof NumberField) ((NumberField)com).updateValue(poly);
		});
	}

	public static class UVFields extends Component {

		public UVFields(float x, float y, float w, float h){
			setSize(w, h);
			setPosition(x, y);
			getStyle().setBorderRadius(8);
			Settings.applyComponentTheme(this);
			getStyle().setBorder(new SimpleLineBorder(FMT.rgba(0x2ea4e8), 2));
			setFocusable(false);
		}

	}

}
