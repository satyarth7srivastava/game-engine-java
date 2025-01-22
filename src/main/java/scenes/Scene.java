package scenes;

import Components.Component;
import Components.ComponentDeserializer;
import Nova.Camera;
import Nova.GameObject;
import Nova.GameObjectDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scene {

    protected Camera camera;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected Renderer renderer = new Renderer();
    protected boolean loadedLevel = false;
    private boolean isRunning = false;

    public Scene(){
//        this.renderer = new Renderer();
    }

    public void init(){

    }

    public void start(){
        for(GameObject go : this.gameObjects){
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }
    public void addGameObjectToScene(GameObject go){
        if(isRunning){
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }else{
            gameObjects.add(go);
        }
    }

    public abstract void update(float dt);
    public abstract void render();

    public Camera getCamera(){
        return this.camera;
    }


    public void imgui(){

    }
    public void saveExit(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> objectsToSerialize = new ArrayList<>();
            for(GameObject obj : this.gameObjects){
                if(obj.isDoSerialization()){
                    objectsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objectsToSerialize));
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void load(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!inFile.isEmpty()) {
            int maxGoId = -1;
            int maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);

                for (Component c : objs[i].getAllComponents()) {
                    if ((c.getUid() > maxCompId)) {
                        maxCompId = c.getUid();
                    }
                }
                if (objs[i].getUid() > maxGoId) {
                    maxGoId = objs[i].getUid();
                }
            }

            maxGoId++;
            maxCompId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);
            this.loadedLevel = true;
        }
    }

    public GameObject getGameObject(int gameObjectId){
        Optional<GameObject> res = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();
        return res.orElse(null);
    }
}
