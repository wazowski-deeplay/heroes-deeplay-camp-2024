package io.deeplay.camp.exceptions;

import lombok.Getter;

@Getter
public class GameManagerException extends Exception {
    public ConnectionErrorCode connectionErrorCode;
    public GameManagerException(ConnectionErrorCode connectionErrorCode){
        super(connectionErrorCode.getMessage());
        this.connectionErrorCode = connectionErrorCode;
    }
}
