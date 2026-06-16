package info.pengutd.game.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import org.jetbrains.annotations.NotNull;

public class DamageTextParticle extends Particle {
    public static final float START_LIFETIME = 0.5f;
    private final float damage;
    private final @NotNull Color color;
    private final float yVelocity = 1.5f;
    private final @NotNull String text;
    private final BitmapFont font;

    public DamageTextParticle(@NotNull Vector2 pos, @NotNull World world, float damage) {
        super(pos, world, START_LIFETIME);
        this.damage = damage;

        Skin skin = PenguTD.getInstance().getAssetManager().get(Assets.DEFAULT_SKIN);
        font = new BitmapFont(Gdx.files.internal("default.fnt"));

        float intensity = Math.min(damage / 3f, 1f);
        font.getData().scale(intensity / 2f);
        color = new Color(1f, 1f - intensity, 1f - intensity, 1f);
        text = "" + round(damage);
    }

    /// rundet den float auf 2 Nachkommastellen
    private static float round(float damage) {
        float rnd = Math.round(damage * 100);
        return rnd / 100;
    }

    /// logik update
    @Override
    public void update(float delta) {
        super.update(delta);

        getPos().y += yVelocity * getWorld().getTileWidth() * delta;
    }

    /// zeichnet das particle mit dem batch
    /// pos ist die mitte
    @Override
    public void draw(@NotNull SpriteBatch batch) {
        float progress = getLifetime() / START_LIFETIME;
        float alpha = progress * progress * 0.7f;
        font.setColor(color.r, color.g, color.b, alpha);

        GlyphLayout layout = new GlyphLayout(font, text);
        float x = getPos().x - layout.width / 2f;
        float y = getPos().y + layout.height / 2f;
        font.draw(batch, layout, x, y);

        font.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
