module io.deeplay.camp.core {
  exports io.deeplay.camp.core.dto;
  exports io.deeplay.camp.core.dto.server;
  exports io.deeplay.camp.core.dto.client;
  exports io.deeplay.camp.core.dto.client.game;
  exports io.deeplay.camp.core.dto.client.party;
  exports io.deeplay.camp.core.dto.client.connection;
  
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires static lombok;
  requires io.deeplay.camp.game;

  opens io.deeplay.camp.core.dto.client to
      com.fasterxml.jackson.databind;
  opens io.deeplay.camp.core.dto.client.party to
      com.fasterxml.jackson.databind;
  opens io.deeplay.camp.core.dto.client.game to
      com.fasterxml.jackson.databind;
  opens io.deeplay.camp.core.dto.client.connection to
      com.fasterxml.jackson.databind;
  opens io.deeplay.camp.core.dto.server to
      com.fasterxml.jackson.databind;
}
