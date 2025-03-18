import javafx.scene.image.Image;

/**
 * SpriteLoader - Centralized class for loading and caching all game sprites and
 * images
 * Ensures all images are properly loaded on time and cached for reuse
 */
public class SpriteLoader {

    // Terrain paths
    private static final String WATER_PATH = "/assets/Terrain/Water/Water.png";
    private static final String FOAM_PATH = "/assets/Terrain/Water/Foam/Foam.png";
    private static final String ROCKS_PATH_PREFIX = "/assets/Terrain/Water/Rocks/Rocks_0";
    private static final String GROUND_TILEMAP_PATH = "/assets/Terrain/Ground/Tilemap_Flat.png";
    private static final String ELEVATION_TILEMAP_PATH = "/assets/Terrain/Ground/Tilemap_Elevation.png";
    private static final String SHADOWS_PATH = "/assets/Terrain/Ground/Shadows.png";
    private static final String BRIDGE_PATH = "/assets/Terrain/Bridge/Bridge_All.png";
    private static final String PLAYER_PATH_BLUE = "/assets/Factions/Knights/Troops/Warrior/Blue/Warrior_Blue.png";
    private static final String PLAYER_PATH_RED = "/assets/Factions/Knights/Troops/Warrior/Red/Warrior_Red.png";
    private static final String PLAYER_PATH_YELLOW = "/assets/Factions/Knights/Troops/Warrior/Yellow/Warrior_Yellow.png";
    private static final String PLAYER_PATH_PURPLE = "/assets/Factions/Knights/Troops/Warrior/Purple/Warrior_Purple.png";

    private static Image WATER;
    private static Image FOAM;
    private static Image ROCKS1;
    private static Image ROCKS2;
    private static Image ROCKS3;
    private static Image GROUND_TILEMAP;
    private static Image ELEVATION_TILEMAP;
    private static Image SHADOWS;
    private static Image BRIDGE;
    private static Image PLAYER_BLUE;
    private static Image PLAYER_RED;
    private static Image PLAYER_YELLOW;
    private static Image PLAYER_PURPLE;

    public static void loadSprites(RecieverThread recieverThread) {
        // Images = loaded on startup
        WATER = new Image(SpriteLoader.class.getResourceAsStream(WATER_PATH));
        FOAM = new Image(SpriteLoader.class.getResourceAsStream(FOAM_PATH));
        ROCKS1 = new Image(SpriteLoader.class.getResourceAsStream(ROCKS_PATH_PREFIX + "1.png"));
        ROCKS2 = new Image(SpriteLoader.class.getResourceAsStream(ROCKS_PATH_PREFIX + "2.png"));
        ROCKS3 = new Image(SpriteLoader.class.getResourceAsStream(ROCKS_PATH_PREFIX + "3.png"));
        GROUND_TILEMAP = new Image(SpriteLoader.class.getResourceAsStream(GROUND_TILEMAP_PATH));
        ELEVATION_TILEMAP = new Image(SpriteLoader.class.getResourceAsStream(ELEVATION_TILEMAP_PATH));
        SHADOWS = new Image(SpriteLoader.class.getResourceAsStream(SHADOWS_PATH));
        BRIDGE = new Image(SpriteLoader.class.getResourceAsStream(BRIDGE_PATH));
        PLAYER_BLUE = new Image(SpriteLoader.class.getResourceAsStream(PLAYER_PATH_BLUE));
        PLAYER_RED = new Image(SpriteLoader.class.getResourceAsStream(PLAYER_PATH_RED));
        PLAYER_YELLOW = new Image(SpriteLoader.class.getResourceAsStream(PLAYER_PATH_YELLOW));
        PLAYER_PURPLE = new Image(SpriteLoader.class.getResourceAsStream(PLAYER_PATH_PURPLE));

        recieverThread.setSpritesLoaded(true);
    }

    public static Image getSprite(String sprite) {
        String spriteName = sprite.toLowerCase().split("_")[0];
        if (spriteName.equals("player")) {
            String faction = sprite.toLowerCase().split("_")[2];
            switch (faction) {
                case "blue":
                    return PLAYER_BLUE;
                case "red":
                    return PLAYER_RED;
                case "yellow":
                    return PLAYER_YELLOW;
                case "purple":
                    return PLAYER_PURPLE;
                default:
                    return null;
            }
        }
        switch (spriteName) {
            case "water":
                return WATER;
            case "foam":
                return FOAM;
            case "rocks1":
                return ROCKS1;
            case "rocks2":
                return ROCKS2;
            case "rocks3":
                return ROCKS3;
            case "grass":
                return GROUND_TILEMAP;
            case "sand":
                return GROUND_TILEMAP;
            case "elevation_tilemap":
                return ELEVATION_TILEMAP;
            case "shadows":
                return SHADOWS;
            case "bridge":
                return BRIDGE;
            default:
                System.out.println("SPRITE NOT FOUND: " + sprite);
                return null;
        }
    }
}