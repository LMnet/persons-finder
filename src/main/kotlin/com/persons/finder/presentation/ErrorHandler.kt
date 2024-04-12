package com.persons.finder.presentation

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ErrorBody> {
        return ResponseEntity.badRequest().body(ErrorBody("Validation error: ${e.message}"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorBody> {
        val errors = e.bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Invalid value" }
        return ResponseEntity.badRequest().body(ErrorBody("Validation error: $errors"))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorBody> {
        return ResponseEntity.badRequest().body(ErrorBody("Parameter conversion error: ${e.message}"))
    }
}
