package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture background;

    private Viewport viewport;

    private Sound coinSound;

    private boolean coinAlive = true;

    private Character player;
    private Coin coin;

    @Override
    public void create() {

        Animations.load();

        coinSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/coin_play.mp3"));


        batch = new SpriteBatch();
        background = new Texture("bg_place.png");

        viewport = new ScreenViewport();

        player = new Character(200, 100);
        coin = new Coin(500, 250);
    }

    @Override
    public void render() {

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        float delta = Gdx.graphics.getDeltaTime();

        // обновление логики
        player.update(delta);
        coin.update(delta);

        if (coinAlive && isColliding(player, coin)) {
            coinAlive = false;
            coinSound.play();
        }

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        // фон
        batch.draw(
            background,
            0,
            0,
            viewport.getScreenWidth(),
            viewport.getScreenHeight()
        );

        // монета (только если жива)
        if (coinAlive) {
            coin.render(batch);
        }

        // игрок
        player.render(batch);

        batch.end();
    }

    private boolean isColliding(Character player, Coin coin) {

        return player.getX() < coin.getX() + coin.getWidth() &&
            player.getX() + player.getWidth() > coin.getX() &&
            player.getY() < coin.getY() + coin.getHeight() &&
            player.getY() + player.getHeight() > coin.getY();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        Animations.dispose();
        player.dispose();
        coinSound.dispose();
    }
}
