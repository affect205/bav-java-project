#include "view_slaves_list_item.h"

#include <QEvent>
#include <QMouseEvent>
#include <QPainter>
#include <QFrame>
#include <QGroupBox>
#include <QVBoxLayout>

QImage* ViewSlavesListItem::icon_screen_off = new QImage(":/images/screen_off.png");
QImage* ViewSlavesListItem::icon_screen_on  = new QImage(":/images/screen_on.png");


ViewSlavesListItem::ViewSlavesListItem(const QHash<QString, QString>& info,
                                       QWidget *parent) :
    info(info),
    QWidget(parent)
{
    // контейнер элементов
    QVBoxLayout* pvboxlt = new QVBoxLayout();
    gb_wrap = new QGroupBox();

    // состяние сотрудника (в сети, не в сети)
    QHash<QString, QString> slave_presence;
    slave_presence["in"] = QString("<b style='color: green;'>В сети</b>");
    slave_presence["out"] = QString("<b style='color: red;'>Не в сети</b>");

    // фото сотрудника
    QString src = ":/images/user_disabled.png";
    plbl_icon_user = new ViewSlavesListItemPhoto(src);
    plbl_icon_user->setMaximumSize(QSize(64, 64));

    // иконка мобильный телефон
    icon_mobile = new QImage(":/images/phone_mobile.png");
    plbl_icon_mobile = new QLabel();
    plbl_icon_mobile->setMaximumHeight(24);

    // иконка служебный телефон
    icon_service = new QImage(":/images/phone_service.png");
    plbl_icon_service = new QLabel();
    plbl_icon_service->setMaximumHeight(24);

    // иконка электронная почта
    icon_email = new QImage(":/images/email.png");
    plbl_icon_email = new QLabel();
    plbl_icon_email->setMaximumHeight(24);

    // кнопка перехода на просмотр сотрудника
    pbtn_icon_screen    = new QPushButton();
    pbtn_icon_screen->setMaximumSize(QSize(64, 64));
    pbtn_icon_screen->setToolTip("Перейти");
    pbtn_icon_screen->setFlat(true);
    pbtn_icon_screen->setIconSize(QSize(36, 36));

    // не загрузили фото - ставим фото по умолчанию
    icon_mobile->isNull()
            ? plbl_icon_mobile->setText("<b>mobile: </b>")
            : plbl_icon_mobile->setPixmap(QPixmap::fromImage(*icon_mobile).scaled(24, 24));

    // не загрузили фото - ставим фото по умолчанию
    icon_service->isNull()
            ? plbl_icon_service->setText("<b>service: </b>")
            : plbl_icon_service->setPixmap(QPixmap::fromImage(*icon_service).scaled(24, 24));

    // не загрузили фото - ставим фото по умолчанию
    icon_email->isNull()
            ? plbl_icon_email->setText("<b>email: </b>")
            : plbl_icon_email->setPixmap(QPixmap::fromImage(*icon_email).scaled(24, 24));

    // не загрузили фото - ставим фото по умолчанию
    icon_screen_off->isNull()
            ? pbtn_icon_screen->setText("<b>Перейти</b>")
            : pbtn_icon_screen->setIcon(QPixmap::fromImage(*icon_screen_off).scaled(36, 36));

    // информация о сотруднике
    QString fullname = "<b>" + info["surname"] + " " + info["firstname"]
            + " " + info["middlename"] + "</b>";

    plbl_title      = new QLabel(fullname + "<br>" + info["position"]
            + "<br>" + slave_presence["out"]);
    plbl_mobile     = new QLabel(QString("+7 ")     + info["mobilephone"]);
    plbl_service    = new QLabel(QString("+7 480")  + info["servicephone"]);
    plbl_email      = new QLabel(info["email"]);

    // разметка для строки контактов
    QGridLayout* pgridlt_list = new QGridLayout();
    pgridlt_list->setAlignment(Qt::AlignLeft);

    // разметка для главной строки
    QVBoxLayout* phboxlt_main      = new QVBoxLayout();
    phboxlt_main->setSpacing(30);
    QHBoxLayout* phboxlt_info      = new QHBoxLayout();
    phboxlt_info->setSpacing(40);
    QHBoxLayout* phboxlt_mobile     = new QHBoxLayout();
    phboxlt_mobile->setSpacing(20);
    QHBoxLayout* phboxlt_service    = new QHBoxLayout();
    phboxlt_service->setSpacing(20);
    QHBoxLayout* phboxlt_email      = new QHBoxLayout();
    phboxlt_email->setSpacing(20);

    // элементы главной строки
    phboxlt_info->addWidget(plbl_icon_user);
    phboxlt_info->addWidget(plbl_title);
    phboxlt_info->addWidget(pbtn_icon_screen);

    // элементы строки контактов
    phboxlt_mobile->addWidget(plbl_icon_mobile);
    phboxlt_mobile->addWidget(plbl_mobile);
    phboxlt_service->addWidget(plbl_icon_service);
    phboxlt_service->addWidget(plbl_service);
    phboxlt_email->addWidget(plbl_icon_email);
    phboxlt_email->addWidget(plbl_email);

    pgridlt_list->addLayout(phboxlt_mobile, 0, 0, Qt::AlignLeft);
    pgridlt_list->addLayout(phboxlt_service, 0, 1, Qt::AlignCenter);
    pgridlt_list->addLayout(phboxlt_email, 0, 2, Qt::AlignRight);

    phboxlt_main->addLayout(phboxlt_info);
    phboxlt_main->addLayout(pgridlt_list);

    // добавляем разметку в группу
    gb_wrap->setLayout(phboxlt_main);
    pvboxlt->addWidget(gb_wrap);

    // обработка клика по кнопке просмотра сотрудника
    QObject::connect(pbtn_icon_screen, SIGNAL(clicked()),
                     this, SLOT(slot_send_item_clicked()));

    this->setLayout(pvboxlt);
}

/**
 * Переход на страницу сотрудника
 */
void ViewSlavesListItem::slot_send_item_clicked()
{
    // выведем на консоль
    qDebug() << QString("Slot: item clicked (enumber=%1)...").arg(info["enumber"]);

    // посылаем сигнал о событии
    emit signal_item_clicked(info["enumber"].toInt());
}

/**
 * Установка информации о сотруднике
 */
void ViewSlavesListItem::set_item_info(const QHash<QString, QString>& info)
{
    this->info = info;
}

/**
 * Уход курсора с виджета
 * @param e
 */
void ViewSlavesListItem::leaveEvent(QEvent *e)
{
    gb_wrap->setStyleSheet("border: none;");
    pbtn_icon_screen->setIcon(QPixmap::fromImage(*icon_screen_off));
    QWidget::leaveEvent(e);
}

/**
 * Приход курсора на виджет
 * @param e
 */
void ViewSlavesListItem::enterEvent(QEvent *e)
{
    gb_wrap->setStyleSheet("QGroupBox{border: 2px solid lightblue; border-radius: 16px;}");
    pbtn_icon_screen->setIcon(QPixmap::fromImage(*icon_screen_on));
    QWidget::leaveEvent(e);
}


void ViewSlavesListItem::paintEvent(QPaintEvent *e)
{
    QWidget::paintEvent(e);
}





