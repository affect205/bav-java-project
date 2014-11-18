#include "view_item_main.h"

ViewItemMain::ViewItemMain(QWidget *parent) :
    QWidget(parent)
{
    QVBoxLayout* pvboxlayout = new QVBoxLayout();
    QLabel* plbl_title = new QLabel("<h3>Главная:</h3><hr>");
    pvboxlayout->addWidget(plbl_title);
    this->setLayout(pvboxlayout);
}
