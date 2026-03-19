package backend.libgdx.render.shapes;

import backend.libgdx.render.rawDataMesh.RawDataMesh;
import engine.graphics.interfaces.IShape;

import java.lang.reflect.Field;

public abstract class Shape implements IShape {
    protected String key;

    public abstract RawDataMesh createRawDataMesh();

    public String getKey(){
        return key;
    }

    public void inicialiceKey(){
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName().toUpperCase());
        sb.append("[");
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getName().equals("key")) continue;
            try {
                field.setAccessible(true);
                sb.append(field.getName())
                        .append(":(")
                        .append(field.get(this))
                        .append("),");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        this.key = sb.toString();
    }

    public String getType(){
        return this.getClass().getSimpleName();
    }

    // Remove reflection-based implementation - let subclasses override
    public float[] getParameters(){
        return new float[0]; // Default empty implementation
    }
    
    // Utility method to safely get field values
    protected float[] getShapeParameters() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            java.util.List<Float> params = new java.util.ArrayList<>();
            
            for (Field field : fields) {
                if (field.getName().equals("key")) continue;
                field.setAccessible(true);
                Object value = field.get(this);
                if (value instanceof Float) {
                    params.add((Float) value);
                } else if (value instanceof Integer) {
                    params.add(((Integer) value).floatValue());
                }
            }
            
            float[] result = new float[params.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = params.get(i);
            }
            return result;
        } catch (IllegalAccessException e) {
            // Log error but don't crash
            System.err.println("Error accessing shape parameters: " + e.getMessage());
            return new float[0];
        }
    }
}