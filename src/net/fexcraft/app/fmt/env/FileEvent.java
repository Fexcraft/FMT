package net.fexcraft.app.fmt.env;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public enum FileEvent {

	CREATE,
	DELETE,
	CHANGE;

	public boolean create(){
		return this == CREATE;
	}

	public boolean delete(){
		return this == DELETE;
	}

	public boolean change(){
		return this == CHANGE;
	}

}
