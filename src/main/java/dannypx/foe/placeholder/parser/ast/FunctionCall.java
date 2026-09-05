package dannypx.foe.placeholder.parser.ast;

import dannypx.foe.placeholder.registry.PlaceholderTreeNode;

import java.util.List;

public record FunctionCall(PlaceholderTreeNode resolved, List<Node> args) implements Node {}
