package info.pengutd.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for GameObject.
 * <p>
 * GameObject is abstract, so we use a minimal concrete subclass (TestObject) that
 * returns fixed dimensions and a null texture. No LibGDX rendering context is needed
 * because none of the tested methods touch OpenGL.
 */
@ExtendWith(MockitoExtension.class)
class GameObjectTest {

    // -------------------------------------------------------------------------
    // Minimal concrete subclass
    // -------------------------------------------------------------------------

    /**
     * Concrete stand-in for testing. Fixed 40x20 size, no texture, no update logic.
     * World is always a mock so no file I/O or OpenGL occurs.
     */
    private static class TestObject extends GameObject {

        TestObject(Vector2 pos) {
            super(mock(World.class), pos);
        }

        @Override
        public @NotNull TextureRegion getTexture() {
            //noinspection DataFlowIssue
            return null; // not needed for any test here
        }

        @Override
        public @NotNull String getType() {
            return "";
        }

        @Override
        public float getWidth() {
            return 40f;
        }

        @Override
        public float getHeight() {
            return 20f;
        }

        @Override
        public void update(float delta) {}

        @Override
        public void dispose() {}
    }

    // =========================================================================
    // Constructor — initial position and hitbox
    // =========================================================================

    @Test
    void constructor_setsPositionCorrectly() {
        TestObject obj = new TestObject(new Vector2(100f, 200f));

        assertEquals(100f, obj.getX());
        assertEquals(200f, obj.getY());
    }

    @Test
    void constructor_centresHitboxOnPosition() {
        TestObject obj = new TestObject(new Vector2(100f, 200f));

        Rectangle hitbox = obj.getHitbox();
        // hitbox center must equal the construction position
        assertEquals(100f, hitbox.x + hitbox.width  / 2f, 0.001f);
        assertEquals(200f, hitbox.y + hitbox.height / 2f, 0.001f);
    }

    @Test
    void constructor_hitboxHasCorrectDimensions() {
        TestObject obj = new TestObject(new Vector2(0f, 0f));

        assertEquals(40f, obj.getHitbox().width,  0.001f);
        assertEquals(20f, obj.getHitbox().height, 0.001f);
    }

    // =========================================================================
    // setPos — position and hitbox stay in sync
    // =========================================================================

    @Test
    void setPos_updatesXAndY() {
        TestObject obj = new TestObject(new Vector2(0f, 0f));

        obj.setPos(new Vector2(50f, 75f));

        assertEquals(50f, obj.getX(), 0.001f);
        assertEquals(75f, obj.getY(), 0.001f);
    }

    @Test
    void setPos_recentresHitbox() {
        TestObject obj = new TestObject(new Vector2(0f, 0f));

        obj.setPos(new Vector2(50f, 75f));

        Rectangle hitbox = obj.getHitbox();
        assertEquals(50f, hitbox.x + hitbox.width  / 2f, 0.001f);
        assertEquals(75f, hitbox.y + hitbox.height / 2f, 0.001f);
    }

    // =========================================================================
    // getPos — must return a defensive copy
    // =========================================================================

    @Test
    void getPos_returnsACopy() {
        TestObject obj = new TestObject(new Vector2(10f, 20f));

        Vector2 copy = obj.getPos();
        copy.set(999f, 999f); // mutate the returned copy

        // internal state must be unchanged
        assertEquals(10f, obj.getX(), 0.001f);
        assertEquals(20f, obj.getY(), 0.001f);
    }

    @Test
    void getPos_valuesMatchCurrentPosition() {
        TestObject obj = new TestObject(new Vector2(10f, 20f));
        obj.setPos(new Vector2(30f, 40f));

        Vector2 pos = obj.getPos();

        assertEquals(30f, pos.x, 0.001f);
        assertEquals(40f, pos.y, 0.001f);
    }

    // =========================================================================
    // Rotation
    // =========================================================================

    @Test
    void rotationDeg_defaultsToZero() {
        TestObject obj = new TestObject(new Vector2(0f, 0f));

        assertEquals(0f, obj.getRotationDeg(), 0.001f);
    }

    @Test
    void setRotationDeg_storesValue() {
        TestObject obj = new TestObject(new Vector2(0f, 0f));

        obj.setRotationDeg(45f);

        assertEquals(45f, obj.getRotationDeg(), 0.001f);
    }

    @Test
    void setRotationDeg_acceptsNegativeValues() {
        TestObject obj = new TestObject(new Vector2(0f, 0f));

        obj.setRotationDeg(-90f);

        assertEquals(-90f, obj.getRotationDeg(), 0.001f);
    }

    // =========================================================================
    // toJson
    // =========================================================================

    @Test
    void toJson_containsXYAndRotation() {
        TestObject obj = new TestObject(new Vector2(5f, 10f));
        obj.setRotationDeg(30f);

        JsonValue json = obj.toJson();

        assertEquals(5f,  json.getFloat("x"),        0.001f);
        assertEquals(10f, json.getFloat("y"),         0.001f);
        assertEquals(30f, json.getFloat("rotation"),  0.001f);
    }

    // =========================================================================
    // fromJson
    // =========================================================================

    @Test
    void fromJson_restoresPositionAndRotation() {
        TestObject obj = new TestObject(new Vector2(0f, 0f));

        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        json.addChild("x",        new JsonValue(7f));
        json.addChild("y",        new JsonValue(13f));
        json.addChild("rotation", new JsonValue(90f));

        obj.fromJson(json);

        assertEquals(7f,  obj.getX(),            0.001f);
        assertEquals(13f, obj.getY(),             0.001f);
        assertEquals(90f, obj.getRotationDeg(),   0.001f);
    }

    @Test
    void fromJson_recentresHitboxAfterRestore() {
        TestObject obj = new TestObject(new Vector2(0f, 0f));

        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        json.addChild("x",        new JsonValue(60f));
        json.addChild("y",        new JsonValue(80f));
        json.addChild("rotation", new JsonValue(0f));

        obj.fromJson(json);

        Rectangle hitbox = obj.getHitbox();
        assertEquals(60f, hitbox.x + hitbox.width  / 2f, 0.001f);
        assertEquals(80f, hitbox.y + hitbox.height / 2f, 0.001f);
    }

    // =========================================================================
    // toJson → fromJson roundtrip
    // =========================================================================

    @Test
    void toJsonFromJson_roundtrip() {
        TestObject original = new TestObject(new Vector2(33f, 44f));
        original.setRotationDeg(270f);

        JsonValue json = original.toJson();

        TestObject restored = new TestObject(new Vector2(0f, 0f));
        restored.fromJson(json);

        assertEquals(original.getX(),            restored.getX(),            0.001f);
        assertEquals(original.getY(),            restored.getY(),            0.001f);
        assertEquals(original.getRotationDeg(),  restored.getRotationDeg(),  0.001f);
    }
}
