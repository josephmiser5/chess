package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;

import io.javalin.http.Context;
import request.CreateGameRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import service.GameService;
import service.UserService;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);

        Gson gson = new Gson();

        javalin.post("/user", ctx -> {
            RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult res = userService.register(req);
            ctx.status(200);
            ctx.result(gson.toJson(res));
        });

        javalin.post("/session", ctx -> {
            LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult res = userService.login(req);
            ctx.status(200);
            ctx.result(gson.toJson(res));
        });

        javalin.delete("/session", ctx -> {
            LogoutRequest req = new LogoutRequest(ctx.header("authorization"));
            LogoutResult res = userService.logout(req);
            ctx.status(200);
            ctx.result(gson.toJson(res));
        });

        javalin.get("/game", _ -> {
        });

        javalin.post("/game", ctx -> {
            String authToken = ctx.header("authorization");
            CreateGameRequest body = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameRequest req = new CreateGameRequest(authToken, body.gameName());
            CreateGameResult res = gameService.createGame(req);
            ctx.status(200);
            ctx.result(gson.toJson(res));
        });

        javalin.put("/game", _ -> {
        });

        javalin.delete("/db", ctx -> {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
            ctx.status(200);
            ctx.result("{}");
        });

        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        });


        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        });

        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        });

        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
