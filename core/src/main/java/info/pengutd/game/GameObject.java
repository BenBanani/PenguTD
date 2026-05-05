package info.pengutd.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.utils.Disposable;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.NotNull;

/// Base Klasse für alle GameObjects.
/// GameObjects sind alle Objekte mit Hitbox und Textur wie Türme und Gegner
public abstract class GameObject implements Disposable, JsonSerializable {
    @NotNull
    private final World world;

    protected GameObject(@NotNull World world) {
        this.world = world;
    }

    /// @return Textur die gerendert wird (Animationen müssen hier gehandelt werden)
    public abstract @NotNull Texture getTexture();

    /// Zeichnet das GameObject auf den Screen
    /// SpriteBatch.begin() muss davor aufgerufen werden
    /// Unterklassen sollten debug funktionen implementieren und super.draw() aufrufen
    public void draw(@NotNull SpriteBatch batch) {
        batch.draw(getTexture(), getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
    }

    /// @return hähe des Gegners in Pixel
    public abstract float getHeight();

    /// @return breite des Gegners in Pixel
    public abstract float getWidth();

    /// @return x Position der Mitte des Gegners in Pixeln von Links
    public abstract float getX();

    ///  @return y Position der Mitte des Gegners in Pixeln von unten
    public abstract float getY();

    public abstract void update(float delta);

    public @NotNull World getWorld() {
        return world;
    }

    public abstract Shape2D getHitbox();
}
