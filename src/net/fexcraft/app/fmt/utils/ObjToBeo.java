package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.ObjView;
import net.fexcraft.app.fmt.port.ex.BObjExporter;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.FRLObjParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static net.fexcraft.app.fmt.utils.Logging.log;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ObjToBeo {

	public static void run(String[] args) throws IOException {
		String loc = args[1];
		for(int i = 2; i < args.length; i++){
			loc += " " + args[i];
		}
		File jar = new File(loc);
		if(!jar.exists()){
			Logging.log("File not found. Specified: " + jar.toPath());
			return;
		}
		if(!jar.getName().endsWith(".jar") && !jar.getName().endsWith(".zip")){
			Logging.log("Specified file is not a jar/zip archive.");
			return;
		}
		Logging.log("Starting conversion of: " + jar.toPath());
		File fol = jar.getParentFile();
		File con = new File(fol, jar.getName().substring(0, jar.getName().length() - 4) + ".beo.jar");
		ZipFile zip = new ZipFile(jar);
		ZipOutputStream zop = new ZipOutputStream(new FileOutputStream(con));
		zip.stream().forEach(elm -> {
			try{
				BObjExporter.INSTANCE.setNormals(true);
				if(elm.getName().endsWith(".obj")){
					Logging.log("Converting: " + elm.getName());
					Map<String, ArrayList<Polyhedron>> map = new FRLObjParser(null, zip.getInputStream(elm))
						.normals(true).flipUV(false, true).parse();
					Model temp = new Model(null, elm.getName().substring(elm.getName().lastIndexOf("/") + 1, elm.getName().length() - 4));
					for(Map.Entry<String, ArrayList<Polyhedron>> entry : map.entrySet()){
						temp.addGroup(null, entry.getKey());
						temp.get(entry.getKey()).add(new ObjView(temp, entry.getValue()));
					}
					zop.putNextEntry(new ZipEntry(elm.getName().substring(0, elm.getName().length() - 3) + "beo"));
					BObjExporter.INSTANCE.writeModel(temp, zop, temp.allgroups());
					zop.closeEntry();
				}
				else{
					zop.putNextEntry(elm);
					InputStream stream = zip.getInputStream(elm);
					byte[] bytes = new byte[1024];
					int length;
					while((length = stream.read(bytes)) >= 0){
						zop.write(bytes, 0, length);
					}
					zop.closeEntry();
					stream.close();
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
		});
		zip.close();
		zop.close();
		Logging.log("Finished conversion of: " + con.toPath());
	}

}
