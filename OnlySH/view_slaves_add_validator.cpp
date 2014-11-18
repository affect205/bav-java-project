#include "view_slaves_add_validator.h"

ViewSlavesAddValidator::ViewSlavesAddValidator(QObject *parent,
                                                   ValidatorType vt) :
    QValidator(parent),
    vldtype(vt)
{

}

QValidator::State ViewSlavesAddValidator::validate(QString& str, int& pos) const
{
    QRegExp rxp;
    switch(vldtype)
    {// определяем шаблон строки
        case IP:
            // регулярное выражение для проверки ipv4
            //rxp.setPatternSyntax("^(?:[0-9]{1,3}\.){1,3}[0-9]{1,3}$");
            if (str.isEmpty())
            {// пропускаем пустую строку
                return Acceptable;
            }

            rxp.setPattern("^[0-9]{1,3}$");
            if (rxp.exactMatch(str))
            {// ввод адреса до первой точки
                return Acceptable;
            }

            rxp.setPattern("^([0-9]{1,3}\\.){1,3}([0-9]{1,3})?$");
            if (rxp.exactMatch(str))
            {// ввод адреса после первой точки
                return Acceptable;
            }

            break;

        case PORT:
            rxp.setPattern("^[0-9]{,5}$");
            if (rxp.exactMatch(str))
            {// ввод порта
                return Acceptable;
            }
            break;

        case STRNAME:
            // регулярное выражение для простой строки
            rxp.setPattern("^[A-zА-я]{,32}$");
            break;
    }

    if ( rxp.exactMatch(str) )
    {// введенная строка приемлема
        qDebug() << QString("Right validation! string: %1 pos: %2").arg(str).arg(pos);
        return Acceptable;
    }

    qDebug() << QString("Failed validation! string: %1 pos: %2").arg(str).arg(pos);
    return Invalid;
}
