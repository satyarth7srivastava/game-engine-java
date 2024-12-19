package styy;

public class LevelScene extends Scene{
    public LevelScene(){
        System.out.println("Inside Level Scene");
        Window.get().r = 1;
        Window.get().b = 0;
        Window.get().g = 1;
        Window.get().a = 1;
    }

    @Override
    public void update(float dt) {
    }
}
