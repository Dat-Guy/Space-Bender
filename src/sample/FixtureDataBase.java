package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;

public class FixtureDataBase {

    private PolygonShape physicsShape;

    public FixtureDataBase(PolygonShape shape) {
        physicsShape = shape;
    }

    public void draw(GraphicsContext gc, float scale, Color color) {
        double[] x = new double[physicsShape.getVertexCount()];
        double[] y = new double[physicsShape.getVertexCount()];

        for (int i = 0; i < x.length; i++) {
            x[i] = physicsShape.getVertex(i).x * scale;
            y[i] = physicsShape.getVertex(i).y * scale;
        }
        gc.setStroke(color);
        gc.setLineWidth(1);
        gc.strokePolygon(x, y, x.length);
    }
}
