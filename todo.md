1. Подготовка базы данных
- Создание структуры БД с помощью Liquibase
- Написание DDL-скриптов в формате XML или YAML для создания таблиц.
- Организация таблиц в необходимые схемы, исключая схему public для таблиц сущностей.
- Использование sequence для генерации идентификаторов.
2. Предзаполнение данных
- Создание отдельного скрипта миграции для предзаполнения таблиц начальными данными.
3. Настройка служебных таблиц
- Размещение служебных таблиц Liquibase в отдельной схеме. 
4. Интеграция с PostgreSQL
-  Работа с PostgreSQL через JDBC
- Обновление репозиториев для работы с БД PostgreSQL, включая запись всех сущностей.
5. Настройка dataSource
- Конфигурирование подключения к БД в приложении через конфиг-файлы.