package net.fexcraft.app.fmt.attributes;

import java.util.ArrayList;

public enum UpdateType {
	
	POLYGON_POSITION("polygon"),
	POLYGON_ROTATION("polygon"),
	POLYGON_OFFSET("polygon"),
	POLYGON_ADDED("polygon"),
	POLYGON_REMOVED,
	
	GROUP_POSITION("group"),
	GROUP_ROTATION("group"),
	GROUP_OFFSET("group"),
	
	MODEL_POSITION("model"),
	MODEL_ROTATION("model"),
	MODEL_OFFSET("model"),
	MODEL_AUTHOR("model"),
	MODEL_LOAD("@model", "@group", "@polygon");
	
	public ArrayList<String> groups, run_groups;
	
	UpdateType(String... strs){
		for(String str : strs){
			if(str.startsWith("@")){
				if(run_groups == null) run_groups = new ArrayList<>();
				run_groups.add(str.substring(1));
			}
			else{
				if(groups == null) groups = new ArrayList<>();
				groups.add(str);
			}
		}
	}

	boolean containsAny(ArrayList<String> groups){
		if(this.groups != null){
			for(String group : groups){
				if(this.groups.contains(group)) return true;
			}
		}
		return false;
	}

}
