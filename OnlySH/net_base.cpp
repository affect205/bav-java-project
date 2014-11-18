#include "net_base.h"

#include <QDebug>

NetBase::NetBase(QObject *parent) :
    QObject(parent)
{
}

// значение клиента по умолчанию
NetMasterTcpClient* NetBase::client = NULL;

// потоковый загрузчик
NetMasterClientDownloader* NetBase::downloader = new NetMasterClientDownloader();

// поток на исполнение
QThread* NetBase::thread = new QThread();


// порт для подключения
int NetBase::port = 11001;

/**
 * @brief инициализация клиента
 */
void NetBase::init_client(const QString& host, const int& port) {

    NetBase::client = new NetMasterTcpClient(host, port);
}

/**
 * @brief обновление данных по сотруднику (снимки)
 * @param QString date - дата обновления
 * @param QString time - время, с которого пойдет обновление
 */
void NetBase::refresh_working(const QString &enumber, const QString &date, const QString &time) {

    qDebug() << QString("refresh working: date(%2) time(%3)...").arg(date).arg(time);

    // передаем загрузчику данные для скачивания
    downloader->set_client(NetBase::client);
    downloader->set_working(enumber, date, time);

    // связываем загрузчик с потоком
    QObject::connect(thread, SIGNAL(started()), downloader, SLOT(slot_refresh_working()));
    QObject::connect(downloader, SIGNAL(signal_finished()), thread, SLOT(terminate()));

    // запуск потока
    thread->start(QThread::IdlePriority);
}
