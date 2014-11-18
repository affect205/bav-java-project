#ifndef VIEW_SLAVES_LIST_ITEM_PHOTO_H
#define VIEW_SLAVES_LIST_ITEM_PHOTO_H

#include <QLabel>
#include <QDebug>


class ViewSlavesListItemPhoto : public QLabel
{
    Q_OBJECT

    QImage* photo;
public:
    explicit ViewSlavesListItemPhoto(QString& src);
    ~ViewSlavesListItemPhoto() {}

protected:
    virtual void mousePressEvent(QMouseEvent* e);
    virtual void enterEvent(QEvent *e);
    virtual void leaveEvent(QEvent *e);

signals:

public slots:

};

#endif // VIEW_SLAVES_LIST_ITEM_PHOTO_H
