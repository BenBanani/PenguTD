package info.pengutd.save;

import com.badlogic.gdx.utils.JsonValue;

/// Interface für Klassen, die als JSON serialisiert werden können um spielstände zu speichern
/// die Json dateien sollten in assets/saves gespeichert werden
/// jedes Json Objekt muss ein type Feld haben
/// todo alex tests hierfür machen
public interface JsonSerializable {
    JsonValue toJson();
    void fromJson(JsonValue json);
}
