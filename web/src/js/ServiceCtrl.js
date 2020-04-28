'use strict';
angular
    .module('app')
    .controller('ServiceCtrl', ServiceCtrl)

function ServiceCtrl($http, $location, $localStorage, $scope, uiGridConstants, i18nService, $uibModal, dialogs, $route, $window) {
    var version_api = "v1";
    
    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';
    $scope.doctorId = "";

    $scope.colums = [{
            id: "createdDateLabel",
            header: 'Дата создания',
            width: 120
        },
        {
            id: "label",
            header: ['Наименование услуги',
                {
                    content: 'textFilter'
                }
            ],
            width: 570,
            sort: 'string'
        },
        {
            id: "price",
            header: 'Стоимость услуги, руб',
            width: 180
        },
        {
            id: "doctorPercentLabel",
            header: [{
                    text: "Вознаграждение доктору",
                    colspan: 2
                },
                "Процент"
            ],
            width: 90
        },
        {
            id: "doctorSumLabel",
            header: ["", "Сумма"],
            width: 90
        },
        {
            id: "categoryLabel",
            header: ['Категория услуги',
            {
                content: 'textFilter'
            }],
            width: 150,
            sort: 'string'
        }
    ]

    $scope.getPersonalRateOfService = function () {
        var dataOfPersonalRate = [];
        var url = "";
        var id = $scope.serviceId;
        if (id != null && id != "") {
            url = "/api/"+version_api+"/services/" + id + "/service";
            webix.ajax().headers($localStorage.headers.value).sync().get(url, {
                success: function (text, data, XmlHttpRequest) {
                    dataOfPersonalRate = text;
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    console.log("FAIL=" + text)
                }
            });
        }
        $$("gridPersonalRate").clearAll();
        $$("gridPersonalRate").parse(dataOfPersonalRate);
    };

    $scope.clearPersonalRateComponents = function () {
        $$("cmbDoctorId").setValue(null);
        $$("newPrice").setValue("");
        $$("newDoctorPay").setValue("");
        $$("newDoctorPayType").setValue(null);
    };

    $scope.addPersonalRateOfService = function () {
        var url = "";
        var id = $scope.serviceId;
        console.log("$$(editform).validate()-"+$$("editform").validate())
        if (id === null || id === "" || id === undefined && $$("editform").validate()) {
            $scope.saveRow();
            if ($scope.serverValidation) {
                id = $scope.serviceId;
            }
        }
        if (id != null && id != "" && id != undefined && $$("editform").validate()) {
            if ($scope.getPersonalRate() != null) {
                url = "/api/"+version_api+"/services/" + id + "/personalRate/";
                webix.ajax().headers($localStorage.headers.value).sync().post(url, $scope.getPersonalRate(), {
                    success: function (text, data, XmlHttpRequest) {
                        $scope.getPersonalRateOfService();
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                        console.log("FAIL=" + text)
                    }
                });
            } else {
                webix.alert(" Не заполнены обязательные поля блока \"Персональная ставка\"");
            }
        } else {
            webix.alert(" Ошибка сохранения услуги ");
        }
    };

    $scope.deletePersonalRateOfService = function () {
        var dataOfPersonalRate = [];
        var url = "";
        var id = $scope.serviceId;
        var selectedPersonalRate = $$("gridPersonalRate").getSelectedItem();

        if (selectedPersonalRate === null || selectedPersonalRate === "" || selectedPersonalRate === undefined) {
            webix.alert(" Не выбрана ставка для удаления ");
        } else if (id != null && id != "" && id != undefined) {
            url = "/api/"+version_api+"/services/" + id + "/personalRate/";
            webix.ajax().headers($localStorage.headers.value).sync().del(url, JSON.stringify(selectedPersonalRate), {
                success: function (text, data, XmlHttpRequest) {
                    $scope.getPersonalRateOfService();
                },
                error: function (text, data, XmlHttpRequest) {
                    $scope.checkAuth(XmlHttpRequest);
                    webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ");
                    console.log("FAIL=" + text)
                }
            });
        }
    };

    $scope.getPersonalRate = function () {
        var personalRate = new Object();
        if ($$("cmbDoctorId").getValue() === null || $$("cmbDoctorId").getValue() === undefined || $$("cmbDoctorId").getValue() === "" ||
            $$("newPrice").getValue() === null || $$("newPrice").getValue() === undefined || $$("newPrice").getValue() === "" ||
            $$("newDoctorPay").getValue() === null || $$("newDoctorPay").getValue() === undefined || $$("cmbDoctorId").getValue() === "" ||
            $$("newDoctorPayType").getValue() === null || $$("newDoctorPayType").getValue() === undefined || $$("newDoctorPayType").getValue() === "") {
            return null;
        } else {
            personalRate.doctorId = $$("cmbDoctorId").getValue();
            personalRate.price = $$("newPrice").getValue();
            personalRate.doctorPay = $$("newDoctorPay").getValue();
            personalRate.doctorPayType = $$("newDoctorPayType").getValue();
            return JSON.stringify(personalRate);
        }
    }

    webix.ready(function () {
        webix.ui({
            view: "window",
            id: "editwin",
            head: "Услуга",
            modal: true,
            position: "center",
            width: 650,
            autoheight: false,
            body: {
                view: "form",
                id: "editform",
                complexData: true,
                elements: [{
                        view: "text",
                        label: "Наименование услуги",
                        name: "label",
                        inputWidth: 610,
                        labelWidth: 200,
                        invalidMessage: "Поле обязательно к заполнению"
                    },
                    {
                        cols: [{
                                view: "text",
                                label: "Стоимость услуги",
                                name: "price",
                                inputWidth: 311,
                                labelWidth: 200,
                                invalidMessage: ""
                            },
                            {
                                view: "label",
                                label: " руб",
                                css: "margin_col"
                            }
                        ]
                    },
                    {
                        cols: [{
                                view: "text",
                                label: "Ставка доктора",
                                name: "doctorPay",
                                inputWidth: 311,
                                labelWidth: 200,
                            },
                            {
                                view: "radio",
                                name: "doctorPayType",
                                labelWidth: 201,
                                css: "margin_col",
                                options: [{
                                        id: "percent",
                                        value: "%"
                                    },
                                    {
                                        id: "sum",
                                        value: "Сумма"
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        view: "radio",
                        label: "Категория",
                        name: "category",
                        labelWidth: 120,
                        invalidMessage: "Выберите категорию услуги",
                        options: [{
                                id: "consult",
                                value: "Консультация"
                            },
                            {
                                id: "ultra",
                                value: "УЗИ"
                            },
                            {
                                id: "analyzes",
                                value: "Анализ"
                            },
                            {
                                id: "mazok",
                                value: "Мазок"
                            },
                            {
                                id: "pcr",
                                value: "ПЦР"
                            }
                        ]
                    },
                    {
                        view: "template",
                        type: "section",
                        template: "<span class='lb_template'>Персональная ставка доктора</span>"
                    },
                    {
                        id: "personalRate",
                        view: "layout",
                        margin: 8,
                        padding: 10,
                        css: "layout_without_border",
                        rows: [{
                                cols: [{
                                        view: "combo",
                                        label: "Доктор",
                                        id: "cmbDoctorId",
                                        labelWidth: 95,
                                        options: JSON.parse(webix.ajax().headers($localStorage.headers.value).sync().get("/api/"+version_api+"/doctors").response)
                                    },
                                    {
                                        gravity: 0.55,
                                        cols: [{
                                                view: "button",
                                                type: "form",
                                                value: "AddPersonalRate",
                                                label: "Добавить",
                                                click: function () {
                                                    $scope.addPersonalRateOfService();
                                                }
                                            },
                                            {
                                                view: "button",
                                                value: "deletePersonalRate",
                                                label: "Удалить",
                                                click: function () {
                                                    $scope.deletePersonalRateOfService();
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
                                                label: "Стоимость",
                                                id: "newPrice",
                                                labelWidth: 95
                                            },
                                            {
                                                view: "text",
                                                label: "Ставка",
                                                id: "newDoctorPay",
                                                css: "margin_col_stavka",
                                                inputWidth: 169,
                                                labelWidth: 70,
                                            }
                                        ]
                                    },
                                    {
                                        gravity: 0.55,
                                        view: "radio",
                                        id: "newDoctorPayType",
                                        css: "margin_col",
                                        options: [{
                                                id: "percent",
                                                value: "%"
                                            },
                                            {
                                                id: "sum",
                                                value: "Сумма"
                                            }
                                        ]
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
                                id: "gridPersonalRate",
                                columns: [{
                                        id: "label",
                                        header: "ФИО доктора",
                                        fillspace: true
                                    },
                                    {
                                        id: "price",
                                        header: "Прайс",
                                        width: 100
                                    },
                                    {
                                        id: "doctorPay",
                                        header: "Ставка доктора",
                                        width: 100
                                    },
                                    {
                                        id: "doctorPayTypeLabel",
                                        header: "Тип ставки",
                                        width: 100
                                    }
                                ],
                                on: {
                                    onBeforeLoad: function () {
                                        this.showOverlay("Загрузка...");
                                    },
                                    onAfterLoad: function () {
                                        if (!this.count())
                                            this.showOverlay("Нет добавленных персональных ставок для доктора");
                                        else
                                            this.hideOverlay();
                                    }
                                },
                                data: []
                            }
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
                                    $$("gridPersonalRate").clearAll();
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
                    label: webix.rules.isNotEmpty,
                    price: webix.rules.isNotEmpty,
                    category:webix.rules.isNotEmpty
                }
            }
        });

        webix.ui({
            id: "table",
            container: "priceListGrid",
            view: "datatable",
            autoheight: true,
            width: 1200,
            editable: true,
            editaction: "custom",
            scroll: false,
            select: "row",
            data: webix.ajax().headers($localStorage.headers.value).get("/api/"+version_api+"/services"),
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
                    $scope.serviceId = $$("table").getSelectedId();
                    $scope.getPersonalRateOfService();
                    $scope.clearPersonalRateComponents();
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
                }
            }
        });
        $$("editform").bind($$("table"));
    });

    $scope.saveRow = function () {
        var id = $scope.serviceId;
        //$$("table").getSelectedId();
        var data = $$("editform").getValues();
        data.author = $localStorage.currentUser.login;
        if (!id) {
            var url = "/api/"+version_api+"/services/";
            console.log("add service data="+JSON.stringify(data));
            webix.ajax().headers($localStorage.headers.value).sync()
                .post(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serviceId = JSON.parse(text).id;
                        $scope.serverValidation = true;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("label");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        $scope.serviceId = null;
                        console.log("Fail. Add usluga - " + id)
                    }
                });
            $$("editform").bind($$("table"));
        } else {
            var url = "/api/"+version_api+"/services/" + id;
            webix.ajax().headers($localStorage.headers.value).sync()
                .put(url, JSON.stringify(data), {
                    success: function (text, data, XmlHttpRequest) {
                        $$("table").parse(text);
                        $scope.serverValidation = true;
                        $scope.serviceId = JSON.parse(text).id;
                    },
                    error: function (text, data, XmlHttpRequest) {
                        $scope.checkAuth(XmlHttpRequest);
                        $$("editform").markInvalid("label");
                        webix.alert(" " + JSON.parse(text).code + ": \"" + JSON.parse(text).message + "\" ")
                        $scope.serverValidation = false;
                        console.log("Fail. Update usluga - " + id)
                    }
                });
        }
    }

    $scope.deleteRow = function () {
        var id = $$("table").getSelectedId();
        if (!id) return;
        webix.confirm({
            title: "Удаление услуги",
            text: "Вы уверены, что хотите удалить услугу?",
            callback: function (result) {
                if (result) {
                    var url = "/api/"+version_api+"/services/" + id;
                    webix.ajax().headers($localStorage.headers.value)
                        .del(url, JSON.stringify($$('editform').getValues()), {
                            success: function (text, data, XmlHttpRequest) {
                                $$("table").remove(id);
                            },
                            error: function (text, data, XmlHttpRequest) {
                                $scope.checkAuth(XmlHttpRequest);
                                console.log("Fail. Delete usluga - " + id)
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
        $scope.serviceId = null;
        $scope.clearPersonalRateComponents();
        $$("gridPersonalRate").clearAll();
        $$("editwin").show();

    };

    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    };
}