package render.shapes;

import render.shapes.rawDataMesh.RawDataMesh;

import java.lang.reflect.Field;
import java.util.Locale;

public abstract class Shape {
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
}