'use strict';
angular
  .module('app').controller('LoginCtrl', LoginController);

function LoginController($location, $localStorage, $scope, $rootScope, AuthSrv) {
  initController();
  function initController() {
    console.log("INIT  LoginController....");
    if ($localStorage.tookenExpired) {
      webix.alert(" Истекло время сессии ");
      $localStorage.tookenExpired = false;
    }
    AuthSrv.Logout();
  };

  $scope.login = function () {
    $scope.error = null
    $scope.loading = true
    $rootScope.name = null
    AuthSrv.Login($scope.userLogin, $scope.userPassword, function (result) {
      if (result === true) {
        $rootScope.name = $localStorage.currentUser.name;
        $location.path('/main');
        console.log("name = " + $scope.name);
      } else {
        if ($localStorage.errorMessage !== null) {
          $scope.error = "Код ошибки:" + $localStorage.responseStatus + "\n" + $localStorage.errorMessage;
        }
        $scope.loading = false;
      }
    });
  };
};
