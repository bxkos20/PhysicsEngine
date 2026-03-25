package engine.app.backend;

import backend.libgdx.app.BackendLauncher;

/**
 * Enum representing different types of backend launchers.
 * Currently only supports LIBGDX.
 */
public enum LauncherType {
    LIBGDX;

    public IBackendLauncher getLauncher() {
        switch (this) {
            case LIBGDX:
                return new BackendLauncher();
        }
        throw new UnsupportedOperationException("Unknown LauncherType: " + this);
    }
}

