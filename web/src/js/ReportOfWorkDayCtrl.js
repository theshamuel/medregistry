'use strict';
angular
    .module('app')
    .controller('ReportOfWorkDayCtrl', ReportOfWorkDayCtrl)

function ReportOfWorkDayCtrl($http, $location, $localStorage, $scope, $rootScope, uiGridConstants, i18nService, $window, $route) {
    var version_api = "v1";
    
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';

    webix.Date.startOnMonday = true;

    webix.ready(function () {
        webix.ui({
            view: "form",
            id: "editform",
            width: 600,
            container: "reportOfWorkDay",
            complexData: true,
            css: "company-form",
            elements: [{
                    view: "label",
                    label: "Выберите дату отчета:",
                    css: "report_title",
                },
                {
                    cols: [{
                            view: "layout",
                            rows: [{}]
                        },
                        {
                            view: "calendar",
                            id: "calendar",
                            date: new Date(),
                            css: "without_border",
                            events: webix.Date.isHoliday,
                            weekHeader: true,
                        },
                        {
                            view: "layout",
                            rows: [{}]
                        }
                    ]
                },
                {
                    cols: [{
                            view: "button",
                            type: "form",
                            value: "byDay",
                            label: "Отчет за смену",
                            click: function () {
                                $scope.getReportOfWorkDay();
                            }
                        },
                        {
                            view: "button",
                            type: "form",
                            value: "byDayDoctor",
                            label: "Отчет по докторам",
                            click: function () {
                                $scope.getReportOfWorkDayDoctor();
                            }
                        }
                    ]
                },
                {
                    cols: [{
                            view: "layout",
                            rows: [{}]
                        },
                        {
                            view: "button",
                            value: "Cancel",
                            label: "Отмена",
                            click: function () {
                                $window.location.href = '/main';
                            }
                        },
                        {
                            view: "layout",
                            rows: [{}]
                        }
                    ]
                }
            ],
        });

        $$('calendar').selectDate(new Date());

    });

    $scope.getReportOfWorkDay = function () {
        var dateReport = $$('calendar').getValue();
        if (dateReport != null && dateReport != undefined && dateReport != "") {
            var url = "/api/"+version_api+"/reports/file/reportOfWorkDay/"+dateReport.toJSON();
            webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
                $rootScope.saveByteArray([data], 'Отчет_за_день_'+dateReport.toJSON()+'.docx');
            });
        } else {
            webix.alert({
                title: "Внимание",
                ok: "ОК",
                type: "alert-warning",
                text: " Выберите дату для формирования отчета "
            });
        }
    }

    $scope.getReportOfWorkDayDoctor = function () {
        var dateReport = $$('calendar').getValue();
        if (dateReport != null && dateReport != undefined && dateReport != "") {

            var url = "/api/"+version_api+"/reports/file/reportOfWorkDayByDoctor/"+dateReport.toJSON();
            webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
                $rootScope.saveByteArray([data], 'Расчетный_лист_'+dateReport.toJSON()+'.docx');
            });
        } else {
            webix.alert({
                title: "Внимание",
                ok: "ОК",
                type: "alert-warning",
                text: " Выберите дату для формирования отчета "
            });
        }
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