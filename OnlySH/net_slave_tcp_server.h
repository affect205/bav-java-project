#ifndef SLAVE_TCP_SERVER_H
#define SLAVE_TCP_SERVER_H

/**
  * @class SlaveTcpServer
  * @author AlexDiaz
  * Класс сервера раба. Собирает информацию на компьютере сотрудника и отправляет
  * клиенту хозяина по tcp соединению
  *
  */

#include <QWidget>
#include <QTcpServer>
#include <QTextEdit>

#include "sys_helper.h"

class NetSlaveTcpServer : public QWidget
{
    Q_OBJECT
private:

    // серверный сокет
    QTcpServer* m_ptcp_server;
    QTextEdit*  m_ptxt;
    quint32     m_nNextBlockSize;

    // путь до скриншотов
    const QString DATA_WORKING_PATH = "C:\\Qt\\data\\slave\\working";


    // путь до собранных данных
    const QString DATA_PATH = "C:\\Qt\\data";

    // тестовая отправка данных клиенту
    void send_to_client(QTcpSocket* psocket, const QString& str);

    // отправка скриншота клиенту
    void send_screen_to_client(QTcpSocket* psocket, const QImage& screen, const QString& name);

public:
    explicit NetSlaveTcpServer(int nport, QWidget *parent = 0);

signals:

public slots:
    virtual void slot_new_connection();
            void slot_read_client();

};

#endif // SLAVE_TCP_SERVER_H
