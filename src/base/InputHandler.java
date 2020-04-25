package base;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;

/**
 * Wrapper class for app-wide input. Listeners set up for asynchronous input,
 * queries take the current state of the input device.
 */
public class InputHandler {

    // Mapping of KeyCodes to boolean state, where true is pressed and false is released.
    private static final HashMap<KeyCode, Boolean> keyCodeBooleanHashMap = new HashMap<>();

    /**
     * Handles 'key pressed' events.
     */
    public static class keyPressedHandler implements EventHandler<KeyEvent> {

        /**
         * Helper function which updates the key map with newly pressed keys.
         * @param keyEvent The event object (tells us what key was pressed).
         */
        @Override
        public void handle(KeyEvent keyEvent) {
            keyCodeBooleanHashMap.put(keyEvent.getCode(), true);
        }
    }

    /**
     * Handles 'key released' events.
     */
    public static class keyReleasedHandler implements EventHandler<KeyEvent> {

        /**
         * Helper function which updates the key map with newly released keys.
         * @param keyEvent The event object (tells us what key was pressed).
         */
        @Override
        public void handle(KeyEvent keyEvent) {
            keyCodeBooleanHashMap.put(keyEvent.getCode(), false);
        }
    }

    /**
     * Getter which does a safe get of the keymap (not auto-populated by default, so some values may not exist yet).
     * Assumes that any keys not registered are released by default (hopeful thinking that nobody holds down buttons
     * while the program is loading, which could sidestep this).
     * @param code The keycode to query the map with.
     * @return The state of the key as a boolean value; true is pressed, false is released.
     */
    public static boolean getKey(KeyCode code) {
        return keyCodeBooleanHashMap.getOrDefault(code, false);
    }


}
