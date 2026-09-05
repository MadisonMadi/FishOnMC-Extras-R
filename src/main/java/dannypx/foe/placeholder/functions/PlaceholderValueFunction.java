package dannypx.foe.placeholder.functions;

import java.util.List;

public interface PlaceholderValueFunction {
    PlaceholderValue resolve(List<String> args);
}
