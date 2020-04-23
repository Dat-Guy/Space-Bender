package base.fixtures;

import base.GameData;
import base.InventoryNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;

public class AsteroidFixtureData extends FixtureDataBase {

    private InventoryNode loot;
    private Color color;

    public AsteroidFixtureData(PolygonShape shape, int materialCount) {
        super(shape);
        loot = new InventoryNode();

        double red = 0;
        double green = 0;
        double blue = 0;
        double colorSum = 0;

        for (int i = 0; i < materialCount; i++) {
            double amount = 2 + 8 * Math.random();
            GameData.materials m = GameData.materials.values()[(int) (Math.random() * GameData.materials.values().length)];
            colorSum += amount;
            red += GameData.materialToColor(m).getRed() * amount;
            green += GameData.materialToColor(m).getGreen() * amount;
            blue += GameData.materialToColor(m).getBlue() * amount;

            loot.addMaterials(m, amount);
        }

        color = Color.color(red / colorSum, green / colorSum, blue / colorSum);
    }

    @Override
    public void draw(GraphicsContext gc, float scale, Color color) {
        super.draw(gc, scale, color.interpolate(this.color, 0.8));
    }
}
