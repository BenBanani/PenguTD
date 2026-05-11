package info.pengutd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.jetbrains.annotations.NotNull;

public class Assets {
    private Assets() {}

    public static final String TOWER1 = "game/tower/tower1.png";
    public static final String UI_BACKGROUND = "background.png";
    public static final String SNOWBALL_PROJECTILE = "game/tower/projectile/snowball.png";
    public static final String MISSING_TEXTURE = "missing_texture.png";

    public static final String SETTINGS_SCREEN_ATLAS = "atlas/settings_screen_ui.atlas";
    public static final String START_SCREEN_ATLAS = "atlas/start_screen_ui.atlas";
    public static final String TOWER_SELECTION_ATLAS = "atlas/tower_selection_ui.atlas";
    public static final String WARRIOR_ENEMY_ATLAS = "atlas/enemies.atlas";

    /// @return the texture region or the missing texture if the region is not found
    public static @NotNull TextureRegion findRegionOrMissing(TextureAtlas atlas, String name) {
        TextureRegion region = atlas.findRegion(name);
        if (region != null) return region;

        Gdx.app.error("Assets", "Missing texture: " + name);
        return new TextureRegion(PenguTD.getInstance().getAssetManager().get(MISSING_TEXTURE, Texture.class));
    }
}
