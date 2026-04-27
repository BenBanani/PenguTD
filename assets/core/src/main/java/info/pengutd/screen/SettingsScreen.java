package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.PenguTD;
import info.pengutd.Settings;

public class SettingsScreen implements Screen {

    private final Screen oldScreen;
    // private TextureAtlas textureAtlas;
    private Stage stage;
    private Texture backgroundTexture;
    private TextButton fullscreenButton;
    private TextButton backButton;

    public SettingsScreen(Screen screen) {
        oldScreen = screen;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        buildUI();

        addListeners();
    }

    private void addListeners() {
        fullscreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean fullScreen = !Settings.get().getFullScreen();
                fullscreenButton.setText("Fullscreen: " + (fullScreen ? "On" : "Off"));
                if (fullScreen) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(800, 480);
                }
                Settings.get().setFullScreen(fullScreen);
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PenguTD.getInstance().setScreenAndDispose(oldScreen);
            }
        });
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        Image background = new Image(backgroundTexture);
        background.setScaling(Scaling.fill);
        background.setFillParent(true);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Label label = new Label("Settings", skin);

        root.add(label).padBottom(40).row();

        Slider soundSlider = new Slider(0, 1, 0.01f, false, skin);
        soundSlider.setValue(Settings.get().getMusicVolume());
        root.add(new Label("Sound Volume", skin)).pad(10);
        root.add(soundSlider).row();

        Slider musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue(Settings.get().getMusicVolume());
        root.add(new Label("Music Volume", skin)).pad(10);
        root.add(musicSlider).row();

        fullscreenButton = new TextButton("Fullscreen: " + (Settings.get().getFullScreen() ? "On" : "Off"), skin);
        root.add(fullscreenButton).row();

        backButton = new TextButton("Back", skin);
        root.add(backButton).row();

        stage.addActor(background);
        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        // textureAtlas.dispose();
    }
}
