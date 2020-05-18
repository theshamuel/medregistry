'use strict';
angular
    .module('app')
    .controller('VisitsTodayCtrl', VisitsTodayCtrl)

function VisitsTodayCtrl($http, $location, $localStorage, $scope, $rootScope, uiGridConstants, i18nService, $uibModal, dialogs, $route, $window) {
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';
    $scope.visitId = "";

    $scope.columns = [{
            id: "dateTimeLabel",
            header: 'Дата и время приема',
            width: 200
        },
        {
            id: "clientLabel",
            header: 'ФИО пациента',
            width: 300
        },
        {
            id: "phoneLabel",
            header: 'Телефон',
            width: 150
        },
        {
            id: "totalSum",
            header: 'Сумма к оплате',
            width: 140
        },
    ];

    let dateEvent = new Date();
    let formatDate = webix.Date.dateToStr("%d.%m.%Y");
    let version_api = "v1";
    $scope.reloadComboClients = function () {
        let dataClient = [];
        let url = "/api/"+version_api+"/clients";
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

    $scope.getAllInfoClientById = function () {
        let dataClient = [];
        if ($$("cmbClient").getValue() != null && $$("cmbClient").getValue() != undefined && $$("cmbClient").getValue() != "") {
            let url = "/api/"+version_api+"/clients/" + $$("cmbClient").getValue();
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataClient = JSON.parse(text);
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                }
            });

        }
        return dataClient;
    };

    $scope.reloadGridVisit = function () {
        let dataVisits = [];
        if ($localStorage.visTdDoctorId != null && $localStorage.visTdDoctorId != undefined && $localStorage.visTdDoctorId != "") {
            let url = "/api/"+version_api+"/visits/" + $localStorage.visTdDoctorId + "/" + dateEvent.toJSON();
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataVisits = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                }
            });
            $$("visitsGrid").clearAll();
            $$("visitsGrid").parse(dataVisits);
        }

    };

    $scope.getInfoClient = function () {
        let passportLabelClient = "";
        let birthdayClient = "";
        let phoneClient = "";
        let addressClient = "";
        if ($$("cmbClient").getValue() != null && $$("cmbClient").getValue() != undefined && $$("cmbClient").getValue() != "") {
            let url = "/api/"+version_api+"/clients/" + $$("cmbClient").getValue();
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

    $scope.getServicesOfVisit = function () {
        let dataOfServices = [];
        let url = "";
        let id = $scope.visitId;
        let error = false;
        if (id != null && id != undefined && id != "") {
            url = "/api/"+version_api+"/visits/" + id + "/services";
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfServices = text;
                    console.log(text);
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    error = true;
                    console.log("FAIL=" + text)
                }
            });
        }
        if (!error){
            $$("gridServices").clearAll();
            $$("gridServices").parse(dataOfServices);
        }else{
            error = false;
        }
    };

    $scope.getReportsOfVisit = function () {
        let dataOfReports = [];
        let url = "";
        let id = $scope.visitId;
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

    $scope.printCardClient = function () {
        let dateReport = new Date();
        let clientId = $$("cmbClient").getValue();
        let doctorId = $$("cmbDoctorOnForm").getValue();
        let url = "/api/"+version_api+"/reports/file/clientCard/" + clientId + "/" + doctorId + "/" + dateReport.toJSON();
        webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
            $rootScope.saveByteArray([data], 'Карта_пациента-' + dateReport.toJSON() + '.xls');
        });
    }

    $scope.addServicesOfVisit = function () {
        let url = "";
        let id = $scope.visitId;
        let serverValid = true;
        if ((id === null || id === "" || id === undefined) && $$("editform").validate() || ($scope.visitChange!=null && $scope.visitChange!=undefined && $scope.visitChange == true)) {
            $scope.saveRow();
            id = $scope.visitId;
            serverValid = $scope.serverValidation;
            $scope.visitChange = false;
        }
        if ($$("editform").validate() && serverValid) {
            if (id != null && id != "" && id != undefined  && $$("cmbService").getValue() != null && $$("cmbService").getValue() != undefined && $$("cmbService").getValue() != "") {
                url = "/api/"+version_api+"/visits/" + id + "/services/" + $$("cmbService").getValue() + "?discount=";
                if ($$("discount").getValue() != "" && $$("discount").getValue() != undefined && $$("discount").getValue() != null)
                    url = url + $$("discount").getValue();
                console.log("addServicesOfVisit url="+url)
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
        } else {
            webix.alert({
                title: "Ошибка",
                ok: "ОК",
                type: "alert-error",
                text: "Заполните обязательные поля для приема"
            });
        }
    };

    $scope.deleteServicesOfVisit = function () {
        let dataOfServices = [];
        let url = "";
        let id = $scope.visitId;
        let servicesId = $$("gridServices").getSelectedId();
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

    $scope.getAppointmentsList = function () {
        if ($$("cmbDoctorInList") != null && $$("cmbDoctorInList").getValue() != null && $$("cmbDoctorInList").getValue() != undefined && $$("cmbDoctorInList").getValue() != "") {
            let url = "/api/"+version_api+"/appointments/reserved/hasvisit?doctorId=" + $$("cmbDoctorInList").getValue() + "&dateEvent=" + dateEvent.toJSON() + "&hasVisit=false&visitId";
            $localStorage.visTdDoctorId = $$("cmbDoctorInList").getValue();
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    $$("listAppointmentsToday").clearAll();
                    $$("listAppointmentsToday").define("data", text);
                    $$("listAppointmentsToday").refresh();
                    $scope.reloadGridVisit();
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    $$("listAppointmentsToday").clearAll();
                    $$("listAppointmentsToday").define("data", []);
                    $$("listAppointmentsToday").refresh();
                }
            });
        }

    };

    $scope.getHistoryVisits = function () {
        let dataOfHistiryVisits = [];
        let dataOfHistiryUltra = [];
        let dataOfHistiryAnalyzes = [];
        let url = "";
        let id = $scope.clientId;
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

    $scope.getReservedAppoinmentsHasVisit = function () {
        let dataAppointments = [];
        if ($$("cmbDoctorOnForm").getValue() != null && $$("cmbDoctorOnForm").getValue() != undefined && $$("cmbDoctorOnForm").getValue() != "") {
            let id = $scope.visitId;
            let url = "/api/"+version_api+"/appointments/reserved/hasvisit?doctorId=" + $$("cmbDoctorOnForm").getValue() + "&dateEvent=" + dateEvent.toJSON() + "&hasVisit=false&visitId";
            if (id != undefined)
                url = url + "=" + id;
            console.log("URL=" + url);
            $scope.visitChange = true;
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataAppointments = JSON.parse(text);
                },
                error: function (text, data, XmlHttpRequest) {
                    //Сделать стандартные проверки
                    $scope.checkAuth(XmlHttpRequest);
                }
            });
            $$('cmbAppointments').define("options", dataAppointments);
            $$('cmbAppointments').refresh();

        }
    };

    $scope.saveListAppoinmentsAsFile = function () {
        let visTdDoctor = $localStorage.visTdDoctorId;
        let dateReport = new Date();
        if (visTdDoctor === null || visTdDoctor === "" || visTdDoctor === undefined) {
            webix.alert(" Не выбран доктор для приемов ");
        } else {
            let url = "/api/"+version_api+"/reports/file/listAppointments/"+ visTdDoctor +  "/" + dateReport.toJSON();
            webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
                if (text!=null)
                    $rootScope.saveByteArray([data], 'Приемы_' + dateReport.toJSON() + '.docx');
            });
        }
    };


    webix.ready(function () {

        // window edit visit
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
                        invalidMessage: ""
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
                                            if ($$("editform").validate()) {
                                                $scope.saveRow();
                                                $scope.getReportsOfVisit();
                                            }
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
                                        id: "result",
                                        view: "layout",
                                        margin: 8,
                                        padding: 10,
                                        rows: [{
                                            view: "textarea",
                                            label: "Диагноз",
                                            id: "diagnosis",
                                            name: "diagnosis",
                                            labelPosition: "top",
                                            height: 90,
                                            value: ""
                                        },
                                            {
                                                view: "textarea",
                                                label: "Лечение",
                                                id: "therapy",
                                                name: "therapy",
                                                labelPosition: "top",
                                                height: 110,
                                                value: ""
                                            },
                                            {
                                                view: "textarea",
                                                label: "Дообследование",
                                                id: "additionalExamination",
                                                name: "additionalExamination",
                                                labelPosition: "top",
                                                height: 78,
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
                                            this.getTopParentView().hide();
                                        }
                                        $scope.getAppointmentsList()
                                    } else {
                                        webix.alert({
                                            title: "Ошибка",
                                            ok: "ОК",
                                            type: "alert-error",
                                            text: "Заполните обязательные поля для приема"
                                        });
                                    }
                                }
                            },
                            {
                                view: "button",
                                value: "Cancel",
                                label: "Отмена",
                                click: function () {
                                    $scope.getAppointmentsList()
                                    this.getTopParentView().hide();
                                }
                            }
                        ]
                    }
                ],
                rules: {
                    doctorId: webix.rules.isNotEmpty,
                    appointmentId: webix.rules.isNotEmpty,
                    clientId: webix.rules.isNotEmpty
                }

            }
        });

        // grid visits
        webix.ui({
            id: "visitsGrid",
            container: "visitsGrid",
            view: "datatable",
            width: 790,
            height: 680,
            editable: true,
            scroll: "y",
            editaction: "custom",
            select: "row",
            datatype: "json",
            columns: $scope.columns,
            // pager: {
            //     template: "{common.first()} {common.prev()} {common.pages()} {common.next()} {common.last()}",
            //     container: 'pager',
            //     size: 15,
            //     group: 5
            // },
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
                    $scope.getAppointmentsList
                    if (!this.count())
                        this.showOverlay("Нет данных для отображения");
                    else
                        this.hideOverlay();
                }
            }
        });

        // form combo doctor + list appointments today
        webix.ui({
            view: "form",
            id: "formDoctor",
            container: "formDoctor",
            css: "doctor_edit",
            elements: [{
                    view: "combo",
                    id: "cmbDoctorInList",
                    label: "ФИО доктора",
                    name: "doctor",
                    labelWidth: 120,
                    inputWidth: 350,
                    options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/doctors").response),
                    on: {
                        onChange: $scope.getAppointmentsList
                    }
                },
                 {
                    view: "list",
                    id: "listAppointmentsToday",
                    width: 350,
                    height: 598,
                    css: "visit_doc_cmb",
                    type: {
                        itemTemplate: function (obj) {
                            if (obj.isHere === true)
                                return "<div class='list_cell_free'><div><span class='lb_template'> "+obj.timeEvent+"</span> - "+obj.client+" </div> - "+obj.service+"</div>"
                            else
                                return "<div class='list_cell_coming_app'><div><span class='lb_template'> "+obj.timeEvent+"</span> - "+obj.client+" </div> - "+obj.service+"</div>"
                        },
                        height: 80,
                        template: "{common.itemTemplate()}",
                    },
                    data: [],
                    on: {
                        onAfterLoad: function () {
                            if (this.count() > 0) {
                                this.hideOverlay();
                            } else {
                                webix.extend(this, webix.OverlayBox);
                                this.showOverlay("<div class='lb_template'>На сегодня отсутствуют<br/> записи на прием<br/></div>");
                            }
                        }
                    }
                }
            ]
        })

        // window edit client
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
                    // price: webix.rules.isNotEmpty
                }
            }
        });

        $$("editform").bind($$("visitsGrid"));
        let visTdDoctor = $localStorage.visTdDoctorId;
        if (visTdDoctor != null && visTdDoctor != undefined && visTdDoctor != "") {
            $$("cmbDoctorInList").setValue(visTdDoctor);
            $$("cmbDoctorInList").refresh();
        }
    });

    // save row visit
    $scope.saveRow = function () {
        let id = $scope.visitId;
        let data = $$("editform").getValues();
        data.author = $localStorage.currentUser.login;
        if (id === null || id === undefined || id === "") {
            let url = "/api/"+version_api+"/visits/";
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
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        $scope.serverValidation = false;
                        console.log("Fail. Add visit - " + id)
                    }
                });
            $$("editform").bind($$("visitsGrid"));
        } else {
            let url = "/api/"+version_api+"/visits/" + id;
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
    }

    // delete row visit
    $scope.deleteRow = function () {
        let id = $$("visitsGrid").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление приема",
            text: "Вы уверены, что хотите удалить прием?",
            callback: function (result) {
                if (result) {
                    let url = "/api/"+version_api+"/visits/" + id;
                    webix.ajax().headers($localStorage.headers.value)
                        .del(url, JSON.stringify($$('editform').getValues()), {
                            success: function (text, data, XmlHttpRequest) {
                                $$("visitsGrid").remove(id);
                                $scope.getAppointmentsList();
                            },
                            error: function (text, data, XmlHttpRequest) {
                                $scope.checkAuth(XmlHttpRequest);
                                console.log("Fail. Delete visit - " + id)
                            }
                        });
                }
            }
        });
    }

    // add row visit
    $scope.addRow = function () {
        $$("visitsGrid").clearSelection();
        $$("editform").clear();
        $$("editform").clearValidation();
        $$("gridServices").clearAll();
        $scope.visitId = null;
        $$("cmbService").setValue(null);
        $$("infoVisit").setValue("service");
        $$("editwin").show();
        $$("cmbDoctorOnForm").setValue($$("cmbDoctorInList").getValue());
    }

    // save row client
    $scope.saveClient = function () {
        let data = $$("editformClient").getValues();
        let id = data.id;
        data.author = $localStorage.currentUser.login;
        if (!id) {
            let url = "/api/"+version_api+"/clients/";
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
                        $$("editformClient").markInvalid("name");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Add client - " + id)
                    }
                });
        } else {
            let url = "/api/"+version_api+"/clients/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $scope.serverValidation = true;
                        $scope.reloadComboClients();
                        $scope.getInfoClient();
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editformClient").markInvalid("name");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Update client - " + id)
                    }
                });
        }
    }

    // insert сlient
    $scope.insertClient = function () {
        $$("editformClient").clear();
        $$("editformClient").clearValidation();
        $scope.clientId = null;
        $$("infoTabs").setValue("generalInfo");
        $$("editwinClient").show();

    }

    // update сlient
    $scope.updateClient = function () {
        $$("editformClient").clear();
        $$("editformClient").clearValidation();
        $scope.clientId = $$("cmbClient").getValue();
        $scope.getHistoryVisits();
        $$("infoTabs").setValue("generalInfo");
        $$("editwinClient").show();
        $$("editformClient").setValues($scope.getAllInfoClientById());
    }

    // $$("editformClient").bind($$("cmbClient"));
    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    }
}