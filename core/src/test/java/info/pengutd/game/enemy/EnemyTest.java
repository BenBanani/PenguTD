package info.pengutd.game.enemy;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.game.World;
import info.pengutd.game.enemy.Enemy.Direction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Enemy.
 *
 * Enemy is abstract and its constructor always calls findPath(), which reads
 * getWorld().getMap().getLayers().get("path"). Every test therefore needs that
 * chain stubbed. Two construction helpers cover the two branches:
 *
 *   - makeEnemy()      → path layer exists with one PointMapObject, so the enemy
 *                        is positioned at that point and has a usable path.
 *   - makeEnemyNoPath()→ path layer is null, so findPath() logs an error and the
 *                        enemy starts at (0,0) with an empty path.
 *
 * Stubs are created locally in each helper or test so that only stubs actually
 * invoked by the code under test are ever registered.
 */
@ExtendWith(MockitoExtension.class)
class EnemyTest {

    // -------------------------------------------------------------------------
    // Minimal concrete subclass
    // -------------------------------------------------------------------------

    /**
     * Concrete Enemy for testing. Fixed stats, no texture, no atlas loading.
     * health is mutable so individual tests can control isAlive() outcomes.
     */
    private static class TestEnemy extends Enemy {

        private int health;

        TestEnemy(World world, int health) {
            super(world, new Vector2(0f, 0f), 42);
            this.health = health;
        }

        @Override public @NotNull TextureRegion getTexture() {
            //noinspection DataFlowIssue
            return null;
        }

        @Override
        public @NotNull String getType() {
            return "";
        }

        @Override public float getWidth()  { return 20f; }
        @Override public float getHeight() { return 20f; }
        @Override public int   getHealth() { return health; }
        @Override public float getSpeed()  { return 100f; }

        @Override
        protected void setHealth(int value) {

        }

        @Override public void  pop(int damage) { health -= damage; }
        @Override public void  die() { health = 0; }
        @Override public void  dispose() {}
    }

    // -------------------------------------------------------------------------
    // Construction helpers
    // -------------------------------------------------------------------------

    /**
     * Stubs the minimal map/layer chain that findPath() needs, then adds a single
     * PointMapObject at the given coordinates.  The stubs are placed directly on
     * the mocks that are passed in so the caller controls exactly which mocks exist.
     *
     * findPath() calls:
     *   world.getMap().getLayers().get("path")          → mapLayer
     *   mapLayer.getObjects()                           → objects (iterated)
     *   PointMapObject.getPoint()                       → Vector2
     *
     * It does NOT call getLayers() a second time, so one stub for getLayers() suffices.
     */
    private World stubWorldWithPath(float pointX, float pointY) {
        World world           = mock(World.class);
        com.badlogic.gdx.maps.tiled.TiledMap map = mock(com.badlogic.gdx.maps.tiled.TiledMap.class);
        MapLayers layers      = mock(MapLayers.class);
        MapLayer pathLayer    = mock(MapLayer.class);
        MapObjects objects    = mock(MapObjects.class);

        PointMapObject point  = mock(PointMapObject.class);
        when(point.getPoint()).thenReturn(new Vector2(pointX, pointY));

        when(world.getMap()).thenReturn(map);
        when(map.getLayers()).thenReturn(layers);
        when(layers.get("path")).thenReturn(pathLayer);
        when(pathLayer.getObjects()).thenReturn(objects);
        // iterator() is called by the for-each in findPath()
        Iterator<MapObject> it = Collections.singletonList((MapObject)point).iterator();
        when(objects.iterator()).thenReturn(it);
        // when(objects.iterator()).thenReturn(List.<com.badlogic.gdx.maps.MapObject>of(point).iterator());

        // update() calls world.getEnemies().removeValue(...) when the enemy reaches
        // the last path point. Return an empty Array so it does not NPE.
        org.mockito.Mockito.lenient().when(world.getEnemies()).thenReturn(new com.badlogic.gdx.utils.Array<Enemy>());

        return world;
    }

