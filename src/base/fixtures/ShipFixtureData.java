package base.fixtures;

import base.GameData;
import base.InventoryNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;

public class ShipFixtureData extends FixtureDataBase {

    private GameData.shipParts type;
    private InventoryNode loot;
    private int rotation;

    public ShipFixtureData(PolygonShape shape, GameData.shipParts type, int rotation) {
        super(shape);
        this.type = type;
        this.rotation = rotation;
        this.loot = new InventoryNode();
    }

    @Override
    public void draw(GraphicsContext gc, double scale, Color color) {
        double[] x = new double[physicsShape.getVertexCount()];
        double[] y = new double[physicsShape.getVertexCount()];

        for (int i = 0; i < x.length; i++) {
            x[i] = physicsShape.getVertex(i).x * scale;
            y[i] = physicsShape.getVertex(i).y * scale;
        }
        gc.setFill(GameData.partToColor(type));
        gc.setStroke(color);
        gc.setLineWidth(1);
        gc.fillPolygon(x, y, x.length);
        gc.strokePolygon(x, y, x.length);
    }

    public int getRotation() {
        return rotation;
    }

    public GameData.shipParts getPart() {
        return type;
    }

    public InventoryNode getLoot() {
        return loot;
    }

    public void addLoot(InventoryNode i) {
        loot.takeAll(i, false);
    }
}
