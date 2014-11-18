#ifndef VIEW_SLAVES_ONE_H
#define VIEW_SLAVES_ONE_H

#include <QWidget>
#include <QVBoxLayout>
#include <QLabel>
#include <QGroupBox>
#include <QTabBar>
#include <QStackedWidget>
#include <QDebug>

#include "view_slaves_one_working.h"
#include "view_slaves_one_desktop.h"
#include "sys_helper.h"

class ViewSlavesOne : public QWidget
{
    Q_OBJECT

private:
    // стек виджетов
    QStackedWidget* psw_tab_content;

    // список виджетов раздела
    QList<QWidget*> list_tab_widgets;

    // личный номер сотрудника
    QString enumber;

    // путь до директории с данными по сотрудникам
    static const QString DATA_PATH;

public:
    // конструктор
    explicit ViewSlavesOne(const QString& enumber = "", QWidget *parent = 0);

    // смена текущего сотрудника
    void set_current_employee(const QString& enumber);

    // время получения последнего скрина по сотруднику
    static QString get_last_working_time(const QString &enumber);

protected:
    // загрузка данных для текущего струдника
    void load_data();

signals:

public slots:
    // смена текущей вкладки
    void slot_current_changed(const int& itemid);

};

#endif // VIEW_SLAVES_ONE_H
