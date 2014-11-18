#ifndef NET_BASE_H
#define NET_BASE_H

#include <QObject>

#include "net_master_tcp_client.h"
#include "net_master_client_downloader.h"

/**
 * @class NetBase
 * @brief Класс, обеспечивающий доступ tcp клиенту,
 * содержащий методы для управления сетевым взаимодействием
 */

class NetMasterTcpClient;
class NetMasterClientDownloader;

class NetBase : public QObject
{
    Q_OBJECT

private:

    // запрет на создание класса
    explicit NetBase(QObject *parent = 0);

public:

    // инициализация клиента
    static void init_client(const QString& host, const int& port);

    // обновление данных по сотруднику (скриншоты)
    static void refresh_working(const QString& enumber, const QString& date, const QString& time);

protected:

    // tcp клиент приложения
    static NetMasterTcpClient* client;

    // порт для подключения
    static int port;

    // потоковый загрузчик
    static NetMasterClientDownloader* downloader;

    // поток на исполнение
    static QThread* thread;

signals:

public slots:

};

#endif // NET_BASE_H
