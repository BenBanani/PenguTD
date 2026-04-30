package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import info.pengutd.game.World;

///  Base Klasse für alle Gegner
public abstract class Enemy implements Disposable {
    private final World world;
    private boolean debug = false;

    protected Enemy(World world) {
        this.world = world;
    }

    /// @return Textur die gerendert wird (Animationen müssen hier gehandelt werden)
    public abstract Texture getTexture();

    public abstract int getHealth();

    ///  @return speed in pixel per second
    public abstract float getSpeed();

    ///  @return Path den das Enemy gehen muss
    public abstract Array<Vector2> getPath();

    ///  Zeichnet das Enemy auf den Screen
    /// SpriteBatch.begin() muss davor aufgerufen werden
    public void draw(SpriteBatch batch) {
        batch.draw(getTexture(), getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
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
            for (Vector2 vec : this.getPath()) {
                renderer.circle(vec.x, vec.y, 5);
            }

            renderer.end();

            batch.begin();
        }
    }

    /// @return hähe des Gegners in Pixel
    public abstract float getHeight();

    /// @return breite des Gegners in Pixel
    public abstract float getWidth();

    /// @return x Position der Mitte des Gegners in Pixeln von Links
    public abstract float getX();

    ///  @return y Position der Mitte des Gegners in Pixeln von unten
    public abstract float getY();

    /// Logik update des Enemies
    public abstract void move(float delta);

    /// todo muss alle texturen disposen, nicht nur eine
    @Override
    public void dispose() {
        getTexture().dispose();
    }

    public World getWorld() {
        return world;
    }

    /// nehme schade in höhe von damage
    public abstract void pop(int damage);

    public abstract Shape2D getHitbox();

    public abstract void die();

    /// Schaltet den Debug Modus an
    /// Jetzt werden zusätzlich die Hitbox und der Path gezeichnet
    public Enemy debug() {
        debug = true;
        return this;
    }
}
