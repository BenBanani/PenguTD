package info.pengutd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import org.jetbrains.annotations.NotNull;

/**
 * Singleton Klasse für alle Einstellungen.
 * Jede Einstellung ist mit eindeutigen Key in dem App Preferences gespeichert.
 */
public class Settings {
    private static Settings instance;
    private final @NotNull Preferences preferences;
    private static final String PREF_NAME = "pengutd_settings";

    private static final String KEY_SOUND = "sound";
    private static final String KEY_MUSIC = "music";
    private static final String KEY_FULL_SCREEN = "full_screen";
    private static final String KEY_ACCOUNT = "account";

    private Settings() {
        preferences = Gdx.app.getPreferences(PREF_NAME);
    }

    public static @NotNull Settings get() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public float getSoundVolume() {
        return preferences.getFloat(KEY_SOUND, 1.0f);
    }

    public float getMusicVolume() {
        return preferences.getFloat(KEY_MUSIC, 1.0f);
    }

    public boolean getFullScreen() {
        return preferences.getBoolean(KEY_FULL_SCREEN, false);
    }

    public String getAccountName() {
        return preferences.getString(KEY_ACCOUNT);
    }

    public void setSoundVolume(float volume) {
        preferences.putFloat(KEY_SOUND, volume);
        preferences.flush();
    }

    public void setMusicVolume(float volume) {
        preferences.putFloat(KEY_MUSIC, volume);
        preferences.flush();
    }

    public void setFullScreen(boolean fullScreen) {
        preferences.putBoolean(KEY_FULL_SCREEN, fullScreen);
        preferences.flush();
    }

    public void setAccountName(String accountName) {
        preferences.putString(KEY_ACCOUNT, accountName);
        preferences.flush();
    }
}
