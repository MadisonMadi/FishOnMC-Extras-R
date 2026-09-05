package dannypx.foe.placeholder.parser.ast;

public sealed interface Node permits Literal, PlaceholderReference, FunctionCall, BinaryOp, UnaryOp, AstError, Group {}

