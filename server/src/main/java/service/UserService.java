package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import java.util.UUID;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;
    public UserService(UserDAO userDAO_, AuthDAO authDAO_) {
        userDAO = userDAO_;
        authDAO = authDAO_;
    }
    public RegisterResult register(RegisterRequest req) throws DataAccessException,
            BadRequestException, AlreadyTakenException {
        if (req.username() == null || req.password() == null || req.email() == null) throw new BadRequestException("Error: bad request");
        UserData userData = userDAO.getUser(req.username());
        if (userData == null) {
            userDAO.createUser(req.username(), req.password(), req.email());
            String authToken = generateToken();
            authDAO.createAuth(authToken, req.username());
            return new RegisterResult(req.username(), authToken);
        }
        throw new AlreadyTakenException("Error: already taken");
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
