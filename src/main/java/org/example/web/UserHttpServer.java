package org.example.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.errors.UserNotFoundException;
import org.example.model.User;
import org.example.service.UserService;

import java.util.Map;

public class UserHttpServer extends AbstractVerticle {
    private final UserService service;
    private final int port;

    public UserHttpServer(UserService service, int port) {
        this.service = service;
        this.port = port;
    }

    public static void start(Vertx vertx, UserService service, int port, Promise<Void> startPromise) {
        vertx.deployVerticle(new UserHttpServer(service, port), ar -> {
            if (ar.succeeded()) startPromise.complete();
            else startPromise.fail(ar.cause());
        });
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/users").handler(context -> {
            try {
                JsonObject body = context.body().asJsonObject();
                String name = body.getString("name");
                String email = body.getString("email");
                User created = service.createUser(name, email);
                JsonObject resp = new JsonObject()
                        .put("id", created.getId())
                        .put("name", created.getName())
                        .put("email", created.getEmail());
                context.response()
                        .setStatusCode(201)
                        .putHeader("Content-Type", "application/json")
                        .end(resp.encode());
            } catch (IllegalArgumentException e) {
                badRequest(context, e.getMessage());
            } catch (Exception e) {
                internalError(context, e);
            }
        });



        router.get("/users/:id").handler(context -> {
            try {
                long id = parseId(context.pathParam("id"));
                User u = service.getUser(id);
                JsonObject resp = new JsonObject()
                        .put("id", u.getId())
                        .put("name", u.getName())
                        .put("email", u.getEmail());
                context.response()
                        .putHeader("Content-Type", "application/json")
                        .end(resp.encode());
            } catch (UserNotFoundException e) {
                notFound(context);
            } catch (IllegalArgumentException e) {
                badRequest(context, e.getMessage());
            } catch (Exception e) {
                internalError(context, e);
            }
        });

        router.put("/users/:id/email").handler(context -> {
            try {
                long id = parseId(context.pathParam("id"));
                JsonObject body = context.body().asJsonObject();
                String email = body.getString("email");
                User u = service.updateEmail(id, email);
                JsonObject resp = new JsonObject()
                        .put("id", u.getId())
                        .put("name", u.getName())
                        .put("email", u.getEmail());
                context.response()
                        .putHeader("Content-Type", "application/json")
                        .end(resp.encode());
            } catch (UserNotFoundException e) {
                notFound(context);
            } catch (IllegalArgumentException e) {
                badRequest(context, e.getMessage());
            } catch (Exception e) {
                internalError(context, e);
            }
        });


        router.delete("/users/:id").handler(context -> {
            try {
                long id = parseId(context.pathParam("id"));
                service.deleteUser(id);
                context.response().setStatusCode(204).end();
            } catch (UserNotFoundException e) {
                notFound(context);
            } catch (IllegalArgumentException e) {
                badRequest(context, e.getMessage());
            } catch (Exception e) {
                internalError(context, e);
            }
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, ar -> {
                    if (ar.succeeded()) startPromise.complete();
                    else startPromise.fail(ar.cause());
                });
    }

    private static long parseId(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("id needs to ne a number");
        }
    }

    private static void badRequest(io.vertx.ext.web.RoutingContext context, String msg) {
        context.response().setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(new JsonObject().put("error", msg).encode());

    }

    private static void notFound(io.vertx.ext.web.RoutingContext ctx) {
        ctx.response().setStatusCode(404).end();
    }

    private static void internalError(io.vertx.ext.web.RoutingContext context, Throwable e) {
        context.response().setStatusCode(500)
                .putHeader("Content-Type", "application/json")
                .end(new JsonObject().put("error", "internal").encode());

     }
}