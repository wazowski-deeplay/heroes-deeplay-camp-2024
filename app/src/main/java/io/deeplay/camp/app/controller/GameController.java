package io.deeplay.camp.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.app.Client;
import io.deeplay.camp.app.model.GameModel;
import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.core.dto.client.game.MakeMoveDto;
import io.deeplay.camp.core.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.core.dto.server.ErrorGameResponseDto;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.mechanics.GameStage;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import java.io.IOException;
import java.util.Stack;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;

public class GameController {
  /** Модель текущей игры. */
  @Setter private GameModel gameModel;

  /** Верхняя панель с карточками. */
  @FXML private GridPane topGridPane;

  /** Нижняя панель с карточками. */
  @FXML private GridPane bottomGridPane;

  /** Кнопка передачи хода. */
  @FXML private Button passTurnButton;

  /** Стэк кликов для обработки ходов */
  private final Stack<Position> clickStack;

  /** Флаг для проверки загрузки состояния movement. */
  boolean movementIsLoaded;

  /** Флаг для проверки загрузки состояния placement. */
  boolean placementIsLoaded;

  /** Контроллеры состояния placement. */
  private final UnitPlacementController[][] unitPlacementControllers;

  /** Контроллеры состояния movement. */
  private final UnitMovementController[][] unitMovementControllers;

  /** Конструктор. */
  public GameController() {
    unitPlacementControllers = new UnitPlacementController[Board.COLUMNS][Board.ROWS];
    unitMovementControllers = new UnitMovementController[Board.COLUMNS][Board.ROWS];
    clickStack = new Stack<>();
    movementIsLoaded = false;
    placementIsLoaded = false;
  }

  /** Метод загрузки состояния movement. */
  public void loadUnitPlacementView() {
    loadViews("/io/deeplay/camp/app/view/unit-placement-view.fxml", topGridPane, 0);
    loadViews("/io/deeplay/camp/app/view/unit-placement-view.fxml", bottomGridPane, 2);
    placementIsLoaded = true;
  }

  /** Метод загрузки состояния placement. */
  public void loadUnitMovementView() {
    loadViews("/io/deeplay/camp/app/view/unit-movement-view.fxml", topGridPane, 0);
    loadViews("/io/deeplay/camp/app/view/unit-movement-view.fxml", bottomGridPane, 2);
    movementIsLoaded = true;
  }

