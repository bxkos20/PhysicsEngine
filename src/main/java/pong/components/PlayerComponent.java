package pong.components;

import engine.inputs.Key;

public class PlayerComponent {
    public Key keyUp;
    public Key keyDown;
    public float speed;

    public PlayerComponent(Key keyUp, Key keyDown, float speed){
        this.keyUp = keyUp;
        this.keyDown = keyDown;
        this.speed = speed;
    }
}