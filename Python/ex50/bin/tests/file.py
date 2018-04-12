from flask import Flask, jsonify, request
from flask.ext.pymongo import PyMongo

app = Flask(__name__)

app.config['MONGO_DBNAME'] = 'nodelogin'
app.config['MONGO_URI'] = 'mongodb://root:root@ds117848.mlab.com:17848/nodelogin'

mongo = PyMongo(app)

@app.route('/getData', methods=['GET'])
def getData():
    data = mongo.db.data
    output = []
    for q in data.find():
        output.append({'name':q['username']})
    return jsonify({'result':output})

@app.route('/getData/<name>', methods=['GET'])
def getData_single(name):
    data = mongo.db.data
    q = data.find_one({'username' : name})
    output = {'name' : q['username'], 'value' : q['value']}    
    return jsonify({'result':output})

@app.route('/saveData', methods=['POST'])
def saveData_single():
    data = mongo.db.data
    name = request.json['username']
    latitude = request.json['latitude']
    longitude = request.json['longitude']
    value = request.json['value']
    save_id = data.insert({'username': name, 'latitude':latitude, 'longitude' : longitude, 'value' : value})
    new_data = data.find_one({'_id' : save_id})
    output = {'name' : new_data['username'], 'value':new_data['value']}
    return jsonify({'result':output})

if __name__ == '__main__':
    app.run(debug=True)