package net.johnbrooks.xen.engine.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import net.johnbrooks.xen.engine.entities.Entity;
import net.johnbrooks.xen.engine.manager.screens.ScreenComponent;
import net.johnbrooks.xen.engine.manager.screens.ScreenManager;

import java.util.ArrayList;
import java.util.List;

public class TiledEntitySelector {

    protected ScreenComponent screenComponent;
    protected Rectangle selectionRectangle;
    protected ShapeRenderer shapeRenderer;
    protected boolean drawing, disabled;
    protected Stage stage;
    protected EventListener eventListener;

    protected List<Entity> selectedEntities;

    public TiledEntitySelector(ScreenComponent _screenComponent) {
        screenComponent = _screenComponent;
        stage = screenComponent.getStage();
        selectedEntities = new ArrayList<>();
        selectionRectangle = new Rectangle() {
            public boolean overlaps (Rectangle r) {
                return Math.min(x, x + width) < Math.max(r.x, r.x + r.width) && Math.max(x, x + width) > Math.min(r.x, r.x + r.width) && Math.min(y, y + height) < Math.max(r.y, r.y + r.height) && Math.max(y, y + height) > Math.min(r.y, r.y + r.height);
            }
        };
        shapeRenderer = new ShapeRenderer();
        eventListener = getEventListener();
        stage.addListener(eventListener);
    }

    public Rectangle getSelectionRectangle() {
        return selectionRectangle;
    }

    public void render(SpriteBatch spriteBatch) {
        if (!disabled) {
            if (drawing) {
                spriteBatch.begin();
                // Allow transparency in draw
                Gdx.gl.glEnable(GL20.GL_BLEND);
                // Draw a green filled rectangle within the selection of the mouse.
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(0f, 1f, 0f, 0.3f);
                shapeRenderer.rect(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
                shapeRenderer.end();
                // Disable transparency before leaving function
                Gdx.gl.glDisable(GL20.GL_BLEND);
                spriteBatch.end();
            }
        }
    }

    public InputListener getEventListener() {
        return new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Do nothing is we are clicking on a UIComponent
                if (screenComponent.getUi().isHoveringOverComponent()) {
                    return true;
                }

                // If we left click, reset selection's width and height and set the x and y to where clicked.
                if (event.getButton() == Input.Buttons.LEFT) {
                    selectionRectangle.x = x;
                    selectionRectangle.y = y;
                    selectionRectangle.width = 0;
                    selectionRectangle.height = 0;

                    // Set our selection to draw
                    drawing = true;

                    // Stop Selecting units

                    selectedEntities.clear();
                    selectFirstEntity();

                } else {
                    moveSelectedEntitiesToClickedPosition(event, x, y);
                }

                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                updateAndDrawDraggedRectangle(event, x, y, pointer);
                updateEntityList();
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // Do nothing is we are clicking on a UIComponent
                if (screenComponent.getUi().isHoveringOverComponent()) {
                    super.touchUp(event, x, y, pointer, button);
                    return;
                }

                // Finalize the width and height of selection.
                if (event.getButton() == Input.Buttons.LEFT) {
                    selectionRectangle.width = x - selectionRectangle.x;
                    selectionRectangle.height = y - selectionRectangle.y;

                    // Stop drawing the rectangle.
                    drawing = false;
                }

                super.touchUp(event, x, y, pointer, button);
            }
        };
    }

    protected void updateAndDrawDraggedRectangle(InputEvent event, float x, float y, int pointer) {
        // Set the width depending on where the mouse is dragged.
        if (drawing) {
            selectionRectangle.width = x - selectionRectangle.x;
            selectionRectangle.height = y - selectionRectangle.y;
        }
    }

    protected void updateEntityList() {
        selectedEntities.clear();
        for (int i = 0; i < ScreenManager.getActive().getEntityList().size(); i++) {
            Entity entity = ScreenManager.getActive().getEntityList().get(i);
            if (entity.isSelectable() && entity.isMultipleSelection() && selectionRectangle.overlaps(entity.getRectangle())) {
                entity.setSelected(true);
                selectedEntities.add(entity);
            } else {
                entity.setSelected(false);
            }
        }
    }

    protected void moveSelectedEntitiesToClickedPosition(InputEvent event, float x, float y) {
        // Update all selected units target locations.
        for (Entity entity : ScreenManager.getActive().getEntityList()) {
            if (!entity.isImmobile() && entity.isSelected()) {
                // GET WORLD POS FROM X Y
                Vector3 worldPosition = ScreenManager.getActive().getCamera().unproject(new Vector3(x, screenComponent.getCamera().viewportHeight - y, 0));
                entity.setTargetPath(new Vector2(worldPosition.x, worldPosition.y));
            }
        }
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        if (disabled == !this.disabled) {
            stage.removeListener(eventListener);
        } else if (!disabled && this.disabled) {
            stage.addListener(eventListener);
        }

    }

    public List<Entity> getSelectedEntities() {
        return selectedEntities;
    }

    public boolean isDisabled() {
        return disabled;
    }

    protected void selectFirstEntity() {
        boolean hasSelectedEntity = false;
        for (int i = 0; i < ScreenManager.getActive().getEntityList().size(); i++) {
            Entity entity = ScreenManager.getActive().getEntityList().get(i);
            // Select a unit if we are clicking them. Only allow 1 to be selected.
            entity.setSelected(false);

            if (!hasSelectedEntity) {
                if (entity.isSelectable() && entity.isMultipleSelection() && selectionRectangle.overlaps(entity.getRectangle())) {
                    entity.setSelected(true);
                    selectedEntities.add(entity);
                    hasSelectedEntity = true;
                }
            }
        }
    }
}
