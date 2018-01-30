var express = require('express');
var router = express.Router();
var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;
var bodyParser = require('body-parser');
router.use(bodyParser.json());
router.use(bodyParser.urlencoded({ extended: true }));

var User = require('../models/user');
var data = require('../models/data');

// Register
router.get('/register', function(req, res){
	res.render('register');
});

// Login
router.get('/login', function(req, res){
	res.render('login');
});

// Register User
router.post('/register', function(req, res){
	var name = req.body.name;
	var email = req.body.email;
	var username = req.body.username;
	var password = req.body.password;
	var password2 = req.body.password2;

	req.checkBody('name', 'Name is required').notEmpty();
	req.checkBody('email', 'Email is required').notEmpty();
	req.checkBody('email', 'Email is not valid').isEmail();
	req.checkBody('username', 'Username is required').notEmpty();
	req.checkBody('password', 'Password is required').notEmpty();
	req.checkBody('password2', 'Passwords do not match').equals(req.body.password);

	var errors = req.validationErrors();

	if(errors){
		res.render('register',{
			errors:errors
		});
	} else {
		var newUser = new User({
			name: name,
			email:email,
			username: username,
			password: password
		});

		User.createUser(newUser, function(err, user){
			if(err) throw err;
			console.log(user);
		});

		req.flash('success_msg', 'You are registered and can now login');

		res.redirect('/users/login');
	}
});

passport.use(new LocalStrategy(
  function(username, password, done) {
   User.getUserByUsername(username, function(err, user){
   	if(err) throw err;
   	if(!user){
   		return done(null, false, {message: 'Unknown User'});
   	}

   	User.comparePassword(password, user.password, function(err, isMatch){
   		if(err) throw err;
   		if(isMatch){
   			return done(null, user);
   		} else {
   			return done(null, false, {message: 'Invalid password'});
   		}
   	});
   });
  }));

passport.serializeUser(function(user, done) {
  done(null, user.id);
});

passport.deserializeUser(function(id, done) {
  User.getUserById(id, function(err, user) {
    done(err, user);
  });
});

router.post('/login',
  passport.authenticate('local', {successRedirect:'/', failureRedirect:'/users/login',failureFlash: true}),
  function(req, res) {
    res.redirect('/');
  });

router.get('/logout', function(req, res){
    req.logout();
    req.flash('success_msg', 'You are logged out');
    res.redirect('/users/login');
});

router.post('/saveData', function(req, res){
    var username = req.body.username;
    var latitude = req.body.latitude;
    var longitude = req.body.longitude;
    var value = req.body.value;
    console.log("name = " + username);
    console.log("latitude =  " + latitude);
    console.log("value =  " + longitude);
    var newData = new data({
        username: username,
        latitude: latitude,
        longitude: longitude,
        value: value,
    });

    data.saveData(newData, function(err, user){
        var temp = {"result" : "success", "data" : "saved"};
        res.end(temp.toString());
        if(err) throw err;
        //console.log(data);
    });

    req.flash('success_msg', 'Your data is saved');
});

router.get('/showAllData', function(req, res){
    data.find(function(err, data){
        var temp = {"result" : "success", "data" : data};
        res.end(JSON.stringify(temp));
        if(err) throw err;
        //console.log(data);
    });

    req.flash('success_msg', 'Your data is saved');
});

router.post('/showUserData', function(req, res){
    var username = req.body.username;
    console.log(username);
    data.find({"username":username},function(err, data){
        res.end(JSON.stringify(data));
        if(err) throw err;
        //console.log(data);
    });

    req.flash('success_msg', 'Your data is saved');
});

module.exports = router;