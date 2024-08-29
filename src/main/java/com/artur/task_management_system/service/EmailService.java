package com.artur.task_management_system.service;

public interface EmailService {
    /**
     * Отправляет письмо с подтверждением регистрации на указанный email.
     *
     * Создает MIME сообщение, заполняет его содержимым и отправляет через JavaMailSender.
     *
     * @param to адрес электронной почты получателя
     * @param name имя получателя
     * @param link ссылка для подтверждения регистрации
     * @throws IllegalStateException если произошла ошибка при отправке письма
     */
    void sendConfirmationEmail(String to, String name, String link);
}
