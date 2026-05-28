package info.pengutd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.jetbrains.annotations.NotNull;

public enum Assets {
    ;
    public static final String UI_BACKGROUND = "background.png";
    public static final String MISSING_TEXTURE = "missing_texture.png";

    public static final String SETTINGS_SCREEN_ATLAS = "atlas/settings_screen_ui.atlas";
    public static final String START_SCREEN_ATLAS = "atlas/start_screen_ui.atlas";
    public static final String ACCOUNT_SCREEN_ATLAS = "atlas/account_screen_ui.atlas";
    public static final String TOWER_SELECTION_ATLAS = "atlas/tower_selection_ui.atlas";
    public static final String PAUSE_SCREEN_ATLAS = "atlas/pause_screen_ui.atlas";
    public static final String DEFEAT_SCREEN_ATLAS = "atlas/defeat_screen_ui.atlas";
    public static final String VICTORY_SCREEN_ATLAS = "atlas/victory_screen_ui.atlas";
    public static final String ENEMY_ATLAS = "atlas/enemies.atlas";
    public static final String PROJECTILE_ATLAS = "atlas/projectiles.atlas";
    public static final String TOWER_ATLAS = "atlas/towers.atlas";

    public static final String DEFAULT_SKIN = "uiskin.json";

    /// @return the texture region or the missing texture if the region is not found
    public static @NotNull TextureRegion findRegionOrMissing(@NotNull TextureAtlas atlas, @NotNull String name) {
        TextureRegion region = atlas.findRegion(name);
        if (region != null) return region;

        Gdx.app.error("Assets", "Missing texture: " + name);
        return new TextureRegion(PenguTD.getInstance().getAssetManager().get(MISSING_TEXTURE, Texture.class));
    }
}
