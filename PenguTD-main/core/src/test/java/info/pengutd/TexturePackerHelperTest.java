package info.pengutd;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

class TexturePackerHelperTest {

    @Test
    void main_shouldCallTexturePackerProcessForAllScreens() {

        try (MockedStatic<TexturePacker> mocked = mockStatic(TexturePacker.class)) {

            TexturePackerHelper.main(new String[]{});

            // verify exact calls (order-independent)
            mocked.verify(() ->
                    TexturePacker.process(
                            "assets/start_screen",
                            "assets/atlas",
                            "start_screen_ui"
                    )
            );

            mocked.verify(() ->
                    TexturePacker.process(
                            "assets/settings_screen",
                            "assets/atlas",
                            "settings_screen_ui"
                    )
            );

            mocked.verify(() ->
                    TexturePacker.process(
                            "assets//game/tower_selection_screen",
                            "assets/atlas",
                            "tower_selection_ui"
                    )
            );

            // verify total number of calls
            mocked.verify(() ->
                    TexturePacker.process(anyString(), anyString(), anyString()),
                    times(3)
            );
        }
    }
}