package ru.yandex.practicum.exception;

public class PaymentServiceException extends RuntimeException {
  public PaymentServiceException(String message) {
    super(message);
  }
}
