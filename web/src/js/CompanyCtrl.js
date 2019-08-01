'use strict';
angular
    .module('app')
    .controller('CompanyCtrl', CompanyCtrl)

function CompanyCtrl($http, $location, $localStorage, $scope, uiGridConstants, i18nService, $window, $route) {
    var version_api = "v1";
    
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';

    webix.ready(function () {
        webix.ui({
            view: "form",
            id: "editform",
            width: 800,
            container: "companyForm",
            complexData: true,
            css: "company-form",
            elements: [{
                    view: "text",
                    label: "Наименование полное",
                    name: "fullName",
                    inputWidth: 755,
                    labelWidth: 265,
                    invalidMessage: "Обязательное поле"
                },
                {
                    view: "text",
                    label: "Наименование краткое",
                    name: "shortName",
                    inputWidth: 755,
                    labelWidth: 265
                },
                {
                    view: "text",
                    label: "Наименование альтернативное",
                    name: "extraName",
                    inputWidth: 755,
                    labelWidth: 265
                },
                {

                    rows: [{
                            view: "tabbar",
                            height: 35,
                            multiview: true,
                            options: [{
                                    id: "generalInfo",
                                    css: "common_tab",
                                    value: "Общие реквизиты"
                                },
                                {
                                    id: "juridicalInfo",
                                    css: "common_tab",
                                    value: "Юридические реквизиты"
                                },
                            ],
                        },
                        {
                            css: "bottom_border_tab_header",
                            cells: [{
                                    id: "generalInfo",
                                    view: "layout",
                                    margin: 8,
                                    padding: 10,
                                    rows: [{
                                            view: "text",
                                            label: "Фактический адрес",
                                            name: "addressFact",
                                            labelWidth: 208,
                                        },
                                        {
                                            view: "text",
                                            label: "Юридический адрес",
                                            name: "addressJur",
                                            labelWidth: 208
                                        },
                                        {
                                            view: "layout",
                                            label: "Инфо",
                                            borderless: true,
                                            rows: [{
                                                cols: [{
                                                        view: "text",
                                                        label: "Телефон",
                                                        name: "phone",
                                                        labelWidth: 208,
                                                    },
                                                    {
                                                        view: "text",
                                                        label: "Сайт",
                                                        name: "site",
                                                        css: "company_site",
                                                        labelWidth: 50,
                                                        inputWidth: 350
                                                    }
                                                ]
                                            }]
                                        },
                                        {
                                            view: "template",
                                            type: "section",
                                            template: "---"
                                        },
                                        {
                                            view: "text",
                                            label: "Руководитель",
                                            name: "director",
                                            labelWidth: 208,
                                        },
                                        {
                                            view: "text",
                                            label: "ФИО руководителя в р/п",
                                            name: "directorNameRp",
                                            labelWidth: 208
                                        },
                                        {
                                            view: "text",
                                            label: "ФИО руководителя в д/п",
                                            name: "directorNameDp",
                                            labelWidth: 208
                                        }
                                    ]
                                },
                                {
                                    id: "juridicalInfo",
                                    view: "layout",
                                    margin: 8,
                                    padding: 10,
                                    rows: [{
                                            view: "layout",
                                            label: "ЮрИнфо",
                                            borderless: true,
                                            rows: [{
                                                cols: [{
                                                        view: "text",
                                                        label: "ИНН",
                                                        name: "inn",
                                                        labelWidth: 55,
                                                        inputWidth: 212,
                                                    },
                                                    {
                                                        view: "text",
                                                        label: "КПП",
                                                        name: "kpp",
                                                        css: "company_kpp",
                                                        labelWidth: 55,
                                                        inputWidth: 212
                                                    },
                                                    {
                                                        view: "text",
                                                        label: "ОКПО",
                                                        name: "okpo",
                                                        css: "company_kpp",
                                                        labelWidth: 75,
                                                        inputWidth: 225
                                                    }
                                                ]
                                            }]
                                        },
                                        {
                                            view: "layout",
                                            label: "ОГРН",
                                            rows: [{
                                                cols: [{
                                                        view: "text",
                                                        label: "ОГРН",
                                                        name: "ogrn",
                                                        labelWidth: 55,
                                                        inputWidth: 212
                                                    },
                                                    {
                                                        view: "text",
                                                        label: "ОКАТО",
                                                        name: "okato",
                                                        labelWidth: 66,
                                                        inputWidth: 223
                                                    },
                                                    {
                                                        view: "layout",
                                                        rows: [{}]
                                                    }
                                                ]
                                            }]
                                        },
                                        {
                                            view: "text",
                                            label: "Банк",
                                            name: "bank",
                                            labelWidth: 140
                                        },
                                        {
                                            view: "layout",
                                            label: "Банк",
                                            borderless: true,
                                            rows: [{
                                                cols: [{
                                                        view: "text",
                                                        label: "Расчетный счет",
                                                        name: "checkingAccount",
                                                        labelWidth: 140
                                                    },
                                                    {
                                                        view: "text",
                                                        label: "Корр/с",
                                                        name: "corrAccount"
                                                    }
                                                ]
                                            }]
                                        },
                                        {
                                            view: "template",
                                            type: "section",
                                            template: "---"
                                        },
                                        {
                                            view: "text",
                                            label: "Лицензия",
                                            name: "license",
                                            labelWidth: 140,
                                            invalidMessage: "Обязательное поле"
                                        },
                                        {
                                            view: "text",
                                            label: "Дополнительно",
                                            name: "extraInfo",
                                            labelWidth: 140
                                        }
                                    ]
                                },
                            ]
                        }
                    ],
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
                                }
                            }
                        },
                        {
                            view: "button",
                            value: "Cancel",
                            label: "Отмена",
                            click: function () {
                                $window.location.href = '/main';
                            }
                        }
                    ]
                }
            ],
            rules: {
                fullName: webix.rules.isNotEmpty,
                license: webix.rules.isNotEmpty,
                inn: function (value) {
                    if (!webix.rules.isNotEmpty(value)) {
                        webix.alert({
                            title: "Ошибка",
                            ok: "ОК",
                            type: "alert-error",
                            text: "Заполните поле ИНН"
                        });
                        return false;
                    }
                    if (!webix.rules.isNumber(value)) {
                        webix.alert({
                            title: "Ошибка",
                            ok: "ОК",
                            type: "alert-error",
                            text: "Поле ИНН должно быть числовым"
                        });
                        return false;
                    }
                    return true;
                },
                kpp: function (value) {
                    if (!webix.rules.isNotEmpty(value)) {
                        webix.alert({
                            title: "Ошибка",
                            ok: "ОК",
                            type: "alert-error",
                            text: "Заполните поле КПП"
                        });
                        return false;
                    }
                    if (!webix.rules.isNumber(value)) {
                        webix.alert({
                            title: "Ошибка",
                            ok: "ОК",
                            type: "alert-error",
                            text: "Поле КПП должно быть числовым"
                        });
                        return false;
                    }
                    return true;
                }
            }
        });
        var data = webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/company").response;
        $scope.companyId = JSON.parse(data).id;
        $$("editform").parse(data);
    });

    $scope.saveRow = function () {
        var data = $$('editform').getValues();
        data.author = $localStorage.currentUser.login;
        var url = "/api/"+version_api+"/company/" + $scope.companyId;
        webix.ajax().headers($localStorage.headers.value).sync()
            .put(url, JSON.stringify(data), {
                success: function (text, data, XmlHttpRequest) {
                    webix.alert("Данные успешно сохранены");
                    $scope.serverValidation = true;
                },
                error: function (text, data, XmlHttpRequest) {
                    webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                    $scope.serverValidation = false;
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("Fail. Update company - " + $scope.companyId)
                }
            });
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