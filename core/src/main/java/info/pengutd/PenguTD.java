package info.pengutd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import info.pengutd.screen.StartScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class PenguTD extends Game {
    private static PenguTD instance;

    @Override
    public void create() {
        instance = this;
        if (Settings.get().getFullScreen()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(800, 480);
        }
        setScreen(new StartScreen());
    }

    // Muss aufgerufen werden um Screens zu ändern, wenn der alte Screen nicht mehr gebraucht wird (sonst Memory Leak)
    public void setScreenAndDispose(Screen newScreen) {
        Screen oldScreen = getScreen();
        setScreen(newScreen);
        if (oldScreen != null) oldScreen.dispose();
    }

    @Override
    public void render() {
        super.render();
    }

    public static PenguTD getInstance() {
        return instance;
    }
}
