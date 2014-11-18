#ifndef FORM_ADD_EMPLOYEE_H
#define FORM_ADD_EMPLOYEE_H

#include <QObject>
#include <QtWidgets/QLabel>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QLineEdit>
#include <QtWidgets/QComboBox>
#include <QDebug>
#include <QMessageBox>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QLayout>

#include "view_slaves_add_validator.h"

class ViewSlavesAdd : public QWidget
{
    Q_OBJECT
public:
    explicit ViewSlavesAdd(QWidget* parent = 0);
signals:

public slots:
    void create_employee();
};

#endif // FORM_ADD_EMPLOYEE_H
