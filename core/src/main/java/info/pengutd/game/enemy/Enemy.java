package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import info.pengutd.game.World;

///  Base Klasse für alle Gegner
public abstract class Enemy implements Disposable {
    private final World world;

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
    }

    /// @return hähe des Gegners in Pixel
    public abstract float getHeight();

    /// @return breite des Gegners in Pixel
    public abstract float getWidth();

    /// @return x Position des Gegners in Pixeln von Links
    public abstract float getX();

    ///  @return y Position des Gegners in Pixeln von unten
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

    public abstract void die();
}
