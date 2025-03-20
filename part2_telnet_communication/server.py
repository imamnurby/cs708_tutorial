from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/hello', methods=['POST'])
def receive_hello():
    data = request.json
    message = data.get('message', 'No message received')
    print(f"Received message: {message}")
    return jsonify({"status": "success", "received": message})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)