/** Класс таблицы процессов
*
*
*/

#include "process_table.h"

// конструктор
ProcessTable::ProcessTable()
{
    this->tableWidget = new QTableWidget(3, 3);

    // создаем и заполняем таблицу
    QString qstr = "Item";
    for(int row=0; row != this->tableWidget->rowCount(); ++row){
        for(int column=0; column != this->tableWidget->columnCount(); ++column)
        {
            QTableWidgetItem *newItem = new QTableWidgetItem(qstr);
            this->tableWidget->setItem(row, column, newItem);
        }
    }
}

// деструктор
ProcessTable::~ProcessTable()
{
    delete this->tableWidget;
}

// вывод таблицы
void ProcessTable::show()
{
    this->tableWidget->setEnabled(false);
    this->tableWidget->show();
    return;
}
