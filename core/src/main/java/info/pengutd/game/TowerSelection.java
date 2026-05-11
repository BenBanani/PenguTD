package info.pengutd.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;
import info.pengutd.Assets;
import info.pengutd.PenguTD;

public class TowerSelection implements Disposable {
    private final Stage uiStage;
    private final TextureAtlas atlas;
    private final World world;
    private Label moneyLabel;
    private Label hpLabel;

    public TowerSelection(World world) {
        uiStage = new Stage(world.getViewport());
        this.world = world;

        atlas = PenguTD.getInstance().getAssetManager().get(Assets.TOWER_SELECTION_ATLAS);
        // table auf ganzem Screen
        Table root = new Table();
        root.setFillParent(true);

        // sidebar table
        Table sidebar = new Table();
        sidebar.top();
        sidebar.setBackground(
            new TextureRegionDrawable(Assets.findRegionOrMissing(atlas, "background")).tint(new Color(1, 1, 1, 0.6f))  // 40% transparent
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

        sidebar.add(pauseButton()).width(180).height(80).colspan(2).row();

        uiStage.addActor(root);
    }

    public Stage getStage() {
        return uiStage;
    }

    private Stack towerElement(int i) {
        Stack stack = new Stack();
        Image buttonBackground = new Image(Assets.findRegionOrMissing(atlas, "button_blue"));
        buttonBackground.getColor().a = 0.9f;
        buttonBackground.setScaling(Scaling.stretch);
        stack.add(buttonBackground);

        // Table, weil stack alle inhalte immer auf volle größe erweitert
        Table content = new Table().center();
        Image image = new Image(Assets.findRegionOrMissing(atlas, "tower" + i));
        content.add(image).width(40f).height(40f);
        stack.add(content);

        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.setSelectedTower(i);
            }
        });

        return stack;
    }

    private Actor pauseButton() {
        Stack stack = new Stack();
        Image buttonBackground = new Image(Assets.findRegionOrMissing(atlas, "button_blue"));
        buttonBackground.getColor().a = 0.9f;
        buttonBackground.setScaling(Scaling.stretch);
        stack.add(buttonBackground);

        Table content = new Table().center();
        Label textLabel = new Label("Pause", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        content.add(textLabel);
        stack.add(content);

        stack.setTouchable(Touchable.enabled);
        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.pause();
            }
        });

        return stack;
    }

    /// das blaue rechteck oben, in dem Geld und Hp angezeigt werden
    private Stack topElement() {
        Stack stack = new Stack();
        Image topBackground = new Image(Assets.findRegionOrMissing(atlas, "button_blue"));
        topBackground.getColor().a = 0.9f;
        topBackground.setScaling(Scaling.stretch);

        Table topContent = new Table();
        topContent.add(new Image(Assets.findRegionOrMissing(atlas, "money"))).size(40f, 40f).pad(5);
        moneyLabel = new Label("" + world.getMoney(), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        topContent.add(moneyLabel).row();

        topContent.add(new Image(Assets.findRegionOrMissing(atlas, "hp"))).size(40f, 40f).pad(5);
        hpLabel = new Label("" + world.getHp(), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        topContent.add(hpLabel).row();

        stack.add(topBackground);
        stack.add(topContent);
        return stack;
    }

    public void updateTopElement() {
        moneyLabel.setText("" + world.getMoney());
        hpLabel.setText("" + world.getHp());
    }

    public void render(float delta) {
        uiStage.act(delta);
        uiStage.draw();
    }

    public void dispose() {
        uiStage.dispose();
    }
}
