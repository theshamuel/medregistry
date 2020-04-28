'use strict';
angular
    .module('app')
    .controller('AppointmentsCtrl', AppointmentsCtrl)

function AppointmentsCtrl($http, $location, $localStorage, $scope, uiGridConstants, i18nService, $uibModal, dialogs, $route, $window) {
    var version_api = "v1";
    
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';

    webix.Date.startOnMonday = true;

    $scope.colums = [{
            id: "dateTimeEventLabel",
            header: 'Дата и время приема',
            width: 250,
            sort: 'string'
        },
        {
            id: "doctorLabel",
            header: 'ФИО доктора',
            width: 250
        },
        {
            id: "client",
            header: 'ФИО пациента',
            width: 250
        },
        {
            id: "phone",
            header: "Телефон",
            width: 150
        },
        {
            id: "service",
            header: "Предварительная услуга",
            width: 325
        }
    ]
    webix.ready(function () {
        webix.proxy.addHeaders = {
            $proxy:true,
            load:function(view, callback, params){
                webix.ajax().bind(view).headers($localStorage.headers.value).get(this.source, params, callback); 
            }
        };
        var getFreeAppointmets = function () {
            var id = $$("table").getSelectedId();
            if ($$("dateEvent").getValue() != null && $$("dateEvent") != undefined &&
                $$("cmdDoctor").getValue() != null && $$("cmdDoctor") != undefined && $$("cmdDoctor").getValue() != "") {
                var url = "/api/"+version_api+"/appointments/freetime?doctorId=" + $$("cmdDoctor").getValue() + "&dateEvent=" + $$("dateEvent").getValue().toJSON() + "&id";
                if (id != undefined)
                    url = url + "=" + id;
                $$("freeTimeEvent").define("options", JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                    success: function (text, data) {},
                    error: function (text, data) {
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                    }
                }).response));
                $$("freeTimeEvent").refresh();
            } else {
                $$("freeTimeEvent").define("options", []);
            }
        }

        // filter
        webix.ui({
            view: "layout",
            label: "filter",
            container: "filter",
            id: "filter",
            borderless: true,
            width: 1200,
            rows: [{
                cols: [{
                        cols: [{
                            view: "datepicker",
                            editable: true,
                            // label: "Дата приема",
                            id: "filterDateEvent",
                            inputWidth: 245,
                        }]
                    },
                    {
                        cols: [{
                            view: "text",
                            // label: "ФИО доктора",
                            id: "filterDoctor",
                            inputWidth: 245,
                        }]
                    },
                    {
                        cols: [{
                            view: "text",
                            // label: "Пациент",
                            id: "filterClient",
                            inputWidth: 245,
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
                                    var url = $scope.getTableURI(15,0);
                                    $$('table').load("addHeaders->"+url);
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
                                    $$('filterDateEvent').setValue(null)
                                    $$('filterDoctor').setValue("");
                                    $$('filterClient').setValue("");
                                    $$('table').clearAll();
                                    var url = $scope.getTableURI(15,0);
                                    $$('table').load("addHeaders->"+url);
                                    $$('table').refresh();
                                    $$('table').setPage(0);
                                }
                            },
                        ]
                    },
                    {
                        gravity: 1.5,
                        view: "layout",
                        rows: [{}]
                    }
                ]
            }]
        });


        webix.ui({
            view: "window",
            id: "editwin",
            head: "Запись на прием",
            modal: true,
            position: "center",
            width: 650,
            autoheight: false,
            body: {
                view: "form",
                id: "editform",
                complexData: true,
                elements: [{
                        cols: [{
                                view: "layout",
                                rows: [{}]
                            },
                            {
                                gravity: 0.37,
                                view: "checkbox",
                                label: "Двойная запись",
                                name: "isDoubleAppointment",
                                labelWidth: 147,
                                value: 0
                            }
                        ]
                    },
                    {

                        view: "combo",
                        label: "ФИО доктора",
                        id: "cmdDoctor",
                        name: "doctorId",
                        labelWidth: 130,
                        on: {
                            onChange: getFreeAppointmets
                        },
                        options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/doctors").response),
                        invalidMessage: ""
                    },

                    {
                        cols: [{
                                view: "datepicker",
                                editable: true,
                                label: "Дата приема",
                                id: "dateEvent",
                                name: "dateEvent",
                                labelWidth: 130,
                                invalidMessage: "",
                                on: {
                                    onChange: getFreeAppointmets
                                }
                            },
                            {
                                id: "freeTimeEvent",
                                view: "combo",
                                label: "Время приема",
                                name: "timeEvent",
                                stringResult: true,
                                labelWidth: 130,
                                inputWidth: 297,
                                css: "margin_appoint_col",
                                invalidMessage: ""
                            }

                        ]
                    },
                    {
                        cols: [{
                                view: "text",
                                label: "ФИО пациента",
                                name: "client",
                                labelWidth: 130,
                            },
                            {
                                view: "text",
                                label: "Телефон",
                                name: "phone",
                                labelWidth: 130,
                                inputWidth: 297,
                                css: "margin_appoint_col"
                            }
                        ]
                    },
                    {
                        view: "text",
                        label: "Предварительная услуга",
                        name: "service",
                        labelWidth: 210
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
                                    $$("editform").clear();
                                    $$("editform").clearValidation();
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
                    dateEvent: webix.rules.isNotEmpty,
                    timeEvent: webix.rules.isNotEmpty,
                    doctorId: webix.rules.isNotEmpty
                }
            }
        });
        webix.ui({
            id: "table",
            container: "appointmentsGrid",
            view: "datatable",
            autoheight: true,
            width: 1200,
            editable: false,
            scroll: false,
            editaction: "custom",
            multiselect: false,
            select: "row",
            datafetch: 15,
            datathrottle: 300,
            loadahead: 15,
            data: webix.ajax().headers($localStorage.headers.value).get("/api/"+version_api+"/appointments?count=15&start=0"),
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
                    var url = $scope.getTableURI(count,start);
                    var dataGrid = webix.ajax().headers($localStorage.headers.value).get(url);
                    this.parse(dataGrid);
                    return false;
                }
            }
        });
        $$("editform").bind($$("table"));
    });
    $scope.getTableURI = function(count,start){
        var url = "/api/"+version_api+"/appointments?count=" + count + "&start=" + start+"&filter=";
        if ($$("filterDateEvent").getValue()!=null && $$("filterDateEvent").getValue().toJSON()!="")
            url = url + "dateEvent="+$$("filterDateEvent").getValue().toJSON()+";";
        if ($$("filterDoctor").getValue()!=null && $$("filterDoctor").getValue()!="")
            url = url + "doctor="+$$("filterDoctor").getValue()+";";
        if ($$("filterClient").getValue()!=null && $$("filterClient").getValue()!="")
            url = url + "client="+$$("filterClient").getValue()+";";
        return url;
    }
    $scope.saveRow = function () {
        var id = $$("table").getSelectedId();
        var data = $$("editform").getValues();
        data.author = $localStorage.currentUser.login;
        if (!id || $scope.isCopy) {
            var url = "/api/"+version_api+"/appointments/";
            if ($scope.isCopy)
                data.id = null;
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("dateEvent");
                        $$("editform").markInvalid("timeEvent");
                        $$("editform").markInvalid("doctorId");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Add appointment - " + id)
                    }
                });
            if ($scope.isCopy)
                $$("table").unselectAll();
            $scope.isCopy = false;
            $$("editform").bind($$("table"));
        } else {
            var url = "/api/"+version_api+"/appointments/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("dateEvent");
                        $$("editform").markInvalid("timeEvent");
                        $$("editform").markInvalid("doctorId");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Update appointment - " + id)
                    }
                });
        }
        $$("editform").clear();
        $$("editform").clearValidation();

    }

    $scope.deleteRow = function () {
        var id = $$("table").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление записи",
            text: "Вы уверены, что хотите удалить запись?",
            callback: function (result) {
                if (result) {
                    var url = "/api/"+version_api+"/appointments/" + id;
                    webix.ajax().headers($localStorage.headers.value)
                        .del(url, JSON.stringify($$('editform').getValues()), {
                            success: function (text, data, XmlHttpRequest) {
                                $$("table").remove(id);
                            },
                            error: function (text, data, XmlHttpRequest) {
                                $scope.checkAuth(XmlHttpRequest);
                                console.log("Fail. Delete appointment - " + id)
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
        var item = $$("table").getSelectedItem();
        if (item === null || item === "" || item === undefined) {
            webix.alert(" Не выбрана запись на прием для копирования ");
        } else {
            $scope.isCopy = true;
            $$("editwin").show();
            $$("freeTimeEvent").setValue(null);
        }
    }


    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    }
}