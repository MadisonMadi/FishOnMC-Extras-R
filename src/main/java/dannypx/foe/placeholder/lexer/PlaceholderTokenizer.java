package dannypx.foe.placeholder.lexer;

import dannypx.foe.placeholder.token.PlaceholderParseException;
import dannypx.foe.placeholder.token.Token;
import dannypx.foe.placeholder.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderTokenizer {
    private final String source;

    private int pos = 0;

    public PlaceholderTokenizer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token t;

        do {
            t = nextToken();
            tokens.add(t);
        } while (t.type() != TokenType.EOF);

        return tokens;
    }

    private Token nextToken() {
        // If at the end
        if(this.isAtEnd()) {
            return Token.of(TokenType.EOF, "", pos, pos);
        }

        char c = this.peekChar();

        // If it is whitespace
        if(Character.isWhitespace(c)) {
            return this.scanWhitespace();
        }

        // If it is a quoted string
        if(c == '"') {
            return this.scanQuotedString();
        }

        if(c == '<') return this.consumeFixed(TokenType.LT, 1);
        if(c == '>') return this.consumeFixed(TokenType.GT, 1);
        if(c == '=') return this.consumeFixed(TokenType.ASSIGN, 1);
        if(c == '!') return this.consumeFixed(TokenType.BANG, 1);

        if(c == '\\' && this.hasNext(1)) {
            int start = pos;
            char literal = this.peekChar(1);
            this.advancePos(2);

            return Token.of(TokenType.ESCAPED_LITERAL, String.valueOf(literal), start, pos);
        }

        if(Character.isDigit(c)) {
            return this.scanNumber();
        }

        if(c == '%') return this.consumeFixed(TokenType.PERCENT, 1);
        if(c == '.') return this.consumeFixed(TokenType.DOT, 1);
        if(c == '(') return this.consumeFixed(TokenType.LPARENTHESIS, 1);
        if(c == ')') return this.consumeFixed(TokenType.RPARENTHESIS, 1);
        if(c == ',') return this.consumeFixed(TokenType.COMMA, 1);
        if(c == '+') return this.consumeFixed(TokenType.PLUS, 1);
        if(c == '-') return this.consumeFixed(TokenType.MINUS, 1);
        if(c == '*') return this.consumeFixed(TokenType.STAR, 1);
        if(c == '/') return this.consumeFixed(TokenType.SLASH, 1);

        if(this.isIdentStart(c)) {
            return this.scanIdent();
        }

        return this.scanLiteralRun();
    }

    private Token scanWhitespace() {
        int start = pos;

        while(!this.isAtEnd() && Character.isWhitespace(this.peekChar())) {
            this.advancePos(1);
        }
        return Token.of(TokenType.WHITESPACE, source.substring(start, pos), start, pos);
    }

    private Token scanQuotedString() {
        int start = pos;
        this.advancePos(1);
        StringBuilder sb = new StringBuilder();

        while(!this.isAtEnd() && this.peekChar() != '"') {
            char c = this.peekChar();
            if(c == '\\' && peekChar(1) == '"') {
                sb.append('"');
                this.advancePos(2);
            } else {
                sb.append(c);
                this.advancePos(1);
            }
        }

        if(this.isAtEnd()) {
            throw new PlaceholderParseException(
                    "Unterminated stirng literal starting at position " + start, start
            );
        }

        this.advancePos(1);

        return Token.of(TokenType.STRING, sb.toString(), start, pos);
    }

    private Token scanNumber() {
        int start = pos;

        while(!this.isAtEnd() && Character.isDigit(this.peekChar())) {
            this.advancePos(1);
        }

        if(
                !this.isAtEnd()
                && this.peekChar() == '.'
                && this.hasNext(1)
                && Character.isDigit(this.peekChar(1))
        ) {
            this.advancePos(1);

            while(!this.isAtEnd() && Character.isDigit(peekChar())) {
                this.advancePos(1);
            }
        }

        return Token.of(TokenType.NUMBER, source.substring(start, pos), start, pos);
    }

    private Token scanIdent() {
        int start = pos;

        while(!this.isAtEnd() && this.isIdentPart(this.peekChar())) {
            this.advancePos(1);
        }

        return Token.of(TokenType.IDENTIFIER, source.substring(start, pos), start, pos);
    }

    private Token scanLiteralRun() {
        int start = pos;
        StringBuilder sb = new StringBuilder();

        while(!this.isAtEnd() && !this.startsToken(this.peekChar())) {
            sb.append(this.peekChar());
            this.advancePos(1);
        }

        if(sb.isEmpty()) {
            char c = peekChar();
            this.advancePos(1);
            return Token.of(TokenType.LITERAL, String.valueOf(c), start, pos);
        }

        return Token.of(TokenType.LITERAL, sb.toString(), start, pos);
    }

    ///

    private boolean isIdentStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isIdentPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private boolean startsToken(char c) {
        return c == '%'
                || c == '<'
                || c == '>'
                || c == '\\'
                || c == '"'
                || Character.isWhitespace(c);
    }

    ///

    private boolean isAtEnd() {
        return pos >= source.length();
    }

    private boolean hasNext(int offset) {
        return pos + offset < source.length();
    }

    private char peekChar() {
        return pos < source.length() ? source.charAt(pos) : '\0';
    }

    private char peekChar(int offset) {
        return (pos + offset) < source.length() ? source.charAt(pos + offset) : '\0';
    }

    private void advancePos(int count) {
        pos += count;
    }

    private Token consumeFixed(TokenType type, int length) {
        int start = pos;
        this.advancePos(length);
        return Token.of(type, source.substring(start, pos), start, pos);
    }

    public static PlaceholderTokenizer of(String source) {
        return new PlaceholderTokenizer(source);
    }
}
