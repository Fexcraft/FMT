package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;
import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.polygon.uv.UVType;
import net.fexcraft.app.fmt.texture.Texture;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.BoolElm;
import net.fexcraft.app.fmt.ui.Dialog.DialogButton;
import net.fexcraft.app.fmt.ui.DropList;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.lib.common.math.RGB;

public class AutoUVPositioner {

	public static boolean HALT = true, ALL, SAVESPACE, DETACH;
	private static ArrayList<CoordContainer> list;
	private static Group resetsel;
	private static net.fexcraft.app.fmt.ui.Dialog dialog;
	private static Texture texture;
	private static int last;

	public static void runReset(boolean cUV){
		int width = 440;
		resetsel = null;
		String suffix = cUV ? "_type" : "";
		/*Dialog dialog = new Dialog(translate("texture_autopos.reset" + suffix + ".dialog"), width + 20, 180);
		Label label0 = new Label(translate("texture_autopos.reset" + suffix + ".info"), 10, 10, width, 20);
		label0.getStyle().setFont("roboto-bold");
		Label label1 = new Label(translate("texture_autopos.reset" + suffix + ".group"), 10, 40, width / 20, 20);
		SelectBox<String> texture = new SelectBox<>(10 + width / 2, 40, width / 2, 20);
		texture.addElement("all-groups");
		for(Group group : FMT.MODEL.allgroups()) texture.addElement(group.id);
		texture.addSelectBoxChangeSelectionEventListener(listener -> {
			if(listener.getNewValue().equals("all-groups")) resetsel = null;
			else resetsel = FMT.MODEL.get(listener.getNewValue());
		});
		texture.setSelected(0, true);
		texture.setVisibleCount(12);
		Button button = new Button(translate("dialog.button.confirm"), 10, 130, 100, 20);
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(e.getAction() != MouseClickAction.CLICK) return;
			if(resetsel != null){
				if(cUV){
					resetsel.forEach(poly -> {
						//TODO reset cuv
						poly.recompile();
					});
				}
				else{
					resetsel.forEach(poly -> {
						poly.textureX = -1;
						poly.textureY = -1;
						poly.recompile();
					});
				}
			}
			else{
				if(cUV){
					FMT.MODEL.allgroups().forEach(group -> group.forEach(poly -> {
						//TODO reset cuv
						poly.recompile();
					}));
				}
				else{
					FMT.MODEL.allgroups().forEach(group -> group.forEach(poly -> {
						poly.textureX = -1;
						poly.textureY = -1;
						poly.recompile();
					}));
				}
			}
			dialog.close();
			//TODO GenericDialog.showOK(null, null, null, "texture_autopos.reset" + suffix + ".done");
		});
		dialog.getContainer().add(label0);
		dialog.getContainer().add(label1);
		dialog.getContainer().add(texture);
		dialog.getContainer().add(button);*/
		//dialog.show(FMT.FRAME);
	}

	public static void runAutoPos(){
		int width = 640;
		int height = 270;
		DropList<TextureGroup> tex = new DropList<>(width);
		BoolElm save = new BoolElm();
		BoolElm pall = new BoolElm();
		BoolElm deta = new BoolElm();
		FMT.UI.createDialog(width + 20, height, "texture_pos.auto.title")
			.addBoldText(0, "texture_pos.auto.warning")
			.addText(1.5f, "texture_pos.auto.texture_group")
			.addRowElm(2.5f, tex, width)
			.addCheck(4, save, "texture_pos.auto.savespace")
			.addCheck(5, pall, "texture_pos.auto.process_all")
			.addCheck(6, deta, "texture_pos.auto.detach_all")
			.consumer(d -> startAutoPos(tex.getSelVal()), null)
			.buttons(200, DialogButton.CONTINUE, DialogButton.CANCEL);
		save.set(() -> SAVESPACE, b -> SAVESPACE = b);
		pall.set(() -> !ALL, b -> ALL = !b);
		deta.set(() -> DETACH, b -> DETACH = b);
		if(TextureManager.getGroups().isEmpty()) TextureManager.addGroup(null, false);
		for(TextureGroup group : TextureManager.getGroups()){
			tex.addEntry(group.name, group);
		}
		tex.selectEntry(0);
	}

