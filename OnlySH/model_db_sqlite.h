#ifndef MODEL_DB_SQLITE_H
#define MODEL_DB_SQLITE_H

#include <QtSql>
#include "sys_config.h"
#include "i_model_db_driver.h"

/**
 * Драйвер БД для работы с Sqlite
 * Класс реализует интерфейс IModelDBDriver
 */
class ModelDBSqlite : public IModelDBDriver
{
private:
    // тип драйвера бд
    const IModelDBDriver::DB_DRIVER_TYPE db_drivar_type = IModelDBDriver::SQLLITE;

    // путь до БД
    QString db_path;

public:

    // конструктор
    explicit ModelDBSqlite(const QString &db_path);

    // информация о колонках таблицы
    virtual QSet<QString> get_table_info(const QString& tblname) const;

    // должность подчиненного
    virtual QString get_position_by_id(const int& positionid) const;

    // список подчиненных указанного начальника
    virtual QVector<HashSlave> get_slaves_by_master(const int& masterid) const;

    // список отделов
    virtual QStringList get_departments() const;

    // получение ip адреса комп-ра сотрудника по личному номеру
    virtual QString get_ip_by_enumber(const QString &enumber) const;

    // деструктор
    virtual ~ModelDBSqlite() {}
};

#endif // MODEL_DB_SQLITE_H
