package io.deeplay.camp.botfarm.bots.max_MinMax;

import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;

import java.util.List;
import java.util.Random;

public class ResultFunction implements UtilityFunction {
    @Override
    public double getUtility(GameState gameState, PlayerType playerType) {
        double ownHp = 0;
        double enemyHp = 0;

        List<Position> ownUnitPositions;
        List<Position> enemyUnitPositions;


        if (playerType == PlayerType.FIRST_PLAYER) {
            ownUnitPositions = gameState.getBoard().enumerateUnits(0, 2);
            enemyUnitPositions = gameState.getBoard().enumerateUnits(2, 4);
        } else {
            ownUnitPositions = gameState.getBoard().enumerateUnits(2, 4);
            enemyUnitPositions = gameState.getBoard().enumerateUnits(0, 2);
        }

        for (Position p : ownUnitPositions) {
            ownHp += gameState.getBoard().getUnit(p.x(), p.y()).getCurrentHp();
        }


        for (Position p : enemyUnitPositions) {
            enemyHp += gameState.getBoard().getUnit(p.x(), p.y()).getCurrentHp();
        }

        // Возвращаем разницу в здоровье своих юнитов и юнитов противника
        // Чем больше разница, тем лучше для текущего игрока.
        // Отрицательное значение — это плохо для текущего игрока.
        return ownHp - enemyHp;
    }
}
