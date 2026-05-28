package info.pengutd.save;

import com.badlogic.gdx.utils.JsonValue;
import org.jetbrains.annotations.NotNull;

/// Interface für Klassen, die als JSON serialisiert werden können um spielstände zu speichern
/// die Json dateien sollten in assets/saves gespeichert werden
/// jedes Json Objekt muss ein type Feld haben
public interface JsonSerializable {
    @NotNull JsonValue toJson();
    void fromJson(@NotNull JsonValue json);
}
