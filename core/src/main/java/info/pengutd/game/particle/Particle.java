package info.pengutd.game.particle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import info.pengutd.game.World;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

/// Basisklasse für ein Partikelsystem
public abstract class Particle implements Disposable {  // nicht json serializable, muss nicht gespeichert werden
    private final @NotNull Vector2 pos;
    private final @NotNull World world;
    private float lifetime;  // übrige lifetime in sekunden

    public Particle(@NotNull Vector2 pos, @NotNull World world, float lifetime) {
        this.pos = pos;
        this.world = world;
        this.lifetime = lifetime;
    }

    public @NotNull World getWorld() {
        return world;
    }

    public @NotNull Vector2 getPos() {
        return pos;
    }

    public float getLifetime() {
        return lifetime;
    }

    @MustBeInvokedByOverriders
    public void update(float delta) {
        lifetime -= delta;
    }

    public boolean isAlive() {
        return lifetime > 0;
    }

    public abstract void render(@NotNull SpriteBatch batch);
}
