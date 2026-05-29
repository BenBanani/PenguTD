package info.pengutd.stats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import info.pengutd.profile.PlayerProfile;
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

    public void addKill() {
        globalStats.enemiesKilled++;
        assert profileStats != null; // Im Spiel ist immer ein Profil ausgewählt
        profileStats.enemiesKilled++;
        assert gameStats != null; // Im Spiel ist immer ein Spiel ausgewählt
        gameStats.kills++;
    }

    public void addDamage(int damage) {
        assert gameStats != null;
        gameStats.damageDealt += damage;
    }

    public void addPlacedTower() {
        assert gameStats != null;
        gameStats.towersPlaced++;
    }

    public void addWaveFinished() { // todo
        assert gameStats != null;
        gameStats.wave++;
        assert profileStats != null;
        if (gameStats.wave > profileStats.highestWave) {
            profileStats.highestWave = gameStats.wave;
        }
        if (gameStats.wave > globalStats.highestWave) {
            globalStats.highestWave = gameStats.wave;
        }
    }

    public void addMoneyEarned(int amount) {
        assert  gameStats != null;
        gameStats.money += amount;
        assert profileStats != null;
        profileStats.moneyEarned += amount;
        globalStats.moneyEarned += amount;
    }

    public void addPlayTime(float seconds) {
        if (gameStats != null) {
            gameStats.playTime += seconds;
        }
        if (profileStats != null) {
            profileStats.playTime += seconds;
        }
        globalStats.playTime += seconds;
    }

    public void addGamePlayed() {
        globalStats.gamesPlayed++;
        assert profileStats != null;
        profileStats.gamesPlayed++;
    }

    public void addWin() { // todo
        assert profileStats != null;
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
