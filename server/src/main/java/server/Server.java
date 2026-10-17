package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;

import io.javalin.http.Context;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;

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

        Gson gson = new Gson();

        javalin.post("/user", ctx -> {
            RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult res = userService.register(req);
            ctx.status(200);
            ctx.result(gson.toJson(res));
        });

        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
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
