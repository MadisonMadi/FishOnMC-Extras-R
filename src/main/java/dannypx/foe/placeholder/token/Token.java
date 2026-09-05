package dannypx.foe.placeholder.token;

public record Token(TokenType type, String text, int start, int end) {

    public static Token of(TokenType type, String text, int start, int end) {
        return new Token(type, text, start, end);
    }
}
