package info.pengutd.game.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.game.GameObject;
import info.pengutd.game.World;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

///  Base Klasse für alle Gegner
public abstract class Enemy extends GameObject implements Disposable, JsonSerializable {
    private boolean debug = false;
    private int id;
    private final @NotNull Array<Vector2> path = new Array<>();
    ///  Index des aktuellen Zielpunkts im Pfad
    int currentPathIndex = 0;
    private float popTimeLeft = 0f;

    protected Enemy(@NotNull World world, Vector2 pos, int id) {
        super(world, pos);
        findPath();
        this.id = id;
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
            Gdx.app.error("WarriorEnemy", "No path layer found in map");
        }
    }

    public abstract int getHealth();

    ///  @return speed in pixel per second
    public abstract float getSpeed();

    ///  @return Path den das Enemy gehen muss
    public @Nullable Array<Vector2> getPath() {
        return path;
    }

    public float getPopTimeLeft() {
        return popTimeLeft;
    }

    ///  Zeichnet das Enemy auf den Screen
    /// SpriteBatch.begin() muss davor aufgerufen werden
    @Override
    public void draw(@NotNull SpriteBatch batch) {
        super.draw(batch);
        if (debug) {
            batch.end(); // wir zeichnen mit dem internen batch von ShapeRenderer

            ShapeRenderer renderer = new ShapeRenderer();
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            renderer.begin(ShapeRenderer.ShapeType.Line);
            // Hitbox
            renderer.setColor(Color.RED);
            Rectangle box = getHitbox();
            renderer.rect(box.x, box.y, box.width, box.height);
            // Path
            renderer.setColor(Color.GREEN);
            if (this.getPath() != null) {
                this.getPath().forEach(vec -> renderer.circle(vec.x, vec.y, 5));
            }

            renderer.end();

            batch.begin();
        }
    }

    public void setPopTimeLeft(float popTimeLeft) {
        this.popTimeLeft = popTimeLeft;
    }

    @Override
    /// Logik update des Enemies
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

    /// nehme schade in höhe von damage
    public abstract void pop(int damage);

    public abstract void die();

    public boolean isAlive() {
        return getHealth() > 0 || popTimeLeft > 0;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("id", new JsonValue(id));
        value.addChild("currentPathIndex", new JsonValue(currentPathIndex));
        value.addChild("popTimeLeft", new JsonValue(popTimeLeft));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        id = json.getInt("id");
        this.currentPathIndex = json.getInt("currentPathIndex");
        this.popTimeLeft = json.getFloat("popTimeLeft");
    }

    /// Schaltet den Debug Modus an
    /// Jetzt werden zusätzlich die Hitbox und der Path gezeichnet
    public Enemy debug() {
        debug = true;
        return this;
    }
}
