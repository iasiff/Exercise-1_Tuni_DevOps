FROM python:3.12-alpine
RUN apk add --no-cache gcc musl-dev linux-headers
RUN addgroup -S mygrp && adduser -S appusr -G mygrp
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY app.py .
RUN mkdir -p /app/vstorage && chown -R appusr:mygrp /app/vstorage
USER appusr
EXPOSE 5000
CMD ["gunicorn", "--bind", "0.0.0.0:5000", "app:app"]