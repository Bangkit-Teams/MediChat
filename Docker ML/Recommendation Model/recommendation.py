from flask import Flask, request, jsonify
from llama_cpp import Llama
import os

# Create a Flask object
app = Flask("Recommendation System")
model = None

@app.route('/recommendation', methods=['POST'])
def generate_response(max_tokens=None):
    global model
    
    try:
        data = request.get_json()

        # Check if the required fields are present in the JSON data
        if 'user_message' in data:
            user_message = data['user_message']

            model =
            tokenizer = 
            # Run the model
            output = model(prompt, max_tokens=max_tokens, echo=True, temperature=0.2, top_p=9, top_k=4)
            
            text = output['choices'][0]['text'].split('[/INST]')[1].strip()
            
            return jsonify({"response": text})

        else:
            return jsonify({"error": "Missing required parameters"}), 400

    except Exception as e:
        return jsonify({"Error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=int(os.environ.get('PORT', 8080), debug=True))