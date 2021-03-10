package net.fexcraft.app.fmt.utils;

import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.liquidengine.legui.util.IOUtil;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import net.fexcraft.app.fmt.settings.Settings;

/** Based on <https://github.com/LWJGL/lwjgl3/blob/8598adb990362bf8d51b66b5c03b743ee5d5c14c/modules/samples/src/test/java/org/lwjgl/demo/stb/Truetype.java> */
public final class FontSizeUtil {
	
    private static STBTTFontinfo info;

    private static void loadFontInfo(){
    	ByteBuffer type = null;
        try{
            type = IOUtil.resourceToByteBuffer(Settings.FONT_PATH);
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        info = STBTTFontinfo.create();
        if(!stbtt_InitFont(info, type)) throw new IllegalStateException("Error while initializing font information.");
	}

    public static float getWidth(String text){
    	if(text == null || text.length() < 1) return 0;
        loadFontInfo();
        int width = 0, length = text.length();
        try(MemoryStack stack = stackPush()){
        	stack.push();
            IntBuffer cpoint = stack.mallocInt(1);
            IntBuffer advwidth = stack.mallocInt(1);
            IntBuffer lsb = stack.mallocInt(1);
            int idx = 0;
            while(idx < length){
                idx += getCodePoint(text, length, idx, cpoint);
                int point = cpoint.get(0);
                stbtt_GetCodepointHMetrics(info, point, advwidth, lsb);
                width += advwidth.get(0);
                if(idx < length){
                    getCodePoint(text, length, idx, cpoint);
                    width += stbtt_GetCodepointKernAdvance(info, point, cpoint.get(0));
                }
            }
            stack.pop();
        }
        return width * stbtt_ScaleForPixelHeight(info, Settings.FONT_SIZE);
    }

	private static int getCodePoint(String text, int length, int idx, IntBuffer out){
        char c1 = text.charAt(idx);
        if(Character.isHighSurrogate(c1) && idx + 1 < length){
            char c2 = text.charAt(idx + 1);
            if(Character.isLowSurrogate(c2)) {
                out.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        out.put(0, c1);
        return 1;
    }

}