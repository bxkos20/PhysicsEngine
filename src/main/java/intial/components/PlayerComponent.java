package intial.components;

import engine.inputs.Key;

public class PlayerComponent {
    public Key keyUp;
    public Key keyDown;

    public PlayerComponent(Key keyUp, Key keyDown){
        this.keyUp = keyUp;
        this.keyDown = keyDown;
    }
}
