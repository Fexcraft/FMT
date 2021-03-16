package net.fexcraft.app.fmt.ui.field;

public interface Field {

	public float getValue();

	public float tryAdd(float value, boolean positive, float rate);

	public void apply(float f);

	public void onScroll(double yoffset);

	public String id();

	public default Runnable update(){ return null; }
	
}
