#include "view_item_config_option.h"

#include <QVBoxLayout>
#include <QGridLayout>
#include <QLabel>
#include <QGroupBox>
#include <QVBoxLayout>

ViewItemConfigOption::ViewItemConfigOption(const QString& optkey, QWidget *parent) :
    QWidget(parent),
    option(optkey)
{
    // корневой элемент и разметка
    QGroupBox* pgb_root         = new QGroupBox();
    pgb_root->setStyleSheet("QGroupBox { font: bold 12px;"
                            "color: black;"
                            "border: none; }");
    pgb_root->setTitle("");
    pgb_root->setMinimumSize(QSize(240, 120));


    QVBoxLayout* pvboxlt_root   = new QVBoxLayout();

    // содержимое виджета
    QLabel* lbl_optname  = new QLabel("<b>" + SysConfig::get_option(option, "name") + "</b>");
    QLabel* lbl_optval   = new QLabel("Значение: <b>" + SysConfig::get_option(option, "value") + "</b>");
    QLabel* lbl_desc     = new QLabel("<em>" + SysConfig::get_option(option, "desc") + "</em><hr>");

    // корневая разметка
    QGridLayout* pgrdlt_option = new QGridLayout();
    pgrdlt_option->addWidget(lbl_optname, 0, 0);
    pgrdlt_option->addWidget(lbl_optval,  1, 0);
    pgrdlt_option->addWidget(lbl_desc,    2, 0);


    // располагаем элементы по разметке
    pgb_root->setLayout(pgrdlt_option);
    pvboxlt_root->addWidget(pgb_root);

    this->setLayout(pvboxlt_root);
}
