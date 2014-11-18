#ifndef FORM_ADD_EMPLOYEE_VALIDATOR_H
#define FORM_ADD_EMPLOYEE_VALIDATOR_H

#include <QObject>
#include <QValidator>
#include <QDebug>

class ViewSlavesAddValidator : public QValidator
{ 
public:
    // типы валидаторов
    enum ValidatorType { IP, PORT, STRNAME };

    ViewSlavesAddValidator(QObject* parent, ValidatorType vt);
    virtual State validate(QString& str, int& pos) const;

private:
    ValidatorType vldtype;
};

#endif // FORM_ADD_EMPLOYEE_VALIDATOR_H
