package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Translator.format;
import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.CheckBox;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ProgressBar;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.texture.Texture;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.lib.common.math.RGB;

public class AutoUVPositioner {

	public static boolean HALT = true, ALL, SAVESPACE, DETACH;
	private static ArrayList<CoordContainer> list;
	private static Group selected, resetsel;
	private static ProgressDialog dialog;
	private static Texture texture;
	private static int last;

	public static void runReset(boolean cUV){
		int width = 440;
		resetsel = null;
		String suffix = cUV ? "_type" : "";
		Dialog dialog = new Dialog(translate("texture_autopos.reset" + suffix + ".dialog"), width + 20, 180);
		Label label0 = new Label(translate("texture_autopos.reset" + suffix + ".info"), 10, 10, width, 20);
		label0.getStyle().setFont("roboto-bold");
		Label label1 = new Label(translate("texture_autopos.reset" + suffix + ".group"), 10, 40, width / 20, 20);
		SelectBox<String> texture = new SelectBox<>(10 + width / 2, 40, width / 2, 20);
		texture.addElement("all-groups");
		for(Group group : FMT.MODEL.groups()) texture.addElement(group.id);
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
					FMT.MODEL.groups().forEach(group -> group.forEach(poly -> {
						//TODO reset cuv
						poly.recompile();
					}));
				}
				else{
					FMT.MODEL.groups().forEach(group -> group.forEach(poly -> {
						poly.textureX = -1;
						poly.textureY = -1;
						poly.recompile();
					}));
				}
			}
			dialog.close();
			GenericDialog.showOK(null, null, null, "texture_autopos.reset" + suffix + ".done");
		});
		dialog.getContainer().add(label0);
		dialog.getContainer().add(label1);
		dialog.getContainer().add(texture);
		dialog.getContainer().add(button);
		dialog.show(FMT.FRAME);
	}

	public static void tryAutoPos(){
		int width = 540;
		Dialog dialog = new Dialog(translate("texture_autopos.autopos.dialog"), width + 20, 210);
		Label label0 = new Label(translate("texture_autopos.autopos.info"), 10, 10, width, 20);
		label0.getStyle().setFont("roboto-bold");
		Label label1 = new Label(translate("texture_autopos.autopos.polygroup"), 10, 40, width / 20, 20);
		SelectBox<String> texture = new SelectBox<>(10 + width / 2, 40, width / 2, 20);
		texture.addElement("all-groups");
		for(Group group : FMT.MODEL.groups()) texture.addElement(group.id);
		texture.addSelectBoxChangeSelectionEventListener(listener -> {
			if(listener.getNewValue().equals("all-groups")) selected = null;
			else selected = FMT.MODEL.get(listener.getNewValue());
		});
		texture.setSelected(0, true);
		texture.setVisibleCount(6);
		CheckBox checkbox0 = new CheckBox(10, 70, width, 20);
		checkbox0.getStyle().setPadding(5f, 10f, 5f, 5f);
		checkbox0.setChecked(SAVESPACE);
		checkbox0.addCheckBoxChangeValueListener(listener -> SAVESPACE = listener.getNewValue());
		checkbox0.getTextState().setText(translate("texture_autopos.autopos.savespace"));
		CheckBox checkbox1 = new CheckBox(10, 100, width, 20);
		checkbox1.getStyle().setPadding(5f, 10f, 5f, 5f);
		checkbox1.setChecked(!ALL);
		checkbox1.addCheckBoxChangeValueListener(listener -> ALL = !listener.getNewValue());
		checkbox1.getTextState().setText(translate("texture_autopos.autopos.process_all"));
		CheckBox checkbox2 = new CheckBox(10, 130, width, 20);
		checkbox2.getStyle().setPadding(5f, 10f, 5f, 5f);
		checkbox2.setChecked(DETACH);
		checkbox2.addCheckBoxChangeValueListener(listener -> DETACH = listener.getNewValue());
		checkbox2.getTextState().setText(translate("texture_autopos.autopos.detach_all"));
		Button button = new Button(translate("texture_autopos.autopos.start"), 10, 160, 100, 20);
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(e.getAction() != MouseClickAction.CLICK) return;
			startAutoPos();
			dialog.close();
		});
		dialog.getContainer().add(label0);
		dialog.getContainer().add(label1);
		dialog.getContainer().add(texture);
		dialog.getContainer().add(checkbox0);
		dialog.getContainer().add(checkbox1);
		dialog.getContainer().add(checkbox2);
		dialog.getContainer().add(button);
		dialog.show(FMT.FRAME);
		return;
	}

	private static void startAutoPos(){
		/*new Thread("AutoPosThread"){
			@Override
			public void run(){
				log("STARTING AUTOPOS THREAD");
				HALT = false;
				final int sizex = selected == null ? FMT.MODEL.texSizeX : selected.texSizeX;
				final int sizey = selected == null ? FMT.MODEL.texSizeY : selected.texSizeY;
				log("Setting size to " + sizex + "x, " + sizey + "y.");
				log("Group selected: " + (selected == null ? "none" : selected.id));
				log("All-Polygons is set to '" + ALL + "' and Save-Space is set to '" + SAVESPACE + "'. Detach is set to '" + DETACH + "'.");
				if(list == null){
					list = getSortedList(ALL);
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
						showPercentageDialog(corcon.wrapper.getTurboList().id, corcon.name(), getPercent(last, list.size()));
						if(corcon.coords == null || corcon.coords.length == 0){
							log("skipping [" + corcon.wrapper.getTurboList().id + ":" + corcon.name() + "] (missing texture definition)");
							continue;
						}
						boolean whole = !corcon.poly();
						if((corcon.wrapper.textureX > -1 && corcon.wrapper.textureY > -1) && (!ALL || (!DETACH && !whole && corcon.wrapper.getUVCoords(corcon.face).absolute()))){
							log("skipping [" + corcon.wrapper.getTurboList().id + ":" + corcon.name() + "] (texture not -1x -1y)");
							if(!corcon.exclude && whole){
								corcon.wrapper.burnToTexture(texture, null);
							}
							else if(corcon.exclude){
								corcon.wrapper.burnToTexture(texture, null, corcon.coords, false, null);
							}
							else{
								corcon.wrapper.burnToTexture(texture, null, corcon.coords, true, corcon.face.index());
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
									log("[" + corcon.wrapper.getTurboList().id + ":" + corcon.name() + "] >> " + xar + "x, " + yar + "y;");
									if(!corcon.poly()){
										corcon.wrapper.textureX = xar;
										corcon.wrapper.textureY = yar;
										//wrapper.texpos = wrapper.newTexturePosition(true, false);
										corcon.wrapper.burnToTexture(texture, null);
									}
									else{
										UVCoords coords = corcon.wrapper.getUVCoords(corcon.face);
										FaceUVType type = coords.type();
										float[] arr = type.automatic() ? new float[2] : coords.value();
										switch(type){
											case AUTOMATIC:
											case ABSOLUTE:
											case OFFSET_ONLY:{
												type = FaceUVType.ABSOLUTE;
												arr[0] = xar;
												arr[1] = yar;
												break;
											}
											case ABSOLUTE_ENDS:
											case OFFSET_ENDS:{
												float minx = arr[0], miny = arr[1];
												arr[0] += xar - minx;
												arr[1] += yar - miny;
												arr[2] += xar - minx;
												arr[3] += yar - miny;
												type = FaceUVType.ABSOLUTE_ENDS;
												break;
											}
											case ABSOLUTE_FULL:
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
												type = FaceUVType.ABSOLUTE_FULL;
												break;
											}
										}
										coords.set(type).value(arr);
										float[][] newarr = corcon.wrapper.newTexturePosition(true, false)[corcon.face.index()];
										corcon.wrapper.burnToTexture(texture, null, new float[][][]{ newarr }, true, corcon.face.index());
									}
									pass = true;
									Thread.sleep(10);
									break;
								}
							}
						}
						if(!pass){
							log("[" + corcon.wrapper.getTurboList().id + ":" + corcon.name() + "] >> could not be mapped;");
						}
					}
					catch(Exception e){
						log(e);
						// FMTB.showDialogbox("Autoposition failed with Exception", "See Console for details.", "ok", null, DialogBox.NOTHING, null);
					}
				}
				if(dialog != null) dialog.close();
				DialogBox.showOK("texture_autopos.autopos.title", () -> FMTB.MODEL.recompile(), null, "texture_autopos.autopos.complete");
				last = (HALT = (list = null) == null) ? -1 : 0;
				texture = null;
				log("STOPPING AUTOPOS THREAD");
				return;
			}
		}.start();*/
	}

	private static void showPercentageDialog(String group, String polygon, int percent){
		if(dialog == null) dialog = new ProgressDialog();
		dialog.progressbar.setValue(percent);
		dialog.label.getTextState().setText(format("texture_autopos.autopos.processing", group, polygon));
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

	private static ArrayList<CoordContainer> getSortedList(boolean all){
		ArrayList<CoordContainer> arrlist = new ArrayList<>();
		/*if(selected == null){
			for(Group group : FMT.MODEL.groups()){
				addAll(arrlist, group);
			}
		}
		else{
			addAll(arrlist, selected);
		}
		arrlist.sort(new java.util.Comparator<CoordContainer>(){
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
		Collections.reverse(arrlist);
		if(!all){
			ArrayList<CoordContainer> pri = (ArrayList<CoordContainer>)arrlist.stream().filter(con -> con.positioned()).collect(Collectors.toList());
			ArrayList<CoordContainer> sec = (ArrayList<CoordContainer>)arrlist.stream().filter(con -> !con.positioned()).collect(Collectors.toList());
			arrlist.clear();
			arrlist.addAll(pri);
			arrlist.addAll(sec);
		}*/
		return arrlist;
	}

	private static void addAll(ArrayList<CoordContainer> arrlist, Group group){
		/*for(PolygonWrapper wrapper : group){
			if(!wrapper.getType().isTexturable()) continue;
			boolean detach = DETACH && (wrapper.getType() == ShapeType.BOX || wrapper.getType() == ShapeType.SHAPEBOX);
			if(detach || wrapper.anyFaceUVAbsolute()){
				for(UVCoords coord : wrapper.cuv.values()){
					if(!wrapper.isFaceActive(coord.face())) continue;//face is disabled
					if(detach || coord.absolute()){
						arrlist.add(new CoordContainer(wrapper, coord.side(), detach && !coord.absolute()));
					}
				}
				if(!detach && !wrapper.isAllFaceUVAbsolute()){
					arrlist.add(new CoordContainer(wrapper, true));
				}
				if(detach) wrapper.textureX = wrapper.textureY = -1;
			}
			else{
				arrlist.add(new CoordContainer(wrapper, false));
			}
		}*/
	}

	private static int getPercent(int i, int all){
		return (i * 100) / all;
	}

	public static class ProgressDialog extends Dialog {

		private ProgressBar progressbar;
		private Label label;

		public ProgressDialog(){
			super(translate("texture_autopos.autopos.title"), 400, 90);
			label = new Label(format("texture_autopos.autopos.processing", 0, "initializing"), 10, 10, 340, 20);
			dialog = this;
			dialog.setResizable(false);
			dialog.getContainer().add(label);
			progressbar = new ProgressBar(10, 40, 380, 10);
			progressbar.setValue(0);
			dialog.getContainer().add(progressbar);
			dialog.show(FMT.FRAME);
		}

		public void close(){
			super.close();
			dialog = null;
		}

	}
	
	public static class CoordContainer {
		
		/*public float[][][] coords;
		public Face face;
		public PolygonWrapper wrapper;
		public float min_x, min_y, max_x, max_y;
		public boolean detached, exclude;
		
		public CoordContainer(PolygonWrapper wrapper, boolean excempt){
			this.wrapper = wrapper;
			coords = wrapper.newTexturePosition(true, exclude = excempt);
		}

		public CoordContainer(PolygonWrapper wrapper, Face key, boolean detach){
			this.wrapper = wrapper;
			coords = new float[][][]{ wrapper.newTexturePosition(true, false)[(face = key).index()] };
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
			return wrapper.textureX > -1 && wrapper.textureY >= -1 && !detached;
		}

		public String name(){
			return wrapper.name() + (face == null ? ";" : ":" + face.id());
		}

		public boolean poly(){
			return face != null;
		}*/
		
	}
	
}
