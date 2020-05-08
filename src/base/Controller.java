package base;

import base.bodies.AsteroidBodyData;
import base.bodies.BodyDataBase;
import base.bodies.PlayerBodyData;
import base.bodies.ShipBodyData;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DestructionListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML Canvas canvas;
    @FXML AnchorPane parent;

    @SuppressWarnings({"unused"})
    private World world;
    private final boolean DEBUG_DRAW = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create a new world with no gravity (we're in SPACE)
        world = new World(new Vec2(0, 0));

        world.setContactListener(new ContactListener() {

            private boolean doActiveCheckA(Contact contact) {
                return contact.m_fixtureA.getBody().m_userData != null &&
                        BodyDataBase.class.isAssignableFrom(contact.m_fixtureA.m_body.m_userData.getClass()) &&
                        !((BodyDataBase) contact.m_fixtureA.m_body.m_userData).transTick;
            }

            private boolean doActiveCheckB(Contact contact) {
                return contact.m_fixtureB.getBody().m_userData != null &&
                        BodyDataBase.class.isAssignableFrom(contact.m_fixtureB.m_body.m_userData.getClass())
                        && !((BodyDataBase) contact.m_fixtureB.m_body.m_userData).transTick;
            }

            @Override
            public void beginContact(Contact contact) {
                if (doActiveCheckA(contact) && doActiveCheckB(contact)) {
                    ((BodyDataBase) contact.m_fixtureA.getBody().m_userData).addCollided(contact.m_fixtureA, contact.m_fixtureB);
                }
                if (doActiveCheckB(contact) && doActiveCheckA(contact)) {
                    ((BodyDataBase) contact.m_fixtureB.getBody().m_userData).addCollided(contact.m_fixtureB, contact.m_fixtureA);
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold manifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {

            }
        });

        world.setDestructionListener(new DestructionListener() {
            @Override
            public void sayGoodbye(Joint joint) {
                System.out.println("Joint removed");
            }

            @Override
            public void sayGoodbye(Fixture fixture) {

            }
        });

        // Tell the canvas what size it should try to follow
        canvas.widthProperty().bind(parent.widthProperty());
        canvas.heightProperty().bind(parent.heightProperty());

        BodyDef a = new BodyDef();
        a.type = BodyType.STATIC;
        a.linearDamping = 0.05f;

        for (int i = 0; i < 30; i++) {
            createAsteroid(a);
        }

        BodyDef p = new BodyDef();
        p.type = BodyType.DYNAMIC;
        p.linearDamping = 0.5f;
        p.angularDamping = 0.5f;

        PolygonShape playerCollision = new PolygonShape();
        playerCollision.setAsBox(0.3f, 0.3f);
        FixtureDef playerComp = new FixtureDef();
        playerComp.shape = playerCollision;
        playerComp.density = 5.0f;
        playerComp.restitution = 0.0f;

        Body player = world.createBody(p);
        player.createFixture(playerComp);
        player.m_userData = new PlayerBodyData(Color.color(0.2, 0.7, 0.9));

        BodyDef s = new BodyDef();
        s.type = BodyType.DYNAMIC;
        s.position = new Vec2(0, -10);
        s.angularDamping = 0.1f;
        s.linearDamping = 0.1f;

        Body ship = world.createBody(s);
        ship.m_userData = new ShipBodyData(Color.color(0.2, 0.7, 0.9));
        ((ShipBodyData) ship.m_userData).addPart(ship, 0, 0, GameData.shipParts.THRUSTER, 0);
        ((ShipBodyData) ship.m_userData).addPart(ship, 0, 1, GameData.shipParts.SEAT, 0);
        ((ShipBodyData) ship.m_userData).addPart(ship, -1, 0, GameData.shipParts.THRUSTER, 3);
        ((ShipBodyData) ship.m_userData).addPart(ship, 1, 0, GameData.shipParts.THRUSTER, 1);
        ((ShipBodyData) ship.m_userData).addPart(ship, 0, 2, GameData.shipParts.THRUSTER, 2);

        new AnimationTimer() {
            @Override
            public void handle(long l) {

                GraphicsContext gc = canvas.getGraphicsContext2D();

                // Wipe the previous draw with a rectangle draw
                gc.restore();
                gc.setFill(Color.BLACK);
                gc.setLineWidth(0);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.save();

                // Setup parameters for shape draws
                gc.setFill(Color.color(1, 1, 1, 0.1));
                gc.setStroke(Color.color(1, 1, 1, 0.3));
                gc.setLineWidth(3);

                PlayerBodyData pD = (PlayerBodyData) player.m_userData;
                double scale = 20.0;
                double rot = (pD.isSeated() ? -pD.getSeat().m_body.getAngle() : -player.getAngle()) * 180 / Math.PI;
                Vec2 trans = (pD.isSeated() ? pD.getSeat().m_body.getPosition() : player.getPosition());

                gc.save();
                Rotate r = new Rotate(rot, canvas.getWidth() / 2, canvas.getHeight() / 2);
                gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
                gc.translate(canvas.getWidth() / 2 - trans.x * scale, canvas.getHeight() / 2 - trans.y * scale);

                // Do shape draws...
                if (DEBUG_DRAW) {
                    Body b = world.getBodyList();
                    for (int i = 0; i < world.getBodyCount(); i++) {
                        if (b.m_userData != null && BodyDataBase.class.isAssignableFrom(b.m_userData.getClass())) {
                            ((BodyDataBase) b.m_userData).draw(b, gc, scale);
                        } else if (false /*Bullet Class Here*/) {

                        } else {
                            System.out.println("Cannot draw polygon for body: " + b.toString());
                        }
                        b = b.getNext();
                    }
                } else {
                    // Do advanced draws.
                }

                gc.restore();

                // Do game logic
                ((ShipBodyData) ship.m_userData).handleToStore(ship, world);

                ((PlayerBodyData) player.m_userData).doKeyUpdate(player);

                for (Body b = world.getBodyList(); b != null; b = b.getNext()) {
                    if (b.m_userData != null && BodyDataBase.class.isAssignableFrom(b.m_userData.getClass())) {
                        ((BodyDataBase) b.m_userData).transTick = false;
                        ((BodyDataBase) b.m_userData).handleDoomed(b);
                        ((BodyDataBase) b.m_userData).applyTransform(b);
                    }
                    if (b.getFixtureList() == null) {
                        b.m_userData = new DestructionFlag() {
                            @Override
                            public void onDestroy() {
                                //Do nothing
                            }

                            @Override
                            public Class getType() {
                                return AsteroidBodyData.class;
                            }
                        };
                    }
                }

                for (Body b = world.getBodyList(); b != null;) {
                    Body n = b.m_next;
                    if (b.m_userData != null && DestructionFlag.class.isAssignableFrom(b.m_userData.getClass())) {
                        if (AsteroidBodyData.class.isAssignableFrom(((DestructionFlag) b.m_userData).getType())) {
                            createAsteroid(a);
                        }
                        world.destroyBody(b);
                    }
                    b = n;
                }

                Joint n;
                for (Joint j = world.getJointList(); j != null; j = n) {
                    n = j.m_next;
                    if (j.m_userData != null && DestructionFlag.class.isAssignableFrom(j.m_userData.getClass())) {
                        world.destroyJoint(j);
                    }
                }

                //TODO: Cause disjoint asteroids to break into smaller pieces, and shift body center relative to center of mass accordingly.

                // Step forward the game physics
                world.step(1.0f/60, 8, 3);

            }
        }.start();
    }

    public void createAsteroid(BodyDef asteroidDef) {
        asteroidDef.position = new Vec2((float) (Math.random() - 0.5) * 100, (float) (Math.random() - 0.5) * 100);
        asteroidDef.angle = (float) (Math.random() * Math.PI * 2);

        Body asteroid = world.createBody(asteroidDef);
        asteroid.m_userData = new AsteroidBodyData();

        int tileCount = (int) Math.ceil(Math.random() * 50);
        int bound = (int) Math.ceil(Math.sqrt(tileCount));

        int x;
        int y;

        for (int j = 0; j < tileCount; j++) {
            x = (int) ((Math.random() - 0.5) * bound);
            y = (int) ((Math.random() - 0.5) * bound);
            if (!((AsteroidBodyData) asteroid.m_userData).checkTile(x, y)) {
                ((AsteroidBodyData) asteroid.m_userData).addTile(asteroid, x, y);
            }
        }
    }
}
