
public class TileVariant {

    // Player variants
    public static final int[] PLAYER_IDLE1 = { 0, 0 };
    public static final int[] PLAYER_IDLE2 = { 3, 0 };
    public static final int[] PLAYER_IDLE3 = { 6, 0 };
    public static final int[] PLAYER_IDLE4 = { 9, 0 };
    public static final int[] PLAYER_IDLE5 = { 12, 0 };
    public static final int[] PLAYER_IDLE6 = { 15 , 0 };

    // Grass variants
    public static final int[] GRASS_SOLO = { 3, 3 };
    public static final int[] GRASS_TOP_LEFT = { 0, 0 };
    public static final int[] GRASS_TOP = { 1, 0 };
    public static final int[] GRASS_TOP_RIGHT = { 2, 0 };
    public static final int[] GRASS_LEFT = { 0, 1 };
    public static final int[] GRASS_CENTER = { 1, 1 };
    public static final int[] GRASS_RIGHT = { 2, 1 };
    public static final int[] GRASS_BOTTOM_LEFT = { 0, 2 };
    public static final int[] GRASS_BOTTOM = { 1, 2 };
    public static final int[] GRASS_BOTTOM_RIGHT = { 2, 2 };
    public static final int[] GRASS_HOR_LEFT = { 0, 3 };
    public static final int[] GRASS_HOR_CENTER = { 1, 3 };
    public static final int[] GRASS_HOR_RIGHT = { 2, 3 };
    public static final int[] GRASS_VER_TOP = { 3, 0 };
    public static final int[] GRASS_VER_CENTER = { 3, 1 };
    public static final int[] GRASS_VER_BOTTOM = { 3, 2 };

    // Sand variants with x coordinate incremented by 5
    public static final int[] SAND_SOLO = { 8, 3 };
    public static final int[] SAND_TOP_LEFT = { 5, 0 };
    public static final int[] SAND_TOP = { 6, 0 };
    public static final int[] SAND_TOP_RIGHT = { 7, 0 };
    public static final int[] SAND_LEFT = { 5, 1 };
    public static final int[] SAND_CENTER = { 6, 1 };
    public static final int[] SAND_RIGHT = { 7, 1 };
    public static final int[] SAND_BOTTOM_LEFT = { 5, 2 };
    public static final int[] SAND_BOTTOM = { 6, 2 };
    public static final int[] SAND_BOTTOM_RIGHT = { 7, 2 };
    public static final int[] SAND_HOR_LEFT = { 5, 3 };
    public static final int[] SAND_HOR_CENTER = { 6, 3 };
    public static final int[] SAND_HOR_RIGHT = { 7, 3 };
    public static final int[] SAND_VER_TOP = { 8, 0 };
    public static final int[] SAND_VER_CENTER = { 8, 1 };
    public static final int[] SAND_VER_BOTTOM = { 8, 2 };

    public static int[] getVariant(String sprite) {
        String[] spriteParts = sprite.toUpperCase().split("_");

        if (spriteParts[0].equals("WATER")) {
            return new int[] { 0, 0 };
        }

        String variant = spriteParts[1];
        String type = spriteParts[0];

        switch (type) {
            case "GRASS":
                return getGrassVariantXY(variant);
            case "SAND":
                return getSandVariantXY(variant);
            case "PLAYER":
                return getPlayerVariantXY(variant);
        }
        return null;
    }

    private static int[] getGrassVariantXY(String variant) {
        switch (variant) {
            case "SOLO":
                return GRASS_SOLO;
            case "TOP_LEFT":
                return GRASS_TOP_LEFT;
            case "TOP":
                return GRASS_TOP;
            case "TOP_RIGHT":
                return GRASS_TOP_RIGHT;
            case "LEFT":
                return GRASS_LEFT;
            case "CENTER":
                return GRASS_CENTER;
            case "RIGHT":
                return GRASS_RIGHT;
            case "BOTTOM_LEFT":
                return GRASS_BOTTOM_LEFT;
            case "BOTTOM":
                return GRASS_BOTTOM;
            case "BOTTOM_RIGHT":
                return GRASS_BOTTOM_RIGHT;
            case "HOR_LEFT":
                return GRASS_HOR_LEFT;
            case "HOR_CENTER":
                return GRASS_HOR_CENTER;
            case "HOR_RIGHT":
                return GRASS_HOR_RIGHT;
            case "VER_TOP":
                return GRASS_VER_TOP;
            case "VER_CENTER":
                return GRASS_VER_CENTER;
            case "VER_BOTTOM":
                return GRASS_VER_BOTTOM;
            default:
                return GRASS_SOLO;
        }
    }

    private static int[] getSandVariantXY(String variant) {
        switch (variant) {
            case "SOLO":
                return SAND_SOLO;
            case "TOP_LEFT":
                return SAND_TOP_LEFT;
            case "TOP":
                return SAND_TOP;
            case "TOP_RIGHT":
                return SAND_TOP_RIGHT;
            case "LEFT":
                return SAND_LEFT;
            case "CENTER":
                return SAND_CENTER;
            case "RIGHT":
                return SAND_RIGHT;
            case "BOTTOM_LEFT":
                return SAND_BOTTOM_LEFT;
            case "BOTTOM":
                return SAND_BOTTOM;
            case "BOTTOM_RIGHT":
                return SAND_BOTTOM_RIGHT;
            case "HOR_LEFT":
                return SAND_HOR_LEFT;
            case "HOR_CENTER":
                return SAND_HOR_CENTER;
            case "HOR_RIGHT":
                return SAND_HOR_RIGHT;
            case "VER_TOP":
                return SAND_VER_TOP;
            case "VER_CENTER":
                return SAND_VER_CENTER;
            case "VER_BOTTOM":
                return SAND_VER_BOTTOM;
            default:
                return SAND_SOLO;
        }
    }

    private static int[] getPlayerVariantXY(String variant) {
        switch (variant) {
            case "IDLE1":
                return PLAYER_IDLE1;
            case "IDLE2":
                return PLAYER_IDLE2;
            case "IDLE3":
                return PLAYER_IDLE3;
            case "IDLE4":
                return PLAYER_IDLE4;
            case "IDLE5":
                return PLAYER_IDLE5;
            case "IDLE6":
                return PLAYER_IDLE6;
            default:
                return null;
        }
    }
}