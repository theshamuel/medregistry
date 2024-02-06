'use strict';

let app = angular.module('app', ['ngRoute', 'ngStorage', 'ngMessages', 'ngTouch', 'ui.grid', 'ui.grid.edit', 'ui.grid.pagination',
    'ui.grid.autoResize', 'ui.bootstrap', 'schemaForm', 'dialogs.main', 'pascalprecht.translate', 'dialogs.default-translations', 'webix']).config(config).run(run);

function config($routeProvider, $locationProvider) {

    $routeProvider
        .when('/login', {
            templateUrl: 'login.html',
            controller: 'LoginCtrl'
        })
        .when('/about', {
            templateUrl: 'about.html',
            controller: 'AboutCtrl'
        })
        .when('/main', {
            templateUrl: 'main.html',
            controller: 'MainCtrl'
        })
        .when('/users', {
            templateUrl: 'users.html',
            controller: 'UsersCtrl'
        })
        .when('/service', {
            templateUrl: 'service.html',
            controller: 'ServiceCtrl'
        })
        .when('/clients', {
            templateUrl: 'clients.html',
            controller: 'ClientCtrl'
        })
        .when('/company', {
            templateUrl: 'company.html',
            controller: 'CompanyCtrl'
        })
        .when('/doctors', {
            templateUrl: 'doctor.html',
            controller: 'DoctorCtrl'
        })
        .when('/schedule', {
            templateUrl: 'schedule.html',
            controller: 'ScheduleCtrl'
        })
        .when('/visits', {
            templateUrl: 'visits.html',
            controller: 'VisitsCtrl'
        })
        .when('/visits-today', {
            templateUrl: 'visits-today.html',
            controller: 'VisitsTodayCtrl'
        })
        .when('/reports', {
            templateUrl: 'reports.html',
            controller: 'ReportsCtrl'
        })
        .when('/appointments', {
            templateUrl: 'appointments.html',
            controller: 'AppointmentsCtrl'
        })
        .when('/workspace', {
            templateUrl: 'workspace-operator.html',
            controller: 'WorkspaceOperatorCtrl'
        })
        .when('/reportofworkday', {
            templateUrl: 'reportofworkday.html',
            controller: 'ReportOfWorkDayCtrl'
        })
        .when('/reportofperiod', {
            templateUrl: 'reportofperiod.html',
            controller: 'ReportOfPeriodCtrl'
        })
        .when('/reportofperiodprofit', {
            templateUrl: 'reportofperiodprofit.html',
            controller: 'ReportOfPeriodProfitCtrl'
        })
        .when('/online', {
            templateUrl: 'online.html',
            controller: 'OnlineAppointmentsCtrl'
        })
        .when('/blood-tests', {
            templateUrl: 'blood-tests.html',
            controller: 'BloodTestsCtrl'
        })
        .otherwise({
            redirectTo: '/login'
        });

    $locationProvider.html5Mode(true);
};

function run($rootScope, $http, $location, $localStorage) {
    console.log("APP IS STARTING.....")
    webix.i18n.setLocale('ru-RU');
    $rootScope.$on('$viewContentLoaded', function (event, next, current) {
        let e = document.getElementById("id_menu")
        if (e != null) {
            if (window.location.pathname.includes("login")) {
                e.style.display = "none"
            } else {
                e.style.display = "block"
            }
        }
    });
    $rootScope.logout = function () {
        $localStorage.currentUser = null;
        $rootScope.userLogin = '';
        $rootScope.userPassword = '';
        $http.defaults.headers.common.Authorization = '';
        $location.path('/login');
    };
    Date.prototype.toJSON = function () {
        moment.locale('ru');
        return moment(this).format("YYYY-MM-DD")
    }
    if ($localStorage.currentUser) {
        $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.currentUser.token;
        $rootScope.name = $localStorage.currentUser.name;
    }

    $rootScope.$on('$locationChangeStart', function (event, next, current) {
        let publicPages = ['/login'];
        let restrictedPage = publicPages.indexOf($location.path()) === -1;
        if (restrictedPage && !$localStorage.currentUser) {
            $location.path('/login');
        }
    });

    $rootScope.saveByteArray = (function () {
        let a = document.createElement("a");
        document.body.appendChild(a);
        a.style = "display: none";
        return function (data, name) {
            let blob = new Blob(data, {type: "octet/stream"}),
                url = window.URL.createObjectURL(blob);
            a.href = url;
            a.download = name;
            a.click();
            window.URL.revokeObjectURL(url);
        };
    }());
};