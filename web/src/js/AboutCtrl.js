'use strict';
angular
    .module('app')
    .controller('AboutCtrl', ['mainService', '$scope', '$http', '$localStorage', '$window', '$location',Controller]);

function Controller ($scope, $http, $localStorage, $window, $location) {
    console.log("INIT  AboutController....");
};