'use strict';
angular
    .module('app')
    .controller('ScheduleCtrl', ScheduleCtrl)

function ScheduleCtrl($http, $location, $localStorage, $scope, uiGridConstants, i18nService, $uibModal, dialogs, $route) {
    var version_api = "v1";
    
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';

    webix.Date.startOnMonday = true;

    $scope.colums = [{
            id: "doctorLabel",
            header: 'Доктор',
            width: 350,
            sort: 'string'
        },
        {
            id: "dateWorkLabel",
            header: 'Дата приема',
            width: 150,
            sort: 'string'
        },
        {
            id: "timeFrom",
            header: "Время с",
            width: 140
        },
        {
            id: "timeTo",
            header: "Время по",
            width: 140
        },
        {
            id: "timeFromOnline",
            header: "Время с (online)",
            width: 140
        },
        {
            id: "timeToOnline",
            header: "Время по (online)",
            width: 140
        },
        {
            id: "interval",
            header: "Интервал приема",
            width: 140
        }
    ]

    webix.ready(function () {
        webix.proxy.addHeaders = {
            $proxy: true,
            load: function (view, callback, params) {
                webix.ajax().bind(view).headers($localStorage.headers.value).get(this.source, params, callback);
            }
        };

        webix.ui({
            view: "layout",
            label: "filter",
            container: "filter",
            id: "filter",
            borderless: true,
            width: 1200,
            rows: [{
                cols: [
                    {
                        gravity: 1.2,
                        cols: [{
                            view: "text",
                            label: "ФИО доктора",
                            id: "filterDoctor",
                            labelWidth: 120,
                            inputWidth: 350,
                        }]
                    },
                    {
                        cols: [{
                            view: "datepicker",
                            editable: true,
                            label: "Дата приема",
                            id: "filterDateWork",
                            labelWidth: 120,
                            inputWidth: 300
                        }]
                    },
                    {
                        gravity: 0.3,
                        cols: [{
                                view: "button",
                                type: "image",
                                image: "../img/funnel_add_24.png",
                                value: "Apply",
                                click: function () {
                                    $$('table').clearAll();
                                    var url = $scope.getTableURI(15, 0);
                                    $$('table').load("addHeaders->" + url);
                                    $$('table').refresh();
                                    $$('table').setPage(0);
                                }
                            },
                            {
                                view: "button",
                                type: "image",
                                image: "../img/funnel_delete_24.png",
                                value: "Reset",
                                click: function () {
                                    $$('filterDateWork').setValue(null)
                                    $$('filterDoctor').setValue("");
                                    $$('table').clearAll();
                                    var url = $scope.getTableURI(15, 0);
                                    $$('table').load("addHeaders->" + url);
                                    $$('table').refresh();
                                    $$('table').setPage(0);
                                }
                            },
                        ]
                    },
                    {
                        gravity: 1.4,
                        view: "layout",
                        rows: [{}]
                    }
                ]
            }]
        });

        webix.ui({
            view: "window",
            id: "editwin",
            head: "Расписание",
            modal: true,
            position: "center",
            width: 650,
            autoheight: false,
            body: {
                view: "form",
                id: "editform",
                complexData: true,
                elements: [{
                        view: "combo",
                        label: "Доктор",
                        name: "doctor",
                        labelWidth: 155,
                        options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/doctors").response),
                        invalidMessage: ""
                    },
                    {
                        cols: [{
                                view: "datepicker",
                                editable: true,
                                label: "Дата приема",
                                name: "dateWork",
                                labelWidth: 155,
                                invalidMessage: ""
                            },
                            {
                                view: "layout",
                                rows: [{}]
                            }
                        ]
                    },
                    {
                        cols: [{
                                view: "datepicker",
                                editable: true,
                                label: "Время с",
                                id: "timeFrom",
                                name: "timeFrom",
                                type: "time",
                                stringResult: true,
                                labelWidth: 155
                            },
                            {
                                view: "datepicker",
                                editable: true,
                                label: "Время по",
                                id: "timeTo",
                                name: "timeTo",
                                type: "time",
                                inputWidth: 270,
                                labelWidth: 100,
                                stringResult: true,
                                css: "margin_sched_col"
                            }
                        ]
                    },
                    {
                        cols: [{
                                view: "text",
                                label: "Интервал приема",
                                name: "interval",
                                labelWidth: 155
                            },
                            {
                                view: "layout",
                                rows: [{
                                    view: "label",
                                    label: "&nbsp;минут",
                                }]
                            }
                        ]
                    },
                    {
                        cols: [{
                                view: "datepicker",
                                editable: true,
                                label: "Перерыв с",
                                id: "breakFrom",
                                name: "breakFrom",
                                type: "time",
                                stringResult: true,
                                labelWidth: 155
                            },
                            {
                                view: "datepicker",
                                editable: true,
                                label: "по",
                                id: "breakTo",
                                name: "breakTo",
                                type: "time",
                                inputWidth: 270,
                                labelWidth: 100,
                                stringResult: true,
                                css: "margin_sched_col"
                            },
                        ]
                    },
                    {
                        view: "template",
                        type: "section",
                        template: "<span class='lb_template'>Онлайн расписание</span>"
                    },
                    {
                        cols: [{
                                view: "datepicker",
                                editable: true,
                                label: "Время с",
                                id: "timeFromOnline",
                                name: "timeFromOnline",
                                type: "time",
                                stringResult: true,
                                labelWidth: 155
                            },
                            {
                                view: "datepicker",
                                editable: true,
                                label: "Время по",
                                id: "timeToOnline",
                                name: "timeToOnline",
                                type: "time",
                                stringResult: true,
                                inputWidth: 270,
                                labelWidth: 100,
                                css: "margin_sched_col"
                            }
                        ]
                    },
                    {
                        cols: [{
                                view: "button",
                                type: "form",
                                value: "Save",
                                label: "Сохранить",
                                click: function () {
                                    if ($$("editform").validate()) {
                                        $scope.saveRow();
                                        if ($scope.serverValidation) {
                                            this.getTopParentView().hide();
                                        }
                                    }
                                }
                            },
                            {
                                view: "button",
                                value: "Cancel",
                                label: "Отмена",
                                click: function () {
                                    this.getTopParentView().hide();
                                    var item = $$("table").getSelectedId();
                                    if (item != null && item != undefined && item != "") {
                                        $$("table").unselectAll();
                                        $$("table").select(item);
                                    }
                                }
                            }
                        ]
                    }
                ],
                rules: {
                    doctor: webix.rules.isNotEmpty,
                    dateWork: webix.rules.isNotEmpty,
                    timeFrom: function (value) {
                        if (!webix.rules.isNotEmpty(value)) {
                            return false;
                        } else {
                            if ($$("timeTo").getValue() != null && $$("timeTo").getValue() != undefined && $$("timeTo").getValue() != "") {
                                if ($$("timeTo").getValue() < value) {
                                    webix.alert({
                                        title: "Ошибка",
                                        ok: "ОК",
                                        type: "alert-error",
                                        text: " \"Время с\" должно быть меньше \"Время по\" "
                                    });
                                    return false;
                                } else {
                                    return true;
                                }

                            } else {
                                return true;
                            }
                        }
                        return true;
                    },
                    timeTo: function (value) {
                        if (!webix.rules.isNotEmpty(value)) {
                            return false;
                        } else {
                            if ($$("timeFrom").getValue() != null && $$("timeFrom").getValue() != undefined && $$("timeFrom").getValue() != "") {
                                if ($$("timeFrom").getValue() > value) {
                                    return false;
                                } else {
                                    return true;
                                }
                            } else {
                                return true;
                            }
                        }
                        return true;
                    },
                    timeFromOnline: function (value) {
                        if ($$("timeToOnline").getValue() != null && $$("timeToOnline").getValue() != undefined && $$("timeToOnline").getValue() != "") {
                            if ($$("timeToOnline").getValue() < value) {
                                webix.alert({
                                    title: "Ошибка",
                                    ok: "ОК",
                                    type: "alert-error",
                                    text: " \"Время с\" должно быть меньше \"Время по\" в онлайн расписании "
                                });
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return true;
                        }
                        return true;
                    },
                    timeToOnline: function (value) {
                        if ($$("timeFromOnline").getValue() != null && $$("timeFromOnline").getValue() != undefined && $$("timeFromOnline").getValue() != "") {
                            if ($$("timeFromOnline").getValue() > value) {
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return true;
                        }
                        return true;
                    }
                }
            }
        });

        webix.ui({
            id: "table",
            container: "scheduleGrid",
            view: "datatable",
            autoheight: true,
            width: 1200,
            editable: true,
            scroll: false,
            editaction: "custom",
            select: "row",
            datafetch: 15,
            datathrottle: 300,
            loadahead: 15,
            data: webix.ajax().headers($localStorage.headers.value).get("/api/"+version_api+"/schedule"),
            datatype: "json",
            columns: $scope.colums,
            pager: {
                template: "{common.first()} {common.prev()} {common.pages()} {common.next()} {common.last()}",
                container: 'pager',
                size: 15,
                group: 5
            },
            on: {
                onItemDblClick: function () {
                    $$("editform").clearValidation();
                    $$("editwin").show();
                },
                onBeforeLoad: function () {
                    this.showOverlay("Загрузка...");
                },
                onAfterLoad: function () {
                    if (!this.count())
                        this.showOverlay("Нет данных для отображения");
                    else
                        this.hideOverlay();
                },
                onDataRequest: function (start, count) {
                    var url = $scope.getTableURI(count, start);
                    var dataGrid = webix.ajax().headers($localStorage.headers.value).get(url);
                    this.parse(dataGrid);
                    return false;
                }
            }
        });

        $$("editform").bind($$("table"));
    });

    $scope.getTableURI = function (count, start) {
        var url = "/api/"+version_api+"/schedule?count=" + count + "&start=" + start + "&filter=";
        if ($$("filterDateWork").getValue() != null && $$("filterDateWork").getValue().toJSON() != "")
            url = url + "dateWork=" + $$("filterDateWork").getValue().toJSON() + ";";
        if ($$("filterDoctor").getValue() != null && $$("filterDoctor").getValue() != "")
            url = url + "doctor=" + $$("filterDoctor").getValue() + ";";
        return url;
    }

    $scope.saveRow = function () {
        var id = $$("table").getSelectedId();
        var data = $$("editform").getValues();
        data.author = $localStorage.currentUser.login;
        if (!id) {
            var url = "/api/"+version_api+"/schedule/";
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("doctor");
                        $$("editform").markInvalid("dateWork");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        $scope.serverValidation = false;
                        console.log("Fail. Add schedule - " + id)

                    }
                });
            $$("editform").bind($$("table"));
        } else {
            var url = "/api/"+version_api+"/schedule/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("doctor");
                        $$("editform").markInvalid("dateWork");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        $scope.serverValidation = false;
                        console.log("Fail. Update schedule - " + id)
                    }
                });
        }
    }

    $scope.deleteRow = function () {
        var id = $$("table").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление расписания",
            text: "Вы уверены, что хотите удалить расписание?",
            callback: function (result) {
                if (result) {
                    var url = "/api/"+version_api+"/schedule/" + id;
                    webix.ajax().headers($localStorage.headers.value)
                        .del(url, JSON.stringify($$('editform').getValues()), {
                            success: function (text, data, XmlHttpRequest) {
                                $$("table").remove(id);
                            },
                            error: function (text, data, XmlHttpRequest) {
                                $scope.checkAuth(XmlHttpRequest);
                                console.log("Fail. Delete schedule - " + id)
                            }
                        });
                }
            }
        });
    }

    $scope.addRow = function () {
        $$("table").clearSelection();
        $$("editform").clear();
        $$("editform").clearValidation();
        $$("editwin").show();
    }

    $scope.copyRow = function () {
        var id = $$("table").getSelectedId();
        if (id === null || id === "" || id === undefined) {
            webix.alert(" Не выбрано расписание для копирования ");
        } else {
            var url = "/api/"+version_api+"/schedule/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                        $$("table").select(JSON.parse(text).id);
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        $scope.serverValidation = false;
                        console.log("Fail. Add schedule - " + id)
                    }
                });
        }
    }

    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    }
}