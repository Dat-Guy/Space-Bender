package base.bodies;

import base.InputHandler;
import javafx.scene.input.KeyCode;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jetbrains.annotations.NotNull;

public class PlayerBodyData extends BodyDataBase {

    public void doKeyUpdate(@NotNull Body self) {
        if (InputHandler.getKey(KeyCode.UP)) {
            self.applyForceToCenter(new Vec2((float) (100 * Math.sin(self.getAngle())), (float) (-100 * Math.cos(self.getAngle()))));
        }
        if (InputHandler.getKey(KeyCode.DOWN)) {
            self.applyForceToCenter(new Vec2((float) (-100 * Math.sin(self.getAngle())), (float) (100 * Math.cos(self.getAngle()))));
        }
        if (InputHandler.getKey(KeyCode.LEFT)) {
            self.applyForceToCenter(new Vec2((float) (-100 * Math.cos(self.getAngle())), (float) (-100 * Math.sin(self.getAngle()))));
        }
        if (InputHandler.getKey(KeyCode.RIGHT)) {
            self.applyForceToCenter(new Vec2((float) (100 * Math.cos(self.getAngle())), (float) (100 * Math.sin(self.getAngle()))));
        }
    }

    @Override
    public void handleDoomed(@NotNull Body self) {
        toDestroy.clear();
    }
}
