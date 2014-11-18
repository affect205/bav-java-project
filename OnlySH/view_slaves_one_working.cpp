#include "view_slaves_one_working.h"

ViewSlavesOneWorking::ViewSlavesOneWorking(const QString &enumber, QWidget *parent) :
    enumber(enumber),
    QWidget(parent)
{
    // корневая разметка и эл. управления
    QVBoxLayout* pvboxlt_root       = new QVBoxLayout;
    QHBoxLayout* phboxlt_wrap       = new QHBoxLayout;
    QVBoxLayout* pvboxlt_gallery    = new QVBoxLayout;
    QHBoxLayout* phboxlt_control    = new QHBoxLayout;

    // текущий скрин в просмоторщике
    pixmap = new QPixmap();

    QLabel* plbl_title = new QLabel("Просмотр скриншотов рабочего стола");
    pvboxlt_root->addWidget(plbl_title);

    // иконки запуска и остановки
    iplay.load(":/images/player_play.png");
    ipause.load(":/images/player_pause.png");

    // элементы управления галереей
    control_map["prev"]     = new QPushButton("<");
    control_map["next"]     = new QPushButton(">");
    control_map["zoomout"]  = new QPushButton();
    control_map["zoomin"]   = new QPushButton();
    control_map["play"]     = new QPushButton();

    control_map["zoomin"]->setIcon(QIcon(":/images/zoom_in.png"));
    control_map["zoomout"]->setIcon(QIcon(":/images/zoom_out.png"));
    control_map["play"]->setIcon(QIcon(iplay));

    control_map["zoomin"]->setIconSize(QSize(32, 32));
    control_map["zoomout"]->setIconSize(QSize(32, 32));
    control_map["play"]->setIconSize(QSize(32, 32));

    foreach ( QPushButton* button, control_map ) {
        button->setFixedSize(80, 40);
        button->setFlat(true);
        button->setStyleSheet("QPushButton {"
                              "font-size: 20px; font-weight: bold; }");
    }

    // смотритель директорри сообщит об изм-ях в каталоге
    pfs_watcher = new QFileSystemWatcher();

    // связываем события изменения директории с обработчиком
    connect(pfs_watcher, SIGNAL(directoryChanged(QString)),
            this,        SLOT(slot_refresh_working(QString)));

    // связываем события кнопок с обработчиками
    connect(control_map["prev"], SIGNAL(clicked()),
            this,                SLOT(slot_control_prev()));

    connect(control_map["next"], SIGNAL(clicked()),
            this,                SLOT(slot_control_next()));

    connect(control_map["zoomout"], SIGNAL(clicked()),
            this,                SLOT(slot_control_zoomout()));

    connect(control_map["zoomin"], SIGNAL(clicked()),
            this,                SLOT(slot_control_zoomin()));

    connect(control_map["play"], SIGNAL(clicked()),
            this,                SLOT(slot_control_play()));


    // область просмотра изображения
    this->p_scene   = new QGraphicsScene(QRectF(-100, -100, 400, 400));
    this->p_view    = new QGraphicsView(p_scene);
    this->p_view->setBackgroundBrush(QBrush(Qt::black, Qt::SolidPattern));

    // виджет списка доступных изображений
    this->plw_preview = new QListWidget();

    // загружаем снимки
    load_data();

    // связываем событие выбора элемента списка с обработчиком
    connect(plw_preview, SIGNAL(itemSelectionChanged()),
            this,        SLOT(slot_item_selection_changed()));

    // размер сцены берем по умолчанию
    int width   = SCENE_DEF_WIDTH;
    int height  = SCENE_DEF_HEIGHT;

    if ( ! pixmap->isNull() )
    {// возьмем параметры снимка
        width   = pixmap->width();
        height  = pixmap->height();
    }

    this->p_scene->setSceneRect(QRectF(0, 0, width, height));

    // таймер эффекта смена изображения
    timer   = new QTimer();
    timer->setInterval(40);
    //connect(timer, SIGNAL(timeout()), this, SLOT(slot_time()));

    // таймер автоматического показа
    timer_play = new QTimer();
    timer_play->setInterval(play_speed);
    connect(timer_play, SIGNAL(timeout()), this, SLOT(slot_control_next()));

    // слайдер скорости показа и строка значения
    psld_playtime = new QSlider();
    psld_playtime->setRange(0, 50);
    psld_playtime->setPageStep(5);
    psld_playtime->setSingleStep(5);
    psld_playtime->setValue(play_speed/100);
    psld_playtime->setOrientation(Qt::Vertical);

    plbl_playtime = new QLabel("<b>1.0 сек.</b>");
    plbl_playtime->setFixedWidth(30);
    connect(psld_playtime, SIGNAL(valueChanged(int)), this, SLOT(slot_set_view_range(int)));


    // свойства списка изображений
    plw_preview->setIconSize(QSize(80, 60));
    plw_preview->setSpacing(4);
    plw_preview->setFixedWidth(100);
    plw_preview->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
    if ( pixmap_list.count() > 0 ) {
        plw_preview->setCurrentRow(0);
    }

    // добавляем элементы в размеку
    phboxlt_control->addWidget(control_map["prev"]);
    phboxlt_control->addStretch(1);
    phboxlt_control->addWidget(control_map["zoomout"]);
    phboxlt_control->addStretch(1);
    phboxlt_control->addWidget(control_map["play"]);
    phboxlt_control->addWidget(psld_playtime);
    phboxlt_control->addWidget(plbl_playtime);
    phboxlt_control->addStretch(1);
    phboxlt_control->addWidget(control_map["zoomin"]);
    phboxlt_control->addStretch(1);
    phboxlt_control->addWidget(control_map["next"]);

    pvboxlt_gallery->addWidget(p_view);
    pvboxlt_gallery->addLayout(phboxlt_control);

    phboxlt_wrap->addLayout(pvboxlt_gallery);
    phboxlt_wrap->addWidget(plw_preview);

    pvboxlt_root->addLayout(phboxlt_wrap);

    this->setLayout(pvboxlt_root);
}

