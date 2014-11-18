#include "sys_config_struct.h"

SysConfigStruct::SysConfigStruct(QObject *parent) :
    QObject(parent)
{

}

SysConfigStruct::SysConfigStruct(QHash<QString, QString> params, QObject *parent) :
    QObject(parent),
    attrs(params)
{

}

