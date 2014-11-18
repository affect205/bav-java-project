#ifndef APP_SLAVE_H
#define APP_SLAVE_H

#include <QObject>
#include <QDebug>
#include <QNetworkInterface>


#include "net_slave_tcp_server.h"
#include "util_working.h"
#include "sys_config.h"

class AppSlave : public QObject
{
    Q_OBJECT

private:

    // порт прослушивания
    int port;

    // сервер для передачи данных
    NetSlaveTcpServer* server;

    // сборщик данных - создание скриншотов
    UtilWorking* util_working;

    // ссылка на приложение
    QApplication &app;

public:

    // конструктор
    explicit AppSlave(QApplication &app, const int port, QObject *parent = 0);

protected:

    // получение мас-адреса рабочей станции
    QString get_mac_address();

signals:

public slots:

};

#endif // APP_SLAVE_H
