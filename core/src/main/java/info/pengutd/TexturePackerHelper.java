package info.pengutd;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/// Helper klasse für erstellen von texture Atlas anstelle von einzelnen Texturen
public class TexturePackerHelper {
    private static final String assets = "assets/";
    public static void main (String[] args) {
        TexturePacker.process(assets + "start_screen",assets + "atlas", "start_screen_ui");
        TexturePacker.process(assets + "settings_screen",assets + "atlas", "settings_screen_ui");
        TexturePacker.process(assets + "/game/tower_selection_screen",assets + "atlas", "tower_selection_ui");
    }
}
