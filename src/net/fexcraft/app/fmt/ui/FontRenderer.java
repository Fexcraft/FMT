package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.utils.TextureManager;

public class FontRenderer {
	
	public static TextureManager.Texture fonttex;
	
	public static void init(){
		fonttex = TextureManager.getTexture("font", false);
	}
	
	public static enum SIGN {
		
		//TODO keyboard keys
		SPACE  (" ", 5, 0, 0, Keyboard.KEY_SPACE),
		EXCLAMATION_MARK("!", 1, 1, 0),
		QUOTE ("\"", 4, 2, 0),
		HASH   ("#", 5, 3, 0),
		DOLLAR ("$", 5, 4, 0),
		PERCENT("%", 5, 5, 0),
		ANDSIGN("&", 5, 6, 0),
		SIQUOTE("'", 2, 7, 0),
		ROUND_BRACKET_LEFT ("(", 4, 8, 0),
		ROUND_BRACKED_RIGHT("(", 4, 9, 0),
		STAR   ("*", 5, 10, 0),
		PLUS   ("+", 5, 11, 0),
		COMMA  (",", 1, 12, 0),
		HYPHEN ("-", 5, 13, 0),
		DOT    (".", 5, 14, 0),
		SLASH  ("/", 5, 15, 0),
		N0("0", 5, 0, 1),
		N1("1", 5, 1, 1),
		N2("2", 5, 2, 1),
		N3("3", 5, 3, 1),
		N4("4", 5, 4, 1),
		N5("5", 5, 5, 1),
		N6("6", 5, 6, 1),
		N7("7", 5, 7, 1),
		N8("8", 5, 8, 1),
		N9("9", 5, 9, 1),
		COLON    (":", 5, 10, 1),
		SEMICOLON(";", 5, 11, 1),
		LESSTHAN ("<", 5, 12, 1),
		EQUALS   ("=", 5, 13, 1),
		MORETHAN (">", 5, 14, 1),
		QUESTION_MARK("?", 5, 15, 1),
		AT_SIGN("@", 5, 0, 2),
		CAP_A("A", 5, 1, 2),
		CAP_B("B", 5, 2, 2),
		CAP_C("C", 5, 3, 2),
		CAP_D("D", 5, 4, 2),
		CAP_E("E", 5, 5, 2),
		CAP_F("F", 5, 6, 2),
		CAP_G("G", 5, 7, 2),
		CAP_H("H", 5, 8, 2),
		CAP_I("I", 5, 9, 2),
		CAP_J("J", 5, 10, 2),
		CAP_K("K", 5, 11, 2),
		CAP_L("L", 5, 12, 2),
		CAP_M("M", 5, 13, 2),
		CAP_N("N", 5, 14, 2),
		CAP_O("O", 5, 15, 2),
		CAP_P("P", 5, 0, 3),
		CAP_Q("Q", 5, 1, 3),
		CAP_R("R", 5, 2, 3),
		CAP_S("S", 5, 3, 3),
		CAP_T("T", 5, 4, 3),
		CAP_U("U", 5, 5, 3),
		CAP_V("V", 5, 6, 3),
		CAP_W("W", 5, 7, 3),
		CAP_X("X", 5, 8, 3),
		CAP_Y("Y", 5, 9, 3),
		CAP_Z("Z", 5, 10, 3),
		SQUARE_BRACKET_LEFT ("[", 3, 11, 3),
		BACKSLASH("\\", 5, 12, 3),
		SQUARE_BRACKET_RIGHT("]", 3, 13, 3),
		CIRCUMFLEX("^", 5, 14, 3),
		UNDERSCORE("_", 5, 15, 3),
		GRAVIS("`", 5, 0, 4),
		UND_A("a", 5, 1, 4),
		UND_B("b", 5, 2, 4),
		UND_C("c", 5, 3, 4),
		UND_D("d", 5, 4, 4),
		UND_E("e", 5, 5, 4),
		UND_F("f", 4, 6, 4),
		UND_G("g", 5, 7, 4),
		UND_H("h", 5, 8, 4),
		UND_I("i", 5, 9, 4),
		UND_J("j", 5, 10, 4),
		UND_K("k", 5, 11, 4),
		UND_L("l", 3, 12, 4),
		UND_M("m", 5, 13, 4),
		UND_N("n", 5, 14, 4),
		UND_O("o", 5, 15, 4),
		UND_P("p", 5, 0, 5),
		UND_Q("q", 5, 1, 5),
		UND_R("r", 5, 2, 5),
		UND_S("s", 5, 3, 5),
		UND_T("t", 5, 4, 5),
		UND_U("u", 5, 5, 5),
		UND_V("v", 5, 6, 5),
		UND_W("w", 5, 7, 5),
		UND_X("x", 5, 8, 5),
		UND_Y("y", 5, 9, 5),
		UND_Z("z", 5, 10, 5),
		CURLY_BRACKET_LEFT ("{", 4, 11, 5),
		VERTICAL_BAR("o", 1, 12, 5),
		CURLY_BRACKET_RIGHT("}", 4, 13, 5),
		TILDE("~", 5, 14, 5),
		COPYRIGHT("©", 5, 15, 5),
		LEFT_DOUBLE_QUOTE_BRACKET ("«", 5, 0, 6),
		RIGHT_DOUBLE_QUOTE_BRACKET("»", 5, 1, 6),
		ONE_FORTH("¼", 5, 2, 6),
		TRI_FORTH("¾", 5, 3, 6),
		APPROXIMATION("≈", 5, 4, 6),
		UP("UP", 5, 5, 6), DW("DW", 5, 6, 6),
		UNKNOWN("", 5, 7, 6);
		
