import time
import shutil
import requests
import psutil
from datetime import datetime, timezone
from pathlib import Path
from flask import Flask


app = Flask(__name__)

STORAGE_URL = "http://storage:8080"

base_dir = Path.cwd()

vstorage_dir = base_dir / "vstorage"
log_file = vstorage_dir / "log.txt"

vstorage_dir.mkdir(parents=True, exist_ok=True)

def system_status():
    
    #Calculate uptime
    boot_time = psutil.boot_time()
    uptime_in_sec = time.time() - boot_time
    uptime_in_hrs = round(uptime_in_sec / 3600, 2)
    
    #calculate Disk space
    disk_usage = shutil.disk_usage('/')
    free_disk_in_mb = round(disk_usage.free / (1024 * 1024), 2)
    
    timestamp = datetime.now(timezone.utc).isoformat().replace('+00:00', 'Z')

    record = f"Timestamp2-{timestamp}: uptime {uptime_in_hrs} hours, free disk in root: {free_disk_in_mb} Mbytes"
    return record


#POST req to storage service
def post_storage_svc(record:str):
    try:
        requests.post(
            STORAGE_URL,
            data = record.encode("utf-8"),
            headers={'Content-Type': 'text/plain'}
        )
        print(f"Posted to Storage service: {record}")
    except requests.exceptions.RequestException as e:
        print(f"Error posting to Storage: {e}")


# Write to Shared volume
def write_to_vstorage(record:str):
    
    try:
        with open(log_file, "a") as f:
            f.write(record + "\n")
        print(f"Logged to vStorage: {record}")
    except Exception as e:
        print(f"Error writing to vStorage: {e}")

@app.route('/status', methods=['GET'])
def get_status():
   
    record = system_status()
    
    post_storage_svc(record)

    write_to_vstorage(record)

    return record, 200, {'Content-Type': 'text/plain'}

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
