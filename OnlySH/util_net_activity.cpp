#include "util_net_activity.h"

UtilNetActivity::UtilNetActivity(QObject *parent) :
    QObject(parent)
{
    /*
    // сетевая активность
    QProcess netstat;
    netstat.setProcessChannelMode(QProcess::MergedChannels);
    netstat.start("netstat -b");
    //netstat.waitForFinished(-1);

    //QByteArray stdout = netstat.readAllStandardOutput();
    //QString stderr = netstat.readAllStandardError();
    QByteArray data;


    while (netstat.waitForReadyRead()) {
        data.append(netstat.readAll());
    }

    qDebug() << "Output: " << data.data() << "\n";
    //qDebug() << "Error: " << stderr << "\n";
    */
}
