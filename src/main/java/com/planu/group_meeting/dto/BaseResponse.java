package com.planu.group_meeting.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuperBuilder
@Getter
public class BaseResponse {
    protected Integer resultCode;
    protected String resultMsg;


    public static ResponseEntity<BaseResponse> toResponseEntity(HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus)
                .body(BaseResponse.builder()
                        .resultCode(httpStatus.value())
                        .resultMsg(httpStatus.name())
                        .build());
    }

    public static ResponseEntity<BaseResponse> toResponseEntity(HttpStatus httpStatus, String msg) {
        return ResponseEntity.status(httpStatus)
                .body(BaseResponse.builder()
                        .resultCode(httpStatus.value())
                        .resultMsg(msg)
                        .build());
    }

}
