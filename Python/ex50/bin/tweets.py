from twython import Twython, TwythonError

from flask import Flask, jsonify, request

APP_KEY = 'B30MCYl2nztxBeZ1R2TkMSGan'
APP_SECRET = '2NJWbXt8hZsWaZHHsralJaA88jdfYh1XPb9EZhmsREnaJbxru4'

twitter = Twython(APP_KEY, APP_SECRET)

class Tweets:
    def getTweets(self):
        results = twitter.search(q='#csensing OR #noise OR #lake OR #algae OR #smell', count=30)
        all_tweets = results['statuses']
        output = []
        for tweet in all_tweets:
            tweetmsg = tweet['text']
            user_info = tweet['user']
            image = user_info['profile_background_image_url_https']
            screen_name = user_info['screen_name']
            name = user_info['name']
            created_at = tweet['created_at']
            if image is None:
                image = "n"
            if screen_name is None:
                screen_name = "n"
            if name is None:
                name = "n"
            resp = {"message":tweetmsg, "name":name,"screen_name":screen_name,"name":name,"created_at":created_at}
            output.append(resp)
        return jsonify({"response":output})