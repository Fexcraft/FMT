package net.fexcraft.app.fmt.utils.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.CheckBox;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ProgressBar;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeType;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.FaceUVType;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;
import net.fexcraft.lib.common.math.RGB;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureUpdate extends TimerTask {

	public static boolean HALT = true, ALL, SAVESPACE, DETACH;
	private static ArrayList<CoordContainer> list;
	private static Texture texture;
	private static TexUpDialog dialog;
	private static TurboList selected, resetsel;
	private static int last;

	@Override
	public void run(){
		for(TextureGroup group : TextureManager.getGroupsFE()){
			try{
				if(group.texture == null || group.texture.getFile() == null){
					//log("TEXGROUP '" + group.group + "' HAS NO FILE OR TEXTURE LINKED YET.");
					continue;
				}
				if(group.texture.getFile().lastModified() > group.texture.lastedit){
					updateLastEdit(group.texture);
					group.texture.reload();
					log("Changes detected, reloading texture group '" + group.group + "'.");
				}
			}
			catch(Exception e){
				log(e);
			}
		}
	}
	
	public static void updateLastEdit(Texture texture){
		texture.lastedit = texture.getFile().lastModified();
	}

	public static void updateSize(TurboList list){
		return;//TODO reactive this later
		/*Texture texture = (list == null ? FMTB.MODEL.texgroup : list.getTextureGroup()).texture;
		if(texture == null || texture.getImage() == null) return;
		BufferedImage image = texture.getImage();
		int texX = list == null ? FMTB.MODEL.textureSizeX : list.textureX;
		int texY = list == null ? FMTB.MODEL.textureSizeY : list.textureY;
		int texS = list == null ? FMTB.MODEL.textureScale : list.textureS;
		for(int i = 1; i < texS; i++){
			log(texX + " * 2");
			texX *= 2;
			texY *= 2;
			log(" = " + texX);
		}
		if(image.getWidth() != texX || image.getHeight() != texY){
			String tx = (list == null ? FMTB.MODEL.textureSizeX : list.textureX) + "x," + texX + "xs";
			String ty = (list == null ? FMTB.MODEL.textureSizeY : list.textureY) + "y," + texY + "ys";
			if(texX > 4096 || texY > 4096){
				DialogBox.showOK(null, null, null, "texture_update.resize.exceeding_4096", "#" + String.format("[%s], [%s]", tx, ty));
				return;
			}
			texture.resize(texX, texY, 0x00ffffff);
			TextureManager.saveTexture(texture.name);
			if(list == null) FMTB.MODEL.recompile();
			else list.recompile();
			updateLastEdit(texture);
			DialogBox.showOK(null, null, null, "texture_update.resize.success", "#" + String.format("[%s], [%s]", tx, ty));
			return;
		}
		else return;*/
	}

	public static void tryResetPos(){
		int width = 440;
		resetsel = null;
		Dialog dialog = new Dialog(Translator.translate("texture_update.texpos_reset.title"), width + 20, 180);
		Label label0 = new Label(Translator.translate("texture_update.texpos_reset.info"), 10, 10, width, 20);
		label0.getStyle().setFont("roboto-bold");
		Label label1 = new Label(Translator.translate("texture_update.texpos_reset.polygroup"), 10, 40, width / 20, 20);
		SelectBox<String> texture = new SelectBox<>(10 + width / 2, 40, width / 2, 20);
		texture.addElement("all-groups");
		for(TurboList list : FMTB.MODEL.getGroups()) texture.addElement(list.id);
		texture.addSelectBoxChangeSelectionEventListener(listener -> {
			if(listener.getNewValue().equals("all-groups")) resetsel = null;
			else resetsel = FMTB.MODEL.getGroups().get(listener.getNewValue());
		});
		texture.setSelected(0, true);
		texture.setVisibleCount(6);
		Button button = new Button(Translator.translate("dialogbox.button.confirm"), 10, 130, 100, 20);
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()){
				if(resetsel != null){
					resetsel.forEach(turbo -> {
						turbo.textureX = -1;
						turbo.textureY = -1;
						turbo.recompile();
					});
				}
				else{
					FMTB.MODEL.getGroups().forEach(list -> list.forEach(turbo -> {
						turbo.textureX = -1;
						turbo.textureY = -1;
						turbo.recompile();
					}));
				}
				dialog.close();
				DialogBox.showOK(null, null, null, "texture_update.texpos_reset.done");
			}
		});
		dialog.getContainer().add(label0);
		dialog.getContainer().add(label1);
		dialog.getContainer().add(texture);
		dialog.getContainer().add(button);
		dialog.show(FMTB.frame);
	}

	public static void tryResetPosType(){
		int width = 440;
		resetsel = null;
		Dialog dialog = new Dialog(Translator.translate("texture_update.texpostype_reset.title"), width + 20, 180);
		Label label0 = new Label(Translator.translate("texture_update.texpostype_reset.info"), 10, 10, width, 20);
		label0.getStyle().setFont("roboto-bold");
		Label label1 = new Label(Translator.translate("texture_update.texpostype_reset.polygroup"), 10, 40, width / 20, 20);
		SelectBox<String> texture = new SelectBox<>(10 + width / 2, 40, width / 2, 20);
		texture.addElement("all-groups");
		for(TurboList list : FMTB.MODEL.getGroups()) texture.addElement(list.id);
		texture.addSelectBoxChangeSelectionEventListener(listener -> {
			if(listener.getNewValue().equals("all-groups")) resetsel = null;
			else resetsel = FMTB.MODEL.getGroups().get(listener.getNewValue());
		});
		texture.setSelected(0, true);
		texture.setVisibleCount(6);
		Button button = new Button(Translator.translate("dialogbox.button.confirm"), 10, 130, 100, 20);
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()){
				if(resetsel != null){
					resetsel.forEach(turbo -> {
						turbo.cuv.values().forEach(cuv -> cuv.set(null));
						turbo.recompile();
					});
				}
				else{
					FMTB.MODEL.getGroups().forEach(list -> list.forEach(turbo -> {
						turbo.cuv.values().forEach(cuv -> cuv.set(null));
						turbo.recompile();
					}));
				}
				dialog.close();
				DialogBox.showOK(null, null, null, "texture_update.texpos_reset.done");
			}
		});
		dialog.getContainer().add(label0);
		dialog.getContainer().add(label1);
		dialog.getContainer().add(texture);
		dialog.getContainer().add(button);
		dialog.show(FMTB.frame);
	}

	public static void tryAutoPos(){
		int width = 540;
		Dialog dialog = new Dialog(Translator.translate("texture_update.autopos.title"), width + 20, 210);
		Label label0 = new Label(Translator.translate("texture_update.autopos.info"), 10, 10, width, 20);
		label0.getStyle().setFont("roboto-bold");
		Label label1 = new Label(Translator.translate("texture_update.autopos.polygroup"), 10, 40, width / 20, 20);
		SelectBox<String> texture = new SelectBox<>(10 + width / 2, 40, width / 2, 20);
		texture.addElement("all-groups");
		for(TurboList list : FMTB.MODEL.getGroups()) texture.addElement(list.id);
		texture.addSelectBoxChangeSelectionEventListener(listener -> {
			if(listener.getNewValue().equals("all-groups")) selected = null;
			else selected = FMTB.MODEL.getGroups().get(listener.getNewValue());
		});
		texture.setSelected(0, true);
		texture.setVisibleCount(6);
		CheckBox checkbox0 = new CheckBox(10, 70, width, 20);
		checkbox0.getStyle().setPadding(5f, 10f, 5f, 5f);
		checkbox0.setChecked(SAVESPACE);
		checkbox0.addCheckBoxChangeValueListener(listener -> SAVESPACE = listener.getNewValue());
		checkbox0.getTextState().setText(Translator.translate("texture_update.autopos.savespace"));
		CheckBox checkbox1 = new CheckBox(10, 100, width, 20);
		checkbox1.getStyle().setPadding(5f, 10f, 5f, 5f);
		checkbox1.setChecked(!ALL);
		checkbox1.addCheckBoxChangeValueListener(listener -> ALL = !listener.getNewValue());
		checkbox1.getTextState().setText(Translator.translate("texture_update.autopos.process_all"));
		CheckBox checkbox2 = new CheckBox(10, 130, width, 20);
		checkbox2.getStyle().setPadding(5f, 10f, 5f, 5f);
		checkbox2.setChecked(DETACH);
		checkbox2.addCheckBoxChangeValueListener(listener -> DETACH = listener.getNewValue());
		checkbox2.getTextState().setText(Translator.translate("texture_update.autopos.detach_all"));
		Button button = new Button(Translator.translate("texture_update.autopos.start"), 10, 160, 100, 20);
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)e -> {
			if(CLICK == e.getAction()){
				startAutoPos();
				dialog.close();
			}
		});
		dialog.getContainer().add(label0);
		dialog.getContainer().add(label1);
		dialog.getContainer().add(texture);
		dialog.getContainer().add(checkbox0);
		dialog.getContainer().add(checkbox1);
		dialog.getContainer().add(checkbox2);
		dialog.getContainer().add(button);
		dialog.show(FMTB.frame);
		return;
	}

	private static void startAutoPos(){
		new Thread("AutoPosThread"){
			@Override
			public void run(){
				log("STARTING AUTOPOS THREAD");
				HALT = false;
				final int sizex = FMTB.MODEL.tx(selected, false);
				final int sizey = FMTB.MODEL.ty(selected, false);
				log("Setting size to " + sizex + "x, " + sizey + "y.");
				log("Group selected: " + (selected == null ? "none" : selected.id));
				log("All-Polygons is set to '" + ALL + "' and Save-Space is set to '" + SAVESPACE + "'. Detach is set to '" + DETACH + "'.");
				if(list == null){
					list = getSortedList(ALL);
					last = 0;
					if((texture = TextureManager.getTexture("auto-pos-temp", true)) != null){
						texture.getImage().rewind();
						texture.resize(sizex, sizey);
						log("Resized auto-pos temp texture.");
					}
					else{
						texture = TextureManager.createTexture("auto-pos-temp", sizex, sizey);
						texture.setFile(new File("./temp/auto-pos-temp.png"));
						log("Created auto-pos temp texture.");
					}
					//log("Clearing calculation texture...");
					texture.clear(RGB.WHITE.toByteArray());
					log("AutoPosTex size is " + sizex + ", " + sizey + ".");
					//log(texture.getWidth() + " " + texture.getHeight());
					//log(texture.get(0, 0)[0] + " " + RGB.WHITE.toByteArray()[0]);
					//log(texture.get(0, 0)[1] + " " + RGB.WHITE.toByteArray()[1]);
					//log(texture.get(0, 0)[2] + " " + RGB.WHITE.toByteArray()[2]);
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
				DialogBox.showOK("texture_update.autopos.title", () -> FMTB.MODEL.recompile(), null, "texture_update.autopos.complete");
				last = (HALT = (list = null) == null) ? -1 : 0;
				texture = null;
				log("STOPPING AUTOPOS THREAD");
				return;
			}
		}.start();
	}

	private static void showPercentageDialog(String group, String polygon, int percent){
		if(dialog == null) dialog = new TexUpDialog();
		dialog.progressbar.setValue(percent);
		dialog.label.getTextState().setText(Translator.format("texture_update.autopos.processing", group, polygon));
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
						//log("exceeding " + xr + ", " + yr);
						return false;
					}
					//
					if(!texture.equals(xr, yr, RGB.WHITE.toByteArray(), false)){
						//log(xr + " " + yr + " || " + x + " " + y);
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
		if(selected == null){
			for(TurboList list : FMTB.MODEL.getGroups()){
				addAll(arrlist, list);
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
				float x0 = left.max_x - left.min_x;//(int)(left.getType().isCylinder() ? left.getFloat("cyl0", true, false, false) * 4 : left.getFloat("size", true, false, false));
				float x1 = righ.max_x - righ.min_x;//(int)(righ.getType().isCylinder() ? righ.getFloat("cyl0", true, false, false) * 4 : righ.getFloat("size", true, false, false));
				float y0 = left.max_y - left.min_y;//(int)(left.getType().isCylinder() ? (left.getFloat("cyl0", true, false, false) * 2) + left.getFloat("cyl0", true, false, false) : left.getFloat("size", false, true, false));
				float y1 = righ.max_x - righ.min_y;//(int)(righ.getType().isCylinder() ? (righ.getFloat("cyl0", true, false, false) * 2) + righ.getFloat("cyl0", true, false, false) : righ.getFloat("size", false, true, false));
				if(Float.compare(x0, x1) > 1){ return Float.compare(y0, y1); }
				return Float.compare(x0, x1);
			}
		});
		Collections.reverse(arrlist);
		if(!all){
			//ArrayList<PolygonWrapper> pri = (ArrayList<PolygonWrapper>)arrlist.stream().filter(pre -> pre.textureX >= 0 || pre.textureY >= 0).collect(Collectors.toList());
			//ArrayList<PolygonWrapper> sec = (ArrayList<PolygonWrapper>)arrlist.stream().filter(pre -> pre.textureX == -1 && pre.textureY == -1).collect(Collectors.toList());
			ArrayList<CoordContainer> pri = (ArrayList<CoordContainer>)arrlist.stream().filter(con -> con.positioned()).collect(Collectors.toList());
			ArrayList<CoordContainer> sec = (ArrayList<CoordContainer>)arrlist.stream().filter(con -> !con.positioned()).collect(Collectors.toList());
			arrlist.clear();
			arrlist.addAll(pri);
			arrlist.addAll(sec);
		}
		return arrlist;
	}

	private static void addAll(ArrayList<CoordContainer> arrlist, TurboList turbolist){
		for(PolygonWrapper wrapper : turbolist){
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
		}
	}

	private static int getPercent(int i, int all){
		return (i * 100) / all;
	}

	public static class TexUpDialog extends Dialog {

		private ProgressBar progressbar;
		private Label label;

		public TexUpDialog(){
			super(Translator.translate("texture_update.autopos.title"), 400, 90);
			label = new Label(Translator.format("texture_update.autopos.processing", 0, "initializing"), 10, 10, 340, 20);
			dialog = this;
			dialog.setResizable(false);
			dialog.getContainer().add(label);
			progressbar = new ProgressBar(10, 40, 380, 10);
			progressbar.setValue(0);
			dialog.getContainer().add(progressbar);
			dialog.show(FMTB.frame);
		}

		public void close(){
			super.close();
			dialog = null;
		}

	}
	
	public static class CoordContainer {
		
		public float[][][] coords;
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
		}
		
	}

}
