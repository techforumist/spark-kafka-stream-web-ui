var app = angular.module("MyApp", [ 'ngFileUpload' ]);

app.controller("HomeController", function($scope, Upload, $timeout) {
	$scope.clearFile = function() {
		$scope.file = null;
	};
	$scope.uploadFile = function(file) {
		$scope.file.upload = Upload.upload({
			url : '/api/upload',
			data : {
				username : $scope.username,
				file : file
			},
		});

		file.upload.then(function(response) {
			$timeout(function() {
				file.result = response.data;
				console.log(file.result);
			});
		}, function(response) {
			if (response.status > 0)
				$scope.errorMsg = response.status + ': ' + response.data;
		}, function(evt) {
			file.progress = Math.min(100, parseInt(100.0 * evt.loaded
					/ evt.total));
		});
	}
});