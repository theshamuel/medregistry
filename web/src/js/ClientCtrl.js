'use strict';
angular
    .module('app')
    .controller('ClientCtrl', ClientCtrl)

function ClientCtrl($http, $location, $localStorage, $scope, $rootScope, uiGridConstants, i18nService, $uibModal, dialogs, $route, $window) {
    let version_api = "v1";

    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';

    $scope.colums = [{
        id: "createdDateLabel",
        header: 'Дата создания',
        width: 150
    },
    {
        id: "surname",
        header: 'Фамилия',
        width: 350,
        sort: 'string'
    },
    {
        id: "name",
        header: 'Имя',
        width: 175
    },
    {
        id: "middlename",
        header: 'Отчество',
        width: 175
    },
    {
        id: "passportLabel",
        header: 'Серия/номер паспорта',
        width: 200
    },
    {
        id: "phone",
        header: 'Телефон',
        width: 150
    }
    ]

    $scope.getHistoryVisits = function () {
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
    };

    $scope.printCardClient = function () {
        let dateReport = new Date();
        let clientId = $scope.clientId;
        let doctorId = "-1";
        let url = "/api/" + version_api + "/reports/file/clientCard/" + clientId + "/" + doctorId + "/" + dateReport.toJSON();
        webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
            $rootScope.saveByteArray([data], 'Карта_пациента-' + dateReport.toJSON() + '.xls');
        });
    }

    $scope.printNalogSpravka = function () {
        let fioPayer = $$("fioOfPayerID").getValue();
        let familyRelation = $$("familyRelationID").getValue();
        let payerGenderID = $$("genderPayerID").getValue();

        if ($$("editFormOfNalogSpravka").validate() ||
            $$("nalogSpravkaDateFromID").getValue() == null ||
            $$("nalogSpravkaDateToID").getValue() == null ||
            $$("nalogSpravkaSelfPayerID").getValue() == 1 &&
            (fioPayer == "" || fioPayer == null ||
                familyRelation == "" || familyRelation == null || payerGenderID == "" )) {
            webix.alert("Не заполнены обязательные поля!");
            return
        }

        let dateReport = new Date();
        let clientId = $scope.clientId;
        let clientSurname = $scope.clientSurname;
        let dateFrom = $$("nalogSpravkaDateFromID").getValue().toJSON();
        let dateTo = $$("nalogSpravkaDateToID").getValue().toJSON();
        let isSelfPayer = true;
        if ($$("nalogSpravkaSelfPayerID").getValue() == 1) {
            isSelfPayer = false
        }
        let url = "/api/v2/reports/file/reportNalogSpravka/" +
            clientId +
            "/" + dateFrom +
            "/" + dateTo +
            "/" + fioPayer +
            "/" + payerGenderID +
            "/" + familyRelation +
            "/" + isSelfPayer +
            "/report.xlsx";
        webix.ajax().response("blob").headers($localStorage.headers.value).get(url, function (text, data) {
            $rootScope.saveByteArray([data], 'Налоговая_справка-' + clientSurname + '_' + dateReport.toJSON() + '.xlsx');
            $$("nalogSpravkaSelfPayerID").setValue(0);
            $$("editFormOfNalogSpravka").clear();
            $$("editFormOfNalogSpravka").clearValidation();
            $$("editFormOfNalogSpravka").getTopParentView().hide();
        });

    }

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
                    gravity: 1.4,
                    view: "text",
                    label: "ФИО пациента",
                    id: "filterClient",
                    labelWidth: 150,
                    inputWidth: 500,
                },
                {
                    gravity: 0.95,
                    view: "text",
                    label: "Паспортные данные",
                    id: "filterPassport",
                    labelWidth: 175,
                    inputWidth: 335
                },
                {
                    gravity: 0.7,
                    view: "text",
                    label: "Телефон",
                    id: "filterPhone",
                    inputWidth: 225,
                },
                {
                    gravity: 0.25,
                    cols: [{
                        view: "button",
                        type: "image",
                        image: "../img/funnel_add_24.png",
                        value: "Apply",
                        click: function () {
                            $$('table').clearAll();
                            let url = $scope.getTableURI(15, 0);
                            $$('table').load("addHeaders->" + url);
                            $$('table').refresh();
                            $$('table').setPage(0);
                        }
                    },
                    {
                        view: "button",
                        type: "image",
                        image: "../img/funnel_delete_24.png",
                        value: "Reset",
                        click: function () {
                            $$('filterPassport').setValue("");
                            $$('filterPhone').setValue("");
                            $$('filterClient').setValue("");
                            $$('table').clearAll();
                            let url = $scope.getTableURI(15, 0);
                            $$('table').load("addHeaders->" + url);
                            $$('table').refresh();
                            $$('table').setPage(0);
                        }
                    },
                    ]
                }
                ]
            }]
        });
        //
        webix.ui({
            view: "window",
            id: "printNalogSpravke",
            head: "Налоговая справка",
            modal: true,
            position: "center",
            width: 750,
            autoheight: false,
            body: {
                view: "form",
                id: "editFormOfNalogSpravka",
                complexData: true,
                elements: [{
                    view: "checkbox",
                    id: "nalogSpravkaSelfPayerID",
                    label: "Налогоплательщик другое лицо (родственник пациента)",
                    labelWidth: 400,
                    on: {
                        onChange: function () {
                            if (this.getValue() == 1) {
                                $$("fioOfPayerID").enable();
                                $$("familyRelationID").enable();
                                $$("genderPayerID").enable();
                            } else {
                                $$("fioOfPayerID").setValue("");
                                $$("fioOfPayerID").disable();
                                //
                                $$("familyRelationID").setValue("");
                                $$("familyRelationID").disable();
                                //
                                $$("genderPayerID").setValue("");
                                $$("genderPayerID").disable();
                            }
                        }
                    },
                    value: 0
                },
                {
                    view: "radio",
                    id: "genderPayerID",
                    name: "genderPayer",
                    disabled: true,
                    value: "",
                    options: [
                        {
                            id: "male",
                            value: "Муж."
                        },
                        {
                            id: "female",
                            value: "Жен."
                        }
                    ],
                },
                {
                    view: "text",
                    label: "ФИО налогоплательщика",
                    id: "fioOfPayerID",
                    name: "fioOfPayer",
                    labelWidth: 200,
                    disabled: true,
                    invalidMessage: "Обязательное поле"
                }, {

                    cells: [{
                        css: "bottom_border_tab_header",
                        id: "main",
                        view: "layout",
                        margin: 20,
                        paddingY: 10,
                        rows: [
                            {
                                view: "combo",
                                label: "Родственная связь с пациентом",
                                id: "familyRelationID",
                                name: "familyRelation",
                                labelWidth: 250,
                                disabled: true,
                                options: JSON.parse('[{"id":"spouse", "value":"супруг(а)"},{"id":"daughter", "value":"дочь"},{"id":"son", "value":"сын"},{"id":"mother", "value":"мать"},{"id":"father", "value":"отец"}]')

                            },
                            {
                                cols: [{
                                    view: "datepicker",
                                    editable: true,
                                    label: "Дата c",
                                    id: "nalogSpravkaDateFromID",
                                    name: "dateFrom",
                                    labelWidth: 75,
                                    width: 255,
                                }, {
                                    view: "datepicker",
                                    editable: true,
                                    label: "Дата по",
                                    id: "nalogSpravkaDateToID",
                                    name: "dateТо",
                                    labelWidth: 75,
                                    width: 255,
                                },
                                ]
                            },
                        ]
                    }]
                },
                {
                    cols: [{
                        view: "button",
                        type: "form",
                        value: "Print",
                        label: "Печатать",
                        click: function () {
                            $scope.printNalogSpravka();
                        }
                    },
                    {
                        view: "button",
                        value: "Cancel",
                        label: "Отмена",
                        click: function () {
                            $$("nalogSpravkaSelfPayerID").setValue(0);
                            $$("editFormOfNalogSpravka").clear();
                            $$("editFormOfNalogSpravka").clearValidation();
                            this.getTopParentView().hide();
                        }
                    }
                    ]
                }
                ],
                rules: {
                    "dateFrom": webix.rules.isNotEmpty,
                    "dateTo": webix.rules.isNotEmpty,
                    "genderPayer": webix.rules.isNotEmpty
                }
            }
        });
        //
        webix.ui({
            view: "window",
            id: "editwin",
            head: "Пациент",
            modal: true,
            position: "center",
            width: 750,
            autoheight: false,
            body: {
                view: "form",
                id: "editform",
                complexData: true,
                elements: [{
                    view: "text",
                    id: "surname",
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
                                        labelWidth: 135,
                                        width: 265,
                                    },
                                    {
                                        view: "button",
                                        type: "form",
                                        value: "printClient",
                                        label: "Налог. справка",
                                        css: "margin_client_col_nalog_spravka",
                                        width: 160,
                                        inputWidth: 150,
                                        click: function () {
                                            $scope.clientSurname = $$("surname").getValue();
                                            $$("printNalogSpravke").show();
                                        }
                                    },
                                    {
                                        view: "button",
                                        type: "form",
                                        value: "printClient",
                                        label: "Карта пациента",
                                        css: "margin_client_col_client_card",
                                        width: 160,
                                        inputWidth: 150,
                                        click: function () {
                                            $scope.printCardClient();
                                        }
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
                                label: "Email",
                                borderless: true,
                                rows: [{
                                    cols: [{
                                        view: "text",
                                        label: "Эл. почта",
                                        name: "email",
                                        labelWidth: 135,
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
                                        inputWidth: 270,
                                        labelWidth: 100
                                    }
                                    ]
                                }]
                            },
                            {
                                view: "layout",
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
                                        labelWidth: 100,
                                        inputWidth: 270,
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
                            }
                        }
                    },
                    {
                        view: "button",
                        value: "Cancel",
                        label: "Отмена",
                        click: function () {
                            this.getTopParentView().hide();
                            let item = $$("table").getSelectedId();
                            if (item != null && item != undefined && item != "") {
                                $$("table").unselectAll();
                                $$("table").select(item);
                            }
                        }
                    }
                    ]
                }
                ],
                rules: {
                    surname: webix.rules.isNotEmpty,
                    passportSerial: webix.rules.isNotEmpty,
                    passportNumber: webix.rules.isNotEmpty
                }
            }
        });

        webix.ui({
            id: "table",
            container: "clientGrid",
            view: "datatable",
            autoheight: true,
            width: 1200,
            editable: true,
            scroll: false,
            editaction: "custom",
            select: "row",
            datafetch: 15,
            datathrottle: 300,
            loadahead: 15,
            data: webix.ajax().headers($localStorage.headers.value).get("/api/" + version_api + "/clients?count=15&start=0"),
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
                    $scope.clientId = $$("table").getSelectedId();

                    $scope.getHistoryVisits();
                    $$("editform").clearValidation();
                    $$("infoTabs").setValue("generalInfo");
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
                    let url = $scope.getTableURI(count, start);
                    let dataGrid = webix.ajax().headers($localStorage.headers.value).get(url);
                    this.parse(dataGrid);
                    return false;
                }
            }
        });
        $$("editform").bind($$("table"));
    })
        ;
    $scope.getTableURI = function (count, start) {
        let url = "/api/" + version_api + "/clients?count=" + count + "&start=" + start + "&filter=";
        if ($$("filterClient").getValue() != null && $$("filterClient").getValue() != "")
            url = url + "surname=" + $$("filterClient").getValue() + ";";
        if ($$("filterPassport").getValue() != null && $$("filterPassport").getValue() != "")
            url = url + "passport=" + $$("filterPassport").getValue() + ";";
        if ($$("filterPhone").getValue() != null && $$("filterPhone").getValue() != "")
            url = url + "phone=" + $$("filterPhone").getValue() + ";";
        return url;
    }
    $scope.saveRow = function () {
        let data = $$("editform").getValues();
        let id = $scope.clientId;
        data.author = $localStorage.currentUser.login;
        if (!id) {
            let url = "/api/" + version_api + "/clients/";
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                        $scope.clientId = JSON.parse(text).id;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("passportSerial");
                        $$("editform").markInvalid("passportNumber");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Add client - " + id)
                    }
                });
            $$("editform").bind($$("table"));
        } else {
            let url = "/api/" + version_api + "/clients/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("passportSerial");
                        $$("editform").markInvalid("passportNumber");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Update client - " + id)
                    }
                });
        }
    }

    $scope.deleteRow = function () {
        let id = $$("table").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление пациента",
            text: "Вы уверены, что хотите удалить пациента?",
            callback: function (result) {
                if (result) {
                    let url = "/api/" + version_api + "/clients/" + id;
                    webix.ajax().headers($localStorage.headers.value)
                        .del(url, JSON.stringify($$('editform').getValues()), {
                            success: function (text, data, XmlHttpRequest) {
                                $$("table").remove(id);
                            },
                            error: function (text, data, XmlHttpRequest) {
                                $scope.checkAuth(XmlHttpRequest);
                                console.log("Fail. Delete client - " + id)
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
        $$("infoTabs").setValue("generalInfo");
        $$("editwin").show();
        $scope.clientId = null;
        $scope.getHistoryVisits();
    }

    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    }
}