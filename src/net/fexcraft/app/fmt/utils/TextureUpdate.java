package net.fexcraft.app.fmt.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TimerTask;
import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureUpdate extends TimerTask {
	
	private Texture texture; private static long lastedit;
	private static ArrayList<PolygonWrapper> list;
	public static boolean HALT = true, ALL;
	private static BufferedImage image;
	private static int last, per;

	@Override
	public void run(){
        try{
            if(FMTB.MODEL.texture == null) return;
            texture = TextureManager.getTexture(FMTB.MODEL.texture, true);
            if(texture == null || texture.getFile() == null || !texture.getFile().exists()) return;
            if(texture.getFile().lastModified() > lastedit){
            	updateLastEdit(texture.getFile().lastModified()); texture.reload();
            	Print.console("Changes detected, reloading texture.");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}
	
	public static final void updateLastEdit(long date){
		lastedit = date; return;
	}

	public static void updateSizes(){
		Texture texture = TextureManager.getTexture(FMTB.MODEL.texture, true);
		if(texture == null || texture.getImage() == null) return;
		BufferedImage image = texture.getImage();
		if(image.getWidth() != FMTB.MODEL.textureX || image.getHeight() != FMTB.MODEL.textureY){
			if(image.getWidth() > FMTB.MODEL.textureX && image.getWidth() % FMTB.MODEL.textureX == 0
				&& image.getHeight() > FMTB.MODEL.textureY && image.getHeight() % FMTB.MODEL.textureY == 0) return;
			texture.resize(FMTB.MODEL.textureX, FMTB.MODEL.textureY, 0x00ffffff);
			TextureManager.saveTexture(FMTB.MODEL.texture); FMTB.MODEL.recompile();
			updateLastEdit(Time.getDate());
		}
		else return;
	}

	public static void tryAutoPos(Boolean bool){
		if(bool == null){
			FMTB.showDialogbox("Only process polygons with", "0, 0 texture pos?", "Yes", "No (All)", () -> { HALT = false; ALL = false; }, () -> { HALT = false; ALL = true; });
			return;
		} HALT = false; ALL = bool;
		//
		if(list == null){
			list = getSortedList(); last = 0; image = new BufferedImage(FMTB.MODEL.textureX, FMTB.MODEL.textureY, BufferedImage.TYPE_INT_ARGB);
			for(int i = 0; i < image.getWidth(); i++){
				for(int j = 0; j < image.getHeight(); j++){
					image.setRGB(i, j, Color.WHITE.getRGB());
				}
			}
		}
		try{
			if(HALT || last < 0 || last >= list.size()){
				FMTB.showDialogbox("Auto texture positioning", "Complete!", "Good!", null, DialogBox.NOTHING, null);
				last = (HALT = (list = null) == null) ? -1 : 0; image = null; return;
			}
			PolygonWrapper wrapper = list.get(last); last++;
			FMTB.showDialogbox("Processing: " + (per = getPercent(last, list.size())) + "%", wrapper.getTurboList().id + ":" + wrapper.name(), null, null, null, null, per, null);
			if(wrapper.textureX != 0 && wrapper.textureY!= 0 && !bool){ Print.console("skipping0 " + wrapper.name()); return; }
			if(wrapper.texpos == null || wrapper.texpos.length == 0){ Print.console("skipping1 " + wrapper.name()); return; }
			//
			for(int yar = 0; yar < FMTB.MODEL.textureY; yar++){
				for(int xar = 0; xar < FMTB.MODEL.textureX; xar++){
					if(check(wrapper.texpos, xar, yar)){
						Print.console(false, new Object[]{ wrapper.name(), xar, yar });
						wrapper.textureX = xar; wrapper.textureY = yar; wrapper.recompile(); wrapper.burnToTexture(image, null); Thread.sleep(10);
						return;
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace(); //FMTB.showDialogbox("Autoposition failed with Exception", "See Console for details.", "ok", null, DialogBox.NOTHING, null);
		}
	}
	
	private static boolean check(float[][][] texpos, int xx, int yy){
		float[][] ends = null; 
		for(int i = 0; i < texpos.length; i++){ ends = texpos[i];
			for(float y = ends[0][1]; y < ends[1][1]; y += 0.5f){
				for(float x = ends[0][0]; x < ends[1][0]; x += 0.5f){
					int xr = (int)(xx + x), yr = (int)(yy + y);
					if(xr < 0  || yr < 0 ) continue;
					if(xr >= image.getWidth()|| yr >= image.getHeight()) return false;
					//
					if(image.getRGB(xr, yr) != Color.WHITE.getRGB()){ return false; } else continue;
				}
			}
		} return true;
	}
	
	private static ArrayList<PolygonWrapper> getSortedList(){
		ArrayList<PolygonWrapper> arrlist = new ArrayList<>();
		for(TurboList list : FMTB.MODEL.getCompound().values()){ arrlist.addAll(list); }
		arrlist.sort(new java.util.Comparator<PolygonWrapper>(){
			@Override
			public int compare(PolygonWrapper left, PolygonWrapper righ){
				int x0 = (int)(left.getType().isCylinder() ? left.getFloat("cyl0", true, false, false) * 4 : left.getFloat("size", true, false, false));
				int x1 = (int)(righ.getType().isCylinder() ? righ.getFloat("cyl0", true, false, false) * 4 : righ.getFloat("size", true, false, false));
				int y0 = (int)(left.getType().isCylinder() ? (left.getFloat("cyl0", true, false, false) * 2) + left.getFloat("cyl0", true, false, false) : left.getFloat("size", false, true, false));
				int y1 = (int)(righ.getType().isCylinder() ? (righ.getFloat("cyl0", true, false, false) * 2) + righ.getFloat("cyl0", true, false, false) : righ.getFloat("size", false, true, false));
				if(Integer.compare(x0, x1) > 1){ return 1; } return Integer.compare(y0, y1);
			}
		});
		return arrlist;
	}
	
	private static int getPercent(int i, int all){ return (i * 100) / all; }

}
