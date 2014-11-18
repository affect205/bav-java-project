#include "view_item_history.h"

ViewItemHistory::ViewItemHistory(QWidget *parent) :
    QWidget(parent)
{
    QVBoxLayout* pvboxlayout = new QVBoxLayout();
    QLabel* plbl_title = new QLabel("<h3>Главная:</h3><hr>");
    pvboxlayout->addWidget(plbl_title);
    this->setLayout(pvboxlayout);
}
