## Getting Started (RU)
- Сначала выполните команду `mvn clean install` для компиляции кода в jar файл
- Далее выполните команду `java -jar .\target\Monitoring-Service-0.0.1-SNAPSHOT.jar` для запуска jar файла
  
### Stack
- Java 17
- JUnit 5

### API

| Команда     | Ключ                                                           | Уровень доступа | Пример                                     | Описание                              |
|-------------|----------------------------------------------------------------|-----------------|--------------------------------------------|---------------------------------------
| `/register`   | `-u` username `-p` password                                    | ***UNAUTHORIZED***    | /register -u trapper -p trapper            | Регистрирует новую учетную запись     |
| `/login`      | `-u` username `-p` password                                    | ***UNAUTHORIZED***    | /login -u trapper -p trapper               | Входит в существующую учетную запись  |
| `/exit`       | нет                                                            | ***UNAUTHORIZED***    | /exit                                      | Закрывает программу                   |
| `/submit`     | `-s` scaner (HOT or COLD) `-i` indication `-m` month `-y` year | ***USER***            | /submit -s hot -i 200.5 -m January -y 2024 | Отправляет показания                  |
| `/get`        | нет                                                            | ***USER***            | /get                                       | Получает показания за текущий месяц   |
| `/getMonthly` | `-m` month `-y` year                                           | ***USER***            | /getMonthly -m January -y 2024             | Получает показания за выбранный месяц |
| `/history`    | нет                                                            | ***USER***            | /history                                   | История отправки показаний            |
| `/rights`     | `-u` username `-a` action (upgrade or downgrade)               | ***ADMIN***           | /rights -u trapper -a upgrade              | Контроль прав                         |
| `/audit`      | нет                                                            | ***ADMIN***           | /audit                                           | Аудит                                 |
| `/logout`     | нет                                                            | ***USER***            | /logout                                           | Выход из аккаунта                     |

