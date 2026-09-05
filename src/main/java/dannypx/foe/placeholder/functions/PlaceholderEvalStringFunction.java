package dannypx.foe.placeholder.functions;

import java.util.List;

public interface PlaceholderEvalStringFunction {
    String resolve(List<PlaceholderValue> args);
}
