package info.pengutd.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.save.JsonSerializable;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

/// Base Klasse für alle GameObjects.
/// GameObjects sind alle Objekte mit Hitbox und Textur wie Türme und Gegner
public abstract class GameObject implements Disposable, JsonSerializable {
    private final @NotNull World world;
    private final @NotNull Vector2 pos;
    private final @NotNull Rectangle hitbox;
    private float rotationDeg = 0;

    protected GameObject(@NotNull World world, @NotNull Vector2 pos) {
        this.world = world;
        this.pos = pos;
        hitbox = new Rectangle(0, 0, getWidth(), getHeight());
        hitbox.setCenter(pos);
    }

    /// @return Textur die gerendert wird (Animationen müssen hier gehandelt werden)
    public abstract @NotNull TextureRegion getTexture();

    /// @return Typ des GameObjects für JSON Serialisierung und Ressourcen
    public abstract @NotNull String getType();

    /// Zeichnet das GameObject auf den Screen
    /// SpriteBatch.begin() muss davor aufgerufen werden
    /// Unterklassen sollten debug funktionen implementieren und super.draw() aufrufen
    public void draw(@NotNull SpriteBatch batch) {
        batch.draw(getTexture(), getX() - getWidth() / 2, getY() - getHeight() / 2,
            getWidth() / 2f, getHeight() / 2f,
            getWidth(), getHeight(), flipX() ? -1 : 1, flipY() ? -1 : 1, rotationDeg);
    }

    /// Soll die Textur auf der x-Achse gespiegelt sein
    protected boolean flipX() {
        return false;
    }

    /// Soll die Texture auf der y-Achse gespiegelt sein
    protected boolean flipY() {
        return false;
    }

    /// @return höhe des Gegners in Pixel
    public abstract float getHeight();

    /// @return breite des Gegners in Pixel
    public abstract float getWidth();

    /// @return x Position der Mitte des Gegners in Pixeln von Links
    public float getX() {
        return pos.x;
    }

    /// @return y Position der Mitte des Gegners in Pixeln von unten
    public float getY() {
        return pos.y;
    }

    /// logik update
    /// @param delta Zeit seit dem letzten update() in Sekunden
    public abstract void update(float delta);

    public @NotNull World getWorld() {
        return world;
    }

    /// @return Kopie der Position der Mitte des GameObjects
    public @NotNull Vector2 getPos() {
        return new Vector2(getX(), getY());
    }

    public void setPos(@NotNull Vector2 pos) {
        this.pos.set(pos);
        hitbox.setCenter(pos);
    }

    public @NotNull Rectangle getHitbox() {
        return hitbox;
    }

    @Override
    @MustBeInvokedByOverriders
    public @NotNull JsonValue toJson() {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        value.addChild("type", new JsonValue(getType()));
        value.addChild("x", new JsonValue(getX()));
        value.addChild("y", new JsonValue(getY()));
        value.addChild("rotation", new JsonValue(rotationDeg));
        return value;
    }

    @Override
    @MustBeInvokedByOverriders
    public void fromJson(@NotNull JsonValue json) {
        setPos(new Vector2(json.getFloat("x"), json.getFloat("y")));
        rotationDeg = json.getFloat("rotation");
    }

    public float getRotationDeg() {
        return rotationDeg;
    }

    public void setRotationDeg(float rotationDeg) {
        this.rotationDeg = rotationDeg;
    }
}
