import pika
from orient_db_handler import OrientDBHandler
import json

exchange_name = 'test_exchange'
queue_name = 'test_queue'
binding_key = 'policy_match'

_host = 'localhost'
_port = 2424
_db_name = 'test'
_username = 'root'
_password = 'root'

orient_handler = OrientDBHandler(_host, _port, _db_name, _username, _password)

def process_message(ch, method, properties, body):
    body_json = json.loads(body.decode('utf-8'))
    orient_handler.create_vertices_and_edges(body_json)

# Connessione a RabbitMQ
connection = pika.BlockingConnection(pika.ConnectionParameters(host='10.255.6.31'))
channel = connection.channel()  # sessione di comunicazione all'interno della connessione RabbitMQ

# messa in ascolto
channel.exchange_declare(exchange=exchange_name, exchange_type='direct')
channel.queue_declare(queue=queue_name, durable=True)  # indica che la coda sarà persistente, ovvero sopravviverà a riavvii di RabbitMQ.
channel.queue_bind(exchange=exchange_name, queue=queue_name, routing_key=binding_key)
channel.basic_consume(queue=queue_name, on_message_callback=process_message, auto_ack=True)  # momento in cui il programma è in attesa dei messaggi provenienti dalla queue

# consume dei messaggi
channel.start_consuming()