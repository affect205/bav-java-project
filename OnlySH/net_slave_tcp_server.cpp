#include "net_slave_tcp_server.h"

#include <QVBoxLayout>
#include <QMessageBox>
#include <QTcpSocket>
#include <QTime>
#include <QLabel>
#include <QDir>
#include <QFile>
#include <QImageWriter>
#include <QBuffer>

/**
 * @brief Конструктор
 * @param nport
 * @param parent
 */
NetSlaveTcpServer::NetSlaveTcpServer(int nport, QWidget *parent) :
    QWidget(parent),
    m_nNextBlockSize(0)
{
    m_ptcp_server = new QTcpServer(this);
    if ( !m_ptcp_server->listen(QHostAddress::Any, nport) )
    {
        QMessageBox::critical(0,
                              "Server Error",
                              "Unable to start server: "
                              + m_ptcp_server->errorString());

        m_ptcp_server->close();
        return;
    }

    connect(m_ptcp_server, SIGNAL(newConnection()),
            this, SLOT(slot_new_connection()));

    m_ptxt = new QTextEdit;
    m_ptxt->setReadOnly(true);

    // Layout setup
    QVBoxLayout* pvbxlayout = new QVBoxLayout;
    pvbxlayout->addWidget(new QLabel("<h1>Server</h1>"));
    pvbxlayout->addWidget(m_ptxt);
    setLayout(pvbxlayout);
}

/**
 * @brief Слот: создание нового соединения
 */
/* virtual */ void NetSlaveTcpServer::slot_new_connection()
{
    // подтверждение связи с клиентом
    QTcpSocket* pclient_socket = m_ptcp_server->nextPendingConnection();

    qDebug() << "new connection...";

    // разрыв соединения
    connect(pclient_socket, SIGNAL(disconnected()),
            pclient_socket, SLOT(deleteLater()));

    // поступление запроса от клиента
    connect(pclient_socket, SIGNAL(readyRead()),
            this, SLOT(slot_read_client()));

    send_to_client(pclient_socket, "ServerResponse: connected!");
}

/**
 * @brief Слот: сбор данных, полученных от слиента
 *
 */
void NetSlaveTcpServer::slot_read_client()
{
    // отправивший запрос сокет
    QTcpSocket* pclient_socket = (QTcpSocket*)sender();

    QDataStream in(pclient_socket);
    in.setVersion(QDataStream::Qt_5_1);

    for (;;)
    {// используем цикл - не все данные могут прийти одновременно

        if ( ! m_nNextBlockSize )
        {
            if ( pclient_socket->bytesAvailable() < sizeof(quint32) )
            {// данные закончились - выходим
                break;
            }
            in >> m_nNextBlockSize;
        }

        if ( pclient_socket->bytesAvailable() < m_nNextBlockSize ) {
            break;
        }

        QTime time;
        QString str;
        in >> time >> str;

        m_nNextBlockSize = 0;

        if ( str.contains("screen:") )
        {// отправка клиенту собранных скринов
            qDebug() << "Server: screen sending...";

            QStringList screens;

            // скрин, с которого начинаем рассылку
            QString last = str.split(":").at(1);

            QDir dir(DATA_WORKING_PATH);

            // сканируем директорию
            foreach ( QString screen, dir.entryList(QDir::Files) )
            {// добавляем скриншот в список
                if ( screen == "." || screen == ".."
                     || ! SysHelper::is_working_format(screen) )
                {// не соответствует формату - отсеиваем
                    continue;
                }

                if ( SysHelper::exact_working_time(screen) > last )
                {// более новый скрин - в очередь на отправку
                    screens.append(screen);
                }
            }

            if ( ! screens.isEmpty() )
            {// есть что передавать - передаем
                foreach ( QString str, screens )
                {// отправляем снимок

                    qDebug() << str;
                    QImage image(DATA_WORKING_PATH + "\\" + str);

                    if ( ! image.isNull() )
                    {// скрин не битый - отправляем
                        send_screen_to_client(pclient_socket, image, str);
                    } else {
                        qDebug() << "Server: no screen...";
                    }

                    QString msg = time.toString() + " Client has got - " + str;
                    m_ptxt->append(msg);
                }
            }

        } else {
            // отправка клиенту текстового сообщения
            qDebug() << "Server: message sending...";

            QString msg = time.toString() + " Client has sent - " + str;
            m_ptxt->append(msg);

            send_to_client(pclient_socket,
                           "Server response: Received \" " + str + "\"");
        }
    }
}

/**
 * @brief Отправка данных клиенту
 * @param QTcpSocket* psocket
 * @param QImage& screen
 */
void NetSlaveTcpServer::send_to_client(QTcpSocket *psocket, const QString &str) {

    QByteArray arr_block;

    qDebug() << "Server: send message to client...";
    QDataStream out(&arr_block, QIODevice::WriteOnly);

    out.setVersion(QDataStream::Qt_5_1);
    out << quint32(0) << QTime::currentTime() << str;

    out.device()->seek(0);
    out << quint32(arr_block.size() - sizeof(quint32));


    psocket->write(arr_block);
}

/**
 * @brief Отправка скриншота клиенту
 * @param QTcpSocket* psocket
 * @param QImage& screen
 * @param QString& nmae
 */
void NetSlaveTcpServer::send_screen_to_client(QTcpSocket *psocket,
                                              const QImage &screen,
                                              const QString &name) {

    QByteArray buffer;

    qDebug() << "Server: send screen to client...";
    QDataStream out(&buffer, QIODevice::WriteOnly);
    out.setVersion(QDataStream::Qt_5_1);
    out << quint32(0) << name << screen;


    out.device()->seek(0);
    out << quint32(buffer.size() - sizeof(quint32));

    psocket->write(buffer);
}














