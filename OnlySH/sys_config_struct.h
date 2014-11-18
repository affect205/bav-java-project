#ifndef SYS_CONFIG_STRUCT_H
#define SYS_CONFIG_STRUCT_H

#include <QObject>

class SysConfigStruct : public QObject
{
    Q_OBJECT
public:
    // конструктор
    explicit SysConfigStruct(QObject *parent = 0);

    explicit SysConfigStruct(QHash<QString, QString> params,
                             QObject *parent = 0);

    QHash<QString, QString> attrs;
signals:

public slots:

};

#endif // SYS_CONFIG_STRUCT_H
