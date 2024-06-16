# Use python as base image
FROM python
# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY ./recommendation.py /app/recommendation.py

# Install the needed packages
RUN pip install --no-cache-dir llama-cpp-python
RUN pip install --no-cache-dir Flask
RUN pip install --no-cache-dir gunicorn

# Run llama_cpu_server.py when the container launches
CMD exec gunicorn --bind :$PORT --workers 1 --threads 1 --timeout 0 llama_cpu_server:app
