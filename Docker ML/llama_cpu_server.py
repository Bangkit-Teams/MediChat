from flask import Flask, request, jsonify
from llama_cpp import Llama
import os

# Create a Flask object
app = Flask("Llama server")
model = None

@app.route('/llama', methods=['POST'])
def generate_response(max_tokens=None):
    global model
    
    try:
        data = request.get_json()

        # Check if the required fields are present in the JSON data
        if 'user_message' in data:
            system_message = "Below is an instruction that describes a task, paired with an input that provides further context. Write a response that appropriately completes the request."
            user_message = data['user_message']

            # Prompt creation
            prompt = f"""[INST] <<SYS>>
            {system_message}
            <</SYS>>
            \n\n### Instruction: \n{user_message}\n\n### Input:\n\n\n### Response:\n [/INST]"""
            
            # Create the model if it was not previously created
            if model is None:
                # Put the location of to the GGUF model that you've download from HuggingFace here
                model_path = "MediChat_medium_quant-unsloth.Q4_K_M.gguf"
                
                # Create the model
                model = Llama(model_path=model_path)
             
            # Run the model
            output = model(prompt, max_tokens=max_tokens, echo=True, temperature=0.2, top_p=9, top_k=4)
            
            return jsonify(output)

        else:
            return jsonify({"error": "Missing required parameters"}), 400

    except Exception as e:
        return jsonify({"Error": str(e)}), 500

if __name__ == '__main__':
    # app.run(host='0.0.0.0', port=int(os.environ.get('PORT', 8080), debug=True))
    app.run(host='0.0.0.0', port=int(os.environ.get('PORT', 8080), debug=True))