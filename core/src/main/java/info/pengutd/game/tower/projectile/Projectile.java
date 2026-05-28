package info.pengutd.game.tower.projectile;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.game.GameObject;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.Tower;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// Base Klasse für alle Projektile
public abstract class Projectile extends GameObject {
    private int damage;
    private final @NotNull Vector2 direction;
    private @NotNull Tower tower;
    private boolean alive = true;

    protected Projectile(@NotNull World world, @NotNull Vector2 pos, @NotNull Vector<Vector2> direction, @NotNull Tower tower, int damage) {
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

    /// @return der enemy mit dem das projectile kollidiert oder null, wenn keiner diesen frame getroffen wurde
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

    /// wird aufgerufen, wenn der projectile ein enemy trifft
    /// @param enemy das getroffen wurde
    public void onHit(@NotNull Enemy enemy) {
        tower.onProjectileHit(this, enemy);
    }

    public @NotNull Tower getTower() {
        return tower;
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue json = super.toJson();
        json.addChild("damage", new JsonValue(damage));
        json.addChild("direction_x", new JsonValue(direction.x));
        json.addChild("direction_y", new JsonValue(direction.y));
        json.addChild("tower", new JsonValue(tower.getId()));
        return json;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        damage = json.getInt("damage");
        direction.set(json.getFloat("direction_x"), json.getFloat("direction_y"));

        Tower tower = getWorld().getTowerFromId(json.getInt("tower"));
        if (tower == null) throw new IllegalStateException("Parent tower vom Projectile ist null!");
        this.tower = tower;
    }
}
