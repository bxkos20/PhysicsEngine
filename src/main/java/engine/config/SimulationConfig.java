package engine.config;

/**
 * Centralized configuration for the physics simulation.
 * Eliminates magic numbers and provides easy tuning.
 */
public final class SimulationConfig {
    
    // Display Configuration
    public static final class Display {
        public static final int SCREEN_WIDTH = 1280;
        public static final int SCREEN_HEIGHT = 720;
        public static final float ASPECT_RATIO = (float) SCREEN_WIDTH / SCREEN_HEIGHT;
    }
    
    // World Configuration  
    public static final class World {
        public static final int WORLD_WIDTH = 2500;
        public static final int WORLD_HEIGHT = 2500;
        public static final float WORLD_ASPECT_RATIO = (float) WORLD_WIDTH / WORLD_HEIGHT;
    }
    
    // Simulation Configuration
    public static final class Simulation {
        public static final int TOTAL_DOTS = 2000;
        public static final int DOT_TYPES = 10; // Number of dot types
        public static final int DOTS_PER_TYPE = TOTAL_DOTS / DOT_TYPES;
        
        // Physics constants
        public static final float FIXED_TIMESTEP = 1.0f / 60.0f; // 60 FPS physics
        public static final float MAX_FRAME_TIME = 0.25f; // Spiral death prevention
        public static final float DEFAULT_TIMESCALE = 1.0f;
        
        // Dot interaction constants
        public static final float GRAVITY_CONSTANT = 25.0f;
        public static final float MIN_INTERACTION_DISTANCE = 25.0f;
        public static final float MAX_INTERACTION_DISTANCE = 75.0f;
    }
    
    // Rendering Configuration
    public static final class Rendering {
        public static final int BATCH_VERTICES = 10000;
        public static final int BATCH_INDICES = 20000;
        
        // Shape parameters
        public static final float DEFAULT_DOT_RADIUS = 5.0f;
        public static final int DEFAULT_CIRCLE_SEGMENTS = 32;
        public static final float DEFAULT_LINE_WIDTH = 2.5f;
    }
    
    // Performance Configuration
    public static final class Performance {
        public static final boolean ENABLE_MULTITHREADING = true;
        public static final int GRID_CELL_SIZE = 50; // For spatial partitioning
        public static final boolean ENABLE_PROFILING = true;
    }
    
    // Private constructor to prevent instantiation
    private SimulationConfig() {
        throw new UnsupportedOperationException("Config class - do not instantiate");
    }
}
