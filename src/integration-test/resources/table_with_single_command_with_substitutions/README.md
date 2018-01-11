# Running a command from a selected row from the search results

This example shows how to setup Tabular to provide searching across a
table of data, and then the ability to run a command from a selected row.

The csv file [table.csv](table.csv) in this package contains a simple
CSV containing a dataset. Replace this with your own dataset. Remember,
the first row contains the column headings, and the subsequent rows
contain the table data.

config.properties contains just the property specifying the shortcut
to install to launch Tabular.

hotkey.combinations.show=shift ctrl PLUS

```
# Variables to use during subtitution
domain=tools4j.com

#
app.columns.to.display.in.data.table=Host,Env,LogicalName,App,Country,Name
app.data.column.to.display.when.selected=Host
app.command.column.to.display.when.selected=Name
app.close.console.on.command.finish=false
app.skip.command.browse.if.only.one.command.configured=true

app.column.abbreviations.Host=h app.column.abbreviations.Env=e
app.column.abbreviations.App=a app.column.abbreviations.HomeDir=d
app.column.abbreviations.LogsDir=l app.column.abbreviations.Country=c
app.column.abbreviations.Name=n

app.commmands.openHomeDir.name=Open Home Dir
app.commmands.openHomeDir.predicate=true
app.commmands.openHomeDir.command=${workingDir}/iterm.sh "ssh
${RealHost} 'cd ~/'" app.commmands.openHomeDir.description=ssh to the
target host, and open the home directory
```



