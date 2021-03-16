package net.fexcraft.app.fmt.demo;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ModelSteve {
	
	private static ModelRendererTurbo[] group0, group1;
	static{
		group0 = new ModelRendererTurbo[8]; int textureX = 64, textureY = 64;
		group0[0] = new ModelRendererTurbo(null, 32, 0, textureX, textureY);
		group0[0].addShapeBox(-4F, -8F, -4F, 8, 8, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group0[0].setRotationPoint(0F, -14F, 0F);
		group0[1] = new ModelRendererTurbo(null, 40, 16, textureX, textureY);
		group0[1].addShapeBox(-3F, -2F, -2F, 4, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group0[1].setRotationPoint(-5F, -12F, 0F);
		group0[1].rotationAngleX = -35F;
		group0[2] = new ModelRendererTurbo(null, 0, 16, textureX, textureY);
		group0[2].addShapeBox(-2F, 0F, -2F, 4, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group0[2].setRotationPoint(1.9F, -2F, 0F);
		group0[2].rotationAngleX = -75F;
		group0[2].rotationAngleY = -15F;
		group0[3] = new ModelRendererTurbo(null, 0, 0, textureX, textureY);
		group0[3].addShapeBox(-4F, -8F, -4F, 8, 8, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group0[3].setRotationPoint(0F, -14F, 0F);
		group0[4] = new ModelRendererTurbo(null, 16, 16, textureX, textureY);
		group0[4].addShapeBox(-4F, 0F, -2F, 8, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group0[4].setRotationPoint(0F, -14F, 0F);
		group0[5] = new ModelRendererTurbo(null, 40, 16, textureX, textureY);
		group0[5].addShapeBox(0F, -2F, -2F, 4, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group0[5].setRotationPoint(4F, -12F, 0F);
		group0[5].rotationAngleX = -35F;
		group0[6] = new ModelRendererTurbo(null, 0, 16, textureX, textureY);
		group0[6].addShapeBox(-2F, 0F, -2F, 4, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group0[6].setRotationPoint(-1.9F, -2F, 0F);
		group0[6].rotationAngleX = -75F;
		group0[6].rotationAngleY = 15F;
		group0[7] = new ModelRendererTurbo(null, 24, 0, textureX, textureY);
		group0[7].addShapeBox(-3F, -6F, -1F, 6, 6, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group0[7].setRotationPoint(0F, -14F, 0F);
		//
		group1 = new ModelRendererTurbo[8];
		group1[0] = new ModelRendererTurbo(null, 32, 0, textureX, textureY);
		group1[0].addShapeBox(-4F, -8F, -4F, 8, 8, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group1[0].setRotationPoint(0F, -14F, 0F);
		group1[1] = new ModelRendererTurbo(null, 40, 16, textureX, textureY);
		group1[1].addShapeBox(-3F, -2F, -2F, 4, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group1[1].setRotationPoint(-5F, -12F, 0F);
		group1[1].rotationAngleX = -35F;
		group1[2] = new ModelRendererTurbo(null, 0, 16, textureX, textureY);
		group1[2].addShapeBox(-2F, 0F, -2F, 4, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group1[2].setRotationPoint(1.9F, -2F, 0F);
		group1[2].rotationAngleX = -75F;
		group1[2].rotationAngleY = -15F;
		group1[3] = new ModelRendererTurbo(null, 0, 0, textureX, textureY);
		group1[3].addShapeBox(-4F, -8F, -4F, 8, 8, 8, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group1[3].setRotationPoint(0F, -14F, 0F);
		group1[4] = new ModelRendererTurbo(null, 16, 16, textureX, textureY);
		group1[4].addShapeBox(-4F, 0F, -2F, 8, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group1[4].setRotationPoint(0F, -14F, 0F);
		group1[5] = new ModelRendererTurbo(null, 40, 16, textureX, textureY);
		group1[5].addShapeBox(0F, -2F, -2F, 4, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group1[5].setRotationPoint(4F, -12F, 0F);
		group1[5].rotationAngleX = -35F;
		group1[6] = new ModelRendererTurbo(null, 0, 16, textureX, textureY);
		group1[6].addShapeBox(-2F, 0F, -2F, 4, 12, 4, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group1[6].setRotationPoint(-1.9F, -2F, 0F);
		group1[6].rotationAngleX = -75F;
		group1[6].rotationAngleY = 15F;
		group1[7] = new ModelRendererTurbo(null, 24, 0, textureX, textureY);
		group1[7].addShapeBox(-3F, -6F, -1F, 6, 6, 1, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F);
		group1[7].setRotationPoint(0F, -14F, 0F);
		for(ModelRendererTurbo turbo : group1){
			turbo.setLines(Settings.getSelectedColor());
		}
	}

	public static void render(float rot){
		GL11.glPushMatrix();
		GL11.glRotatef(rot, 0, 1, 0);
		for(ModelRendererTurbo turbo : group0) turbo.render();
		GL11.glPopMatrix();
	}

	public static void renderLines(int rot){
		GL11.glPushMatrix();
		GL11.glRotatef(rot, 0, 1, 0);
		for(ModelRendererTurbo turbo : group1) turbo.render();
		GL11.glPopMatrix();
	}

}
