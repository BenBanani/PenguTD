package info.pengutd.save;

import com.badlogic.gdx.utils.JsonValue;

public interface JsonSerializable {
    JsonValue toJson();
    void fromJson(JsonValue json);
}
