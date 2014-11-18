#ifndef SYS_HELPER_H
#define SYS_HELPER_H

#include <QObject>

class SysHelper : public QObject
{
    Q_OBJECT

private:

    // запрет на создание класса
    explicit SysHelper(QObject *parent = 0);

public:

    // проверка имени файла на соответствие шаблону
    static bool is_working_format(const QString &str);

    // получение времени из имени файла скрина
    static QString exact_working_time(const QString &str);


    // путь до данных (сотрудник)
    static const QString SLAVE_DATA_PATH;

    // путь до данных со скринами (сотрудник)
    static const QString SLAVE_DATA_PATH_WORKING;


signals:

public slots:

};

#endif // SYS_HELPER_H
