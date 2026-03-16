package net.fexcraft.app.fmt.nui;

import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.nui.FMTInterface.col_bd;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Field extends Element {

	public Consumer<String> consumer;
	public String previous;
	public boolean hidden;

	public Field(float width){
		super();
		size(width, 26);
		hoverable = true;
		selectable = true;
		color(col_bd);
	}

	public Field(float width, Consumer<String> cons){
		this(width);
		consumer = cons;
	}

	@Override
	public void init(Object... args){
		text("");
		//add(new Element().color(RGB.BLACK).size(2, 20).pos(w - 2, 5));
	}

	@Override
	public Element text(Object ntext){
		super.text(ntext).recompile();
		if(elements != null){
			elements.get(0).pos(text.w > w ? w - 2 : text.w + 6, 5);
		}
		return this;
	}

	@Override
	protected void onSelect(){
		previous = text.text();
	}

	@Override
	protected void onDeselect(Element current){
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

}
