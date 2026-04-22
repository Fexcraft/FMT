package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.oui.UIUtils;
import net.fexcraft.app.fmt.oui.UVViewer;
import net.fexcraft.app.fmt.oui.fields.NumberField;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.polygon.uv.UVType;
import net.fexcraft.app.fmt.ui.DropList;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.ui.TextElm;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.oui.UVViewer.SELECTED;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_FIELD;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class UVEditorTab extends EditorTab {

	public ETabCom general;
	public ETabCom[] uvtype = new ETabCom[UVType.values().length];
	private Field ru, rv;
	private DropList<Face> face;
	private DropList<UVType> type;

	public UVEditorTab(){
		super(EditorRoot.EditorMode.TEXTURE);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add((general = new ETabCom("general")), lang_prefix + "general", 260);
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.root_uv"));
		general.add((ru = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.TEX, PolyVal.ValAxe.X))).pos(F30, next_y_pos(1)));
		general.add((rv = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.TEX, PolyVal.ValAxe.Y))).pos(F31, next_y_pos(0)));
		general.add(new Element().pos(F32, next_y_pos(0)).size(F3S, FS).color(GENERIC_FIELD.value).onclick(ci -> {
			FMT.MODEL.updateValue(ru.polyval(), ru.set(-1), 0);
			FMT.MODEL.updateValue(rv.polyval(), rv.set(-1), 0);
		}).translate(lang_prefix + "general.root_uv_reset").text_centered(true).hoverable(true));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.face"));
		general.add((face = new DropList<>(FF)).pos(FO, next_y_pos(1)));
		face.onchange((key, val) -> {
			UVViewer.SELECTED = val;
			updateSelectedFace(FMT.MODEL.first_selected());
		});
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.type"));
		general.add((type = new DropList<>(FF)).pos(FO, next_y_pos(1)));
		for(UVType uvtype : UVType.values()){
			type.addEntry(uvtype.name().toLowerCase(), uvtype);
		}
		type.onchange(this::updateUVType);
		general.add(new Element().pos(FO, next_y_pos(1.25f)).size(FF, FS).color(GENERIC_FIELD.value)
			.onclick(ci -> UVViewer.addIfAbsent()).translate(lang_prefix + "general.viewer").text_centered(true).hoverable(true));
		//
		for(int idx = 0; idx < UVType.values().length; idx++){
			UVType val = UVType.values()[idx];
			String midfix = "mapping_" + val.name().toLowerCase();
			if(val.automatic()){
				container.add((uvtype[idx] = new ETabCom("type-" + val)), lang_prefix + midfix, 60);
				uvtype[idx].add(new TextElm(0, next_y_pos(-1), FF).translate(lang_prefix + midfix + "_info").text_autoscale());
			}
			else if(val.basic()){
				container.add((uvtype[idx] = new ETabCom("type-" + val)), lang_prefix + midfix, 100);
				uvtype[idx].add(new TextElm(0, next_y_pos(-1), FF).translate(lang_prefix + midfix + ".offset").text_autoscale());
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV, PolyVal.ValAxe.X))).pos(F20, next_y_pos(1)));
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV, PolyVal.ValAxe.Y))).pos(F21, next_y_pos(0)));
			}
			else if(val.ends()){
				container.add((uvtype[idx] = new ETabCom("type-" + val)), lang_prefix + midfix, 160);
				uvtype[idx].add(new TextElm(0, next_y_pos(-1), FF).translate(lang_prefix + midfix + ".start").text_autoscale());
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_START, PolyVal.ValAxe.X))).pos(F20, next_y_pos(1)));
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_START, PolyVal.ValAxe.Y))).pos(F21, next_y_pos(0)));
				uvtype[idx].add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + midfix + ".end").text_autoscale());
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_END, PolyVal.ValAxe.X))).pos(F20, next_y_pos(1)));
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_END, PolyVal.ValAxe.Y))).pos(F21, next_y_pos(0)));
			}
			else{
				container.add((uvtype[idx] = new ETabCom("type-" + val)), lang_prefix + midfix, 280);
				uvtype[idx].add(new TextElm(0, next_y_pos(-1), FF).translate(lang_prefix + midfix + ".top_right").text_autoscale());
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_TR, PolyVal.ValAxe.X))).pos(F20, next_y_pos(1)));
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_TR, PolyVal.ValAxe.Y))).pos(F21, next_y_pos(0)));
				uvtype[idx].add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + midfix + ".top_left").text_autoscale());
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_TL, PolyVal.ValAxe.X))).pos(F20, next_y_pos(1)));
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_TL, PolyVal.ValAxe.Y))).pos(F21, next_y_pos(0)));
				uvtype[idx].add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + midfix + ".bot_left").text_autoscale());
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_BL, PolyVal.ValAxe.X))).pos(F20, next_y_pos(1)));
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_BL, PolyVal.ValAxe.Y))).pos(F21, next_y_pos(0)));
				uvtype[idx].add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + midfix + ".bot_right").text_autoscale());
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_BR, PolyVal.ValAxe.X))).pos(F20, next_y_pos(1)));
				uvtype[idx].add((new Field(Field.FieldType.FLOAT, F2S, updcom, new PolygonValue(PolyVal.CUV_BR, PolyVal.ValAxe.Y))).pos(F21, next_y_pos(0)));
			}
			uvtype[idx].visible = false;
		}
		//
		updcom.add(UpdateEvent.PolygonSelected.class, e -> {
			face.clear();
			face.addEntry("none", NoFace.NONE);
			Polygon poly = FMT.MODEL.first_selected();
			if(poly != null){
				poly.cuv.keySet().forEach(key -> {
					face.addEntry(key, Face.get(key, true));
				});
				if(poly.isValidUVFace(UVViewer.SELECTED)) face.selectValue(UVViewer.SELECTED);
				else face.selectValue(UVViewer.SELECTED = poly.getUVFaces()[0]);
			}
			else {
				face.selectEntry(0);
				UVViewer.SELECTED = NoFace.NONE;
			}
			updateSelectedFace(poly);
		});
		updcom.add(UpdateEvent.PickFace.class, e -> {
			face.clear();
			face.addEntry("none", NoFace.NONE);
			Polygon poly = e.polygon();
			poly.cuv.keySet().forEach(key -> face.addEntry(key, Face.get(key, true)));
			face.selectValue(UVViewer.SELECTED = e.face());
			updateSelectedFace(poly);
		});
	}

	private void updateSelectedFace(Polygon poly){
		if(poly == null) showCom(UVType.AUTOMATIC);
		else{
			UVCoords cuv = poly.cuv.get(UVViewer.SELECTED);
			if(cuv == null || cuv.automatic()){
				showCom(UVType.AUTOMATIC);
				type.selectValue(UVType.AUTOMATIC);
			}
			else{
				showCom(cuv.type());
				type.selectValue(cuv.type());
			}
		}
	}

	private void updateUVType(String key, UVType val){
		for(Polygon poly : FMT.MODEL.selected()){
			UVCoords cor = poly.cuv.get(UVViewer.SELECTED);
			if(cor != null && cor.type() != val){
				cor.set(val);
				float[][][] poss = poly.newUV(false, false);
				float[][] pos = poss[UVViewer.SELECTED.index()];
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
				UpdateHandler.update(new UpdateEvent.PolygonUVType(poly, val));
			}
		}
		if(FMT.MODEL.first_selected() != null){
			UpdateHandler.update(new UpdateEvent.PolygonSelected(FMT.MODEL.first_selected(), 0, 0));
		}
		showCom(val);
	}

	private void showCom(UVType type){
		for(ETabCom com : uvtype) com.visible = false;
		uvtype[type.ordinal()].visible = true;
		reorderComponents();
	}

}
