/*
   The book project lets a user keep track of different books they would like to read, are currently
   reading, have read or did not finish.
   Copyright (C) 2022  Karan Kumar

   This program is free software: you can redistribute it and/or modify it under the terms of the
   GNU General Public License as published by the Free Software Foundation, either version 3 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
   PURPOSE.  See the GNU General Public License for more details.

   You should have received a copy of the GNU General Public License along with this program.
   If not, see <https://www.gnu.org/licenses/>.
*/

package com.duke.bookproject.security.jwt.exceptionhandler;

import com.duke.bookproject.ExcludeFromJacocoGeneratedReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
@ExcludeFromJacocoGeneratedReport
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException e) {
            HttpStatus status = e.getStatus();

            ErrorResponse errorResponse = createErrorResponse(status)
                    .error(status.getReasonPhrase())
                    .message(e.getReason())
                    .build();
            return ResponseEntity.status(status).body(errorResponse);
        }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleDefaultException(Exception e) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    ErrorResponse errorResponse =
        createErrorResponse(status)
            .error("Internal Error")
            .message(
                "An internal error occurred. Please try again. "
                    + "If the error persists, please contact the support.")
            .build();

    return ResponseEntity.status(status).body(errorResponse);
  }

  public static ErrorResponse.ErrorResponseBuilder createErrorResponse(HttpStatus status) {
    return ErrorResponse.builder().timestamp(LocalDateTime.now()).status(status.value());
  }
}
