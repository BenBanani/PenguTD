package info.pengutd.game.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.PenguTD;
import info.pengutd.Assets;
import info.pengutd.game.World;
import org.jetbrains.annotations.NotNull;

/// Standard Gegner mit mehreren Stufen.
/// wenn ein Gegner getroffen wird, wird er zu einem Gegner mit geringerer Stufe
public class NormalEnemy extends Enemy {
    ///  Höhe in tiles
    public static final float HEIGHT = 0.75f;
    ///  breite in tiles
    public static final float WIDTH = 0.65f;
    ///  speed in tiles
    private static final float MOVEMENT_MULTIPLIER = 0.75f;
    private static final float POP_DURATION = 0.5f;
    private final @NotNull Texture texture;
    private final @NotNull Texture popTexture;
    private int level;

    public NormalEnemy(int level, @NotNull World world, int id) {
        super(world, new Vector2(), id); // placeholder position
        texture = PenguTD.getInstance().getAssetManager().get(Assets.NORMAL_ENEMY);
        popTexture = PenguTD.getInstance().getAssetManager().get(Assets.ENEMY_POP);
        this.level = level;
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return new TextureRegion(getPopTimeLeft() > 0 ? popTexture : texture);
    }

    @Override
    public int getHealth() {
        return level;
    }

    @Override
    public float getSpeed() {
        return MOVEMENT_MULTIPLIER * level * getWorld().getTileWidth();
    }

    @Override
    public float getHeight() {
        return HEIGHT * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        if (getPopTimeLeft() <= 0) {
            return WIDTH * getWorld().getTileWidth();
        }
        return getHeight(); // pop texture ist quadratisch
    }

    @Override
    public void pop(int damage) {
        if (getPopTimeLeft() > 0) return; // kein Schaden nehmen wenn gerade gepoppt
        level -= damage;
        setPopTimeLeft(POP_DURATION);
        if (level <= 0) die();
        // todo Geld geben + stats erhöhen
    }

    @Override
    public void die() {
        // todo Geld geben + stats erhöhen
    }

    /// Zum Speichern des Gegners als .json datei
    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("type", new JsonValue("normal_enemy"));
        value.addChild("level", new JsonValue(level));
        return value;
    }

    /// Lädt einen Gegner aus json ein.
    /// world muss bereits gesetzt sein, level ist egal
    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        this.level = json.getInt("level");

    }

    @Override
    public void dispose() {
        // nichts, da Texturen im AssetManager verwaltet werden
    }
}
