package dannypx.foe.placeholder.functions;

import java.util.List;

public interface PlaceholderEvalNumberFunction {
    Number resolve(List<PlaceholderValue> args);
}
