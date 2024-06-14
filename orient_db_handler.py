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
        return self.client is not None  # Verifica se self.client è definito e ha un ID sessione valido

    def create_vertices_and_edges(self, data):
        try:
            print(data)
            self.connect()  # Assicura che la connessione sia aperta

            # Controlla se gli host esistono già
            source = self.get_or_create_host(data['srcip'], data['srcintf'])
            dest = self.get_or_create_host(data['dstip'], data['dstintf'])

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
                self.client.command(
                    f"CREATE EDGE LinkToHost FROM {source._rid} TO {dest._rid} "
                    f"SET proto = {int(data['proto'])}, sentbyte = {int(data['sentbyte'])}, "
                    f"dstport = {int(data['dstport'])}, policyid = {int(data['policyid'])}, "
                    f"devname = '{data['devname']}', trandisp = '{data['trandisp']}'"
                )
                print(f"Created edge LinkToHost between {source._rid} and {dest._rid}")
                
        finally:
            # Non chiudere la connessione qui; lascia aperta per le successive operazioni
            pass

    def get_or_create_host(self, ip, interfaccia):
        # Controlla se il vertice Host esiste già
        query = f"SELECT FROM Host WHERE IP = '{ip}'"
        result = self.client.query(query)

        if result:  # Se esiste già, restituisci il vertice
            return result[0]
        else:  # Altrimenti, crea un nuovo vertice Host
            return self.client.command(f"CREATE VERTEX Host SET IP = '{ip}', interfaccia = '{interfaccia}'")[0]

    def close(self):
        if self.client:
            self.client.db_close()