package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.attributes.UpdateHandler.update;
import static net.fexcraft.app.fmt.attributes.UpdateType.MODEL_AUTHOR;
import static net.fexcraft.app.fmt.attributes.UpdateType.MODEL_LOAD;
import static net.fexcraft.app.fmt.settings.Settings.ASK_POLYGON_REMOVAL;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.joml.Vector3f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.MouseClickEvent;

import com.google.common.collect.ImmutableMap;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.GroupSelectionPanel;
import net.fexcraft.app.fmt.ui.fields.Field;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.utils.CornerUtil;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.MRTRenderer.DrawMode;
import net.fexcraft.app.fmt.utils.SaveHandler;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Model {

	public static long SELECTED_POLYGONS;
	//
	private LinkedHashMap<String, Boolean> authors = new LinkedHashMap<>();
	private ArrayList<Group> groups = new ArrayList<>();
	private ArrayList<Polygon> selected = new ArrayList<>();
	public ModelOrientation orient = ModelOrientation.CLASSIC;
	public ModelFormat format = ModelFormat.UNIVERSAL;
	public Vector3f pos = new Vector3f();
	public Vector3f rot = new Vector3f();
	public TextureGroup texgroup = null;
	public String texhelper;
	public int texSizeX = 256, texSizeY = 256;
	public boolean visible = true, subhelper;
	public float opacity = 1f;
	public Vector3f scale;
	public String name;
	public File file;
	
	public Model(File file, String name){
		this.file = file;
		this.name = name;
	}
	
	/** For now just for FMTB files. */
	public Model load(){
		SaveHandler.open(this, file);
		update(MODEL_LOAD, this);
		if(FMT.MODEL == this) FMT.updateTitle();
		return this;
	}
	
	public final void addAuthor(String name, boolean locked){
		authors.put(name, locked);
		update(MODEL_AUTHOR, name);
	}
	
	public final Map<String, Boolean> getAuthors(){
		return ImmutableMap.copyOf(authors);
	}
	
	public final boolean hasAuthors(){
		return !authors.isEmpty();
	}

	public long count(boolean selected){
		long am = 0;
		for(Group group : groups){
			if(selected && !group.selected){
				for(Polygon poly : group){
					if(poly.selected) am++;
				}
			}
			else am += group.size();
		}
		return am;
	}
	
	public boolean isHelper(){
		return subhelper || FMT.MODEL != this;
	}

	public void recompile(){
		for(Group group : groups){
			group.recompile();
		}
	}

	public void bindtex(){
		if(texgroup != null) texgroup.texture.bind();
	}

	public void render(){
		if(!visible) return;
		DrawMode mode = DrawMode.textured(texgroup != null);
		for(Group group : groups){
			group.render(mode);
			if(Settings.LINES.value) group.render(DrawMode.LINES);
		}
		if(Settings.LINES.value && isLastSelectedCornerMarked()) CornerUtil.renderCorners();
	}

	public void renderPicking(){
		if(!visible) return;
		for(Group group : groups){
			group.renderPicking();
		}
	}

	public void add(String groupid, Polygon poly){
		Group group = null;
		if(groupid == null){
			if(groups.size() == 0) addGroup(group = new Group(this, "group0"));
			else group = groups.get(Settings.ADD_TO_LAST.value ? groups.size() - 1 : 0);
		}
		else{
			group = get(groupid);
			if(group == null) addGroup(group = new Group(this, groupid));
		}
		group.add(poly);
	}

	public Group get(String string){
		for(Group group : groups) if(group.id.equals(string)) return group;
		return null;
	}

	public boolean contains(String group){
		return get(group) != null;
	}

	public void addGroup(String name){
		Group group = new Group(this, name);
		groups.add(group);
		update(UpdateType.GROUP_ADDED, new Object[]{ this, group });
	}

	public void addGroup(Group group){
		groups.add(group);
		update(UpdateType.GROUP_ADDED, new Object[]{ this, group });
	}
	
	public void remGroup(int i){
		Group group = groups.remove(i);
		update(UpdateType.GROUP_REMOVED, new Object[]{ this, group });
	}
	
	public void remGroup(String id){
		Group group = get(id);
		if(group == null) return;
		remGroup(group);
	}
	
	public void remGroup(Group group){
		if(groups.remove(group)){
			update(UpdateType.GROUP_REMOVED, new Object[]{ this, group });
		}
	}
	
	public ArrayList<Group> groups(){
		return groups;
	}

	public ArrayList<Polygon> selected(){
		/*var list = new ArrayList<Polygon>();
		for(Group group : groups){
			if(group.selected) list.addAll(group);
			else{
				for(Polygon poly : group){
					if(poly.selected) list.add(poly);
				}
			}
		}
		return list;*/
		return selected;
	}

	public ArrayList<Group> selected_groups(){
		ArrayList<Group> groups = new ArrayList<>();
		for(Group group : this.groups){
			if(group.selected) groups.add(group);
		}
		return groups;
	}

	public ArrayList<Polygon> selection_copy(){
		var list = new ArrayList<Polygon>();
		list.addAll(selected);
		return list;
	}
	
	public Polygon first_selected(){
		return selected.isEmpty() ? null : selected.get(0);
	}
	
	public Polygon last_selected(){
		return selected.isEmpty() ? null : selected.get(selected.size() - 1);
	}

	private boolean isLastSelectedCornerMarked(){
		return selected.size() > 0 && selected.get(selected.size() - 1) instanceof Shapebox;
	}

	public void updateValue(PolygonValue value, Field field){
		if(selected.isEmpty()){
			field.value();
			return;
		}
		Polygon poly = selected.get(0);
		float curr = poly.getValue(value);
		float fval = field.value();
		poly.setValue(value, fval);
		update(UpdateType.POLYGON_VALUE, poly, value);
		if(selected.size() > 1){
			for(int i = 1; i < selected.size(); i++){
				poly = selected.get(i);
				float diff = poly.getValue(value) - curr;
				poly.setValue(value, fval + diff);
				update(UpdateType.POLYGON_VALUE, poly, value);
			}
		}
	}

	public void select(Polygon polygon){
		int old = selected.size();
		if(polygon.selected) polygon.selected = !selected.remove(polygon);
		else{
			if(!GGR.isAltDown()) clear_selection();
			polygon.selected = selected.add(polygon);
		}
		update(UpdateType.POLYGON_SELECTED, polygon, old, selected.size());
	}

	private void clear_selection(){
		for(Group group : groups){
			if(group.selected) select(group);
		}
		for(Polygon poly : selected){
			poly.selected = false;
			update(UpdateType.POLYGON_SELECTED, poly, -1);
		}
		selected.clear();
	}

	public void select(Group group){
		int old = selected.size();
		if(group.selected){
			selected.removeAll(group);
			group.selected = false;
			group.forEach(poly -> poly.selected = false);
		}
		else{
			if(!GGR.isAltDown()){
				clear_selection();
			}
			selected.addAll(group);
			group.selected = true;
		}
		update(UpdateType.GROUP_SELECTED, group, old, selected.size());
	}

	public void delsel(){
		ArrayList<Polygon> selected = selection_copy();
		Runnable rem = () -> selected.removeIf(poly -> poly.group().remove(poly));
		if(ASK_POLYGON_REMOVAL.value){
			GenericDialog.showOC(null, rem, null, "model.delete.remove_selected_polygons", selected.size() + "");
		}
		else rem.run();
	}

	public void hidesel(){
		ArrayList<Polygon> selected = selection_copy();
		selected.forEach(poly -> {
			poly.visible = !poly.visible;
			update(UpdateType.POLYGON_VISIBLITY, poly, poly.visible);
		});
	}

	public int totalPolygons(){
		int am = 0;
		for(Group group : groups){
			am += group.size();
		}
		return am;
	}

	public int totalGroups(){
		return groups.size();
	}

	public void copySelected(){
		ArrayList<Polygon> selected = selection_copy();
		var bool = Settings.SELECT_COPIED.value;
		ArrayList<Polygon> copied = new ArrayList<>();
		for(Polygon poly : selected){
			Polygon newpoly = poly.copy(null);
			if(bool) copied.add(newpoly);
			this.add("clipboard", newpoly);
		}
		if(bool){
			clear_selection();
			copied.forEach(poly -> select(poly));
		}
	}

	public void copyToClipboard(boolean hierarchy){
		ArrayList<Polygon> selected = selection_copy();
		if(selected.isEmpty()) return;
		JsonMap map = new JsonMap();
		map.add("origin", "fmt");
		map.add("version", FMT.VERSION);
		map.add("model", name);
		map.add("type", hierarchy ? "grouped-clipboard" : "simple-clipboard");
		if(hierarchy){
			JsonMap groups = new JsonMap();
			for(Polygon polygon : selected){
				if(!groups.has(polygon.group().id)) groups.addArray(polygon.group().id);
				groups.getArray(polygon.group().id).add(polygon.save(false));
			}
			map.add("groups", groups);
		}
		else{
			JsonArray array = new JsonArray();
			for(Polygon polygon : selected){
				array.add(polygon.save(false));
			}
			map.add("polygons", array);
		}
		Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection sel = new StringSelection(map.toString());
		cp.setContents(sel, sel);
	}

	public void pasteFromClipboard(){
		Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable data = cp.getContents(null);
		if(!data.isDataFlavorSupported(DataFlavor.stringFlavor)) return;
		try{
			String str = data.getTransferData(DataFlavor.stringFlavor).toString();
			if(!str.startsWith("{")) return;
			JsonMap map = JsonHandler.parse(str, true).asMap();
			if(!map.has("origin") && !map.get("origin").string_value().contains("fmt")) return;
			if(!map.has("type") || !map.has("model")) return;
			this.clear_selection();
			boolean external = !map.get("model").string_value().equals(name);
			String model = map.getString("model", "unknown");
			switch(map.get("type").string_value()){
				case "simple-clipboard":{
					String groupto = external ? model + Settings.PASTED_GROUP.value : "clipboard";
					Runnable run = () -> {
						map.get("polygons").asArray().value.forEach(elm -> {
							add(groupto, Polygon.from(this, elm.asMap()));
						});
					};
					if(external){
						GenericDialog.showYN("model.clipboard.paste_external", run, null,
							"#ORIGIN: " + map.get("origin").string_value() + " " + map.getString("version", ""),
							"#MODEL: " + model,
							"#POLYGONS: " + map.get("polygons").asArray().size()
						);
					}
					else run.run();
					return;
				}
				case "grouped-clipboard":{
					Runnable run = () -> {
						for(Entry<String, JsonObject<?>> group : map.get("groups").asMap().value.entrySet()){
							String groupto = external ? model + "-" + group.getKey() + Settings.PASTED_GROUP.value : group.getKey() + "-cb";
							group.getValue().asArray().value.forEach(poly -> {
								add(groupto, Polygon.from(this, poly.asMap()));
							});
						}
					};
					if(external){
						int polygons = 0;
						for(JsonObject<?> array : map.get("groups").asMap().value.values()){
							polygons += array.asArray().size();
						}
						GenericDialog.showYN("model.clipboard.paste_external_grouped", run, null,
							"#ORIGIN: " + map.get("origin").string_value() + " " + map.getString("version", ""),
							"#MODEL: " + model,
							"#GROUPS: " + map.get("groups").asMap().size(),
							"#POLYGONS: " + polygons
						);
					}
					else run.run();
					return;
				}
				default: return;
			}
		}
		catch(UnsupportedFlavorException | IOException e){
			Logging.log(e);
		}
	}

	public void flipShapeboxes(Collection<Polygon> collection, int axe){
		Collection<Polygon> polygons = collection != null ? collection : selection_copy().stream().filter(pre -> pre.getShape().isShapebox()).collect(Collectors.toList());
		for(Polygon polygon : polygons){
			if(polygon instanceof Shapebox == false) continue;
			Vector3f[] copy = new Vector3f[8];
			Shapebox shapebox = (Shapebox)polygon;
			copy[0] = shapebox.cor0;
			copy[1] = shapebox.cor1;
			copy[2] = shapebox.cor2;
			copy[3] = shapebox.cor3;
			copy[4] = shapebox.cor4;
			copy[5] = shapebox.cor5;
			copy[6] = shapebox.cor6;
			copy[7] = shapebox.cor7;
			switch(axe){
				case 0:{
					shapebox.cor0 = copy[3];
					shapebox.cor1 = copy[2];
					shapebox.cor2 = copy[1];
					shapebox.cor3 = copy[0];
					shapebox.cor4 = copy[7];
					shapebox.cor5 = copy[6];
					shapebox.cor6 = copy[5];
					shapebox.cor7 = copy[4];
					break;
				}
				case 1:{
					shapebox.cor0 = copy[4];
					shapebox.cor1 = copy[5];
					shapebox.cor2 = copy[6];
					shapebox.cor3 = copy[7];
					shapebox.cor4 = copy[0];
					shapebox.cor5 = copy[1];
					shapebox.cor6 = copy[2];
					shapebox.cor7 = copy[3];
					break;
				}
				case 2:{
					shapebox.cor0 = copy[1];
					shapebox.cor1 = copy[0];
					shapebox.cor2 = copy[3];
					shapebox.cor3 = copy[2];
					shapebox.cor4 = copy[5];
					shapebox.cor5 = copy[4];
					shapebox.cor6 = copy[7];
					shapebox.cor7 = copy[6];
					break;
				}
			}
			shapebox.recompile();
			continue;
		}
		//TODO update event/s
		return;
	}

	public void flipBoxPosition(Collection<Polygon> collection, int axe){
		Collection<Polygon> polygons = collection != null ? collection : selection_copy();
		for(Polygon polygon : polygons){
			if(!polygon.getShape().isRectagular()) continue;
			Box box = (Box)polygon;
			switch(axe){
				case 0:{
					box.pos.x += box.size.x;
					box.pos.x = -box.pos.x;
					break;
				}
				case 1:{
					box.pos.y += box.size.y;
					box.pos.y = -box.pos.y;
					break;
				}
				case 2:{
					box.pos.z += box.size.z;
					box.pos.z = -box.pos.z;
					break;
				}
			}
			box.recompile();
		}
		//TODO update event/s
		return;
	}

	public void rescale(){
		int width = 420, height = 410;
		Dialog dialog = new Dialog(translate("model.rescale.dialog"), width, 0);
		Settings.applyComponentTheme(dialog.getContainer());
		dialog.getContainer().add(new Label(translate("model.rescale.scale"), 10, 10, width - 20, 20));
		NumberField input = new NumberField((EditorComponent)null, 10, 30, width - 20, 20);
		float[] scale = { 1 };
		input.setup(0.001f, 16, true, () -> {
			scale[0] = input.value();
		});
		input.apply(scale[0]);
		dialog.getContainer().add(input);
		dialog.getContainer().add(new Label(translate("model.rescale.groups"), 10, 60, width - 20, 20));
		GroupSelectionPanel panel = new GroupSelectionPanel(10, 80, width - 20, 200);
		dialog.getContainer().add(panel);
		Label label = null;
		dialog.getContainer().add(label = new Label(translate("model.rescale.warning0"), 10, 290, width - 20, 20));
		label.getStyle().setFont("roboto-bold");
		dialog.getContainer().add(label = new Label(translate("model.rescale.warning1"), 10, 310, width - 20, 20));
		label.getStyle().setFont("roboto-bold");
		dialog.getContainer().add(label = new Label(translate("model.rescale.warning2"), 10, 330, width - 20, 20));
		label.getStyle().setFont("roboto-bold");
		Button button0 = new Button(translate("dialog.button.confirm"), 10, 360, 100, 20);
		button0.getListenerMap().addListener(MouseClickEvent.class, lis -> {
			if(lis.getAction() == CLICK){
				rescale0(panel.getSelectedGroups(), scale[0]);
				dialog.close();
			}
		});
		dialog.getContainer().add(button0);
		Button button1 = new Button(translate("dialog.button.cancel"), 120, 360, 100, 20);
		button1.getListenerMap().addListener(MouseClickEvent.class, lis -> {
			if(lis.getAction() == CLICK) dialog.close();
		});
		dialog.getContainer().add(button1);
		dialog.setSize(width, height);
		dialog.setResizable(false);
		dialog.show(FMT.FRAME);
	}

	public void rescale0(ArrayList<Group> selected, float scale){
		for(Group group : selected){
			ArrayList<Polygon> boxes = (ArrayList<Polygon>)group.stream().filter(wrapper -> wrapper.getShape() == Shape.BOX).collect(Collectors.toList());
			group.removeAll(boxes);
			boxes.forEach(box -> group.add(box.convert(Shape.SHAPEBOX)));
			for(Polygon poly : group){
				scalePolygon(poly, scale);
			}
		}
		//TODO update event/s
	}

	private void scalePolygon(Polygon poly, float scale){
		poly.pos.mul(scale);
		poly.off.mul(scale);
		if(poly instanceof Shapebox){
			Shapebox sb = (Shapebox)poly;
			sb.size.mul(scale);
			sb.cor0.mul(scale);
			sb.cor1.mul(scale);
			sb.cor2.mul(scale);
			sb.cor3.mul(scale);
			sb.cor4.mul(scale);
			sb.cor5.mul(scale);
			sb.cor6.mul(scale);
			sb.cor7.mul(scale);
		}
		if(poly instanceof Cylinder){
			Cylinder cyl = (Cylinder)poly;
			cyl.radius *= scale;
			cyl.radius2 *= scale;
			cyl.length *= scale;
			if(cyl.topoff != null && !(cyl.topoff.x == 0f && cyl.topoff.y == 0f && cyl.topoff.z == 0f)){
				cyl.topoff.mul(scale);
			}
		}
		poly.recompile();
	}

}
