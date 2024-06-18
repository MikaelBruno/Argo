import pika
from orient_db_handler import OrientDBHandler
import json
import keyboard  # Importa la libreria keyboard per gestire gli eventi di tastiera

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

def close_connection():
    orient_handler.close()  # Chiude la connessione al database
    print("Connessione al database chiusa.")
    exit(0)  # Esci dal programma

# Gestione dell'evento di pressione del tasto
keyboard.add_hotkey('q', lambda: close_connection())

# Connessione a RabbitMQ
connection = pika.BlockingConnection(pika.ConnectionParameters(host='10.255.6.31'))
channel = connection.channel()

# Messa in ascolto
channel.exchange_declare(exchange=exchange_name, exchange_type='direct')
channel.queue_declare(queue=queue_name, durable=True)
channel.queue_bind(exchange=exchange_name, queue=queue_name, routing_key=binding_key)
channel.basic_consume(queue=queue_name, on_message_callback=process_message, auto_ack=True)

# Consume dei messaggi
try:
    print("In attesa di messaggi. Premi 'q' per chiudere la connessione al database.")
    channel.start_consuming()
except KeyboardInterrupt:
    close_connection()  # Chiudi la connessione se viene rilevato un'interruzione da tastiera
finally:
    connection.close()  # Chiudi la connessione a RabbitMQ in modo pulito
