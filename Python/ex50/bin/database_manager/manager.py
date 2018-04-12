import pymongo
from pymongo import MongoClient
from flask import Flask, jsonify, request
from datetime import datetime

MONGO_HOST = "mongodb://root:root@ds117848.mlab.com:17848/nodelogin"
MONGO_PORT = 23456
MONGO_DB = "nodelogin"
MONGO_USER = "root"
MONGO_PASS = "root"
connection = MongoClient(MONGO_HOST, MONGO_PORT)
mongo = connection[MONGO_DB]

class DatabaseManager:            
    def getData(self):
        data = mongo.event
        output = []
        for q in data.find():
            output.append({'latitude':q['event_latitude'],'longitude':q['event_longitude']})
        return jsonify({'result':output})

    def getUserSubmittedData(self, username):
        data = mongo.event
        output = []
        for q in data.find({'event_device_id': username}):
            output.append({'latitude':q['event_latitude'],'longitude':q['event_longitude']})
        return jsonify({'result':output})
    
    def saveData(self, name, latitude, longitude, value):
        data = mongo.event
        currTime = str(datetime.now())
        save_id = data.insert({'event_device_id': name, 'event_latitude':latitude, 'event_longitude' : longitude, 'event_type' : value, 'event_date_time':currTime}) 
        new_data = data.find_one({'_id' : save_id})
        output = {'name' : new_data['event_device_id'], 'value':new_data['event_type']}
        return jsonify({'result':output})