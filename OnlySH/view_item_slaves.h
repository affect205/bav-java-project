/**
 * Отображение пункта меню "Сотрудники"
 *
 */
#ifndef VIIEW_ITEM_SLAVES_H
#define VIIEW_ITEM_SLAVES_H

// Qt includes
#include <QWidget>
#include <QDebug>
#include <QList>
#include <QStackedWidget>

// Project includes
#include "app_master.h"
#include "view_slaves_add.h"
#include "view_slaves_one.h"
#include "view_slaves_list.h"


class ViewItemSlaves : public QWidget
{
    Q_OBJECT

    // список с содержиммым вкладок
    QList<QWidget*> list_tab_content;

    // оболочка для содержимого вкладки
    QStackedWidget* psw_content_wrapper;

    // виджет вкладок
    QTabBar* ptb_tab;

    // индексы вкладок
    const int TAB_INDEX_LIST    = 0;
    const int TAB_INDEX_ADD     = 1;
    const int TAB_INDEX_VIEW    = 2;

public:
    explicit ViewItemSlaves(QWidget *parent = 0);

signals:

public slots:
    // слот: смена текущей вкладки
    void slot_current_changed(const int& tabid);
    // слот: переход на страницу сотрудника
    void slot_show_employee(const int& enumber);

};

#endif // VIIEW_ITEM_SLAVES_H
