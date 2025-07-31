package ru.tw1.euchekavelo.exception.enums;

public enum ExceptionMessage {

    GROUP_NOT_FOUND_EXCEPTION_MESSAGE("Группа с указанным идентификатором не найдена."),
    TOWN_NOT_FOUND_EXCEPTION_MESSAGE("Город с указанным идентификатором не найден."),
    USER_NOT_FOUND_EXCEPTION_MESSAGE("Пользователь с указанным идентификатором не найден."),
    USER_SUBSCRIPTION_EXCEPTION_MESSAGE("Пользователь не может подписаться сам на себя."),
    SOURCE_USER_NOT_FOUND_EXCEPTION_MESSAGE("Исходный пользователь с указанным идентификатором не найден."),
    DESTINATION_USER_NOT_FOUND_EXCEPTION_MESSAGE("Пользователь назначения с указанным идентификатором не найден."),
    USER_SUBSCRIPTION_ALREADY_EXISTS_EXCEPTION_MESSAGE("Подписка на целевого пользователя уже существует."),
    USER_UNSUBSCRIBE_HIMSELF_EXCEPTION_MESSAGE("Пользователь не может отписаться от себя сам."),
    USER_SUBSCRIPTION_NOT_FOUND_EXCEPTION_MESSAGE("Подписка, связанная с конечным пользователем, не выявлена."),
    IMPOSSIBLE_REMOVING_USER_GROUP_EXCEPTION_MESSAGE("Невозможно удалить пользователя из группы."),
    EMPTY_FILE_EXCEPTION_MESSAGE("Обнаружен пустой файл."),
    INVALID_FILE_EXCEPTION_MESSAGE("Неверный формат файла."),
    PHOTO_NOT_FOUND_EXCEPTION_MESSAGE("Фотография пользователя не найдена по указанным критериям."),
    RESOURCE_ACCESS_DENIED_EXCEPTION_MESSAGE("Доступ к защищенному ресурсу запрещен.");

    private final String exceptionText;

    ExceptionMessage(String exceptionText) {
        this.exceptionText = exceptionText;
    }

    public String getExceptionMessage() {
        return exceptionText;
    }
}
