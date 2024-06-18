import pyorient

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
            print(data)
            self.connect()  # Assicura che la connessione sia aperta

            # Controlla se gli host esistono già
            source = self.get_or_create_host(data['srcip'], data.get('srcintf', 'unknown'))
            dest = self.get_or_create_host(data['dstip'], data.get('dstintf', 'unknown'))

            # Creazione o aggiornamento dell'edge LinkToHost
            query = f"SELECT FROM LinkToHost WHERE out = {source._rid} AND in = {dest._rid}"
            existing_links = self.client.query(query)

            if existing_links:
                existing_link = existing_links[0]
                if 'sentbyte' in data:
                    new_sentbyte = int(existing_link.oRecordData.get('sentbyte', 0)) + int(data['sentbyte'])
                    self.client.command(
                        f"UPDATE LinkToHost SET sentbyte = {new_sentbyte} WHERE @rid = {existing_link._rid}"
                    )
                    print(f"Updated edge LinkToHost with RID: {existing_link._rid}")
                else:
                    print("Warning: 'sentbyte' field not found in data. Skipping update.")
                
            else:  # Altrimenti, crea un nuovo edge
                proto = int(data['proto']) if 'proto' in data else 0
                sentbyte = int(data['sentbyte']) if 'sentbyte' in data else 0
                dstport = int(data['dstport']) if 'dstport' in data else 0
                policyid = int(data['policyid']) if 'policyid' in data else 0
                devname = data['devname'] if 'devname' in data else ''
                trandisp = data['trandisp'] if 'trandisp' in data else ''

                self.client.command(
                    f"CREATE EDGE LinkToHost FROM {source._rid} TO {dest._rid} "
                    f"SET proto = {proto}, sentbyte = {sentbyte}, "
                    f"dstport = {dstport}, policyid = {policyid}, "
                    f"devname = '{devname}', trandisp = '{trandisp}'"
                )
                print(f"Created edge LinkToHost between {source._rid} and {dest._rid}")
                
        except pyorient.PyOrientCommandException as e:
            print(f"Error executing OrientDB command: {e}")
        except Exception as e:
            print(f"Unexpected error: {e}")
        finally:
            # Non chiudere la connessione qui; lascia aperta per le successive operazioni
            pass

    def get_or_create_host(self, ip, interfaccia):
        try:
            # Controlla se il vertice Host esiste già
            query = f"SELECT FROM Host WHERE IP = '{ip}'"
            result = self.client.query(query)

            if result:  # Se esiste già, restituisci il vertice
                return result[0]
            else:  # Altrimenti, crea un nuovo vertice Host
                return self.client.command(f"CREATE VERTEX Host SET IP = '{ip}', interfaccia = '{interfaccia}'")[0]
        except pyorient.PyOrientCommandException as e:
            print(f"Error executing OrientDB command: {e}")
            return None
        except Exception as e:
            print(f"Unexpected error: {e}")
            return None

    def close(self):
        if self.client:
            self.client.db_close()
