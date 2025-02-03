package util;

import org.lwjgl.PointerBuffer;
import org.lwjgl.util.nfd.NFDOpenDialogArgs;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.nio.ByteBuffer;

public class FileDialogHandler {

    public static String getFilePath() {
        PointerBuffer outp = PointerBuffer.allocateDirect(400);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(400);
        NFDOpenDialogArgs args1 = new NFDOpenDialogArgs(byteBuffer);

        NativeFileDialog.NFD_Init();
        int res = NativeFileDialog.NFD_OpenDialog_With(outp, args1);

        if (res == 1){
            return outp.getStringUTF8();
        }else{
            return "";
        }
    }

    public static void addSpriteSheet(String resourceName){

    }
}
