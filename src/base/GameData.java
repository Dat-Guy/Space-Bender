package base;

import javafx.scene.paint.Color;

public class GameData {

    public enum materials {
        IRON,
        SULFUR,
        MAGNESIUM,
        SILICON,
        TITANIUM,
        HYDROGEN,
        OXYGEN,
        ALUMINUM,
        SODIUM,
        LITHIUM,
        POTASSIUM,
        SILVER,
        GOLD,
        LEAD,
        URANIUM
    }

    public static char[] materialToPrefix(materials m) {
        if (m == null) {
            return null;
        } else {
            switch (m) {
                case IRON:
                    return new char[]{'F', 'e'};
                case GOLD:
                    return new char[]{'A', 'u'};
                case LEAD:
                    return new char[]{'P', 'b'};
                case OXYGEN:
                    return new char[]{'O'};
                case SILVER:
                    return new char[]{'A', 'g'};
                case SODIUM:
                    return new char[]{'N', 'a'};
                case SULFUR:
                    return new char[]{'S'};
                case LITHIUM:
                    return new char[]{'L', 'i'};
                case SILICON:
                    return new char[]{'S', 'i'};
                case URANIUM:
                    return new char[]{'U'};
                case ALUMINUM:
                    return new char[]{'A', 'l'};
                case HYDROGEN:
                    return new char[]{'H'};
                case TITANIUM:
                    return new char[]{'T', 'i'};
                case MAGNESIUM:
                    return new char[]{'M', 'g'};
                case POTASSIUM:
                    return new char[]{'K'};
            }
        }
        throw new Error("Attempted to return prefix for material with no prefix.");
    }

    public static Color materialToColor(materials m) {
        if (m == null) {
            return null;
        } else {
            switch (m) {
                case IRON:
                    return Color.color(0.6, 0.2, 0.1);
                case POTASSIUM:
                    return Color.color(0.6, 0.6, 0.6);
                case MAGNESIUM:
                    return Color.color(0.8, 0.8, 0.8);
                case SILICON:
                    return Color.color(0.1, 0.2, 0.2);
                case LITHIUM:
                    return Color.color(0.5, 0.5, 0.5);
                case TITANIUM:
                    return Color.color(0.9, 0.9, 0.9);
                case HYDROGEN:
                    return Color.color(0.8, 0.3, 0.6);
                case ALUMINUM:
                    return Color.color(0.4, 0.4, 0.3);
                case URANIUM:
                    return Color.color(0.2, 0.8, 0.3);
                case SULFUR:
                    return Color.color(0.8, 0.75, 0.3);
                case LEAD:
                    return Color.color(0.6, 0.6, 0.8);
                case GOLD:
                    return Color.color(0.8, 0.7, 0.3);
                case OXYGEN:
                    return Color.color(0.2, 0.5, 0.9);
                case SILVER:
                    return Color.color(1.0, 1.0, 1.0);
                case SODIUM:
                    return Color.color(0.9, 0.9, 0.9);
            }
        }
        throw new Error("Attempted to return color for metal with no defined color.");
    }

    public enum components {
        AMMUNITION,
        FUEL
    }

}
