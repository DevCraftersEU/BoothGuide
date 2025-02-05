package com.westers.boothguide.rest;

import com.westers.boothguide.services.impl.exceptions.ApplicationEntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class AbstractRestController {

    private final static Logger logger = LoggerFactory.getLogger(AbstractRestController.class);

    @ExceptionHandler(WrongMethodException.class)
    private ResponseEntity<String> handleWrongMethodException() {
        logger.info("Returning wrong method answer");
        return ResponseEntity.badRequest().body("Please use the correct HTTP method");
    }

    @ExceptionHandler(ApplicationEntityNotFoundException.class)
    private ResponseEntity<String> handleEntityNotFoundException() {
        logger.info("Returning not found entity answer");
        return ResponseEntity.notFound().build();
    }

    static class WrongMethodException extends RuntimeException {
    }

}
