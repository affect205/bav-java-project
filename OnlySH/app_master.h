#ifndef APP_MASTER_H
#define APP_MASTER_H

#include <QObject>

#include "i_model_db_driver.h"
#include "model_db_sqlite.h"
#include "net_base.h"
#include "app_main_window.h"

class AppMainWindow;

class AppMaster : public QObject
{
    Q_OBJECT
private:

    // главное окно приложения
    AppMainWindow* pmainwindow;

    // драйвер БД
    static IModelDBDriver* DB_DRIVER;

public:

    // конструктор
    explicit AppMaster(QObject *parent = 0);

    // получение драйвера БД
    static IModelDBDriver* get_db_driver() { return AppMaster::DB_DRIVER; }

    // установка драйвера БД
    static void set_db_driver(IModelDBDriver* db_driver);

signals:

public slots:

};

#endif // APP_MASTER_H
