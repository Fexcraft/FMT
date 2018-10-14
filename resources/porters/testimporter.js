var getId = function(file){
	return "testimporter"
}

var getName = function(file){
	return "Test JTMT Importer"
}

var getExtensions = function(){
	return [ ".jtmt", ".json" ]
}

var isImporter = function(){ return true; }
var isExporter = function(){ return false; }

var importModel = function(file){
	var lines = [];//temporary lines holder;
	var FileReader = Java.type("java.io.FileReader");//get the file reader class
	var Scanner = Java.type("java.util.Scanner");//get the scanner class
	var scan = new Scanner(new FileReader(file));//create a scanner instance
	while(scan.hasNext()){//loop through the scanner and get the lines out
		lines.push(scan.next());
	}
	//in this example we're just gonna feed it into the json util as one string,
	var string = "";
	for(var i = 0; i < lines.length; i++){
		string += lines[i];
	}
	return string;
}