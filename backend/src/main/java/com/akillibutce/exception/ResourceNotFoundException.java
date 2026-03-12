package com.akillibutce.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mesaj) {
        super(mesaj);
    }

    public ResourceNotFoundException(String kaynak, Long id) {
        super(kaynak + " bulunamadi: id=" + id);
    }
}
