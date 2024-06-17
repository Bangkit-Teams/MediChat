from flask import Flask, request, jsonify
import os
from tensorflow.keras.preprocessing.text import tokenizer_from_json
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import load_model
import numpy as np

# Create a Flask object
app = Flask("Recommendation System")
model = None

@app.route('/recommendation', methods=['POST'])
def generate_response():
    global model
    
    try:
        data = request.get_json()

        # Check if the required fields are present in the JSON data
        if 'user_message' in data:
            user_message = data['user_message']

            model = load_model('recommendation.h5')
            with open('tokenizer.json') as json_file:
                tokenizer_json = json_file.read()
            tokenizer = tokenizer_from_json(tokenizer_json)
            # Run the model
            user_message = tokenizer.texts_to_sequences([user_message])
            user_message = pad_sequences(user_message, maxlen=120, padding='post', truncating='post')
            output = model.predict(user_message)
            
            keluaran = np.argmax(output, axis=1)
            match keluaran:
                case 0:
                    keluaran = 'dokter umum'
                case 1:
                    keluaran = 'spesialis anak'
                case 2:
                    keluaran = 'spesialis bedah'
                case 3:
                    keluaran = 'spesialis gizi'
                case 4:
                    keluaran = 'spesialis jantung'
                case 5:
                    keluaran = 'spesialis kandungan & kebidanan'
                case 6:
                    keluaran = 'spesialis kulit'
                case 7:
                    keluaran = 'spesialis mata'
                case 8:
                    keluaran = 'spesialis obstetri & ginekologi'
                case 9:
                    keluaran = 'spesialis ortopedi'
                case 10:
                    keluaran = 'spesialis paru'
                case 11:
                    keluaran = 'spesialis penyakit dalam'
                case 12:
                    keluaran = 'spesialis psikiater'
                case 13:
                    keluaran = 'spesialis saraf'
                case 14:
                    keluaran = 'spesialis telinga, hidung & tenggorokan'
                case 15:
                    keluaran = 'spesialis urologi'
            
            return jsonify({"response": keluaran})

        else:
            return jsonify({"error": "Missing required parameters"}), 400

    except Exception as e:
        return jsonify({"Error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=int(os.environ.get('PORT', 8080), debug=True))