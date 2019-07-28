'use strict';
angular
    .module('app')
    .controller('ReportsCtrl', ReportsCtrl)

function ReportsCtrl($http, $location, $localStorage, $scope, uiGridConstants, i18nService, $uibModal, dialogs, $route, $window) {
    var version_api = "v1";
    
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';
    $scope.colums = [{
            id: "createdDateLabel",
            header: 'Дата создания',
            width: 150
        },
        {
            id: "label",
            header: ['Наименование отчета',
                {
                    content: 'textFilter'
                }
            ],
            width: 300,
            sort: 'string'
        },
        {
            id: "serviceLabel",
            header: ['Услуга',
                {
                    content: 'textFilter'
                }
            ],
            width: 500
        },
        {
            id: "template",
            header: "Шаблон отчета",
            width: 250
        }
    ]
    webix.ready(function () {
        webix.ui({
            view: "window",
            id: "editwin",
            head: "Отчет",
            modal: true,
            position: "center",
            width: 650,
            autoheight: false,
            body: {
                view: "form",
                id: "editform",
                complexData: true,
                elements: [{
                        view: "text",
                        label: "Наименование отчета",
                        name: "label",
                        labelWidth: 200,
                        invalidMessage: "Поле обязательно к заполнению"
                    },
                    {
                        view: "combo",
                        label: "Наименование услуги",
                        name: "serviceId",
                        labelWidth: 200,
                        options: {
                            filter: function (item, value) {
                                return item.label.toString().toLowerCase().indexOf(value.toLowerCase()) > -1;
                            },
                            body: {
                                data: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/services").response),
                            }
                        }
                    },
                    {
                        view: "text",
                        label: "Шаблон отчета",
                        name: "template",
                        labelWidth: 200,
                        invalidMessage: ""
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
                                    if (item!=null && item != undefined && item != ""){
                                        $$("table").unselectAll();
                                        $$("table").select(item);
                                    }
                                }
                            }
                        ]
                    }
                ],
                rules: {
                    label: webix.rules.isNotEmpty,
                    template: webix.rules.isNotEmpty,
                }
            }
        });
        webix.ui({
            id: "table",
            container: "reportsGrid",
            view: "datatable",
            autoheight: true,
            width: 1200,
            editable: true,
            editaction: "custom",
            scroll: false,
            select: "row",
            data: webix.ajax().headers($localStorage.headers.value).get("/api/"+version_api+"/reports"),
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
                }
            }
        });
        $$("editform").bind($$("table"));
    });

    $scope.saveRow = function () {
        var id = $$("table").getSelectedId();
        var data = $$("editform").getValues();
        data.author = $localStorage.currentUser.login;
        if (!id) {
            var url = "/api/"+version_api+"/reports/";
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $$("editform").markInvalid("serviceId");
                        $$("editform").markInvalid("template");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        $scope.serverValidation = false;
                        $scope.checkAuth(XmlHttpRequest);
                        console.log("Fail. Add otchet - " + id)
                    }
                });
            $$("editform").bind($$("table"));
        } else {
            var url = "/api/"+version_api+"/reports/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $$("editform").markInvalid("serviceId");
                        $$("editform").markInvalid("template");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        $scope.serverValidation = false;
                        $scope.checkAuth(XmlHttpRequest);
                        console.log("Fail. Update otchet - " + id)
                    }
                });
        }
    }
    $scope.deleteRow = function () {
        var id = $$("table").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление отчета",
            text: "Вы уверены, что хотите удалить отчет?",
            callback: function (result) {
                if (result) {
                    var url = "/api/"+version_api+"/reports/" + id;
                    webix.ajax().headers($localStorage.headers.value)
                        .del(url, JSON.stringify($$('editform').getValues()), {
                            success: function (text, data, XmlHttpRequest) {
                                $$("table").remove(id);
                            },
                            error: function (text, data, XmlHttpRequest) {
                                $scope.checkAuth(XmlHttpRequest);
                                console.log("Fail. Delete otchet - " + id)
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

    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    }

    $scope.logout = function () {
        $localStorage.currentUser = null;
        $scope.userLogin = '';
        $scope.userPassword = '';
        $http.defaults.headers.common.Authorization = '';
        // $location.path('/');
        $window.location.href = '/login';
    };
}