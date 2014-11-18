#ifndef VIEW_SLAVES_ONE_WORKING_H
#define VIEW_SLAVES_ONE_WORKING_H

#include <QWidget>
#include <QGraphicsScene>
#include <QGraphicsItem>
#include <QGraphicsPixmapItem>
#include <QGraphicsView>
#include <QVBoxLayout>
#include <QLabel>
#include <QListView>
#include <QDir>
#include <QDebug>
#include <QImage>
#include <QGridLayout>
#include <QListWidget>
#include <QListWidgetItem>
#include <QPushButton>
#include <QTimer>
#include <QGraphicsOpacityEffect>
#include <QFileSystemWatcher>
#include <QSlider>

#include "net_base.h"

class ViewSlavesOneWorking : public QWidget
{
    Q_OBJECT
private:

    // виджет списка доступных изображений
    QListWidget* plw_preview;

    // элементы списка
    QList<QListWidgetItem*> preview_item_list;

    // элементы управления
    QMap<QString, QPushButton*> control_map;

    // список доступных изображений
    QStringList pixmap_list;

    // область просмотра изображения
    QGraphicsScene* p_scene;
    QGraphicsView* p_view;

    // текущий объект на сцене
    QGraphicsItem* p_pixmap_item;

    // глубина zoom
    int zoom_depth = 0;

    // таймер для смены изображения на сцене
    QTimer* timer;

    // таймер для автоматического показа изображений
    QTimer* timer_play;

    // слайдер скорости показа
    QSlider* psld_playtime;
    QLabel* plbl_playtime;

    // скорость показа
    int play_speed = 1000;


    // текущая прозрачность области просмотра
    float opacity = 0.0f;

    // предельная глубина zoom
    const int ZOOM_IN_LIMIT = 8;
    const int ZOOM_OUT_LIMIT = -4;

    // директория с изображениями
    const QString DATA_PATH = "C:\\Qt\\data\\screen";

    // ширина и высота сцены по умолчанию
    const int SCENE_DEF_WIDTH = 1280;
    const int SCENE_DEF_HEIGHT = 1024;

    // личный номер сотрудника
    QString enumber = "";

    // текущий снимок
    QPixmap* pixmap;

    // список сников экрана
    QList<QPixmap*> list_screen;

    // иконки зпуска и остановки
    QPixmap iplay;
    QPixmap ipause;

    // режим показа
    bool play = false;

    // смотритель директории - сообщает об добавлении файлов
    QFileSystemWatcher* pfs_watcher;



public:

    // конструктор
    explicit ViewSlavesOneWorking(const QString& enumber = "", QWidget *parent = 0);

    // загрузка данных для текущего сотрудника
    void load_data();

    // обновление данных для текущего сотрудника
    void refresh_data();

    // смена текущего сотрудника
    void set_current_employee(const QString& enumber);

protected:

    // подучение директории со снимками
    QString get_data_path();

    // удаление предыдущих снимков
    void clear_previous();

signals:

public slots:
    void slot_item_selection_changed();
    void slot_control_prev();
    void slot_control_next();
    void slot_control_zoomout();
    void slot_control_zoomin();
    void slot_control_play();
    void slot_time();
    void slot_set_view_range(int);

    // обновление данных по сотруднику
    void slot_refresh_working(QString dir);
};

#endif // VIEW_SLAVES_ONE_WORKING_H
