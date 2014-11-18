#ifndef UTIL_WORKING_H
#define UTIL_WORKING_H

#include <QObject>
#include <QTimer>


#include "util_working_screen_maker.h"
#include "sys_helper.h"


class UtilWorking : public QObject
{
    Q_OBJECT
private:

    int counter = 0;

    // частота создания скрина (сек.)
    int period = 30;

    // таймер
    QTimer* timer;

    // ссылка на приложение
    QApplication &app;

public:

    // конструктор
    explicit UtilWorking(QApplication &app, const int period, QObject *parent = 0);

    // запуск сборщика
    void start();

    // остановка сборщика
    void stop();

signals:

public slots:

    // Слот: создание скрина
    void slot_make_screen();

};

#endif // UTIL_WORKING_H
