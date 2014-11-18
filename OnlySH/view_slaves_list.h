#ifndef VIEW_SLAVES_LIST_H
#define VIEW_SLAVES_LIST_H

// Qt includes
#include <QWidget>
#include <QVBoxLayout>
#include <QLabel>

// Project includes
#include "app_master.h"
#include "i_model_db_driver.h"
#include "view_slaves_list_item.h"

class ViewSlavesList : public QWidget
{
    Q_OBJECT

private:
    // элементы списка (виджеты с данными о сотрудниках)
    QList<ViewSlavesListItem*> list_slave_items;

    // данные по сотрудникам
    QVector<IModelDBDriver::HashSlave> data_slave;

    // данные по отделам

public:
    // конструктор
    explicit ViewSlavesList(QWidget *parent = 0);

    // получение личного номера первого сотрудника в списке
    QString get_first_enumber();

protected:
    // получение списка текущих сотрудников
    QStringList get_slave_list();

signals:
    // сигнал: выбор сотруника из списка
    void signal_employee_clicked(const int& enumber);

public slots:
    // слот: обработка выбор сотруника из списка
    void slot_send_employee_clicked(const int&);

};

#endif // VIEW_SLAVES_LIST_H
