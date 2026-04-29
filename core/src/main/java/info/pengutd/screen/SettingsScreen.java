package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.PenguTD;
import info.pengutd.Settings;

// todo ui
public class SettingsScreen implements Screen {

    private final Screen oldScreen;
    // todo private TextureAtlas textureAtlas;
    private Stage stage;
    private Texture backgroundTexture;
    private TextButton fullscreenButton;
    private ImageButton backButton;
    private Texture titleTexture;
    private Texture buttonTexture;
    private Texture backButtonTexture;

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
        root.debug().top();

        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        Image background = new Image(backgroundTexture);
        background.setScaling(Scaling.fill);
        background.setFillParent(true);

        buttonTexture = new Texture(Gdx.files.internal("settings_screen/button.png"));

        titleTexture = new Texture(Gdx.files.internal("settings_screen/title.png"));
        Image title = new Image(titleTexture);
        root.add(title).width(300).height(108).colspan(2).padTop(25).padBottom(25).row();

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        root.add(createSlider(skin, "Sound Volume")).width(300).height(75).pad(5).row();

        root.add(createSlider(skin, "Music Volume")).width(300).height(75).pad(5).row();

        root.add(createFullscreenButton(skin)).width(300).height(75).pad(5).row();

        Table topLeft = new Table();
        topLeft.setFillParent(true);
        topLeft.top().left();
        backButtonTexture = new Texture(Gdx.files.internal("settings_screen/back_button.png"));
        backButton = new ImageButton(new TextureRegionDrawable(backButtonTexture));
        topLeft.add(backButton).size(50, 50).pad(25).row();

        stage.addActor(background);
        stage.addActor(topLeft);
        stage.addActor(root);
    }

    private Stack createSlider(Skin skin, String labelName) {
        Stack stack = new Stack();
        Image background = new Image(buttonTexture);
        stack.add(background);
        Table content = new Table();
        content.setFillParent(true);
        content.add(new Label(labelName, skin)).row();
        Slider musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue(Settings.get().getMusicVolume());
        content.add(musicSlider);
        stack.add(content);

        return stack;
    }

    private Stack createFullscreenButton(Skin skin) {
        Stack stack = new Stack();
        Image background = new Image(buttonTexture);
        stack.add(background);
        Table content = new Table();
        content.setFillParent(true);
        fullscreenButton = new TextButton("Fullscreen: " + (Settings.get().getFullScreen() ? "On" : "Off"), skin);
        content.add(fullscreenButton).row();
        stack.add(content);

        return stack;
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
        titleTexture.dispose();
        buttonTexture.dispose();
        backButtonTexture.dispose();
        // textureAtlas.dispose();
    }
}
