package base.bodies;

import base.InputHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jetbrains.annotations.NotNull;

public class PlayerBodyData extends BodyDataBase {

    private boolean seated;
    private Fixture shipSeat;

    public PlayerBodyData(Color color) {
        super();
        this.color = color;
    }

    public void doKeyUpdate(@NotNull Body self) {
        if (!seated) {
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
        } else {
            ((ShipBodyData) shipSeat.m_body.m_userData).handleInput(shipSeat.m_body, self);
        }
    }

    @Override
    public void handleDoomed(@NotNull Body self) {
        toDestroy.clear();
    }

    public void seat(Fixture seat) {
        if (!seated) {
            shipSeat = seat;
            seated = true;
        }
    }

    public Fixture getSeat() {
        if (seated) {
            return shipSeat;
        } else {
            return null;
        }
    }

    public void unseat() {
        if (seated) {
            shipSeat = null;
            seated = false;
        }
    }

    public boolean isSeated() {
        return seated;
    }

    @Override
    public void draw(Body self, GraphicsContext gc, double scale) {
        //if (!seated) {
            super.draw(self, gc, scale);
        //}
    }
}
