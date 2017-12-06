# Tabular
Graphical tool to search through a static datatable, with the option of launching commands from that data.

Tabular was born from the need to view (and search) in a tabular way across a small dataset in a fast and easy manner.

Working in large corporations as a programmer I'd often have to deal with a large number of applications across a large number of environments and hosts.  I wanted a tool to be able to easily lookup the host where a particular application was housed.  And then as an added step run a command against that host, usually just to open a ssh session.

I've written it in a way to be agnostic to the dataset being viewed in the hope that others might find it useful for other use cases.

There are 3 ways that Tabular can be used.

1. To search across table data
2. To search across table data, select a row, and run a command using data from that row
3. To search across table data, select a row, search for an appropriate command, select a command to run.

1. To search across table data
In this mode, the user can search across the data in a table.  The indexing used is very simple. See this explanation for more detail.  Tabular Indexing Overview



2. To search across table data, select a row, and run a command using data from that row






Tabular Indexing Overview