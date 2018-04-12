'use strict';

/* Filters */

angular.module('CsenseFlask.filters', []).
filter('title', function() {
    return function(columnName) {
        return columnName.charAt(0).toUpperCase() + columnName.substring(1).toLowerCase();
    };
}).
filter('lowercase', function() {
    return function(str) {
        return str.toLowerCase();
    };
}).
filter('cuLabel', function() {
    return function(obj, createLabel, updateLabel) {
        if (!obj || !obj.id) {
            return createLabel;
        } else {
            return updateLabel;
        }
    };
}).
filter('boolText', function() {
    return function(value) {
        return value ? 'Yes' : 'No';
    };
}).
filter('dateText', function() {
    return function(dateString) {
        if (dateString == null) {
            return false;
        } else {
            return moment(new Date(dateString)).format("DD MMM YYYY");
        }
    };
}).
filter('readableDate', function() {
    return function(dateString) {
        if (dateString == null) {
            return '-';
        } else {
            return moment(new Date(dateString)).fromNow();
        }
    };
}).
filter('escapeHtml', function() {
    return function(unsafe) {
        return escape(unsafe);
    };
}).
filter('truncate', function () {
    return function (text, length, end) {
        if (isNaN(length))
            length = 10;

        if (end === undefined)
            end = "...";

        if (text.length <= length || text.length - end.length <= length) {
            return text;
        }
        else {
            return String(text).substring(0, length-end.length) + end;
        }
    };
}).
filter('taskTypeText', function() {
    return function(taskType) {
        var r = '';
        if (taskType == 'pending_high') {
            r = 'Pending (High)';
        } else if (taskType == 'pending') {
            r = 'Pending';
        } else if (taskType == 'complete') {
            r = 'Complete';
        } else if (taskType == 'all_tasks') {
            r = 'All Tasks';
        }
        return r;
    };
}).
filter('orderByText', function() {
    return function(orderBy) {
        var r = '';
        if (orderBy == 0) {
            r = 'Priority';
        } else if (orderBy == 1) {
            r = 'Creation Date';
        } else if (orderBy == 2) {
            r = 'Last Update Date';
        }
        return r;
    };
}).
filter('usersOrderByText', function() {
    return function(orderBy) {
        var r = '';
        if (orderBy == 0) {
            r = 'Name';
        } else if (orderBy == 1) {
            r = 'Email';
        } else if (orderBy == 2) {
            r = 'Last activity';
        }
        return r;
    };
}).
filter('priorityText', function() {
    return function(priority) {
        var r = '';
        if (!priority) {
            r = '';
        } else if (priority == 5) {
            r = 'High';
        } else if (priority == 4) {
            r = 'High-Medium';
        } else if (priority == 3) {
            r = 'Medium';
        } else if (priority == 2) {
            r = 'Low-Medium';
        } else if (priority == 1) {
            r = 'Low';
        }

        return r;
    };
}).
filter('statusText', function() {
    return function(status) {
        var r = '';
        if (!status) {
            r = '';
        } else if (status == 5) {
            r = 'Paused';
        } else if (status == 4) {
            r = 'Need Clarification';
        } else if (status == 3) {
            r = 'Complete';
        } else if (status == 2) {
            r = 'On Going';
        }

        return r;
    };
}).
filter('statusColor', function() {
    return function(status) {
        var r = '#666';
        if (status == 5) {
            r = '#D2691E';
        } else if (status == 4) {
            r = '#DB7093';
        } else if (status == 3) {
            r = '#0081c2';
        } else if (status == 2) {
            r = '#008B45';
        }

        return r;
    };
}).
filter('highlight', function() {
    return function(data, search) {
        return highlight(data, search);
    };
}).
filter('details', function() {
    return function(task, searchText) {
        var r = "";
        var description = highlight(escape(task.description), escape(searchText));
        for (var i = 0; i < task.files.length ; i++) {
            var file = task.files[i].file;
            r += '<li><a href="rest/download_file?taskId=' + task.id + '&fileId=' + file.id + '" title="Size: ' + sizeText(file.size) + '">' + highlight(escape(file.name), searchText) + '</a></li>';
        }
        if (r) {
            r = 'Files: <br/><ol>' + r + '</ol>';
        }
        if (description && r) {
            return description + '<br/><br/>' + r;
        } else if (description) {
            return description;
        } else {
            return r;
        }
    }
}).
filter('sizeText', function() {
    return function(bytes) {
        return sizeText(bytes);
    }
}).
filter('highlightSummary', function() {
    return function(data, taskId, searchedTaskId) {
        if (data) {
            if (taskId == searchedTaskId) {
                data = "<span style=\"color:#00adee\">" + data + "</span>";
            }
        }
        return data;
    };
});


//Helper methods
function highlight (data, search) {
    if (data) {
        data = data.toString();
        if (search) {
            data = data.replace( new RegExp( "(" + preg_quote( search ) + ")" , 'gi' ), "<span class='search-highlight'>$1</span>" );
        }
    }
    return data;
};

function escape(unsafe) {
    if (!unsafe) return unsafe;
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
};

function sizeText(bytes) {
    if (typeof bytes !== 'number') {
        return '';
    }
    if (bytes >= 1000000000) {
        return (bytes / 1000000000).toFixed(2) + ' gb';
    }
    if (bytes >= 1000000) {
        return (bytes / 1000000).toFixed(2) + ' mb';
    }
    return (bytes / 1000).toFixed(2) + ' kb';
};
