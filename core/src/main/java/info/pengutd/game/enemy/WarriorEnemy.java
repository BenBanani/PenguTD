package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import org.jetbrains.annotations.NotNull;

/// Standard Gegner mit mehreren Stufen.
/// wenn ein Gegner getroffen wird, wird er zu einem Gegner mit geringerer Stufe
public class WarriorEnemy extends Enemy {
    ///  Höhe in tiles
    public static final float HEIGHT = 0.75f;
    ///  breite in tiles
    public static final float WIDTH = 0.65f;
    public static final String JSON_TYPE = "warrior_enemy";
    public static final float SPEED_TO_ANIMATION_TIME = 500f;
    ///  speed in tiles
    private static final float SPEED = 0.75f;
    private static final float POP_DURATION = 0.1f;
    private final @NotNull TextureRegion popTexture;
    private final @NotNull EnemyAnimator animatorSide;
    private final @NotNull EnemyAnimator animatorUp;
    private final @NotNull EnemyAnimator animatorDown;
    private int level;

    public WarriorEnemy(int level, @NotNull World world, int id) {
        super(world, new Vector2(), id); // placeholder position
        this.level = level;
        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.WARRIOR_ENEMY_ATLAS);
        animatorSide = new EnemyAnimator("warrior_side", 4, atlas, getSpeed() / SPEED_TO_ANIMATION_TIME);
        animatorUp = new EnemyAnimator("warrior_up", 4, atlas, getSpeed() / SPEED_TO_ANIMATION_TIME);
        animatorDown = new EnemyAnimator("warrior_down", 4, atlas, getSpeed() / SPEED_TO_ANIMATION_TIME);
        popTexture = atlas.findRegion("pop");
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return getPopTimeLeft() > 0 ? popTexture : findAnimator().getTexture();
    }

    private @NotNull EnemyAnimator findAnimator() {
        switch (getDirection()) {
            case UP: return animatorUp;
            case DOWN: return animatorDown;
            default: return animatorSide;
        }
    }

    @Override
    protected boolean flipX() {
        return getDirection() == Direction.LEFT;
    }

    @Override
    public int getHealth() {
        return level;
    }

    @Override
    public float getSpeed() {
        return SPEED * level * getWorld().getTileWidth();
    }

    @Override
    public float getHeight() {
        return HEIGHT * getWorld().getTileHeight();
    }

    @Override
    public float getWidth() {
        if (getPopTimeLeft() <= 0) {
            return WIDTH * getWorld().getTileWidth();
        }
        return getHeight(); // pop texture ist quadratisch
    }

    @Override
    public void pop(int damage) {
        if (getPopTimeLeft() > 0) return; // kein Schaden nehmen wenn gerade gepoppt
        level -= damage;
        setPopTimeLeft(POP_DURATION);
        if (level <= 0) die();
        animatorSide.setFrameDuration(getSpeed() / SPEED_TO_ANIMATION_TIME);
        animatorUp.setFrameDuration(getSpeed() / SPEED_TO_ANIMATION_TIME);
        animatorDown.setFrameDuration(getSpeed() / SPEED_TO_ANIMATION_TIME);
        // todo Geld geben + stats erhöhen
    }

    @Override
    public void die() {
        // todo Geld geben + stats erhöhen
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        animatorSide.update(delta);
        animatorUp.update(delta);
        animatorDown.update(delta);
    }

    /// Zum Speichern des Gegners als .json datei
    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("type", new JsonValue(JSON_TYPE));
        value.addChild("level", new JsonValue(level));
        return value;
    }

    /// Lädt einen Gegner aus json ein.
    /// world muss bereits gesetzt sein, level ist egal
    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
        this.level = json.getInt("level");

    }

    @Override
    public void dispose() {
        // nichts, da Texturen im AssetManager verwaltet werden
    }
}
