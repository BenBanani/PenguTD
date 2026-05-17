package info.pengutd;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

/**
 * Unit tests for PenguTD.
 *
 * create() and dispose() require an OpenGL context and real asset files so they
 * are not tested here. The following is covered:
 *
 *   - getInstance()         → returns the value assigned to the static field
 *   - getAssetManager()     → returns the injected AssetManager
 *   - setScreenAndDispose() → posts a runnable that swaps screens and then posts
 *                             a second runnable that disposes the old screen
 *
 * Game.setScreen() internally reads Gdx.graphics.getWidth() / getHeight() for
 * the resize() call, so both Gdx.app and Gdx.graphics must be mocked.
 */
@ExtendWith(MockitoExtension.class)
class PenguTDTest {

    // -------------------------------------------------------------------------
    // Reflection helpers
    // -------------------------------------------------------------------------

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    private void setStaticField(Class<?> clazz, String name, Object value) throws Exception {
        Field f = clazz.getDeclaredField(name);
        f.setAccessible(true);
        f.set(null, value);
    }

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    /**
     * Creates a PenguTD with state injected via reflection so create() never runs.
     * Both Gdx.app and Gdx.graphics are mocked:
     *   - Gdx.app      is read by setScreenAndDispose() for postRunnable()
     *   - Gdx.graphics is read by Game.setScreen() for getWidth() / getHeight()
     */
    private PenguTD makePenguTD(AssetManager assetManager) throws Exception {
        Gdx.app      = mock(Application.class);
        Gdx.graphics = mock(Graphics.class);

        PenguTD game = new PenguTD();
        setStaticField(PenguTD.class, "instance", game);
        setField(game, "assetManager", assetManager);
        return game;
    }

    // =========================================================================
    // getInstance
    // =========================================================================

    @Test
    void getInstance_returnsNullBeforeCreate() throws Exception {
        setStaticField(PenguTD.class, "instance", null);

        assertNull(PenguTD.getInstance());
    }

    @Test
    void getInstance_returnsInjectedInstance() throws Exception {
        PenguTD game = makePenguTD(mock(AssetManager.class));

        assertSame(game, PenguTD.getInstance());
    }

    // =========================================================================
    // getAssetManager
    // =========================================================================

    @Test
    void getAssetManager_returnsInjectedAssetManager() throws Exception {
        AssetManager assetManager = mock(AssetManager.class);
        PenguTD game = makePenguTD(assetManager);

        assertSame(assetManager, game.getAssetManager());
    }

    // =========================================================================
    // setScreenAndDispose
    // =========================================================================

    /**
     * setScreenAndDispose() posts a Runnable to Gdx.app. We capture and run it
     * synchronously to assert on what it does without a real application loop.
     *
     * Execution model:
     *   setScreenAndDispose(newScreen)
     *     └─ posts R1 to Gdx.app
     *          R1: setScreen(newScreen) + posts R2 to Gdx.app
     *                R2: oldScreen.dispose()
     */
    @Test
    void setScreenAndDispose_postsRunnableThatSetsNewScreen() throws Exception {
        PenguTD game      = makePenguTD(mock(AssetManager.class));
        Screen oldScreen  = mock(Screen.class);
        Screen newScreen  = mock(Screen.class);

        // Put oldScreen in place — Game.setScreen() is safe now that Gdx.graphics is mocked
        game.setScreen(oldScreen);

        ArgumentCaptor<Runnable> outerCaptor = ArgumentCaptor.forClass(Runnable.class);
        game.setScreenAndDispose(newScreen);
        verify(Gdx.app).postRunnable(outerCaptor.capture());

        // Running R1 swaps the screen
        outerCaptor.getValue().run();

        assertSame(newScreen, game.getScreen());
    }

    @Test
    void setScreenAndDispose_innerRunnableDisposesOldScreen() throws Exception {
        PenguTD game      = makePenguTD(mock(AssetManager.class));
        Screen oldScreen  = mock(Screen.class);
        Screen newScreen  = mock(Screen.class);

        game.setScreen(oldScreen);

        ArgumentCaptor<Runnable> outerCaptor = ArgumentCaptor.forClass(Runnable.class);
        game.setScreenAndDispose(newScreen);
        verify(Gdx.app).postRunnable(outerCaptor.capture());

        // R1 runs → sets new screen AND posts R2
        ArgumentCaptor<Runnable> innerCaptor = ArgumentCaptor.forClass(Runnable.class);
        outerCaptor.getValue().run();
        verify(Gdx.app, times(2)).postRunnable(innerCaptor.capture());

        // R2 runs → disposes old screen
        innerCaptor.getAllValues().get(1).run();
        verify(oldScreen).dispose();
    }

    @Test
    void setScreenAndDispose_doesNotPostDisposeRunnableWhenNoOldScreen() throws Exception {
        PenguTD game     = makePenguTD(mock(AssetManager.class));
        Screen newScreen = mock(Screen.class);

        // No prior setScreen() call — getScreen() returns null
        ArgumentCaptor<Runnable> outerCaptor = ArgumentCaptor.forClass(Runnable.class);
        game.setScreenAndDispose(newScreen);
        verify(Gdx.app).postRunnable(outerCaptor.capture());

        // R1 runs — must NOT post a second runnable because there is no old screen
        outerCaptor.getValue().run();
        verify(Gdx.app, times(1)).postRunnable(any());
    }
}