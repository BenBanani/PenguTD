package info.pengutd.game.tower;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import org.jetbrains.annotations.NotNull;

/// Der erste einfachste Turm
public class SnowballTower extends Tower {
    public static final String JSON_TYPE = "snowball_tower";
    ///  breite in tiles
    private static final float WIDTH = .75f;
    ///  höhe in tiles
    private static final float HEIGHT = .75f;
    /// range in tiles
    private static final float RANGE = 2f;
    /// damage
    private static final int DAMAGE = 1;
    /// attack speed in seconds
    private static final float ATTACK_SPEED = 1f;
    private final @NotNull Texture texture;

    public SnowballTower(@NotNull World world, @NotNull Vector2 pos) {
        super(world, pos);
        texture = PenguTD.getInstance().getAssetManager().get(Assets.TOWER1);
    }

    @Override
    public int getCost() {
        return 200;
    }

    @Override
    public float getRange() {
        return (RANGE * getWorld().getTileWidth());
    }

    @Override
    public int getDamage() {
        return DAMAGE;
    }

    @Override
    public float getAttackSpeed() {
        return ATTACK_SPEED;
    }

    @Override
    public @NotNull TextureRegion getTexture() {
        return new TextureRegion(texture);
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
        // nichts da Texturen im AssetManager verwaltet werden
    }

    @Override
    public @NotNull JsonValue toJson() {
        JsonValue value = super.toJson();
        value.addChild("type", new JsonValue(JSON_TYPE));
        return value;
    }

    /// Türme müssen nach den Gegnern geladen werden
    @Override
    public void fromJson(@NotNull JsonValue json) {
        super.fromJson(json);
    }
}
