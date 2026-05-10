package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.jetbrains.annotations.NotNull;

public class EnemyAnimatorSet {
    private final @NotNull EnemyAnimator up;
    private final @NotNull EnemyAnimator down;
    private final @NotNull EnemyAnimator side;

    public EnemyAnimatorSet(@NotNull EnemyAnimator up, @NotNull EnemyAnimator down, @NotNull EnemyAnimator side) {
        this.up = up;
        this.down = down;
        this.side = side;
    }

    public @NotNull EnemyAnimator getUp() {
        return up;
    }

    public @NotNull EnemyAnimator getDown() {
        return down;
    }

    public @NotNull EnemyAnimator getSide() {
        return side;
    }

    public void update(float delta) {
        up.update(delta);
        down.update(delta);
        side.update(delta);
    }

    public @NotNull TextureRegion getTexture(Enemy.Direction direction) {
        switch (direction) {
            case UP: return up.getTexture();
            case DOWN: return down.getTexture();
            case LEFT:
            case RIGHT:
                return side.getTexture();
        }
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }

    public void setFrameDuration(float duration) {
        up.setFrameDuration(duration);
        down.setFrameDuration(duration);
        side.setFrameDuration(duration);
    }
}
