package info.pengutd.profile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProfileManager {
    public static final String SAVE_FILE = "saves/profiles.json";
    private final @NotNull Array<PlayerProfile> profiles = new Array<>();
    private @Nullable PlayerProfile currentProfile;

    /// @return ausgewähltes Profil oder null
    public @Nullable PlayerProfile getCurrentProfile() {
        return currentProfile;
    }

    /// lädt alle Profile aus der saves/profiles.json datei
    public void loadProfiles() {
        FileHandle handle = Gdx.files.local(SAVE_FILE);

        profiles.clear();
        currentProfile = null;

        if (!handle.exists()) {
            saveProfiles();
            return;
        }

        JsonValue value = new JsonReader().parse(handle);

        JsonValue jsonProfiles = value.get("profiles");
        for (JsonValue profile : jsonProfiles) {
            PlayerProfile playerProfile = new PlayerProfile(profile.getString("name"));
            playerProfile.fromJson(profile);

            profiles.add(playerProfile);
        }

        currentProfile = getProfileByName(value.getString("currentProfile"));
    }

    /// schreibt alle Profile in die saves/profiles.json datei
    public void saveProfiles() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);

        value.addChild("currentProfile", currentProfile != null ? new JsonValue(currentProfile.getName()) : new JsonValue(JsonValue.ValueType.nullValue));

        JsonValue jsonProfiles = new JsonValue(JsonValue.ValueType.array);
        profiles.forEach(p -> jsonProfiles.addChild(p.toJson()));
        value.addChild("profiles", jsonProfiles);

        FileHandle handle = Gdx.files.local("saves/profiles.json");
        handle.writeString(value.prettyPrint(JsonWriter.OutputType.json, 1), false);
    }

    /// @return das Profil mit dem Namen name, oder null, wenn keins gefunden wurde
    public @Nullable PlayerProfile getProfileByName(@NotNull String name) {
        for (int i = 0; i < profiles.size; i++) {
            PlayerProfile profile = profiles.get(i);
            if (profile.getName().equals(name)) return profile;
        }
        return null;
    }

    /// erstellt ein neues leeres Profil, und speichert dieses direkt.
    /// wenn bereits ein Profil mit dem Namen existiert passiert nichts
    ///
    /// @return ob das Profil erstellt werden konnte
    public boolean createProfile(@NotNull String name) {
        return addProfile(new PlayerProfile(name));
    }

    /// erstellt ein neues leeres Profil, und speichert dieses direkt.
    /// wenn bereits ein Profil mit dem Namen existiert passiert nichts
    ///
    /// @return ob das Profil erstellt werden konnte
    public boolean addProfile(@NotNull PlayerProfile profile) {
        if (profiles.contains(profile, false)) {
            return false;
        }
        profiles.insert(0, profile);
        saveProfiles();
        return true;
    }

    // entfernt das Profil, setzt gegebenenfalls currentProfile auf null und speichert
    public void deleteProfile(@NotNull PlayerProfile profile) {
        profiles.removeValue(profile, false);
        if (profile.equals(currentProfile)) {
            currentProfile = null;
        }
        saveProfiles();
    }

    /// Wählt das Profil als currentProfile aus und speichert direkt
    public void selectProfile(@Nullable PlayerProfile profile) {
        currentProfile = profile;
        saveProfiles();
    }

    /// @return alle Profile
    public @NotNull Array<PlayerProfile> getProfiles() {
        return profiles;
    }
}
