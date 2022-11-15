import requests

url = 'http://127.0.0.1:5000/register'

registerObject = {'student_id': 's1911027', 'password': '12312312312'}

response = requests.post(url, json=registerObject)

print(response)
print(response.status_code)