/**
 * @brief загрузка изображений
 */
void ViewSlavesOneWorking::load_data() {

    // удаляем предыдущие снимки
    clear_previous();

    // сканируем директорию с изображениями
    QDir dir(get_data_path());

    // сканируем директорию
    foreach (QString file, dir.entryList(QDir::Files))
    {//qDebug() << file;
        if ( file == "." || file == ".." ) {
            continue;
        }
        pixmap_list.append(file);
    }

    // добавляем скрин в виджет списка
    foreach ( QString file, pixmap_list )
    {
        QIcon icon(QPixmap(get_data_path() + "\\" + file).scaled(80, 60));
        QListWidgetItem* item = new QListWidgetItem(icon, "", plw_preview);

        preview_item_list.append(item);
    }

    if ( ! pixmap_list.empty() )
    {// выводим на экран первое изображение
        pixmap = new QPixmap(get_data_path() + "\\" + pixmap_list.at(0));

        // добавляем изображение на сцену
        p_pixmap_item = p_scene->addPixmap(*pixmap);
        //p_pixmap_item->setFlags(QGraphicsItem::ItemIsMovable);
    }
}

/**
 * @brief обновление данных для текущего сотрудника
 * p.s. по сравнению с load_data сцена не очищается;
 * скрины добавляются, не загружаются заново
 */
void ViewSlavesOneWorking::refresh_data() {

    // сканируем директорию с изображениями
    QDir dir(get_data_path());

    // сканируем директорию
    foreach (QString file, dir.entryList(QDir::Files))
    {//qDebug() << file;
        if ( file == "." || file == ".." ) {
            continue;
        }

        qDebug() << "screen: " << file;

        if ( ! pixmap_list.contains(file) )
        {// скрина нет в списке - добавляем
            pixmap_list.append(file);

            // добавляем скрин в виджет списка
            QIcon icon(QPixmap(get_data_path() + "\\" + file).scaled(80, 60));
            QListWidgetItem* item = new QListWidgetItem(icon, "", plw_preview);

            preview_item_list.append(item);
        }
    }
}

/**
 * @brief Удаление предыдущих снимков
 */
void ViewSlavesOneWorking::clear_previous() {

    // очистка виджета предварительного просмотра
    plw_preview->clear();

    // очистка элементов списка
    preview_item_list.clear();

    // очистка данных
    pixmap_list.clear();

    // очистка оюласти просмотра
    p_scene->clear();

    // освобождаем память для текущего снимка
    if ( !pixmap->isNull() ) {
        delete pixmap;
    }
}

/**
 * @brief Смена текущего сотрудника
 */
void ViewSlavesOneWorking::set_current_employee(const QString& enumber) {

    qDebug() << QString("Set current employee prev(%1) next(%2)").arg(this->enumber).arg(enumber);

    if ( this->enumber != enumber )
    {// сотрудник изменен - загрузка новых данных
        this->enumber = enumber;
        load_data();
        // текущая директория
        pfs_watcher->addPath(get_data_path());
    }
}

