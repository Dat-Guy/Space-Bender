package base.bodies;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BodyDataBase {

    protected Color color;
    protected Map<Fixture, Fixture> toDestroy;

    public BodyDataBase() {
        color = new Color(Math.max(Math.random(), 0.3), Math.max(Math.random(), 0.3), Math.max(Math.random(), 0.3), 0.5);
        toDestroy = new HashMap<>();
    }

    public void draw(Body self, GraphicsContext gc, double scale) {
        gc.save();
        gc.translate(self.getPosition().x * scale, self.getPosition().y * scale);
        gc.rotate(self.getAngle() * 180 / Math.PI);
        gc.setStroke(color);
        gc.setLineWidth(1);
        for (Fixture f = self.m_fixtureList; f != null; f = f.m_next) {
            PolygonShape shape = (PolygonShape) f.m_shape;
            int count = shape.getVertexCount();
            double[] x = new double[count];
            double[] y = new double[count];

            for (int j = 0; j < count; j++) {
                x[j] = shape.getVertex(j).x * scale;
                y[j] = shape.getVertex(j).y * scale;
            }

            gc.strokePolygon(x, y, count);
        }
        gc.restore();
    }

    public void addCollided(@NotNull Fixture fSelf, @NotNull Fixture fOther) {
        toDestroy.put(fSelf, fOther);
    }

    public void handleCollided(@NotNull Body self) {
        for (Fixture f : toDestroy.keySet()) {
            f.m_userData = null;
            self.destroyFixture(f);
        }
        toDestroy.clear();
    }

}
