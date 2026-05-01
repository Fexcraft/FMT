package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Frame;
import net.fexcraft.app.fmt.ui.RunElm;
import net.fexcraft.app.fmt.utils.ByteUtils;
import net.fexcraft.app.fmt.utils.Logging;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class IconFrameFrame extends Frame {

	public IconFrameFrame(){
		size(257, 257);
		border(Settings.SELECTION_LINES.value);
		pos((FMT.SCALED_WIDTH - 256) * 0.5f, (FMT.SCALED_HEIGHT - 256) * 0.5f);
	}

	@Override
	public void init(Object... args){
		add(new RunElm(0, 257, 256, "dialog.button.save", ci -> {
			FMT.UI.remElm(this);
			FMT.queue(() -> {
				FvtmPackElm pack = (FvtmPackElm)args[0];
				FileChooser.choose("editor.config.pack_utils.icon_from_view.choose", new File(pack.file, "/assets/" + pack.id + "/textures/"), FileChooser.TYPE_PNG, true, this::saveInto);
			});
		}).text_centered(true));
	}

	private void saveInto(File file){
		if(file == null) return;
		ByteBuffer buffer = ByteBuffer.allocateDirect(256 * 256 * 4);
		buffer.order(ByteOrder.nativeOrder());
		GL11.glReadPixels((int)hedron.posX, (int)hedron.posY, 256, 256, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		byte[] arr0 = new byte[4];
		byte[] arr1 = new byte[4];
		int col;
		for(int x = 0; x < 256; x++){
			for(int y = 0; y < 256; y++){
				int yy = 255 - y;
				buffer.get((x + yy * 256) * 4, arr0);
				col = ByteUtils.getRGB(arr0);
				arr1[0] = arr0[3];
				arr1[1] = arr0[0];
				arr1[2] = arr0[1];
				arr1[3] = arr0[2];
				img.setRGB(x, y, col == Settings.BACKGROUND.value.packed ? 0x00000000 : ByteBuffer.wrap(arr1).getInt());
			}
		}
		try{
			ImageIO.write(img, "PNG", file);
			FMT.WORKSPACE.reloadPacks(null);
		}
		catch(IOException e){
			Logging.log(e);
		}
	}

}
