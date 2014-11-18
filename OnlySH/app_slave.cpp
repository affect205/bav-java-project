#include "app_slave.h"

/**
 * @brief Конструктор
 * @param QApplication &app
 * @param int port
 * @param QObject parent
 */
AppSlave::AppSlave(QApplication &app, const int port, QObject *parent) :
    app(app),
    port(port),
    QObject(parent)
{
    // инициализация сервера сотрудника
    server = new NetSlaveTcpServer(port);
    //server->show();

    // частота создания скринов
    int period = SysConfig::get_option("screen_period", "value").toInt();

    // создаем и запускаем сборщик скринов
    util_working = new UtilWorking(app, period);
    //util_working->start();
}

/**
 * @brief получение мас-адреса рабочей станции
 */
QString AppSlave::get_mac_address() {

    QStringList mac;
    foreach ( QNetworkInterface interface, QNetworkInterface::allInterfaces() )
    {
        if ( ! interface.hardwareAddress().isEmpty() )
        {// добавим мас-адрес в список
            mac << interface.hardwareAddress();
        }
    }
    return mac.join("|");
}




