package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

import java.util.UUID;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;
    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    public RegisterResult register(RegisterRequest req) throws DataAccessException,
            BadRequestException, AlreadyTakenException {
        if (req.username() == null || req.password() == null || req.email() == null) {
            throw new BadRequestException();
        }
        UserData userData = userDAO.getUser(req.username());
        if (userData == null) {
            userDAO.createUser(req.username(), req.password(), req.email());
            String authToken = generateToken();
            authDAO.createAuth(authToken, req.username());
            return new RegisterResult(req.username(), authToken);
        }
        throw new AlreadyTakenException();
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        if (req.username() == null || req.password() == null) {
            throw new BadRequestException();
        }
        UserData userData = userDAO.getUser(req.username());
        if (userData == null) throw new UnauthorizedException();
        if (!req.password().equals(userData.password())) {
            throw new UnauthorizedException();
        }
        String authToken = generateToken();
        authDAO.createAuth(authToken, req.username());
        return new LoginResult(req.username(), authToken);
    }

    public LogoutResult logout(LogoutRequest req) throws DataAccessException {
        if (req.authToken() == null) {
            throw new BadRequestException();
        }
        AuthData authData = authDAO.getAuth(req.authToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        authDAO.deleteAuth(authData.authToken());
        return new LogoutResult();
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
