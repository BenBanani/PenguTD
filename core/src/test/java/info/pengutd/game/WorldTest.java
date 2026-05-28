package info.pengutd.game;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import info.pengutd.save.JsonSerializable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.tower.Tower;
import info.pengutd.game.tower.projectile.Projectile;

/**
 * Unit tests for World focusing on pure logic: entity management, tower placement
 * rules, JSON serialisation, and state transitions.
 *
 * LibGDX requires an OpenGL context that is unavailable in a plain JUnit run.
 * We bypass this entirely by:
 *   1. Using the {@code fromJson = true} constructor, which skips the real show()
 *      initialisation block.
 *   2. Injecting fields (map, dimensions, …) directly via reflection only in the
 *      tests that actually need them, so no stub is ever declared without being used.
 */
@ExtendWith(MockitoExtension.class)
class WorldTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Creates a bare World (no map, no layers, no LibGDX context) suitable for
     * tests that only exercise pure-Java logic (IDs, enemy lookup, JSON, …).
     */
    private World bareWorld() {
        return new World(true);
    }

    /**
     * Creates a World and injects a mocked TiledMap plus the given tile dimensions.
     * Only the fields that every canPlaceTower call reads are pre-configured here;
     * layer and cell stubs are set up inside each individual test.
     */
    private World worldWithMap(TiledMap map, int tileW, int tileH) throws Exception {
        World world = new World(true);
        setField(world, "map",        map);
        setField(world, "tileWidth",  tileW);
        setField(world, "tileHeight", tileH);
        return world;
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    // =========================================================================
    // createEntityId
    // =========================================================================

    @Test
    void createEntityId_startsAtZeroAndIncrements() {
        World world = bareWorld();

        assertEquals(0, world.createEntityId(), "first ID must be 0");
        assertEquals(1, world.createEntityId(), "second ID must be 1");
        assertEquals(2, world.createEntityId(), "third ID must be 2");
    }

    // =========================================================================
    // getEnemyFromId
    // =========================================================================

    @Test
    void getEnemyFromId_returnsNullOnEmptyList() {
        World world = bareWorld();

        assertNull(world.getEnemyFromId(42));
    }

    @Test
    void getEnemyFromId_returnsMatchingEnemy() {
        World world = bareWorld();

        Enemy enemyA = mock(Enemy.class);
        Enemy enemyB = mock(Enemy.class);
        when(enemyA.getId()).thenReturn(10);
        when(enemyB.getId()).thenReturn(20);

        world.getEnemies().add(enemyA);
        world.getEnemies().add(enemyB);

        assertSame(enemyB, world.getEnemyFromId(20));
    }

    @Test
    void getEnemyFromId_returnsNullForUnknownId() {
        World world = bareWorld();

        Enemy enemy = mock(Enemy.class);
        when(enemy.getId()).thenReturn(5);
        world.getEnemies().add(enemy);

        assertNull(world.getEnemyFromId(99));
    }

    // =========================================================================
    // Dimension getters
    // =========================================================================

    @Test
    void dimensionGetters_returnInjectedValues() throws Exception {
        World world = bareWorld();
        setField(world, "mapWidth",  30);
        setField(world, "mapHeight", 20);
        setField(world, "tileWidth", 16);
        setField(world, "tileHeight", 16);

        assertEquals(30, world.getMapWidth());
        assertEquals(20, world.getMapHeight());
        assertEquals(16, world.getTileWidth());
        assertEquals(16, world.getTileHeight());
    }

    // =========================================================================
    // canPlaceTower
    // =========================================================================

    /**
     * Stubs only the ground-layer lookup. canPlaceTower reads the ground layer
     * in every code path, but only reaches the detail layer after passing both
     * the null-cell and placable checks — so do NOT stub detail here.
     */
    private void stubGroundLayer(TiledMap map, TiledMapTileLayer groundLayer) {
        MapLayers layers = mock(MapLayers.class);
        when(map.getLayers()).thenReturn(layers);
        when(layers.get("ground")).thenReturn(groundLayer);
    }

    /**
     * Stubs a cell on {@code groundLayer} and configures whether its tile carries
     * the "placable" property. Used by the three tests that need a non-null cell.
     */
    private void stubCell(TiledMapTileLayer groundLayer, boolean placable) {
        TiledMapTileLayer.Cell cell = mock(TiledMapTileLayer.Cell.class);
        TiledMapTile tile           = mock(TiledMapTile.class);
        MapProperties tileProps     = mock(MapProperties.class);

        when(groundLayer.getCell(anyInt(), anyInt())).thenReturn(cell);
        when(cell.getTile()).thenReturn(tile);
        when(tile.getProperties()).thenReturn(tileProps);
        when(tileProps.containsKey("placable")).thenReturn(placable);
    }

    @Test
    void canPlaceTower_returnsFalseWhenCellIsNull() throws Exception {
        // Early return at null-cell check: only ground layer is ever accessed.
        TiledMap map             = mock(TiledMap.class);
        TiledMapTileLayer ground = mock(TiledMapTileLayer.class);

        stubGroundLayer(map, ground);
        when(ground.getCell(anyInt(), anyInt())).thenReturn(null);

        World world = worldWithMap(map, 32, 32);
        Tower tower = mock(Tower.class); // getHitbox() never reached — no stub needed

        assertFalse(world.canPlaceTower(new Vector2(64, 64), tower));
    }

    @Test
    void canPlaceTower_returnsFalseWhenTileNotPlacable() throws Exception {
        // Early return at placable check: only ground layer is ever accessed.
        TiledMap map             = mock(TiledMap.class);
        TiledMapTileLayer ground = mock(TiledMapTileLayer.class);

        stubGroundLayer(map, ground);
        stubCell(ground, false);

        World world = worldWithMap(map, 32, 32);
        Tower tower = mock(Tower.class); // getHitbox() never reached — no stub needed

        assertFalse(world.canPlaceTower(new Vector2(64, 64), tower));
    }

    @Test
    void canPlaceTower_returnsFalseWhenOverlapsExistingTower() throws Exception {
        // Early return at tower-overlap check: only ground layer is ever accessed.
        TiledMap map             = mock(TiledMap.class);
        TiledMapTileLayer ground = mock(TiledMapTileLayer.class);

        stubGroundLayer(map, ground);
        stubCell(ground, true);

        World world = worldWithMap(map, 32, 32);

        Tower existing = mock(Tower.class);
        when(existing.getHitbox()).thenReturn(new Rectangle(48, 48, 32, 32)); // centred at (64,64)
        Array<Tower> towers = getField(world, "towers");
        towers.add(existing);

        Tower newTower = mock(Tower.class);
        when(newTower.getHitbox()).thenReturn(new Rectangle(0, 0, 32, 32));

        assertFalse(world.canPlaceTower(new Vector2(64, 64), newTower));
    }

    @Test
    void canPlaceTower_returnsTrueOnEmptyPlacableTile() throws Exception {
        // Full path: reaches both ground AND detail-layer collision checks.
        TiledMap map             = mock(TiledMap.class);
        TiledMapTileLayer ground = mock(TiledMapTileLayer.class);
        MapLayers layers         = mock(MapLayers.class);
        MapLayer detail          = mock(MapLayer.class);
        MapObjects noItems       = mock(MapObjects.class);

        when(map.getLayers()).thenReturn(layers);
        when(layers.get("ground")).thenReturn(ground);
        when(layers.get("detail_tiles")).thenReturn(detail);
        when(detail.getObjects()).thenReturn(noItems);
        when(noItems.iterator()).thenReturn(new ArrayList<com.badlogic.gdx.maps.MapObject>().iterator());
        stubCell(ground, true);

        World world = worldWithMap(map, 32, 32);
        Tower tower = mock(Tower.class);
        when(tower.getHitbox()).thenReturn(new Rectangle(0, 0, 32, 32));

        assertTrue(world.canPlaceTower(new Vector2(64, 64), tower));
    }

    // =========================================================================
    // addProjectile
    // =========================================================================

    @Test
    void addProjectile_addsToInternalList() throws Exception {
        World world          = bareWorld();
        Projectile projectile = mock(Projectile.class);

        world.addProjectile(projectile);

        Array<Projectile> projectiles = getField(world, "projectiles");
        assertEquals(1, projectiles.size);
        assertSame(projectile, projectiles.get(0));
    }

    // =========================================================================
    // toJson
    // =========================================================================

    @Test
    void toJson_containsRequiredTopLevelKeys() {
        World world = bareWorld();

        com.badlogic.gdx.utils.JsonValue json = world.toJson();

        assertNotNull(json);
        assertEquals("world", json.getString("type"));
        assertNotNull(json.get("enemies"));
        assertNotNull(json.get("towers"));
        assertTrue(json.has("next_entity_id"));
    }

    @Test
    void toJson_nextEntityIdMatchesCreatedIdCount() {
        World world = bareWorld();

        world.createEntityId(); // 0
        world.createEntityId(); // 1
        world.createEntityId(); // 2  →  nextEntityId == 3

        assertEquals(3, world.toJson().getInt("next_entity_id"));
    }

    // =========================================================================
    // fromJson guard
    // =========================================================================

    @Test
    void fromJson_throwsIllegalStateWhenFlagIsFalse() {
        JsonSerializable world = new World(false);
        com.badlogic.gdx.utils.JsonValue dummy =
                new com.badlogic.gdx.utils.JsonValue(com.badlogic.gdx.utils.JsonValue.ValueType.object);

        assertThrows(IllegalStateException.class, () -> world.fromJson(dummy));
    }

    // =========================================================================
    // setSelectedTower
    // =========================================================================

    @Test
    void setSelectedTower_zeroNullsOutPreviewTower() throws Exception {
        World world = bareWorld();
        setField(world, "previewTower", mock(Tower.class));

        world.setSelectedTower(0);

        assertNull(getField(world, "previewTower"));
    }
}
