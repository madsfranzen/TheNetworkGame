import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * SpriteLoader - Centralized class for loading and caching all game sprites and
 * images
 * Ensures all images are properly loaded on time and cached for reuse
 */
public class SpriteLoader {
    // Cache for storing loaded images to avoid reloading
    private static final Map<String, Image> imageCache = new HashMap<>();

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

    /**
     * Static initializer to preload essential images
     */
    static {
        // Preload essential images
        getWaterTile();
        getGroundTileset();
        getBridgeTileset();
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SpriteLoader() {
        // This class should not be instantiated
    }



    /**
     * Load an image from the specified path
     * 
     * @param path The resource path
     * @return The loaded Image
     * @throws RuntimeException if the image cannot be loaded
     */
    private static Image loadImage(String path) {
        try {
            if (imageCache.containsKey(path)) {
                return imageCache.get(path);
            }

            try (InputStream is = SpriteLoader.class.getResourceAsStream(path)) {
                if (is == null) {
                    System.err.println("Failed to load image: Resource not found at " + path);
                    return createFallbackImage();
                }

                Image image = new Image(is);
                if (image.isError()) {
                    System.err.println("Error loading image: " + image.getException().getMessage());
                    return createFallbackImage();
                }

                imageCache.put(path, image);
                return image;
            }
        } catch (Exception e) {
            System.err.println("Error loading image from " + path + ": " + e.getMessage());
            return createFallbackImage();
        }
    }

    /**
     * Create a fallback image for when a resource cannot be loaded
     * 
     * @return A simple fallback image
     */
    private static Image createFallbackImage() {
        // Simplified fallback - we could create a more sophisticated fallback if needed
        WritableImage fallbackImage = new WritableImage(64, 64);
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                if ((x / 8 + y / 8) % 2 == 0) {
                    fallbackImage.getPixelWriter().setArgb(x, y, 0xFFFF00FF); // Magenta
                } else {
                    fallbackImage.getPixelWriter().setArgb(x, y, 0xFF000000); // Black
                }
            }
        }
        return fallbackImage;
    }

    // Methods to access individual sprites

    /**
     * Get the water tile image
     */
    public static Image getWaterTile() {
        return WATER;
    }

    /**
     * Get the foam spritesheet
     */
    public static Image getFoamSpritesheet() {
        return FOAM;
    }

    /**
     * Get a specific rocks image by index (1-4)
     */
    public static Image getRocksImage(int index) {
        if (index < 1 || index > 4) {
            throw new IllegalArgumentException("Rocks index must be between 1 and 4");
        }
        return loadImage(ROCKS_PATH_PREFIX + index + ".png");
    }

    /**
     * Get all rocks images as an array
     */
    public static Image[] getAllRocksImages() {
        Image[] rocks = new Image[4];
        for (int i = 0; i < 4; i++) {
            rocks[i] = getRocksImage(i + 1);
        }
        return rocks;
    }

    /**
     * Get the ground tileset
     */
    public static Image getGroundTileset() {
        return loadImage(GROUND_TILEMAP_PATH);
    }

    /**
     * Get the elevation tileset for plateaus and stairs
     */
    public static Image getElevationTileset() {
        return ELEVATION_TILEMAP;
    }

    /**
     * Get the shadows image
     */
    public static Image getShadowsImage() {
        return loadImage(SHADOWS_PATH);
    }

    /**
     * Get the bridge tileset
     */
    public static Image getBridgeTileset() {
        return loadImage(BRIDGE_PATH);
    }

    /**
     * Extract a single frame from a spritesheet
     * 
     * @param spritesheet The spritesheet image
     * @param frameIndex  The index of the frame to extract
     * @param frameCount  The total number of frames in the spritesheet
     * @return The extracted frame as a WritableImage
     */
    public static WritableImage extractFrame(Image spritesheet, int frameIndex, int frameCount) {
        int frameWidth = (int) spritesheet.getWidth() / frameCount;
        int frameHeight = (int) spritesheet.getHeight();
        return new WritableImage(
                spritesheet.getPixelReader(),
                frameIndex * frameWidth,
                0,
                frameWidth,
                frameHeight);
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
        return loadImage(sprite);
    }
}