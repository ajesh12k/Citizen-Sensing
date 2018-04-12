from flask import Flask, render_template ,session, escape, request, Response
from flask import url_for, redirect, send_from_directory
from flask import send_file, make_response, abort
from flask_socketio import SocketIO
import uuid

import os, sys
sys.path.append(os.path.join(os.path.dirname(__file__), "../"))

from manager import DatabaseManager
from tweets import Tweets

app = Flask(__name__)
app.secret_key="cSensing_app"
# socketio = SocketIO(app)

# if __name__ == '__main__':
#     socketio.run(app)
    
db = DatabaseManager()
tweet = Tweets()

# routing for basic pages (pass routing onto the Angular app)
@app.route('/')
@app.route('/about')
@app.route('/help')
@app.route('/devices')
@app.route('/login')
@app.route('/manage-events')
@app.route('/lost-report')
@app.route('/users')
@app.route('/events-mb')
def basic_pages(**kwargs):
    return make_response(open('templates/index.html').read())
    # routing for basic pages (pass routing onto the Angular app)

@app.route('/login', methods = ['POST'])
def login():
   if request.method == 'POST':
      session['username'] = request.form['username']
      session['password'] = request.form['password']
      return redirect(url_for('index'))
   return     
   
@app.route('/<model_name>/<item_id>')
def rest_device_page(**kwargs):
    return make_response(open('templates/index.html').read())
    # routing for REST alike pages (pass routing onto the Angular app)
 
@app.route('/logout')
def logout():
   # remove the username from the session if it is there
   session.pop('username', None)
   return redirect(url_for('basic_pages'))   
    

@app.route('/getEventTypes', methods=['POST'])
def get_event_types():
    return db.getEventTypes(), 200, {'Content-Type': 'application/json'}


@app.route('/getEvent', methods=['POST'])
def get_Event():
    event_id = request.json['event_id']
    device_id = request.json['device_id']
    fromDate = request.json['fromDate']
    toDate = request.json['toDate']
    status = request.json['status']
    changeDate = request.json['changedDate']
    changeBy = request.json['changedBy']
    event_type = request.json['event_type']
    if event_id == "" :
        event_id = "all"
    if device_id == "" :
        device_id = "all"
    if event_type == "" :
        event_type = "all"
    if fromDate == "" :
        fromDate = "all"
    if toDate == "" :
        toDate = "all"
    if status == "" :
        status = "all"
    if changeDate == "" :
        changeDate = "all"
    if changeBy == "" :
        changeBy = "all"
    return db.getEvent(event_id, device_id, fromDate, toDate, status, changeDate, changeBy, event_type), 200, {'Content-Type': 'application/json'}

@app.route('/saveEvent', methods=['POST'])
def save_Event():
    name = request.json['device_id']
    latitude = request.json['latitude']
    longitude = request.json['longitude']
    event_type = request.json['type']
    if name is not None and latitude is not None and longitude is not None and event_type is not None:
        return db.saveEvent(name, latitude, longitude, event_type), 200, {'Content-Type': 'application/json'}
    else:
        return {"status":"Failed to save as data is incomplete!"}

@app.route('/updateEvent', methods=['POST'])
def update_Event():
    event_id = request.json['event_id']
    status = request.json['status']
    updated_by = request.json['updated_by']
    return db.updateEvent(event_id, status, updated_by), 200, {'Content-Type': 'application/json'}

@app.route('/getDevice', methods=['POST'])
def get_device():
    deviceName = request.json['device_id']
    status = request.json['status']
    if deviceName == "" :
        deviceName = "all"
    if status == "" :
        status = "all"
    return db.getDevice(deviceName, status), 200, {'Content-Type': 'application/json'}
    
@app.route('/checkDevice', methods=['POST'])
def check_device():
    deviceName = request.json['device_mac']
    return db.checkDevice(deviceName), 200, {'Content-Type': 'application/json'}

@app.route('/saveDevice', methods=['POST'])
def save_Device():
    device_id = request.json['device_id']
    device_mac = request.json['device_mac']
    if device_id != None and device_mac != None:
        return db.saveDevice(device_id, device_mac), 200, {'Content-Type': 'application/json'}
    else:
        return {"status":"Failed to save as data is incomplete!"}

@app.route('/updateDevice', methods=['POST'])
def update_Device():
    device_id = request.json['device_id']
    status = request.json['status']
    return db.updateDevice(device_id, status), 200, {'Content-Type': 'application/json'}
    
@app.route('/saveUser', methods=['POST'])
def save_User():
    username = request.json['username']
    password = request.json['password']
    name = request.json['name']
    position = request.json['position']
    if username != None and password != None:
        return db.saveUser(username, password, name, position), 200, {'Content-Type': 'application/json'}
    else:
        return {"status":"Failed to save user as data is incomplete!"}
        
@app.route('/validateUser', methods=['POST'])
def validate_User():
    username = request.json['username']
    password = request.json['password']
    if username != None and password != None:
        checkValidity = db.validateUser(username, password)
        if checkValidity == "true":
            session['username'] = username
            session['id'] = str(uuid.uuid4())
            return redirect(url_for('basic_pages'))
        else:
            return "failed", 200, {'Content-Type':'application/json'}
    else:
        return {"status":"Failed to validate user as data is incomplete!"}
        
        
@app.route('/getUserInfo', methods=['POST'])
def get_user():
    username = request.json['username']
    if username != None and password != None:
        return db.getUserInfo(username), 200, {'Content-Type': 'application/json'}
    else:
        return {"status":"Failed to validate user as data is incomplete!"}
        

@app.route('/updatePassword', methods=['POST'])
def update_password():
    username = request.json['username']
    password = request.json['password']
    newPassword = request.json['newPassword']
    if username != None and password != None:
        return db.updatePassword(username, password, newPassword), 200, {'Content-Type': 'application/json'}
    else:
        return {"status":"Failed to validate user as data is incomplete!"}

@app.route('/getUsers', methods=['POST'])
def get_users():
    return db.getUsers(), 200, {'Content-Type': 'application/json'}
    
    
@app.route('/getTweets', methods=['POST'])
def get_tweets():
    return tweet.getTweets(), 200, {'Content-Type': 'application/json'}


app.run(host=os.getenv('IP', '0.0.0.0'),port=int(os.getenv('PORT', 8080)))

#{"device_id":"hello","latitude":"18","longitude":"24","type":"s"}
#01. Save event

#{"event_id":"ad57e6cf-05ee-4c1a-8d55-09e769c3a3be","status":"done","updated_by":"krishna"}
#08. Update event status

#{"device_id":"","event_id":"","fromDate":"","toDate":"","status":"","changedBy":"","changedDate":"","event_type":""}
#06. Get event by status(resolved/pending/all)
#02. Get my event
#03. Get all event
#04. Get event by date
#05. Get event by to and from date
#14. Get event satus change date
#15. Get event status change user id

#{"device_id":"hello","device_mac":"3X:4C:5C:6V"}
#16. Save device Info

#{"device_id":"hello","status":""}
#07. Get device by status( lost, active, all)

#{"device_id":"hello","status":"inactive"}
#12. Update device status.

#{"username":"","name":"","password":"","position":""} - /saveUser
#{"username":"","password":""} - /validateUser
#{"username":""} - /getUserInfo
#09. Post user credentials
#10. Get user credentials
#11. Update user credentials
#13. Validate device by device id.
