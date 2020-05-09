'use strict';
angular
    .module('app')
    .controller('DoctorCtrl', DoctorCtrl)

function DoctorCtrl($http, $location, $localStorage, $scope, uiGridConstants, i18nService, $uibModal, dialogs, $route) {
    let version_api = "v1";
    
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';
    $scope.colums = [{
            id: "createdDateLabel",
            header: 'Дата создания',
            width: 125
        },
        {
            id: "surname",
            header: ['Фамилия',
                {
                    content: 'textFilter'
                }
            ],
            width: 200,
            sort: 'string'
        },
        {
            id: "name",
            header: 'Имя',
            width: 150
        },
        {
            id: "middlename",
            header: 'Отчество',
            width: 150
        },
        {
            id: "phone",
            header: 'Телефон',
            width: 150
        },
        {
            id: "positionLabel",
            header: 'Должность',
            width: 225
        },
        {
            id: "personalRateLabel",
            header: 'Ставка',
            width: 75
        },
        {
            id: "isNotWorkLabel",
            header: 'Состояние',
            width: 125
        }
    ]

    $scope.getInfoPersonalRate = function () {
        let InfoPersonalRate = [];
        let url = "";
        let id = $scope.doctorId;
        if (id != null && id != "") {
            url = "/api/"+version_api+"/services/" + id + "/doctor";
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    InfoPersonalRate = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }
            });
        }
        $$("gridInfoPersonalRate").clearAll();
        $$("gridInfoPersonalRate").parse(InfoPersonalRate);
    };

    webix.ready(function () {

        webix.ui({
            view: "window",
            id: "editwin",
            head: "Доктор",
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
                                view: "checkbox",
                                label: "Подрядчик",
                                name: "contractor",
                                labelWidth: 100,
                                value: 0
                            },
                            {
                                view: "checkbox",
                                label: "Исключить из отчета",
                                name: "excludeFromReport",
                                labelWidth: 170,
                                css: "chek_doc_state",
                                value: 0
                            },
                            {
                                view: "checkbox",
                                label: "Увольнение",
                                name: "isNotWork",
                                labelWidth: 110,
                                value: 0,
                            }
                        ]
                    },
                    {
                        view: "text",
                        label: "Фамилия",
                        name: "surname",
                        labelWidth: 100,
                        invalidMessage: "Обязательное поле"
                    },
                    {
                        cols: [{
                                view: "text",
                                label: "Имя",
                                name: "name",
                                inputWidth: 300,
                                labelWidth: 100,
                            },
                            {
                                view: "text",
                                label: "Отчество",
                                name: "middlename",
                                labelWidth: 100,
                                inputWidth: 300,
                                css: "margin_doc_col"
                            }
                        ]
                    },
                    {
                        view: "text",
                        label: "Телефон",
                        name: "phone",
                        labelWidth: 100,
                        inputWidth: 300,
                    },
                    {
                        view: "combo",
                        label: "Должность",
                        name: "position",
                        labelWidth: 100,
                        options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/doctors/positions").response)
                    },
                    {
                        id: "InfoPersonalRate",
                        view: "layout",
                        css: "layout_without_border",
                        margin: 8,
                        padding: 10,
                        rows: [{
                            view: "datatable",
                            css: "table_history_visits",
                            id: "gridInfoPersonalRate",
                            height: 250,
                            // header: false,
                            // select: true,
                            scroll: "y",
                            columns: [{
                                id: "label",
                                header: "Услуги с персональной ставкой",
                                fillspace: true
                            }, ],
                            on: {
                                onBeforeLoad: function () {
                                    this.showOverlay("Загрузка...");
                                },
                                onAfterLoad: function () {
                                    if (!this.count())
                                        this.showOverlay("Персональные ставки не назначены");
                                    else
                                        this.hideOverlay();
                                }
                            },
                            data: []
                        }]
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
                                    let item = $$("table").getSelectedId();
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
                    surname: webix.rules.isNotEmpty,
                }
            }
        });

        webix.ui({
            id: "table",
            container: "doctorGrid",
            view: "datatable",
            autoheight: true,
            width: 1200,
            editable: true,
            scroll: false,
            editaction: "custom",
            select: "row",
            data: webix.ajax().headers($localStorage.headers.value).get("/api/"+version_api+"/doctors"),
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
                    $scope.doctorId = $$("table").getSelectedId();
                    $scope.getInfoPersonalRate();
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
                }
            }
        });
        $$("editform").bind($$("table"));
    });

    $scope.saveRow = function () {
        let id = $$("table").getSelectedId();
        let data = $$("editform").getValues();
        data.author = $localStorage.currentUser.login;
        if (!id) {
            let url = "/api/"+version_api+"/doctors/";
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        console.log("TEXT=" + text)
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $$("editform").markInvalid("surname");
                        $$("editform").markInvalid("name");
                        $$("editform").markInvalid("middlename");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        $scope.checkAuth(XmlHttpRequest);
                        console.log("Fail. Add doctor - " + id)
                    }
                });
            $$("editform").bind($$("table"));
        } else {
            let url = "/api/"+version_api+"/doctors/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("surname");
                        $$("editform").markInvalid("name");
                        $$("editform").markInvalid("middlename");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Update doctor - " + id)
                    }
                });
        }
    }

    $scope.deleteRow = function () {
        let id = $$("table").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление услуги",
            text: "Вы уверены, что хотите удалить доктора?",
            callback: function (result) {
                if (result) {
                    let url = "/api/"+version_api+"/doctors/" + id;
                    webix.ajax().headers($localStorage.headers.value)
                        .del(url, JSON.stringify($$('editform').getValues()), {
                            success: function (text, data, XmlHttpRequest) {
                                $$("table").remove(id);
                            },
                            error: function (text, data, XmlHttpRequest) {
                                $scope.checkAuth(XmlHttpRequest);
                                console.log("Fail. Delete doctor - " + id)
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
        $scope.doctorId = null;
        $$("gridInfoPersonalRate").clearAll();
        $$("editwin").show();
    }

    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    }
}