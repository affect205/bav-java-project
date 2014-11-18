#include <QtWidgets/QApplication>

#include <QtNetwork/QHostAddress>
#include <QtNetwork/QIPv6Address>
#include <QValidator>
#include <QVBoxLayout>
#include <QDialog>
#include <QProcess>
#include <QByteArray>
#include <QTimer>
#include <QTextCodec>

#include "app_master.h"
#include "app_slave.h"
#include "sys_auth.h"
#include "sys_config.h"


//#include <windows.h>
//#include <conio.h>
//#include <tlhelp32.h>

int main (int argc, char *argv[])
{
    QTextCodec* codec = QTextCodec::codecForName("UTF-8");
    QTextCodec::setCodecForLocale(codec);

    QApplication app(argc, argv);

    qDebug("start programm...");

    // инициализация настроек приложения
    SysConfig::init_config();

    // создание серверов сотрудников
    int port = 11001;
    AppSlave* slave = new AppSlave(app, port);

    // создание клиента руководителя
    AppMaster* master = new AppMaster();

    /*
    // модуль авторизации
    SysAuth* pauth = new SysAuth();
    pauth->show();

    if ( pauth->exec() == QDialog::Accepted &&
         pauth->isAccess() )
    {// авторизация пройден - доступ разрешен
        qDebug() << "Accepted dialog";
        AppMaster* master = new AppMaster();
    }
    */

    qDebug("close programm...");
    return app.exec();
}
