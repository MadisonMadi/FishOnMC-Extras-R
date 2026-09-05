package dannypx.foe.placeholder.parser.ast;

import java.util.List;

public record Group(List<Node> children) implements Node {}
