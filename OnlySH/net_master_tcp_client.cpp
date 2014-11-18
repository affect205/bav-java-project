#include "net_master_tcp_client.h"

#include <QVBoxLayout>
#include <QLabel>
#include <QTime>
#include <QByteArray>
#include <QDebug>
#include <QBuffer>

// инициализация статических переменных
QString NetMasterTcpClient::WORKING_DATE = "";
QString NetMasterTcpClient::WORKING_TIME = "";

// порт для подключения
int NetMasterTcpClient::port = 11001;

NetMasterTcpClient::NetMasterTcpClient(const QString& host, const int& port, QWidget *parent) :
    QWidget(parent),
    m_next_block_size(0)
{
    m_ptcp_socket = new QTcpSocket(this);

    m_ptcp_socket->connectToHost(host, port);

    // тип передаваемых данных-умолчанию
    current_type = Text;

    // заполняем верхушку стека
    stack_enumber.push("undef");

    // соединение установлено
    connect(m_ptcp_socket, SIGNAL(connected()), SLOT(slot_connected()));
    // готовность считывать данные
    connect(m_ptcp_socket, SIGNAL(readyRead()), SLOT(slot_ready_read()));
    // ошибка соединения
    connect(m_ptcp_socket, SIGNAL(error(QAbstractSocket::SocketError)),
            this,          SLOT(slot_error(QAbstractSocket::SocketError)));
    // разрав соединения
    connect(m_ptcp_socket, SIGNAL(disconnected()),
            this,          SLOT(slot_disconnected()));

    m_ptxt_info     = new QTextEdit;
    m_ptxt_input    = new QLineEdit;
    m_ptxt_info->setReadOnly(true);

    QPushButton* pcmd = new QPushButton("&Send");
    connect(pcmd, SIGNAL(clicked()), SLOT(slot_send_to_server()));
    connect(m_ptxt_input, SIGNAL(returnPressed()),
            this,         SLOT(slot_send_to_server()));

    // кнопка на получение скриншотов
    pbtn_screen     = new QPushButton("&Screen");

    // связываем кнопку получения снимков с обработчиком
    connect(pbtn_screen, SIGNAL(clicked()), SLOT(slot_send_query_on_screen()));

    QVBoxLayout* pvbxlayout = new QVBoxLayout;
    pvbxlayout->addWidget(new QLabel("<h1>Client</h1>"));
    pvbxlayout->addWidget(m_ptxt_info);
    pvbxlayout->addWidget(m_ptxt_input);
    pvbxlayout->addWidget(pcmd);
    pvbxlayout->addWidget(pbtn_screen);

    setLayout(pvbxlayout);
}

/**
 * @brief Слот: получение данных от сервера
 */
void NetMasterTcpClient::slot_ready_read()
{
    QDataStream in(m_ptcp_socket);
    in.setVersion(QDataStream::Qt_5_1);

    for(;;)
    {
        if ( !m_next_block_size )
        {
            if (m_ptcp_socket->bytesAvailable() < sizeof(quint32)) {
                break;
            }
            in >> m_next_block_size;
        }

        if ( m_ptcp_socket->bytesAvailable() < m_next_block_size ) {
            break;
        }

        m_next_block_size = 0;

        if ( current_type == Text )
        {// прием сообщения - чтение данных из потока
            QString str;
            QTime time;
            in >> time >> str;

            // вывод сообщения
            m_ptxt_info->append(time.toString() + " " + str);

        } else if ( current_type == Screen )
        {// прием скринa - чтение данных из потока
            QImage img;
            QString name;
            in >> name >> img;

            // сохраняем скрин в каталог с данными по сотруднику
            QString path = DATA_SCREEN_PATH + "\\" + stack_enumber.top() + "\\" + name;
            qDebug() << "Client: received - " << path;

            img.save(path, "jpg");

            qDebug() << "Client: screen saved...";

            // сигнал о получении файла
            emit signal_data_received(name);

            // вывод сообщения
            QString msg = QTime::currentTime().toString() + " " + QString("Received screen %1...").arg(path);
            m_ptxt_info->append(msg);
        }
    }
}

