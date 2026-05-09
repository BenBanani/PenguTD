package info.pengutd.screen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import info.pengutd.PenguTD;

class StartScreenTest {

    @BeforeEach
    void setUp() {
        Gdx.app = mock(Application.class);
        Gdx.input = mock(Input.class);
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = Gdx.gl;
        Gdx.gl30 = null;
    }

    @AfterEach
    void tearDown() throws Exception {
        Gdx.app = null;
        Gdx.input = null;
        Gdx.gl = null;
        Gdx.gl20 = null;
        Gdx.gl30 = null;
        setStaticField(PenguTD.class, "instance", null);
    }

    @Test
    void addAnimations_shouldAttachActionsToTitleAndButtons() throws Exception {
        StartScreen screen = new StartScreen();

        ImageButton[] buttons = createMockButtons();
        Image title = mock(Image.class);

        setPrivateField(screen, "buttons", buttons);
        setPrivateField(screen, "title", title);

        invokePrivateMethod(screen, "addAnimations");

        verify(title, times(1)).addAction(any());
        for (int i = 0; i < buttons.length; i++) {
            verify(buttons[i], times(1)).addAction(any());
        }
    }

    @Test
    void addListeners_shouldRegisterExitListenerOnExitButton() throws Exception {
        StartScreen screen = new StartScreen();

        ImageButton[] buttons = createMockButtons();
        Image title = mock(Image.class);

        setPrivateField(screen, "buttons", buttons);
        setPrivateField(screen, "title", title);

        invokePrivateMethod(screen, "addListeners");

        ArgumentCaptor<ClickListener> captor = ArgumentCaptor.forClass(ClickListener.class);
        verify(buttons[5], times(2)).addListener(captor.capture());

        ClickListener exitListener = captor.getAllValues().get(1);
        InputEvent event = mock(InputEvent.class);

        exitListener.clicked(event, 0f, 0f);

        verify(Gdx.app).exit();
    }

    @Test
    void render_shouldActAndDrawStage() throws Exception {
        StartScreen screen = new StartScreen();
        Stage stage = mock(Stage.class);

        setPrivateField(screen, "stage", stage);

        screen.render(0.16f);

        verify(stage).act(0.16f);
        verify(stage).draw();
    }

    @Test
    void resize_shouldUpdateViewportForPositiveDimensions() throws Exception {
        StartScreen screen = new StartScreen();
        Stage stage = mock(Stage.class);
        Viewport viewport = mock(Viewport.class);

        when(stage.getViewport()).thenReturn(viewport);
        setPrivateField(screen, "stage", stage);

        screen.resize(1024, 768);

        verify(viewport).update(1024, 768, true);
    }

    @Test
    void hide_shouldClearInputProcessor() {
        StartScreen screen = new StartScreen();

        screen.hide();

        verify(Gdx.input).setInputProcessor(null);
    }

    @Test
    void dispose_shouldDisposeStage() throws Exception {
        StartScreen screen = new StartScreen();
        Stage stage = mock(Stage.class);

        setPrivateField(screen, "stage", stage);

        screen.dispose();

        verify(stage).dispose();
    }

    private static ImageButton[] createMockButtons() {
        ImageButton[] buttons = new ImageButton[6];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = mock(ImageButton.class);
        }
        return buttons;
    }

    private static void invokePrivateMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void setStaticField(Class<?> type, String fieldName, Object value) throws Exception {
        Field field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}