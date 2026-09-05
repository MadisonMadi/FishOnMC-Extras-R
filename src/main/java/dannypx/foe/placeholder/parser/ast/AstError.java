package dannypx.foe.placeholder.parser.ast;

public record AstError(String message, int tokenStart, int tokenEnd) implements Node {}
