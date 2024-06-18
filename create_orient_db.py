import pyorient
# modificato un pezzo nella libreria pika commentando in connection altrimenti lancia un errore quando si connette, nella versione di git sembra abbiano risolto:
"""
if self.protocol > SUPPORTED_PROTOCOL:
    raise PyOrientWrongProtocolVersionException(
        "Protocol version " + str(self.protocol) +
        " is not supported yet by this client.", [])
"""

_database_name = "test"

_database_ver_link_creation_commands = [
    "CREATE CLASS SourceToDestination EXTENDS E;",
    "CREATE PROPERTY SourceToDestination.trandisp STRING;",
    "CREATE PROPERTY SourceToDestination.dstport INTEGER;",
    "CREATE PROPERTY SourceToDestination.srcintf STRING;",
    "CREATE PROPERTY SourceToDestination.policyid INTEGER;",
    "CREATE PROPERTY SourceToDestination.protocol INTEGER;",
    "CREATE PROPERTY SourceToDestination.source STRING;",
    "CREATE PROPERTY SourceToDestination.destination STRING;",
    "CREATE PROPERTY SourceToDestination.srcport INTEGER;",
    "CREATE PROPERTY SourceToDestination.device_id STRING;",
    "CREATE PROPERTY SourceToDestination.sentbyte INTEGER;",
    "CREATE PROPERTY SourceToDestination.dstintf STRING;",
    "CREATE PROPERTY SourceToDestination.conn_matches STRING;",
    "CREATE PROPERTY SourceToDestination.last_seen DATETIME;"
]

    
"""
{'srcip': '10.255.5.245', 'sentbyte': '4597', 'proto': '17', 'dstintfrole': 'undefined',
'dstip': '216.58.204.131', 'dstintf': 'port1', 'srcintf': 'port2', 'policyid': '4', 'srcname': 'AD',
'trandisp': 'snat', 'dstport': '443', 'srcport': '58048', 'srcintfrole': 'undefined', 'devname': 'FGT_LabSecurity'}
"""

_database_ver_host_creation_commands = [
    "CREATE CLASS Host EXTENDS V;",
    "CREATE PROPERTY Host.ip STRING;",
    "CREATE PROPERTY Host.interface STRING;",
]


client = pyorient.OrientDB("localhost", 2424)
client.set_session_token(True)
session_id = client.connect( "root", "root" )
client.db_exists( _database_name, pyorient.STORAGE_TYPE_MEMORY )
client.db_open( _database_name, "root", "root" )


for command in _database_ver_link_creation_commands:
    client.command(command)

for command in _database_ver_host_creation_commands:
    client.command(command)
    
    
    
    
