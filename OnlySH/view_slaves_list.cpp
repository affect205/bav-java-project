#include "view_slaves_list.h"

#include <QScrollArea>
#include <QGroupBox>
#include <QComboBox>

ViewSlavesList::ViewSlavesList(QWidget *parent) :
    QWidget(parent)
{
    // назначим имя объекту
    this->setObjectName("view_slave_list");

    // загрузка данных по сотрудникам
    const int masterid = 2;
    data_slave = AppMaster::get_db_driver()->get_slaves_by_master(masterid);

    // загрузка данных по подразделениям
    QStringList lst_dep(AppMaster::get_db_driver()->get_departments());

    // данные по сотрудниками
    QStringList lst_empl = get_slave_list();


    // корневой элемент и разметка
    QVBoxLayout* pvboxlt_root = new QVBoxLayout();
    pvboxlt_root->setAlignment(Qt::AlignCenter);

    QGroupBox* pgb_root = new QGroupBox();
    pgb_root->setTitle("Cотрудники");

    QGroupBox* pgb_select = new QGroupBox();
    pgb_select->setTitle("Выбор");

    QHBoxLayout* phboxlt_header = new QHBoxLayout();

    // список отделов
    QLabel* lbl_dep     = new QLabel("<b>Отдел:</b>");
    QComboBox* cb_dep   = new QComboBox();
    cb_dep->setFixedWidth(200);

    cb_dep->addItems(lst_dep);
    cb_dep->setEditable(true);

    // список сотрудников
    QLabel* lbl_empl    = new QLabel("<b>Сотрудник:</b>");
    QComboBox* cb_empl  = new QComboBox();
    cb_empl->setFixedWidth(200);

    cb_empl->addItems(lst_empl);
    cb_empl->setEditable(true);

    // список сотрудников
    QScrollArea* scroll_area = new QScrollArea();

    QGroupBox* pgb_wrap = new QGroupBox(scroll_area);
    pgb_wrap->setStyleSheet("QGroupBox { border: none; }");

    QVBoxLayout* pvboxlayout = new QVBoxLayout();
    QVBoxLayout* pvboxlt_slaves = new QVBoxLayout(pgb_wrap);


    foreach ( IModelDBDriver::HashSlave hash, data_slave )
    {// добавляем в список нового сотрудника
        list_slave_items.append(new ViewSlavesListItem(hash, this));
    }

    foreach (ViewSlavesListItem* item, list_slave_items)
    {// добавление сотрудников на разметку
        pvboxlt_slaves->addWidget(item);

        // связываем событие выбора сотрудника из ViewSlavesListItem с обработчиком
        connect(item,   SIGNAL(signal_item_clicked(const int&)),
                this,    SLOT(slot_send_employee_clicked(const int&)));
    }

    pgb_wrap->setLayout(pvboxlt_slaves);

    // добавление сформированного списка в область прокрутки
    scroll_area->setWidget(pgb_wrap);
    scroll_area->setVerticalScrollBarPolicy(Qt::ScrollBarAlwaysOn);

    // заполнение разметки
    phboxlt_header->addWidget(lbl_dep);
    phboxlt_header->addWidget(cb_dep);
    phboxlt_header->addStretch(1);
    phboxlt_header->addWidget(lbl_empl);
    phboxlt_header->addWidget(cb_empl);
    pgb_select->setLayout(phboxlt_header);

    pvboxlayout->addWidget(scroll_area);
    pgb_root->setLayout(pvboxlayout);

    pvboxlt_root->addWidget(pgb_select);
    pvboxlt_root->addWidget(pgb_root);

    this->setLayout(pvboxlt_root);
}

/**
 * Получение списка подчиненных сотрудников
 * @return QStringList
 */
QStringList ViewSlavesList::get_slave_list() {

    QStringList list;
    foreach( IModelDBDriver::HashSlave slave, data_slave )
    {// добавляем сотрудника в список
        list.append(QString("%1 %2 %3").arg(slave["surname"])
                .arg(slave["firstname"]).arg(slave["middlename"]));
    }
    return list;
}

/**
 * Получение личного номера первого сотрудника в списке
 * @return QString
 */
QString ViewSlavesList::get_first_enumber() {

    if ( data_slave.empty() ) {
        return "";
    }
    return data_slave.at(0).value("enumber");
}

/**
 * Слот: переход на страницу сотрудника
 */
void ViewSlavesList::slot_send_employee_clicked(const int& enumber)
{
    // выведем на консоль
    qDebug() << QString("Slot: employee clicked (enumber=%1)...").arg(enumber);

    // посылаем сигнал о событии
    emit signal_employee_clicked(enumber);
}
