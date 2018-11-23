var getId = function(){
	return "testexporter"
}

var getName = function(){
	return "JTMT Exporter"
}

var getExtensions = function(){
	return [ ".jtmt", ".json" ]
}

var isImporter = function(){ return false; }
var isExporter = function(){ return true; }

var exportModel = function(jtmt, file){
	var FileWriter = Java.type("java.io.FileWriter");
	var writer = new FileWriter(file);
	writer.write(jtmt); writer.flush(); writer.close();
	return ">>> Success! >>>";
}