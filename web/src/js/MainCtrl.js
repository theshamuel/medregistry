'use strict';
angular
    .module('app')
    .controller('MainCtrl', Controller);

function Controller ($rootScope,$scope, $http, $localStorage, $location,$uibModal) {
    console.log("INIT  MainController....");
    $scope.logout = function() {
        $localStorage.currentUser = null;
        $scope.userLogin = '';
        $scope.userPassword = '';
        $http.defaults.headers.common.Authorization = '';
        $location.path('/login');
    };

 };