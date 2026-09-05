package dannypx.foe.placeholder.functions;

import java.util.List;

public interface PlaceholderEvalValueFunction {
    PlaceholderValue resolve(List<PlaceholderValue> args);
}
