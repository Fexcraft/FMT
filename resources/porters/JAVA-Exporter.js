// Defines the module
function getId(file) { return "javaexporter"; }
function getName(file) { return "TMT-JAVA-EXPORTER"; }
function getExtensions() { return [".java"]; }
function isExporter() { return true; }
function isImporter() { return false; }

function fillVal(partObject, value, defaultValue) {
	if (partObject[value] === undefined) {
    	partObject[value] = defaultValue
  	}
  	return partObject
}

function convertBox(partObject) {
	partObject = fillVal(partObject, "texture_x", 0)
	partObject = fillVal(partObject, "texture_y", 0)
	partObject = fillVal(partObject, "type", "shapebox")

	partObject = fillVal(partObject, "pos_x", 0)
	partObject = fillVal(partObject, "pos_y", 0)
	partObject = fillVal(partObject, "pos_z", 0)

	partObject = fillVal(partObject, "off_x", 0)
	partObject = fillVal(partObject, "off_y", 0)
	partObject = fillVal(partObject, "off_z", 0)

	partObject = fillVal(partObject, "rot_x", 0)
	partObject = fillVal(partObject, "rot_y", 0)
	partObject = fillVal(partObject, "rot_z", 0)

	partObject = fillVal(partObject, "visible", true)

	partObject = fillVal(partObject, "width", 1)
	partObject = fillVal(partObject, "height", 1)
	partObject = fillVal(partObject, "depth", 1)

	// I went mad with multicursor. (Not)

	for (var i=0; i<8; i++) {
		partObject = fillVal(partObject, "x" + i, 0)
		partObject = fillVal(partObject, "y" + i, 0)
		partObject = fillVal(partObject, "z" + i, 0)
	}

	return filledBox
}

function exportModel(jtmt, file) {
	print(jtmt)
	var model = JSON.parse(jtmt);
	if (model.format != "1" || model.type != "jtmt") {
		return "Not able to export from this format";
	}

	var partGroups = []
	for (var i=0; i<model.model; i++) {
		
	}

	var FileWriter = Java.type("java.io.FileWriter");
	var writer = new FileWriter(file);
	writer.write(jtmt);
	writer.flush();
	writer.close();
	return "YEEEEEEE";
}
