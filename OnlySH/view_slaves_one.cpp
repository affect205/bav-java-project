#include "view_slaves_one.h"

#include <QTime>
#include <QDir>

// путь до директории с данными
const QString ViewSlavesOne::DATA_PATH = "C:\\Qt\\data";

ViewSlavesOne::ViewSlavesOne(const QString &enumber, QWidget *parent) :
    enumber(enumber),
    QWidget(parent)
{
    // корневая разметка
    QVBoxLayout* pvboxlt_root = new QVBoxLayout();

    // контейнер элементов
    QGroupBox* pgb_wrap = new QGroupBox;
    pgb_wrap->setTitle("Страница сотрудника");
    pgb_wrap->setAlignment(Qt::AlignLeft);

    // разметка элементов
    QVBoxLayout* pvboxlt_wrap = new QVBoxLayout;
    pvboxlt_wrap->setAlignment(Qt::AlignLeft);

    // вкладки
    QTabBar* ptb_tabbar = new QTabBar;
    ptb_tabbar->setShape(QTabBar::RoundedNorth);
    ptb_tabbar->addTab(QString("Рабочая активность"));
    ptb_tabbar->addTab(QString("Рабочий стол"));
    ptb_tabbar->addTab(QString("Сетевая активность"));
    ptb_tabbar->addTab(QString("Запущенные программы"));
    ptb_tabbar->addTab(QString("Статистика"));

    // инициализиция списка виджетов кладок
    list_tab_widgets.append(new ViewSlavesOneWorking());
    list_tab_widgets.append(new ViewSlavesOneDesktop());

    // виджет для содержимого вкладок
    psw_tab_content = new QStackedWidget();
    psw_tab_content->addWidget(list_tab_widgets.at(0));
    psw_tab_content->addWidget(list_tab_widgets.at(1));
    psw_tab_content->addWidget(new QLabel("<h3>Сетевая активность</h3>"));
    psw_tab_content->addWidget(new QLabel("<h3>Запущенные программы</h3>"));
    psw_tab_content->addWidget(new QLabel("<h3>Статистика</h3>"));

    // связываем смену вкладки с обработчиком
    connect(ptb_tabbar, SIGNAL(currentChanged(int)),
            this,       SLOT(slot_current_changed(const int&)));

    // выводим содержимое первой вкладки
    psw_tab_content->setCurrentIndex(0);

    // добавлям элементы на разметку
    pvboxlt_wrap->addWidget(ptb_tabbar);
    pvboxlt_wrap->addWidget(psw_tab_content);

    pgb_wrap->setLayout(pvboxlt_wrap);

    // добавляем контейнер в разметку
    pvboxlt_root->addWidget(pgb_wrap);
    this->setLayout(pvboxlt_root);
}

/**
 * @brief Слот: смена текущей вкладки
 * @param const int& itemid - индекс вкладки
 */
void ViewSlavesOne::slot_current_changed(const int &itemid) {

    qDebug() << "SLOT: Item selected... itemid: " << itemid;

    // выведем из оболочки выбранный пункт меню
    this->psw_tab_content->setCurrentIndex(itemid);
}

/**
 * @brief Смена текущего сотрудника
 * @param const QString& enumber - личный номер сотрудника
 */
void ViewSlavesOne::set_current_employee(const QString& enumber) {

    if ( this->enumber != enumber )
    {// сотрудник изменен - подружаем новые данные
        this->enumber = enumber;
        load_data();
    }
}

/**
 * @brief Загрузка данных длятекущего сотрудника
 */
void ViewSlavesOne::load_data() {

    // загрузка данных вкладки рабочаая активность
    qobject_cast<ViewSlavesOneWorking*>(list_tab_widgets.at(0))->
            set_current_employee(this->enumber);
}

/**
 * @brief Время получения последнего скрина по сотруднику
 * @param QString& enumber - личный номер сотрудника
 */
QString ViewSlavesOne::get_last_working_time(const QString &enumber) {

    // директория со скринами
    QString path = ViewSlavesOne::DATA_PATH + "\\screen\\" + enumber;
    QDir dir(path);

    QString last = "";

    if ( dir.exists() )
    {// директория существует - сканируем
        QStringList files = dir.entryList();

        foreach ( QString file, files )
        {// проверка файла на соответствие шаблону: yyyy-MM-dd_hh-mm-ss.jpg
            if ( SysHelper::is_working_format(file) && ( file > last ) )
            {
                last = file;
            }
        }
    }

    if ( last != "" )
    {// извлечем из строки время получения
        last = SysHelper::exact_working_time(last);
        qDebug() << "File time: " << last;
    }

    return last;
}
