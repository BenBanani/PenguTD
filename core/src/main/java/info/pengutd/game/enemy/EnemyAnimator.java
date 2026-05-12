package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import info.pengutd.Assets;
import org.jetbrains.annotations.NotNull;

/// EnemyAnimator kümmert sich um die Animation von Gegnern.
/// pro frame einmal update aufrufen und dann mit getTexture() die jeweilige Texture für den frame abfragen
public class EnemyAnimator {
    private final @NotNull Array<TextureRegion> textures;
    private int index;
    private float age;
    private float frameDuration;

    /// @param textures Liste der Textures die für die Animation verwendet werden
    /// @param frameDuration Dauer in Sekunden pro Frame
    public EnemyAnimator(@NotNull Array<TextureRegion> textures, float frameDuration) {
        this.textures = textures;
        this.frameDuration = frameDuration;
    }

    /// @param textureName Name der animationstexturen im atlas
    /// die texturen sollten die namen textureName + index haben
    /// Beispiel warrior_side0, warrior_side1, warrior_side2, warrior_side3, usw.
    /// @param amount Anzahl der Texturen
    /// @param atlas TextureAtlas
    /// @param frameDuration Dauer in Sekunden pro Frame
    public EnemyAnimator(@NotNull String textureName, int amount, @NotNull TextureAtlas atlas, float frameDuration) {
        textures = new Array<>(amount);
        for (int i = 0; i < amount; i++) {
            textures.add(Assets.findRegionOrMissing(atlas, textureName + i));
        }
        this.frameDuration = frameDuration;
    }

    /// @return aktuelle Texture für den Frame
    public @NotNull TextureRegion getTexture() {
        return textures.get(index);
    }

    /// Sollte jeden Frame aufgerufen werden
    /// @param delta Zeit in Sekunden seit letzter update
    public void update(float delta) {
        age += delta;
        if (age >= frameDuration) {
            age -= frameDuration;
            index = (index + 1) % textures.size;
        }
    }

    public void setFrameDuration(float v) {
        frameDuration = v;
    }
}
