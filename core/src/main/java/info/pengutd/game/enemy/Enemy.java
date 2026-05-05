package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import info.pengutd.game.GameObject;
import info.pengutd.game.World;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

///  Base Klasse für alle Gegner
public abstract class Enemy extends GameObject implements Disposable, JsonSerializable {
    private boolean debug = false;

    protected Enemy(@NotNull World world) {
        super(world);
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
            Rectangle box = (Rectangle) getHitbox();
            renderer.rect(box.x, box.y, box.width, box.height);
            // Path
            renderer.setColor(Color.GREEN);
            if (this.getPath() != null) {
                for (Vector2 vec : this.getPath()) {
                    renderer.circle(vec.x, vec.y, 5);
                }
            }

            renderer.end();

            batch.begin();
        }
    }

    @Override
    /// Logik update des Enemies
    public abstract void update(float delta);

    /// todo muss alle texturen disposen, nicht nur eine
    @Override
    public void dispose() {
        getTexture().dispose();
    }

    /// nehme schade in höhe von damage
    public abstract void pop(int damage);

    public abstract void die();

    /// Schaltet den Debug Modus an
    /// Jetzt werden zusätzlich die Hitbox und der Path gezeichnet
    public Enemy debug() {
        debug = true;
        return this;
    }
}
