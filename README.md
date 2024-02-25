## Getting Started (RU)
- Выполните `docker-compose build` и `docker-compose up` для развертывания бд
- Сначала выполните команду `mvn clean install` для компиляции кода в jar файл
- Далее выполните команду `java -jar .\target\Monitoring-Service-2.1.0.jar` для запуска jar файла

### PR
- [Dev/task1 -> main #1 (CLI)](https://github.com/trapper-umin/Monitoring-Service/pull/2) 
- [Dev/task2 -> main #2 (JDBC, LIQUIBASE)](https://github.com/trapper-umin/Monitoring-Service/pull/3)
- [Dev/task3 -> main #3 (Servlet API, AOP)](https://github.com/trapper-umin/Monitoring-Service/pull/4)
- [Dev/task4 -> main #4 (Spring)](https://github.com/trapper-umin/Monitoring-Service/pull/5)

### Stack
- Java 17
- JUnit 5

### REST API

| Method | API           | JSON REQUEST                                                                    | Headers                                | JSON RESPONSE                                                                                                             | Уровень доступа |  Описание                              |
|--------|---------------|---------------------------------------------------------------------------------|----------------------------------------|---------------------------------------------------------------------------------------------------------------------------|---------|---------------------------------------
| POST   | `/api/v1/users/register`   | `{"username":"root","password":"root"}`                                         | нет                                    | `{"token": "JFY-vcZQ9zg"}`                                                                                                                                |  ***UNAUTHORIZED***              |  Регистрирует новую учетную запись     |
| POST   | `/api/v1/users/authenticate`      | `{"username":"root","password":"root"}`                                         | нет                                    | `{"token": "JFY-vcZQ9zg"}`                                                                                                                                | ***UNAUTHORIZED*** |  Входит в существующую учетную запись  |
| POST   | `/api/v1/readings/submit`     | `{"sensor": "HOT","indication": 2.55,"month": "January","year": "2020"}`       | key: `Authorization` Value: `Bearer token` | нет                                                                                                                                                       | ***USER***            |  Отправляет показания                  |
| GET    | `/api/v1/readings/current`        | нет                                                               |         key: `Authorization` Value: `Bearer token`                          | `{"status": 200,"operation": "get current readings","time": "2024-02-11 23:13:42","user": "root","sensors": []}`                                          | ***USER***            |  Получает показания за текущий месяц   |
| GET    | `/api/v1/readings/monthly` | нет                                                                     |        key: `Authorization` Value: `Bearer token`                           | `{"status": 200,"operation": "get readings for January 2020","time": "2024-02-11 23:14:37","user": "root","sensors": []}`                                 | ***USER***            |  Получает показания за выбранный месяц |
| GET    | `/api/v1/readings/history`    | нет                                                                   |      key: `Authorization` Value: `Bearer token`                             | `{"status": 200,"message": "histories of submitting for 111","time": "2024-02-11 23:15:21","body": []}`                                                   | ***USER***            |  История отправки показаний            |
| POST   | `/api/v1/admins/rights`     | `{"username": "root","action": "upgrade"}`                              |      key: `Authorization` Value: `Bearer token`                             | `{"status": 200,"message": "all users","time": "2024-02-14 23:17:04","body": [{"username": "qqqq","role": "USER"}]}`                                      | ***ADMIN***           |  Контроль прав                         |
| GET    | `/api/v1/admins/audit`      | нет                                                                    |      key: `Authorization` Value: `Bearer token`                             | `{"status": 200,"message": "get system audit","time": "2024-02-14 23:18:32","body": [{"log": "Method authentication in ImplAuthController was called"}]}` | ***ADMIN***           |  Аудит                                 |

### CLI API

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