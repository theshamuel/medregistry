'use strict';

angular
    .module('app').factory('AuthSrv', Service);

function Service($http, $localStorage) {
    let service = {};

    service.Login = Login;
    service.Logout = Logout;

    return service;

    function Login(login, password, callback) {
        console.log("Service Login STARTING....");
        $http.post('/auth', { login: login, password: md5(password)}).then(function (response) {
            $localStorage.errorMessage = null;
            if (response.data.token) {
                $localStorage.currentUser = { login: login, token: response.data.token, name:response.data.name };
                $http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
                $localStorage.headers = {value:{
                    "Content-Type": "application/json",
                    "Authorization": 'Bearer ' + response.data.token
                }};
                callback(true);
            }
        },function errorCallback(response) {
            console.log("response="+response.status)
            $localStorage.responseStatus=response.status;
            if (response.status === 404 && response.data.message===undefined)
                $localStorage.errorMessage = "Сервер API недоступен";
            else
                $localStorage.errorMessage=response.data.message;
            callback(false);
        });
    }

    function Logout() {
        delete $localStorage.currentUser;
        delete $localStorage.headers;
        $http.defaults.headers.common.Authorization = '';
    }
}
