package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private Viewport viewport;
    private Character player;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("bg_place.png");
        image.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        viewport = new ScreenViewport();
        player = new Character(200, 100);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();
        player.update(Gdx.graphics.getDeltaTime());

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(image, 
            0, 0,
            viewport.getWorldWidth(), viewport.getWorldHeight(),
            0, 0,
            (int)viewport.getWorldWidth(), (int)image.getHeight(),
            false, false
        );
        player.render(batch);   
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        player.dispose();
    }
}