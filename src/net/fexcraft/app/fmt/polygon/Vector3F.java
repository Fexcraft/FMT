package net.fexcraft.app.fmt.polygon;

import net.fexcraft.lib.script.ScrBlock;
import net.fexcraft.lib.script.ScrElm;
import net.fexcraft.lib.script.ScrElmType;
import org.joml.Vector3f;

public class Vector3F extends Vector3f implements ScrElm {

    private VecVal vx = new VecVal(this, 0), vy = new VecVal(this, 1), vz = new VecVal(this, 2);
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

    @Override
    public ScrElm scr_get(ScrBlock block, String target){
        switch(target){
            case "x": return vx;
            case "y": return vy;
            case "z": return vz;
        }
        return NULL;
    }

    public static class VecVal implements ScrElm {

        private final Vector3F vec;
        private final int a;

        public VecVal(Vector3F vec, int a){
            this.vec = vec;
            this.a = a;
        }

        @Override
        public int scr_int(){
            return (int)(a == 0 ? vec.x : a == 1 ? vec.y : vec.z);
        }

        @Override
        public float scr_flt(){
            return a == 0 ? vec.x : a == 1 ? vec.y : vec.z;
        }

        @Override
        public void scr_set(int val){
            if(a == 0){
                vec.x = val;
            }
            else if(a == 1){
                vec.y = val;
            }
            else{
                vec.z = val;
            }
            if(vec.polygon != null) vec.update();
        }

        @Override
        public void scr_set(float val){
            if(a == 0){
                vec.x = val;
            }
            else if(a == 1){
                vec.y = val;
            }
            else{
                vec.z = val;
            }
            if(vec.polygon != null) vec.update();
        }

        @Override
        public ScrElmType scr_type(){
            return ScrElmType.FLOAT;
        }

    }

    private void update(){
        if(polygon.glm == null) return;
        polygon.glm.posX = x;
        polygon.glm.posY = y;
        polygon.glm.posZ = z;
    }

}
