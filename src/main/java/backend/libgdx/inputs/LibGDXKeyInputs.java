package backend.libgdx.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import engine.inputs.IKeyInput;
import engine.inputs.Key;

public class LibGDXKeyInputs implements IKeyInput {

    @Override
    public boolean isPress(Key key) {
        // Obtenemos el código de tecla correspondiente en LibGDX
        int gdxKey = getLibGDXKey(key);

        // Si la tecla no está mapeada, devolvemos false por seguridad
        if (gdxKey == Input.Keys.UNKNOWN) {
            return false;
        }

        // Retorna true si la tecla está siendo presionada en este momento
        return Gdx.input.isKeyPressed(gdxKey);
    }

    /**
     * Método auxiliar para convertir tu enum 'Key' a las constantes de LibGDX.
     */
    private int getLibGDXKey(Key key) {
        if (key == null) return Input.Keys.UNKNOWN;

        switch (key) {
            case A: return Input.Keys.A;
            case B: return Input.Keys.B;
            case C: return Input.Keys.C;
            case D: return Input.Keys.D;
            case E: return Input.Keys.E;
            case F: return Input.Keys.F;
            case G: return Input.Keys.G;
            case H: return Input.Keys.H;
            case I: return Input.Keys.I;
            case J: return Input.Keys.J;
            case K: return Input.Keys.K;
            case L: return Input.Keys.L;
            case M: return Input.Keys.M;
            case N: return Input.Keys.N;
            case O: return Input.Keys.O;
            case P: return Input.Keys.P;
            case Q: return Input.Keys.Q;
            case R: return Input.Keys.R;
            case S: return Input.Keys.S;
            case T: return Input.Keys.T;
            case U: return Input.Keys.U;
            case V: return Input.Keys.V;
            case W: return Input.Keys.W;
            case X: return Input.Keys.X;
            case Y: return Input.Keys.Y;
            case Z: return Input.Keys.Z;

            // Teclas de dirección
            case UP: return Input.Keys.UP;
            case RIGHT: return Input.Keys.RIGHT;
            case DOWN: return Input.Keys.DOWN;
            case LEFT: return Input.Keys.LEFT;

            default: return Input.Keys.UNKNOWN;
        }
    }
}
