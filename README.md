#Инструкция по работе с приложением.

Приложение предоставляет доступ к облачному хранилищу.
Перед курсором выводится полный текущий локальный путь и путь на сервере.

##Команды:

UPLOAD <имя файла из текущей локальной директории> - загрузить указанный файл в облако.

DOWNLOAD <имя файла из текущей директории в облаке> - скачать указанный файл. Файл будет
скачан в текущую локальную директорию.

LS - показать список файлов и директорий в текущей директории.

CD <имя директории> - перейти в директорию с указанным именем.

MKDIR <имя директории> - создать директорию с указанным именем.

RM <имя файла или директории> - удалить файл или директорию (если директория не пустая,
она не будет удалена).

MV <имя файла> <имя директории> - переместить файл в указанную директорию. Директория указывается от текущей.

RENAME <имя файла> <новое имя файла> - переименовать файл.

FREESPACE - показать объем свободного места в облаке.

LLS - показать список файлов и директорий в текущей локальной директории.

LCD <имя директории> - перейти в локальную директорию с указанным именем.

LRM <имя файла или директории> - удалить локально файл или директорию 
(если директория не пустая, она не будет удалена).

LMKDIR <имя директории> - создать локальную директорию с указанным именем.

LMV <имя файла> <имя директории> - переместить файл в указанную директорию. Директория указывается от текущей.

LRENAME <имя файла> <новое имя файла> - переименовать локальный файл.

EXIT - выход из приложения.

HELP - показать команды.

## Дополнительные сведения
- в облаке у каждого пользователя своя директория. Все они лежат в папке dirstorage.