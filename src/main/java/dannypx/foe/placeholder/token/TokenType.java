package dannypx.foe.placeholder.token;

public enum TokenType {
    PERCENT, // %
    IDENTIFIER, // Identifiable names for placeholder IDs or Functions,
    DOT, // Tree branch separator
    LPARENTHESIS, // (
    RPARENTHESIS, // )
    COMMA, // ,

    LT, // <
    GT, // >
    ASSIGN, // =
    BANG, // !

    PLUS, // +
    MINUS, // -
    STAR, // *
    SLASH, // /

    NUMBER, // Any number from 0-9
    LITERAL, // Any plain text outside of placeholder
    ESCAPED_LITERAL, // Any plain text produced by \<any character>
    STRING, // "..." quoted literal or text
    WHITESPACE, // Spaces/tabs/newlines

    EOF // End Of File (or string in this case)
}