	private static void startAutoPos(TextureGroup texgroup){
		new Thread("AutoPosThread"){
			@Override
			public void run(){
				log("STARTING AUTOPOS THREAD");
				HALT = false;
				boolean selmod = texgroup == FMT.MODEL.texgroup;
				List<Group> selected = FMT.MODEL.allgroups().stream().filter(g -> g.texgroup == texgroup || (g.texgroup == null && selmod)).toList();
				final int sizex = selmod ? FMT.MODEL.texSizeX : selected.get(0).texSizeX;
				final int sizey = selmod ? FMT.MODEL.texSizeY : selected.get(0).texSizeY;
				log("Setting size to " + sizex + "x, " + sizey + "y.");
				log("TexGroup selected: " + texgroup.name);
				log("All-Polygons is set to '" + ALL + "' and Save-Space is set to '" + SAVESPACE + "'. Detach is set to '" + DETACH + "'.");
				if(list == null){
					list = getSortedList(selected, ALL);
					last = 0;
					if((texture = TextureManager.get("auto-pos-temp", true)) != null){
						texture.getImage().rewind();
						texture.resize(sizex, sizey);
						log("Resized auto-pos temp texture.");
					}
					else{
						texture = TextureManager.createTexture("auto-pos-temp", sizex, sizey);
						texture.setFile(new File("./temp/auto-pos-temp.png"));
						log("Created auto-pos temp texture.");
					}
					texture.clear(RGB.WHITE.toByteArray());
					log("AutoPosTex size is " + sizex + ", " + sizey + ".");
				}
				while(!HALT && last >= 0 && last < list.size()){
					try{
						CoordContainer corcon = list.get(last);
						last++;
						final float percent = (last * 100f) / list.size();
						FMT.queue(() -> showPercentDialog(corcon.polygon.group().id, corcon.name(), percent));
						if(corcon.coords == null || corcon.coords.length == 0){
							log("skipping [" + corcon.polygon.group().id + ":" + corcon.name() + "] (missing texture definition)");
							continue;
						}
						boolean whole = !corcon.poly();
						if((corcon.polygon.textureX > -1 && corcon.polygon.textureY > -1) && (!ALL || (!DETACH && !whole && corcon.polygon.cuv.get(corcon.face).detached()))){
							log("skipping [" + corcon.polygon.group().id + ":" + corcon.name() + "] (texture not -1x -1y)");
							if(!corcon.exclude && whole){
								corcon.polygon.paintTex(texture, null);
							}
							else if(corcon.exclude){
								corcon.polygon.paintTex(texture, null, corcon.coords, false, null);
							}
							else{
								corcon.polygon.paintTex(texture, null, corcon.coords, true, corcon.face.index());
							}
							Thread.sleep(10);
							continue;
						}
						//
						boolean pass = false;
						for(int yar = 0; yar < sizey; yar++){
							if(pass) break;
							for(int xar = 0; xar < sizex; xar++){
								if(check(corcon.coords, xar, yar)){
									log("[" + corcon.polygon.group().id + ":" + corcon.name() + "] >> " + xar + "x, " + yar + "y;");
									if(!corcon.poly()){
										corcon.polygon.textureX = xar;
										corcon.polygon.textureY = yar;
										//polygon.texpos = polygon.newTexturePosition(true, false);
										corcon.polygon.paintTex(texture, null);
									}
									else{
										UVCoords coords = corcon.polygon.cuv.get(corcon.face);
										UVType type = coords.type();
										float[] arr = type.automatic() ? new float[2] : coords.value();
										switch(type){
											case AUTOMATIC:
											case DETACHED:
											case OFFSET:{
												type = UVType.DETACHED;
												arr[0] = xar;
												arr[1] = yar;
												break;
											}
											case DETACHED_ENDS:
											case OFFSET_ENDS:{
												float minx = arr[0], miny = arr[1];
												arr[0] += xar - minx;
												arr[1] += yar - miny;
												arr[2] += xar - minx;
												arr[3] += yar - miny;
												type = UVType.DETACHED_ENDS;
												break;
											}
											case DETACHED_FULL:
											case OFFSET_FULL:{
												float minx = arr[2], miny = arr[3];
												arr[0] += xar - minx;
												arr[1] += yar - miny;
												arr[2] += xar - minx;
												arr[3] += yar - miny;
												arr[4] += xar - minx;
												arr[5] += yar - miny;
												arr[6] += xar - minx;
												arr[7] += yar - miny;
												type = UVType.DETACHED_FULL;
												break;
											}
										}
										coords.set(type).value(arr);
										float[][] newarr = corcon.polygon.newUV(true, false)[corcon.face.index()];
										corcon.polygon.paintTex(texture, null, new float[][][]{ newarr }, true, corcon.face.index());
									}
									pass = true;
									Thread.sleep(10);
									break;
								}
							}
						}
						if(!pass){
							log("[" + corcon.polygon.group().id + ":" + corcon.name() + "] >> could not be mapped;");
						}
					}
					catch(Exception e){
						log(e);
					}
				}
				FMT.queue(() -> {
					FMT.UI.createDialog(400, 80, "texture_pos.auto.title")
						.addText(0, "texture_pos.auto.complete")
						.consumer(d -> FMT.MODEL.recompile(), null)
						.buttons(100, DialogButton.OK);
				});
				last = (HALT = (list = null) == null) ? -1 : 0;
				texture = null;
				log("STOPPING AUTOPOS THREAD");
				return;
			}
		}.start();
	}

	private static void showPercentDialog(String group, String polygon, float percent){
		if(dialog == null ){
			dialog = FMT.UI.createDialog(510, 80, "texture_pos.auto.title")
				.addText(0, "texture_pos.auto.processing", "", "")
				.addRowElm(1, new Element().size(501, FS).color(GENERIC_BACKGROUND_0.value).border(GENERIC_BACKGROUND_2.value));
			dialog.container.lastElement().add(new Element().color(POLYGON_NORMAL.value).size(1, 1));
			dialog.on_close = d -> dialog = null;
		}
		dialog.container.elements.get(0).translate("texture_pos.auto.processing", group, polygon);
		dialog.container.elements.get(1).elements.get(0).size(percent * 5, FS).recompile();
	}

