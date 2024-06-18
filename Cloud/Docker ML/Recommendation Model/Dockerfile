# Use python as base image
FROM python:3.11.7
# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY ./recommendation.py /app/recommendation.py
COPY ./recommendation.h5 /app/recommendation.h5
COPY ./tokenizer.json /app/tokenizer.json

# Install the needed packages
RUN pip install --no-cache-dir Flask
RUN pip install --no-cache-dir gunicorn
RUN pip install --no-cache-dir tensorflow==2.15.0

# Run llama_cpu_server.py when the container launches
CMD exec gunicorn --bind :$PORT --workers 2 --threads 4 --timeout 0 recommendation:app
