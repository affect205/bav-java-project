#include "view_item_config.h"

#include <QGroupBox>
#include <QDebug>
#include <QScrollArea>
#include <stdlib.h>

ViewItemConfig::ViewItemConfig(QWidget *parent) :
    QWidget(parent)
{
    // корневой элемент и разметка
    QScrollArea* pscroll_area = new QScrollArea();
    pscroll_area->setVerticalScrollBarPolicy(Qt::ScrollBarAlwaysOn);

    QGroupBox* pgb_root = new QGroupBox();
    pgb_root->setTitle("Настройки");
    pgb_root->setStyleSheet("font: 14px;"
                            "color: black;");

    QGroupBox* pgb_scrollwrap = new QGroupBox(pscroll_area);
    pgb_scrollwrap->setStyleSheet("QGroupBox { border: none; }");

    QVBoxLayout* pvboxlt_root = new QVBoxLayout();
    pvboxlt_root->setSpacing(40);

    QVBoxLayout* pvboxlt_wrap = new QVBoxLayout();
    pvboxlt_root->setSpacing(20);

    // разметка для настроек
    QVBoxLayout* pvboxlt_options = new QVBoxLayout();
    pvboxlt_options->setSpacing(40);

    // получение настроек приложения
    SysConfig::ConfigStruct options = SysConfig::get_options();

    SysConfig::ConfigStruct::Iterator iter;
    for ( iter = options.begin(); iter != options.end(); ++iter )
    {// добавление виджета в список настроек
        option_widget_list.append(new ViewItemConfigOption(iter.key()));
    }

    foreach ( ViewItemConfigOption* opt_widget, option_widget_list )
    {// добавление элементов в разметку
        pvboxlt_options->addWidget(opt_widget);

    }

    // расположение элементов по разметке
    pgb_scrollwrap->setLayout(pvboxlt_options);
    pscroll_area->setWidget(pgb_scrollwrap);
    pvboxlt_wrap->addWidget(pscroll_area);

    pgb_root->setLayout(pvboxlt_wrap);
    pvboxlt_root->addWidget(pgb_root);

    this->setLayout(pvboxlt_root);
}
