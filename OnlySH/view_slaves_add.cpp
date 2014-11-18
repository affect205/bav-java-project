#include "view_slaves_add.h"

#include "QGroupBox"

ViewSlavesAdd::ViewSlavesAdd(QWidget* parent) :
    QWidget(parent)
{
    // корневая разметка
    QVBoxLayout* pvboxlt_main = new QVBoxLayout;

    // контейнер элементов
    QGroupBox* pgb_wrap = new QGroupBox();
    pgb_wrap->setTitle("Добавление сотрудника");

    // разметка для разметок
    QVBoxLayout* pvboxlt_wrap = new QVBoxLayout;
    pvboxlt_wrap->setAlignment(Qt::AlignLeft);
    pvboxlt_wrap->setSpacing(50);

    // контейнер колонок
    QHBoxLayout* phboxlt_columns = new QHBoxLayout;
    phboxlt_columns->setAlignment(Qt::AlignJustify);
    // левая колонка
    QVBoxLayout* pvboxlt_left = new QVBoxLayout;
    pvboxlt_left->setAlignment(Qt::AlignLeft);
    // правая колонка
    QVBoxLayout* pvboxlt_right = new QVBoxLayout;
    pvboxlt_right->setAlignment(Qt::AlignLeft);

    // элементы управления
    QPushButton* pbtn_send = new QPushButton("Создать");
    pbtn_send->setMaximumWidth(80);

    // валидаторы ввода текста
    ViewSlavesAddValidator* pvld_txt = new ViewSlavesAddValidator(
                NULL, ViewSlavesAddValidator::STRNAME);
    ViewSlavesAddValidator* pvld_ip = new ViewSlavesAddValidator(
                NULL, ViewSlavesAddValidator::IP);
    ViewSlavesAddValidator* pvld_port = new ViewSlavesAddValidator(
                NULL, ViewSlavesAddValidator::PORT);

    QLabel* plbl_sname  = new QLabel("<b>Фамилия:</b>");
    QLabel* plbl_fname  = new QLabel("<b>Имя:</b>");
    QLabel* plbl_mname  = new QLabel("<b>Отчество:</b>");
    QLabel* plbl_pos    = new QLabel("<b>Должность:</b>");
    QLabel* plbl_ipport = new QLabel("<b>ip/port:</b>");
    QLabel* plbl_mode   = new QLabel("<b>Режим:</b>");
    QLabel* plbl_colon  = new QLabel("<b>:</b>");
    plbl_colon->setMaximumWidth(10);

    QLineEdit* ptxt_sname   = new QLineEdit;
    QLineEdit* ptxt_fname   = new QLineEdit;
    QLineEdit* ptxt_mname   = new QLineEdit;
    QLineEdit* ptxt_pos     = new QLineEdit;
    QLineEdit* ptxt_ip      = new QLineEdit;
    QLineEdit* ptxt_port    = new QLineEdit;

    // валидаторы текстовых эл-ов
    ptxt_sname->setValidator(pvld_txt);
    ptxt_fname->setValidator(pvld_txt);
    ptxt_mname->setValidator(pvld_txt);
    ptxt_pos->setValidator(pvld_txt);
    ptxt_ip->setValidator(pvld_ip);
    ptxt_port->setValidator(pvld_port);

    // размеры элементов
    ptxt_sname->setMaximumWidth(200);
    ptxt_fname->setMaximumWidth(200);
    ptxt_mname->setMaximumWidth(200);
    ptxt_pos->setMaximumWidth(200);
    ptxt_ip->setMaximumWidth(120);
    ptxt_port->setMaximumWidth(60);

    QComboBox* pcb_mode    = new QComboBox;
    QStringList psl_modes;

    psl_modes << "Упрощенный" << "Стандартный" << "Усиленный";
    pcb_mode->addItems(psl_modes);
    pcb_mode->setEditable(true);


    //plbllastname->setBuddy(ptxt);
    //QObject::connect(ptxt, SIGNAL(textChanged(const QString&)),
    //                 plbl_sname, SLOT(setText(const QString&)));

    QObject::connect(pbtn_send, SIGNAL(clicked()), this, SLOT(create_employee()));

    // распределяем эл-ты по разметке
    /*
    for (int i=0; i < 7; ++i) {
        vphboxlayout.push_back(new QHBoxLayout);
    }*/

    int ndx_space = 30;

    // установка границ и смещения
    pvboxlt_left->setSpacing(ndx_space);
    pvboxlt_right->setSpacing(ndx_space);

    // заполнение левой колонки
    pvboxlt_left->addStrut(100);
    pvboxlt_left->addWidget(plbl_sname);
    pvboxlt_left->addWidget(plbl_fname);
    pvboxlt_left->addWidget(plbl_mname);
    pvboxlt_left->addWidget(plbl_pos);
    pvboxlt_left->addWidget(plbl_ipport);
    pvboxlt_left->addWidget(plbl_mode);

    // заполнение правой колонки
    pvboxlt_right->addWidget(ptxt_sname);
    pvboxlt_right->addWidget(ptxt_fname);
    pvboxlt_right->addWidget(ptxt_mname);
    pvboxlt_right->addWidget(ptxt_pos);

    // ip и порт в отдельную разметку
    QHBoxLayout* phboxlt_ipport = new QHBoxLayout();
    phboxlt_ipport->addWidget(ptxt_ip);
    phboxlt_ipport->addWidget(plbl_colon);
    phboxlt_ipport->addWidget(ptxt_port);
    pvboxlt_right->addLayout(phboxlt_ipport);

    pvboxlt_right->addWidget(pcb_mode);


    // добавляем эл-ты в родительскую разметку
    //foreach (QHBoxLayout* layout, vphboxlayout) {
        //pvboxlayout->addLayout(layout);
    //}

    /*
    for (int i=0; i < vphboxlayout.size(); ++i)
    {
        pvboxlayout->addLayout(vphboxlayout.at(i));
        pvboxlayout->addStrut(50);
    }*/

    // добавляем колонки в главную разметку
    phboxlt_columns->addLayout(pvboxlt_left);
    phboxlt_columns->addLayout(pvboxlt_right);

    pvboxlt_wrap->addLayout(phboxlt_columns);
    pvboxlt_wrap->addWidget(pbtn_send);

    pgb_wrap->setLayout(pvboxlt_wrap);
    pvboxlt_main->addWidget(pgb_wrap);

    // pvboxlt_main->addLayout(phboxlt_columns);
    // pvboxlt_main->addWidget(pbtn_send);

    // возвращаем разметку в родительский контейнер
    this->setLayout(pvboxlt_main);
}

void ViewSlavesAdd::create_employee()
{
    QMessageBox::information(NULL, "Info", "New employee has been created...");
}


