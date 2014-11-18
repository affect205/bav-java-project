#ifndef VIEW_ITEM_CONFIG_OPTION_H
#define VIEW_ITEM_CONFIG_OPTION_H

#include <QWidget>

#include "sys_config.h"

class ViewItemConfigOption : public QWidget
{
    Q_OBJECT
private:
    // имя настройки
    QString option;
public:
    explicit ViewItemConfigOption(const QString& optkey, QWidget *parent = 0);

signals:

public slots:

};

#endif // VIEW_ITEM_CONFIG_OPTION_H
