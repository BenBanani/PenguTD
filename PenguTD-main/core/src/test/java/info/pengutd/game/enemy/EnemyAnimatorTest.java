package info.pengutd.game.enemy;

import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Unit tests for EnemyAnimator.
 *
 * TextureRegion is mocked so no OpenGL context or atlas file is needed.
 * Every test constructs its own animator with only the frames it actually accesses,
 * so no stub is ever declared without being used.
 */
@ExtendWith(MockitoExtension.class)
class EnemyAnimatorTest {

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /** Builds an Array of {@code count} distinct TextureRegion mocks. */
    private Array<TextureRegion> frames(int count) {
        Array<TextureRegion> arr = new Array<>(count);
        for (int i = 0; i < count; i++) {
            arr.add(mock(TextureRegion.class));
        }
        return arr;
    }

    // =========================================================================
    // Initial state
    // =========================================================================

    @Test
    void getTexture_returnsFirstFrameBeforeAnyUpdate() {
        Array<TextureRegion> textures = frames(3);
        EnemyAnimator animator = new EnemyAnimator(textures, 0.2f);

        assertSame(textures.get(0), animator.getTexture());
    }

    // =========================================================================
    // update — frame not yet due
    // =========================================================================

    @Test
    void update_doesNotAdvanceFrameWhenDeltaBelowDuration() {
        Array<TextureRegion> textures = frames(2);
        EnemyAnimator animator = new EnemyAnimator(textures, 0.2f);

        animator.update(0.1f); // less than one frame duration

        assertSame(textures.get(0), animator.getTexture());
    }

    // =========================================================================
    // update — exact frame boundary
    // =========================================================================

    @Test
    void update_advancesToNextFrameWhenDeltaEqualsFrameDuration() {
        Array<TextureRegion> textures = frames(2);
        EnemyAnimator animator = new EnemyAnimator(textures, 0.2f);

        animator.update(0.2f);

        assertSame(textures.get(1), animator.getTexture());
    }

    // =========================================================================
    // update — accumulation across multiple calls
    // =========================================================================

    @Test
    void update_accumulatesDeltaAcrossMultipleCalls() {
        Array<TextureRegion> textures = frames(2);
        EnemyAnimator animator = new EnemyAnimator(textures, 0.2f);

        animator.update(0.1f);
        animator.update(0.1f); // total age == frameDuration → advance

        assertSame(textures.get(1), animator.getTexture());
    }

    @Test
    void update_remainderCarriesOverToNextFrame() {
        Array<TextureRegion> textures = frames(3);
        EnemyAnimator animator = new EnemyAnimator(textures, 0.2f);

        // First update advances to frame 1 and leaves 0.1 s of leftover age.
        animator.update(0.3f);
        // Second update adds 0.1 s: total carried age == 0.2 s → advance to frame 2.
        animator.update(0.1f);

        assertSame(textures.get(2), animator.getTexture());
    }

    // =========================================================================
    // update — wrap-around
    // =========================================================================

    @Test
    void update_wrapsAroundToFirstFrameAfterLastFrame() {
        Array<TextureRegion> textures = frames(2);
        EnemyAnimator animator = new EnemyAnimator(textures, 0.2f);

        animator.update(0.2f); // frame 0 → frame 1
        animator.update(0.2f); // frame 1 → frame 0

        assertSame(textures.get(0), animator.getTexture());
    }

    @Test
    void update_cyclesThroughAllFramesAndWraps() {
        Array<TextureRegion> textures = frames(3);
        EnemyAnimator animator = new EnemyAnimator(textures, 0.1f);

        animator.update(0.1f);
        assertSame(textures.get(1), animator.getTexture());

        animator.update(0.1f);
        assertSame(textures.get(2), animator.getTexture());

        animator.update(0.1f);
        assertSame(textures.get(0), animator.getTexture());
    }

    // =========================================================================
    // setFrameDuration
    // =========================================================================

    @Test
    void setFrameDuration_slowerDurationPreventsAdvanceAtOldBoundary() {
        Array<TextureRegion> textures = frames(2);
        EnemyAnimator animator = new EnemyAnimator(textures, 0.2f);

        animator.setFrameDuration(0.5f);
        animator.update(0.2f); // would have advanced at old duration, must not now

        assertSame(textures.get(0), animator.getTexture());
    }

    @Test
    void setFrameDuration_fasterDurationAdvancesEarlier() {
        Array<TextureRegion> textures = frames(2);
        EnemyAnimator animator = new EnemyAnimator(textures, 0.5f);

        animator.setFrameDuration(0.1f);
        animator.update(0.1f); // advances at new shorter duration

        assertSame(textures.get(1), animator.getTexture());
    }
}