package info.pengutd;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import info.pengutd.screen.StartScreen;

class PenguTDTest {

    @AfterEach
    void tearDown() throws Exception {
        setStaticField(PenguTD.class, "instance", null);
        Gdx.app = null;
        Gdx.graphics = null;
    }

    @Test
    void create_shouldSetInstanceLoadAssetsAndUseWindowedModeWhenFullscreenIsFalse() throws Exception {
        Graphics graphics = mock(Graphics.class);
        Application app = mock(Application.class);
        Graphics.DisplayMode displayMode = mock(Graphics.DisplayMode.class);

        Gdx.graphics = graphics;
        Gdx.app = app;

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(app).postRunnable(any(Runnable.class));

        when(graphics.getDisplayMode()).thenReturn(displayMode);

        Settings settings = mock(Settings.class);
        when(settings.getFullScreen()).thenReturn(false);

        try (MockedStatic<Settings> settingsMock = mockStatic(Settings.class);
             MockedConstruction<AssetManager> assetManagerConstruction = mockConstruction(AssetManager.class);
             MockedConstruction<StartScreen> startScreenConstruction = mockConstruction(StartScreen.class)) {

            settingsMock.when(Settings::get).thenReturn(settings);

            PenguTD game = new PenguTD();
            game.create();

            assertSame(game, PenguTD.getInstance());
            verify(graphics).setWindowedMode(800, 480);

            assertEquals(1, assetManagerConstruction.constructed().size());
            AssetManager assetManager = assetManagerConstruction.constructed().get(0);

            verify(assetManager).load(Assets.TOWER1, Texture.class);
            verify(assetManager).load(Assets.UI_BACKGROUND, Texture.class);
            verify(assetManager).load(Assets.SNOWBALL_PROJECTILE, Texture.class);
            verify(assetManager).load(Assets.SETTINGS_SCREEN_ATLAS, TextureAtlas.class);
            verify(assetManager).load(Assets.START_SCREEN_ATLAS, TextureAtlas.class);
            verify(assetManager).load(Assets.TOWER_SELECTION_ATLAS, TextureAtlas.class);
            verify(assetManager).load(Assets.WARRIOR_ENEMY_ATLAS, TextureAtlas.class);
            verify(assetManager).finishLoading();

            assertSame(assetManager, game.getAssetManager());
            assertEquals(1, startScreenConstruction.constructed().size());
        }
    }

    @Test
    void create_shouldSetInstanceLoadAssetsAndUseFullscreenModeWhenFullscreenIsTrue() throws Exception {
        Graphics graphics = mock(Graphics.class);
        Application app = mock(Application.class);
        Graphics.DisplayMode displayMode = mock(Graphics.DisplayMode.class);

        Gdx.graphics = graphics;
        Gdx.app = app;

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(app).postRunnable(any(Runnable.class));

        when(graphics.getDisplayMode()).thenReturn(displayMode);

        Settings settings = mock(Settings.class);
        when(settings.getFullScreen()).thenReturn(true);

        try (MockedStatic<Settings> settingsMock = mockStatic(Settings.class);
             MockedConstruction<AssetManager> assetManagerConstruction = mockConstruction(AssetManager.class);
             MockedConstruction<StartScreen> startScreenConstruction = mockConstruction(StartScreen.class)) {

            settingsMock.when(Settings::get).thenReturn(settings);

            PenguTD game = new PenguTD();
            game.create();

            assertSame(game, PenguTD.getInstance());
            verify(graphics).setFullscreenMode(displayMode);

            assertEquals(1, assetManagerConstruction.constructed().size());
            AssetManager assetManager = assetManagerConstruction.constructed().get(0);

            verify(assetManager).load(Assets.TOWER1, Texture.class);
            verify(assetManager).load(Assets.UI_BACKGROUND, Texture.class);
            verify(assetManager).load(Assets.SNOWBALL_PROJECTILE, Texture.class);
            verify(assetManager).load(Assets.SETTINGS_SCREEN_ATLAS, TextureAtlas.class);
            verify(assetManager).load(Assets.START_SCREEN_ATLAS, TextureAtlas.class);
            verify(assetManager).load(Assets.TOWER_SELECTION_ATLAS, TextureAtlas.class);
            verify(assetManager).load(Assets.WARRIOR_ENEMY_ATLAS, TextureAtlas.class);
            verify(assetManager).finishLoading();

            assertSame(assetManager, game.getAssetManager());
            assertEquals(1, startScreenConstruction.constructed().size());
        }
    }

    @Test
    void dispose_shouldDisposeAssetManager() throws Exception {
        PenguTD game = new PenguTD();
        AssetManager assetManager = mock(AssetManager.class);

        setPrivateField(game, "assetManager", assetManager);

        game.dispose();

        verify(assetManager).dispose();
    }

    @Test
    void setScreenAndDispose_shouldSetNewScreenAndDisposeOldScreen() throws Exception {
        Graphics graphics = mock(Graphics.class);
        Application app = mock(Application.class);

        Gdx.graphics = graphics;
        Gdx.app = app;

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(app).postRunnable(any(Runnable.class));

        PenguTD game = new PenguTD();
        Screen oldScreen = mock(Screen.class);
        Screen newScreen = mock(Screen.class);

        game.setScreen(oldScreen);
        game.setScreenAndDispose(newScreen);

        assertSame(newScreen, game.getScreen());
        verify(oldScreen).dispose();
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void setStaticField(Class<?> type, String fieldName, Object value) throws Exception {
        Field field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}