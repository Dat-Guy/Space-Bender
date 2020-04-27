package base.bodies;

import base.fixtures.AsteroidFixtureData;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AsteroidBodyData extends TiledBodyDataBase {

    public AsteroidBodyData(){
        map = new FixtureTilemap();
        toDestroy = new HashMap<>();
        color = Color.color(0.2, 0.2, 0.2);
    }

    @Override
    public Fixture addTile(@NotNull Body self, int x, int y) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f, new Vec2(x, y), 0);

        FixtureDef fD = new FixtureDef();
        fD.density = 1;
        fD.friction = 0.3f;
        fD.restitution = 0.2f;
        fD.shape = shape;

        Fixture f = self.createFixture(fD);

        f.m_userData = new AsteroidFixtureData(shape, 5);
        map.addFixture(f, x, y);

        return f;
    }

    @Override
    public void handleCollided(@NotNull Body self) {
        for (Fixture f : toDestroy.keySet()) {
            if (!PlayerBodyData.class.isAssignableFrom(toDestroy.get(f).m_body.m_userData.getClass())) {
                self.destroyFixture(f);
            }
        }
        toDestroy.clear();
    }
}
