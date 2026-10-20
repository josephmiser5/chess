package service.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Error: unauthorized");
    }
}
