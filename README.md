# java-kanban

Repository for homework project.
Спринт 4:
Реализовал все необходимые пункты логики.
Удаление эпиков, у которых есть сабтаски, никак не предотвращается. Вместе с ними удаляются связанные сабтаски
Не предполагается, что сабтаск может быть добавлен без добавленного заранее эпика, связанного с ним.
Новому эпику всегда проставляется статус NEW
Учтен вариант, что у сабтаска поменяют epicId и сделают update

Спринт 5:
Реализована логика хранения истории просмотра задач
Добавлено покрытие тестами в классе InMemoryTaskManagerTest

Спринт 5 v2:
Убран закомментированный код в классе Main
Исправлен импорт классов, теперь не через *
Исправлены замечания по интерфейсам
Исправлена логика работы метода clearSubTasks()
Дополнен тест EpicShouldChangeStatus(), проверяющий исправленную логику

Спринт 6:
Реализовано ТЗ + Дополнительное задание с пользовательским сценарием + рефакторинг

Спринт 6 v2:
Создан метод clone() в классах задач, чтобы не терялась индивидуальность метода toString().
Исправлены замечания

Спринт 7:
Реализовано ТЗ + тесты + рефакторинг

Спринт 8:
Реализовано ТЗ + тесты + рефакторинг


