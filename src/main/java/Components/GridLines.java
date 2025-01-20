package Components;

import Nova.Camera;
import Nova.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Settings;

public class GridLines extends Component{

    @Override
    public void update(float dt){
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        int firstX = ((int) (cameraPos.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH);
        int firstY = ((int) (cameraPos.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT);

        int numVtLines = (int) (projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH);
        int numHzLines = (int) (projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT);

        int height = (int) (projectionSize.y * camera.getZoom());
        int width = (int) (projectionSize.x * camera.getZoom());

        int maxLines = Math.max(numVtLines,numHzLines);
        Vector3f color = new Vector3f(0.2f,0.2f,0.2f);

        for (int i = 0; i < maxLines; i++) {
            int x = firstX + (Settings.GRID_WIDTH * i);
            int y = firstY + (Settings.GRID_HEIGHT * i);

            if (i < numVtLines){
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHzLines){
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }


        }

    }
}
