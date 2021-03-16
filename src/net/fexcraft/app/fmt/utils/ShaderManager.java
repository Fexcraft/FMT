package net.fexcraft.app.fmt.utils;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;

public class ShaderManager {
	
	public static ShaderProgram GENERAL;

	public static int load(String string, int type) throws IOException {
		return load(new File(string), type);
	}

	private static int load(File file, int type) throws IOException {
		int shaderid = glCreateShader(type);
		glShaderSource(shaderid, FileUtils.readFileToString(file, StandardCharsets.UTF_8));
		glCompileShader(shaderid);
		return shaderid;
	}

	public static void loadPrograms() throws IOException {
		GENERAL = loadProgram("general");
	}

	private static ShaderProgram loadProgram(String id) throws IOException {
		int vert = ShaderManager.load("./resources/shaders/vertex_" + id + ".glsl", GL_VERTEX_SHADER); 
		int frag = ShaderManager.load("./resources/shaders/fragment_" + id + ".glsl", GL_FRAGMENT_SHADER);
		int prog = glCreateProgram();
		glLinkProgram(prog);
		glAttachShader(prog, vert);
		glAttachShader(prog, frag);
		glBindAttribLocation(prog, 0, "position");
		glBindAttribLocation(prog, 1, "color_in");
		glBindAttribLocation(prog, 2, "uv_in");
		glBindAttribLocation(prog, 3, "normal_in");
		glBindAttribLocation(prog, 4, "light_in");
		glLinkProgram(prog);
		if(glGetProgrami(vert, GL_COMPILE_STATUS) == GL_FALSE){
			log("[VertShader/" + id + "]", glGetShaderInfoLog(vert, glGetShaderi(vert, GL_INFO_LOG_LENGTH)));
		}
		if(glGetProgrami(frag, GL_COMPILE_STATUS) == GL_FALSE){
		    log("[FragShader/" + id + "]", glGetShaderInfoLog(frag, glGetShaderi(frag, GL_INFO_LOG_LENGTH)));
		}
		return new ShaderProgram(prog, vert, frag);
	}

	private static void log(String string, String info){
		if(info.length() == 0) return;
		System.err.println(string + ": " + info);
	}
	
	public static class ShaderProgram {
		
		private final int vert, frag, prog;
		
		public ShaderProgram(int prog2, int vert2, int frag2){
			prog = prog2;
			vert = vert2;
			frag = frag2;
		}

		public void use(){
			glUseProgram(prog);
		}

		public void delete(){
			glDeleteShader(vert);
			glDeleteShader(frag);
			glDeleteProgram(prog);
		}
		
		public int vertex(){
			return vert;
		}
		
		public int fragment(){
			return frag;
		}

		public int program(){
			return prog;
		}
		
	}

	public static void applyUniforms(Consumer<ShaderProgram> cons){
		cons.accept(GENERAL);
	}

	public static int getUniform(String string){
		return glGetUniformLocation(ShaderManager.GENERAL.program(), string);
	}

}
