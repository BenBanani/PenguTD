package info.pengutd.game.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
    private final @NotNull Array<Vector2> path = new Array<>();
    ///  Index des aktuellen Zielpunkts im Pfad
    int currentPathIndex = 0;
    private int level;
    private float popTimeLeft = 0f;

    public NormalEnemy(int level, @NotNull World world, int id) {
        super(world, new Vector2(), id); // placeholder position
        texture = PenguTD.getInstance().getAssetManager().get(Assets.NORMAL_ENEMY);
        popTexture = PenguTD.getInstance().getAssetManager().get(Assets.ENEMY_POP);
        this.level = level;

        findPath();
    }

    private void findPath() {
        // Path finding initialisieren
        MapLayer mapLayer = getWorld().getMap().getLayers().get("path");
        if (mapLayer != null) {
            for (MapObject obj : mapLayer.getObjects()) {
                if (obj instanceof PointMapObject) {
                    PointMapObject point = (PointMapObject) obj;
                    path.add(point.getPoint().cpy().add(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)));
                }
            }
            Vector2 start = path.get(0);
            setPos(start);
        } else {
            Gdx.app.error("NormalEnemy", "No path layer found in map");
        }
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return new TextureRegion(popTimeLeft > 0 ? popTexture : texture);
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
    public @NotNull Array<Vector2> getPath() {
        return path;
    }

    @Override
    public float getHeight() {
        return HEIGHT * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        if (popTimeLeft <= 0) {
            return WIDTH * getWorld().getTileWidth();
        }
        return getHeight(); // pop texture ist quadratisch
    }

    /// laufe richtung nächsten waypoint
    @Override
    public void update(float delta) {

        if (popTimeLeft > 0) {
            popTimeLeft -= delta;
            return;
        }

        if (path.size == 0) return;
        if (currentPathIndex >= path.size - 1) return;
        Vector2 target = path.get(currentPathIndex).lerp(path.get(currentPathIndex + 1), 0.02f); // lerp damit der weg nicht so eckig ist

        if (getPos().dst2(target) < getSpeed() * delta * getSpeed() * delta) {
            currentPathIndex++;
        }

        Vector2 dir = target.cpy().sub(getPos()).nor();

        setPos(getPos().add(dir.scl(getSpeed() * delta)));
    }

    @Override
    public void pop(int damage) {
        if (popTimeLeft > 0) return; // kein Schaden nehmen wenn gerade gepoppt
        level -= damage;
        popTimeLeft = POP_DURATION;
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
        value.addChild("type", new JsonValue("normal_enemy"));  // für lesbarkeit der .json datei
        value.addChild("x", new JsonValue(getX()));
        value.addChild("y", new JsonValue(getY()));
        value.addChild("currentPathIndex", new JsonValue(currentPathIndex));
        value.addChild("level", new JsonValue(level));
        value.addChild("popTimeLeft", new JsonValue(popTimeLeft));
        return value;
    }

    /// Lädt einen Gegner aus json ein.
    /// world muss bereits gesetzt sein, level ist egal
    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        setPos(new Vector2(json.getFloat("x"), json.getFloat("y")));
        this.currentPathIndex = json.getInt("currentPathIndex");
        this.level = json.getInt("level");
        this.popTimeLeft = json.getFloat("popTimeLeft");
    }

    @Override
    public void dispose() {
        // nichts, da Texturen im AssetManager verwaltet werden
    }
}
