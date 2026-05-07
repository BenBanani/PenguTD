package info.pengutd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import info.pengutd.screen.StartScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class PenguTD extends Game {
    private static PenguTD instance;
    private AssetManager assetManager;

    public static PenguTD getInstance() {
        return instance;
    }

    @Override
    public void create() {
        instance = this;
        assetManager = new AssetManager();
        if (Settings.get().getFullScreen()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(800, 480);
        }
        loadAssets();
        setScreen(new StartScreen());
    }

    private void loadAssets() {
        assetManager.load(Assets.ENEMY_POP, Texture.class);
        assetManager.load(Assets.WARRIOR_ENEMY, Texture.class);
        assetManager.load(Assets.TOWER1, Texture.class);
        assetManager.load(Assets.UI_BACKGROUND, Texture.class);

        assetManager.load(Assets.SETTINGS_SCREEN_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.START_SCREEN_ATLAS, TextureAtlas.class);
        assetManager.load(Assets.TOWER_SELECTION_ATLAS, TextureAtlas.class);

        assetManager.finishLoading();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        super.dispose();
    }

    // Muss aufgerufen werden, um Screens zu ändern, wenn der alte Screen nicht mehr gebraucht wird (sonst Memory Leak)
    public void setScreenAndDispose(Screen newScreen) {
        Screen oldScreen = getScreen();
        Gdx.app.postRunnable(() -> {
            setScreen(newScreen);
            if (oldScreen != null) oldScreen.dispose();
        });
    }
}
