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
    "CREATE CLASS LinkToHost EXTENDS E;",
    "CREATE PROPERTY LinkToHost.trandisp STRING;",
    "CREATE PROPERTY LinkToHost.dstport INTEGER;",
    "CREATE PROPERTY LinkToHost.srcintf STRING;",
    "CREATE PROPERTY LinkToHost.policyid INTEGER;",
    "CREATE PROPERTY LinkToHost.proto INTEGER;",
    "CREATE PROPERTY LinkToHost.srcip STRING;",
    "CREATE PROPERTY LinkToHost.dstip STRING;",
    "CREATE PROPERTY LinkToHost.srcport INTEGER;",
    "CREATE PROPERTY LinkToHost.devname STRING;",
    "CREATE PROPERTY LinkToHost.sentbyte INTEGER;",
    "CREATE PROPERTY LinkToHost.dstintf STRING;"
]

_database_ver_host_creation_commands = [
    "CREATE CLASS Host EXTENDS V;",
    "CREATE PROPERTY Host.IP STRING;",
    "CREATE PROPERTY Host.interfaccia STRING;",
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
    