import pyorient
from datetime import datetime

class OrientDBHandler:
    def __init__(self, host, port, db_name, username, password):
        self.host = host
        self.port = port
        self.db_name = db_name
        self.username = username
        self.password = password
        self.client = None

    def connect(self):
        try:
            if not self.is_connected():
                self.client = pyorient.OrientDB(self.host, self.port)
                self.client.set_session_token(True)
                self.client.db_open(self.db_name, self.username, self.password)
        except pyorient.PyOrientConnectionException as e:
            print(f"Errore durante la connessione a OrientDB: {e}")
            self.client = None

    def is_connected(self):
        return self.client is not None 

    def create_vertices_and_edges(self, data):
        try:
            self.connect()  # Ensure the connection is open
            current_time = datetime.fromisoformat(data["@timestamp"])

            # Get or create source and destination hosts
            source = self.get_or_create_host(data['srcip'], data['srcintf'], data.get('srcname'))
            dest = self.get_or_create_host(data['dstip'], data['dstintf'])

            # Check for existing edge
            query = f"SELECT FROM SourceToDestination WHERE out = {source._rid} AND in = {dest._rid}"
            existing_links = self.client.query(query)

            if existing_links and len(existing_links) > 0:
                existing_link = existing_links[0]
                new_sentbyte = int(existing_link.oRecordData.get('sentbyte', 0)) + int(data.get('sentbyte', 0))
                new_conn_matches = int(existing_link.oRecordData.get('conn_matches', 0)) + 1
                self.client.command(
                    f"UPDATE SourceToDestination SET sentbyte = {new_sentbyte}, conn_matches = {new_conn_matches}, last_seen = '{current_time}' WHERE @rid = {existing_link._rid}"
                )
                print(f"Updated edge SourceToDestination with RID: {existing_link._rid}")
            else:  # Create new edge if it doesn't exist
                trandisp = data.get('trandisp', '')
                dstport = int(data.get('dstport', 0))
                srcintf = data.get('srcintf', '')
                policyid = int(data.get('policyid', 0))
                protocol = int(data.get('proto', 0))
                source_field = data.get('srcip', '')
                destination = data.get('dstip', '')
                srcport = int(data.get('srcport', 0))
                device_id = data.get('devname', '')
                sentbyte = int(data.get('sentbyte', 0))
                dstintf = data.get('dstintf', '')
                conn_matches = 0  

                self.client.command(
                    f"CREATE EDGE SourceToDestination FROM {source._rid} TO {dest._rid} "
                    f"SET trandisp = '{trandisp}', "
                    f"dstport = {dstport}, srcintf = '{srcintf}', policyid = {policyid}, "
                    f"protocol = {protocol}, source = '{source_field}', destination = '{destination}', "
                    f"srcport = {srcport}, device_id = '{device_id}', sentbyte = {sentbyte}, "
                    f"dstintf = '{dstintf}', conn_matches = {conn_matches}, last_seen = '{current_time}'"
                )
                print(f"Created edge SourceToDestination between {source._rid} and {dest._rid}")

        except pyorient.PyOrientCommandException as e:
            print(f"Error executing OrientDB command: {e}")
        except Exception as e:
            print(f"Unexpected error: {e}")
            print(data)
        finally:
            # Do not close the connection here; leave it open for subsequent operations
            pass

    def get_or_create_host(self, ip, interface, name=None):
        try:
            # Check if the Host vertex already exists
            if name is not None:
                query = f"SELECT FROM Host WHERE name = '{name}'"
            else:
                query = f"SELECT FROM Host WHERE ip = '{ip}'"
                
            result = self.client.query(query)
    
            if result and len(result) > 0:  # If it exists, return the vertex
                return result[0]
            elif name is not None:  # Otherwise, create a new Host vertex
                return self.client.command(f"CREATE VERTEX Host SET ip = '{ip}', interface = '{interface}', name = '{name}'")[0]
            else:  # Otherwise, create a new Host vertex without name
                return self.client.command(f"CREATE VERTEX Host SET ip = '{ip}', interface = '{interface}'")[0]
        
        except pyorient.PyOrientCommandException as e:
            print(f"Error executing OrientDB command: {e}")
            return None
        except Exception as e:
            print(f"Unexpected error: {e}")
            return None

    def close(self):
        if self.client:
            self.client.db_close()
