package dannypx.foe.placeholder.parser.ast;

public record BinaryOp(String op, Node left, Node right) implements Node {}
