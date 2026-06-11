package info.pengutd.stats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import info.pengutd.profile.PlayerProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// Statsmanager kümmert sich um alle Stats im Spiel.
/// Er lebt in PenguTD.getInstance()
public class StatsManager {
    private final @NotNull GlobalStats globalStats;
    private @Nullable ProfileStats profileStats;
    private @Nullable GameStats gameStats;


    /// Erstellt einen StatsManager mit profile und game stats als null und lädt die globalStats
    public StatsManager() {
        globalStats = new GlobalStats();
    }

    /// Formatiert das Datum in (*d) (hh) (mm) ss.
    /// Für negative sekunden gibt es "" zurück
    @Contract(pure = true)
    public static @NotNull String formatDuration(long seconds) {
        if (seconds < 0) return "";
        long time = seconds;
        long days = time / 86400;
        time %= 86400;

        long hours = time / 3600;
        time %= 3600;

        long minutes = time / 60;
        time %= 60;

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append("d ");
        }

        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }

        if (minutes > 0 || hours > 0 || days > 0) {
            sb.append(minutes).append("m ");
        }

        sb.append(time).append("s");

        return sb.toString().trim();
    }

    public void addKill() {
        {
            assert profileStats != null; // Im Spiel ist immer ein Profil ausgewählt
            assert gameStats != null; // Im Spiel ist immer ein Spiel ausgewählt
            assert !gameStats.isClosed();
        }
        globalStats.enemiesKilled++;
        profileStats.enemiesKilled++;
        gameStats.kills++;
    }

    public void addDamage(int damage) {
        {
            assert gameStats != null;
            assert !gameStats.isClosed();
        }
        gameStats.damageDealt += damage;
    }

    public void addPlacedTower() {
        {
            assert gameStats != null;
            assert !gameStats.isClosed();
        }
        gameStats.towersPlaced++;
    }

    public void addWaveFinished() { // todo
        {
            assert profileStats != null;
            assert gameStats != null;
            assert !gameStats.isClosed();
        }
        gameStats.wave++;
        if (gameStats.wave > profileStats.highestWave) {
            profileStats.highestWave = gameStats.wave;
        }
        if (gameStats.wave > globalStats.highestWave) {
            globalStats.highestWave = gameStats.wave;
        }
    }

    public void addMoneyEarned(int amount) {
        {
            assert profileStats != null;
            assert gameStats != null;
            assert !gameStats.isClosed();
        }
        gameStats.money += amount;
        profileStats.moneyEarned += amount;
        globalStats.moneyEarned += amount;
    }

    public void addPlayTime(float seconds) {
        if (gameStats != null && !gameStats.isClosed()) {
            gameStats.playTime += seconds;
        }
        if (profileStats != null) {
            profileStats.playTime += seconds;
        }
        globalStats.playTime += seconds;
    }

    public void addGamePlayed() {
        {
            assert profileStats != null;
        }
        globalStats.gamesPlayed++;
        profileStats.gamesPlayed++;
    }

    public void addWin() { // todo
        {
            assert profileStats != null;
        }
        profileStats.wins++;
    }

    public void loadProfileStats(@Nullable PlayerProfile profile) {
        if (profile == null) {
            profileStats = null;
            return;
        }
        FileHandle handle = Gdx.files.local("saves/" + profile.getName() + "/stats.json");
        if (!handle.exists()) {
            profileStats = new ProfileStats(profile);
            saveProfileStats();
            return;
        }
        profileStats = new ProfileStats(profile);
        profileStats.fromJson(new JsonReader().parse(handle));
    }

    public GameStats createGameStats() {
        gameStats = new GameStats();
        return gameStats;
    }

    public @NotNull GlobalStats getGlobalStats() {
        return globalStats;
    }

    public @Nullable ProfileStats getProfileStats() {
        return profileStats;
    }

    public void setProfileStats(@Nullable ProfileStats profileStats) {
        this.profileStats = profileStats;
    }

    public @Nullable GameStats getGameStats() {
        return gameStats;
    }

    public void setGameStats(@Nullable GameStats gameStats) {
        this.gameStats = gameStats;
    }

    public void saveProfileStats() {
        if (profileStats == null) {
            return;
        }
        FileHandle handle = Gdx.files.local("saves/" + profileStats.getProfile().getName() + "/stats.json");
        handle.writeString(profileStats.toJson().prettyPrint(JsonWriter.OutputType.json, 1), false);
    }

    public void saveGlobalStats() {
        globalStats.saveStats();
    }
}
