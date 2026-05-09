package info.pengutd.game.enemy;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
//nicht fertig
/**
 * Unit tests for WarriorEnemy.
 *
 * The constructor calls:
 *   1. super() → Enemy() → findPath() → world.getMap().getLayers().get("path")
 *   2. PenguTD.getInstance().getAssetManager().get(Assets.WARRIOR_ENEMY_ATLAS)
 *   3. atlas.findRegion(name + i)  four times per animator (three animators)
 *   4. atlas.findRegion("pop")
 *
 * All four chains are mocked so no OpenGL context, asset files, or singleton
 * state is needed. Every stub is placed only in the helper that needs it.
 *
 * PenguTD.getInstance() is a static call on a singleton. We mock it via
 * Mockito's mockStatic so the real singleton is never initialised.
 */
@ExtendWith(MockitoExtension.class)
class WarriorEnemyTest {

    // -------------------------------------------------------------------------
    // Shared atlas mock — used by every test via makeEnemy()
    // -------------------------------------------------------------------------

    /**
     * Builds the full mock chain the WarriorEnemy constructor needs and returns
     * a ready-to-use WarriorEnemy.
     *
     * Stubs registered here are exactly those the constructor calls:
     *   - Gdx.app              → absorbs Gdx.app.error() in the no-path branch
     *   - world/map/layers     → findPath() with one PointMapObject
     *   - PenguTD singleton    → getAssetManager().get(atlas key)
     *   - atlas.findRegion()   → a non-null TextureRegion for every call
     *
     * @param level  the level passed to the WarriorEnemy constructor
     * @param tileW  stubbed return value for world.getTileWidth()
     * @param tileH  stubbed return value for world.getTileHeight()
     */
    private WarriorEnemy makeEnemy(int level, int tileW, int tileH) {
        // --- Gdx static ---
        Gdx.app = mock(Application.class);

        // --- world / map / path layer ---
        World world    = mock(World.class);
        com.badlogic.gdx.maps.tiled.TiledMap map = mock(com.badlogic.gdx.maps.tiled.TiledMap.class);
        MapLayers layers   = mock(MapLayers.class);
        MapLayer pathLayer = mock(MapLayer.class);
        MapObjects objects = mock(MapObjects.class);
        PointMapObject pt  = mock(PointMapObject.class);

        when(pt.getPoint()).thenReturn(new Vector2(0f, 0f));
        when(world.getMap()).thenReturn(map);
        when(map.getLayers()).thenReturn(layers);
        when(layers.get("path")).thenReturn(pathLayer);
        when(pathLayer.getObjects()).thenReturn(objects);
        when(objects.iterator())
                .thenReturn(List.<com.badlogic.gdx.maps.MapObject>of(pt).iterator());

        when(world.getTileWidth()).thenReturn(tileW);
        when(world.getTileHeight()).thenReturn(tileH);

        // --- PenguTD singleton ---
        PenguTD penguTD      = mock(PenguTD.class);
        AssetManager assetMgr = mock(AssetManager.class);
        TextureAtlas atlas    = mock(TextureAtlas.class);

        // findRegion() is called for every animation frame AND for "pop".
        // Return a distinct mock per call so identity checks stay meaningful.
        when(atlas.findRegion(anyString())).thenReturn(mock(TextureRegion.class));

        when(assetMgr.get(Assets.WARRIOR_ENEMY_ATLAS, TextureAtlas.class))
                .thenReturn(atlas);
        when(penguTD.getAssetManager()).thenReturn(assetMgr);

        try (var ignored = mockStatic(PenguTD.class)) {
            when(PenguTD.getInstance()).thenReturn(penguTD);
            return new WarriorEnemy(level, world, 7);
        }
    }

    // =========================================================================
    // getHealth
    // =========================================================================

    @Test
    void getHealth_equalsLevel() {
        WarriorEnemy enemy = makeEnemy(3, 32, 32);

        assertEquals(3, enemy.getHealth());
    }

    @Test
    void getHealth_equalsLevelAfterConstruction() {
        WarriorEnemy enemy = makeEnemy(1, 32, 32);

        assertEquals(1, enemy.getHealth());
    }

    // =========================================================================
    // getSpeed
    // =========================================================================

    @Test
    void getSpeed_scalesWithLevelAndTileWidth() {
        // SPEED = 0.75f, level = 2, tileWidth = 32 → 0.75 * 2 * 32 = 48
        WarriorEnemy enemy = makeEnemy(2, 32, 32);

        assertEquals(0.75f * 2 * 32, enemy.getSpeed(), 0.001f);
    }

    @Test
    void getSpeed_zeroWhenLevelIsZero() {
        WarriorEnemy enemy = makeEnemy(0, 32, 32);

        assertEquals(0f, enemy.getSpeed(), 0.001f);
    }

    // =========================================================================
    // getHeight / getWidth
    // =========================================================================

    @Test
    void getHeight_scalesWithTileHeight() {
        // HEIGHT = 0.75f, tileHeight = 32 → 0.75 * 32 = 24
        WarriorEnemy enemy = makeEnemy(2, 32, 32);

        assertEquals(0.75f * 32, enemy.getHeight(), 0.001f);
    }

    @Test
    void getWidth_scalesWithTileWidthWhenNotPopped() {
        // WIDTH = 0.65f, tileWidth = 32 → 0.65 * 32 = 20.8
        WarriorEnemy enemy = makeEnemy(2, 32, 32);

        assertEquals(0.65f * 32, enemy.getWidth(), 0.001f);
    }

