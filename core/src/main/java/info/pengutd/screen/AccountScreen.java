package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;
import info.pengutd.profile.PlayerProfile;
import org.jetbrains.annotations.Nullable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class AccountScreen implements Screen {
    private final Screen previousScreen;
    private Skin skin;
    private TextureAtlas atlas;
    private Stage stage;
    private @Nullable PlayerProfile selectedProfile;
    private ScrollPane scrollPane;
    private Image title;
    private Stack selectButton;
    private Stack deleteButton;
    private Texture bgTexture;
    private ImageButton backButton;

    public AccountScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);
        selectedProfile = PenguTD.getInstance().getProfileManager().getCurrentProfile();

        loadAssets();
        buildUi();
    }

    private void loadAssets() {
        skin = PenguTD.getInstance().getAssetManager().get(Assets.DEFAULT_SKIN);

        bgTexture = PenguTD.getInstance().getAssetManager().get(Assets.UI_BACKGROUND);
        atlas = PenguTD.getInstance().getAssetManager().get(Assets.ACCOUNT_SCREEN_ATLAS);

    }

    private void buildUi() {
        Image background = new Image(bgTexture);
        background.setFillParent(true);
        background.setScaling(Scaling.fill);
        stage.addActor(background);

        Table root = new Table();
        root.setFillParent(true);
        root.pad(20);
        stage.addActor(root);

        title = new Image(Assets.findRegionOrMissing(atlas, "title"));
        root.add(title).colspan(2).width(300).height(108).row();

        scrollPane = createProfilesTable();
        root.add(scrollPane).colspan(2).width(420).height(260).padBottom(20).row();

        selectButton = createSelectButton();
        root.add(selectButton).width(180).height(50).padRight(20).padBottom(20);

        deleteButton = createDeleteButton();
        root.add(deleteButton).width(180).height(50).padLeft(20).padBottom(20);

        backButton = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "back_button")));
        backButton.setSize(50, 50);
        backButton.setPosition(25, stage.getHeight() - 75);

        backButton.setTransform(true);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                backButton.addAction(scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                backButton.addAction(scaleTo(1f, 1f, 0.1f));
            }
        });
        stage.addActor(backButton);

        addAnimations();
    }

    private Stack createSelectButton() {
        Stack stack = new Stack();
        ImageButton button = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "button_bg")));
        button.getImage().setScaling(Scaling.stretch);
        stack.add(button);
        Label label = new Label("Select Profile", skin);
        label.setAlignment(Align.center);
        stack.add(label);
        stack.setTouchable(Touchable.enabled);
        stack.setTransform(true);
        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                assert selectedProfile != null;
                PenguTD.getInstance().getProfileManager().selectProfile(selectedProfile);
                updateCards((Table) scrollPane.getChild(0));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stack.addAction(scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                stack.addAction(scaleTo(1f, 1f, 0.1f));
            }
        });
        return stack;
    }

    private Stack createDeleteButton() {
        Stack stack = new Stack();
        ImageButton button = new ImageButton(new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "button_bg")));
        button.getImage().setScaling(Scaling.stretch);
        stack.add(button);
        Label label = new Label("Delete Profile", skin);
        label.setAlignment(Align.center);
        stack.add(label);
        stack.setTouchable(Touchable.enabled);
        stack.setTransform(true);
        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                assert selectedProfile != null;
                PenguTD.getInstance().getProfileManager().deleteProfile(selectedProfile);
                updateCards((Table) scrollPane.getChild(0));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stack.addAction(scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                stack.addAction(scaleTo(1f, 1f, 0.1f));
            }
        });
        return stack;
    }

    private void addAnimations() {
        backButton.addAction(sequence(moveBy(-100, 0), moveBy(100, 0, 0.5f, Interpolation.smoother)));
        scrollPane.addAction(sequence(moveBy(800, 0), moveBy(-800, 0, 0.5f, Interpolation.smoother)));
        title.addAction(sequence(moveBy(0, 200), moveBy(0, -200, 0.5f, Interpolation.smoother)));
        deleteButton.addAction(sequence(moveBy(0, -100), moveBy(0, 100, 0.5f, Interpolation.smoother)));
        selectButton.addAction(sequence(moveBy(0, -100), moveBy(0, 100, 0.5f, Interpolation.smoother)));
    }

    private ScrollPane createProfilesTable() {
        Table list = new Table();
        list.top();
        list.pad(10);

        buildList(list);

        ScrollPane pane = new ScrollPane(list, skin);
        pane.setScrollingDisabled(true, false);
        pane.setScrollbarsOnTop(true);
        pane.setForceScroll(false, false);
        pane.setTouchable(Touchable.enabled);
        pane.setScrollbarsVisible(false);
        pane.getStyle().background = null;
        pane.setColor(new Color(1f, 1f, 1f, 0.8f));
        stage.setScrollFocus(pane);

        return pane;
    }

    private void updateCards(Table list) {
        list.clearChildren();

        buildList(list);
    }

    private void buildList(Table list) {
        TextureRegion bg = Assets.findRegionOrMissing(atlas, "button_bg");

        PenguTD.getInstance().getProfileManager().getProfiles().forEach(profile -> {
            Table card = new Table();
            card.setBackground(new TextureRegionDrawable(bg));
            card.pad(10);
            card.left();

            Label name = new Label(profile.getName(), skin);
            name.setFontScale(1.1f);
            name.setTouchable(Touchable.disabled);

            Label selectText = new Label(profile.equals(PenguTD.getInstance().getProfileManager().getCurrentProfile()) ? "Selected" : "Tap to select", skin);
            selectText.setColor(Color.GRAY);
            selectText.setTouchable(Touchable.disabled);

            if (profile.equals(selectedProfile)) {
                card.setColor(0.8f, 1f, 0.8f, 1f);
            } else {
                card.setColor(Color.WHITE);
            }

            card.add(name).left().padLeft(20).row();
            card.add(selectText).padLeft(20).left();

            card.setTouchable(Touchable.enabled);
            card.setTransform(true);

            card.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedProfile = profile;

                    updateCards(list);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    card.addAction(scaleTo(1.05f, 1.05f, 0.1f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    card.addAction(scaleTo(1f, 1f, 0.1f));
                }
            });

            list.add(card).width(380).height(75).padBottom(10).row();
        });
    }


    private void close() {
        stage.getRoot().setTouchable(Touchable.disabled);
        backButton.addAction(moveBy(-100, 0, 0.5f, Interpolation.smoother));
        scrollPane.addAction(moveBy(800, 0, 0.5f, Interpolation.smoother));
        title.addAction(moveBy(0, 200, 0.5f, Interpolation.smoother));
        selectButton.addAction(moveBy(0, -100, 0.5f, Interpolation.smoother));
        deleteButton.addAction(moveBy(0, -100, 0.5f, Interpolation.smoother));

        backButton.addAction(sequence(delay(0.5f), run(() -> {
            PenguTD.getInstance().getProfileManager().saveProfiles();
            PenguTD.getInstance().setScreenAndDispose(previousScreen);
        })));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        scrollPane.setScrollbarsVisible(false);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
    }
}
