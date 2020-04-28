'use strict';
angular
    .module('app')
    .controller('ReportOfPeriodCtrl', ReportOfPeriodCtrl)

function ReportOfPeriodCtrl($http, $location, $localStorage, $scope, $rootScope, uiGridConstants, i18nService, $window, $route) {
    let version_api = "v2";

    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';

    webix.Date.startOnMonday = true;

    webix.ready(function () {
        webix.ui({
            view: "form",
            id: "editform",
            width: 550,
            container: "reportOfPeriod",
            complexData: true,
            css: "company-form",
            elements: [{
                cols: [{
                    view: "label",
                    label: "Начальная дата:",
                    css: "report_title",
                }, {
                    view: "label",
                    label: "Конечная дата:",
                    css: "report_title",
                }]
            },
                {
                    cols: [
                        {
                            view: "calendar",
                            id: "startDate",
                            date: new Date(),
                            css: "without_border",
                            events: webix.Date.isHoliday,
                            weekHeader: true,
                        },
                        {
                            view: "calendar",
                            id: "endDate",
                            date: new Date(),
                            css: "without_border",
                            events: webix.Date.isHoliday,
                            weekHeader: true,
                        }
                    ]
                },
                {
                    cols: [{
                        view: "combo",
                        name: "doctorId",
                        id: "cmbDoctor",
                        labelWidth: 145,
                        options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/v1/doctors").response),
                        invalidMessage: ""
                    },
                        {
                            view: "button",
                            type: "form",
                            value: "byDayDoctor",
                            label: "Отчет по докторам за период",
                            click: function () {
                                $scope.getReportOfPeriod();
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

        $$('startDate').selectDate(new Date());
        $$('endDate').selectDate(new Date());
    });

    $scope.getReportOfPeriod = function () {
        let startDate = $$('startDate').getValue();
        let endDate = $$('endDate').getValue();
        let doctorId = $$('cmbDoctor').getValue()
        console.log(doctorId)
        if (doctorId == null || doctorId == "") {
            webix.alert({
                title: "Внимание",
                ok: "ОК",
                type: "alert-warning",
                text: " Выберите доктора формирования отчета "
            });
            return
        }
        if (startDate != null && startDate != undefined && startDate != "" &&
            endDate != null && endDate != undefined && endDate != "") {
            if (startDate > endDate) {
                webix.alert({
                    title: "Внимание",
                    ok: "ОК",
                    type: "alert-warning",
                    text: " Начальная дата должна быть меньше или равна конечной "
                });
            } else {
                let url = "/api/" + version_api + "/report/file/reportPeriodByDoctor/" + doctorId + "/" +
                    startDate.toJSON() + "/" + endDate.toJSON() + "/report.xlsx";
                webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
                    $rootScope.saveByteArray([data], 'Расчетный_лист_' + startDate.toJSON() + '_' + endDate.toJSON() + '.xlsx');
                });
            }
        } else {
            webix.alert({
                title: "Внимание",
                ok: "ОК",
                type: "alert-warning",
                text: " Выберите начальную и конечную даты формирования отчета "
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