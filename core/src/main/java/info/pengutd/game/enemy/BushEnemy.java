package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.particle.DamageTextParticle;
import org.jetbrains.annotations.NotNull;

/// Pinguin der sich im Busch versteckt. Er kann zum Beispiel nur vom SniperPinguin gesehen werden,
///  oder durch Flächenschaden getötet werden
/// todo mayby hit cooldown?
public class BushEnemy extends Enemy {
    /// in tiles
    public static final float HEIGHT = 0.75f;
    public static final float WIDTH = 0.65f;
    public static final String JSON_TYPE = "bush_enemy";
    private static final float SPEED = 0.75f;
    public static final float FRAME_DURATION = (1f / 4 * SPEED);
    private static final float POP_DURATION = 0.8f;
    private static final float VISIBLE_SPEED_MULTIPLIER = 1.5f;
    private final @NotNull Array<EnemyAnimatorSet> animators = new Array<>(3);
    private boolean visible = false;
    private float health = 2;


    public BushEnemy(@NotNull World world, int id) {
        super(world, new Vector2(), id);
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.ENEMY_ATLAS);
        animators.add(
            new EnemyAnimatorSet(
                new EnemyAnimator("bush_invis_up", 4, atlas, FRAME_DURATION),
                new EnemyAnimator("bush_invis_down", 4, atlas, FRAME_DURATION),
                new EnemyAnimator("bush_invis_side", 4, atlas, FRAME_DURATION)),
            new EnemyAnimatorSet(
                new EnemyAnimator("bush_visible_up", 4, atlas, FRAME_DURATION / VISIBLE_SPEED_MULTIPLIER),
                new EnemyAnimator("bush_visible_down", 4, atlas, FRAME_DURATION / VISIBLE_SPEED_MULTIPLIER),
                new EnemyAnimator("bush_visible_side", 4, atlas, FRAME_DURATION / VISIBLE_SPEED_MULTIPLIER)),
            new EnemyAnimatorSet(
                new EnemyAnimator("bush_transform_up", 4, atlas, POP_DURATION / 4f),
                new EnemyAnimator("bush_transform_side", 4, atlas, POP_DURATION / 4f),
                new EnemyAnimator("bush_transform_down", 4, atlas, POP_DURATION / 4f)
            )
        );
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    protected void setHealth(float value) {
        if (value > 2) return;
        health = value;
        visible = (value <= 1);
    }

    @Override
    public float getSpeed() {
        return visible ? SPEED * VISIBLE_SPEED_MULTIPLIER * getWorld().getTileWidth() : SPEED * getWorld().getTileWidth();
    }

    @Override
    public void pop(float damage) {
        if (getPopTimeLeft() > 0 || damage <= 0) return;
        health -= Math.min(damage, 1);
        getWorld().addParticle(new DamageTextParticle(getPos().add(0, getHeight() / 2f), getWorld(), Math.min(damage, 1)));
         if (health <= 1 && !visible) {
             setPopTimeLeft(POP_DURATION); // nur dann transform
             visible = true;
         }
        if (health <= 0) {
            die();
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")  // BushEnemy stirbt nicht wirklich, daher keine stats erhöhen
    @Override
    public void die() {
        health = 0;
        WarriorEnemy newEnemy = new WarriorEnemy(1, getWorld(), getWorld().createEntityId());
        newEnemy.setPos(getPos());
        newEnemy.currentPathIndex = currentPathIndex;
        newEnemy.setDirection(getDirection());
        getWorld().addEnemy(newEnemy);
    }

    @Override
    public void update(float delta) {
        if (getPopTimeLeft() > 0) {  // stehen bleiben, wenn gepoppt
            setPopTimeLeft(getPopTimeLeft() - delta);
        } else {
            super.update(delta);
        }
        // animators updaten
        if (getPopTimeLeft() > 0) {
            animators.get(2).update(delta);
        } else if (visible) {
            animators.get(1).update(delta);
        } else {
            animators.get(0).update(delta);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    protected boolean flipX() {
        return getDirection() == Direction.LEFT;
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        if (getPopTimeLeft() > 0) return animators.get(2).getTexture(getDirection());
        if (visible) return animators.get(1).getTexture(getDirection());
        return animators.get(0).getTexture(getDirection());
    }

    @Override
    public @NotNull String getType() {
        return JSON_TYPE;
    }

    @Override
    public float getHeight() {
        return HEIGHT * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        return WIDTH * getWorld().getTileWidth();
    }

    @Override
    public void dispose() {

    }
}
