#include "net_master_client_downloader.h"

#include <QDebug>

NetMasterClientDownloader::NetMasterClientDownloader(QObject *parent) :
    QObject(parent)
{
}

// инициализация клиента
NetMasterTcpClient* NetMasterClientDownloader::client = NULL;

/**
 * @brief Слот: обновление данных по рабочей активности
 */
void NetMasterClientDownloader::slot_refresh_working() {


    qDebug() << "Slot: Thread is exec...";

    if ( NetMasterClientDownloader::client != NULL )
    {// запускаем функцию обновления
        NetMasterClientDownloader::client->refresh_working(
                    refresh_data.value("enumber"),
                    refresh_data.value("date"),
                    refresh_data.value("time"));
    }

    emit signal_finished();
}

/**
 * @brief установка клиента
 * @param NetMasterTcpClient client
 */
void NetMasterClientDownloader::set_client(NetMasterTcpClient *client) {

     NetMasterClientDownloader::client = client;
}

/**
 * @brief установка данных для запроса на обновления раб. активности
 * @param QString &enumber
 * @param QString &date
 * @param QString &time
 */

void NetMasterClientDownloader::set_working(const QString &enumber,
                 const QString &date,
                 const QString &time) {

    refresh_data.insert("enumber", enumber);
    refresh_data.insert("date", date);
    refresh_data.insert("time", time);
}


/**
 * @brief Сигнал: окончание действия
 */
/*
void NetMasterClientDownloader::signal_finished() {

    qDebug() << "Signal: Transmission is done...";
}
*/
