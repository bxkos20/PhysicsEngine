// Nuevo archivo: engine/config/Settings.java
package engine.config;

import engine.config.implementations.*;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    private final Map<Class<? extends ISettings>, ISettings> settingsMap = new HashMap<>();

    public static Settings createDefault(){
        return new Settings().add(new PerformanceSettings())
                .add(new RenderingSettings())
                .add(new ScreenSettings())
                .add(new SimulationSettings())
                .add(new WorldSettings());
    }

    /**
     * Adds a setting component to this configuration.
     * If a component of the same type already exists, it will be replaced.
     */
    public Settings add(ISettings setting) {
        settingsMap.put(setting.getClass(), setting);
        return this;
    }

    /**
     * Retrieves a setting component of a specific type.
     * Returns null if no component of that type has been added.
     */
    @SuppressWarnings("unchecked")
    public <T extends ISettings> T get(Class<T> settingClass) {
        return (T) settingsMap.get(settingClass);
    }
}