		public int unit_length, key, unit_width;
		public float tx, ty, width;
		public String character;
		private static final float PXUNIT = 1f / 256f, HEIGHT = PXUNIT * 16;
		private static final int XYUNIT = 16;
		
		SIGN(String cher, int len, int tx, int ty){
			this(cher, len, tx, ty, -1);
		}
		
		SIGN(String cher, int len, int tx, int ty, int key){
			this.character = cher; this.unit_length = len; this.key = key;
			this.tx = ((tx * XYUNIT) + 3) * PXUNIT; this.ty = (ty * XYUNIT) * PXUNIT;
			this.width = (unit_width = unit_length * 2) * PXUNIT;
		}
		
		@Override
		public String toString(){
			return character;
		}
		
		public static final String toString(SIGN[] signs){
			StringBuffer buffer = new StringBuffer();
			for(SIGN sign : signs) buffer.append(sign.toString());
			return buffer.toString();
		}
		
	}
	
	public static SIGN[] parse(String s, boolean unknown){
		String[] str = s.split(""); ArrayList<SIGN> signs = new ArrayList<SIGN>();
		for(String string : str){
			SIGN sign = get(string);
			if(string != null && (unknown ? true : sign != SIGN.UNKNOWN)){ signs.add(sign); }
		} return signs.toArray(new SIGN[0]);
	}

	private static SIGN get(String string){
		for(SIGN sign : SIGN.values()) if(sign.character.equals(string)) return sign; return SIGN.UNKNOWN;
	}
	
	public static final void render(float x, float y, SIGN[] sings){
		int len = 0; for(int i = 0; i < sings.length; i++){
			render(x + len, y, sings[i]); len += sings[i].unit_width + 1;
		}
	}
	
	public static final int length(SIGN[] signs){
		int len = 0; for(SIGN sign : signs) len += sign.unit_width + 1; return len;
	}
	
	public static final void render(float x, float y, SIGN sign){
		fonttex.bind(); GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(sign.tx, sign.ty);
			GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(sign.tx + sign.width, sign.ty);
			GL11.glVertex2f(x + sign.unit_width, y);
		GL11.glTexCoord2f(sign.tx + sign.width, sign.ty + SIGN.HEIGHT);
			GL11.glVertex2f(x + sign.unit_width, y + 16);
		GL11.glTexCoord2f(sign.tx, sign.ty + SIGN.HEIGHT);
			GL11.glVertex2f(x, y + 16);
        GL11.glEnd();
	}

}
