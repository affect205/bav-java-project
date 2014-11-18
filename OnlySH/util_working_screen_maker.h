#ifndef UTIL_WORKING_SCREEN_MAKER_H
#define UTIL_WORKING_SCREEN_MAKER_H

#include <QObject>
#include <QApplication>
#include <QDir>
#include <QFileDialog>
#include <QDebug>
#include <QPixmap>
#include <QDesktopWidget>

class UtilWorkingScreenMaker : public QObject
{
    Q_OBJECT

private:


public:

    // создание скриншота рабочей области
    static void make(const QApplication&, const QString &path);

public slots:

};

#endif // UTIL_WORKING_SCREEN_MAKER_H
