package ru.practicum.shareit.exception;

/**
 * Исулючение отсутствия объекта
 */
public class ObjectNotFountException extends LoggingException {
    public ObjectNotFountException(String message, String className) {
        super(message, className);
    }
}
