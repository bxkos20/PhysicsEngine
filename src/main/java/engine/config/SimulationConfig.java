package engine.config;

/**
 * Centralized configuration for the physics simulation.
 * 
 * <p>Eliminates magic numbers and provides easy tuning. All configuration
 * values are organized into nested static classes by category.</p>
 * 
 * <p>Usage: {@code SimulationConfig.Display.SCREEN_WIDTH}</p>
 */
public final class SimulationConfig { //TODO: Make json save method;
    /**
     * Display/viewport configuration.
     * Controls the application window dimensions.
     */
    public static final class Display {
        /** Viewport width in pixels */
        public static final int SCREEN_WIDTH = 1280;
        /** Viewport height in pixels */
        public static final int SCREEN_HEIGHT = 720;
    }
    
    /**
     * World/simulation space configuration.
     * Defines the bounds of the physics simulation area.
     */
    public static final class World {
        /** World width in simulation units */
        public static final int WORLD_WIDTH = 2000;
        /** World height in simulation units */
        public static final int WORLD_HEIGHT = 2000;
    }
    
    /**
     * Physics simulation configuration.
     * Controls entity counts, physics constants, and interaction parameters.
     */
    public static final class Simulation {
        /** Total number of particles/entities in simulation */
        public static final int TOTAL_DOTS = 2500;
        /** Number of distinct particle types */
        public static final int DOT_TYPES = 10;
        /** Particles per type (TOTAL_DOTS / DOT_TYPES) */
        public static final int DOTS_PER_TYPE = TOTAL_DOTS / DOT_TYPES;
        
        /** Fixed timestep for physics (1/60 = 60 Hz physics) */
        public static final float FIXED_TIMESTEP_SIMULATION = 1.0f / 60.0f;
        /** Fixed timestep for physics (1/60 = 60 Hz physics) */
        public static final float FIXED_TIMESTEP_RENDERING = 1.0f / 30.0f;
        /** Maximum frame time to prevent spiral of death */
        public static final float MAX_FRAME_TIME = 0.25f;
        /** Default time multiplier (1.0 = real-time) */
        public static final float DEFAULT_TIMESCALE = 1.0f;
        
        /** Gravitational attraction strength between particles */
        public static final float GRAVITY_CONSTANT = 25.0f;
        /** Minimum distance for interaction calculations */
        public static final float MIN_INTERACTION_DISTANCE = 25.0f;
        /** Maximum distance for interaction calculations */
        public static final float MAX_INTERACTION_DISTANCE = 75.0f;
    }
    
    /**
     * Rendering configuration.
     * Controls GPU batch sizes and shape quality.
     */
    public static final class Rendering {
        /** Maximum vertices per batch */
        public static final int BATCH_VERTICES = 50000;
        /** Maximum indices per batch */
        public static final int BATCH_INDICES = 150000;
        
        /** Default particle radius in world units */
        public static final float DEFAULT_DOT_RADIUS = 5.0f;
    }
    
    /**
     * Performance optimization configuration.
     * Controls threading, spatial partitioning, and profiling.
     */
    public static final class Performance {
        /** Enable parallel processing for collision detection */
        public static final boolean ENABLE_MULTITHREADING = true;
        /** Grid cell size for spatial partitioning (pixels) */
        public static final int GRID_CELL_SIZE = 50;
    }
    
    // Private constructor to prevent instantiation
    private SimulationConfig() {
        throw new UnsupportedOperationException("Config class - do not instantiate");
    }
}
