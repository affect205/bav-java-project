CONFIG += console
CONFIG += rtti
CONFIG += c++11

QT += widgets
QT += sql
QT += network
QT += xml

SOURCES += \
    main.cpp \
    process_table.cpp \
    menu_sidebar.cpp \
    view_item_slaves.cpp \
    view_item_main.cpp \
    view_item_history.cpp \
    view_slaves_list.cpp \
    view_slaves_one.cpp \
    view_slaves_add.cpp \
    view_slaves_add_validator.cpp \
    view_slaves_list_item.cpp \
    view_item_config.cpp \
    sys_auth.cpp \
    view_slaves_one_working.cpp \
    model_db_sqlite.cpp \
    view_slaves_list_item_photo.cpp \
    sys_config.cpp \
    view_item_config_option.cpp \
    net_master_tcp_client.cpp \
    net_slave_tcp_server.cpp \
    net_base.cpp \
    sys_helper.cpp \
    app_master.cpp \
    app_slave.cpp \
    util_net_activity.cpp \
    util_working_screen_maker.cpp \
    util_working.cpp \
    app_main_window.cpp \
    net_master_client_downloader.cpp \
    view_slaves_one_desktop.cpp

HEADERS += \
    process_table.h \
    menu_sidebar.h \
    view_item_slaves.h \
    view_item_main.h \
    view_item_history.h \
    view_slaves_list.h \
    view_slaves_one.h \
    view_slaves_add.h \
    view_slaves_add_validator.h \
    view_slaves_list_item.h \
    view_item_config.h \
    sys_auth.h \
    view_slaves_one_working.h \
    model_db_sqlite.h \
    i_model_db_driver.h \
    view_slaves_list_item_photo.h \
    sys_config.h \
    view_item_config_option.h \
    net_slave_tcp_server.h \
    net_master_tcp_client.h \
    net_base.h \
    sys_helper.h \
    app_master.h \
    app_slave.h \
    util_net_activity.h \
    util_working_screen_maker.h \
    util_working.h \
    app_main_window.h \
    net_master_client_downloader.h \
    view_slaves_one_desktop.h

FORMS +=

RESOURCES += \
    images.qrc


