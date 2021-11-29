'use strict';
angular
    .module('app')
    .controller('BloodTestsCtrl', BloodTestsCtrl)

function BloodTestsCtrl($http, $location, $localStorage, $scope, uiGridConstants, i18nService, $uibModal, dialogs, $route, $window) {
    let version_api = "v1";

    i18nService.setCurrentLang('ru');
    $scope.lang = 'ru-RU';
    $scope.language = 'Russian';
    $scope.doctorId = "";

    $scope.checkAuth = function (XmlHttpRequest) {
        if (XmlHttpRequest.status === 401) {
            $localStorage.tookenExpired = true;
            $scope.logout();
        }
    };
}