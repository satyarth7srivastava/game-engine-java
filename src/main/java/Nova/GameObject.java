package Nova;


import Components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_counter = 0;
    private int uid = -1;
    private boolean doSerialization = true;

    public String name;
    private List<Component> components;
    public transient Transform transform;

    public GameObject(String name){
        this.name = name;
        this.components = new ArrayList<>();
        this.uid = ID_counter++;
    }

    public <T extends Component> T getComponent(Class<T> componentClass){
        for (Component c : components){
            if(componentClass.isAssignableFrom(c.getClass())) {
                try{
                    return componentClass.cast(c);
                } catch (ClassCastException e){
                    e.printStackTrace();
                    assert false:"Error: Class casting error!!";
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass){
        for(int i = 0; i < components.size(); i++){
            Component c = components.get(i);
            if(componentClass.isAssignableFrom(c.getClass())){
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c){
        c.generateId();
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(float dt){
        for (Component component : components) {
            component.update((dt));
        }
    }

    public void start(){
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            component.start();
        }
    }


    public void imgui(){
        for(Component c : components){
            c.imgui();
        }
    }

    public static void init(int maxId){
        ID_counter = maxId;
    }
    public int getUid(){
        return this.uid;
    }

    public List<Component> getAllComponents() {
        return components;
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean isDoSerialization(){
        return this.doSerialization;
    }
}
