#ifndef UTIL_WORKING_SCREEN_TIMER_H
#define UTIL_WORKING_SCREEN_TIMER_H

#include <QObject>
#include <QTimer>


#include "util_working_screen_maker.h"

class UtilWorkingScreenTimer : public QObject
{
    Q_OBJECT
    int counter = 0;
public:
    explicit UtilWorkingScreenTimer(QObject *parent);
    QTimer* timer;
signals:

public slots:
    void onTimeout();

};

#endif // UTIL_WORKING_SCREEN_TIMER_H
