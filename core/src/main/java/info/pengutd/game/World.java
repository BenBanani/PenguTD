package info.pengutd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import info.pengutd.game.enemy.Enemy;
import info.pengutd.game.enemy.NormalEnemy;

public class World implements Screen, InputProcessor {

    private SpriteBatch batch;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Enemy testEnemey;
    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    private Stage uiStage;
    private Table towerSelectionTable;
    private Texture tableBackgroundTexture;

    @Override
    public void show() {
        batch = new SpriteBatch();
        map = new TmxMapLoader().load("map/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);

        viewport = new FitViewport(
            mapWidth * tileWidth,
            mapHeight * tileHeight
        );
        testEnemey = new NormalEnemy(4, this);

        uiStage = new Stage(new ScreenViewport());

        // Damit UI Input bekommt
        Gdx.input.setInputProcessor(new InputMultiplexer(this, uiStage));

        towerSelectionTable = new Table();

        // Rechts andocken
        towerSelectionTable.setFillParent(true);
        towerSelectionTable.top().right();

        // Abstand außen
        towerSelectionTable.pad(20);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        // Tower Buttons
        TextButton cannonButton = new TextButton("Cannon", skin);
        TextButton iceButton = new TextButton("Ice", skin);
        TextButton sniperButton = new TextButton("Sniper", skin);

        // Vertikal anordnen
        towerSelectionTable.add(cannonButton).width(180).height(60).padBottom(10);
        towerSelectionTable.row();

        towerSelectionTable.add(iceButton).width(180).height(60).padBottom(10);
        towerSelectionTable.row();

        towerSelectionTable.add(sniperButton).width(180).height(60);

        uiStage.addActor(towerSelectionTable);
    }

    public TiledMap getMap() {
        return map;
    }

    @Override
    public void render(float delta) {
        testEnemey.move(delta);

        ScreenUtils.clear(Color.BLACK);

        viewport.apply();

        mapRenderer.setView((OrthographicCamera) viewport.getCamera());
        mapRenderer.render();

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        renderMapObjects();

        testEnemey.draw(batch);
        batch.end();

        uiStage.act(delta);
        uiStage.draw();

    }

    private void renderMapObjects() {
        MapLayer objectLayer = map.getLayers().get("detail_tiles");

        Array<TextureMapObject> objects = new Array<>();

        for (MapObject obj : objectLayer.getObjects()) {
            if (obj instanceof TextureMapObject) {
                TextureMapObject texObj = (TextureMapObject) obj;
                objects.add(texObj);
            }
        }
        objects.sort((a, b) -> Float.compare(b.getY(), a.getY()));
        objects.forEach((obj) -> batch.draw(obj.getTextureRegion(), obj.getX(), obj.getY()));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        mapRenderer.dispose();
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        testEnemey.pop(1);
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public int get_map_height() {
        return mapHeight;
    }

    public int get_map_width() {
        return mapWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }
}
