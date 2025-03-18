
public class TileVariant {

    public static final int[] GRASS_SOLO = { 3, 3 };

    public static int[] getVariant(String sprite) {
        String[] spriteParts = sprite.toUpperCase().split("_");
        String type = spriteParts[0];
        String variant = spriteParts[1];
        switch (type) {
            case "GRASS":
                return getGrassVariantXY(variant);
        }
        return null;
    }

    private static int[] getGrassVariantXY(String variant) {
        switch (variant) {
            case "SOLO":
                return GRASS_SOLO;
        }
        return null;
    }

}