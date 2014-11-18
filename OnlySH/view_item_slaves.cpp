#include "view_item_slaves.h"

#include <QTabBar>
#include <QVBoxLayout>
#include <QLabel>
#include <QListWidget>
#include <QCheckBox>
#include <QDate>
#include <QTime>

ViewItemSlaves::ViewItemSlaves(QWidget *parent) :
    QWidget(parent)
{
    QLabel* plbl_title = new QLabel("<h3>Сотрудники:</h3><hr>");
    plbl_title->setStyleSheet("text-decoration: underline;");
    plbl_title->setAlignment(Qt::AlignTop);
    plbl_title->setFixedHeight(30);

    // виджет вкладок
    ptb_tab = new QTabBar();
    ptb_tab->setShape(QTabBar::TriangularNorth);
    ptb_tab->addTab(QString("Список"));
    ptb_tab->addTab(QString("Добавить"));
    ptb_tab->addTab(QString("Просмотр"));

    // заблокируем переход на вкладку "просмотр"
    ptb_tab->setTabEnabled(2, false);

    QVBoxLayout* pvboxlayout = new QVBoxLayout();
    pvboxlayout->setAlignment(Qt::AlignTop);
    pvboxlayout->addWidget(plbl_title);
    pvboxlayout->addWidget(ptb_tab);

    // загружаем в список содержимое вкладок
    list_tab_content.append(new ViewSlavesList());
    list_tab_content.append(new ViewSlavesAdd());
    list_tab_content.append(new ViewSlavesOne());

    // оболочка содержимого вкладки
    psw_content_wrapper = new QStackedWidget();

    // добавим содержимое вкладок в оболочку
    psw_content_wrapper->addWidget(list_tab_content.at(0));
    psw_content_wrapper->addWidget(list_tab_content.at(1));
    psw_content_wrapper->addWidget(list_tab_content.at(2));

    // связываем смену вкладки с обработчиком
    connect(ptb_tab, SIGNAL(currentChanged(int)),
            this, SLOT(slot_current_changed(const int&)));

    // связываем событие выбора сотрудника из ViewSlavesList с обработчиком
    connect(list_tab_content.at(0), SIGNAL(signal_employee_clicked(const int&)),
            this, SLOT(slot_show_employee(const int&)));

    // выводим содержимое первой вкладки
    psw_content_wrapper->setCurrentIndex(0);

    // добавим оболочку в разметку
    pvboxlayout->addWidget(psw_content_wrapper);

    // добавляем разметку в родительский виджет
    this->setLayout(pvboxlayout);
}

/**
 * Слот: смена текущей вкладки
 * @param tabid
 */
void ViewItemSlaves::slot_current_changed(const int& tabid)
{
    qDebug() << QString("Slot: current changed: tabid: %1").arg(tabid);

    // меняем содержимое вкладки
    psw_content_wrapper->setCurrentIndex(tabid);
}

/**
 * Слот: переход на страницу просмотра сотрудника
 * @param enumber - личный номер сотрудника
 */
void ViewItemSlaves::slot_show_employee(const int &enumber)
{
    qDebug() << QString("Slot: show employee...");

    // передаем просмоторщику номер сотрудника
    qobject_cast<ViewSlavesOne*>(list_tab_content.at(this->TAB_INDEX_VIEW))->
            set_current_employee(QString::number(enumber));

    // меняем вкладку
    ptb_tab->setCurrentIndex(this->TAB_INDEX_VIEW);

    // меняем содержимое вклдаки
    psw_content_wrapper->setCurrentIndex(this->TAB_INDEX_VIEW);

    // время получения последнего скрина
    QString time = ViewSlavesOne::get_last_working_time(QString::number(enumber));

    // обновляем данные по сотрудниику
    NetBase::refresh_working(QString::number(enumber),
                             QDate::currentDate().toString("yyyy-MM-dd"),
                             time);
}
