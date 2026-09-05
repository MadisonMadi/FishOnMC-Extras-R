package dannypx.foe.placeholder.token;

public class PlaceholderParseException extends RuntimeException {
    public final int position;

    public PlaceholderParseException(String message) {
        this(message, -1);
    }

    public PlaceholderParseException(String message, int position) {
        super(message);
        this.position = position;
    }
}
