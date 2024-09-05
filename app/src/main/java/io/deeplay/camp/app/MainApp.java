package io.deeplay.camp.app;

import java.io.IOException;

import io.deeplay.camp.app.service.GameJoinService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {
  private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("view/main-view.fxml"));
      Scene scene = new Scene(fxmlLoader.load());
      primaryStage.setTitle("Heroes");
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (IOException e) {
      logger.error("Ошибка с доступом к ресурсам!");
    }
  }
}
