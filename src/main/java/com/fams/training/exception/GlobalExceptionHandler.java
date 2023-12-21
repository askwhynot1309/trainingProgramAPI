package com.fams.training.exception;

import com.fams.training.DTO.Message;
import com.fams.training.DTO.ResponseMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseMessage> handleBadRequestException(BadRequestException ex) {
        String errorMessage = ex.getMessage();

        logger.error(errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseMessage(HttpStatus.BAD_REQUEST.value(), null, Message.BAD_REQUEST)
        );
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ResponseMessage> handleInternalServerErrorException(InternalServerErrorException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
        );
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ResponseMessage> handleInvalidFileException(InvalidFileException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseMessage(HttpStatus.BAD_REQUEST.value(), null, Message.INVALID_FILE)
        );
    }

    @ExceptionHandler(NotFoundContentException.class)
    public ResponseEntity<ResponseMessage> handleNotFoundContentException(NotFoundContentException ex) {
        String errorMessage = ex.getMessage();

        logger.error(errorMessage);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseMessage> handleIllegalStateException(IllegalStateException ex) {
        String errorMessage = ex.getMessage();

        logger.error(errorMessage);

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                new ResponseMessage(HttpStatus.EXPECTATION_FAILED.value(), null, Message.UPDATE_FAIL_BY_STATUS)
        );
    }


//    @ExceptionHandler(NoContentException.class)
//    public ResponseEntity<?> handleNoContentException(NoContentException ex) {
//        String errorMessage = ex.getMessage();
//
//        logger.error(errorMessage);
//
//        ResponseMessage responseObject = ResponseMessage.builder()
//                .code(HttpStatus.NO_CONTENT.value())
//                .message(Message.NO_CONTENT)
//                .build();
//
//        return new ResponseEntity<>(responseObject, HttpStatus.NO_CONTENT);
//    }


}

