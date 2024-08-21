package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.mechanics.GameState;

public record StateChance(GameState gameState, double chance) {}
