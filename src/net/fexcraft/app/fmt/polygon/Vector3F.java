package net.fexcraft.app.fmt.polygon;

import org.joml.Vector3f;

public class Vector3F extends Vector3f {

    public Polygon polygon;

    public Vector3F(Polygon poly, float x, float y, float z){
        super(x, y, z);
        polygon = poly;
    }

    public Vector3F(float x, float y, float z){
        super(x, y, z);
    }

    public Vector3F(){
        super();
    }

    private void update(){
        if(polygon.glm == null) return;
        polygon.glm.posX = x;
        polygon.glm.posY = y;
        polygon.glm.posZ = z;
    }

}
