#include "util_working_screen_maker.h"

#include <QDateTime>
#include <QScreen>
#include <QDebug>
#include <QFile>

/**
 * @brief Создание скриншота рабочей области
 * @param QApplication &app - ссылка на экземпляр приложения
 * @param QString path      - директория для сохранения
 */
void UtilWorkingScreenMaker::make(const QApplication &app, const QString &path)
{
    // делаем скрин экрана
    QDesktopWidget *desktop = app.desktop();
    QScreen* screen = QGuiApplication::primaryScreen();

    if ( !screen )
        return;

    QPixmap originalPixmap = screen->grabWindow(desktop->winId(), 0, 0,
                                               desktop->width(), desktop->height());

    // данные для сохранения
    QString format = "jpg";
    QString name = QDateTime::currentDateTime().toString("/yyyy-MM-dd_hh-mm-ss.")
            + format;

    QDir dir(path);
    if ( ! dir.exists() )
    {// директории не существует - выходим
        qDebug() << "UtilWorkingSceenMaker: dir does't exist...";
        return;
    }

    QString initialPath = path + name;

    qDebug() << "UtilWorkingSceenMaker: making " << initialPath;

    // сохраняем
    QFile file(initialPath);
    file.open(QIODevice::WriteOnly);
    originalPixmap.save(&file, format.toStdString().c_str());
    file.close();


//    // диалог для сохранения файла
//    QString fileName = QFileDialog::getSaveFileName(
//                desktop, tr("Save As"),
//                initialPath,
//                tr("%1 Files (*.%2);;All Files (*)")
//                .arg(format.toUpper())
//                .arg(format));

//    qDebug() << fileName << "\n";

//    if ( ! fileName.isEmpty() ) {
//        originalPixmap.save(fileName, format.toStdString().c_str());
//    }
}
