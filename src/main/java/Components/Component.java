package Components;

import Nova.GameObject;
import editor.NImGui;
import imgui.ImGui;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {

    private static int ID_counter = 0; //this is global to component class
    private  int uid = -1;             //while this is of that component instance that is
                                       //is being created

    public transient GameObject gameObject = null;

    public void update(float dt){

    }

    public void editorUpdate(float dt){

    }


    public void start(){

    }

    public void imgui(){
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for(Field field : fields){
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient) continue;
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if(isPrivate){
                    field.setAccessible(true);
                }

                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if(type == int.class){
                    int val = (int) value;
                    field.set(this, NImGui.dragInt(name, val));
                }else if(type == float.class){
                    float val = (float) value;
                    field.set(this, NImGui.dragFloat(name, val));
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)){
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if(ImGui.checkbox(name + ": ", val)){
                        field.set(this, !val);
                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    NImGui.drawVec2Controls(name, val);
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat3(name + ": ", imVec)){
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                } else if (type.isEnum()) {
                    String[] enumVal = getEnumValues(type);
                    String enumType = ((Enum) value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumVal));

                    if (ImGui.combo(field.getName(), index, enumVal, enumVal.length)){
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
                }

                if(isPrivate){
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public void generateId(){
        if(this.uid == -1){
            this.uid = ID_counter++;
        }
    }

    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType){
        String[] enumVal = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T enumIntegerValue : enumType.getEnumConstants()){
            enumVal[i] = enumIntegerValue.name();
            i++;
        }
        return enumVal;
    }
    private int indexOf(String str, String[] arr){
        for (int i = 0; i < arr.length; i++) {
            if (str.equals(arr[i])){
                return i;
            }
        }
        return -1;
    }
    public void destroy(){

    }

    public int getUid(){
        return this.uid;
    }

    public static void init(int maxId){
        ID_counter = maxId;
    }

}
