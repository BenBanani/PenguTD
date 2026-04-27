package info.pengutd.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
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
        testEnemey.draw(batch);
        batch.end();

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
