#include "sys_helper.h"

#include <QRegExp>
#include <QStringList>


// путь до данных (сотрудник)
const QString SysHelper::SLAVE_DATA_PATH = "C:\\Qt\\data\\slave";

// путь до данных со скринами (сотрудник)
const QString SysHelper::SLAVE_DATA_PATH_WORKING = SysHelper::SLAVE_DATA_PATH + "\\working";

// запрет на создание коеструктора
SysHelper::SysHelper(QObject *parent) :
    QObject(parent)
{
}

/**
 * @brief Провнрка имени файла на соответствие формату
 * @param QString &str
 * @return bool
 */
bool SysHelper::is_working_format(const QString &str) {

    // рег. выражение для даты
    QString re_date = "^[0-9]{4}-(0[1-9]|1[012])-(0[1-9]|1[0-9]|2[0-9]|3[01])";
    // рег. выражение для времени
    QString re_time = "([0-1]\\d|2[0-3])(-[0-5]\\d){2}";
    // рег. выражение для имени файла
    QString re_file = re_date + "_" + re_time + "\.jpg$";

    QRegExp reg_exp( re_file );

    if ( str.contains(reg_exp) ) {
        return true;
    }

    return false;
}

/**
 * @brief Получение времени из имени скрина
 * @param QString &str
 * @return QString
 */
QString SysHelper::exact_working_time(const QString &str) {

    if ( ! SysHelper::is_working_format(str) )
    {// неверный формат - вернем пустую строку
        return "";
    }

    // иначе вренем время
    QString time = str.split("_").at(1).split(".").at(0);

    return time;
}
