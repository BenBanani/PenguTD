package info.pengutd.game.tower.projectile;

import com.badlogic.gdx.math.Vector2;
import info.pengutd.game.GameObject;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import org.jetbrains.annotations.NotNull;

public abstract class Projectile extends GameObject {
    private final int damage;
    private final @NotNull Vector2 direction;
    private boolean alive = true;

    protected Projectile(@NotNull World world, @NotNull Vector2 pos, @NotNull Vector2 direction , int damage) {
        super(world, pos);
        this.damage = damage;
        this.direction = direction;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void update(float delta) {
        if (getPos().x < 0 || getPos().y < 0 ||
            getPos().x > getWorld().getTileWidth() * getWorld().getMapWidth() ||
            getPos().y > getWorld().getTileHeight() * getWorld().getMapHeight()
        ) {
            destroy();
            return;
        }
        setPos(getPos().add(direction.nor().scl(getSpeed() * delta)));

        checkCollision();
    }

    private void checkCollision() {
    }

    private void destroy() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    /// @return geschwindigkeit des projectiles in pixeln pro sekunde
    public abstract float getSpeed();

    /// wird aufgerufen wenn der projectile ein enemy trifft
    ///
    /// @param enemy das getroffen wurde
    public void onHit(Enemy enemy) {
        destroy();
    }
}
