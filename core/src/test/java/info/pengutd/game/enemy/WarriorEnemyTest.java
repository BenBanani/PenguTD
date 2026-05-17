package info.pengutd.game.enemy;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.game.World;

@ExtendWith(MockitoExtension.class)
class WarriorEnemyTest {

    private WarriorEnemy makeEnemy(int level, int tileW, int tileH) {
        Gdx.app = mock(Application.class);

        World world        = mock(World.class);
        com.badlogic.gdx.maps.tiled.TiledMap map = mock(com.badlogic.gdx.maps.tiled.TiledMap.class);
        MapLayers layers   = mock(MapLayers.class);
        MapLayer pathLayer = mock(MapLayer.class);
        MapObjects objects = mock(MapObjects.class);
        PointMapObject pt  = mock(PointMapObject.class);

        doReturn(new Vector2(0f, 0f)).when(pt).getPoint();
        doReturn(map).when(world).getMap();
        doReturn(layers).when(map).getLayers();
        doReturn(pathLayer).when(layers).get("path");
        doReturn(objects).when(pathLayer).getObjects();
        doReturn(Collections.singletonList(pt).iterator()).when(objects).iterator();
        // doReturn(List.<com.badlogic.gdx.maps.MapObject>of(pt).iterator()).when(objects).iterator(); // geht nicht weil java 7
        doReturn(tileW).when(world).getTileWidth();
        doReturn(tileH).when(world).getTileHeight();

        PenguTD penguTD        = mock(PenguTD.class);
        AssetManager assetMgr  = mock(AssetManager.class);
        TextureAtlas atlas      = mock(TextureAtlas.class);

        doReturn(mock(TextureAtlas.AtlasRegion.class)).when(atlas).findRegion(anyString());
        doReturn(atlas).when(assetMgr).get(Assets.WARRIOR_ENEMY_ATLAS);
        doReturn(assetMgr).when(penguTD).getAssetManager();

        MockedStatic<PenguTD> staticMock = mockStatic(PenguTD.class);
        try {
            staticMock.when(PenguTD::getInstance).thenReturn(penguTD);
            return new WarriorEnemy(level, world, 7);
        } finally {
            staticMock.close();
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
        WarriorEnemy enemy = makeEnemy(2, 32, 32);

        assertEquals(0.75f * 32, enemy.getHeight(), 0.001f);
    }

    @Test
    void getWidth_scalesWithTileWidthWhenNotPopped() {
        WarriorEnemy enemy = makeEnemy(2, 32, 32);

        assertEquals(0.65f * 32, enemy.getWidth(), 0.001f);
    }

    @Test
    void getWidth_equalsHeightWhenPopped() {
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
        enemy.getPath().add(new Vector2(1000f, 0f));
        enemy.update(0.016f);

        assertFalse(enemy.flipX());
    }

    @Test
    void flipX_trueWhenDirectionIsLeft() {
        WarriorEnemy enemy = makeEnemy(2, 32, 32);
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
        enemy.setPopTimeLeft(0.1f);

        enemy.pop(1);

        assertEquals(4, enemy.getHealth());
    }

    @Test
    void pop_setsPoptimeLeft() {
        WarriorEnemy enemy = makeEnemy(4, 32, 32);

        enemy.pop(1);

        assertTrue(enemy.getPopTimeLeft() > 0f);
    }

    @Test
    void pop_killsEnemyWhenLevelReachesZero() {
        WarriorEnemy enemy = makeEnemy(1, 32, 32);

        enemy.pop(1);

        assertEquals(0, enemy.getHealth());
    }

    // =========================================================================
    // isAlive
    // =========================================================================

    @Test
    void isAlive_trueWhenLevelAboveZero() {
        WarriorEnemy enemy = makeEnemy(2, 32, 32);

        assertTrue(enemy.isAlive());
    }

    @Test
    void isAlive_falseAfterLethalPopExpires() {
        WarriorEnemy enemy = makeEnemy(1, 32, 32);
        enemy.pop(1);
        enemy.setPopTimeLeft(0f);

        assertFalse(enemy.isAlive());
    }

    @Test
    void isAlive_trueDirectlyAfterLethalPop() {
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

        assertEquals(original.getHealth(),      restored.getHealth());
        assertEquals(original.getPopTimeLeft(), restored.getPopTimeLeft(), 0.001f);
        assertEquals(original.getX(),           restored.getX(),           0.001f);
        assertEquals(original.getY(),           restored.getY(),           0.001f);
    }
}
