package de.lukas.web;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.put;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class DingRestApi {

  private final static DingStore dingStore = DingStore.INSTANCE;

  public static void main(String[] args) {
    final Javalin app = Javalin.create().start(7000);

    app.routes(() -> {
      path("dinge", () -> {

        get(DingRestApi::getDinge);

        path(":id", () -> {
          get(DingRestApi::getDing);

          put(DingRestApi::putDing);

          delete(DingRestApi::deleteDing);
        });

      });
    });
  }

  private static void getDinge(final Context ctx) {
    ctx.json(dingStore.getAll());
  }

  private static void getDing(final Context ctx) {
    ctx.json(dingStore.get(Integer.valueOf(ctx.pathParam("id")).intValue()));
  }

  private static void putDing(final Context ctx) {
    final Integer id = Integer.valueOf(ctx.pathParam("id"));
    final Ding ding = ctx.bodyAsClass(Ding.class);

    if (id.intValue() != ding.getId()) {
      ctx.status(400);
      ctx.result("unterschiedliche ids");
    } else {
      dingStore.put(ding);
    }
  }

  private static void deleteDing(final Context ctx) {
    dingStore.remove(Integer.valueOf(ctx.pathParam("id")));
  }

}
