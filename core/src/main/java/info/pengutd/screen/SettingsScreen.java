package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.PenguTD;
import info.pengutd.Settings;

// todo ui
public class SettingsScreen implements Screen {

    private final Screen previousScreen;
    private Skin skin;
    // todo private TextureAtlas textureAtlas;
    private Stage stage;

    private Texture bgTexture;
    private Texture titleTexture;
    private Texture buttonTexture;
    private Texture backButtonTexture;
    private Image title;
    private Stack soundSlider;
    private Stack musicSlider;
    private Stack fullscreenButton;
    private ImageButton backButton;

    public SettingsScreen(Screen screen) {
        previousScreen = screen;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        buildUI();
    }

    private void loadAssets() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        bgTexture = new Texture(Gdx.files.internal("background.png"));
        titleTexture = new Texture(Gdx.files.internal("settings_screen/title.png"));
        buttonTexture = new Texture(Gdx.files.internal("settings_screen/button.png"));
        backButtonTexture = new Texture(Gdx.files.internal("settings_screen/back_button.png"));
    }

    private void buildUI() {
        Image background = new Image(bgTexture);
        background.setFillParent(true);
        background.setScaling(Scaling.fill);
        stage.addActor(background);

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(20);

        title = new Image(titleTexture);
        root.add(title)
            .width(300).height(108)
            .colspan(2)
            .padBottom(15)
            .row();

        int moveDistance = 150;
        title.addAction(
            Actions.sequence(
                Actions.moveBy(0, moveDistance),
                Actions.moveBy(0, -moveDistance, 0.5f, Interpolation.smoother)
            )
        );

        soundSlider = createSlider("Sound Volume", Settings.get().getSoundVolume(), v -> Settings.get().setSoundVolume(v), false);
        root.add(soundSlider).width(400).height(100).row();

        musicSlider = createSlider("Music Volume", Settings.get().getMusicVolume(), v -> Settings.get().setMusicVolume(v), true);
        root.add(musicSlider).width(400).height(100).row();

        fullscreenButton = createFullscreenButton();
        root.add(fullscreenButton).width(400).height(100).row();

        stage.addActor(root);

        backButton = createBackButton();
        stage.addActor(backButton);
    }

    private ImageButton createBackButton() {
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(backButtonTexture));
        backButton.setSize(50, 50);
        backButton.setPosition(25, stage.getHeight() - 75);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().setTouchable(Touchable.disabled);

                title.addAction(Actions.moveBy(0, 150, 0.5f, Interpolation.smoother));
                soundSlider.addAction(Actions.moveBy(-600, 0, 0.5f, Interpolation.smoother));
                musicSlider.addAction(Actions.moveBy(600, 0, 0.5f, Interpolation.smoother));
                fullscreenButton.addAction(Actions.moveBy(-600, 0, 0.5f, Interpolation.smoother));
                backButton.addAction(Actions.moveBy(0, 100, 0.5f, Interpolation.smoother));

                backButton.addAction(Actions.sequence(
                    Actions.delay(0.5f),
                    Actions.run(() -> {
                        PenguTD.getInstance().setScreenAndDispose(previousScreen);
                    })
                ));
            }
        });

        int moveDistance = 100;
        backButton.addAction(
            Actions.sequence(
                Actions.moveBy(0, moveDistance),
                Actions.moveBy(0, -moveDistance, 0.5f, Interpolation.smoother)
            )
        );
        return backButton;
    }

    private Stack createSlider(String labelName, float initialValue, SliderCallback callback, boolean fromLeft) {
        Stack stack = new Stack();

        Image bg = new Image(buttonTexture);
        stack.add(bg);

        Table content = new Table();
        content.pad(5);

        Label lbl = new Label(labelName, skin);

        Slider slider = new Slider(0f, 1f, 0.01f, false, skin);
        slider.setValue(initialValue);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                callback.onChange(slider.getValue());
            }
        });

        content.add(lbl).left().row();
        content.add(slider).width(250);

        stack.add(content);

        addAnimation(stack, fromLeft);

        return stack;
    }

    private Stack createFullscreenButton() {
        Stack stack = new Stack();

        Image background = new Image(buttonTexture);
        stack.add(background);

        Table content = new Table();
        content.setFillParent(true);

        Label fullscreenLabel = new Label(
            "Fullscreen: " + (Settings.get().getFullScreen() ? "On" : "Off"),
            skin
        );

        content.center();
        content.add(fullscreenLabel).expand().center();
        stack.add(content);

        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean newValue = !Settings.get().getFullScreen();

                Settings.get().setFullScreen(newValue);

                if (newValue) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(800, 480);
                }

                fullscreenLabel.setText(
                    "Fullscreen: " + (newValue ? "On" : "Off")
                );
            }
        });

        addAnimation(stack, false);

        return stack;
    }

    private void addAnimation(Actor actor, boolean fromLeft) {
        int moveDistance = 600;
        actor.addAction(
            Actions.sequence(
                Actions.moveBy(fromLeft ? -moveDistance : moveDistance, 0),
                Actions.moveBy(fromLeft ? moveDistance : -moveDistance, 0, 0.5f, Interpolation.smoother)
            )
        );
    }

    private void addExitAnimation(Actor actor, boolean toLeft) {
        int moveDistance = 600;
        actor.addAction(
            Actions.moveBy(toLeft ? -moveDistance : moveDistance, 0, 0.5f, Interpolation.smoother)
        );
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
        bgTexture.dispose();
        titleTexture.dispose();
        buttonTexture.dispose();
        backButtonTexture.dispose();
        // textureAtlas.dispose();
    }

    private interface SliderCallback {
        void onChange(float value);
    }
}
