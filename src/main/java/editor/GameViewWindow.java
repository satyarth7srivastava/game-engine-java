package editor;

import Nova.MouseListner;
import Nova.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;

public class GameViewWindow {

    private float leftX, rightX, bottomY, topY;
    private boolean isPlaying = false;

    public void imgui(){
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                    | ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();

        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)){
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }

        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)){
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }

        ImGui.endMenuBar();

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenterPositionForViewport(windowSize);


        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topleft = new ImVec2();
        ImGui.getCursorScreenPos(topleft);
        topleft.x -= ImGui.getScrollX();
        topleft.y -= ImGui.getScrollY();

        leftX = topleft.x;
        rightX = topleft.x + windowSize.x;
        bottomY = topleft.y;
        topY = topleft.y + windowSize.y;

        int textureId = Window.getFrameBuffer().getTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        MouseListner.setGameViewPos(new Vector2f(topleft.x, topleft.y));
        MouseListner.setGameViewSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport(){
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y){
            //we must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenterPositionForViewport(ImVec2 aspectSize){
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewPortX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewPortY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);
        return new ImVec2(viewPortX + ImGui.getCursorPosX(), viewPortY + ImGui.getCursorPosY());
    }

    public boolean getWantCaptureMouse(){
        return (MouseListner.getX() >= leftX && MouseListner.getX() <= rightX
                && MouseListner.getY() >= bottomY && MouseListner.getY() <= topY);
    }
}