    /**
     * Stubs the layer lookup to return null, exercising the "no path layer" branch.
     * findPath() only calls world.getMap().getLayers().get("path") in this branch —
     * nothing further — so only those three stubs are registered.
     */
    private World stubWorldWithoutPath() {
        // findPath()'s else-branch calls Gdx.app.error(...).
        // Gdx.app is a static field that is null outside a LibGDX context,
        // so we provide a mock Application to prevent the NullPointerException.
        // No when() stub is needed: the mock silently absorbs the error() call.
        com.badlogic.gdx.Gdx.app = mock(com.badlogic.gdx.Application.class);

        World world      = mock(World.class);
        com.badlogic.gdx.maps.tiled.TiledMap map = mock(com.badlogic.gdx.maps.tiled.TiledMap.class);
        MapLayers layers = mock(MapLayers.class);

        when(world.getMap()).thenReturn(map);
        when(map.getLayers()).thenReturn(layers);
        when(layers.get("path")).thenReturn(null);

        return world;
    }

    private TestEnemy makeEnemy(float pathPointX, float pathPointY, int health) {
        return new TestEnemy(stubWorldWithPath(pathPointX, pathPointY), health);
    }

    private TestEnemy makeEnemyNoPath(int health) {
        return new TestEnemy(stubWorldWithoutPath(), health);
    }

    // =========================================================================
    // getId / setId
    // =========================================================================

    @Test
    void getId_returnsIdPassedToConstructor() {
        TestEnemy enemy = makeEnemy(0f, 0f, 10);

        assertEquals(42, enemy.getId());
    }

    // =========================================================================
    // isAlive
    // =========================================================================

    @Test
    void isAlive_returnsTrueWhenHealthAboveZero() {
        TestEnemy enemy = makeEnemy(0f, 0f, 5);

        assertTrue(enemy.isAlive());
    }

    @Test
    void isAlive_returnsFalseWhenHealthIsZero() {
        TestEnemy enemy = makeEnemy(0f, 0f, 0);

        assertFalse(enemy.isAlive());
    }

    @Test
    void isAlive_returnsTrueWhenHealthZeroButPopTimeLeftPositive() {
        TestEnemy enemy = makeEnemy(0f, 0f, 0);
        enemy.setPopTimeLeft(1f);

        assertTrue(enemy.isAlive());
    }

    // =========================================================================
    // popTimeLeft
    // =========================================================================

    @Test
    void popTimeLeft_defaultsToZero() {
        TestEnemy enemy = makeEnemy(0f, 0f, 10);

        assertEquals(0f, enemy.getPopTimeLeft(), 0.001f);
    }

    @Test
    void setPopTimeLeft_storesValue() {
        TestEnemy enemy = makeEnemy(0f, 0f, 10);

        enemy.setPopTimeLeft(2.5f);

        assertEquals(2.5f, enemy.getPopTimeLeft(), 0.001f);
    }

    // =========================================================================
    // update — popTimeLeft countdown
    // =========================================================================

    @Test
    void update_decrementsPopTimeLeft() {
        TestEnemy enemy = makeEnemy(0f, 0f, 10);
        enemy.setPopTimeLeft(1f);

        enemy.update(0.4f);

        assertEquals(0.6f, enemy.getPopTimeLeft(), 0.001f);
    }

    @Test
    void update_doesNotMoveWhilePopTimeLeftPositive() {
        // Place path point far from origin; enemy must not move while stunned.
        TestEnemy enemy = makeEnemy(500f, 500f, 10);
        enemy.setPopTimeLeft(1f);
        Vector2 posBefore = enemy.getPos();

        enemy.update(0.016f);

        assertEquals(posBefore.x, enemy.getPos().x, 0.001f);
        assertEquals(posBefore.y, enemy.getPos().y, 0.001f);
    }

    // =========================================================================
    // update — movement and direction
    // =========================================================================

    @Test
    void update_doesNothingWhenPathIsEmpty() {
        // No-path branch leaves path empty; update must not throw.
        TestEnemy enemy = makeEnemyNoPath(10);
        Vector2 posBefore = enemy.getPos();

        enemy.update(0.016f);

        assertEquals(posBefore.x, enemy.getPos().x, 0.001f);
        assertEquals(posBefore.y, enemy.getPos().y, 0.001f);
    }

    @Test
    void update_doesNothingWhenAtLastPathPoint() {
        // Only one path point → currentPathIndex (0) >= path.size-1 (0) → early return.
        TestEnemy enemy = makeEnemy(10f, 10f, 10);
        Vector2 posBefore = enemy.getPos();

        enemy.update(0.016f);

        assertEquals(posBefore.x, enemy.getPos().x, 0.001f);
        assertEquals(posBefore.y, enemy.getPos().y, 0.001f);
    }

