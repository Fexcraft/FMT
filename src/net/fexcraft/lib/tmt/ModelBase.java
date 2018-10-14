package net.fexcraft.lib.tmt;

/**
* Replaces the old `ModelBase` in this package.
* @Author Ferdinand Calo' (FEX___96)
*/

public abstract class ModelBase {
	
	public static final ModelBase EMPTY = new EmptyModelBase();
	
	/** render whole model */
	public abstract void render();
	
	/** render sub-model array */
	public void render(ModelRendererTurbo[] model){
		for(ModelRendererTurbo sub : model){
			sub.render();
		}
	}
	
	public void render(ModelRendererTurbo[] model, float scale, boolean rotorder){
		for(ModelRendererTurbo sub : model){
			sub.render(scale);
		}
	}
	
	protected void translate(ModelRendererTurbo[] model, float x, float y, float z){
		for(ModelRendererTurbo mod : model){
			mod.rotationPointX += x;
			mod.rotationPointY += y;
			mod.rotationPointZ += z;
		}
	}
	
	public abstract void translateAll(float x, float y, float z);
	
	protected void rotate(ModelRendererTurbo[] model, float x, float y, float z){
		for(ModelRendererTurbo mod : model){
			mod.rotateAngleX += x;
			mod.rotateAngleY += y;
			mod.rotateAngleZ += z;
		}
	}
	
	public abstract void rotateAll(float x, float y, float z);
	
	/** Legacy Method */
	protected void flip(ModelRendererTurbo[] model){
		fixRotations(model);
	}
	
	/** Legacy Method */
	public void flipAll(){
		//To be overriden by extending classes.
	}
	
	/**
	 * Based on @EternalBlueFlame's fix.
	 * @param array ModelRendererTurbo Array
	 */
	public static void fixRotations(ModelRendererTurbo[] array){
        for(ModelRendererTurbo model : array){
            if(model.isShape3D){
                model.rotateAngleY = -model.rotateAngleY;
                model.rotateAngleX = -model.rotateAngleX;
                model.rotateAngleZ = -model.rotateAngleZ + 3.14159f;
            }
            else{
                model.rotateAngleZ = -model.rotateAngleZ;
            }
        }
    }
	
	/*public static final void bindTexture(ResourceLocation rs){
		Minecraft.getMinecraft().renderEngine.bindTexture(rs);
		//TextureManager.bindTexture(rs);
	}*/
	
}