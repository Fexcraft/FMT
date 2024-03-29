package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImageResize.STBIR_COLORSPACE_LINEAR;
import static org.lwjgl.stb.STBImageResize.STBIR_EDGE_ZERO;
import static org.lwjgl.stb.STBImageResize.STBIR_FILTER_BOX;
import static org.lwjgl.stb.STBImageResize.stbir_resize_uint8_generic;

import java.io.File;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Texture {

	public static final int CHANNELS = 4;
	//
	private Integer glTexid;
	private ByteBuffer buffer;
	private int[] width = { 0 }, height = { 0 }, channels = { 4 };
	private boolean rebind = true, reload;
	public final String name;
	public long lastedit;
	private File file;
	
	public Texture(String name, File location){
		buffer = stbi_load((file = location).getPath(), width, height, channels, CHANNELS);
		if(buffer == null) log("Error while loading texture '" + name + "': " + stbi_failure_reason());
		this.name = name;
	}
	
	public Texture(String name, int width, int height){
		buffer = BufferUtils.createByteBuffer((this.width[0] = width) * (this.height[0] = height) * (channels[0] = CHANNELS));
		buffer.rewind();
		this.name = name;
	}
	
	public void resize(int width, int height){
		if(width == this.width[0] && height == this.height[0]) return;
		try{
			ByteBuffer oldbuffer = buffer;
			buffer = BufferUtils.createByteBuffer(width * height * CHANNELS);
			stbir_resize_uint8_generic(oldbuffer, this.width[0], this.height[0], 0, buffer, width, height, 0, 4, 3, 0, STBIR_EDGE_ZERO, STBIR_FILTER_BOX, STBIR_COLORSPACE_LINEAR);
			this.width[0] = width;
			this.height[0] = height;
			rebind();
		}
		catch(Exception e){
			log(e);
		}
	}

	public ByteBuffer getBuffer(){
		if(reload && file != null){
			buffer = stbi_load(file.getPath(), width, height, channels, CHANNELS);
			reload = false;
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

	public boolean rebindQueued(){
		return rebind;
	}

	public boolean reload(){
		return reload = rebind();
	}

	public void bind(){
		if(glTexid == null){
			glTexid = GL11.glGenTextures();
			rebind();
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTexid);
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

	public Integer getGlTexId(){
		return glTexid;
	}
	
	public void clear(byte[] bytes){
		if(bytes == null) bytes = new byte[]{ (byte)255, (byte)255, (byte)255, (byte)0 };
		if(bytes.length == 3) bytes = new byte[] { bytes[0], bytes[1], bytes[2], (byte)0 };
		for(int x = 0; x < width[0]; x++){
			for(int y = 0; y < height[0]; y++){
				buffer.put(bytes[0]);
				buffer.put(bytes[1]);
				buffer.put(bytes[2]);
				buffer.put(bytes[3]);
			}
		}
	}

	public void set(int x, int y, byte[] rgb){
		int pos = (x + y * width[0]) * channels[0];
		buffer.put(pos + 0, (byte)(rgb[0] + 128));
		buffer.put(pos + 1, (byte)(rgb[1] + 128));
		buffer.put(pos + 2, (byte)(rgb[2] + 128));
		buffer.put(pos + 3, (byte)255);
	}

	public byte[] get(int x, int y){
		int index = (x + y * width[0]) * channels[0];
		return new byte[]{ buffer.get(index), buffer.get(index + 1), buffer.get(index + 2), buffer.get(index + 3)};
	}

	public Texture setFile(File file){
		this.file = file;
		return this;
	}

	public boolean equals(int x, int y, byte[] color){
		return equals(x, y, color, true);
	}

	public boolean equals(int x, int y, byte[] color, boolean add){
		byte[] local = get(x, y);
		if(add) return (local[0] + 128) == color[0] && (local[1] + 128) == color[1] && (local[2] + 128) == color[2];
		else return local[0] == color[0] && local[1] == color[1] && local[2] == color[2];
	}

	public void save(){
		TextureManager.save(this);
	}

}
