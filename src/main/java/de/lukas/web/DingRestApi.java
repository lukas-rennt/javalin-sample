package de.lukas.web;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.put;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class DingRestApi {

  private final static Logger LOGGER = Logger.getLogger(DingRestApi.class.getCanonicalName());

  private final static DingStore dingStore = DingStore.INSTANCE;

  public static void main(String[] args) throws Exception {
    try {
      LOGGER.info(() -> "before logging configuration");
      LogManager.getLogManager().readConfiguration(new FileInputStream("/etc/javalin-sample/conf/logging.properties"));
      LOGGER.info(() -> "after logging configuration");
    } catch (final IOException e) {
      LOGGER.warning(() -> "logging configuration failed; " + e.getMessage());
    }

//    LogManager.getLogManager()
//        .readConfiguration(DingRestApi.class.getClassLoader().getResourceAsStream("logging.properties"));

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
    LOGGER.info(() -> "Hallo");

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
