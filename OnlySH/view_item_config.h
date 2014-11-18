#ifndef VIEWITEMCONFIG_H
#define VIEWITEMCONFIG_H

#include <QWidget>
#include <QLabel>
#include <QVBoxLayout>

#include "sys_config.h"
#include "view_item_config_option.h"

class ViewItemConfig : public QWidget
{
    Q_OBJECT
private:
    // виджеты настроек
    QList<ViewItemConfigOption*> option_widget_list;

public:
    explicit ViewItemConfig(QWidget *parent = 0);

    // отрисовка настройки
    void draw_option();
signals:

public slots:

};

#endif // VIEWITEMCONFIG_H
