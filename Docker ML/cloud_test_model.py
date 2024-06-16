import requests

# URL of the deployed service on GCP
url = "https://generate-response1-t7yc42rinq-uc.a.run.app/llama"

# Headers
headers = {
    "Content-Type": "application/json"
}

# Payload
data = {
    "user_message": "Dok, kepalaku dari kemarin pusing, kenapa ya?",
    "max_tokens": None  # or any integer value
}

try:
    # Sending the POST request
    response = requests.post(url, headers=headers, json=data)
    response.raise_for_status()  # Check if the request was successful

    # Print the status code and response headers for debugging
    print(f"Response Status Code: {response.status_code}")
    print(f"Response Headers: {response.headers}")

    try:
        # Parsing JSON response
        data = response.json()
        print("Response JSON:", data)
    except requests.exceptions.JSONDecodeError:
        print("Response content is not valid JSON or is empty.")
        print(f"Response content: {response.text}")

except requests.exceptions.RequestException as e:
    print(f"An error occurred: {e}")
