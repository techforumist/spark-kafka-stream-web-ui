var app = angular.module("MyApp", [ 'ngFileUpload' ]);

app.controller("HomeController", function($scope, Upload, $timeout, $http) {
	$scope.fileJobs = [];
	$scope.file = null;
	$scope.clearFile = function() {
		$scope.file = null;
	};

	$scope.loadFileJobs = function() {
		$http.get('/api/files').success(function(data) {
			$scope.fileJobs = data;
		}).error(function(data) {
			console.error(data);
		});
	};
	$scope.loadFileJobs();
	$scope.uploadFile = function(file) {
		$scope.file = file;
		$scope.file.upload = Upload.upload({
			url : '/api/upload',
			data : {
				username : $scope.username,
				file : file
			},
		});

		$scope.file.upload.then(function(response) {
			$timeout(function() {
				$scope.file.result = response.data;
				console.log($scope.file.result);
				$scope.fileJobs.push({
					id : $scope.file.result.id,
					filePath : $scope.file.result.filePath,
					status : 'Pending'
				});
			});
		}, function(response) {
			if (response.status > 0)
				$scope.errorMsg = response.status + ': ' + response.data;
		}, function(evt) {
			$scope.file.progress = Math.min(100, parseInt(100.0 * evt.loaded
					/ evt.total));
			$scope.file.progressString = $scope.file.progress + '%';
		});
	}

	var ws = new WebSocket("ws://localhost:8585/websocket");
	ws.onmessage = function(data) {
		var message = JSON.parse(data.data);
		console.log(message);
		changeStatus(message.id, message);
		$scope.$apply();
	};
	var changeStatus = function(id, file) {
		for (var i = 0; i < $scope.fileJobs.length; i++) {
			if ($scope.fileJobs[i].id == id) {
				$scope.fileJobs[i] = file;
			}
		}
	};
	ws.onerror = function(data) {
		console.log(data);
	};

});
app.filter('millSecondsToTimeString', function() {
	return function(millseconds) {
		if (millseconds < 0)
			millseconds = 0;
		var oneSecond = 1000;
		var oneMinute = oneSecond * 60;
		var oneHour = oneMinute * 60;
		var oneDay = oneHour * 24;

		var seconds = Math.floor((millseconds % oneMinute) / oneSecond);
		var minutes = Math.floor((millseconds % oneHour) / oneMinute);
		var hours = Math.floor((millseconds % oneDay) / oneHour);
		var days = Math.floor(millseconds / oneDay);

		var timeString = '';
		if (days !== 0) {
			timeString += (days !== 1) ? (days + ' days ') : (days + ' day ');
		}
		if (hours !== 0) {
			timeString += (hours !== 1) ? (hours + ' hours ')
					: (hours + ' hour ');
		}
		if (minutes !== 0) {
			timeString += (minutes !== 1) ? (minutes + ' minutes ')
					: (minutes + ' minute ');
		}
		if (seconds !== 0 || millseconds < 1000) {
			timeString += (seconds !== 1) ? (seconds + ' seconds ')
					: (seconds + ' second ');
		}

		if (timeString.startsWith('0'))
			return "";
		return timeString;
	};
});