/**
 * @brief Слот: смена текущего элемента списка
 */
void ViewSlavesOneWorking::slot_item_selection_changed() {

    qDebug() << "Slot: item selection changed...";

    // сменим изображение на выбранное
    const int ndx = plw_preview->currentRow();

    // директория со снимками
    QString path = get_data_path();

    if ( pixmap->load(path + "\\" + pixmap_list.at(ndx)) )
    {
        p_scene->removeItem(p_pixmap_item);
        p_scene->setSceneRect(QRectF(0, 0, pixmap->width(), pixmap->height()));
        p_pixmap_item->setOpacity(0.0);
        timer->start();
        p_pixmap_item = p_scene->addPixmap(*pixmap);
        //p_pixmap_item->setFlags(QGraphicsItem::ItemIsMovable);
    }
}

/**
 * @brief Получение директории со снимками
 * @return QString path
 */
QString ViewSlavesOneWorking::get_data_path() {

    QString dir = ( this->enumber.isEmpty() ) ? "" : this->enumber;

    if ( dir == "" ) {
        return DATA_PATH;
    }

    return DATA_PATH + "\\" + dir;
}

/**
 * @brief Слот: событие таймера
 */
void ViewSlavesOneWorking::slot_time() {

    if ( opacity >= 1.0 )
    {// полная прзрачность - стоп таймер
        p_pixmap_item->setOpacity(1.0);
        opacity = 0.0;
        timer->stop();
    } else
    {// иначе наращиваем прозрачность
        p_pixmap_item->setOpacity(opacity);
        opacity += 0.1;
    }
}

/**
 * @brief Слот: предыдущее изображение
 */
void ViewSlavesOneWorking::slot_control_prev() {

    const int ndx = plw_preview->currentRow();

    // переходим на след. изображение
    plw_preview->setCurrentRow( (ndx <= 0 ) ? plw_preview->count()-1 : ndx-1 );

    // иницируем событие изменения изображения
    emit slot_item_selection_changed();
}

/**
 * @brief Слот: следующее изображение
 */
void ViewSlavesOneWorking::slot_control_next() {

    const int ndx = plw_preview->currentRow();

    // переходим на след. изображение
    plw_preview->setCurrentRow( (ndx >= (plw_preview->count()-1 ) ? 0 : ndx+1) );

    // иницируем событие изменения изображения
    emit slot_item_selection_changed();
}

/**
 * @brief Слот: отдаление изображения
 */
void ViewSlavesOneWorking::slot_control_zoomout() {

    if ( zoom_depth > ZOOM_OUT_LIMIT ) {
        p_view->scale(1/1.2, 1/1.2);
        zoom_depth--;
    }
}

/**
 * @brief Слот: приближение изображения
 */
void ViewSlavesOneWorking::slot_control_zoomin() {

    if ( zoom_depth < ZOOM_IN_LIMIT ) {
        p_view->scale(1.2, 1.2);
        zoom_depth++;
    }
}

/**
 * @brief Слот: автоматический показ снимков
 */
void ViewSlavesOneWorking::slot_control_play() {

    // меняем состояние
    play = ! play;

    // меняем иконку
    ( play ) ? control_map["play"]->setIcon(ipause) : control_map["play"]->setIcon(iplay);

    // запускаем/останавливаем показ
    ( play ) ? timer_play->start() : timer_play->stop();
}

/**
 * @brief Слот: обновление данных по сотруднику
 * @param str
 */
void ViewSlavesOneWorking::slot_refresh_working(QString dir) {

    qDebug() << " Directory changed: " << dir;

    if ( dir == get_data_path() )
    {// изменние текущей директории - обновляем данные
        refresh_data();
    }
}

/**
 * @brief Слот: отображение длительности показа
 * @param int
 */
void ViewSlavesOneWorking::slot_set_view_range(int val) {

    if ( val % 5 != 0 )
    {// выравнивание значение
        val -= val % 5;
    }

    // скорость показа
    ( val == 0 ) ? play_speed = 200 : play_speed = val * 100;
    // интервал таймера
    timer_play->setInterval(play_speed);
    // корректировка слайдера
    psld_playtime->setValue(val);
    // показ значения
    double vl = (double)(val*100)/1000;
    plbl_playtime->setText(QString("<b>%1 сек.</b>").arg(vl));
}
