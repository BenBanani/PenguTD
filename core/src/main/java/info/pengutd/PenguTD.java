package info.pengutd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import info.pengutd.game.tower.BeaconTower;
import info.pengutd.game.tower.FishTower;
import info.pengutd.game.tower.SniperTower;
import info.pengutd.game.tower.SnowballTower;
import info.pengutd.profile.ProfileManager;
import info.pengutd.screen.StartScreen;
import info.pengutd.stats.StatsManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class PenguTD extends Game {
    @SuppressWarnings("StaticCollection")
    public static final Map<Integer, String> towerNames = new HashMap<>();
    private static PenguTD instance;

    static {
        towerNames.put(1, FishTower.JSON_TYPE);
        towerNames.put(2, SnowballTower.JSON_TYPE);
        towerNames.put(3, SniperTower.JSON_TYPE);
        towerNames.put(4, BeaconTower.JSON_TYPE);
        towerNames.put(5, "machine_gun_tower");
        towerNames.put(6, "mafia_tower");
    }

    private AssetManager assetManager;
    private ProfileManager profileManager;
    private StatsManager statsManager;

    public static PenguTD getInstance() {
        return instance;
    }

    @Override
    public void create() {
        instance = this;
        statsManager = new StatsManager();
        assetManager = new AssetManager();
        profileManager = new ProfileManager();
        if (Settings.get().getFullScreen()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(800, 480);
        }

        // wichtige texturen laden
        assetManager.load(Assets.MISSING_TEXTURE, Texture.class);
        assetManager.load(Assets.UI_BACKGROUND, Texture.class);
        assetManager.load(Assets.START_SCREEN_ATLAS, TextureAtlas.class);

        loadAssets();

        assetManager.finishLoading();
        setScreen(new StartScreen());
    }

    /// startet laden der texturen. danach sollte assetManager.finishLoading(); aufgerufen werden
    public void loadAssets() {
        profileManager.loadProfiles();
        if (profileManager.getCurrentProfile() != null) {
            statsManager.loadProfileStats(profileManager.getCurrentProfile());
        }

        assetManager.load(Assets.SETTINGS_SCREEN_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.STATS_SCREEN_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.ACCOUNT_SCREEN_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.TOWER_SELECTION_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.PAUSE_SCREEN_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.DEFEAT_SCREEN_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.VICTORY_SCREEN_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.ENEMY_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.PROJECTILE_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.TOWER_ATLAS, TextureAtlas.class);

        assetManager.load(Assets.DEFAULT_SKIN, Skin.class);

    }

    public @NotNull AssetManager getAssetManager() {
        return assetManager;
    }

    public @NotNull ProfileManager getProfileManager() {
        return profileManager;
    }

    public @NotNull StatsManager getStatsManager() {
        return statsManager;
    }

    @Override
    public void render() {
        statsManager.addPlayTime(Gdx.graphics.getDeltaTime());
        super.render();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        profileManager.saveProfiles();
        statsManager.saveProfileStats();
        statsManager.saveGlobalStats();
        super.dispose();
    }

    // Muss aufgerufen werden, um Screens zu ändern, wenn der alte Screen nicht mehr gebraucht wird (sonst Memory Leak)
    public void setScreenAndDispose(@NotNull Screen newScreen) {
        Screen oldScreen = getScreen();
        Gdx.app.postRunnable(() -> {
            setScreen(newScreen);
            if (oldScreen != null) {
                Gdx.app.postRunnable(oldScreen::dispose);
            }
        });
    }
}
