package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TowerSelection implements Disposable {
    private final Stage uiStage;
    private final Texture backgroundTexture;
    private final Texture topButtonTexture;

    public TowerSelection(Viewport viewport) {
        uiStage = new Stage(viewport);
        Gdx.input.setInputProcessor(new InputMultiplexer(Gdx.input.getInputProcessor(), uiStage));

        backgroundTexture = new Texture(Gdx.files.internal("game/tower_selection_screen/background.png"));
        topButtonTexture = new Texture(Gdx.files.internal("game/tower_selection_screen/button_blue.png"));

        // table auf ganzem Screen
        Table root = new Table();
        root.setFillParent(true);

        // sidebar table
        Table sidebar = new Table();
        sidebar.top();
        sidebar.setBackground(
            new TextureRegionDrawable(new TextureRegion(backgroundTexture)).tint(new Color(1, 1, 1, 0.6f))  // 40% transparent
        );
        sidebar.defaults().growX().pad(10);

        // linker Bereich (spiel)
        root.add().expandX().growY();
        // rechte Sidebar
        root.add(sidebar).width(200).growY();

        Stack stack = topElement();
        sidebar.add(stack).width(180).height(180).padBottom(10);
        sidebar.row();
        uiStage.addActor(root);
    }

    /// das blaue rechteck oben, in dem Geld und Hp angezeigt werden
    private Stack topElement() {
        Stack stack = new Stack();
        Image topBackground = new Image(topButtonTexture);
        topBackground.getColor().a = 0.9f;
        topBackground.setScaling(Scaling.stretch);
        Table topContent = new Table();
        topContent.add(new Label("Money", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).pad(10).row();
        topContent.add(new Label("HP", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).pad(10);
        stack.add(topBackground);
        stack.add(topContent);
        return stack;
    }

    public void render(float delta) {
        uiStage.act(delta);
        uiStage.draw();
    }

    public void dispose() {
        uiStage.dispose();
        backgroundTexture.dispose();
    }
}
