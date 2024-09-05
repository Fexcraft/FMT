package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.update.UpdateHandler.update;
import static net.fexcraft.app.fmt.settings.Settings.ASK_POLYGON_REMOVAL;
import static net.fexcraft.app.fmt.ui.fields.NumberField.round;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.utils.*;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.Generator;
import net.fexcraft.lib.script.elm.FltElm;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.event.MouseClickEvent;

import com.google.common.collect.ImmutableMap;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.GroupSelectionPanel;
import net.fexcraft.app.fmt.ui.fields.Field;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Model {

	public static long SELECTED_POLYGONS;
	//
	private LinkedHashMap<String, Boolean> authors = new LinkedHashMap<>();
	private ArrayList<Group> allgroups = new ArrayList<>();
	private ArrayList<Pivot> pivots = new ArrayList<>();
	private ArrayList<Polygon> selected = new ArrayList<>();
	private ArrayList<VertexOffset> selected_verts = new ArrayList<>();
	public LinkedHashMap<String, String> export_values = new LinkedHashMap<>();
	public LinkedHashMap<String, ArrayList<String>> export_listed_values = new LinkedHashMap<>();
	public ArrayList<ArrayList<String>> export_group_presets = new ArrayList<>();
	public ArrayList<String> export_group_preset_keys = new ArrayList<>();
	public ModelOrientation orient = ModelOrientation.FVTM4_DEFAULT;
	public ModelFormat format = ModelFormat.UNIVERSAL;
	public Matrix4f matrix;
	public Vector3f pos = new Vector3f();
	public Vector3f rot = new Vector3f();
	public Vector3f scl = new Vector3f(1, 1, 1);
	public TextureGroup texgroup = null;
	public Pivot sel_pivot = null;
	public String texhelper;
	public int texSizeX = 256, texSizeY = 256;
	public boolean visible = true;
	public boolean helper;
	public boolean subhelper;
	public float opacity = 1f;
	public String name;
	public UUID uuid;
	public File file;
	
	public Model(File file, String name){
		this.file = file;
		this.name = name;
		pivots.add(new Pivot("root", true));
		uuid = UUID.randomUUID();
	}

	/** For now just for FMTB files. */
	public Model load(){
		SaveHandler.open(this, file, false);
		update(new ModelLoad(this));
		if(FMT.MODEL == this) FMT.updateTitle();
		return this;
	}
	
	public final void addAuthor(String name, boolean locked){
		authors.put(name, locked);
		update(new ModelAuthor(this, name));
	}
	
	public final Map<String, Boolean> getAuthors(){
		return ImmutableMap.copyOf(authors);
	}
	
	public final boolean hasAuthors(){
		return !authors.isEmpty();
	}

	public long count(boolean selected){
		long am = 0;
		for(Group group : allgroups){
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
		for(Group group : allgroups){
			group.recompile();
		}
	}

	public void bindtex(){
		if(texgroup != null) texgroup.texture.bind();
		else if(texhelper != null) TextureManager.bind(texhelper);
	}

	/*public static final Polyhedron<GLObject> centermarker0 = new Generator<GLObject>(null, Generator.Type.CUBOID)
			.set("x", -.125f).set("y", -16f).set("z", -.125f).set("width", .125f).set("height", 32f).set("depth", .125f).make().setGlObj(new GLObject());
	public static final Polyhedron<GLObject> centermarker1 = new Generator<GLObject>(null, Generator.Type.CUBOID)
			.set("x", -16f).set("y", -.125f).set("z", -.125f).set("width", 32f).set("height", .125f).set("depth", .125f).make().setGlObj(new GLObject());
	public static final Polyhedron<GLObject> centermarker2 = new Generator<GLObject>(null, Generator.Type.CUBOID)
			.set("x", -.125f).set("y", -.125f).set("z", -16f).set("width", .125f).set("height", .125f).set("depth", 32f).make().setGlObj(new GLObject());*/

	private static final Polyhedron<GLObject> centermarker0 = new Generator<GLObject>(null, Generator.Type.CUBOID)
			.set("x", -.125f).set("y", -8f).set("z", -.125f).set("width", .25f).set("height", 16f).set("depth", .25f).make().setGlObj(new GLObject());
	private static final Polyhedron<GLObject> centermarker1 = new Generator<GLObject>(null, Generator.Type.CUBOID)
			.set("x", -8f).set("y", -.125f).set("z", -.125f).set("width", 16f).set("height", .25f).set("depth", .25f).make().setGlObj(new GLObject());
	private static final Polyhedron<GLObject> centermarker2 = new Generator<GLObject>(null, Generator.Type.CUBOID)
			.set("x", -.125f).set("y", -.125f).set("z", -8f).set("width", .25f).set("height", .25f).set("depth", 16f).make().setGlObj(new GLObject());
	static {
		centermarker0.glObj.polycolor = RGB.GREEN.toFloatArray();
		centermarker1.glObj.polycolor = RGB.RED.toFloatArray();
		centermarker2.glObj.polycolor = RGB.BLUE.toFloatArray();
	}

	public void render(FltElm alpha){
		if(!visible) return;
		DrawMode mode = DrawMode.textured(texgroup != null || texhelper != null);
		for(Pivot pivot : pivots){
			PolyRenderer.setPivot(pivot);
			if(Settings.PMARKER.value){
				PolyRenderer.mode(DrawMode.RGBCOLOR);
				centermarker0.render();
				centermarker1.render();
				centermarker2.render();
			}
			for(Group group : pivot.groups){
				group.render(mode, alpha);
				if(Settings.LINES.value) group.render(DrawMode.LINES, alpha);
			}
		}
		PolyRenderer.setPivot(null);
		if(Settings.LINES.value && Settings.POLYMARKER.value && isLastSelectedCornerMarked()) CornerUtil.renderCorners();
	}

	public void renderPicking(){
		if(!visible) return;
		for(Pivot pivot : pivots){
			PolyRenderer.setPivot(pivot);
			for(Group group : pivot.groups){
				group.renderPicking();
			}
			PolyRenderer.setPivot(null);
		}
	}

	public void renderVertexPicking(){
		if(!visible) return;
		for(Pivot pivot : pivots){
			PolyRenderer.setPivot(pivot);
			for(Group group : pivot.groups){
				group.renderVertexPicking();
			}
			PolyRenderer.setPivot(null);
		}
	}

	public void add(String pid, String gid, Polygon poly){
		Group group = null;
		if(gid == null){
			if(allgroups.size() == 0) addGroup(pid, group = new Group(this, "group0", pid));
			else group = allgroups.get(Settings.ADD_TO_LAST.value ? allgroups.size() - 1 : 0);
		}
		else{
			group = get(gid);
			if(group == null) addGroup(pid, group = new Group(this, gid, pid));
		}
		group.add(poly);
		if(!helper && Settings.SELECT_NEW.value) select(poly);
	}

	public Group get(String string){
		for(Group group : allgroups) if(group.id.equals(string)) return group;
		return null;
	}

	public Pivot getP(String id){
		if(id == null) return pivots.get(0);
		for(Pivot pivot : pivots) if(pivot.id.equals(id)) return pivot;
		return getRootPivot();
	}

	public boolean contains(String group){
		return get(group) != null;
	}

	public void addGroup(String pid, String name){
		Pivot pivot = getP(pid);
		Group group = new Group(this, name, pid);
		pivot.groups.add(group);
		allgroups.add(group);
		if(!helper) update(new GroupAdded(this, group));
	}

	public void addGroup(String pid, Group group){
		Pivot pivot = getP(pid);
		group.pivot = pid;
		pivot.groups.add(group);
		allgroups.add(group);
		if(!helper) update(new GroupAdded(this, group));
	}
	
	public void remGroup(int i){
		Group group = allgroups.remove(i);
		Pivot pivot = getP(group.pivot);
		pivot.groups.remove(group);
		update(new GroupRemoved(this, group));
	}
	
	public void remGroup(String id){
		Group group = get(id);
		if(group == null) return;
		remGroup(group);
	}
	
	public void remGroup(Group group){
		if(allgroups.remove(group)){
			getP(group.pivot).groups.remove(group);
			update(new GroupRemoved(this, group));
		}
	}

	public void remPivot(Pivot pivot){
		if(pivot.root) return;
		pivots.remove(pivot);
		Pivot root = getRootPivot();
		String rid = root == null ? null : root.id;
		pivot.groups.forEach(group -> {
			group.pivot = rid;
		});
		update(new PivotRemoved(this, pivot));
	}

	public Pivot getRootPivot(){
		for(Pivot pivot : pivots) if(pivot.root) return pivot;
		return null;
	}

	public ArrayList<Group> allgroups(){
		return allgroups;
	}

	public ArrayList<Pivot> pivots(){
		return pivots;
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
		for(Group group : allgroups){
			if(group.selected) groups.add(group);
		}
		return groups;
	}

	public Group first_selected_group(){
		for(Group group : allgroups){
			if(group.selected) return group;
		}
		return null;
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

	public void updateValue(PolygonValue value, Field field, float alt){
		if(selected.isEmpty()){
			if(field != null) field.value();
			return;
		}
		Polygon poly = selected.get(0);
		float oval = poly.getValue(value);
		float fval = field == null ? oval + alt : field.value();
		poly.setValue(value, round(fval));
		update(new PolygonValueEvent(poly, value, true));
		if(value.doesUpdateMoreFields()) update(new PolygonSelected(poly, selected.size(), selected.size()));
		if(selected.size() > 1){
			float diff = poly.getValue(value) - oval;
			diff = round(diff);
			for(int i = 1; i < selected.size(); i++){
				poly = selected.get(i);
				poly.setValue(value, round(poly.getValue(value) + diff));
				update(new PolygonValueEvent(poly, value, false));
			}
		}
	}

	public void select(Polygon polygon){
		select(polygon, false);
	}

	public void select(Polygon polygon, boolean resel){
		int old = selected.size();
		if(!resel && polygon.selected) polygon.selected = !selected.remove(polygon);
		else{
			if(!resel && !GGR.isAltDown()) clear_selection();
			polygon.selected = selected.add(polygon);
		}
		update(new PolygonSelected(polygon, old, selected.size()));
	}

	private void clear_selection(){
		for(Group group : allgroups){
			if(group.selected) select(group);
		}
		for(Polygon poly : selected){
			poly.selected = false;
			update(new PolygonSelected(poly, -1, -1));
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
		if(!helper) update(new GroupSelected(group, old, selected.size()));
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
			update(new PolygonVisibility(poly, poly.visible));
		});
	}

	public int totalPolygons(){
		int am = 0;
		for(Group group : allgroups){
			am += group.size();
		}
		return am;
	}

	public int totalGroups(){
		return allgroups.size();
	}

	public int totalPivots(){
		return pivots.size();
	}

	public void copySelected(){
		ArrayList<Polygon> selected = selection_copy();
		var bool = Settings.SELECT_COPIED.value;
		ArrayList<Polygon> copied = new ArrayList<>();
		for(Polygon poly : selected){
			Polygon newpoly = poly.copy(null);
			if(bool) copied.add(newpoly);
			this.add(null, "clipboard", newpoly);
		}
		if(bool){
			clear_selection();
			copied.forEach(poly -> select(poly, true));
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
			int format = map.getInteger("format", SaveHandler.FORMAT);
			this.clear_selection();
			boolean external = !map.get("model").string_value().equals(name);
			String model = map.getString("model", "unknown");
			switch(map.get("type").string_value()){
				case "simple-clipboard":{
					String groupto = external ? model + Settings.PASTED_GROUP.value : "clipboard";
					Runnable run = () -> {
						map.get("polygons").asArray().value.forEach(elm -> {
							add(null, groupto, Polygon.from(this, elm.asMap(), format));
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
						for(Entry<String, JsonValue<?>> group : map.get("groups").asMap().value.entrySet()){
							String groupto = external ? model + "-" + group.getKey() + Settings.PASTED_GROUP.value : group.getKey() + "-cb";
							group.getValue().asArray().value.forEach(poly -> {
								add(null, groupto, Polygon.from(this, poly.asMap(), format));
							});
						}
					};
					if(external){
						int polygons = 0;
						for(JsonValue<?> array : map.get("groups").asMap().value.values()){
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
		input.setup(0.001f, 16, true, field -> {
			scale[0] = field.value();
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

	public void name(String string){
		String old = name;
		name = string;
		FMT.updateTitle();
		UpdateHandler.update(new ModelRenamed(this, old, name));
	}

	@Override
	public String toString(){
		return "Model([" + name + "], " + pivots.size() + "/" + allgroups.size() + ")";
	}

	public String getGroupPreset(int idx){
		if(export_group_preset_keys.size() == 0) return "new_preset";
		if(idx < 0) return getGroupPreset(export_group_preset_keys.size() - 1);
		if(idx >= export_group_preset_keys.size()) return getGroupPreset(0);
		return export_group_preset_keys.get(idx);
	}

	public void rotate90(){
		ArrayList<Polygon> polis = selection_copy();
		for(Polygon poly : polis){
			Vector3f vec = new Vector3f(poly.pos);
			poly.pos.x = -vec.z;
			poly.pos.z = -vec.x;
			vec = new Vector3f(poly.rot);
			poly.rot.x = vec.z;
			poly.rot.z = vec.x;
			if(poly instanceof Box){
				Box box = (Box)poly;
				vec = new Vector3f(box.size);
				box.size.x = vec.z;
				box.size.z = vec.x;
				box.off.x = -vec.z;
				box.off.z = -vec.x;
			}
			if(poly instanceof Cylinder){
				Cylinder cyl = (Cylinder)poly;
				if(cyl.direction < 4){
					if(cyl.direction > 1){
						cyl.direction -= 2;
						cyl.off.z -= cyl.length;
					}
					else{
						cyl.direction += 2;
						cyl.off.x -= cyl.length;
					}
				}
			}
			if(poly instanceof Shapebox){
				Shapebox box = (Shapebox)poly;
				Vector3f[] arr = new Vector3f[8];
				Vector3f[] org = box.corners();
				for(int i = 0; i < 8; i++){
					arr[i] = new Vector3f(org[i]);
				}
				box.cor0.set(arr[2]);
				box.cor1.set(arr[1]);
				box.cor2.set(arr[0]);
				box.cor3.set(arr[3]);
				box.cor4.set(arr[6]);
				box.cor5.set(arr[5]);
				box.cor6.set(arr[4]);
				box.cor7.set(arr[7]);
			}
			poly.recompile();
		}
	}

	public void addPivot(Pivot pivot){
		pivots.add(pivot);
	}

	public void select(Pivot pivot){
		sel_pivot = sel_pivot == pivot ? null : pivot;
		UpdateHandler.update(new PivotSelected(this, pivot));
	}

	public void rerootpivots(){
		for(Pivot pivot : pivots) pivot.reroot();
	}

	public void select(VertexOffset off){
		if(!GGR.isAltDown()) selected_verts.clear();
		selected_verts.add(off);
		Logging.bar("Currently selected vertices: " + selected_verts.size());
	}

}