  /**
   * Метод загрузки карточек по параметрам.
   *
   * @param fxmlPath Путь к представлению карточки.
   * @param gridPane Панель, на которую выгружаются карточки.
   * @param startRow Смещение для корректной записи в массив контроллеров нижней панели.
   */
  private void loadViews(String fxmlPath, GridPane gridPane, int startRow) {
    gridPane.getChildren().clear();
    for (int row = 0; row < 2; row++) {
      for (int col = 0; col < 3; col++) {
        try {
          FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
          Pane view = loader.load();
          if (fxmlPath.contains("unit-placement")) {
            UnitPlacementController controller = loader.getController();
            controller.setGameController(this);
            gridPane.add(view, col, row);
            unitPlacementControllers[col][row + startRow] = controller;
          } else if (fxmlPath.contains("unit-movement")) {
            UnitMovementController controller = loader.getController();
            controller.setGameController(this);
            gridPane.add(view, col, row);
            unitMovementControllers[col][row + startRow] = controller;
            controller.setX(col);
            controller.setY(row + startRow);
            controller.setClickStack(clickStack);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Метод обновления всей игровой панели.
   *
   * @param gameState Состояние, которое необходимо отобразить.
   */
  public void updateGamePane(GameState gameState) {
    updateGameModel(gameState);
    switch (gameModel.getGameStage()) {
      case PLACEMENT_STAGE:
        if (unitPlacementControllers[0][0] == null) {
          loadUnitPlacementView();
        }
        setWaiting(false);
        drawThisPlayerPlacement(gameState);
        drawEnemyPlayerPlacement(gameState);
        updatePanesDisable();
        break;
      case MOVEMENT_STAGE:
        if (!movementIsLoaded) {
          loadUnitMovementView();
        }
        drawThisPlayerMovement(gameState);
        drawEnemyPlayerMovement(gameState);
        updatePanesDisable();
        break;
    }
  }

  /**
   * Метод обновления игровой модели.
   *
   * @param gameState Состояние, которое нужно перенести в gameModel.
   */
  private void updateGameModel(GameState gameState) {
    gameModel.setCurrentBoard(gameState.getCurrentBoard());
    gameModel.setCurrentPlayer(gameState.getCurrentPlayer());
    gameModel.setGameStage(gameState.getGameStage());
  }

  /** Метод, блокирующий панели, на который нельзя нажать в данный момент. */
  private void setUnitsDisable() {
    Board board = gameModel.getCurrentBoard();
    Unit[][] units = board.getUnits();
    for (int row = 0; row < Board.ROWS; row++) {
      for (int col = 0; col < Board.COLUMNS; col++) {
        if (units[col][row] != null) {
          int controllerCol =
              gameModel.getCurrentPlayer() == PlayerType.FIRST_PLAYER
                  ? Board.COLUMNS - 1 - col
                  : col;
          int controllerRow =
              gameModel.getCurrentPlayer() == PlayerType.FIRST_PLAYER ? Board.ROWS - 1 - row : row;
          if (units[col][row].getCurrentHp() <= 0) {
            unitMovementControllers[controllerCol][controllerRow].getPane().setDisable(true);
          }
          if (units[col][row].isMoved() && controllerRow >= 2) {
            unitMovementControllers[controllerCol][controllerRow].getPane().setDisable(true);
          }
        }
      }
    }
  }

  /**
   * Метод отрисовки карточек текущего игрока в состоянии placement.
   *
   * @param gameState Игровое состояние, которое нужно отрисовать.
   */
  private void drawThisPlayerPlacement(GameState gameState) {
    drawPlayerUnits(gameState, 0, Board.ROWS / 2, unitPlacementControllers);
  }

  /**
   * Метод отрисовки карточек текущего игрока в состоянии movement.
   *
   * @param gameState Игровое состояние, которое нужно отрисовать.
   */
  private void drawThisPlayerMovement(GameState gameState) {
    drawPlayerUnits(gameState, 0, Board.ROWS / 2, unitMovementControllers);
  }

  /**
   * Метод отрисовки карточек врага в состоянии placement.
   *
   * @param gameState Игровое состояние, которое нужно отрисовать.
   */
  private void drawEnemyPlayerPlacement(GameState gameState) {
    drawPlayerUnits(gameState, Board.ROWS / 2, Board.ROWS, unitPlacementControllers);
  }

  /**
   * Метод отрисовки карточек врага в состоянии movement.
   *
   * @param gameState Игровое состояние, которое нужно отрисовать.
   */
  private void drawEnemyPlayerMovement(GameState gameState) {
    drawPlayerUnits(gameState, Board.ROWS / 2, Board.ROWS, unitMovementControllers);
  }

  /**
   * Реализация метода отрисовки игровой панели по параметрам..
   *
   * @param gameState Игровое состояние, которое нужно отрисовать.
   * @param startY Значение Y с которого необходимо стартовать в массиве контроллеров.
   * @param endY Значение Y на котором необходимо закончить в массиве контроллеров.
   * @param controllers Массив контроллеров.
   * @param <T> Тип контроллеров.
   */
  private <T> void drawPlayerUnits(GameState gameState, int startY, int endY, T[][] controllers) {
    Board board = gameState.getBoard();
    boolean isFirstPlayer = gameModel.getThisPlayer() == PlayerType.FIRST_PLAYER;
    for (int x = 0; x < Board.COLUMNS; x++) {
      for (int y = startY; y < endY; y++) {
        Unit unit = board.getUnit(x, y);
        if (unit != null) {
          int controllerX = isFirstPlayer ? Board.COLUMNS - 1 - x : x;
          int controllerY = isFirstPlayer ? Board.ROWS - 1 - y : y;
          if (controllers[controllerX][controllerY] instanceof UnitPlacementController) {
            ((UnitPlacementController) controllers[controllerX][controllerY]).setUnit(unit);
          } else if (controllers[controllerX][controllerY] instanceof UnitMovementController) {
            ((UnitMovementController) controllers[controllerX][controllerY]).setUnit(unit);
          }
        }
      }
    }
  }

  /**
   * Метод, связывающий чекбоксы. Если выбран генерал - другие чекбоксы блокируются.
   *
   * @param sourceController Контроллер панели с юнитом и чекбоксом.
   */
  public void updateGeneralCheckboxes(UnitPlacementController sourceController) {
    for (int row = Board.ROWS / 2; row < Board.ROWS; row++) {
      for (int col = 0; col < Board.COLUMNS; col++) {
        UnitPlacementController controller = unitPlacementControllers[col][row];
        if (controller != sourceController) {
          controller.disableGeneralCheckbox(sourceController.isGeneralSelected());
        }
      }
    }
    updatePassTurnButtonState();
  }

  /** Метод обновления кнопки передачи хода. */
  void updatePassTurnButtonState() {
    if (gameModel.getGameStage() == GameStage.PLACEMENT_STAGE) {
      boolean allComboboxesSelected = true;
      boolean generalSelected = false;
      for (int row = Board.ROWS / 2; row < Board.ROWS; row++) {
        for (int col = 0; col < Board.COLUMNS; col++) {
          UnitPlacementController controller = unitPlacementControllers[col][row];
          if (!controller.isComboboxSelected()) {
            allComboboxesSelected = false;
          }
          if (controller.isGeneralSelected()) {
            generalSelected = true;
          }
        }
      }
      passTurnButton.setDisable(!(allComboboxesSelected && generalSelected));
    } else if (gameModel.getGameStage() == GameStage.MOVEMENT_STAGE) {
      passTurnButton.setDisable(!gameModel.isThisPlayerTurn());
    }
  }

  /** Метод, обрабатывающий кнопку передачи хода. */
  public void passTurn() {
    switch (gameModel.getGameStage()) {
      case PLACEMENT_STAGE -> passPlacementTurn();
      case MOVEMENT_STAGE -> passMovementTurn();
    }
  }

  /** Метод передачи хода в состоянии movement. */
  private void passMovementTurn() {
    sendChangePlayerDto();
  }

  /** Метод передачи хода в состоянии placement. */
  private void passPlacementTurn() {
    boolean isFirstPlayer = gameModel.getThisPlayer() == PlayerType.FIRST_PLAYER;
    int startY = 2;
    int endY = 4;
    for (int controllerX = 0; controllerX < Board.COLUMNS; controllerX++) {
      for (int controllerY = startY; controllerY < endY; controllerY++) {
        int boardX = isFirstPlayer ? Board.COLUMNS - 1 - controllerX : controllerX;
        int boardY = isFirstPlayer ? Board.ROWS - controllerY - 1 : controllerY;
        sendPlaceUnitDto(boardX, boardY, controllerX, controllerY);
      }
    }
    sendChangePlayerDto();
  }

  /**
   * Метод отправки дто с постановкой юнита.
   *
   * @param boardX X на доске.
   * @param boardY Y на доске.
   * @param controllerX X в массиве контроллеров.
   * @param controllerY Y в массиве контроллеров.
   */
  private void sendPlaceUnitDto(int boardX, int boardY, int controllerX, int controllerY) {
    PlaceUnitDto placeUnitDto =
        new PlaceUnitDto(
            gameModel.getGamePartyId(),
            boardX,
            boardY,
            unitPlacementControllers[controllerX][controllerY].getUnitType(),
            !(controllerX == Board.COLUMNS - 1 && controllerY == 3),
            unitPlacementControllers[controllerX][controllerY].isGeneralSelected());
    try {
      String request = JsonConverter.serialize(placeUnitDto);
      Client.getInstance().sendMessage(request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Метод для установки ожидания второга игрока.
   *
   * @param isWaiting Ждет/не ждет.
   */
  public void setWaiting(boolean isWaiting) {
    setBottomGrindDisable(isWaiting);
    setTopGrindDisable(isWaiting);
  }

  /** Метод отправки дто передачи хода. */
  private void sendChangePlayerDto() {
    ChangePlayerDto changePlayerDto = new ChangePlayerDto(gameModel.getGamePartyId());
    try {
      String request = JsonConverter.serialize(changePlayerDto);
      Client.getInstance().sendMessage(request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /** Метод обновления доски после клика. */
  public void updateBoardAfterClick() {
    if (clickStack.size() == 1) {
      Position clickPosition = clickStack.peek();
      int x = clickPosition.x();
      int y = clickPosition.y();
      if (unitMovementControllers[x][y].getUnitButton().getText().equals("Healer")) {
        setTopGrindDisable(true);
        setDisabledAllExceptPosition(x, y, false);
      } else {
        setDisabledAllExceptPosition(x, y, true);
        setTopGrindDisable(false);
        setUnitsDisable();
      }
    } else if (clickStack.size() == 2) {
      if (clickStack.get(0).x() == clickStack.get(1).x()
          && clickStack.get(0).y() == clickStack.get(1).y()) {
        updatePanesDisable();
        return;
      }
      sendMoveDto();
      updatePanesDisable();
    }
  }

  /** Метод отправки дто с ходом. */
  private void sendMoveDto() {
    Position to = clickStack.get(1);
    Position from = clickStack.get(0);
    int fromX =
        gameModel.getThisPlayer() == PlayerType.FIRST_PLAYER
            ? Board.COLUMNS - 1 - from.x()
            : from.x();
    int fromY =
        gameModel.getThisPlayer() == PlayerType.FIRST_PLAYER ? Board.ROWS - 1 - from.y() : from.y();
    int toX =
        gameModel.getThisPlayer() == PlayerType.FIRST_PLAYER ? Board.COLUMNS - 1 - to.x() : to.x();
    int toY =
        gameModel.getThisPlayer() == PlayerType.FIRST_PLAYER ? Board.ROWS - 1 - to.y() : to.y();
    unitMovementControllers[from.x()][from.y()].setMoved(true);
    MakeMoveDto makeMoveDto = new MakeMoveDto(gameModel.getGamePartyId(), fromX, fromY, toX, toY);
    try {
      String message = JsonConverter.serialize(makeMoveDto);
      Client.getInstance().sendMessage(message);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Метод, блокирующий/разблокирующий все панели, кроме указанной.
   *
   * @param exceptX X не блокирующейся панели.
   * @param exceptY Y не блокирующейся панели.
   * @param disable Блокировать/разблокировать.
   */
  private void setDisabledAllExceptPosition(int exceptX, int exceptY, boolean disable) {
    for (int row = 0; row < bottomGridPane.getRowCount(); row++) {
      for (int col = 0; col < bottomGridPane.getColumnCount(); col++) {
        if (row == exceptY && col == exceptX) {
          bottomGridPane
              .getChildren()
              .get(row * bottomGridPane.getColumnCount() + col)
              .setDisable(!disable);
        } else {
          bottomGridPane
              .getChildren()
              .get(row * bottomGridPane.getColumnCount() + col)
              .setDisable(disable);
        }
      }
    }
  }

  /**
   * Метод, блокирующий нижние панели.
   *
   * @param disable Блокировать/разблокировать.
   */
  public void setBottomGrindDisable(boolean disable) {
    bottomGridPane.getChildren().forEach(node -> node.setDisable(disable));
  }

  /**
   * Метод, блокирующий верхние панели.
   *
   * @param disable Блокировать/разблокировать.
   */
  public void setTopGrindDisable(boolean disable) {
    topGridPane.getChildren().forEach(node -> node.setDisable(disable));
  }

  /** Метод, обновляющий блокировку панелей для каждой игровой ситуации. */
  public void updatePanesDisable() {
    switch (gameModel.getGameStage()) {
      case PLACEMENT_STAGE -> {
        if (gameModel.isThisPlayerTurn()) {
          setTopGrindDisable(true);
          setBottomGrindDisable(false);
        } else {
          setTopGrindDisable(true);
          setBottomGrindDisable(true);
        }
      }
      case MOVEMENT_STAGE -> {
        if (gameModel.isThisPlayerTurn()) {
          setTopGrindDisable(true);
          setBottomGrindDisable(false);
        } else {
          setTopGrindDisable(true);
          setBottomGrindDisable(true);
        }
        setUnitsDisable();
        clickStack.clear();
      }
    }
  }

  public void showError(ErrorGameResponseDto errorGameResponseDto) {
    Stage errorStage = new Stage();
    errorStage.initModality(Modality.APPLICATION_MODAL);
    errorStage.initStyle(StageStyle.UTILITY);
    errorStage.setTitle("Error");

    Label errorLabel = new Label(errorGameResponseDto.getMessage());
    errorLabel.setWrapText(true);

    VBox vbox = new VBox(errorLabel);
    vbox.setSpacing(10);
    vbox.setPadding(new Insets(20));

    Scene scene = new Scene(vbox);
    errorStage.setScene(scene);

    errorStage.setX(
        topGridPane.getScene().getWindow().getX()
            + topGridPane.getScene().getWindow().getWidth() / 2
            - 200);
    errorStage.setY(
        topGridPane.getScene().getWindow().getY()
            + topGridPane.getScene().getWindow().getHeight() / 2
            - 100);
    errorStage.setWidth(400);
    errorStage.setHeight(200);

    errorStage.showAndWait();
  }
}
