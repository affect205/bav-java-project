#include "app_main_window.h"

AppMainWindow::AppMainWindow(QWidget *parent) :
    QMainWindow(parent)
{
    qDebug() << "Class: AppMainWindow...";

    // боковое меню
    MenuSidebar* menu_sidebar = new MenuSidebar();

    // боковая панель
    QDockWidget* pdockwidget = new QDockWidget();
    pdockwidget->setWidget(menu_sidebar);

    // установка боковой панели
    addDockWidget(Qt::LeftDockWidgetArea, pdockwidget);

    // вывод раздела при нажатии пункта меню
    QObject::connect(menu_sidebar, SIGNAL(signal_clicked_item(const int&)),
            this, SLOT(slot_show_clicked_item(const int&)));



    // установка виджета рабочей области
    list_menu_items.append(new ViewItemMain());
    list_menu_items.append(new ViewItemSlaves());
    list_menu_items.append(new ViewItemHistory());
    list_menu_items.append(new ViewItemConfig());

    // виджет-обертка центральной области окна
    psw_central_wrapper = new QStackedWidget();
    psw_central_wrapper->addWidget(list_menu_items.at(0));
    psw_central_wrapper->addWidget(list_menu_items.at(1));
    psw_central_wrapper->addWidget(list_menu_items.at(2));
    psw_central_wrapper->addWidget(list_menu_items.at(3));

    // добавляем обертку в окно
    setCentralWidget(psw_central_wrapper);
}

void AppMainWindow::slot_show_clicked_item(const int &itemid)
{
    qDebug() << "SLOT: Item selected... itemid: " << itemid;

    // выведем из оболочки выбранный пункт меню
    psw_central_wrapper->setCurrentIndex(itemid);
}

