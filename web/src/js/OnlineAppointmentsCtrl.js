'use strict';
angular
    .module('app')
    .controller('OnlineAppointmentsCtrl', OnlineAppointmentsCtrl)

function OnlineAppointmentsCtrl($http, $location, $localStorage, $scope, uiGridConstants, i18nService, $window, $route) {
    var version_api = "v1";
    
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';

    webix.ready(function () {
        webix.ui({
            view: "form",
            id: "editform",
            width: 963,
            container: "online",
            complexData: true,
            css: "company-form",
            elements: [{
                    view: "label",
                    label: "Запись на прием:",
                    css: "online_title",
                },
                {
                    cols: [{
                            // gravity: 0.75,
                            view: "text",
                            label: "Ваше имя",
                            // inputWidth: 450,
                            labelWidth: 195
                        },
                        {
                            gravity: 0.50,
                            view: "text",
                            label: "Телефон",
                            css: "margin_online_col",
                            labelWidth: 85,
                            inputWidth: 300
                        }
                    ]
                },
                {
                    view: "combo",
                    label: "Выберите специалиста",
                    id: "cmbDoctorId",
                    labelWidth: 195,
                    inputWidth: 620,
                    options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/doctors").response)
                },
                {
                    cols: [{
                            cols: [{
                                    view: "datepicker",
                                    editable: true,
                                    label: "Дата приема",
                                    id: "dateEvent",
                                    name: "dateEvent",
                                    labelWidth: 195
                                },
                                {
                                    gravity: 0.7,
                                    id: "freeTimeEvent",
                                    view: "combo",
                                    label: "Время приема",
                                    name: "timeEvent",
                                    stringResult: true,
                                    labelWidth: 125,
                                    inputWidth: 245,
                                    css: "margin_appoint_col",
                                }
                            ]
                        },
                        {
                            gravity: 0.50,
                            view: "layout",
                            rows: [{}]
                        }
                    ]
                },
                {
                    view: "text",
                    label: "Комментарий",
                    name: "comment",
                    // inputWidth: 755,
                    labelWidth: 195
                },
                {
                    view: "label",
                    label: "* Отмена записи на прием по телефонам +7 (49232) 9-39-08, +7 (920) 903-17-95 *",
                    // css: "online_title",
                },
                {
                    cols: [
                        {
                            view: "layout",
                            rows: [{}]
                        },
                        {
                            view: "button",
                            type: "danger",
                            value: "Danger",
                            label: "Записаться",
                            // click: function () {
                            //     if ($$("editform").validate()) {
                            //         $scope.saveRow();
                            //     }
                            // }
                        },
                        {
                            view: "layout",
                            rows: [{}]
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
        var data = webix.ajax().headers($localStorage.headers.value).sync().get("/api/company").response;
        $scope.companyId = JSON.parse(data).id;
        $$("editform").parse(data);
    });

    $scope.saveRow = function () {
        var data = $$('editform').getValues();
        data.author = $localStorage.currentUser.login;
        var url = "/api/company/" + $scope.companyId;
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
}