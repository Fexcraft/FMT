// FMT EXPORTER (JTMT -> FLANSMOD TMT), MADE BY @GoldSloth#0472
// Covered by Fexcraft licence.

// Defines the module
function getId(file) { return "flans_exporter"; }
function getName(file) { return "TMT-FLANS-EXPORTER"; }
function getExtensions() { return [".java"]; }
function isExporter() { return true; }
function isImporter() { return false; }

function writeTestLog(message) {
  var FileWriter=Java.type("java.io.FileWriter");
  var fw = new FileWriter("exporter-log.log");
  fw.write(message + "\n");
  fw.close()
}

var MultiString = function(f) {
	return f.toString().split('\n').slice(1, -1).join('\n');
}

function fillVal(partObject, value, defaultValue) {
	if (partObject[value] === undefined) {
    	partObject[value] = defaultValue
  	}
  	return partObject
}

function convertCylinder(partObject) {
	partObject = fillVal(partObject, "texture_x", 0)
	partObject = fillVal(partObject, "texture_y", 0)
	partObject = fillVal(partObject, "type", "cylinder")

	partObject = fillVal(partObject, "pos_x", 0)
	partObject = fillVal(partObject, "pos_y", 0)
	partObject = fillVal(partObject, "pos_z", 0)

	partObject = fillVal(partObject, "off_x", 0)
	partObject = fillVal(partObject, "off_y", 0)
	partObject = fillVal(partObject, "off_z", 0)

	partObject = fillVal(partObject, "radius", 1)
	partObject = fillVal(partObject, "length", 1)
	partObject = fillVal(partObject, "segments", 1)
	partObject = fillVal(partObject, "direction", 1)
	partObject = fillVal(partObject, "basescale", 1)
	partObject = fillVal(partObject, "topscale", 1)
	partObject = fillVal(partObject, "top_offset_x", 0)
	partObject = fillVal(partObject, "top_offset_y", 0)
	partObject = fillVal(partObject, "top_offset_z", 0)

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

	partObject = fillVal(partObject, "width", 1)
	partObject = fillVal(partObject, "height", 1)
	partObject = fillVal(partObject, "depth", 1)

	for (var i = 0; i < 8; i++) {
		partObject = fillVal(partObject, "x" + i, 0)
		partObject = fillVal(partObject, "y" + i, 0)
		partObject = fillVal(partObject, "z" + i, 0)
	}

	return partObject
}

