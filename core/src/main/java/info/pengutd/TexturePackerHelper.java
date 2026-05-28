package info.pengutd;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/// Helper klasse für erstellen von texture Atlas anstelle von einzelnen Texturen
public final class TexturePackerHelper {
    private static final String assets = "assets/";
    private static final String output = assets + "atlas";

    private TexturePackerHelper() {
    }

    public static void main (String[] args) {
        TexturePacker.process(assets + "start_screen",output, "start_screen_ui");
        TexturePacker.process(assets + "settings_screen",output, "settings_screen_ui");
        TexturePacker.process(assets + "/game/tower_selection_screen",output, "tower_selection_ui");
        TexturePacker.process(assets + "/game/pause_screen",output, "pause_screen_ui");
        TexturePacker.process(assets + "/game/defeat_screen", output, "defeat_screen_ui");
        TexturePacker.process(assets + "/game/victory_screen", output, "victory_screen_ui");
        TexturePacker.process(assets + "/game/enemy",output, "enemies");
        TexturePacker.process(assets + "/game/tower/projectile", output, "projectiles");
        TexturePacker.process(assets + "/game/tower/", output, "towers");
    }
}
