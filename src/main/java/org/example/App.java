package org.example;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.example.store.InMemoryUserStore;
import org.example.web.UserHttpServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        UserService service = new UserServiceImpl(new InMemoryUserStore());

        Vertx vertx = Vertx.vertx();
        Promise<Void> p = Promise.promise();
        UserHttpServer.start(vertx, service, port, p);
        p.future().onComplete(ar -> {
            if (ar.succeeded()) {
                System.out.println("HTTP server started at port " + port);
            } else {
                ar.cause().printStackTrace();
                vertx.close();
            }
        });
    }}
