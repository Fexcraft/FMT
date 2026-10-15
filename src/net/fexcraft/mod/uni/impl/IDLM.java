package net.fexcraft.mod.uni.impl;

import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class IDLM implements IDLManager {

	@Override
	public IDL get(String idl, boolean cache, boolean named){
		return new IDLL(idl);
	}

	public static class IDLL implements IDL {

		private String sp, id;

		public IDLL(String idl){
			String[] a = idl.split(":");
			if(a.length < 1){
				Logging.log("WARNING, invalid IDL: " + idl);
			}
			if(a.length < 2){
				sp = "fmt";
				id = idl;
			}
			else{
				sp = a[0];
				id = a[1];
			}
		}

		@Override
		public String space(){
			return sp;
		}

		@Override
		public String id(){
			return id;
		}

	}

}
