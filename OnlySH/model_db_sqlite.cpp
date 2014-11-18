#include "model_db_sqlite.h"

ModelDBSqlite::ModelDBSqlite(const QString &db_path) :
    db_path(db_path)
{
    // создание подключения к БД
    //QString dbPath = QCoreApplication::applicationDirPath() + "\\shld_db.sqlite";
    //qDebug(dbPath.toUtf8());

    QSqlDatabase sdb = QSqlDatabase::addDatabase("QSQLITE");

    //sdb.setDatabaseName("C:\\sqlite\\shld_db.sqlite");
    sdb.setDatabaseName(db_path);

    if ( !sdb.open() )
    {// нет соединения с БД - выведем ошибку
        qDebug() << sdb.lastError().text().toUtf8();
    }

    qDebug() << "connection was established...";

    sdb.exec("");
}

/**
 * Получение имен колонок для указанной таблицы БД
 * @param const QString& tblname
 */
QSet<QString> ModelDBSqlite::get_table_info(const QString& tblname) const {

    QSqlQuery query(QString("SELECT * FROM %1 ").arg(tblname));

    QSet<QString> fields;

    QSqlRecord rec = query.record();

    //qDebug() << "\nFields of " << tblname << ":";
    for ( int i=0; i < rec.count(); ++i )
    {
        fields.insert(rec.fieldName(i));
        //qDebug() << rec.fieldName(i);
    }

    return fields;
}

/**
 * Получение списка подчиненных по id начальника
 * @param const int& masterid
 * @return
 */
QVector<ModelDBSqlite::HashSlave> ModelDBSqlite::get_slaves_by_master(const int& masterid) const
{
    QVector<HashSlave> slaves;
    // имена колонок будут ключами
    QSet<QString> fields(get_table_info("persons") +
                         get_table_info("employees"));


    // текст запроса
    QString qstr = QString("SELECT * FROM persons AS p "
                           "INNER JOIN employees AS e ON p.id = e.personid "
                           "INNER JOIN positions AS pos ON e.positionid = pos.id "
                           "WHERE e.id IN ("
                           "    SELECT employeeid FROM slaves"
                           "    WHERE id IN("
                           "        SELECT slaveid FROM asbindings "
                           "        WHERE masterid = %1))"
                           "ORDER BY p.surname").arg(masterid);

    QSqlRecord rec;
    QSqlQuery query;
    HashSlave hash;

    if ( query.exec(qstr) )
    {// запрос прошел - извлекаем данные
        rec = query.record();

        while ( query.next() )
        {// заполняем список
            foreach (QString field, fields)
            {
                if ( field == "id" ) {
                    continue;
                }

                if ( field == "positionid" )
                {
                    int positionid = query.value(rec.indexOf("positionid")).toInt();
                    QString position(get_position_by_id(positionid));
                    hash.insert("position", position);
                    continue;
                }

                // заполняем хеш
                QString value = query.value(rec.indexOf(field)).toString();
                hash.insert(field, value);
            }
            // заполняем список
            slaves.push_back(hash);
        }
    }

    return slaves;
}

/**
 * Получение должности сотрудника
 * @param const int& positionid
 * @return
 */
QString ModelDBSqlite::get_position_by_id(const int &positionid) const {

    QSqlRecord rec;
    QSqlQuery query;
    QString qstr = QString("SELECT * FROM positions "
                           "WHERE id = %1 ").arg(positionid);

    if ( query.exec(qstr) )
    {// запрос прошел - извлекаем данные
        if ( query.first() )
        {// данные из первой строки
            rec = query.record();
            return query.value(rec.indexOf("name")).toString();
        }
    } else {
        // иначе выведем ошибку
        qDebug() << query.lastError().text().toUtf8();
    }

    return "";
}

/**
 * Список отделов
 * @return
 */
QStringList ModelDBSqlite::get_departments() const {

    QStringList list;
    QSqlRecord rec;
    QSqlQuery query;
    // текст запроса
    QString qstr = QString("SELECT name FROM departments "
                           "ORDER BY name");

    if ( query.exec(qstr) )
    {// запрос прошел - извлекаем данные
        while ( query.next() )
        {// добавляем в список имена отделов
            rec = query.record();
            list.append(query.value(rec.indexOf("name")).toString());
        }
    } else {
        // иначе выведем ошибку
        qDebug() << query.lastError().text().toUtf8();
    }

    return list;
}


/**
 * @brief Получение ip адреса комп-ра сотрудника по личному ноеру
 * @param QString& enumber
 * @return QString
 */
QString ModelDBSqlite::get_ip_by_enumber(const QString &enumber) const {

    /*
    // конвертация ip-адреса в целое числo
    QHostAddress hostaddr;
    hostaddr.setAddress("129.124.33.1");
    hostaddr.toIPv4Address();
    */

    QString ip;
    QSqlRecord rec;
    QSqlQuery query;

    // запрос на получение ip адреса компьютера по личному номеру сотрудника
    QString qstr = QString(" SELECT ip FROM netaddresses "
                           " WHERE id = ( "
                           "    SELECT netaddressid FROM slaves "
                           "    WHERE employeeid = ( "
                           "        SELECT id FROM employees "
                           "        WHERE enumber = %1 )) ").arg(enumber);

    if ( query.exec(qstr) )
    {// запрос прошел - извлекам данные
        if ( query.first() )
        {// данные присутствуют - сохраним
            rec = query.record();
            ip = query.value(rec.indexOf("ip")).toString();

        }
    } else
    {// иниче выведем ошибку
        qDebug() << query.lastError().text().toUtf8();

    }

    qDebug() << "Enumber: " << enumber << " ip: " << ip;
    return QString("localhost");
}














