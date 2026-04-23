package info.pengutd;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
public class TexturePackerHelper {
    private static final String assets = "assets/";
    public static void main (String[] args) {
        TexturePacker.process(assets + "start_screen",assets + "atlas", "start_screen_ui");
    }
}
