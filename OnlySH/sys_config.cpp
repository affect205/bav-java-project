#include "sys_config.h"

#include <QTextCodec>

// инициализация флага загрузки
bool SysConfig::is_loaded = false;

// словарь настроек
SysConfig::ConfigStruct SysConfig::options;

/**
 * Парсинг конфигурационного xml файла
 */
void SysConfig::init_config() {

    if ( SysConfig::is_loaded )
    {// настройки уже загружены
        qDebug() << "Config file already loaded...";
        return;
    }

    // путь до файла
    QString path = QDir::currentPath() + "/config.xml";

    QDomDocument dom_doc;
    QFile file(path);

    if ( file.open(QIODevice::ReadOnly | QIODevice::Text) )
    {
//        QTextStream in(&file);
//        in.setCodec("UTF-8");
//        QByteArray barr(in.readAll().toStdString().c_str());

        if ( dom_doc.setContent(&file) )
        {
            QDomElement dom_elem = dom_doc.documentElement();
            // парсим документ
            parse_node(dom_elem);
        } else {
            // выведем ошибку
            qDebug() << "No xml content";
        }

    } else {
        // выведем ошибку
        qDebug() << file.errorString();
    }

    // настройки загружены
    SysConfig::is_loaded = true;
}

/**
 * Парсинг dom узла
 * @param const QDomNode& dom_node
 */
void SysConfig::parse_node(const QDomNode& node) {

    QDomNode dom_node = node.firstChild();
    QHash<QString, QString> params;

    while ( ! dom_node.isNull() )
    {// не дошли до конца док-та - парсим
        if ( dom_node.isElement() )
        {
            QDomElement dom_elem = dom_node.toElement();
            if ( ! dom_elem.isNull() )
            {
                if ( dom_elem.tagName() == "option" )
                {// нашли тег настройки - занесем в словарь
                    params.insert("value",  dom_elem.text());
                    params.insert("name",   dom_elem.attribute("name"));
                    params.insert("type",   dom_elem.attribute("type"));
                    params.insert("desc",   dom_elem.attribute("desc"));

                    // добавляем в словарь настроек
                    SysConfig::options.insert(dom_elem.attribute("name"), params);
                }
            }
        }
        // переход на след. уровень
        parse_node(dom_node);
        dom_node = dom_node.nextSibling();
    }
}

/**
 * Получение опции по ключу
 * @param QString &key      - имя настройки
 * @param QString &param    - имя параметра настройки
 * @return QString
 */
QString SysConfig::get_option(const QString& key, const QString& param) {

    if ( ! SysConfig::options.contains(key) )
    {// опции не существует - вернем по умолчанию
        return "";
    }
    // иначе возвращаем треьуемый параметр
    return SysConfig::options.value(key).value(param);
}

/**
 * Получение словаря настроек
 * @return
 */
const SysConfig::ConfigStruct& SysConfig::get_options() {

    return SysConfig::options;
}

/**
 * Вывод настроек приложения
 */
void SysConfig::print_options() {

    SysConfig::ConfigStruct::Iterator iter;
    for ( iter = SysConfig::options.begin(); iter != SysConfig::options.end(); ++iter )
    {
        // вывод ключа и значения
        qDebug() << "Key: " << iter.key();
    }
}

bool SysConfig::was_loaded() {

    return SysConfig::is_loaded;
}


