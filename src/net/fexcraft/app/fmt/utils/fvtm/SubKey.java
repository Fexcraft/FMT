package net.fexcraft.app.fmt.utils.fvtm;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SubKey {

	public final int idx;
	public final String key;

	public SubKey(String str){
		key = str;
		idx = 0;
	}

	public SubKey(int index){
		key = (idx = index)  + "";
	}

	@Override
	public String toString(){
		return key;
	}

}
