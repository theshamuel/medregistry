'use strict';
angular
    .module('app')
    .controller('VisitsCtrl', VisitsCtrl)

function VisitsCtrl($http, $location, $localStorage, $scope, $rootScope, uiGridConstants, i18nService, $uibModal, dialogs, $route) {
    var version_api = "v1";
    
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


    var dateEvent = new Date();
    var formatDate = webix.Date.dateToStr("%d.%m.%Y");

    // +
    $scope.reloadComboClients = function () {
        var dataClient = [];
        var url = "/api/"+version_api+"/clients";
        webix.ajax().headers($localStorage.headers.value).sync().get(url, {
            success: function (text, data, XmlHttpRequest) {
                dataClient = JSON.parse(text);
            },
            error: function (text, data, XmlHttpRequest) {
                //Сделать стандартные проверки
                $scope.checkAuth(XmlHttpRequest);
            }
        });
        $$('cmbClient').define("options", dataClient);
        $$('cmbClient').refresh();
    };
    // +
    $scope.getAllInfoClientById = function () {
        var dataClient = [];
        if ($$("cmbClient").getValue() != null && $$("cmbClient").getValue() != undefined && $$("cmbClient").getValue() != "") {
            var url = "/api/"+version_api+"/clients/" + $$("cmbClient").getValue();
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataClient = JSON.parse(text);
                },
                error: function (text, data, XmlHttpRequest) {
                    //Сделать стандартные проверки
                    $scope.checkAuth(XmlHttpRequest);
                }
            });

        }
        return dataClient;
    };
    // +
    $scope.getReservedAppoinmentsHasVisit = function () {
        var dataAppointments = [];
        if ($$("cmbDoctorOnForm").getValue() != null && $$("cmbDoctorOnForm").getValue() != undefined && $$("cmbDoctorOnForm").getValue() != "") {
            var id = $scope.visitId;
            var url = "/api/"+version_api+"/appointments/reserved/hasvisit?doctorId=" + $$("cmbDoctorOnForm").getValue() + "&dateEvent=" + "&hasVisit=false&visitId";
            if (id != undefined)
                url = url + "=" + id;
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
    };
    // +
    $scope.getInfoClient = function () {
        var passportLabelClient = "";
        var birthdayClient = "";
        var phoneClient = "";
        var addressClient = "";
        if ($$("cmbClient").getValue() != null && $$("cmbClient").getValue() != undefined && $$("cmbClient").getValue() != "") {
            var url = "/api/"+version_api+"/clients/" + $$("cmbClient").getValue();
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    passportLabelClient = JSON.parse(text).passportLabel;
                    birthdayClient = formatDate(JSON.parse(text).birthday);
                    phoneClient = JSON.parse(text).phone;
                    addressClient = JSON.parse(text).address;
                },
                error: function (text, data, XmlHttpRequest) {
                    //Сделать стандартные проверки
                    $scope.checkAuth(XmlHttpRequest);
                }
            });
        }
        $$("passportLabel").setValue(passportLabelClient);
        $$("birthday").setValue(birthdayClient);
        $$("phone").setValue(phoneClient);
        $$("address").setValue(addressClient);
    };
    // +   
    $scope.getHistoryVisits = function () {
        var dataOfHistiryVisits = [];
        var dataOfHistiryUltra = [];
        var dataOfHistiryAnalyzes = [];
        var url = "";
        var id = $scope.clientId;
        if (id != null && id != "") {
            url = "/api/"+version_api+"/clients/" + id + "/consult";
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfHistiryVisits = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }
            });
            url = "/api/"+version_api+"/clients/" + id + "/ultra";
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfHistiryUltra = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }
            });
            url = "/api/"+version_api+"/clients/" + id + "/analyzes";
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
    };
    // +
    $scope.getServicesOfVisit = function () {
        var dataOfServices = [];
        var url = "";
        var id = $scope.visitId;
        var error = false;
        if (id != null && id != "" && id != undefined) {
            url = "/api/"+version_api+"/visits/" + id + "/services";
            console.log("url=" + url);
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfServices = text;
                    console.log("services=" + text);
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
    };
    // +
    $scope.getReportsOfVisit = function () {
        var dataOfReports = [];
        var url = "";
        var id = $scope.visitId;
        if (id != null && id != "") {
            url = "/api/"+version_api+"/reports/" + id;
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
    };

    // +
    $scope.printCardClient = function () {
        var dateReport = new Date();
        var clientId = $$("cmbClient").getValue();
        var doctorId = $$("cmbDoctorOnForm").getValue();
        var url = "/api/"+version_api+"/reports/file/clientCard/" + clientId + "/" + doctorId + "/" + dateReport.toJSON();
        webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
            $rootScope.saveByteArray([data], 'Карта_пациента-' + dateReport.toJSON() + '.xls');
        });
    }
    $scope.reloadGridVisit = function () {
        $$('visitsGrid').clearAll();
        var url = $scope.getTableURI(15,0);
        $$('visitsGrid').load("addHeaders->"+url);
        $$('visitsGrid').refresh();
    };

    $scope.addServicesOfVisit = function () {
        var url = "";
        var id = $scope.visitId;
        var serverValid = true;
        if ((id === null || id === "" || id === undefined) && $$("editform").validate()) {
            $scope.saveRow();
            id = $scope.visitId;
            serverValid = $scope.serverValidation;
        }
        if ($$("editform").validate() && serverValid && id != null && id != "" && id != undefined && $$("cmbService").getValue() != null && $$("cmbService").getValue() != undefined && $$("cmbService").getValue() != "") {
            url = "/api/"+version_api+"/visits/" + id + "/services/" + $$("cmbService").getValue() + "?discount=";
            if ($$("discount").getValue() != "" && $$("discount").getValue() != undefined && $$("discount").getValue() != null)
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

    };

    $scope.saveReportAsFile = function () {
        var reportId = $$("gridReports").getSelectedId();
        var reportItem = $$("gridReports").getSelectedItem();
        var clientId = $$("cmbClient").getValue();
        var visitId = $scope.visitId;
        var doctorId = $$("cmbDoctorOnForm").getValue();
        var dateReport = new Date();
        if (reportId === null || reportId === "" || reportId === undefined) {
            webix.alert(" Не выбран отчет для сохранения ");
        } else {
            var url = "/api/"+version_api+"/reports/file/reportTemplate/" + clientId + "/" + doctorId + "/" + reportId + "/" + visitId + "/" + dateReport.toJSON();
            webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
                if (reportItem != null && reportItem.template === "Contract")
                    $rootScope.saveByteArray([data], 'Договор_' + dateReport.toJSON() + '.xls');
                else
                    $rootScope.saveByteArray([data], 'Бланк_' + reportItem.label + "-" + dateReport.toJSON() + '.doc');
            });
        }
    };

    // +
    $scope.deleteServicesOfVisit = function () {
        var dataOfServices = [];
        var url = "";
        var id = $scope.visitId;
        var servicesId = $$("gridServices").getSelectedId();
        if (servicesId === null || servicesId === "" || servicesId === undefined) {
            webix.alert(" Не выбрана услуга для удаления ");
        } else if (id != null && id != "" && id != undefined) {
            url = "/api/"+version_api+"/visits/" + id + "/services/" + servicesId;
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
    };


    webix.ready(function () {
        webix.proxy.addHeaders = {
            $proxy:true,
            load:function(view, callback, params){
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
                                    var url = $scope.getTableURI(15,0);
                                    $$('visitsGrid').load("addHeaders->"+url);
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
                                    var url = $scope.getTableURI(15,0);
                                    $$('visitsGrid').load("addHeaders->"+url);
                                    $$('visitsGrid').refresh();
                                    $$('visitsGrid').setPage(0);
                                }
                            },
                        ]
                    },
                    {
                        gravity: 0.4,
                        view: "layout",
                        rows:[{}]
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

        // forma visits
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
                        options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/doctors").response),
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
                                options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/clients").response),
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
                                        // label: "Паспорт",
                                        id: "passportLabel",
                                        name: "passportLabel",
                                        disabled: true
                                    },
                                    {
                                        view: "text",
                                        // label: "Дата рождения",
                                        id: "birthday",
                                        name: "birthday",
                                        disabled: true
                                    }
                                ]
                            },
                            {
                                gravity: 0.55,
                                view: "text",
                                // label: "Телефон",
                                id: "phone",
                                name: "phone",
                                disabled: true
                            }
                        ]
                    },
                    {
                        cols: [{
                                view: "text",
                                // label: "Место жительства",
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
                                    onBeforeTabClick: $scope.getReportsOfVisit
                                },
                                options: [{
                                        id: "service",
                                        css: "common_tab",
                                        value: "Услуги"
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
                                                        return item.label.toString().toLowerCase().indexOf(value.toLowerCase()) > -1;
                                                    },
                                                    body: {
                                                        data: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/services").response)
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
                    // appointmentId: webix.rules.isNotEmpty,
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
            data: webix.ajax().headers($localStorage.headers.value).get("/api/"+version_api+"/visits?count=15&start=0"),
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
                    $scope.visitId = $$("visitsGrid").getSelectedId();
                    $$("editform").clearValidation();
                    $$("gridServices").clearAll();
                    $$("cmbService").setValue(null);
                    $$("infoVisit").setValue("service");
                    $$("editwin").show();
                    $scope.getInfoClient();
                    $scope.getServicesOfVisit();
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
        $$("editform").bind($$("visitsGrid"));
    });
    $scope.getTableURI = function(count,start){
        var url = "/api/"+version_api+"/visits?count=" + count + "&start=" + start+"&filter=";
        if ($$("filterDateEvent").getValue()!=null && $$("filterDateEvent").getValue().toJSON()!="")
            url = url + "dateEvent="+$$("filterDateEvent").getValue().toJSON()+";";
        if ($$("filterDoctor").getValue()!=null && $$("filterDoctor").getValue()!="")
            url = url + "doctor.surname="+$$("filterDoctor").getValue()+";";
        if ($$("filterClient").getValue()!=null && $$("filterClient").getValue()!="")
            url = url + "client.surname="+$$("filterClient").getValue()+";";
        if ($$("filterPassport").getValue()!=null && $$("filterPassport").getValue()!="")
            url = url + "passport="+$$("filterPassport").getValue()+";";            
        return url;
    }
    $scope.saveRow = function () {
        var id = $scope.visitId;
        var data = $$("editform").getValues();
        data.author = $localStorage.currentUser.login;
        if (!id) {
            var url = "/api/"+version_api+"/visits/";
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
            var url = "/api/"+version_api+"/visits/" + id;
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
    };

    $scope.deleteRow = function () {
        var id = $$("visitsGrid").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление приема",
            text: "Вы уверены, что хотите удалить прием?",
            callback: function (result) {
                if (result) {
                    var url = "/api/"+version_api+"/visits/" + id;
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
    };

    $scope.addRow = function () {
        $$("visitsGrid").clearSelection();
        $$("editform").clear();
        $$("editform").clearValidation();
        $$("gridServices").clearAll();
        $$("cmbService").setValue(null);
        $scope.visitId = null;
        $$("infoVisit").setValue("service");
        $$("editwin").show();
    };

    // insert сlient
    $scope.insertClient = function () {
        $$("editformClient").clear();
        $$("editformClient").clearValidation();
        $scope.clientId = null;
        $$("editwinClient").show();
    };

    // update сlient
    $scope.updateClient = function () {
        $$("editformClient").clear();
        $$("editformClient").clearValidation();
        $scope.clientId = $$("cmbClient").getValue();
        $scope.getHistoryVisits();
        $$("editwinClient").show();
        $$("editformClient").setValues($scope.getAllInfoClientById());
    };

    // save row client
    $scope.saveClient = function () {
        var data = $$("editformClient").getValues();
        var id = data.id;
        data.author = $localStorage.currentUser.login;
        if (!id) {
            var url = "/api/"+version_api+"/clients/";
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
            var url = "/api/"+version_api+"/clients/" + id;
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
    }

    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    };

    $scope.logout = function () {
        $localStorage.currentUser = null;
        $scope.userLogin = '';
        $scope.userPassword = '';
        $http.defaults.headers.common.Authorization = '';
        // $location.path('/');
        $window.location.href = '/login';
    };
}