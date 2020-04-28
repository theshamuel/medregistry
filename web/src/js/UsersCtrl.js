'use strict';
angular
    .module('app')
    .controller('UsersCtrl', UsersCtrl)

function UsersCtrl($http, $location, $localStorage, $scope, uiGridConstants, i18nService, $uibModal, dialogs, $route, $window) {
    var version_api = "v1";
    
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';

    $scope.colums = [{
            id: "createdDateLabel",
            header: 'Дата создания',
            width: 200
        },
        {
            id: "fullname",
            header: ['Фамилия, имя, отчество', {
                content: 'textFilter'
            }],
            width: 500,
            sort: 'string'
        },
        {
            id: "login",
            header: 'Логин',
            width: 250
        },
        {
            id: "isBlockLabel",
            header: 'Состояние',
            width: 250
        }
    ]
    webix.ready(function () {
        webix.ui({
            view: "window",
            id: "editwin",
            head: "Пользователь",
            modal: true,
            position: "center",
            width: 650,
            autoheight: false,
            body: {
                view: "form",
                id: "editform",
                complexData: true,
                elements: [{
                        view: "checkbox",
                        label: "Заблокирован",
                        name: "isBlock",
                        labelWidth: 130,
                        css: "chek-state",
                        value: 0,
                    },
                    {
                        view: "text",
                        label: "Фамилия, имя, отчество",
                        name: "fullname",
                        inputWidth: 610,
                        labelWidth: 200,
                        invalidMessage: "Поле обязательно к заполнению"
                    },
                    {
                        view: "text",
                        label: "Логин",
                        name: "login",
                        inputWidth: 610,
                        labelWidth: 200,
                        invalidMessage: "Поле обязательно к заполнению"
                    },
                    {
                        view: "text",
                        label: "Пароль",
                        name: "password",
                        type: 'password',
                        inputWidth: 610,
                        labelWidth: 200
                    },
                    {
                        view: "text",
                        label: "Подтверждение пароля",
                        name: "confirmPassword",
                        invalidMessage: "Подтвержденный пароль не совпадает с полем \"Пароль\"",
                        type: 'password',
                        inputWidth: 610,
                        labelWidth: 200
                    },
                    {
                        view: "template",
                        type: "section",
                        template: "<span class='lb_template'>Роли</span>"
                    },
                    {

                        view: "layout",
                        label: "Роли",
                        borderless: true,
                        css: "lay_role",
                        rows: [{
                                cols: [{
                                        view: "checkbox",
                                        name: "roles.admin",
                                        label: "Администратор",
                                        labelWidth: 191
                                    },
                                    {
                                        view: "checkbox",
                                        name: "roles.doctor",
                                        label: "Доктор",
                                        labelWidth: 191
                                    }
                                ]
                            },
                            {
                                cols: [{
                                        view: "checkbox",
                                        name: "roles.manager",
                                        label: "Менеджер",
                                        labelWidth: 191
                                    },
                                    {
                                        view: "checkbox",
                                        name: "roles.operator",
                                        label: "Оператор",
                                        labelWidth: 191
                                    }
                                ]
                            },
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
                    fullname: webix.rules.isNotEmpty,
                    login: webix.rules.isNotEmpty,
                    confirmPassword: function () {
                        if (this.getValues().password != this.getValues().confirmPassword) {
                            return false;
                        }
                        return true;
                    }
                }
            }
        });
        webix.ui({
            id: "table",
            container: "usersGrid",
            view: "datatable",
            autoheight: true,
            width: 1200,
            editable: true,
            editaction: "custom",
            scroll: false,
            datafetch: 15,
            datathrottle: 10,
            loadahead: 15,
            select: "row",
            data: webix.ajax().headers($localStorage.headers.value).get("/api/"+version_api+"/users?count=15&start=0"),
            datatype: "json",
            columns: $scope.colums,
            pager: {
                id:"pager",
                template: "{common.first()} {common.prev()} {common.pages()} {common.next()} {common.last()}",
                container: 'pager',
                size: 15,
                group: 5,
                page:0
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
                    var data = webix.ajax().headers($localStorage.headers.value).get("/api/"+version_api+"/users?count="+count+"&start="+start);
                    this.parse(data, "json");
                    console.log("start="+start+";count="+count+";data="+data);
                    return false;
                }
            }
        });
        
        $$("editform").bind($$("table"));
    });

    $scope.saveRow = function () {
        var id = $$("table").getSelectedId();
        var data = $$("editform").getValues();
        data.author = $localStorage.currentUser.login;
        data.password = md5(data.password);
        if (!id) {
            var url = "/api/"+version_api+"/users/";
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $$("editform").markInvalid("login");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        $scope.serverValidation = false;
                        $scope.checkAuth(XmlHttpRequest);
                    }
                })
        } else {
            var url = "/api/"+version_api+"/users/" + id;
            console.log("url:" + url);
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $$("editform").markInvalid("login");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        $scope.serverValidation = false;
                        $scope.checkAuth(XmlHttpRequest);
                    }
                });
        }
    }
    $scope.deleteRow = function () {
        var id = $$("table").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление пользователя",
            text: "Вы уверены, что хотите удалить пользователя?",
            callback: function (result) {
                if (result) {
                    var url = "/api/"+version_api+"/users/" + id;
                    webix.ajax().headers($localStorage.headers.value)
                        .del(url, JSON.stringify($$('editform').getValues()),{
                            success: function (text, data, XmlHttpRequest) {
                                $$("table").remove(id);
                            },
                            error: function (text, data, XmlHttpRequest) {
                                $scope.checkAuth(XmlHttpRequest);
                                console.log("Fail. Delete user - " + id)
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
}