# Use the official Python image
FROM python:3.11-slim

# Set the working directory in the container
WORKDIR /app

# Copy the requirements file and install dependencies
# We use requirements.txt to define fastapi, uvicorn, scikit-learn, etc.
COPY ./requirements.txt /tmp/requirements.txt
RUN pip install --no-cache-dir -r /tmp/requirements.txt

# Copy the application code
# Assuming the Python code is in ai-service/app/main.py
COPY app /app/app

# Expose the port FastAPI runs on
EXPOSE 8001

# Run the Uvicorn server (replace main:app if your file is named differently)
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8001"]