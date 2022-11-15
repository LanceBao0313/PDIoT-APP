import requests

url = 'http://127.0.0.1:5000/login'

registerObject = {'student_id': 's12347', 'password': '345678'}

response = requests.post(url, json=registerObject)

print(response)
print(response.status_code)
