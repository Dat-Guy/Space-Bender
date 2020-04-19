package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BodyDataBase {

    private FixtureTilemap map;
    private Color color;
    private ArrayList<Fixture> toDestroy;

    public BodyDataBase() {
        map = new FixtureTilemap();
        color = new Color(Math.max(Math.random(), 0.3), Math.max(Math.random(), 0.3), Math.max(Math.random(), 0.3), 0.5);
        toDestroy = new ArrayList<>();
    }

    public void draw(Body self, GraphicsContext gc, float scale) {
        gc.save();
        gc.translate(self.getPosition().x * scale, self.getPosition().y * scale);
        gc.rotate(self.getAngle() * 180 / Math.PI);
        map.drawMap(gc, scale);
        gc.restore();
    }

    public Fixture addTile(@NotNull Body self, int x, int y) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f, new Vec2(x, y), 0);

        FixtureDef fD = new FixtureDef();
        fD.density = 1;
        fD.friction = 0.3f;
        fD.restitution = 0.2f;
        fD.shape = shape;

        Fixture f = self.createFixture(fD);

        f.m_userData = new FixtureDataBase(shape);
        map.addFixture(f, x, y);

        return f;
    }

    public void addDoomed(@NotNull Fixture f) {
        toDestroy.add(f);
    }

    public void destroyDoomed(@NotNull Body self) {
        for (Fixture f : toDestroy) {
            f.m_userData = null;
            self.destroyFixture(f);
        }
        toDestroy.clear();
    }

    private class FixtureTilemap {

        private ArrayList<ArrayList<Fixture>> tilemap;

        private int centerTileX;
        private int centerTileY;

        public FixtureTilemap() {
            centerTileX = 0;
            centerTileY = 0;
            tilemap = new ArrayList<>();
        }

        /**
         * Adds a new fixture to the map given its position relative to the center.
         *
         * @param f The fixture to be added to the map
         * @param x The x-index offset from center
         * @param y The y-index offset from center
         */
        public void addFixture(Fixture f, int x, int y) {
            while (centerTileX < -x) {
                tilemap.add(0, new ArrayList<>());
                centerTileX++;
            }
            while (centerTileY < -y) {
                for (ArrayList<Fixture> row : tilemap) {
                    row.add(0, null);
                }
                centerTileY++;
            }
            while (tilemap.size() < centerTileX + x + 1) {
                tilemap.add(new ArrayList<>());
            }
            for (ArrayList<Fixture> row : tilemap) {
                while (row.size() < centerTileY + y + 1) {
                    row.add(null);
                }
            }
            tilemap.get(centerTileX + x).set(centerTileY + y, f);
        }

        public void drawMap(GraphicsContext gc, float scale) {
            for (ArrayList<Fixture> row : tilemap) {
                for (Fixture f : row) {
                    if (f != null){
                        if (f.m_body == null) {
                            f = null;
                        } else if (f.m_userData != null && FixtureDataBase.class.isAssignableFrom(f.m_userData.getClass())) {
                            ((FixtureDataBase) f.m_userData).draw(gc, scale, color);
                        }
                    }
                }
            }
        }
    }
}
