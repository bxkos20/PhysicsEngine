package engine.config.parameters;

import java.util.HashMap;
import java.util.Map;

public class Parameters {
    private final Map<Class<? extends IParameters>, IParameters> settingsMap = new HashMap<>();

    public Parameters() {
    }

    /**
     * Adds a parameter component to this configuration.
     * If a component of the same type already exists, it will be replaced.
     */
    public Parameters add(IParameters parameter) {
        settingsMap.put(parameter.getClass(), parameter);
        return this;
    }
    
    /**
     * Retrieves a parameter component of a specific type.
     * Returns null if no component of that type has been added.
     */
    @SuppressWarnings("unchecked")
    public <T extends IParameters> T get(Class<T> parameterClass) {
        return (T) settingsMap.get(parameterClass);
    }
}