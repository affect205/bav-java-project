#ifndef APP_MAIN_WINDOW_H
#define APP_MAIN_WINDOW_H

// Qt includes
#include <QMainWindow>
#include <QStackedWidget>
#include <QDebug>
#include <QDockWidget>
#include <QVBoxLayout>

// Project includes
#include "menu_sidebar.h"
#include "view_item_main.h"
#include "view_item_slaves.h"
#include "view_item_history.h"
#include "view_item_config.h"

// External includes
class AppMainWindow : public QMainWindow
{
    Q_OBJECT

    // содержимое пунктов меню
    QList<QWidget*> list_menu_items;

    // виджет-обертка центральной области
    QStackedWidget* psw_central_wrapper;

public:

    // конструктор
    explicit AppMainWindow(QWidget *parent = 0);

signals:

public slots:

    // слот: отображение содержимого пункта меню
    void slot_show_clicked_item(const int& itemid);
};

#endif // APP_MAIN_WINDOW_H
