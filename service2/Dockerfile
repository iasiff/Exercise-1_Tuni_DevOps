FROM python:3.12-alpine
WORKDIR /app
COPY requirements.txt .
RUN apk add gcc python3-dev musl-dev linux-headers
RUN pip install --no-cache-dir -r requirements.txt
COPY app.py .
RUN mkdir -p /app/vstorage
CMD ["flask", "run", "--host=0.0.0.0", "--port=5000"]
