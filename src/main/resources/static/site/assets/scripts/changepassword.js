angular
	.module("subwaytalent",[])
	.config(['$locationProvider',function($locationProvider){
		$locationProvider.html5Mode({
			  enabled: true,
			  requireBase: false  //getting params in url
			});
	}])
	.controller("ChangePasswordController", ["$http","$scope","$location",function($http,$scope,$location){
		$scope.changePasswordRequest = {email:$location.search().email, resetKey:$location.search().resetKey};
		console.log($scope.changePasswordRequest);
		$scope.resetPassword = function(){
			console.log($scope.changePasswordRequest);
			if($scope.changePasswordRequest.newPassword1 == $scope.changePasswordRequest.newPassword){
				$http.put("/api/resetPassword", $scope.changePasswordRequest)
				.success(function(response){
					window.location.href="/password/success";
				})
				.error(function(response){
					alert(response.message);
				});
			}else{
				alert("Passwords don't match");
			}
		};
		
	}]);