package io.github.some_example_name;
import com.badlogic.gdx.graphics.Texture;
class Animations {
    public static Texture coinTexture;
    public static void load() {
        coinTexture = new Texture("coin.png");
        coinTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
    }
    public static void dispose() {
        if (coinTexture != null) {
            coinTexture.dispose();
        }
    }
}
