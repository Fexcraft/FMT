package net.fexcraft.app.fmt.utils.texture;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImageResize.STBIR_COLORSPACE_LINEAR;
import static org.lwjgl.stb.STBImageResize.STBIR_EDGE_ZERO;
import static org.lwjgl.stb.STBImageResize.STBIR_FILTER_DEFAULT;
import static org.lwjgl.stb.STBImageResize.stbir_resize_uint8_generic;

import java.io.File;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.utils.Print;

public class Texture {

	private ByteBuffer buffer;
	private Integer glTextureId;
	private int[] width = { 0 }, height = { 0 }, channels = { 0 };
	private boolean rebind = true, reload;
	public final String name;// was required for debug
	private File file;
	public long lastedit;
	public static final int CHANNELS = 4;

	public Texture(String name, File loc){
		this.name = name;
		buffer = stbi_load(loc.getPath(), width, height, channels, CHANNELS);
		if(buffer == null) Print.console("Error while loading texture '" + name + "': " + stbi_failure_reason());
		//buffer.flip();
		this.file = loc;
	}

	public Texture(String name, int width, int height){
		this(name, width, height, RGB.WHITE.toByteArray());
	}

	public Texture(String name, int width, int height, byte[] color){
		if(color == null) color = RGB.WHITE.toByteArray();
		buffer = stbi_load(new File("./resources/textures/blank.png").getPath(), this.width, this.height, channels, CHANNELS);
		resize(width, height);
		this.width[0] = width;
		this.height[0] = height;
		this.channels[0] = CHANNELS;
		this.name = name;
	}

	public void resize(int width, int height){
		ByteBuffer oldbuffer = buffer;
		buffer = BufferUtils.createByteBuffer(width * height * CHANNELS);
		stbir_resize_uint8_generic(oldbuffer, this.width[0], this.height[0], 0, buffer, width, height, 0, 4, 3, 0, STBIR_EDGE_ZERO, STBIR_FILTER_DEFAULT, STBIR_COLORSPACE_LINEAR);
		this.width[0] = width;
		this.height[0] = height;
		rebind();
	}

	public ByteBuffer getBuffer(){
		if(reload && buffer != null && file != null){
			buffer = stbi_load(file.getPath(), width, height, channels, 0);
		}
		rebind = false;
		return buffer;
	}

	public ByteBuffer getImage(){
		return buffer;
	}

	public boolean rebind(){
		return rebind = true;
	}

	public boolean rebindQ(){
		return rebind;
	}

	public boolean reload(){
		return reload = rebind();
	}

	public void bind(){
		if(glTextureId == null){
			glTextureId = GL11.glGenTextures();
			rebind();
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
		if(rebind){
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width[0], height[0], 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, getBuffer());
		}
	}

	public int getWidth(){
		return width[0];
	}

	public int getHeight(){
		return height[0];
	}

	public File getFile(){
		return file;
	}

	@Override
	public String toString(){
		return String.format("Texture[ %s (%s, %s) ]", name, width[0], height[0]);
	}

	public Integer getGLID(){
		return glTextureId;
	}

	public void clearPixels(){
		for(int x = 0; x < width[0]; x++){
			for(int y = 0; y < height[0]; y++){
				buffer.put((byte)255);
				buffer.put((byte)255);
				buffer.put((byte)255);
				buffer.put((byte)255);
			}
		}
	}

	public void save(){
		TextureManager.saveTexture(this);
	}

	public void set(int x, int y, byte[] rgb){
		int pos = (x + y * height[0]) * CHANNELS;
		/*if(pos >= buffer.capacity()){
			Print.console("overcapacity " + pos + " " + buffer.capacity());
			Print.console("source: " + x + " " + y);
		}*/
		buffer.put(pos + 0, (byte)(rgb[0] + 128));
		buffer.put(pos + 1, (byte)(rgb[1] + 128));
		buffer.put(pos + 2, (byte)(rgb[2] + 128));
		buffer.put(pos + 3, (byte)255);
	}

	public byte[] get(int x, int y){
		int index = (x + y * height[0]) * CHANNELS;
		return new byte[]{ buffer.get(index), buffer.get(index + 1), buffer.get(index + 2), buffer.get(index + 3)};
	}

	public Texture setFile(File file){
		this.file = file;
		return this;
	}

	public boolean equals(int x, int y, byte[] color){
		byte[] local = get(x, y);
		return local[0] == color[0] && color[1] == color[1] && local[2] == color[2];
	}

}