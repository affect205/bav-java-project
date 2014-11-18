#include "util_working.h"

/**
 * @brief Конструктор
 * @param int period
 */
UtilWorking::UtilWorking(QApplication &app, const int period, QObject *parent) :
    app(app),
    period(period),
    QObject(parent)
{
    timer = new QTimer(parent);
    timer->setInterval(period*1000);
    QObject::connect(timer,  SIGNAL(timeout()), this, SLOT(slot_make_screen()));
}

/**
 * @brief Скриншот экрана рабочей области
 */
void UtilWorking::slot_make_screen()
{
    // путь скринов
    QString path = SysHelper::SLAVE_DATA_PATH_WORKING;

    // делаем скрин экрана
    UtilWorkingScreenMaker::make(this->app, path);
    counter++;
    if ( counter > 10 )
        timer->stop();
}

/**
 * @brief Запуск сборщика
 */
void UtilWorking::start() {

    timer->start();
}

/**
 * @brief @brief Запуск сборщика
 */
void UtilWorking::stop() {

    timer->stop();
}
