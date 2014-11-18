#include "app_master.h"

// начальная инициализация драйвера БД
IModelDBDriver* AppMaster::DB_DRIVER = NULL;

AppMaster::AppMaster(QObject *parent) :
    QObject(parent)
{
    // инициализация драйвера БД
    QString db_path = "C:\\sqlite\\shld_db.sqlite";
    AppMaster::set_db_driver(new ModelDBSqlite(db_path));

    // сетевые настройки
    int port = 11001;
    QString host = "localhost";

    // инициализация клиента
    NetBase::init_client(host, port);

    // главное окно приложения
    pmainwindow = new AppMainWindow();

    // вывод окна приложения
    pmainwindow->show();
}

/**
 * @brief установка драйвера БД
 * @param IModelDBDriver *db_driver
 */
void AppMaster::set_db_driver(IModelDBDriver *db_driver) {

    AppMaster::DB_DRIVER = db_driver;
}
