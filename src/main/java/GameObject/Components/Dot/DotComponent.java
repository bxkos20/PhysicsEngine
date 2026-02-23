package GameObject.Components.Dot;

public class DotComponent {
    private final DotType dotType;

    public DotComponent(DotType dotType) {
        this.dotType = dotType;
    }

    public DotType getDotType() {
        return dotType;
    }
}