    @Test
    void update_setsDirectionRightWhenMovingRight() {
        // We need at least two path points so that update() actually moves.
        // Inject a second point manually after construction.
        TestEnemy enemy = makeEnemy(0f, 0f, 10);
        enemy.getPath().add(new Vector2(1000f, 0f)); // second point far to the right

        enemy.update(0.016f);

        assertEquals(Direction.RIGHT, enemy.getDirection());
    }

    @Test
    void update_setsDirectionLeftWhenMovingLeft() {
        TestEnemy enemy = makeEnemy(1000f, 0f, 10);
        enemy.getPath().add(new Vector2(0f, 0f)); // second point to the left

        enemy.update(0.016f);

        assertEquals(Direction.LEFT, enemy.getDirection());
    }

    @Test
    void update_setsDirectionUpWhenMovingUp() {
        TestEnemy enemy = makeEnemy(0f, 0f, 10);
        enemy.getPath().add(new Vector2(0f, 1000f)); // second point upward

        enemy.update(0.016f);

        assertEquals(Direction.UP, enemy.getDirection());
    }

    @Test
    void update_setsDirectionDownWhenMovingDown() {
        TestEnemy enemy = makeEnemy(0f, 1000f, 10);
        enemy.getPath().add(new Vector2(0f, 0f)); // second point downward

        enemy.update(0.016f);

        assertEquals(Direction.DOWN, enemy.getDirection());
    }

    // =========================================================================
    // getPath / findPath
    // =========================================================================

    @Test
    void getPath_containsPointFromMapLayer() {
        TestEnemy enemy = makeEnemy(128f, 256f, 10);

        Array<Vector2> path = enemy.getPath();

        assertFalse(path.isEmpty());
        // findPath() adds a small random jitter (±1 px), so allow 1 px tolerance
        assertEquals(128f, path.get(0).x, 1f);
        assertEquals(256f, path.get(0).y, 1f);
    }

    @Test
    void getPath_isEmptyWhenNoPathLayerExists() {
        TestEnemy enemy = makeEnemyNoPath(10);

        assertTrue(enemy.getPath().isEmpty());
    }

    // =========================================================================
    // toJson / fromJson
    // =========================================================================

    @Test
    void toJson_containsIdCurrentPathIndexAndPopTimeLeft() {
        TestEnemy enemy = makeEnemy(0f, 0f, 10);
        enemy.setPopTimeLeft(1.5f);

        JsonValue json = enemy.toJson();

        assertEquals(42,   json.getInt("id"));
        assertEquals(0,    json.getInt("currentPathIndex"));
        assertEquals(1.5f, json.getFloat("popTimeLeft"), 0.001f);
    }

    @Test
    void fromJson_restoresIdPathIndexAndPopTimeLeft() {
        TestEnemy enemy = makeEnemy(0f, 0f, 10);

        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        json.addChild("x",                new JsonValue(5f));
        json.addChild("y",                new JsonValue(10f));
        json.addChild("rotation",         new JsonValue(0f));
        json.addChild("id",               new JsonValue(99));
        json.addChild("currentPathIndex", new JsonValue(3));
        json.addChild("popTimeLeft",      new JsonValue(2f));

        enemy.fromJson(json);

        assertEquals(99,  enemy.getId());
        assertEquals(3,   enemy.currentPathIndex);
        assertEquals(2f,  enemy.getPopTimeLeft(), 0.001f);
    }

    @Test
    void toJsonFromJson_roundtrip() {
        TestEnemy original = makeEnemy(50f, 60f, 10);
        original.setPopTimeLeft(0.8f);
        original.currentPathIndex = 1;

        JsonValue json = original.toJson();

        TestEnemy restored = makeEnemy(0f, 0f, 10);
        restored.fromJson(json);

        assertEquals(original.getId(),             restored.getId());
        assertEquals(original.currentPathIndex,    restored.currentPathIndex);
        assertEquals(original.getPopTimeLeft(),    restored.getPopTimeLeft(), 0.001f);
        assertEquals(original.getX(),              restored.getX(),           0.001f);
        assertEquals(original.getY(),              restored.getY(),           0.001f);
    }
}
