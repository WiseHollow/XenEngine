package net.johnbrooks.xen.engine.entities.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.johnbrooks.xen.engine.Engine;

public class Animation {

    public static Animation getSpriteAnimation(String nameOfSprite, int width, int height, int index) {
        Texture texture = (Texture) Engine.getAssetManager().get(nameOfSprite, Texture.class);
        TextureRegion[] regions = TextureRegion.split(texture, width, height)[index];
        return new Animation(regions, 0.1f);
    }

    public static Animation getSpriteAnimation(String nameOfSprite, int width, int height, int index, int frames) {
        Texture texture = (Texture) Engine.getAssetManager().get(nameOfSprite, Texture.class);
        TextureRegion[] regions = TextureRegion.split(texture, width, height)[index];
        Animation animation = new Animation(regions, 0.1f);
        animation.totalFrames = frames;
        return animation;
    }

    private TextureRegion[] frames;
    private float time;
    private float delay;
    private int currentFrame, timesPlayed, totalFrames;

    public Animation() {}

    public Animation(TextureRegion[] frames) {
        this.frames = frames;
    }

    public Animation(TextureRegion[] frames, float delay) {
        setFrames(frames, delay);
    }

    public TextureRegion[] getFrames() {
        return frames;
    }

    public void setFrames(TextureRegion[] frames, float delay) {
        this.frames = frames;
        this.delay = delay;
        time = 0;
        currentFrame = 0;
        timesPlayed = 0;
    }

    public void update(float deltaTime) {
        if (delay > 0) {
            time += deltaTime;
            while (time >= delay) {
                step();
            }
        }
    }

    private void step() {
        time -= delay;
        currentFrame++;
        if (currentFrame == frames.length || currentFrame == totalFrames) {
            currentFrame = 0;
            timesPlayed++;
        }
    }

    public TextureRegion getFrame() {
        return frames[currentFrame];
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }
}
