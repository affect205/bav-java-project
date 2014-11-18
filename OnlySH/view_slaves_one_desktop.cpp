#include "view_slaves_one_desktop.h"

#include <QVBoxLayout>
#include <QLabel>

ViewSlavesOneDesktop::ViewSlavesOneDesktop(QWidget *parent) :
    QWidget(parent)
{
    // корневая разметка
    QVBoxLayout* pvboxlt_root       = new QVBoxLayout;

    pvboxlt_root->addWidget(new QLabel("<h3>Рабочий стол</h3>"));

    // установка разметки
    this->setLayout(pvboxlt_root);
}
