var mongoose = require('mongoose');

// Data Schema
var dataSchema = mongoose.Schema({
    username: {
        type: String,
        index:true
    },
    latitude: {
        type: String
    },
    longitude: {
        type: String
    },
    value: {
        type: String
    },
    date: {
        type: Date
    }
});

var data = module.exports = mongoose.model('Data', dataSchema);

module.exports.saveData = function(newData, callback){
    newData.save(callback);
}

module.exports.getDataByUsername = function(username, callback){
    var query = {username: username};
    User.findOne(query, callback);
}

module.exports.getDataById = function(id, callback){
    User.findById(id, callback);
}