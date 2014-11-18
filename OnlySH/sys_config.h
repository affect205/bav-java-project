#ifndef SYS_CONFIG_H
#define SYS_CONFIG_H

#include <QObject>
#include <QFile>
#include <QDir>
#include <QDomDocument>
#include <QDebug>
#include <QHash>

#include "sys_config_struct.h"

class ViewConfig;

class SysConfig : public QObject
{
    Q_OBJECT

private:
    // запрет на создание экземпляра
    explicit SysConfig(QObject *parent = 0) {}

public:

    // структура для хранения отдельной настройки
    typedef QHash<QString, QHash<QString, QString> > ConfigStruct;

    // инициализация настроек
    static void init_config();

    // получение настройки по ключу
    static QString get_option(const QString& key, const QString& param);

    // получение словаря настроек
    static const ConfigStruct& get_options();

    // вывод настроек
    static void print_options();

    // провекра загрузки настроек
    static bool was_loaded();

protected:
    // разбор конфигурационного xml файла
    static void parse_node(const QDomNode& dom_node);

    // флаг загружки настроек
    static bool is_loaded;

    // словарь настроек
    static ConfigStruct options;

signals:

public slots:

};

#endif // SYS_CONFIG_H
