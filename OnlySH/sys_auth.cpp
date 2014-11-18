#include "sys_auth.h"

#include <QDebug>

SysAuth::SysAuth(QWidget *parent) :
    QDialog(parent)
{
    // внешний вид диалога
    this->setWindowTitle("Вход в систему");

    // инициализация элементов управления
    ptxt_login      = new QLineEdit;
    ptxt_password   = new QLineEdit;
    ptxt_password->setEchoMode(QLineEdit::Password);

    QLabel* plbl_login      = new QLabel("&Логин");
    QLabel* plbl_password   = new QLabel("&Пароль");

    plbl_login->setBuddy(ptxt_login);
    plbl_password->setBuddy(ptxt_password);

    QPushButton* pbtn_ok     = new QPushButton("&Войти");
    QPushButton* pbtn_cancel = new QPushButton("&Отмена");

    // создание разметки
    QGridLayout* pgridlt_root = new QGridLayout();

    // размещение элементов
    pgridlt_root->addWidget(plbl_login, 1, 0);
    pgridlt_root->addWidget(ptxt_login, 1, 1);
    pgridlt_root->addWidget(plbl_password, 2, 0);
    pgridlt_root->addWidget(ptxt_password, 2, 1);

    pgridlt_root->addWidget(pbtn_ok, 3, 0);
    pgridlt_root->addWidget(pbtn_cancel, 3, 1);


    // связываем кнопки с обработчиками
    connect(pbtn_ok, SIGNAL(clicked()), this, SLOT(accept()));
    connect(pbtn_cancel,SIGNAL(clicked()), this, SLOT(reject()));

    this->setLayout(pgridlt_root);
}

/**
 * Получение логина
 * @return
 */
QString SysAuth::getLogin() const {
    return ptxt_login->text();
}

/**
 * Получение пароля
 * @return
 */
QString SysAuth::getPassword() const {
    return ptxt_password->text();
}

bool SysAuth::isAccess() const {
    return true;
}

