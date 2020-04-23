package base.bodies;

import base.fixtures.FixtureDataBase;
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

    protected FixtureTilemap map;
    protected Color color;
    protected ArrayList<Fixture> toDestroy;

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

    public boolean checkTile(int x, int y) {
        return map.checkTile(x, y);
    }

    public void addCollided(@NotNull Fixture f) {
        toDestroy.add(f);
    }

    public void handleCollided(@NotNull Body self) {
        for (Fixture f : toDestroy) {
            f.m_userData = null;
            self.destroyFixture(f);
        }
        toDestroy.clear();
    }

    protected class FixtureTilemap {

        private ArrayList<ArrayList<Fixture>> tilemap;

        private int centerTileX;
        private int centerTileY;

        private int mapWidth;
        private int mapHeight;

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

            mapWidth = Math.max(mapWidth, centerTileX + x + 1);
            mapHeight = Math.max(mapHeight, centerTileY + y + 1);

            while (tilemap.size() < mapWidth) {
                tilemap.add(new ArrayList<>());
            }
            for (ArrayList<Fixture> row : tilemap) {
                while (row.size() < mapHeight) {
                    row.add(null);
                }
            }

            tilemap.get(centerTileX + x).set(centerTileY + y, f);
        }

        public boolean checkTile(int x, int y) {

            //If outside the tile grid, or if the tile is null (never assigned) or its body is null (destroyed fixture),
            // return false, otherwise true
            try {
                if (centerTileX + x < 0 || centerTileY + y < 0 || centerTileX + x >= mapWidth || centerTileY + y >= mapHeight) {
                    return false;
                } else if (tilemap.get(centerTileX + x).get(centerTileY + y) == null || tilemap.get(centerTileX + x).get(centerTileY + y).m_body == null) {
                    tilemap.get(centerTileX + x).set(centerTileY + y, null);
                    return false;
                }
                return true;
            } catch(IndexOutOfBoundsException e) {
                tilemap.forEach(fixtures -> System.out.print(fixtures.size() + ","));
                throw new Error("\nAttempted to access element out of range for 2d array.\n" + e.getMessage());
            }

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
