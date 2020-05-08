package base.fixtures;

import base.GameData;
import base.InventoryNode;
import base.bodies.BodyDataBase;
import base.DestructionFlag;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.WeldJointDef;

public class ShipFixtureData extends FixtureDataBase {

    final private WeldJointDef weldJointDef = new WeldJointDef();

    private GameData.shipParts type;
    private InventoryNode loot;
    private int rotation;
    private Body containedEntity;
    private Joint entityConnection;

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
        gc.setStroke(GameData.partToColor(type).invert());
        gc.setLineWidth(2);
        gc.strokeLine(x[rotation] * 0.8, y[rotation] * 0.8, x[(rotation + 1) % 4] * 0.8, y[(rotation + 1) % 4] * 0.8);
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

    public Body getContainedEntity() {
        return containedEntity;
    }

    public void setContainedEntity(Body self, World world, Body entity) {
        if (containedEntity == null) {
            weldJointDef.bodyA = self;
            weldJointDef.bodyB = entity;

            entityConnection = world.createJoint(weldJointDef);

            containedEntity = entity;
        }
    }

    public void removeContainedEntity() {
        if (containedEntity != null) {
            Body self = entityConnection.getBodyA();
            entityConnection.setUserData(new DestructionFlag() {
                @Override
                public void onDestroy() {
                    System.out.println("Destroyed Joint");
                }

                @Override
                public Class getType() {
                    return null;
                }
            });
            ((BodyDataBase) containedEntity.m_userData).queueTransform(self.getPosition().clone().addLocal(self.m_mass * 5, 0), containedEntity.getAngle());
            containedEntity = null;
        }
    }

}