/**
 * @brief Слот: получение сообщения об ошибке
 * @param err
 */
void NetMasterTcpClient::slot_error(QAbstractSocket::SocketError err)
{
    QString error = "Error: " + (err == QAbstractSocket::HostNotFoundError ?
                                   "The host was not found!"        :
                                 err == QAbstractSocket::RemoteHostClosedError ?
                                   "The remote host is closed!"     :
                                 err == QAbstractSocket::ConnectionRefusedError ?
                                   "The connection was refused!"    :
                                 QString(m_ptcp_socket->errorString()));

    m_ptxt_info->append(error);
}

/**
 * @brief Слот: отправка данных на сервер
 */
void NetMasterTcpClient::slot_send_to_server()
{
    qDebug("Slot: send to server...");

    // меняем тип передаваемых данных
    current_type = Text;

    QByteArray arr_block;
    QDataStream out(&arr_block, QIODevice::WriteOnly);
    out.setVersion(QDataStream::Qt_5_1);
    out << quint32(0) << QTime::currentTime() << m_ptxt_input->text();

    out.device()->seek(0);
    out << quint32(arr_block.size() - sizeof(quint32));

    m_ptcp_socket->write(arr_block);

    m_ptxt_input->setText("");
}

/**
 * @brief Слот: подтверждение сервера об установлении соединения
 */
void NetMasterTcpClient::slot_connected()
{
    m_ptxt_info->append("Received the connected() signal");
}

/**
 * @brief Слот: запрос на получение скриншотов
 */
void NetMasterTcpClient::slot_send_query_on_screen() {

    qDebug() << "Slot: send query on screen...";

    // меняем тип передаваемых данных
    current_type = Screen;

    QByteArray arr_block;
    QDataStream out(&arr_block, QIODevice::WriteOnly);

    out.setVersion(QDataStream::Qt_5_1);

    // команда серверу на получение скриншотов
    QString str = "screen:" + NetMasterTcpClient::WORKING_TIME;

    out << quint32(0) << QTime::currentTime() << str;

    out.device()->seek(0);
    out << quint32(arr_block.size() - sizeof(quint32));

    m_ptcp_socket->write(arr_block);
    m_ptxt_input->setText("");
}

/**
 * @brief Слот: разрыв соединения
 */
void NetMasterTcpClient::slot_disconnected() {

    qDebug() << QString("Client: disconnected...");
}


/**
 * @brief Подключение клиента к указанному сотруднику
 * @param enumber
 */
void NetMasterTcpClient::connect_to_slave(const QString &enumber) {

    if ( stack_enumber.top() != enumber )
    {// подключение не установлено - подключаемся

        if ( stack_enumber.contains(enumber) )
        {// убираем номер со дна
            stack_enumber.remove(stack_enumber.indexOf(enumber));
        }
        // перемещаем номер на вершину стека
        stack_enumber.push(enumber);

        // получение ip-адреса сотрудника
        QString host = AppMaster::get_db_driver()->get_ip_by_enumber(enumber);
        host = "localhost";

        // подключаемся к компьютеру сотрудника
        qDebug() << "Client: connect to " << host;
        m_ptcp_socket->connectToHost(host, NetMasterTcpClient::port);
    }
}

/**
 * @brief обновление данных по сотруднику (снимки)
 * @param QString date - дата обновления
 * @param QString time - время, с которого пойдет обновление
 */
void NetMasterTcpClient::refresh_working(const QString &enumber,
                                         const QString &date,
                                         const QString &time) {

    // подключаемся к серверу указанного сотрудника
    connect_to_slave(enumber);

    // обновляем параметры запроса
    NetMasterTcpClient::WORKING_DATE = date;
    NetMasterTcpClient::WORKING_TIME = time;

    // посылаем запрос на обновление данных
    slot_send_query_on_screen();
}

