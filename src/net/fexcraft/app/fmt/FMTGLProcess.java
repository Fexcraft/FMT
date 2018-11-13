package net.fexcraft.app.fmt;

import org.lwjgl.opengl.DisplayMode;

import net.fexcraft.app.fmt.ui.UserInterface;

public interface FMTGLProcess {

	public DisplayMode getDisplayMode();

	public void setupUI(UserInterface ui);

	public UserInterface getUserInterface();

	public void reset();

}
