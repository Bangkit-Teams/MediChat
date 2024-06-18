gcloud builds submit --tag gcr.io/capstone-project-c241-ps397/generate_response
gcloud run deploy --image gcr.io/capstone-project-c241-ps397/generate_response  --platform managed

curl -X POST -H "Content-Type: application/json" -d '{"user_message": "Dok kaki saya sakit kenapa ya ?","max_tokens": "None"}' http://localhost:8000/llama