function exportModel(jtmt, file) {
	var model = JSON.parse(jtmt)
//   writeTestLog(JSON.stringify(model))
	if (model.format != "2" || model.type != "jtmt") {
		return "Not able to export from this format";
	}

	var partGroups = []
	for (var partGroup in model.groups) {
		partGroups.push({"name": partGroup, "polygons": model.groups[partGroup].polygons})
	}
	var modelType = "vehicle"
	var dynamicText = ""

	for (var i = 0; i < partGroups.length; i++) {
		for (var j=0; j < partGroups[i].polygons.length; j++) {
			if (partGroups[i].polygons[j].type == "shapebox" || partGroups[i].polygons[j].type == "box") {
				partGroups[i].polygons[j] = convertBox(partGroups[i].polygons[j])
			} else if (partGroups[i].polygons[j].type == "cylinder") {
				partGroups[i].polygons[j] = convertCylinder(partGroups[i].polygons[j])
			}
		}
	}

	for (var i = 0; i < partGroups.length; i++) {
		var currentGroupName = partGroups[i].name
		var currentGroupParts = partGroups[i].polygons
		dynamicText += "\n§{0} = new ModelRendererTurbo[{1}];".replace("{0}",currentGroupName).replace(
			"{1}", currentGroupParts.length
		)
		for (var j = 0; j < currentGroupParts.length; j++) {
			var currentPart = currentGroupParts[j]
			dynamicText += "\n§{0}[{1}] = new ModelRendererTurbo(this, {2}, {3}, textureX, textureY);".replace(
				"{0}", currentGroupName
				).replace(
				"{1}", j
				).replace(
				"{2}", currentPart.texture_x
				).replace(
				"{3}", currentPart.texture_y
			)
			var offsets = "{0}F, {1}F, {2}F".replace(
				"{0}", currentPart.off_x
				).replace(
				"{1}", currentPart.off_y
				).replace(
				"{2}", currentPart.off_z
			)
			if (currentPart.type == "shapebox" || currentPart.type == "box") {
				var size = "{0}, {1}, {2}".replace(
					"{0}", currentPart.width
					).replace(
					"{1}", currentPart.height
					).replace(
					"{2}", currentPart.depth
				)
				var corners = ""
				for (var i = 0; i < 8; i++) {
					corners += ", {0}F, {1}F, {2}F".replace("{0}", currentPart["x"+i]).replace("{1}", currentPart["y"+i]).replace("{2}", currentPart["z"+i])
				}
				dynamicText += "\n§{0}[{1}].addShapeBox({2}, {3}, 0F{4});".replace(
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
			}

			if (currentPart.type == "cylinder") {
				dynamicText += "\n§{0}[{1}].addCylinder({2}, {3}F, {4}F, {5}, {6}F, {7}F, {8});".replace(
					"{0}", currentGroupName
				).replace(
					"{1}", j
				).replace(
					"{2}", offsets
				).replace(
					"{3}", currentPart.radius
				).replace(
					"{4}", currentPart.length
				).replace(
					"{5}", currentPart.segments
				).replace(
					"{6}", currentPart.basescale
				).replace(
					"{7}", currentPart.topscale
				).replace(
					"{8}", currentPart.direction
				)
			}
			var position = "{0}F, {1}F, {2}F".replace(
				"{0}", currentPart.pos_x
			).replace(
				"{1}", currentPart.pos_y
			).replace(
				"{2}", currentPart.pos_z
			)

			dynamicText += "\n§{0}[{1}].setRotationPoint({2});".replace(
				"{0}", currentGroupName
			).replace(
				"{1}", j
			).replace(
				"{2}", position
			)

			if (currentPart.rot_x != undefined) {
				dynamicText += "\n§{0}[{1}].rotateAngleX = {2}F;".replace(
					"{0}", currentGroupName
				).replace(
					"{1}", j
				).replace(
					"{2}", currentPart.rot_x
				)
			}
			if (currentPart.rot_y != undefined) {
				dynamicText += "\n§{0}[{1}].rotateAngleY = {2}F;".replace(
					"{0}", currentGroupName
				).replace(
					"{1}", j
				).replace(
					"{2}", currentPart.rot_y
				)
			}
			if (currentPart.rot_z != undefined) {
				dynamicText += "\n§{0}[{1}].rotateAngleZ = {2}F;".replace(
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
// FMT EXPORTER (JTMT -> FLANSMOD TMT), MADE BY @GoldSloth#0472
// Covered by Fexcraft licence.
package com.flansmod.client.model.INSERTYOURPACKAGENAMEHERE;

import com.flansmod.client.model.ModelVehicle;
import com.flansmod.client.tmt.ModelRendererTurbo;
import com.flansmod.client.tmt.Coord2D;
import com.flansmod.client.tmt.Shape2D;

public class YOURMODELNAMEHERE extends ModelVehicle
{
	int textureX = {E4};
	int textureY = {E5};

	public YOURMODELNAMEHERE()
	{
±
		translateAll(0F, 0F, 0F);
		flipAll();
	}
}
		**/})

		outputText = outputText.replace('±', dynamicText).replace('{E4}', model.texture_size_x).replace('{E5}', model.texture_size_y)
		while (outputText.indexOf("§") > -1) {
			outputText = outputText.replace("§", "\t\t")
		}
	}

	var FileWriter = Java.type("java.io.FileWriter");
	var writer = new FileWriter(file);
	writer.write(outputText);
	writer.flush();
	writer.close();
	return "Export Successful";
}
