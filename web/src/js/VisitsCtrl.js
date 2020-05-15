'use strict';
angular
    .module('app')
    .controller('VisitsCtrl', VisitsCtrl)

function VisitsCtrl($http, $location, $localStorage, $scope, $rootScope, uiGridConstants, i18nService, $uibModal, dialogs, $route) {
    let version_api = "v1";

    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';
    $scope.visitId = "";

    $scope.colums = [{
        id: "dateTimeLabel",
        header: 'Дата и время приема',
        width: 225
    },
        {
            id: "doctorLabel",
            header: 'ФИО доктора',
            width: 300
        },
        {
            id: "clientLabel",
            header: 'ФИО пациента',
            width: 300
        },
        {
            id: "passportLabel",
            header: "Серия/номер паспорта",
            width: 200
        },
        {
            id: "phoneLabel",
            header: "Телефон",
            width: 175
        },
    ];


    let formatDate = webix.Date.dateToStr("%d.%m.%Y");

    // +
    $scope.reloadComboClients = function () {
        console.time("call reloadComboClients")
        let dataClient = [];
        let url = "/api/" + version_api + "/clients";
        webix.ajax().headers($localStorage.headers.value).sync().get(url, {
            success: function (text, data, XmlHttpRequest) {
                dataClient = JSON.parse(text);
            },
            error: function (text, data, XmlHttpRequest) {
                $scope.checkAuth(XmlHttpRequest);
            }
        });
        $$('cmbClient').define("options", dataClient);
        $$('cmbClient').refresh();
        console.timeEnd("call reloadComboClients")
    };
    // +
    $scope.getAllInfoClientById = function () {
        console.time("call getAllInfoClientById")
        let dataClient = [];
        if ($$("cmbClient").getValue() != null && $$("cmbClient").getValue() != undefined && $$("cmbClient").getValue() != "") {
            let url = "/api/" + version_api + "/clients/" + $$("cmbClient").getValue();
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataClient = JSON.parse(text);
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                }
            });

        }
        console.timeEnd("call getAllInfoClientById")
        return dataClient;
    };
    // +
    $scope.getReservedAppoinmentsHasVisit = function () {
        console.time("call getReservedAppoinmentsHasVisit")
        let dataAppointments = [];
        if ($$("cmbDoctorOnForm").getValue() != null && $$("cmbDoctorOnForm").getValue() != undefined && $$("cmbDoctorOnForm").getValue() != "") {
            let id = $scope.visitId;
            let url = "/api/" + version_api + "/appointments/reserved/hasvisit?doctorId=" + $$("cmbDoctorOnForm").getValue() + "&dateEvent=" + "&hasVisit=false&visitId";
            if (id != undefined)
                url = url + "=" + id;
            console.log("getReservedAppoinmentsHasVisit url=", url)
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataAppointments = JSON.parse(text);
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                }
            });
            $$('cmbAppointments').define("options", dataAppointments);
            $$('cmbAppointments').refresh();
        }
        console.timeEnd("call getReservedAppoinmentsHasVisit")
    };
    // +
    $scope.getInfoClient = function () {
        console.time("call getInfoClient")
        let start = new Date();
        let passportLabelClient = "";
        let birthdayClient = "";
        let phoneClient = "";
        let addressClient = "";
        if ($$("cmbClient").getValue() != null && $$("cmbClient").getValue() != undefined && $$("cmbClient").getValue() != "") {
            let url = "/api/" + version_api + "/clients/" + $$("cmbClient").getValue();
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    passportLabelClient = JSON.parse(text).passportLabel;
                    birthdayClient = formatDate(JSON.parse(text).birthday);
                    phoneClient = JSON.parse(text).phone;
                    addressClient = JSON.parse(text).address;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                }
            });
        }
        $$("passportLabel").setValue(passportLabelClient);
        $$("birthday").setValue(birthdayClient);
        $$("phone").setValue(phoneClient);
        $$("address").setValue(addressClient);
        let elapsed = (new Date() - start) / 1000;
        console.timeEnd("call getInfoClient")
    };
    // +
    $scope.getHistoryVisits = function () {
        console.time("call getHistoryVisits")
        let dataOfHistiryVisits = [];
        let dataOfHistiryUltra = [];
        let dataOfHistiryAnalyzes = [];
        let url = "";
        let id = $scope.clientId;
        if (id != null && id != "") {
            url = "/api/" + version_api + "/clients/" + id + "/consult";
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfHistiryVisits = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }
            });
            url = "/api/" + version_api + "/clients/" + id + "/ultra";
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfHistiryUltra = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }
            });
            url = "/api/" + version_api + "/clients/" + id + "/analyzes";
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfHistiryAnalyzes = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }
            });
        }
        $$("gridHistoryVisits").clearAll();
        $$("gridHistoryVisits").parse(dataOfHistiryVisits);
        $$("gridHistoryUltra").clearAll();
        $$("gridHistoryUltra").parse(dataOfHistiryUltra);
        $$("gridHistoryAnalyzes").clearAll();
        $$("gridHistoryAnalyzes").parse(dataOfHistiryAnalyzes);
        console.timeEnd("call getHistoryVisits")
    };
    // +
    $scope.getServicesOfVisit = function () {
        console.time("call getServicesOfVisit")
        let dataOfServices = [];
        let url = "";
        let id = $scope.visitId;
        let error = false;
        if (id != null && id != "" && id != undefined) {
            url = "/api/" + version_api + "/visits/" + id + "/services";
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfServices = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    error = true;
                    console.log("FAIL=" + text)
                }
            });
        }
        if (!error) {
            $$("gridServices").clearAll();
            $$("gridServices").parse(dataOfServices);
        } else {
            error = false;
        }
        console.timeEnd("call getServicesOfVisit")
    };
    // +
    $scope.getReportsOfVisit = function () {
        console.time("call getReportsOfVisit")
        let dataOfReports = [];
        let url = "";
        let id = $scope.visitId;
        if (id != null && id != "") {
            url = "/api/" + version_api + "/reports/" + id;
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfReports = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }
            });
        }
        $$("gridReports").clearAll();
        $$("gridReports").parse(dataOfReports);
        console.timeEnd("call getReportsOfVisit")
    };

    // +
    $scope.printCardClient = function () {
        console.time("call printCardClient")
        let dateReport = new Date();
        let clientId = $$("cmbClient").getValue();
        let doctorId = $$("cmbDoctorOnForm").getValue();
        let url = "/api/" + version_api + "/reports/file/clientCard/" +
            clientId + "/" + doctorId + "/" + dateReport.toJSON();
        webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
            $rootScope.saveByteArray([data], 'Карта_пациента-' + dateReport.toJSON() + '.xls');
        });
        console.timeEnd("call printCardClient")
    }
    $scope.reloadGridVisit = function () {
        console.time("call reloadGridVisit")
        $$('visitsGrid').clearAll();
        let url = $scope.getTableURI(15, 0);
        $$('visitsGrid').load("addHeaders->" + url);
        $$('visitsGrid').refresh();
        console.timeEnd("call reloadGridVisit")
    };

    $scope.addServicesOfVisit = function () {
        console.time("call addServicesOfVisit")
        let url = "";
        let id = $scope.visitId;
        let serverValid = true;
        if ((id === null || id === "" || id === undefined) && $$("editform").validate()) {
            $scope.saveRow();
            id = $scope.visitId;
            serverValid = $scope.serverValidation;
        }
        if ($$("editform").validate() && serverValid && id != null && id != "" && id != undefined &&
            $$("cmbService").getValue() != null && $$("cmbService").getValue() != undefined &&
            $$("cmbService").getValue() != "") {
            url = "/api/" + version_api + "/visits/" + id + "/services/" + $$("cmbService").getValue() + "?discount=";
            if ($$("discount").getValue() != "" && $$("discount").getValue() != undefined &&
                $$("discount").getValue() != null)
                url = url + $$("discount").getValue();
            webix.ajax().headers($localStorage.headers.value).sync().post(url, {
                success: function (text, data, XmlHttpRequest) {
                    $scope.getServicesOfVisit();
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }

            });
        }
        console.timeEnd("call addServicesOfVisit")
    };

    $scope.saveReportAsFile = function () {
        console.time("call saveReportAsFile")
        let reportId = $$("gridReports").getSelectedId();
        let reportItem = $$("gridReports").getSelectedItem();
        let clientId = $$("cmbClient").getValue();
        let visitId = $scope.visitId;
        let doctorId = $$("cmbDoctorOnForm").getValue();
        let dateReport = new Date();
        let url_ = ""
        if (reportId === null || reportId === "" || reportId === undefined) {
            webix.alert(" Не выбран отчет для сохранения ");
        } else {
            url_ = "/api/" + version_api + "/reports/file/reportTemplate/" +
                clientId + "/" + doctorId + "/" + reportId + "/" + visitId + "/" + dateReport.toJSON();
            if (reportItem != null && reportItem.template == "templateVisitResult") {
                version_api = 'v2'
                url_ = "/api/" + version_api + "/reports/file/reportVisitResult/" + visitId + "/report.xlsx"
            } else {
                version_api = 'v1'
            }
            webix.ajax().response("blob").headers($localStorage.headers.value).get(url_, function (text, data) {
                let fioClientArray = $$("cmbClient").getText().split(" ")
                let suffixClient = clientId
                if (fioClientArray.length > 0) {
                    suffixClient = fioClientArray[0]
                }
                console.log("")
                if (reportItem != null && reportItem.template === "templateVisitResult") {
                    $rootScope.saveByteArray([data], 'Заключение_' + dateReport.toJSON() + '_' +
                        suffixClient + '.xlsx')
                } else if (reportItem != null && reportItem.template === "Contract")
                    $rootScope.saveByteArray([data], 'Договор_' + dateReport.toJSON() + '.xls');
                else
                    $rootScope.saveByteArray([data], 'Бланк_' + reportItem.label + "-" +
                        dateReport.toJSON() + '.doc');
            });
        }
        version_api = 'v1'
        console.timeEnd("call saveReportAsFile")
    };

    // +
    $scope.deleteServicesOfVisit = function () {
        console.time("call deleteServicesOfVisit")
        let dataOfServices = [];
        let url = "";
        let id = $scope.visitId;
        let servicesId = $$("gridServices").getSelectedId();
        if (servicesId === null || servicesId === "" || servicesId === undefined) {
            webix.alert(" Не выбрана услуга для удаления ");
        } else if (id != null && id != "" && id != undefined) {
            url = "/api/" + version_api + "/visits/" + id + "/services/" + servicesId;
            webix.ajax().headers($localStorage.headers.value).sync().del(url, {
                success: function (text, data, XmlHttpRequest) {
                    $scope.getServicesOfVisit();
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }
            });
        }
        console.timeEnd("call deleteServicesOfVisit")
    };


    webix.ready(function () {
        webix.proxy.addHeaders = {
            $proxy: true,
            load: function (view, callback, params) {
                webix.ajax().bind(view).headers($localStorage.headers.value).get(this.source, params, callback);
            }
        };
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
                    gravity: 0.92,
                    cols: [{
                        view: "datepicker",
                        editable: true,
                        id: "filterDateEvent",
                        inputWidth: 220,
                    }]
                },
                    {
                        gravity: 1.21,
                        cols: [{
                            view: "text",
                            editable: true,
                            id: "filterDoctor",
                            inputWidth: 290,
                        }]
                    },
                    {
                        gravity: 1.21,
                        cols: [{
                            view: "text",
                            editable: true,
                            id: "filterClient",
                            inputWidth: 290,
                        }]
                    },
                    {
                        gravity: 0.725,
                        cols: [{
                            view: "text",
                            id: "filterPassport",
                            inputWidth: 170,
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
                                $$('visitsGrid').clearAll();
                                let url = $scope.getTableURI(15, 0);
                                $$('visitsGrid').load("addHeaders->" + url);
                                $$('visitsGrid').refresh();
                                $$('visitsGrid').setPage(0);
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
                                    $$('filterPassport').setValue("");
                                    $$('visitsGrid').clearAll();
                                    let url = $scope.getTableURI(15, 0);
                                    $$('visitsGrid').load("addHeaders->" + url);
                                    $$('visitsGrid').refresh();
                                    $$('visitsGrid').setPage(0);
                                }
                            },
                        ]
                    },
                    {
                        gravity: 0.4,
                        view: "layout",
                        rows: [{}]
                    }
                ]
            }]
        });


        // form client
        webix.ui({
            view: "window",
            id: "editwinClient",
            head: "Пациент",
            modal: true,
            position: "center",
            width: 750,
            autoheight: false,
            body: {
                view: "form",
                id: "editformClient",
                complexData: true,
                elements: [{
                    view: "text",
                    label: "Фамилия",
                    name: "surname",
                    labelWidth: 90,
                    invalidMessage: "Обязательное поле"
                },
                    {
                        cols: [{
                            view: "text",
                            label: "Имя",
                            name: "name",
                            labelWidth: 90
                        },
                            {
                                view: "text",
                                label: "Отчество",
                                name: "middlename",
                                labelWidth: 90
                            }
                        ]
                    },
                    {
                        rows: [{
                            view: "tabbar",
                            height: 35,
                            multiview: true,
                            id: "infoTabs",
                            value: "generalInfo",
                            on: {
                                onBeforeTabClick: $scope.getHistoryVisits
                            },
                            options: [{
                                id: "generalInfo",
                                css: "common_tab",
                                value: "Основные сведения"
                            },
                                {
                                    id: "historyVisits",
                                    css: "common_tab",
                                    value: "История приемов"
                                },
                                {
                                    id: "historyUltra",
                                    css: "common_tab",
                                    value: "УЗИ"
                                },
                                {
                                    id: "historyAnalyzes",
                                    css: "common_tab",
                                    value: "Анализы"
                                }
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
                                        view: "layout",
                                        label: "Инфо",
                                        borderless: true,
                                        rows: [{
                                            cols: [{
                                                view: "datepicker",
                                                editable: true,
                                                label: "Дата рождения",
                                                name: "birthday",
                                                labelWidth: 135
                                            },
                                                {
                                                    view: "button",
                                                    type: "form",
                                                    value: "printClient",
                                                    label: "Карта пациента",
                                                    css: "margin_client_col_sex",
                                                    inputWidth: 187,
                                                    click: function () {
                                                        $scope.printCardClient();
                                                    }
                                                }
                                            ]
                                        }]
                                    },
                                        {
                                            view: "layout",
                                            label: "ФИО",
                                            borderless: true,
                                            rows: [{
                                                cols: [{
                                                    view: "text",
                                                    label: "Телефон",
                                                    name: "phone",
                                                    labelWidth: 135
                                                },
                                                    {
                                                        view: "radio",
                                                        name: "gender",
                                                        css: "margin_client_col_sex",
                                                        options: [{
                                                            id: "man",
                                                            value: "Муж."
                                                        },
                                                            {
                                                                id: "woman",
                                                                value: "Жен."
                                                            }
                                                        ]
                                                    },
                                                ]
                                            }]
                                        },
                                        {
                                            view: "layout",
                                            label: "ФИО",
                                            borderless: true,
                                            rows: [{
                                                cols: [{
                                                    view: "text",
                                                    label: "Место работы",
                                                    name: "workPlace",
                                                    labelWidth: 135,
                                                    // inputWidth: 349
                                                },
                                                    {
                                                        gravity: 0.7,
                                                        view: "text",
                                                        label: "Должность",
                                                        name: "workPosition",
                                                        labelWidth: 100,
                                                        // inputWidth: 349
                                                    }
                                                ]
                                            }]
                                        },
                                        {
                                            view: "template",
                                            type: "section",
                                            template: "<span class='lb_template'>Паспортные данные</span>"
                                        },
                                        {
                                            view: "layout",
                                            label: "Паспорт",
                                            borderless: true,
                                            rows: [{
                                                cols: [{
                                                    view: "text",
                                                    label: "Серия",
                                                    name: "passportSerial",
                                                    labelWidth: 130
                                                },
                                                    {
                                                        view: "text",
                                                        label: "Номер",
                                                        name: "passportNumber",
                                                        css: "margin_client_col",
                                                        inputWidth: 327,
                                                        labelWidth: 140
                                                    }
                                                ]
                                            }]
                                        },
                                        {
                                            view: "layout",
                                            label: "Паспорт2",
                                            borderless: true,
                                            rows: [{
                                                cols: [{
                                                    view: "datepicker",
                                                    editable: true,
                                                    label: "Дата выдачи",
                                                    name: "passportDate",
                                                    labelWidth: 130
                                                },
                                                    {
                                                        view: "text",
                                                        label: "Код подразд.",
                                                        name: "passportCodePlace",
                                                        labelWidth: 140,
                                                        inputWidth: 327,
                                                        css: "margin_client_col"
                                                    }
                                                ]
                                            }]
                                        },
                                        {
                                            view: "text",
                                            label: "Кем выдан",
                                            name: "passportPlace",
                                            labelWidth: 130,
                                        },
                                        {
                                            view: "text",
                                            label: "Адрес",
                                            name: "address",
                                            labelWidth: 130,
                                        }
                                    ]
                                },
                                    {
                                        id: "historyVisits",
                                        view: "layout",
                                        margin: 8,
                                        padding: 10,
                                        rows: [{
                                            view: "datatable",
                                            css: "table_history_visits",
                                            id: "gridHistoryVisits",
                                            height: 365,
                                            header: false,
                                            select: true,
                                            scroll: "y",
                                            columns: [{
                                                id: "dateEvent",
                                                header: "Дата приема",
                                                width: 200
                                            },
                                                {
                                                    id: "label",
                                                    header: "Услуга",
                                                    fillspace: true
                                                },
                                            ],
                                            on: {
                                                onBeforeLoad: function () {
                                                    this.showOverlay("Загрузка...");
                                                },
                                                onAfterLoad: function () {
                                                    if (!this.count())
                                                        this.showOverlay("Приемы отсутствуют");
                                                    else
                                                        this.hideOverlay();
                                                }
                                            },
                                            data: []
                                        }]
                                    },
                                    {
                                        id: "historyUltra",
                                        view: "layout",
                                        margin: 8,
                                        padding: 10,
                                        rows: [{
                                            view: "datatable",
                                            css: "table_history_visits",
                                            id: "gridHistoryUltra",
                                            height: 365,
                                            header: false,
                                            select: true,
                                            scroll: "y",
                                            columns: [{
                                                id: "dateEvent",
                                                header: "Дата приема",
                                                width: 200
                                            },
                                                {
                                                    id: "label",
                                                    header: "Услуга",
                                                    fillspace: true
                                                },
                                            ],
                                            on: {
                                                onBeforeLoad: function () {
                                                    this.showOverlay("Загрузка...");
                                                },
                                                onAfterLoad: function () {
                                                    if (!this.count())
                                                        this.showOverlay("УЗИ отсутствуют");
                                                    else
                                                        this.hideOverlay();
                                                }
                                            },
                                            data: []
                                        }]
                                    },
                                    {
                                        id: "historyAnalyzes",
                                        view: "layout",
                                        margin: 8,
                                        padding: 10,
                                        rows: [{
                                            view: "datatable",
                                            css: "table_history_visits",
                                            id: "gridHistoryAnalyzes",
                                            height: 365,
                                            header: false,
                                            select: true,
                                            scroll: "y",
                                            columns: [{
                                                id: "dateEvent",
                                                header: "Дата приема",
                                                width: 200
                                            },
                                                {
                                                    id: "label",
                                                    header: "Услуга",
                                                    fillspace: true
                                                },
                                            ],
                                            on: {
                                                onBeforeLoad: function () {
                                                    this.showOverlay("Загрузка...");
                                                },
                                                onAfterLoad: function () {
                                                    if (!this.count())
                                                        this.showOverlay("Анализы отсутствуют");
                                                    else
                                                        this.hideOverlay();
                                                }
                                            },
                                            data: []
                                        }]
                                    }
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
                                if ($$("editformClient").validate()) {
                                    $scope.saveClient();
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
                                }
                            }
                        ]
                    }
                ],
                rules: {
                    surname: webix.rules.isNotEmpty,
                    passportSerial: webix.rules.isNotEmpty,
                    passportNumber: webix.rules.isNotEmpty,
                }
            }
        });

        webix.ui({
            view: "window",
            id: "editwin",
            head: "Прием",
            modal: true,
            position: "center",
            width: 700,
            autoheight: false,
            body: {
                view: "form",
                id: "editform",
                complexData: true,
                elements: [{
                    view: "combo",
                    label: "ФИО доктора",
                    name: "doctorId",
                    id: "cmbDoctorOnForm",
                    labelWidth: 145,
                    options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/" +
                        version_api + "/doctors").response),
                    on: {
                        onChange: $scope.getReservedAppoinmentsHasVisit
                    },
                    invalidMessage: ""
                },
                    {
                        view: "combo",
                        label: "Запись на прием",
                        id: "cmbAppointments",
                        name: "appointmentId",
                        labelWidth: 145,
                    },
                    {
                        cols: [{
                            view: "combo",
                            label: "ФИО пациента",
                            id: "cmbClient",
                            name: "clientId",
                            labelWidth: 145,
                            options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/" +
                                version_api + "/clients").response),
                            on: {
                                onChange: $scope.getInfoClient
                            },
                            invalidMessage: ""
                        },
                            {
                                gravity: 0.55,
                                cols: [{
                                    view: "button",
                                    type: "form",
                                    value: "AddClient",
                                    label: "Добавить",
                                    click: function () {
                                        $scope.insertClient();
                                    }
                                },
                                    {
                                        view: "button",
                                        value: "UpdateClient",
                                        label: "Редактировать",
                                        click: function () {
                                            $scope.updateClient();
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        cols: [{
                            cols: [{
                                view: "text",
                                id: "passportLabel",
                                name: "passportLabel",
                                disabled: true
                            },
                                {
                                    view: "text",
                                    id: "birthday",
                                    name: "birthday",
                                    disabled: true
                                }
                            ]
                        },
                            {
                                gravity: 0.55,
                                view: "text",
                                id: "phone",
                                name: "phone",
                                disabled: true
                            }
                        ]
                    },
                    {
                        cols: [{
                            view: "text",
                            id: "address",
                            name: "address",
                            disabled: true
                        },
                            {
                                gravity: 0.55,
                                view: "button",
                                type: "form",
                                value: "PrintCardClient",
                                label: "Карта пациента",
                                click: function () {
                                    $scope.printCardClient();
                                }
                            }
                        ]
                    },
                    {
                        rows: [{
                            view: "tabbar",
                            id: "infoVisit",
                            height: 35,
                            multiview: true,
                            on: {
                                onChange: function() {
                                    if ($$("infoVisit").getValue() != null &&
                                        $$("infoVisit").getValue().localeCompare("report") == 0) {
                                        $scope.saveRow();
                                        $scope.getReportsOfVisit()
                                    }
                                }
                            },
                            options: [{
                                id: "service",
                                css: "common_tab",
                                value: "Услуги"
                            },
                                {
                                    id: "result",
                                    css: "common_tab",
                                    value: "Заключение"
                                },
                                {
                                    id: "report",
                                    css: "common_tab",
                                    value: "Отчеты",
                                }
                            ],
                        },
                            {
                                css: "bottom_border_tab_header",
                                cells: [{
                                    id: "service",
                                    view: "layout",
                                    margin: 8,
                                    padding: 10,
                                    rows: [{
                                        view: "combo",
                                        label: "Услуга",
                                        id: "cmbService",
                                        labelWidth: 70,
                                        options: {
                                            filter: function (item, value) {
                                                return item.label.
                                                toString().
                                                toLowerCase().
                                                indexOf(value.toLowerCase()) > -1;
                                            },
                                            body: {
                                                data: JSON.parse(
                                                    webix.ajax().headers($localStorage.headers.value).
                                                    sync().get("/api/" + version_api + "/services").response)
                                            }
                                        }
                                    },
                                        {
                                            cols: [{
                                                view: "text",
                                                label: "Скидка",
                                                id: "discount",
                                                name: "discount",
                                                labelWidth: 70
                                            },
                                                {
                                                    cols: [{
                                                        view: "button",
                                                        type: "form",
                                                        value: "AddService",
                                                        label: "Добавить",
                                                        click: function () {
                                                            $scope.addServicesOfVisit();
                                                        }
                                                    },
                                                        {
                                                            view: "button",
                                                            value: "deleteService",
                                                            label: "Удалить",
                                                            click: function () {
                                                                $scope.deleteServicesOfVisit();
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    view: "text",
                                                    label: "Терминал",
                                                    id: "terminalSum",
                                                    name: "terminalSum",
                                                    labelWidth: 90
                                                }
                                            ]
                                        },
                                        {
                                            view: "datatable",
                                            css: "table_without_border",
                                            height: 200,
                                            header: false,
                                            scroll: "y",
                                            select: true,
                                            id: "gridServices",
                                            columns: [{
                                                id: "label",
                                                header: "Наименование услуги",
                                                fillspace: true
                                            },
                                                {
                                                    id: "price",
                                                    header: "Прайс",
                                                    width: 100
                                                },
                                                {
                                                    id: "discount",
                                                    header: "Скидка",
                                                    width: 100
                                                }
                                            ],
                                            on: {
                                                onBeforeLoad: function () {
                                                    this.showOverlay("Загрузка...");
                                                },
                                                onAfterLoad: function () {
                                                    if (!this.count())
                                                        this.showOverlay("Нет добавленных услуг");
                                                    else
                                                        this.hideOverlay();
                                                }
                                            },
                                            data: []
                                        }
                                    ]
                                },
                                    {
                                        id: "result",
                                        view: "layout",
                                        margin: 8,
                                        padding: 10,
                                        rows: [{
                                            view: "textarea",
                                            label: "Диагноз",
                                            id: "diagnosis",
                                            name: "diagnosis",
                                            labelPosition: "left",
                                            height: 60,
                                            labelWidth: 110,
                                            value: ""
                                        },
                                            {
                                                view: "textarea",
                                                label: "Лечение",
                                                id: "therapy",
                                                name: "therapy",
                                                labelPosition: "left",
                                                height: 160,
                                                labelWidth: 110,
                                                value: ""
                                            },
                                            {
                                                view: "textarea",
                                                label: "Доп/oбслед.",
                                                id: "additionalExamination",
                                                name: "additionalExamination",
                                                labelPosition: "left",
                                                height: 58,
                                                labelWidth: 110,
                                                value: ""
                                            }
                                        ]
                                    },
                                    {
                                        id: "report",
                                        view: "layout",
                                        margin: 8,
                                        padding: 10,
                                        rows: [{
                                            cols: [{
                                                view: "button",
                                                type: "form",
                                                value: "saveReport",
                                                label: "Распечатать",
                                                inputWidth: 115,
                                                click: function () {
                                                    $scope.saveReportAsFile();
                                                }
                                            },
                                                {
                                                    view: "layout",
                                                    rows: [{}]
                                                }
                                            ]
                                        },
                                            {
                                                view: "datatable",
                                                css: "table_without_border",
                                                id: "gridReports",
                                                height: 246,
                                                header: false,
                                                select: true,
                                                scroll: "y",
                                                columns: [{
                                                    id: "label",
                                                    header: "Наименование отчета",
                                                    fillspace: true
                                                }],
                                                on: {
                                                    onBeforeLoad: function () {
                                                        this.showOverlay("Загрузка...");
                                                    },
                                                    onAfterLoad: function () {
                                                        if (!this.count())
                                                            this.showOverlay("Нет отчетов для сохранения");
                                                        else
                                                            this.hideOverlay();
                                                    }
                                                },
                                                data: []
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
                                    if ($scope.serverValidation) {
                                        $scope.reloadGridVisit();
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
                                    $scope.reloadGridVisit();
                                    this.getTopParentView().hide();
                                }
                            }
                        ]
                    }
                ],
                rules: {
                    doctorId: webix.rules.isNotEmpty,
                    clientId: webix.rules.isNotEmpty
                }
            }
        });

        // grid visits
        webix.ui({
            id: "visitsGrid",
            container: "visitsGrid",
            view: "datatable",
            width: 1200,
            autoheight: true,
            editable: true,
            scroll: false,
            editaction: "custom",
            select: "row",
            datafetch: 15,
            datathrottle: 300,
            loadahead: 15,
            data: webix.ajax().headers($localStorage.headers.value).get("/api/" + version_api +
                "/visits?count=15&start=0"),
            datatype: "json",
            columns: $scope.colums,
            pager: {
                template: "{common.first()} {common.prev()} {common.pages()} {common.next()} {common.last()}",
                container: 'pager',
                size: 15,
                group: 5,
                page: 0
            },
            on: {
                onItemDblClick: function () {
                    console.time("call onItemDblClick")
                    $scope.visitId = $$("visitsGrid").getSelectedId();
                    $$("editform").clearValidation();
                    $$("gridServices").clearAll();
                    $$("cmbService").setValue(null);
                    $$("infoVisit").setValue("service");
                    //Non necessary reload (temporary)
                    // $scope.getInfoClient();
                    $scope.getServicesOfVisit();
                    $$("editwin").show();
                    console.timeEnd("call onItemDblClick")
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
                    let url = $scope.getTableURI(count, start);
                    let dataGrid = webix.ajax().headers($localStorage.headers.value).get(url);
                    this.parse(dataGrid);
                    return false;
                }
            }
        });
        $$("editform").bind($$("visitsGrid"));
    });
    $scope.getTableURI = function (count, start) {
        console.time("call getTableURI")
        let url = "/api/" + version_api + "/visits?count=" + count + "&start=" + start + "&filter=";
        if ($$("filterDateEvent").getValue() != null && $$("filterDateEvent").getValue().toJSON() != "")
            url = url + "dateEvent=" + $$("filterDateEvent").getValue().toJSON() + ";";
        if ($$("filterDoctor").getValue() != null && $$("filterDoctor").getValue() != "")
            url = url + "doctor.surname=" + $$("filterDoctor").getValue() + ";";
        if ($$("filterClient").getValue() != null && $$("filterClient").getValue() != "")
            url = url + "client.surname=" + $$("filterClient").getValue() + ";";
        if ($$("filterPassport").getValue() != null && $$("filterPassport").getValue() != "")
            url = url + "passport=" + $$("filterPassport").getValue() + ";";
        console.timeEnd("call getTableURI")
        return url;
    }
    $scope.saveRow = function () {
        console.time("call saveRow")
        let id = $scope.visitId;
        let data = $$("editform").getValues();
        data.author = $localStorage.currentUser.login;
        if (!id) {
            let url = "/api/" + version_api + "/visits/";
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("visitsGrid").parse(text);
                        $scope.visitId = JSON.parse(text).id;
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("label");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Add visit - " + id)

                    }
                });
            $$("editform").bind($$("visitsGrid"));
        } else {
            let url = "/api/" + version_api + "/visits/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("visitsGrid").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("label");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Update visit - " + id)
                    }
                });
        }
        console.timeEnd("call saveRow")
    };

    $scope.deleteRow = function () {
        console.time("call deleteRow")
        let id = $$("visitsGrid").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление приема",
            text: "Вы уверены, что хотите удалить прием?",
            callback: function (result) {
                if (result) {
                    let url = "/api/" + version_api + "/visits/" + id;
                    webix.ajax().headers($localStorage.headers.value)
                        .del(url, JSON.stringify($$('editform').getValues()), {
                            success: function (text, data, XmlHttpRequest) {
                                $$("visitsGrid").remove(id);
                            },
                            error: function (text, data, XmlHttpRequest) {
                                $scope.checkAuth(XmlHttpRequest);
                                console.log("Fail. Delete visit - " + id)
                            }
                        });
                }
            }
        });
        console.time("call endRow")
    };

    $scope.addRow = function () {
        console.time("call addRow")
        $$("visitsGrid").clearSelection();
        $$("editform").clear();
        $$("editform").clearValidation();
        $$("gridServices").clearAll();
        $$("cmbService").setValue(null);
        $scope.visitId = null;
        $$("infoVisit").setValue("service");
        $$("editwin").show();
        console.timeEnd("call addRow")
    };

    // insert сlient
    $scope.insertClient = function () {
        console.time("call insertClient")
        $$("editformClient").clear();
        $$("editformClient").clearValidation();
        $scope.clientId = null;
        $$("editwinClient").show();
        console.timeEnd("call insertClient")
    };

    // update сlient
    $scope.updateClient = function () {
        console.time("call updateClient")
        $$("editformClient").clear();
        $$("editformClient").clearValidation();
        $scope.clientId = $$("cmbClient").getValue();
        $scope.getHistoryVisits();
        $$("editwinClient").show();
        $$("editformClient").setValues($scope.getAllInfoClientById());
        console.timeEnd("call updateClient")
    };

    // save row client
    $scope.saveClient = function () {
        console.time("call saveClient")
        let data = $$("editformClient").getValues();
        let id = data.id;
        data.author = $localStorage.currentUser.login;
        if (!id) {
            let url = "/api/" + version_api + "/clients/";
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $scope.serverValidation = true;
                        $scope.reloadComboClients();
                        $scope.getInfoClient();
                        $$('cmbClient').setValue(JSON.parse(text).id);
                        $$('cmbClient').refresh();
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editformClient").markInvalid("passportSerial");
                        $$("editformClient").markInvalid("passportNumber");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Add client - " + id)
                    }
                });
        } else {
            let url = "/api/" + version_api + "/clients/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $scope.serverValidation = true;
                        $scope.reloadComboClients();
                        $scope.getInfoClient();
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editformClient").markInvalid("passportSerial");
                        $$("editformClient").markInvalid("passportNumber");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Update client - " + id)
                    }
                });
        }
        console.timeEnd("call saveClient")
    }

    $scope.checkAuth = function (XmlHttpRequest) {
        console.time("call checkAuth")
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
        console.timeEnd("call checkAuth")
    };
}