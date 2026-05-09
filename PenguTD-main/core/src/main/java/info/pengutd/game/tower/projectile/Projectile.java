package info.pengutd.game.tower.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import info.pengutd.game.GameObject;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.Tower;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Projectile extends GameObject {
    private final int damage;
    private final @NotNull Vector2 direction;
    private final @NotNull Tower tower;
    private boolean alive = true;

    protected Projectile(@NotNull World world, @NotNull Vector2 pos, @NotNull Vector2 direction, @NotNull Tower tower, int damage) {
        super(world, pos);
        this.damage = damage;
        this.direction = direction.cpy().nor();
        this.tower = tower;
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
        setPos(getPos().mulAdd(direction, getSpeed() * delta));

        Enemy collisionEnemy = checkCollision();
        if (collisionEnemy != null) {
            onHit(collisionEnemy);
        }
    }

    /// @return der enemy das getroffen wurde oder null, wenn keiner getroffen wurde
    private @Nullable Enemy checkCollision() {
        Array<Enemy> enemies = getWorld().getEnemies();
        for (int i = 0; i < enemies.size; i++) {
            Enemy enemy = enemies.get(i);
            if (getHitbox().overlaps(enemy.getHitbox())) {
                if (!enemy.isAlive()) continue;
                return enemy;
            }
        }
        return null;
    }

    protected void destroy() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    /// @return geschwindigkeit des projectiles in pixeln pro sekunde
    public abstract float getSpeed();

    /// wird aufgerufen wenn der projectile ein enemy trifft
    /// @param enemy das getroffen wurde
    public void onHit(@NotNull Enemy enemy) {
        tower.onProjectileHit(this, enemy);
    }

    public @NotNull Tower getTower() {
        return tower;
    }
}
