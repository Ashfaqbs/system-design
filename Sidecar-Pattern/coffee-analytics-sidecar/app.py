from flask import Flask, request, jsonify
import json
from datetime import datetime

app = Flask(__name__)

# Simple in-memory counter for demo purposes
order_counts = {"latte": 0, "cappuccino": 0, "espresso": 0}

@app.route('/track', methods=['POST'])
def track_order():
    order = request.get_json()
    drink = order.get('drink', 'unknown').lower()
    
    # Update trend counter
    if drink in order_counts:
        order_counts[drink] += 1
    
    # Log to file
    with open('order_trends.log', 'a') as f:
        timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        f.write(f"{timestamp} - Order: {drink}, Size: {order.get('size')}, Extra: {order.get('extra')}\n")
    
    # Creative twist: send a fun message back
    trend_message = f"{drink.capitalize()} is trending with {order_counts[drink]} orders today!"
    return jsonify({"message": trend_message}), 200

if __name__ == '__main__':
    app.run(host='localhost', port=5000)
    
    
    
# pip install -r requirements.txt 
# python app.py
# http://localhost:5000   