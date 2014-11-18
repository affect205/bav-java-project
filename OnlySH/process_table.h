#ifndef PROCESS_TABLE_H
#define PROCESS_TABLE_H

#include <QTableWidget>
#include <QString>
#include <QObject>
#include <QTabBar>
#include <QListView>

class ProcessTable : public QObject
{
    Q_OBJECT

private:
    QTableWidget *tableWidget;

public:
    // конструктор (explicit - запрет неявного конструктoра)
    explicit ProcessTable();

    // деструктор
    ~ProcessTable();

    // отображение таблицы
    void show();
};

#endif // PROCESS_TABLE_H


