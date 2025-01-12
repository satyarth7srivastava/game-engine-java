package renderer;

import Nova.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;
import util.JMath;

import javax.sound.sampled.Line;
import java.awt.*;
import java.awt.datatransfer.FlavorEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int MAX_LINES = 500;

    private static List<Line2D> Lines = new ArrayList<>();
    // 6 floats per vertex, 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

    private static int vaoID, vboID;

    private static boolean started = false; // to check if we have initialized these things over the gpu


    private static void start(){
        //generate the vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //creating the vbo and buffer memory
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);


        //enabling the pointers
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3*Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(2.0f);
    }

    public static void beginFrame(){
        if (!started){
            start();
            started = true;
        }

        // Remove dead lines
        for (int i = 0; i < Lines.size(); i++){
            if(Lines.get(i).beginFrame() < 0){
                Lines.remove(i);
                i--;
            }
        }
    }

    public static void draw(){
        if (Lines.size() <= 0) return;

        int index = 0;
        for (Line2D line : Lines){
            for (int i=0; i<2; i++){
                Vector2f position = (i==0) ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                //load position
                vertexArray[index] = position.x;
                vertexArray[index+1] = position.y;
                vertexArray[index+2] = -10.0f;

                //load color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;

                index += 6;
            }
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, Lines.size() *6*2));


            //using our shader
            shader.use();
            shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
            shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

            //binding vao
            glBindVertexArray(vaoID);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            //drawing the batch
            //Note: this might use Bresenham's line algorithm
            glDrawArrays(GL_LINES, 0, Lines.size()*6*2);


            //disabling the location
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);

            shader.detach();
        }
    }

    //=========================
    //Add line2D methods
    //=========================

    public static void addLine2D(Vector2f from, Vector2f to){
        //TODO: Add CONSTANTS FOR COMMON COLORS
        addLine2D(from, to, new Vector3f(0,1, 0), 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color){
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifeTime){
        if(Lines.size() >= MAX_LINES) return;
        DebugDraw.Lines.add(new Line2D(from, to, color, lifeTime));
    }

    //=========================
    //Add Box2D methods
    //=========================

    public static void addBox2D(Vector2f center, Vector2f dimension,float rotation){
        addBox2D(center, dimension, rotation, new Vector3f(1,0,0), 1);
    }
    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation, Vector3f color){
        addBox2D(center, dimension, rotation, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation, Vector3f color, int lifeTime){
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimension).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimension).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if (rotation != 0.0f){
            for (Vector2f vert : vertices){
                JMath.rotate(vert, rotation, center);
            }
        }

        addLine2D(vertices[0], vertices[1], color, lifeTime);
        addLine2D(vertices[0], vertices[3], color, lifeTime);
        addLine2D(vertices[1], vertices[2], color, lifeTime);
        addLine2D(vertices[2], vertices[3], color, lifeTime);
    }

    //=========================
    //Add Circles methods
    //=========================

    public static void addCircle(Vector2f center, float radius){
        addCircle(center, radius, new Vector3f(1,0,0), 1);
    }
    public static void addCircle(Vector2f center, float radius, Vector3f color){
        addCircle(center, radius, color, 1);
    }

    public static void addCircle(Vector2f center, float radius, Vector3f color, int lifeTime){
        int noPoints = 28;
        Vector2f[] points = new Vector2f[noPoints];
        float increment = (float) 360 / noPoints;
        float currentAngle = 0;

        for (int i = 0; i < noPoints; i++) {
            Vector2f tmp = new Vector2f(0, radius);
            JMath.rotate(tmp, currentAngle, new Vector2f());
            points[i] = tmp.add(center);

            if(i > 0){
                addLine2D(points[i-1], points[i], color, lifeTime);
            }

            currentAngle += increment;
        }
        addLine2D(points[points.length - 1], points[0], color, lifeTime);
    }
}
