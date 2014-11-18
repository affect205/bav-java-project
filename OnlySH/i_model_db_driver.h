#ifndef I_MODEL_DB_DRIVER_H
#define I_MODEL_DB_DRIVER_H

#include <QObject>

class IModelDBDriver : public QObject
{
    Q_OBJECT
public:
    // особый тип данных
    typedef QHash<QString, QString> HashSlave;

    // поддерживаемые драйвера
    enum DB_DRIVER_TYPE { MYSQL, SQLLITE, UNKNOWN };

    // деструктор
    virtual ~IModelDBDriver() {}

    // информация о колонках таблицы
    virtual QSet<QString> get_table_info(const QString& tblname) const = 0;

    // должность подчиненного
    virtual QString get_position_by_id(const int& positionid) const = 0;

    // список подчиненных указанного начальника
    virtual QVector<HashSlave> get_slaves_by_master(const int& masterid) const = 0;

    // список отделов
    virtual QStringList get_departments() const = 0;

    // получение ip адреса комп-ра сотрудника по личному номеру
    virtual QString get_ip_by_enumber(const QString &enumber) const = 0;

signals:

public slots:

};

#endif // I_MODEL_DB_DRIVER_H
