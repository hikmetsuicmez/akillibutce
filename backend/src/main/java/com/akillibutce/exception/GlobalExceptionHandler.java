package com.akillibutce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> dogrulamaHatasiisle(
            MethodArgumentNotValidException ex) {
        Map<String, String> hatalar = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(hata -> {
            String alan = ((FieldError) hata).getField();
            String mesaj = hata.getDefaultMessage();
            hatalar.put(alan, mesaj);
        });

        Map<String, Object> yanit = new HashMap<>();
        yanit.put("zaman", LocalDateTime.now());
        yanit.put("durum", HttpStatus.BAD_REQUEST.value());
        yanit.put("hatalar", hatalar);
        return ResponseEntity.badRequest().body(yanit);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> gecersizArgümanIsle(IllegalArgumentException ex) {
        return hataYaniti(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> gecersizDurumIsle(IllegalStateException ex) {
        return hataYaniti(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> erisimReddiIsle(AccessDeniedException ex) {
        return hataYaniti(HttpStatus.FORBIDDEN, "Bu islemi gerceklestirmek icin yetkiniz bulunmuyor.");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> kaynakBulunamadiIsle(ResourceNotFoundException ex) {
        return hataYaniti(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> genelHataIsle(Exception ex) {
        return hataYaniti(HttpStatus.INTERNAL_SERVER_ERROR, "Sunucu hatasi olustu.");
    }

    private ResponseEntity<Map<String, Object>> hataYaniti(HttpStatus durum, String mesaj) {
        Map<String, Object> yanit = new HashMap<>();
        yanit.put("zaman", LocalDateTime.now());
        yanit.put("durum", durum.value());
        yanit.put("mesaj", mesaj);
        return ResponseEntity.status(durum).body(yanit);
    }
}
