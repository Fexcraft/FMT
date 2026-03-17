package net.fexcraft.app.fmt.nui;

import net.fexcraft.lib.common.math.RGB;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.nui.editor.EditorTab.FS;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Field extends Element {

	public final FieldType type;
	public Consumer<String> consumer;
	public String previous;
	private Element reset;

	public Field(FieldType ftype, float width){
		super();
		type = ftype;
		size(width - FS, FS);
		hoverable = true;
		selectable = true;
		color(0xa6b3b3);
	}

	public Field(FieldType type, float width, Consumer<String> cons){
		this(type, width);
		consumer = cons;
	}

	@Override
	public void init(Object... args){
		text("");
		add(reset = new Element().color(0xf02c00).size(type.text() ? FS : 5, FS).pos(w, 0).text("X")
			.text_centered(true).hoverable(true).onclick(info -> reset_text()));
		reset.hide();
	}

	@Override
	public Element text(Object ntext){
		super.text(ntext);

		return this;
	}

	@Override
	protected void onSelect(){
		previous = text.text();
		reset.show();
	}

	@Override
	protected void onDeselect(Element current){
		reset_text();
		reset.hide();
	}

	public void reset_text(){
		text(previous);
	}

	public boolean onInput(int key, int code, int action, int mods){
		if(key == GLFW_KEY_LEFT_SHIFT || key == GLFW_KEY_RIGHT_SHIFT){
			return false;
		}
		if(key == GLFW_KEY_ESCAPE){
			select(null);
			return false;
		}
		if(action != GLFW_RELEASE) return true;
		if(key == GLFW_KEY_ENTER || key == GLFW_KEY_KP_ENTER){
			if(consumer != null) consumer.accept(text.text());
			previous = null;
			return true;
		}
		if(key >= 32 && key <= 96){
			String kn = GLFW.glfwGetKeyName(key, code);
			if(kn == null) kn = "";
			text(text.text() + kn);
		}
		return true;
	}

	public static enum FieldType {

		TEXT,
		FLOAT,
		INT;

		public boolean text(){
			return this == TEXT;
		}

	}

}
