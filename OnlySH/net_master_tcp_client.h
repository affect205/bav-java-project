#ifndef MASTER_TCP_CLIENT_H
#define MASTER_TCP_CLIENT_H

#include <QWidget>
#include <QTcpSocket>
#include <QTextEdit>
#include <QLineEdit>
#include <QPushButton>

#include "app_master.h"

class NetMasterTcpClient : public QWidget
{
    Q_OBJECT
private:
    QTcpSocket* m_ptcp_socket;
    QTextEdit*  m_ptxt_info;
    QLineEdit*  m_ptxt_input;
    quint32     m_next_block_size;

    QPushButton* pbtn_screen;

    // типы данных для пересылки
    enum DATA_TYPE { Text, Screen };

    // текущий тип данных
    DATA_TYPE current_type;

    // путь до каталога со скринами
    const QString DATA_SCREEN_PATH = "C:\\Qt\\data\\screen";

    // текущие параметры запроса (дата и время создания скрина)
    static QString WORKING_DATE;
    static QString WORKING_TIME;

    // личные номера подключенных сотрудников
    QStack<QString> stack_enumber;

    // порт для подключения
    static int port;

public:

    // конструктор
    explicit NetMasterTcpClient(const QString& host, const int& port, QWidget *parent = 0);

    // подключение к указанному сотруднику
    void connect_to_slave(const QString& enumber);

    // запрос на обновление данных (скриншоты)
    void refresh_working(const QString &enumber, const QString &date, const QString &time);

signals:

    // получение файла
    void signal_data_received(QString);

public slots:

private slots:
    void slot_ready_read();
    void slot_error(QAbstractSocket::SocketError);
    void slot_send_to_server();
    void slot_connected();
    void slot_disconnected();

    // запрос на получение скриншотов
    void slot_send_query_on_screen();


};

#endif // MASTER_TCP_CLIENT_H
