package pong.components;

import engine.inputs.Key;

public class PlayerComponent {
    public Key keyUp;
    public Key keyDown;
    public float xPosition;

    public PlayerComponent(Key keyUp, Key keyDown, float xPosition){
        this.keyUp = keyUp;
        this.keyDown = keyDown;
        this.xPosition = xPosition;
    }
}
