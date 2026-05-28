package info.pengutd.game.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.Settings;
import info.pengutd.game.World;
import info.pengutd.screen.SettingsScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class PauseOverlay implements Disposable {
    public static final int DIALOG_PADDING = 50;
    private final @NotNull World world;
    private final @NotNull Stage uiStage;
    private final @NotNull Table content;
    private final @NotNull Image title;
    private final @NotNull Image resumeButton;
    private final @NotNull Image settingsButton;
    private final @NotNull Image mainMenuButton;
    private final @NotNull TextureAtlas settingsAtlas;
    private @Nullable Table dialog;
    private boolean visible = false;

    public PauseOverlay(@NotNull World world) {
        this.world = world;

        uiStage = new Stage(new FitViewport(800f, 480f));
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        TextureAtlas atlas = PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS);
        settingsAtlas = PenguTD.getInstance().getAssetManager().get(Assets.SETTINGS_SCREEN_ATLAS);

        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.TOWER_SELECTION_ATLAS), "background")).tint(new Color(1, 1, 1, 0.6f)));
        uiStage.addActor(background);

        content = new Table();
        content.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "background_banner")));

        content.setSize(300, 350);
        content.setPosition((800 - 300 - DIALOG_PADDING) / 2f, (480 - 350) / 2f); // 50 nach links → nicht ganz mitte aber sieht gut aus wo es ist
        uiStage.addActor(content);

        title = new Image(Assets.findRegionOrMissing(atlas, "title_banner"));
        title.setSize(400, 100);
        title.setPosition((800 - 400 - DIALOG_PADDING) / 2f, 350);
        uiStage.addActor(title);

        resumeButton = new Image(Assets.findRegionOrMissing(atlas, "resume_button"));
        content.add(resumeButton).size(200, 60).pad(10).row();
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });

        settingsButton = new Image(Assets.findRegionOrMissing(atlas, "settings_button"));
        content.add(settingsButton).size(200, 60).pad(10).row();
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.getRoot().setTouchable(Touchable.disabled);
                dialog = getSettingsDialog();
                uiStage.addActor(dialog);
            }
        });

        mainMenuButton = new Image(Assets.findRegionOrMissing(atlas, "main_menu_button"));
        content.add(mainMenuButton).size(200, 60).pad(10).row();
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.getRoot().setTouchable(Touchable.disabled);
                dialog = getSaveDialog();
                uiStage.addActor(dialog);
            }
        });


        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    close();
                    return true;
                }
                return false;
            }
        });
    }

    private void close() {
        uiStage.getRoot().setTouchable(Touchable.disabled);
        title.addAction(sequence(moveBy(0, 150, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(0, -150, 0.5f)));
        resumeButton.addAction(sequence(moveBy(-600, 500, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(600, -500, 0.5f)));
        settingsButton.addAction(sequence(moveBy(600, 500, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(-600, -500, 0.5f)));
        mainMenuButton.addAction(sequence(moveBy(-600, 500, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(600, -500, 0.5f)));
        content.addAction(sequence(moveBy(0, -500, 0.5f, Interpolation.smoother), delay(0.1f), moveBy(0, 500)));

        if (dialog != null) {
            dialog.remove();
            dialog = null;
        }

        uiStage.addAction(sequence(fadeOut(0.5f), run(() -> world.setPaused(false))));
    }

    /// muss aufgerufen werden bevor das PauseOverlay sichtbar wird
    public void show() {
        world.getInputProcessor().addProcessor(0, uiStage);

        title.addAction(sequence(moveBy(0, 150), moveBy(0, -150, 0.5f, Interpolation.smoother)));
        resumeButton.addAction(sequence(moveBy(-600, 500), moveBy(600, -500, 0.5f, Interpolation.smoother)));
        settingsButton.addAction(sequence(moveBy(600, 500), moveBy(-600, -500, 0.5f, Interpolation.smoother)));
        mainMenuButton.addAction(sequence(moveBy(-600, 500), moveBy(600, -500, 0.5f, Interpolation.smoother)));
        content.addAction(sequence(moveBy(0, -500), moveBy(0, 500, 0.5f, Interpolation.smoother), run(() -> uiStage.getRoot().setTouchable(Touchable.enabled))));

        uiStage.addAction(sequence(fadeIn(0.5f)));

        visible = true;
    }

    /// Zeichnet und updated das Overlay
    /// show sollte vorher aufgerufen worden sein
    public void render(float ignoredDelta) {
        if (!visible) throw new IllegalStateException("PauseOverlay.render() called without show()");
        uiStage.getViewport().apply();
        uiStage.draw();
    }

    public void act(float delta) {
        uiStage.act(delta);
    }

    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    /// Hide sollte aufgerufen werden wenn das PauseOverlay geschlossen wird.
    public void hide() {
        world.getInputProcessor().removeProcessor(uiStage);
        visible = false;
    }

    @Override
    public void dispose() {
        uiStage.dispose();
    }

    private Table getSaveDialog() {
        Table dialogOverlay = new Table();
        dialogOverlay.setFillParent(true);

        dialogOverlay.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.TOWER_SELECTION_ATLAS), "background")).tint(new Color(0, 0, 0, 0.2f)));

        Table dialogBox = new Table();
        dialogBox.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS), "background_banner")));

        dialogBox.setSize(300, 300);
        dialogBox.setPosition((800 - 300 - DIALOG_PADDING) / 2f, (480 - 300) / 2f);

        dialogBox.setOrigin(Align.center);
        dialogBox.setTransform(true);
        dialogBox.addAction(sequence(scaleTo(0f, 0f), scaleTo(1f, 1f, 0.5f, Interpolation.smoother), run(() -> uiStage.getRoot().setTouchable(Touchable.enabled))));

        dialogOverlay.addActor(dialogBox);

        Image saveText = new Image(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS), "save_text"));
        saveText.setOrigin(Align.center);

        Image yesButton = new Image(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS), "yes_button"));

        Image noButton = new Image(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS), "no_button"));

        yesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.getRoot().setTouchable(Touchable.disabled);
                world.saveGame();
                close();
                world.close();
            }
        });

        noButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.getRoot().setTouchable(Touchable.disabled);
                close();
                world.close();
            }
        });

        dialogBox.add(saveText).size(200, 70).pad(10).row();
        dialogBox.add(yesButton).size(160, 50).pad(10).row();
        dialogBox.add(noButton).size(160, 50).pad(10);

        return dialogOverlay;
    }

    private Table getSettingsDialog() {
        Table dialogOverlay = new Table();
        dialogOverlay.setFillParent(true);

        dialogOverlay.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.TOWER_SELECTION_ATLAS), "background")).tint(new Color(0, 0, 0, 0.2f)));

        Table dialogBox = new Table();
        dialogBox.setBackground(new TextureRegionDrawable(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS), "background_banner")));

        dialogBox.setSize(450, 350);
        dialogBox.setPosition((800 - 450 - DIALOG_PADDING) / 2f, (480 - 350) / 2f);

        dialogBox.setOrigin(Align.center);
        dialogBox.setTransform(true);
        dialogBox.addAction(sequence(scaleTo(0f, 0f), scaleTo(1f, 1f, 0.5f, Interpolation.smoother), run(() -> uiStage.getRoot().setTouchable(Touchable.enabled))));

        dialogOverlay.addActor(dialogBox);

        Image settingsText = new Image(Assets.findRegionOrMissing(PenguTD.getInstance().getAssetManager().get(Assets.PAUSE_SCREEN_ATLAS), "settings_text"));
        settingsText.setOrigin(Align.center);

        Table soundSlider = createSlider("Sound Volume", Settings.get().getSoundVolume(), v -> Settings.get().setSoundVolume(v));

        Table musicSlider = createSlider("Music Volume", Settings.get().getMusicVolume(), v -> Settings.get().setMusicVolume(v));

        Table fullscreenButton = createFullscreenButton();

        ImageButton backButton = createBackButton();

        dialogBox.add(settingsText).size(200, 40).pad(10).padTop(20).row();
        dialogBox.add(musicSlider).width(400).height(70).row();
        dialogBox.add(soundSlider).width(400).height(70).row();
        dialogBox.add(fullscreenButton).width(400).height(70).row();
        dialogBox.addActor(backButton);

        return dialogOverlay;
    }

    private ImageButton createBackButton() {
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(settingsAtlas, "back_button")));
        backButton.setSize(50, 50);
        backButton.setPosition(60, 250);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.getRoot().setTouchable(Touchable.disabled);
                assert dialog != null;
                dialog.getChild(0).addAction(sequence(  // dialog.getChild(0) ist die dialogBox
                    parallel(fadeOut(0.5f), scaleTo(0, 0, 0.5f, Interpolation.smoother)), run(() -> {
                        uiStage.getRoot().setTouchable(Touchable.enabled);
                        dialog.remove();
                        dialog = null;
                    })));
            }
        });

        return backButton;
    }

    private Table createSlider(String labelName, float initialValue, SettingsScreen.SliderCallback callback) {
        Table content = new Table();
        content.pad(5);

        Skin skin = PenguTD.getInstance().getAssetManager().get(Assets.DEFAULT_SKIN);
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
        content.add(slider).width(300);

        return content;
    }

    private Table createFullscreenButton() {
        Table content = new Table();
        Skin skin = PenguTD.getInstance().getAssetManager().get(Assets.DEFAULT_SKIN);

        Label fullscreenLabel = new Label("Fullscreen: " + (Settings.get().getFullScreen() ? "On" : "Off"), skin);

        content.add(fullscreenLabel);

        content.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean newValue = !Settings.get().getFullScreen();

                Settings.get().setFullScreen(newValue);

                if (newValue) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(800, 480);
                }

                fullscreenLabel.setText("Fullscreen: " + (newValue ? "On" : "Off"));
            }
        });
        return content;
    }

}
