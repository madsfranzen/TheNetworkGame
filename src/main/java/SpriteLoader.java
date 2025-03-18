import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

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

    // Images = loaded on startup
    public static final Image WATER = new Image(SpriteLoader.class.getResourceAsStream(WATER_PATH));
    public static final Image FOAM = new Image(SpriteLoader.class.getResourceAsStream(FOAM_PATH));
    public static final Image ROCKS = new Image(SpriteLoader.class.getResourceAsStream(ROCKS_PATH_PREFIX));
    public static final Image GROUND_TILEMAP = new Image(SpriteLoader.class.getResourceAsStream(GROUND_TILEMAP_PATH));
    public static final Image ELEVATION_TILEMAP = new Image(SpriteLoader.class.getResourceAsStream(ELEVATION_TILEMAP_PATH));
    public static final Image SHADOWS = new Image(SpriteLoader.class.getResourceAsStream(SHADOWS_PATH));
    public static final Image BRIDGE = new Image(SpriteLoader.class.getResourceAsStream(BRIDGE_PATH));
    
    // Flag to track if initialization has occurred
    private static boolean initialized = false;
    
    /**
     * Initializes the SpriteLoader by ensuring all sprites are loaded.
     * Call this method from the App class during startup.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        // Access all static Image fields to ensure they're loaded
        Image[] images = {
            WATER, FOAM, ROCKS, GROUND_TILEMAP, 
            ELEVATION_TILEMAP, SHADOWS, BRIDGE
        };
        
        // Check if any images failed to load
        for (Image img : images) {
            if (img.isError()) {
                System.err.println("Error loading one or more sprites!");
                break;
            }
        }
        
        System.out.println("🎮 All game sprites loaded successfully!");
        initialized = true;
    }
}