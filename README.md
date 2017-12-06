# CmdLauncher
Graphical tool to launch commands

CmdLauncher was born from the need to view (and search) in a tabular way across a small dataset in a fast and easy manner.

Working in large corporations as a programmer I'd often have to deal with a large number of applications across a large number of environments and hosts.  I wanted a tool to be able to easily lookup the host where a particular application was housed.  And then as an added step run a command against that host, usually just to open a ssh session.  So I wrote CmdLauncher.  I've written it in a way to be agnostic to the dataset being viewed in the hope that others might find it useful for other use cases.

Configuration
Configuration of CmdLauncher is done purely by a csv file, and two properties files.

    app.dataset.1=hosts
    hotkey.combinations.show=shift ctrl PLUS


