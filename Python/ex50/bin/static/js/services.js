'use strict';

angular.module('csenseFlaskServices', ['ngResource'])
    .factory('Post', function($resource) {
        return $resource('/getEvent', {}, {
            query: {
                method: 'POST',
                isArray: true
            }
        });
    })
;
