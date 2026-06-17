package info.pengutd.game.tower;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.GameObject;
import info.pengutd.game.SpeedModifier;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.enemy.EnemyAnimator;
import info.pengutd.game.tower.projectile.Projectile;
import org.jetbrains.annotations.NotNull;

/// (Mafia Manfred) Tower der die Tower im Umkreis stärker macht
public class BeaconTower extends Tower implements SpeedModifier {
    public static final String JSON_TYPE = "beacon_tower";
    private static final float SPEED_MULTIPLIER = 1.25f;
    /// in tiles
    private static final float WIDTH = 1.3f;
    private static final float HEIGHT = 1.1f;
    private static final float RANGE = 3f;
    private final @NotNull EnemyAnimator animator;  // EnemyAnimator, weil der Tower einfach nur durch die Texturen durch cyclen muss

    public BeaconTower(@NotNull World world, @NotNull Vector2 pos, int id) {
        super(world, pos, id);
        animator = new EnemyAnimator("beacon_tower", 5, PenguTD.getInstance().getAssetManager().get(Assets.TOWER_ATLAS), 0.2f);
        getHitbox().setSize(getWidth() * 0.7f, getHeight() * 0.7f);  // box kleiner machen wegen peitsche
        getHitbox().setPosition(getHitbox().getX() - getHitbox().getWidth() * 0.2f, getHitbox().getY() - getHitbox().getHeight() * 0.2f);  // box verschieben damit sie stimmt (wegen
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return animator.getTexture();
    }

    @Override
    public void update(float delta) {
        animator.update(delta);
    }

    @Override
    public boolean affects(@NotNull GameObject obj) {
        if (obj instanceof Enemy) return false;
        float radius2 = getRange() * getRange();
        return obj.getPos().dst2(getPos()) <= radius2;
    }

    @Override
    public float getMultiplier() {
        return SPEED_MULTIPLIER;
    }

    @Override
    public int getCost() {
        return 150;
    }

    @Override
    public float getRange() {
        return RANGE * getWorld().getTileWidth();
    }

    @Override
    public float getDamage() {
        return 0;
    }

    @Override
    public float getAttackSpeed() {
        return 0;
    }

    @Override
    protected float getHandOffset() {
        return 0;
    }

    @Override
    protected @NotNull Projectile createProjectile() {
        throw new IllegalStateException("BeaconTower hat kein Projectile");
    }

    @Override
    public void setPos(@NotNull Vector2 pos) {
        super.setPos(pos);
        getHitbox().setPosition(getHitbox().getX() - getHitbox().getWidth() * 0.2f, getHitbox().getY() - getHitbox().getHeight() * 0.2f);  // immer verschieben
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
