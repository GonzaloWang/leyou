package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // 给所有带@controller 加了 try
public class ControllerExceptionAdvice {
    @ExceptionHandler(LyException.class) // 相当于catch
    public ResponseEntity<String> handlerException(LyException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }
}
