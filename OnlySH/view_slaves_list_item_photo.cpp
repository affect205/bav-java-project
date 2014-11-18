#include "view_slaves_list_item_photo.h"

ViewSlavesListItemPhoto::ViewSlavesListItemPhoto(QString& src) :
    QLabel(src)
{
    photo = new QImage(src);

    // не загрузили фото - ставим фото по умолчанию
    photo->isNull() ?
                this->setText("<b>No Icon</b>") :
                this->setPixmap(QPixmap::fromImage(*photo).scaled(64, 64));

    this->setStyleSheet("border: 2px solid lightgray;");

    show();
}

void ViewSlavesListItemPhoto::mousePressEvent(QMouseEvent *e) {

    qDebug() << "Press...";
}

void ViewSlavesListItemPhoto::enterEvent(QEvent *e) {

    qDebug() << "Enter...";
}

void ViewSlavesListItemPhoto::leaveEvent(QEvent *e) {

    qDebug() << "Leave...";
}
