#include "menu_sidebar.h"

MenuSidebar::MenuSidebar(QWidget *parent) :
    QWidget(parent)
{
    qDebug() << "Class: MenuSidebar...";

    // пункты меню
    QVBoxLayout* pvbl_wrap  = new QVBoxLayout();
    QVBoxLayout* pvbl_group = new QVBoxLayout();
    pvbl_group->setAlignment(Qt::AlignTop);

    QSplitter* psplitter = new QSplitter(Qt::Vertical);
    QGroupBox* pgb_sidebar = new QGroupBox("МЕНЮ");
    pgb_sidebar->setStyleSheet("font: 14px solid black;");

    // заполняем массив с пунктами меню
    vpbtn_items.append(new QPushButton("Главная"));
    vpbtn_items.append(new QPushButton("Сотрудники"));
    vpbtn_items.append(new QPushButton("История"));
    vpbtn_items.append(new QPushButton("Настройки"));

    sigmap = new QSignalMapper(this);

    for (int ndx=0; ndx < vpbtn_items.size(); ++ndx)
    {// делаем flat toggle кнопки и добавим в группу
        vpbtn_items.at(ndx)->setCheckable(true);
        vpbtn_items.at(ndx)->setFlat(true);
        vpbtn_items.at(ndx)->setMinimumSize(120, 30);
        pvbl_group->addWidget(vpbtn_items.at(ndx));

        // при нажатии на кнопку вызываем слот класса QSignalMapper
        connect(vpbtn_items.at(ndx), SIGNAL(clicked()),
                sigmap, SLOT(map()));
        // привязываем кнопку к мапперу
        sigmap->setMapping(vpbtn_items.at(ndx), ndx);
    }

    // обработка нажатия
    QObject::connect(sigmap, SIGNAL(mapped(const int&)),
                     this, SLOT(slot_clicked_item(const int&)));

    // активируем 1-ый пункт меню
    vpbtn_items.at(0)->setChecked(true);

    pgb_sidebar->setLayout(pvbl_group);
    pvbl_wrap->addWidget(pgb_sidebar);

    this->setLayout(pvbl_wrap);
}

/**
 * Слот: Выбор пункта меню
 *
 */
void MenuSidebar::slot_clicked_item(const int& itemid)
{
    //QPushButton* pbtn = (QPushButton*)QObject::sender();
    //pbtn->setText("Clicked");
    for (int ndx=0; ndx < vpbtn_items.size(); ++ndx)
    {
        if ( ndx != itemid )
        {// оставляем выделенным только выбранный пункт меню
            vpbtn_items.at(ndx)->setChecked(false);
        }else
        {// повторно нажатие - выделение не снимается
            vpbtn_items.at(ndx)->setChecked(true);
        }
    }

    // сигнал о нажатии
    qDebug() << "SIGNAL: Item selected...";
    emit(signal_clicked_item(itemid));
}

