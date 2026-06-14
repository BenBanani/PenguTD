package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import info.pengutd.game.particle.DamageTextParticle;
import org.jetbrains.annotations.NotNull;

/// Kevin Pascal mit seinen coolen Jordans
/// Kann zufällig nach vorne dashen
public class CoolEnemy extends Enemy {
    public static final float HEIGHT = 0.75f;
    public static final float WIDTH = 0.75f;
    public static final float SPEED = 2f;
    public static final String JSON_TYPE = "cool_enemy";
    public static final float FRAME_DURATION = (1f /SPEED) / 4f;
    public static final float DASH_CHANCE = 0.5f;  // dash chance per second
    public static final float POP_DURATION = 0.1f;
    public static final float DASH_DURATION = 0.3f;
    public static final int DASH_SPEED_MULTIPLIER = 4;
    private final @NotNull EnemyAnimatorSet animator;
    private float dashTimeLeft = 0f;
    private float health = 1;

    public CoolEnemy(@NotNull World world, int id) {
        super(world, new Vector2(), id);
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.ENEMY_ATLAS);
        animator = new EnemyAnimatorSet(
            new EnemyAnimator("cool_up", 4, atlas, FRAME_DURATION),
            new EnemyAnimator("cool_down", 4, atlas, FRAME_DURATION),
            new EnemyAnimator("cool_side", 4, atlas, FRAME_DURATION)
        );
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public float getSpeed() {
        if (dashTimeLeft > 0) {
            return DASH_SPEED_MULTIPLIER * SPEED * getWorld().getTileWidth();
        }
        return SPEED * getWorld().getTileWidth();
    }

    @Override
    protected void setHealth(float value) {
        health = value;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (health <= 0) return;
        animator.update(delta);
        if (MathUtils.random() < 1f - Math.exp(-DASH_CHANCE * delta)) {  // exp damit der wert nicht über 1 geht, dafür exponentielle annäherung
            dash();
        }

        if (dashTimeLeft > 0) {
            dashTimeLeft -= delta;
        }
    }

    private void dash() {
        if (getPath().size == 0) return;
        //int targetIndex = Math.min(currentPathIndex + 1, getPath().size - 1);

        dashTimeLeft = DASH_DURATION;
    }

    @Override
    public void pop(float damage) {
        if (getPopTimeLeft() > 0 || damage <= 0) return;
        health -= damage;
        getWorld().addParticle(new DamageTextParticle(getPos().add(0, getHeight() / 2f), getWorld(), damage));
        setPopTimeLeft(POP_DURATION);
        if (health <= 0) die();
    }

    @Override
    public void die() {
        super.die();
        health = 0;
        getWorld().addMoney(5);
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return animator.getTexture(getDirection());
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
    protected boolean flipX() {
        return getDirection() == Direction.LEFT;
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("dash_time_left", new JsonValue(dashTimeLeft));
        return value;
    }

    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        dashTimeLeft = json.getFloat("dash_time_left");
    }

    @Override
    public void dispose() {

    }
}
