package base.bodies;

import base.GameData;
import base.InputHandler;
import base.fixtures.AsteroidFixtureData;
import base.fixtures.ShipFixtureData;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ShipBodyData extends TiledBodyDataBase {

    float[] thrustForce; // Values should be interpreted CCW starting to the right of the ship when facing up.

    public ShipBodyData(Color playerColor) {
        toDestroy = new HashMap<>();
        color = playerColor;
        map = new FixtureTilemap();
        thrustForce = new float[]{0, 0, 0, 0};
    }

    public ShipBodyData(Color playerColor, String map) {
        new ShipBodyData(playerColor);
        // TODO: String to tilemap interpreter.
    }

    public Fixture addPart(@NotNull Body self, int x, int y, GameData.shipParts part, int rotation) {

        assert rotation >= 0 && rotation <= 3;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f, new Vec2(x, y), 0);

        FixtureDef fD = new FixtureDef();
        fD.density = 1;
        fD.friction = 0.3f;
        fD.restitution = 0.2f;
        fD.shape = shape;

        Fixture f = self.createFixture(fD);

        f.m_userData = new ShipFixtureData(shape, part, rotation);
        map.addFixture(f, x, y);

        if (part == GameData.shipParts.THRUSTER) {
            thrustForce[rotation] += 10.0;
        }

        return f;
    }

    @Override
    public void addCollided(@NotNull Fixture fSelf, @NotNull Fixture fOther) {
        if (AsteroidFixtureData.class.isAssignableFrom(fOther.m_userData.getClass())) {
            ((ShipFixtureData) fSelf.m_userData).addLoot(((AsteroidFixtureData) fOther.m_userData).getLoot());
        }
    }

    @Override
    public void handleDoomed(@NotNull Body self) {
        for (Fixture f : toDestroy.keySet()) {
            if (((ShipFixtureData) f.m_userData).getPart() == GameData.shipParts.THRUSTER) {
                thrustForce[((ShipFixtureData) f.m_userData).getRotation()] -= 10.0;
            }
            f.m_userData = null;
            self.destroyFixture(f);
        }
        toDestroy.clear();
    }

    public void handleInput(@NotNull Body self) {
        self.applyForceToCenter(new Vec2(0, thrustForce[1] * (InputHandler.getKey(KeyCode.UP) ? -1 : 0)));
        self.applyForceToCenter(new Vec2(0, thrustForce[3] * (InputHandler.getKey(KeyCode.DOWN) ? 1 : 0)));
        self.applyForceToCenter(new Vec2(0, thrustForce[2] * (InputHandler.getKey(KeyCode.LEFT) ? -1 : 0)));
        self.applyForceToCenter(new Vec2(0, thrustForce[0] * (InputHandler.getKey(KeyCode.RIGHT) ? 1 : 0)));
    }
}