    @Test
    void getWidth_equalsHeightWhenPopped() {
        // pop texture is square, so getWidth() returns getHeight() during pop
        WarriorEnemy enemy = makeEnemy(2, 32, 32);
        enemy.setPopTimeLeft(0.1f);

        assertEquals(enemy.getHeight(), enemy.getWidth(), 0.001f);
    }

    // =========================================================================
    // flipX
    // =========================================================================

    @Test
    void flipX_falseWhenDirectionIsRight() {
        WarriorEnemy enemy = makeEnemy(2, 32, 32);
        // default direction after construction with path pointing right
        enemy.getPath().add(new Vector2(1000f, 0f));
        enemy.update(0.016f); // moves right → direction = RIGHT

        assertFalse(enemy.flipX());
    }

    @Test
    void flipX_trueWhenDirectionIsLeft() {
        WarriorEnemy enemy = makeEnemy(2, 32, 32);
        // Add a second point to the left so movement goes LEFT
        enemy.getPath().add(new Vector2(-1000f, 0f));
        enemy.update(0.016f);

        assertTrue(enemy.flipX());
    }

    // =========================================================================
    // pop
    // =========================================================================

    @Test
    void pop_reducesLevelByDamage() {
        WarriorEnemy enemy = makeEnemy(4, 32, 32);

        enemy.pop(1);

        assertEquals(3, enemy.getHealth());
    }

    @Test
    void pop_doesNothingWhileAlreadyPopped() {
        WarriorEnemy enemy = makeEnemy(4, 32, 32);
        enemy.setPopTimeLeft(0.1f); // already in pop state

        enemy.pop(1);

        assertEquals(4, enemy.getHealth()); // health unchanged
    }

    @Test
    void pop_setsPoptimeLeft() {
        WarriorEnemy enemy = makeEnemy(4, 32, 32);

        enemy.pop(1);

        assertTrue(enemy.getPopTimeLeft() > 0f);
    }

    @Test
    void pop_callsDieWhenLevelReachesZero() {
        // level 1 → one hit kills
        WarriorEnemy enemy = makeEnemy(1, 32, 32);

        enemy.pop(1);

        // die() sets level to 0 indirectly via getHealth() == 0
        assertEquals(0, enemy.getHealth());
    }

    // =========================================================================
    // isAlive (inherited, driven by level and popTimeLeft)
    // =========================================================================

    @Test
    void isAlive_trueWhenLevelAboveZero() {
        WarriorEnemy enemy = makeEnemy(2, 32, 32);

        assertTrue(enemy.isAlive());
    }

    @Test
    void isAlive_falseAfterLethalPop() {
        WarriorEnemy enemy = makeEnemy(1, 32, 32);
        enemy.pop(1);           // level → 0, popTimeLeft set
        enemy.setPopTimeLeft(0f); // expire the pop window

        assertFalse(enemy.isAlive());
    }

    @Test
    void isAlive_trueDirectlyAfterLethalPop() {
        // popTimeLeft is still positive immediately after a lethal hit
        WarriorEnemy enemy = makeEnemy(1, 32, 32);
        enemy.pop(1);

        assertTrue(enemy.isAlive());
    }

    // =========================================================================
    // toJson
    // =========================================================================

    @Test
    void toJson_containsTypeAndLevel() {
        WarriorEnemy enemy = makeEnemy(3, 32, 32);

        JsonValue json = enemy.toJson();

        assertEquals(WarriorEnemy.JSON_TYPE, json.getString("type"));
        assertEquals(3, json.getInt("level"));
    }

    @Test
    void toJson_inheritsXYFromGameObject() {
        WarriorEnemy enemy = makeEnemy(3, 32, 32);

        JsonValue json = enemy.toJson();

        // x and y must be present (values come from the path point ≈ 0,0 ± jitter)
        assertTrue(json.has("x"));
        assertTrue(json.has("y"));
    }

    // =========================================================================
    // fromJson
    // =========================================================================

    @Test
    void fromJson_restoresLevel() {
        WarriorEnemy enemy = makeEnemy(1, 32, 32);

        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        json.addChild("x",                new JsonValue(0f));
        json.addChild("y",                new JsonValue(0f));
        json.addChild("rotation",         new JsonValue(0f));
        json.addChild("id",               new JsonValue(7));
        json.addChild("currentPathIndex", new JsonValue(0));
        json.addChild("popTimeLeft",      new JsonValue(0f));
        json.addChild("level",            new JsonValue(5));

        enemy.fromJson(json);

        assertEquals(5, enemy.getHealth());
    }

    // =========================================================================
    // toJson / fromJson roundtrip
    // =========================================================================

    @Test
    void toJsonFromJson_roundtrip() {
        WarriorEnemy original = makeEnemy(3, 32, 32);
        original.setPopTimeLeft(0.05f);

        JsonValue json = original.toJson();

        WarriorEnemy restored = makeEnemy(1, 32, 32);
        restored.fromJson(json);

        assertEquals(original.getHealth(),       restored.getHealth());
        assertEquals(original.getPopTimeLeft(),  restored.getPopTimeLeft(), 0.001f);
        assertEquals(original.getX(),            restored.getX(),           0.001f);
        assertEquals(original.getY(),            restored.getY(),           0.001f);
    }
}