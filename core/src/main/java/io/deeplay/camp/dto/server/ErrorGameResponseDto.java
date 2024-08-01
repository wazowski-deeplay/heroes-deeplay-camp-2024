package io.deeplay.camp.dto.server;

import io.deeplay.camp.exceptions.ErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ErrorGameResponseDto extends ServerDto {
    private ErrorCode errorCode;
    private String message;

    public ErrorGameResponseDto(ErrorCode errorCode, String message) {
        super(ServerDtoType.ERROR_GAME_INFO);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }



    public String getMessage() {
        return message;
    }
}
