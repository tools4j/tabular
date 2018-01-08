# Simple search across a table of data

This example shows how to setup Tabular to provide searching across a
table of data.

The csv file table.csv in this package contains a simple CSV file:

`App,Env,Location,Host
WebServer,Dev,London,lndevweb01
WebServer,Uat,London,lnuatweb01
WebServer,Prod,London,lnprodweb01
WebServer,Prod,Melbourne,mbprodweb01
MessagingServer,Dev,London,lndevmsg01
MessagingServer,Uat,London,lnuatmsg01
MessagingServer,Prod,London,lnprodmsg01
MessagingServer,Prod,Melbourne,mbprodmsg01
DatabaseServer,Dev,London,lndevdb01
DatabaseServer,Uat,London,lnuatdb01
DatabaseServer,Prod,London,lnproddb01
DatabaseServer,Prod,Melbourne,mbproddb01`

The first row contains the column headings, and the subsequent rows
contain the table data.

config.properties contains just the property specifying the shortcut
to install to launch Tabular.

`hotkey.combinations.show=shift ctrl PLUS`

