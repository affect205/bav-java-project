#ifndef VIEW_SLAVES_LIST_ITEM_H
#define VIEW_SLAVES_LIST_ITEM_H

#include <QWidget>
#include <QLabel>
#include <QGridLayout>
#include <QDebug>
#include <QGroupBox>
#include <QPushButton>
#include <typeinfo>

#include "view_slaves_list_item_photo.h"

class ViewSlavesListItem : public QWidget
{
    Q_OBJECT
private:

    // иконки кнопок перехода
    static QImage* icon_screen_off;
    static QImage* icon_screen_on;

    // данные о сотруднике
    QHash<QString, QString> info;
    QLabel* plbl_title;
    QLabel* plbl_mobile;
    QLabel* plbl_service;
    QLabel* plbl_email;
    QImage* icon_mobile;
    QImage* icon_service;
    QImage* icon_email;
    QLabel* plbl_icon_mobile;
    QLabel* plbl_icon_service;
    QLabel* plbl_icon_email;
    QPushButton* pbtn_icon_screen;
    ViewSlavesListItemPhoto* plbl_icon_user;
    QGroupBox* gb_wrap;


public:
    // конструктор
    explicit ViewSlavesListItem(const QHash<QString, QString> &info,
                                QWidget *parent = 0);

    // установка информации о сотруднике
    void set_item_info(const QHash<QString, QString>& info);

signals:
    // сигнал: выбор сотруника из списка
    void signal_item_clicked(const int& enumber);

public slots:
    // слот: обработка выбор сотруника из списка
    void slot_send_item_clicked();

protected:
    virtual void enterEvent(QEvent *);
    virtual void leaveEvent(QEvent *);
    virtual void paintEvent(QPaintEvent *);
};

#endif // VIEW_SLAVES_LIST_ITEM_H
