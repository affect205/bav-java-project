/**
 * Класс для загрузки собранных данных
 * в новом потоке
 *
 */

#ifndef NET_MASTER_CLIENT_DOWNLOADER_H
#define NET_MASTER_CLIENT_DOWNLOADER_H

#include <QObject>
#include "net_master_tcp_client.h"

class NetMasterTcpClient;

class NetMasterClientDownloader : public QObject
{
    Q_OBJECT

private:

    // указатель на клиента
    static NetMasterTcpClient* client;

    // данные для обновления
    QMap<QString, QString> refresh_data;

public:

    // конструктор
    explicit NetMasterClientDownloader(QObject *parent = 0);

    // установка клиента
    void set_client(NetMasterTcpClient* client);

    // установка данных запроса на получение раб. активности
    void set_working(const QString &enumber,
                     const QString &date,
                     const QString &time);

signals:

    // окончание действия
    void signal_finished();

public slots:

    // обновление данных по рабочей активности
    void slot_refresh_working();

};

#endif // NET_MASTER_CLIENT_DOWNLOADER_H
