package net.johnbrooks.xen.engine.manager.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

import java.io.File;
import java.util.HashMap;

public class AssetManager implements Disposable {

    private com.badlogic.gdx.assets.AssetManager manager;
    private int lastProgress;
    private HashMap<String, String> assetPaths;

    public AssetManager() {
        manager = new com.badlogic.gdx.assets.AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetPaths = new HashMap<>();
        lastProgress = -1;
    }

    public void add(Asset... assets) {
        for (Asset asset : assets) {
            if (!assetPaths.containsKey(asset.name)) {
                Gdx.app.log(manager.getClass().getSimpleName(), "Preparing asset load for " + asset.name);
                assetPaths.put(asset.name, asset.path);
                manager.load(asset.path, asset.type);
            } else {
                Gdx.app.log(manager.getClass().getSimpleName(), "Asset has already been loaded for " + asset.name);
            }
        }
    }

    public Object get(String name, Class c) {
        if (assetPaths.containsKey(name)) {
            String path = assetPaths.get(name);
            return c.cast(manager.get(path, c));
        }
        return manager.get(name);
    }

    public boolean load() {
        while (!manager.update()) {
            int progress = (int) (manager.getProgress() * 100);
            if (lastProgress != progress) {
                Gdx.app.log(getClass().getSimpleName(), "Loading assets " + (progress) + "%");
                lastProgress = progress;
            }
        }
        Gdx.app.log(getClass().getSimpleName(), "Loading assets 100%");
        Gdx.app.log(getClass().getSimpleName(), "All assets loaded successfully.");
        return true;
    }

    public boolean loadTick() {
        if (!manager.update()) {
            int progress = (int) (manager.getProgress() * 100);
            Gdx.app.log(getClass().getSimpleName(), "Loading assets " + (progress) + "%");
            lastProgress = progress;
            return false;
        } else {
            Gdx.app.log(getClass().getSimpleName(), "Loading assets 100%");
            Gdx.app.log(getClass().getSimpleName(), "All assets loaded successfully.");
            return true;
        }
    }

    public void loadAllInDirectory(String path, Class type) {
        File directory = new File(path);
        if (!directory.isDirectory()) {
            Gdx.app.error(getClass().getSimpleName(), "Path does not exist for loading " + path);
        } else {
            loadAllInDirectory(directory, type);
        }
    }

    private void loadAllInDirectory(File directory, Class type) {
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                Asset asset = new Asset(file.getName().replaceFirst("[.][^.]+$", ""), file.getPath().replace(File.separatorChar, '/'), type);
                add(asset);
            }
        }
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
