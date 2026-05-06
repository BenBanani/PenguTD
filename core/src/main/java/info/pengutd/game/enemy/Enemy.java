package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    protected Enemy(@NotNull World world, int id) {
        super(world);
        this.id = id;
    }

    public abstract int getHealth();

    ///  @return speed in pixel per second
    public abstract float getSpeed();

    ///  @return Path den das Enemy gehen muss
    public abstract @Nullable Array<Vector2> getPath();

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

    @Override
    /// Logik update des Enemies
    public abstract void update(float delta);

    /// nehme schade in höhe von damage
    public abstract void pop(int damage);

    public abstract void die();

    public boolean isAlive() {
        return getHealth() > 0;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("id", new JsonValue(id));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        id = json.getInt("id");
    }

    /// Schaltet den Debug Modus an
    /// Jetzt werden zusätzlich die Hitbox und der Path gezeichnet
    public Enemy debug() {
        debug = true;
        return this;
    }
}
