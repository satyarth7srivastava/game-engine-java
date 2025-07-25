package scenes;

import Components.Component;
import Components.ComponentDeserializer;
import Nova.Camera;
import Nova.GameObject;
import Nova.GameObjectDeserializer;
import Nova.Transform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import org.joml.Vector2f;
import physics2D.Physics2D;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

    private Camera camera;
    private List<GameObject> gameObjects;
    private Renderer renderer;
    private boolean isRunning;
    private Physics2D physics2D;

    private SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer){
        this.sceneInitializer = sceneInitializer;
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.isRunning = false;
        this.physics2D = new Physics2D();
    }

    public void init(){
        this.camera = new Camera(new Vector2f());
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void start(){
        for(int i = 0; i < gameObjects.size(); i++){
            GameObject go = gameObjects.get(i);
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
        isRunning = true;
    }
    public void addGameObjectToScene(GameObject go){
        if(isRunning){
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }else{
            gameObjects.add(go);
        }
    }

    public void destroy(){
        for (GameObject go : gameObjects){
            go.destroy();
        }
    }

    public List<GameObject> getGameObjects(){
        return this.gameObjects;
    }

    public void editorUpdate(float dt){
        this.camera.adjustProjection();

        for(int i = 0; i < gameObjects.size(); i++){
            GameObject go = gameObjects.get(i);
            go.editorUpdate(dt);

            if (go.isDead()){
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
    }

    public void update(float dt){
        this.camera.adjustProjection();
        this.physics2D.update(dt);

        for(int i = 0; i < gameObjects.size(); i++){
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if (go.isDead()){
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
    }
    public void render(){
        this.renderer.render();
    }

    public Camera getCamera(){
        return this.camera;
    }


    public void imgui(){
        this.sceneInitializer.imgui();
    }

    public GameObject createGameObject(String name){
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void save(){
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
        }
    }

    public GameObject getGameObject(int gameObjectId){
        Optional<GameObject> res = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();
        return res.orElse(null);
    }
}
