package de.lukas.web;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.put;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class DingApplication {

  private final static String DEAFULT_CONF_DIR = "/etc/javalin-sample/conf";
  private final static String APPLICATION_CONF_FILENAME = "application.properties";
  private final static String LOGGING_CONF_FILENAME = "logging.properties";

  private final static Logger LOGGER = Logger.getLogger(DingApplication.class.getCanonicalName());

  private final Javalin javalin;
  private final DingStore dingStore;
  private final int port;

  public static void main(final String[] args) throws Exception {
    final String configDir = getConfigDir(args);

    initLogging(configDir);

    final Properties applicationConfig = getApplicationConfig(configDir);

    new DingApplication(applicationConfig).start();
  }

  private static String getConfigDir(final String[] args) {
    return args.length >= 1 ? args[0] : DEAFULT_CONF_DIR;
  }

  private static String getApplicationConfQualifiedFilename(final String configDir) {
    return configDir + "/" + APPLICATION_CONF_FILENAME;
  }

  private static String getLoggingConfQualifiedFilename(final String configDir) {
    return configDir + "/" + LOGGING_CONF_FILENAME;
  }

  public static void initLogging(final String configDir) {
    try {
      LogManager.getLogManager().readConfiguration( //
          new FileInputStream( //
              getLoggingConfQualifiedFilename(configDir)));
    } catch (final IOException e) {
      LOGGER.warning(() -> "no logging configuration file found; " + e.getMessage());
    }
  }

  private static Properties getApplicationConfig(final String configDir) {
    final Properties applicationConfig = new Properties();
    try {
      applicationConfig.load( //
          new FileInputStream( //
              getApplicationConfQualifiedFilename(configDir)));
    } catch (final IOException e) {
      LOGGER.warning(() -> "no application configuration file found; " + e.getMessage());
    }
    return applicationConfig;
  }

  private static DingStore createDingStore(final Properties applicationConfig) {
    return new DingStoreRedis( //
        applicationConfig.getProperty( //
            "dinge.redis.host", "localhost"), //
        Integer.valueOf( //
            applicationConfig.getProperty( //
                "dinge.redis.port", "6379")));
  }

  public DingApplication(final Properties applicationConfig) {
    this.dingStore = createDingStore(applicationConfig);

    this.javalin = addRoutes(Javalin.create());

    this.port = Integer.valueOf(applicationConfig.getProperty("rest.port", "7000"));
  }

  public void start() {
    this.javalin.start(port);
  }

  private Javalin addRoutes(final Javalin javalin) {
    return javalin.routes(() -> {
      path("dinge", () -> {

        get(this::getDinge);

        path(":id", () -> {
          get(this::getDing);

          put(this::putDing);

          delete(this::deleteDing);
        });

      });
    });
  }

  private void getDinge(final Context ctx) {
    LOGGER.info(() -> "getDinge");
    ctx.json(dingStore.getAll());
  }

  private void getDing(final Context ctx) {
    ctx.json(dingStore.get(Integer.valueOf(ctx.pathParam("id")).intValue()));
  }

  private void putDing(final Context ctx) {
    final Integer id = Integer.valueOf(ctx.pathParam("id"));
    final Ding ding = ctx.bodyAsClass(Ding.class);

    if (id.intValue() != ding.getId()) {
      ctx.status(400);
      ctx.result("unterschiedliche ids");
    } else {
      dingStore.put(ding);
    }
  }

  private void deleteDing(final Context ctx) {
    dingStore.remove(Integer.valueOf(ctx.pathParam("id")));
  }

}
