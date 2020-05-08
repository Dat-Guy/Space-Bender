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
import org.jbox2d.dynamics.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ShipBodyData extends TiledBodyDataBase {

    float[] thrustForce; // Values should be interpreted CCW starting to the top of the ship when facing up.
    private HashMap<Fixture, Body> toAdd = new HashMap<>();

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

        f.m_userData = new ShipFixtureData(shape, part, rotation, new Vec2(x, y));
        map.addFixture(f, x, y);

        if (part == GameData.shipParts.THRUSTER) {
            thrustForce[rotation] += 150.0;
        }

        return f;
    }

    @Override
    public void addCollided(@NotNull Fixture fSelf, @NotNull Fixture fOther) {
        if (fOther.m_userData != null && AsteroidFixtureData.class.isAssignableFrom(fOther.m_userData.getClass())) {
            ((ShipFixtureData) fSelf.m_userData).addLoot(((AsteroidFixtureData) fOther.m_userData).getLoot());
        }
        if (PlayerBodyData.class.isAssignableFrom(fOther.m_body.m_userData.getClass()) && ((ShipFixtureData) fSelf.m_userData).getPart() == GameData.shipParts.SEAT) {
            ((PlayerBodyData) fOther.m_body.m_userData).seat(fSelf);
            toAdd.put(fSelf, fOther.m_body);
        }
    }

    public void handleToStore(@NotNull Body self, World world) {
        for (Fixture f : toAdd.keySet()) {
            ((ShipFixtureData) f.m_userData).setContainedEntity(self, world, toAdd.get(f));
        }
        toAdd.clear();
    }

    @Override
    public void handleDoomed(@NotNull Body self) {
        for (Fixture f : toDestroy.keySet()) {
            if (((ShipFixtureData) f.m_userData).getPart() == GameData.shipParts.THRUSTER) {
                thrustForce[((ShipFixtureData) f.m_userData).getRotation()] -= 150.0;
            }
            f.m_userData = null;
            self.destroyFixture(f);
        }
        toDestroy.clear();
    }

    public void handleInput(@NotNull Body self, @NotNull Body player) {
        if (InputHandler.getKey(KeyCode.UP)) {
            self.applyForceToCenter(new Vec2((float) (thrustForce[2] * Math.sin(self.getAngle())), (float) (-thrustForce[2] * Math.cos(self.getAngle()))));
        }
        if (InputHandler.getKey(KeyCode.DOWN)) {
            self.applyForceToCenter(new Vec2((float) (-thrustForce[0] * Math.sin(self.getAngle())), (float) (thrustForce[0] * Math.cos(self.getAngle()))));
        }
        if (InputHandler.getKey(KeyCode.LEFT)) {
            self.applyForceToCenter(new Vec2((float) (-thrustForce[3] * Math.cos(self.getAngle())), (float) (-thrustForce[3] * Math.sin(self.getAngle()))));
        }
        if (InputHandler.getKey(KeyCode.RIGHT)) {
            self.applyForceToCenter(new Vec2((float) (thrustForce[1] * Math.cos(self.getAngle())), (float) (thrustForce[1] * Math.sin(self.getAngle()))));
        }
        if (InputHandler.getKey(KeyCode.SHIFT)) {
            ((ShipFixtureData) ((PlayerBodyData) player.m_userData).getSeat().m_userData).removeContainedEntity();
            ((PlayerBodyData) player.m_userData).unseat();
        }
    }
}
