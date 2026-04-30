package info.pengutd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TowerSelection implements Disposable {
    private final Stage uiStage;
    private final TextureAtlas atlas;

    public TowerSelection(Viewport viewport) {
        uiStage = new Stage(viewport);
        Gdx.input.setInputProcessor(new InputMultiplexer(Gdx.input.getInputProcessor(), uiStage));

        atlas = new TextureAtlas("atlas/tower_selection_ui.atlas");
        // table auf ganzem Screen
        Table root = new Table();
        root.setFillParent(true);

        // sidebar table
        Table sidebar = new Table();
        sidebar.top();
        sidebar.setBackground(
            new TextureRegionDrawable(new TextureRegion(atlas.findRegion("background"))).tint(new Color(1, 1, 1, 0.6f))  // 40% transparent
        );
        sidebar.defaults().growX().pad(10);

        // linker Bereich (spiel)
        root.add().expandX().growY();
        // rechte Sidebar
        root.add(sidebar).width(200).growY();

        Stack topElement = topElement();
        sidebar.add(topElement).colspan(2).width(180).height(180).padBottom(10).row();

        sidebar.add(towerElement(1)).width(80).height(80);
        sidebar.add(towerElement(2)).width(80).height(80).row();
        sidebar.add(towerElement(3)).width(80).height(80);
        sidebar.add(towerElement(4)).width(80).height(80).row();
        sidebar.add(towerElement(5)).width(80).height(80);
        sidebar.add(towerElement(6)).width(80).height(80).row();
        uiStage.addActor(root);
    }

    private Stack towerElement(int i) {
        Stack stack = new Stack();
        Image buttonBackground = new Image(atlas.findRegion("button_blue"));
        buttonBackground.getColor().a = 0.9f;
        buttonBackground.setScaling(Scaling.stretch);
        stack.add(buttonBackground);

        Image image = new Image(atlas.findRegion("tower" + i));
        image.setSize(50, 50);
        stack.add(image);
        return stack;
    }

    /// das blaue rechteck oben, in dem Geld und Hp angezeigt werden
    private Stack topElement() {
        Stack stack = new Stack();
        Image topBackground = new Image(atlas.findRegion("button_blue"));
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
        atlas.dispose();
    }
}
