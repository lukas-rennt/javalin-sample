package de.lukas.web;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.put;

import io.javalin.Javalin;

public class DingRestApi {

  private final static DingStore dingStore = DingStore.INSTANCE;

  public static void main(String[] args) {
    final Javalin app = Javalin.create().start(7000);

    app.routes(() -> {
      path("dinge", () -> {

        get(ctx -> ctx.json(dingStore.getAll()));

        path(":id", () -> {
          get(ctx -> ctx.json( //
              dingStore.get(Integer.valueOf(ctx.pathParam("id")))));
        });

        put(ctx -> {
          dingStore.put(ctx.bodyAsClass(Ding.class));

          ctx.status(200);
        });

      });
    });
  }
}
