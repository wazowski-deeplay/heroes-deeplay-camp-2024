module io.deeplay.camp.app {
  requires javafx.controls;
  requires javafx.fxml;
  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires io.deeplay.camp.core;
  requires io.deeplay.camp.game;
  requires static lombok;
  requires com.fasterxml.jackson.core;

  opens io.deeplay.camp.app to
      javafx.fxml;
  opens io.deeplay.camp.app.controller to
      javafx.fxml;
  opens io.deeplay.camp.app.service to
      javafx.fxml;

  exports io.deeplay.camp.app.service;
  exports io.deeplay.camp.app.controller;
  exports io.deeplay.camp.app;
}
