package dannypx.foe.placeholder.parser.ast;

import dannypx.foe.placeholder.registry.PlaceholderTreeNode;

import java.util.List;

public record PlaceholderReference(PlaceholderTreeNode resolved, List<String> indices) implements Node {}
