IVASCAPE - старый проект, который может чему-то ещё научить!
(может быть когда-нибудь здесь будет вся ретроспектива но мне лень)


Не забываем, что для запуска программы на версии JDK 10+ (на данный момент это JDK 13)
нужно вводить в VM Options:
--module-path $PATH_TO_FX$ --add-modules javafx.controls,javafx.fxml,javafx.base --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED
где $PATH_TO_FX$ это путь по типу D:\dev\jdk_13.0.1\lib

Чтобы JAVAFX не ругалась и программа нормально отработала - не забываем)