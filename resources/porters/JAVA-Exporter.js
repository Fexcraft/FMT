// Defines the module
function getId(file) { return "javaexporter"; }
function getName(file) { return "TMT-JAVA-EXPORTER"; }
function getExtensions() { return [".java"]; }
function isExporter() { return true; }
function isImporter() { return false; }

var MultiString = function(f) {
	return f.toString().split('\n').slice(1, -1).join('\n');
}

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

	// partObject = fillVal(partObject, "rot_x", 0) Not actually needed.
	// partObject = fillVal(partObject, "rot_y", 0)
	// partObject = fillVal(partObject, "rot_z", 0)

	// partObject = fillVal(partObject, "visible", true) Not needed in spec.

	partObject = fillVal(partObject, "width", 1)
	partObject = fillVal(partObject, "height", 1)
	partObject = fillVal(partObject, "depth", 1)

	// I went mad with multicursor. (Not)

	for (var i=0; i<8; i++) {
		partObject = fillVal(partObject, "x" + i, 0)
		partObject = fillVal(partObject, "y" + i, 0)
		partObject = fillVal(partObject, "z" + i, 0)
	}

	return partObject
}

function exportModel(jtmt, file) {
	// print(jtmt)
	var model = JSON.parse(jtmt);
	if (model.format != "1" || model.type != "jtmt") {
		return "Not able to export from this format";
	}

	var partGroups = []
	for (var partGroup in model.model) {
		partGroups.push({"name": partGroup, "parts": model.model[partGroup]})
	}
	var modelType = "vehicle"
	var dynamicText = ""

	for (var i = 0; i < partGroups.length; i++) {
		for (var j=0; j < partGroups[i].parts.length; j++) {
			// print(partGroups[i][j])
			partGroups[i].parts[j] = convertBox(partGroups[i].parts[j])
		}
	}

	for (var i = 0; i < partGroups.length; i++) {
		var currentGroupName = partGroups[i].name
		print(currentGroupName)
		var currentGroupParts = partGroups[i].parts
		print(currentGroupParts)
		dynamicText += "\n\t\t{0} = new ModelRendererTurbo[{1}];".replace("{0}",currentGroupName).replace(
			"{1}", currentGroupParts.length
		)
		for (var j = 0; j < currentGroupParts.length; j++) {
			var currentPart = currentGroupParts[j]
			dynamicText += "\n\t\t${currentGroupName}[{0}] = new ModelRendererTurbo[this, {1}, {2}, textureX, textureY];".replace(
				"{0}", j
				).replace(
				"{1}", currentPart.texture_x
				).replace(
				"{2}", currentPart.texture_y
			)
			var offsets = "{0}F, {1}F, {2}F".replace(
				"{0}", currentPart.off_x
				).replace(
				"{1}", currentPart.off_y
				).replace(
				"{2}", currentPart.off_z
			)
			var size = "{0}, {1}, {2}".replace(
				"{0}", currentPart.width
				).replace(
				"{1}", currentPart.height
				).replace(
				"{2}", currentPart.depth
			)

			var corners = ""
			for (var i=0; i<8; i++) {
				corners += ", {0}F, {1}F, {2}F".replace("{0}", currentPart["x"+i]).replace("{1}", currentPart["y"+i]).replace("{2}"), currentPart["z"+i]
			}
			dynamicText += "\n\t\t{0}[{1}].addShapeBox({2}, {3}, 0F{4});".replace(
				"{0}", currentGroupName
			).replace(
				"{1}", j
			).replace(
				"{2}", offsets
			).replace(
				"{3}", size
			).replace(
				"{4}", corners
			)
			var position = "{0}F, {1}F, {2}F".replace(
				"{0}", currentPart.pos_x
			).replace(
				"{1}", currentPart.pos_y
			).replace(
				"{2}", currentPart.pos_z
			)

			dynamicText += "\n\t\t{0}[{1}].setRotationPoint({2});".replace(
				"{0}", currentGroupName
			).replace(
				"{1}", j
			).replace(
				"{2}", position
			)

			if (currentPart.rot_x != undefined) {
				dynamicText += "\n\t\t{0}[{1}].rotateAngleX = {2}}F;".replace(
					"{0}", currentGroupName
				).replace(
					"{1}", j
				).replace(
					"{2}", currentPart.rot_x
				)
			}
			if (currentPart.rot_y != undefined) {
				dynamicText += "\n\t\t{0}[{1}].rotateAngleY = {2}}F;".replace(
					"{0}", currentGroupName
				).replace(
					"{1}", j
				).replace(
					"{2}", currentPart.rot_y
				)
			}
			if (currentPart.rot_z != undefined) {
				dynamicText += "\n\t\t{0}[{1}].rotateAngleZ = {2}}F;".replace(
					"{0}", currentGroupName
				).replace(
					"{1}", j
				).replace(
					"{2}", currentPart.rot_z
				)
			}
		}
	}

	var outputText = ""
	if (modelType == "vehicle") {
		outputText += MultiString(function() {/**
		package com.flansmod.client.model.INSERTYOURPACKAGENAMEHERE;

		import com.flansmod.client.model.ModelVehicle;
		import com.flansmod.client.tmt.ModelRendererTurbo;
		import com.flansmod.client.tmt.Coord2D;
		import com.flansmod.client.tmt.Shape2D;

		public class YOURMODELNAMEHERE extends ModelVehicle
		{
			§int textureX = ${model.texture_size_x};
			§int textureY = ${model.texture_size_y};

			§public YOURMODELNAMEHERE()
			§{
				§§±
				§§translateAll(0F, 0F, 0F);
				§§flipAll();
			§}
		}
		**/}).replace("§", "\t").replace("§", "\t")
		outputText = outputText.replace('±', dynamicText)
	}

	var FileWriter = Java.type("java.io.FileWriter");
	var writer = new FileWriter(file);
	writer.write(outputText);
	writer.flush();
	writer.close();
	return "YEEEEEEE";
}
