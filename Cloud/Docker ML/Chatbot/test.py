from flask import Flask, request, jsonify
from llama_cpp import Llama
import os
model = None
system_message = "Below is an instruction that describes a task, paired with an input that provides further context. Write a response that appropriately completes the request."
user_message = "dok, kakiku sakit kenapa ya ?"

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
output = model(prompt, max_tokens=None, echo=True, temperature=0.2, top_p=9, top_k=4)

text = output['choices'][0]['text'].split('[/INST]')[1].strip()

print(text)