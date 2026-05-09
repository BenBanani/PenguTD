package info.pengutd;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

class SettingsTest {

    private Preferences prefs;
    private MockedStatic<Gdx> gdxMock;

    @BeforeEach
    void setUp() throws Exception {
        // Mock Preferences
        prefs = mock(Preferences.class);

        // Mock Application
        Application app = mock(Application.class);
        when(app.getPreferences("pengutd_settings")).thenReturn(prefs);

        // Mock static Gdx
        gdxMock = mockStatic(Gdx.class);
        Gdx.app = app;

        // Reset Singleton (wichtig!)
        java.lang.reflect.Field instanceField = Settings.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @AfterEach
    void tearDown() {
        gdxMock.close();
    }

    @Test
    void testGetDefaultValues() {
        when(prefs.getFloat("sound", 1.0f)).thenReturn(1.0f);
        when(prefs.getFloat("music", 1.0f)).thenReturn(1.0f);
        when(prefs.getBoolean("full_screen", false)).thenReturn(false);
        when(prefs.getString("account")).thenReturn("");

        Settings settings = Settings.get();

        assertEquals(1.0f, settings.getSoundVolume());
        assertEquals(1.0f, settings.getMusicVolume());
        assertFalse(settings.getFullScreen());
        assertEquals("", settings.getAccountName());
    }

    @Test
    void testSetSoundVolume() {
        Settings settings = Settings.get();

        settings.setSoundVolume(0.5f);

        verify(prefs).putFloat("sound", 0.5f);
        verify(prefs).flush();
    }

    @Test
    void testSetMusicVolume() {
        Settings settings = Settings.get();

        settings.setMusicVolume(0.8f);

        verify(prefs).putFloat("music", 0.8f);
        verify(prefs).flush();
    }

    @Test
    void testSetFullScreen() {
        Settings settings = Settings.get();

        settings.setFullScreen(true);

        verify(prefs).putBoolean("full_screen", true);
        verify(prefs).flush();
    }

    @Test
    void testSetAccountName() {
        Settings settings = Settings.get();

        settings.setAccountName("Player1");

        verify(prefs).putString("account", "Player1");
        verify(prefs).flush();
    }
}