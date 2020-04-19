package sample;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML Canvas canvas;
    @FXML AnchorPane parent;

    @SuppressWarnings({"unused"})
    private World world;
    private boolean DEBUG_DRAW = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create a new world with no gravity (we're in SPACE)
        world = new World(new Vec2(0, 0));

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (contact.m_fixtureA.getBody().m_userData != null && BodyDataBase.class.isAssignableFrom(contact.m_fixtureA.getBody().m_userData.getClass())) {
                    ((BodyDataBase) contact.m_fixtureA.getBody().m_userData).addDoomed(contact.m_fixtureA);
                }
                if (contact.m_fixtureB.getBody().m_userData != null && BodyDataBase.class.isAssignableFrom(contact.m_fixtureB.getBody().m_userData.getClass())) {
                    ((BodyDataBase) contact.m_fixtureB.getBody().m_userData).addDoomed(contact.m_fixtureB);
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

        // Tell the canvas what size it should try to follow
        canvas.widthProperty().bind(parent.widthProperty());
        canvas.heightProperty().bind(parent.heightProperty());

        BodyDef a = new BodyDef();
        a.type = BodyType.DYNAMIC;
        a.linearDamping = 0.05f;

        for (int i = 0; i < 100; i++) {
            createAsteroid(a);
        }

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

                gc.save(); // NOTE: do transformations in this order -> Translate Scale Rotate (Skew)
                gc.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
                float scale = 10; // 1 meter -> 40px

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
                ArrayList<Body> toDestroy = new ArrayList<>();

                for (Body b = world.getBodyList(); b != null; b = b.getNext()) {
                    if (b.m_userData != null && BodyDataBase.class.isAssignableFrom(b.m_userData.getClass())) {
                        ((BodyDataBase) b.m_userData).destroyDoomed(b);
                    }
                    if (b.getFixtureList() == null) {
                        toDestroy.add(b);
                    }
                    b.applyForceToCenter(new Vec2(-b.getPosition().x / 100 * b.m_mass, -b.getPosition().y / 100 * b.m_mass));
                }

                for (Body b : toDestroy) {
                    world.destroyBody(b);
                    createAsteroid(a);
                }

                //TODO: Cause disjoint asteroids to break into smaller pieces, and shift body center relative to center of mass accordingly.

                System.out.println(world.getBodyCount());

                // Step forward the game physics
                world.step(1.0f/60, 8, 3);

            }
        }.start();
    }

    public void createAsteroid(BodyDef asteroidDef) {
        asteroidDef.position = new Vec2((float) (Math.random() - 0.5) * 100, (float) (Math.random() - 0.5) * 100);

        Body asteroid = world.createBody(asteroidDef);
        asteroid.m_userData = new BodyDataBase();

        int tileCount = (int) Math.ceil(Math.random() * 100);
        int bound = (int) Math.ceil(Math.sqrt(tileCount));

        for (int j = 0; j < tileCount; j++) {
            ((BodyDataBase) asteroid.m_userData).addTile(asteroid, (int) ((Math.random() - 0.5) * bound), (int) ((Math.random() - 0.5) * bound));
        }

        asteroid.setLinearVelocity(new Vec2((float) (Math.random() - 0.5) * 20, (float) (Math.random() - 0.5) * 20));
        asteroid.setAngularVelocity((float) (Math.random() - 0.5) * 0.5f);
    }
}