	private static boolean check(float[][][] texpos, int xx, int yy){
		float[][] ends = null;
		for(int i = 0; i < texpos.length; i++){
			ends = texpos[i];
			if(ends == null) continue;
			if(!SAVESPACE){
				float[][] newend = new float[ends.length][];
				for(int k = 0; k < newend.length; k++){
					if(newend[k] == null){
						newend[k] = new float[ends[k].length];
					}
					for(int l = 0; l < newend[k].length; l++){
						newend[k][l] = ends[k][l];
					}
				}
				ends = newend;
				//
				ends[0][0] -= 1;
				ends[1][0] += 1;// x
				ends[0][1] -= 1;
				ends[1][1] += 1;// y
			}
			for(float y = ends[0][1]; y < ends[1][1]; y += 0.5f){
				for(float x = ends[0][0]; x < ends[1][0]; x += 0.5f){
					int xr = (int)(xx + x), yr = (int)(yy + y);
					if(xr < 0 || yr < 0) continue;
					if(xr >= texture.getWidth() || yr >= texture.getHeight()){
						return false;
					}
					if(!texture.equals(xr, yr, RGB.WHITE.toByteArray(), false)){
						return false;
					}
					else continue;
				}
			}
		}
		return true;
	}

	private static ArrayList<CoordContainer> getSortedList(List<Group> selected, boolean all){
		ArrayList<CoordContainer> list = new ArrayList<>();
		for(Group group : selected) addAll(list, group);
		list.sort(new java.util.Comparator<CoordContainer>(){
			@Override
			public int compare(CoordContainer left, CoordContainer righ){
				left.initMinMax();
				righ.initMinMax();
				float x0 = left.max_x - left.min_x;
				float x1 = righ.max_x - righ.min_x;
				float y0 = left.max_y - left.min_y;
				float y1 = righ.max_x - righ.min_y;
				if(Float.compare(x0, x1) > 1){ return Float.compare(y0, y1); }
				return Float.compare(x0, x1);
			}
		});
		Collections.reverse(list);
		if(!all){
			ArrayList<CoordContainer> pri = (ArrayList<CoordContainer>)list.stream().filter(con -> con.positioned()).collect(Collectors.toList());
			ArrayList<CoordContainer> sec = (ArrayList<CoordContainer>)list.stream().filter(con -> !con.positioned()).collect(Collectors.toList());
			list.clear();
			list.addAll(pri);
			list.addAll(sec);
		}
		return list;
	}

	private static void addAll(ArrayList<CoordContainer> list, Group group){
		for(Polygon polygon : group){
			if(!polygon.getShape().isTexturable()) continue;
			boolean detach = DETACH && polygon.getShape().isRectagular();
			if(detach || polygon.cuv.anyDetached()){
				for(UVCoords coord : polygon.cuv.values()){
					if(!polygon.isActive(coord.face())) continue;//face is disabled
					if(detach || coord.detached()){
						list.add(new CoordContainer(polygon, coord.side(), detach && !coord.detached()));
					}
				}
				if(!detach && !polygon.cuv.allDetached()){
					list.add(new CoordContainer(polygon, true));
				}
				if(detach) polygon.textureX = polygon.textureY = -1;
			}
			else{
				list.add(new CoordContainer(polygon, false));
			}
		}
	}
	
	public static class CoordContainer {

		public float min_x, min_y, max_x, max_y;
		public boolean detached, exclude;
		public float[][][] coords;
		public Polygon polygon;
		public Face face;
		
		public CoordContainer(Polygon polygon, boolean excempt){
			this.polygon = polygon;
			coords = polygon.newUV(true, exclude = excempt);
		}

		public CoordContainer(Polygon polygon, Face key, boolean detach){
			this.polygon = polygon;
			coords = new float[][][]{ polygon.newUV(true, false)[(face = key).index()] };
			if(detached = detach){
				coords[0][1][0] -= coords[0][0][0];
				coords[0][1][1] -= coords[0][0][1];
				coords[0][0][0] = coords[0][0][1] = 0;
			}
		}

		public void initMinMax(){
			for(float[][] arr : coords){
				if(arr == null) continue;
				for(float[] xy : arr){
					if(xy[0] > max_x) max_x = xy[0];
					if(xy[1] > max_y) max_y = xy[1];
					if(xy[0] < min_x) min_x = xy[0];
					if(xy[1] < min_y) min_y = xy[1];
				}
			}
		}

		public boolean positioned(){
			return polygon.textureX > -1 && polygon.textureY >= -1 && !detached;
		}

		public String name(){
			return polygon.name() + (face == null ? ";" : ":" + face.id());
		}

		public boolean poly(){
			return face != null;
		}
		
	}
	
}
