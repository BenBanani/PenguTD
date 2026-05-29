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
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.PenguTD;
import info.pengutd.game.GameObject;
import info.pengutd.game.World;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

///  Base Klasse für alle Gegner
public abstract class Enemy extends GameObject {
    private final @NotNull Array<Vector2> path = new Array<>();
    ///  Index des aktuellen Zielpunkts im Pfad
    int currentPathIndex = 0;
    private boolean debug = false;
    private int id;
    private float popTimeLeft = 0f;
    private @NotNull Direction direction = Direction.DOWN;

    protected Enemy(@NotNull World world, @NotNull Vector2 pos, int id) {
        super(world, pos);
        findPath();
        this.id = id;
    }

    /// Setzt den Pfad für den Enemy aus dem MapLayer "path"
    /// die wegpunkte sind für jedes Enemy leicht zufällig versetzt
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

    /// @return speed in pixel per second
    public abstract float getSpeed();

    /// @return Path den das Enemy gehen muss
    public @NotNull Array<Vector2> getPath() {
        return path;
    }

    /// Hit cooldown
    public float getPopTimeLeft() {
        return popTimeLeft;
    }

    public void setPopTimeLeft(float popTimeLeft) {
        this.popTimeLeft = popTimeLeft;
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
            path.forEach(vec -> renderer.circle(vec.x, vec.y, 5));

            renderer.end();

            batch.begin();
        }
    }

    /// Logik update des Enemies
    @Override
    public void update(float delta) {
        if (popTimeLeft > 0) {
            popTimeLeft -= delta;
            return;
        }

        if (path.size == 0) return;
        if (currentPathIndex >= path.size - 1) {
            getWorld().damageHp(getHealth());
            getWorld().getEnemies().removeValue(this, true);
            setHealth(0);
            dispose();
            return;
        }
        Vector2 target = path.get(currentPathIndex).lerp(path.get(currentPathIndex + 1), 0.02f); // lerp damit der weg nicht so eckig ist

        if (getPos().dst2(target) < getSpeed() * delta * getSpeed() * delta) {
            currentPathIndex++;
        }

        Vector2 dir = target.cpy().sub(getPos()).nor();

        updateDirection(dir);

        setPos(getPos().add(dir.scl(getSpeed() * delta)));
    }

    protected abstract void setHealth(int value);

    /// Setzt die richtung auf die, in die
    /// @param dir zeigt
    private void updateDirection(@NotNull Vector2 dir) {
        if (Math.abs(dir.x) > Math.abs(dir.y)) {
            if (dir.x > 0) {
                direction = Direction.RIGHT;
            } else {
                direction = Direction.LEFT;
            }
        } else {
            if (dir.y > 0) {
                direction = Direction.UP;
            } else {
                direction = Direction.DOWN;
            }
        }
    }

    /// nehme schade in höhe von damage
    public abstract void pop(int damage);

    @MustBeInvokedByOverriders
    public void die() {
        PenguTD.getInstance().getStatsManager().addKill();
    }

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
        value.addChild("health", new JsonValue(getHealth()));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        id = json.getInt("id");
        currentPathIndex = json.getInt("currentPathIndex");
        popTimeLeft = json.getFloat("popTimeLeft");
        setHealth(json.getInt("health"));
    }

    /// Schaltet den Debug Modus an
    /// Jetzt werden zusätzlich die Hitbox und der Path gezeichnet
    public @NotNull Enemy debug() {
        debug = true;
        return this;
    }

    /// @return aktuelle Richtung in die der gegner läuft
    /// @see Direction
    public @NotNull Direction getDirection() {
        return direction;
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
