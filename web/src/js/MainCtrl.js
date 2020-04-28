'use strict';
angular
    .module('app')
    .controller('MainCtrl', Controller);

function Controller ($rootScope, $scope, $http, $localStorage, $location, $uibModal) {
    console.log("INIT  MainController....");
 };