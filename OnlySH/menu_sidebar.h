/**
* @class
* Класс бокового меню
*
*/
#ifndef MENU_SIDEBAR_H
#define MENU_SIDEBAR_H

#include <QListWidget>
#include <QHBoxLayout>
#include <QToolBox>
#include <QSplitter>
#include <QPushButton>
#include <QGroupBox>
#include <QDebug>
#include <QSignalMapper>
#include <QList>

class MenuSidebar : public QWidget
{
    Q_OBJECT

    // кнопки меню
    QVector<QPushButton*> vpbtn_items;

    // сигнализатор
    QSignalMapper* sigmap;

public:
    explicit MenuSidebar(QWidget *parent = 0);

signals:
    // сигнал: нажатие на пункт меню
    void signal_clicked_item(const int& itemid);

public slots:
    // слот: нажатие на пункт меню
    void slot_clicked_item(const int& itemid);

};

#endif // MENU_SIDEBAR_H
