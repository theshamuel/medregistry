'use strict';
angular
    .module('app')
    .controller('WorkspaceOperatorCtrl', WorkspaceOperatorCtrl)

function WorkspaceOperatorCtrl($http, $location, $localStorage, $scope, $rootScope, uiGridConstants, i18nService, $uibModal, dialogs, $route, $window) {
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';

    $scope.columns = [{
            id: "timeLabel",
            header: 'Время приема',
            width: 70
        }, {
            id: "contractNum",
            header: '№ договора',
            width: 110
        },
        {
            id: "doctorLabel",
            header: ['ФИО доктора',
                {
                    content: 'textFilter'
                }
            ],
            width: 155
        },
        {
            id: "clientLabel",
            header: ['ФИО пациента',
                {
                    content: 'textFilter'
                }
            ],
            width: 180
        },
        {
            id: "totalSum",
            header: 'К оплате',
            width: 90
        },
    ];

    webix.Date.startOnMonday = true;
    var formatDate = webix.Date.dateToStr("%d.%m.%y");
    var formatDay = webix.Date.dateToStr("%D");
    var today = new Date();
    var tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
    var version_api = "v1";
    $scope.visitId = "";

    var now = function () {

        var minutes = "";
        var hours = "";
        var ctoday = new Date();
        if (ctoday.getHours() < 10)
            hours = "0" + ctoday.getHours();
        else
            hours = ctoday.getHours();
        if (ctoday.getMinutes() < 10)
            minutes = "0" + ctoday.getMinutes();
        else
            minutes = ctoday.getMinutes();
        return hours + ":" + minutes;
    };

    var getFreeAppointmets = function () {
        if ($$("dateEvent").getValue() != null && $$("dateEvent") != undefined &&
            $$("cmdDoctor").getValue() != null && $$("cmdDoctor") != undefined && $$("cmdDoctor").getValue() != "") {
            var url = "/api/"+version_api+"/appointments/freetime?doctorId=" + $$("cmdDoctor").getValue() + "&dateEvent=" + $$("dateEvent").getValue().toJSON() + "&id";
            $$("freeTimeEvent").define("options", JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data) {
                    console.log("success");
                },
                error: function (text, data) {
                    console.log("error");
                    webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                }
            }).response));
            $$("freeTimeEvent").refresh();
        } else {
            $$("freeTimeEvent").define("options", []);
        }
    };

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

    var getAppointmetsByDoctor = function () {
        today = $$("calendar").getSelectedDate();
        tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);

        $$("labelToday").define("label", formatDate(today) + " (" + formatDay(today) + ")")
        $$("labelToday").refresh();
        $$("labelTomorrow").define("label", formatDate(tomorrow) + " (" + formatDay(tomorrow) + ")")
        $$("labelTomorrow").refresh();

        if ($$("cmbDoctor").getValue() != null && $$("cmbDoctor") != undefined && $$("cmbDoctor").getValue() != "") {
            var doctorId = $$("cmbDoctor").getValue();
            $localStorage.wspDoctor = doctorId;
            var urlToday = "/api/"+version_api+"/appointments/schedule?doctorId=" + doctorId + "&dateEvent=" + today.toJSON();
            var urlTomorrow = "/api/"+version_api+"/appointments/schedule?doctorId=" + doctorId + "&dateEvent=" + tomorrow.toJSON();
            var dataListToday = [];
            var dataListTomorrow = [];

            webix.ajax().headers($localStorage.headers.value).sync().get(urlToday, {
                success: function (text, data, XmlHttpRequest) {
                    dataListToday = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    //  $scope.checkAuth(XmlHttpRequest);
                    console.log("error");
                }
            })
            $$('listToday').clearAll();
            $$('listToday').define("data", dataListToday);
            $$('listToday').refresh();

            webix.ajax().headers($localStorage.headers.value).sync().get(urlTomorrow, {
                success: function (text, data, XmlHttpRequest) {
                    dataListTomorrow = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    //    $scope.checkAuth(XmlHttpRequest);
                    console.log("error");
                }
            })
            $$('listTomorrow').clearAll();
            $$('listTomorrow').define("data", dataListTomorrow);
            $$('listTomorrow').refresh();

        } else {
            $$('listToday').refresh();
            $$('listTomorrow').refresh();
        }

    }

    var refreshComingVisits = function () {
        $$('comingVisits').clearAll();
        $$('comingVisits').define("data", JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/appointments/all?dateEvent=" + new Date().toJSON() + "&timeEvent=" + now()).response));
        $$('comingVisits').refresh();

    }

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

    $scope.getServicesOfVisit = function () {
        var dataOfServices = [];
        var url = "";
        var id = $scope.visitId;
        var error = false;
        if (id != null && id != undefined && id != "") {
            url = "/api/"+version_api+"/visits/" + id + "/services";
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
    };

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
    $scope.printCardClient = function () {
        var dateReport = new Date();
        var clientId = $$("cmbClient").getValue();
        var doctorId = $$("cmbDoctorOnForm").getValue();
        console.log("doctor=" + doctorId);
        var url = "/api/"+version_api+"/reports/file/clientCard/" + clientId + "/" + doctorId + "/" + dateReport.toJSON();
        webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
            $rootScope.saveByteArray([data], 'Карта_пациента-' + dateReport.toJSON() + '.xls');
        });
    }

    $scope.reloadGridVisit = function () {
        var dataVisits = [];
        var visTdDoctorId = "-1";

        var url = "/api/"+version_api+"/visits/" + visTdDoctorId + "/" + today.toJSON();
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
    };

    $scope.addServicesOfVisit = function () {
        var url = "";
        var id = $scope.visitId;
        var serverValid = true;
        if ((id === null || id === "" || id === undefined) && $$("editformvisit").validate()) {
            $scope.saveRowVisit();
            id = $scope.visitId;
            serverValid = $scope.serverValidation;
        }
        if ($$("editformvisit").validate() && serverValid && id != null && id != "" && id != undefined && $$("cmbService").getValue() != null && $$("cmbService").getValue() != undefined && $$("cmbService").getValue() != "") {
            url = "/api/"+version_api+"/visits/" + id + "/services/" + $$("cmbService").getValue() + "?discount=";
            if ($$("discount").getValue() != "" && $$("discount").getValue() != undefined && $$("discount").getValue() != null)
                url = url + $$("discount").getValue();
            console.log("addServicesOfVisit url=" + url)
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

    $scope.deleteServicesOfVisit = function () {
        var dataOfServices = [];
        var url = "";
        var id = $scope.visitId;
        var servicesId = $$("gridServices").getSelectedId();
        console.log("$$().servicesId()=" + servicesId);
        if (servicesId === null || servicesId === "" || servicesId === undefined) {
            webix.alert(" Не выбрана услуга для удаления ");
        } else if (id != null && id != "" && id != undefined) {
            url = "/api/"+version_api+"/visits/" + id + "/services/" + servicesId;
            console.log("URL=" + url);
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

        // window edit appointment
        webix.ui({
            view: "window",
            id: "editwinapp",
            head: "Запись на прием",
            modal: true,
            position: "center",
            width: 650,
            autoheight: false,
            body: {
                view: "form",
                id: "editformapp",
                complexData: true,
                elements: [
                    {
                        cols: [
                            {
                                view: "layout",
                                rows: [{}]
                            },
                            {
                                gravity: 0.37,
                                view: "checkbox",
                                label: "Двойная запись",
                                name: "isDoubleAppointment",
                                labelWidth: 147,
                                value: 0
                            }
                        ]
                    }, 
                    {
                        view: "combo",
                        label: "ФИО доктора",
                        id: "cmdDoctor",
                        name: "doctorId",
                        labelWidth: 130,
                        on: {
                            onChange: getFreeAppointmets
                        },
                        options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/doctors").response),
                        invalidMessage: ""
                    },
                    {
                        cols: [

                            {
                                view: "datepicker",
                                editable: true,
                                label: "Дата приема",
                                id: "dateEvent",
                                name: "dateEvent",
                                labelWidth: 130,
                                invalidMessage: "",
                                on: {
                                    onChange: getFreeAppointmets
                                }
                            },
                            {
                                id: "freeTimeEvent",
                                view: "combo",
                                label: "Время приема",
                                name: "timeEvent",
                                stringResult: true,
                                labelWidth: 130,
                                inputWidth: 297,
                                css: "margin_appoint_col",
                                invalidMessage: ""
                            }
                        ]
                    },
                    {
                        cols: [{
                                view: "text",
                                label: "ФИО пациента",
                                name: "client",
                                labelWidth: 130,
                            },
                            {
                                view: "text",
                                label: "Телефон",
                                name: "phone",
                                labelWidth: 130,
                                inputWidth: 297,
                                css: "margin_appoint_col"
                            }
                        ]
                    },
                    {
                        view: "text",
                        label: "Предварительная услуга",
                        name: "service",
                        labelWidth: 210
                    },
                    {
                        cols: [{
                                view: "button",
                                type: "form",
                                value: "Save",
                                label: "Сохранить",
                                click: function () {
                                    if ($$("editformapp").validate()) {
                                        $scope.saveRowApp();
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
                    dateEvent: webix.rules.isNotEmpty,
                    timeEvent: webix.rules.isNotEmpty,
                    doctorId: webix.rules.isNotEmpty
                }
            }
        });

        // combo doctor
        webix.ui({
            view: "combo",
            container: "combo_doc",
            id: "cmbDoctor",
            label: "ФИО доктора",
            name: "doctor",
            labelWidth: 120,
            inputWidth: 400,
            options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/doctors").response),
            on: {
                onChange: getAppointmetsByDoctor
            }
        });

        // layout today
        webix.ui({
            view: "layout",
            container: "today",
            width: 300,
            height: 625,
            css: "form_app",
            rows: [{
                    view: "label",
                    id: "labelToday",
                    height: 30,
                    align: "center",
                    css: "lb_template_list"
                },
                {
                    view: "list",
                    container: "listToday",
                    id: "listToday",
                    css: "list_app",
                    type: {
                        itemTemplateStart: function (obj) {
                            if (obj.stateLabel != null && obj.stateLabel != undefined && obj.stateLabel.indexOf("Свободно") > -1)
                                return "<div class='list_cell_free'>"
                            else
                                return "<div class='list_cell'>"
                        },

                        templateStart: "{common.itemTemplateStart()}",

                        template: "<div><div style='display:inline-block;'><span class='list_time'>#timeEvent#</span></div> <div style='display:inline-block; width:75%; text-align:right;'><span class='list_state'>#stateLabel#</span></div></div>",
                        templateEnd: "</div>",
                    },
                    data: [],
                    on: {
                        onAfterLoad: function () {
                            if ($$("listToday").count() > 0) {
                                this.hideOverlay();
                            } else {
                                webix.extend(this, webix.OverlayBox);
                                this.showOverlay("<div class='lb_template'>На дату отсутствуют<br/> записи на прием<br/></div>");

                            }
                        }
                    }
                }
            ],
        });

        // layout tomorrow
        webix.ui({
            view: "layout",
            container: "tomorrow",
            width: 300,
            height: 625,
            css: "form_app",
            rows: [{
                    view: "label",
                    id: "labelTomorrow",
                    height: 30,
                    align: "center",
                    css: "lb_template_list"
                },
                {
                    view: "list",
                    container: "listTomorrow",
                    id: "listTomorrow",
                    css: "list_app",
                    type: {
                        itemTemplateStart: function (obj) {
                            if (obj.stateLabel.indexOf("Свободно") > -1)
                                return "<div class='list_cell_free'>"
                            else
                                return "<div class='list_cell'>"
                        },

                        templateStart: "{common.itemTemplateStart()}",
                        template: "<div><div style='display:inline-block;'><span class='list_time'>#timeEvent#</span></div> <div style='display:inline-block; width:75%; text-align:right;'><span class='list_state'>#stateLabel#</span></div></div>",
                        templateEnd: "</div>",
                    },
                    data: [],
                    on: {
                        onAfterLoad: function () {
                            if ($$("listTomorrow").count() > 0) {
                                this.hideOverlay();
                            } else {
                                webix.extend(this, webix.OverlayBox);
                                this.showOverlay("<div class='lb_template'>На дату отсутствуют<br/> записи на прием</div>");

                            }
                        }
                    }
                }
            ]
        });

        // layout combo doctor + list comingVisits
        webix.ui({
            view: "layout",
            container: "combDoctor",
            width: 300,
            height: 625,
            css: "without_border",
            rows: [{
                    view: "calendar",
                    id: "calendar",
                    date: new Date(),
                    css: "without_border",
                    events: webix.Date.isHoliday,
                    weekHeader: true,
                    on: {
                        onChange: getAppointmetsByDoctor
                    }
                },
                {
                    view: "list",
                    container: "comingVisits",
                    id: "comingVisits",
                    css: "list_app_doctor",
                    on:{
						onItemDblClick: function(id,e, node){
                            var url = "/api/"+version_api+"/appointments/isHere/"+id;
                            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                                success: function (text, data, XmlHttpRequest) {
                                    console.log("success");
                                    refreshComingVisits();
                                },
                                error: function (text, data, XmlHttpRequest) {
                                    $scope.checkAuth(XmlHttpRequest);
                                    console.log("fail");
                                }
                            });
                          
                        },
					},
                    type: {
                        itemTemplate: function (obj) {
                            if (obj.isHere === true)
                                return "<div class='list_cell_free'><div><span class='lb_template'> "+obj.timeEvent+"</span> - "+obj.client+" </div><div style='text-align:right;'>"+obj.doctorLabel+"</div></div>"
                            else
                                return "<div class='list_cell_coming_app'><div><span class='lb_template'> "+obj.timeEvent+"</span> - "+obj.client+" </div><div style='text-align:right;'>"+obj.doctorLabel+"</div></div>"
                        },
                        height: 80,
                        template: "{common.itemTemplate()}",
                    },
                    data: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/appointments/all?dateEvent=" + today.toJSON() + "&timeEvent=" + now()).response),
                    ready: function () {
                        if (!this.count()) { //if no data is available
                            webix.extend(this, webix.OverlayBox);
                            this.showOverlay("<div class='lb_template'>На сегодня отсутствуют<br/> записи на прием<br/></div>&#9785;");
                        }
                    }

                }
            ]
        });
        // grid visits
        webix.ui({
            id: "visitsGrid",
            container: "visitsGrid",
            view: "datatable",
            width: 600,
            height: 625,
            editable: true,
            scroll: "y",
            editaction: "custom",
            select: "row",
            datatype: "json",
            columns: $scope.columns,
            on: {
                onItemDblClick: function () {
                    $scope.visitId = $$("visitsGrid").getSelectedId();
                    $$("editformvisit").clearValidation();
                    $$("gridServices").clearAll();
                    $$("cmbService").setValue(null);
                    $$("infoVisit").setValue("service");
                    $$("editwinvisit").show();
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
                }
            }
        });

        // window edit visit
        webix.ui({
            view: "window",
            id: "editwinvisit",
            head: "Прием",
            modal: true,
            position: "center",
            width: 700,
            autoheight: false,
            body: {
                view: "form",
                id: "editformvisit",
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
                                                                    console.log("CALL ADD")
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
                                                        // gravity: 1.25,
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
                                    if ($$("editformvisit").validate()) {
                                        $scope.saveRowVisit();
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

        $$('calendar').selectDate(new Date());
        var wspDoctor = $localStorage.wspDoctor;
        if (wspDoctor != null && wspDoctor != undefined && wspDoctor != "") {
            $$("cmbDoctor").setValue(wspDoctor);
            $$("cmbDoctor").refresh();
        }
        $scope.reloadGridVisit();
        $$("editformvisit").bind($$("visitsGrid"));
    });

    $scope.saveRowApp = function () {
        var data = $$("editformapp").getValues();
        data.author = $localStorage.currentUser.login;
        var url = "/api/"+version_api+"/appointments/";
        webix.ajax().headers($localStorage.headers.value).sync()
            .post(url, JSON.stringify(data), {
                success: function (text, data, XmlHttpRequest) {
                    $scope.serverValidation = true;
                    getAppointmetsByDoctor();
                    refreshComingVisits()
                    $$('listToday').refresh();
                    $$('listTomorrow').refresh();
                },
                error: function (text, data, XmlHttpRequest) {
                    webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                    console.log("Fail. Add appointment - " + id)
                }
            });
    }

    // save row visit
    $scope.saveRowVisit = function () {
        var id = $scope.visitId;
        var data = $$("editformvisit").getValues();
        data.author = $localStorage.currentUser.login;
        if (id === null || id === undefined || id === "") {
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
                        // $$("editformvisit").markInvalid("label");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        $scope.serverValidation = false;
                        console.log("Fail. Add visit - " + id)
                    }
                });
            $$("editformvisit").bind($$("visitsGrid"));
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
                        $$("editformvisit").markInvalid("label");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Update visit - " + id)
                    }
                });
        }
    }

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


    $scope.addRowApp = function () {
        $$("editformapp").clear();
        $$("editformapp").clearValidation();
        getFreeAppointmets();
        $$("editwinapp").show();
    }

    // add row visit
    $scope.addRowVisit = function () {
        $$("visitsGrid").clearSelection();
        $$("editformvisit").clear();
        $$("editformvisit").clearValidation();
        $$("gridServices").clearAll();
        $scope.visitId = null;
        $$("cmbService").setValue(null);
        $$("infoVisit").setValue("service");
        $$("editwinvisit").show();
    }

    $scope.logout = function () {
        $localStorage.currentUser = null;
        $scope.userLogin = '';
        $scope.userPassword = '';
        $http.defaults.headers.common.Authorization = '';
        $window.location.href = '/login';
    }

    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    }
}