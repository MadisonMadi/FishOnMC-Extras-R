package dannypx.foe.placeholder.functions;

import java.util.List;

public interface PlaceholderEvalBooleanFunction {
    Boolean resolve(List<PlaceholderValue> args);
}
