package net.fexcraft.app.fmt.demo;

import java.util.ArrayList;

import org.joml.Vector3f;

import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ModelSteve {
	
	private static ArrayList<ModelRendererTurbo> marker;
	private static int textureX = 64, textureY = 64;
	private static float shrink = 1.875f * 0.5f;
	static{
		marker = new ArrayList<>();
		marker.add(new ModelRendererTurbo(marker, 16, 16, textureX, textureY).addBox(-4, -12, -2, 8, 12, 4));
		marker.add(new ModelRendererTurbo(marker, 0, 0, textureX, textureY).addBox(-4, 0, -4, 8, 8, 8)
			.setRotationPoint(0, -20, 0).setRotationAngle(0, 0, 0)
		);
		marker.add(new ModelRendererTurbo(marker, 32, 48, textureX, textureY).addBox(4, -0.4f, -0.9f, 4, 12, 4)
			.setRotationPoint(0, -12, 0).setRotationAngle(-35, 0, 0)
		);
		marker.add(new ModelRendererTurbo(marker, 40, 16, textureX, textureY).addBox(-8, -0.4f, -0.9f, 4, 12, 4)
			.setRotationPoint(0, -12, 0).setRotationAngle(-35, 0, 0)
		);
		marker.add(new ModelRendererTurbo(marker, 0, 16, textureX, textureY).addBox(-4, 0, -4, 4, 12, 4)
			.setRotationPoint(0, 2, 0).setRotationAngle(-83, 14.5f, 0)
		);
		marker.add(new ModelRendererTurbo(marker, 16, 48, textureX, textureY).addBox(0, 0, -4, 4, 12, 4)
			.setRotationPoint(0, 2, 0).setRotationAngle(-83, -14.5f, 0)
		);
	}

	public static void render(Vector3f pos, float rot, float scale){
		for(ModelRendererTurbo turbo : marker){
			turbo.rotationPointX += pos.x;
			turbo.rotationPointY += pos.y;
			turbo.rotationPointZ += pos.z;
			turbo.rotationAngleY += rot;
			turbo.render(Static.sixteenth * scale * shrink);
			turbo.rotationAngleY -= rot;
			turbo.rotationPointX -= pos.x;
			turbo.rotationPointY -= pos.y;
			turbo.rotationPointZ -= pos.z;
		}
	}

}
