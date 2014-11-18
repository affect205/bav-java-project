#ifndef MODEL_DB_H
#define MODEL_DB_H

#include <QObject>

class ModelDB : public QObject
{
    Q_OBJECT
public:


    explicit ModelDB(DB_DRIVER driver, QObject *parent = 0);

signals:

public slots:

};

#endif // MODEL_DB_H
