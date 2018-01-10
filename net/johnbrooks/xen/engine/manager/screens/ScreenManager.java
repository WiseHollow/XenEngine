package net.johnbrooks.xen.engine.manager.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

public class ScreenManager {

    private static ScreenComponent activeScreen = null;
    private static ScreenComponent pendingScreen = null;
    private static HashMap<String, ScreenComponent> screens = new HashMap<String, ScreenComponent>();

    public static ScreenComponent add(String screenName, ScreenComponent screenComponent) {
        if (screens.containsKey(screenName)) {
            return null;
        } else {
            screens.put(screenName, screenComponent);
            return screenComponent;
        }
    }

    public static boolean remove(String screenName) {
        ScreenComponent screenComponent = get(screenName);
        if (screenComponent == null) {
            Gdx.app.error("ScreenManager", "Cannot find screen to remove.");
            return false;
        } else if (activeScreen == screenComponent) {
            Gdx.app.error("ScreenManager", "Cannot remove an active screen.");
            return false;
        } else {
            Gdx.app.log("ScreenManager", "Removed and cleaned up " + screenName + " screen");
            screenComponent.dispose();
            screens.remove(screenName);
            return true;
        }
    }

    public static boolean remove(ScreenComponent screenComponent) {
        for (String name : screens.keySet()) {
            if (screens.get(name) == screenComponent) {
                Gdx.app.log("ScreenManager", "Removed and cleaned up " + name + " screen");
                screenComponent.dispose();
                screens.remove(name);
                return true;
            }
        }

        Gdx.app.error("ScreenManager", "Cannot find screen to remove.");
        return false;
    }

    public static ScreenComponent get(String screenName) {
        return screens.get(screenName);
    }

    private static void applyPendingScreen() {
        if (pendingScreen != null) {
            if (pendingScreen != null) {
                pendingScreen.show();
            }
            if (activeScreen != null) {
                activeScreen.hide();
            }
            activeScreen = pendingScreen;
            pendingScreen = null;
        }
    }

    public static void setActive(ScreenComponent screenComponent) {
        pendingScreen = screenComponent;
    }

    public static ScreenComponent getActive() {
        return activeScreen;
    }

    public static void update(float deltaTime) {
        applyPendingScreen();
        if (activeScreen != null) {
            activeScreen.update(deltaTime);
        }
    }

    public static void render(float deltaTime, SpriteBatch spriteBatch) {
        if (activeScreen != null) {
            activeScreen.render(deltaTime, spriteBatch);
        }
    }

    public static void dispose() {
        for (ScreenComponent screenComponent : screens.values()) {
            screenComponent.dispose();
        }
    }
}
