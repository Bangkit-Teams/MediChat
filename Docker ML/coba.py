from llama_cpp import Llama

# Put the location of to the GGUF model that you've download from HuggingFace here
model_path = "MediChat_medium_quant-unsloth.Q4_K_M.gguf"

# Create a llama model
model = Llama(model_path=model_path)

# Prompt creation
system_message = "Please response shortly instruction below, don't get to long please"
user_message = "Dok, kakiku gatel kenapa ya ?"

prompt = f"""[INST] <<SYS>>
{system_message}
<</SYS>>
\n\n### Instruction: \n{user_message}\n\n### Input:\n\n\n### Response:\n [/INST]"""


# Run the model
output = model(prompt, max_tokens=None, echo=True, temperature=0.2, top_p=9, top_k=4)

# Print the model output
print(output)