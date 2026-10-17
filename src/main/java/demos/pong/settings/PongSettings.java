package demos.pong.settings;

import engine.config.settings.ISettings;

/**
 * Contains all configurable settings for the Pong simulation.
 */
public class PongSettings implements ISettings {

    // Player settings
    public float playerSpeed;
    public float playerSize;
    public float playerMass;
    public float playerXOffset;

    // Ball settings
    public float ballRadius;
    public float ballMass;
    public float ballInitialSpeedX;
    public float ballInitialSpeedY;

    // Rendering settings
    public int circleSegments;
    public int circleLineWidth;

    /**
     * Default constructor.
     * Sets standard, playable values for a game of Pong.
     */
    public PongSettings() {
        this.playerSpeed = 200f;
        this.playerSize = 50f;
        this.playerMass = 100f;
        this.playerXOffset = 100f;
        this.ballRadius = 20f;
        this.ballMass = 1f;
        this.ballInitialSpeedX = 200f;
        this.ballInitialSpeedY = 0f;
        this.circleSegments = 32;
        this.circleLineWidth = 10;
    }

    /**
     * Fully parameterized constructor.
     */
    public PongSettings(float playerSpeed, float playerSize, float playerMass, float playerXOffset,
                        float ballRadius, float ballMass, float ballInitialSpeedX, float ballInitialSpeedY,
                        int circleSegments, int circleLineWidth) {
        this.playerSpeed = playerSpeed;
        this.playerSize = playerSize;
        this.playerMass = playerMass;
        this.playerXOffset = playerXOffset;
        this.ballRadius = ballRadius;
        this.ballMass = ballMass;
        this.ballInitialSpeedX = ballInitialSpeedX;
        this.ballInitialSpeedY = ballInitialSpeedY;
        this.circleSegments = circleSegments;
        this.circleLineWidth = circleLineWidth;
    }
}