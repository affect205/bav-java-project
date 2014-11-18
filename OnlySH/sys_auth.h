#ifndef SYSAUTH_H
#define SYSAUTH_H

#include <QDialog>
#include <QLineEdit>
#include <QLabel>
#include <QPushButton>
#include <QGridLayout>

class SysAuth : public QDialog
{
    Q_OBJECT
private:
    QLineEdit* ptxt_login;
    QLineEdit* ptxt_password;

public:
    explicit SysAuth(QWidget *parent = 0);

    QString getLogin() const;
    QString getPassword() const;
    bool isAccess() const;

signals:

public slots:


};

#endif // SYSAUTH_H
