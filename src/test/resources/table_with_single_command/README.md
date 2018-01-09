# Running a command from a selected row

This example shows how to setup Tabular to provide searching across a
table of data, and then run a command from a selected row.

The csv file [table.csv](./table.csv) in this package contains a simple
CSV containing a dataset. Replace this with your own dataset. Remember,
the first row contains the column headings, and the subsequent rows
contain the table data.

```
Id,Item,Category,Color,Cost
1,Top Hat,Hats,Black,19.99
2,Dinner Jacket,Mens,Black,199.99
3,Denim Jacket,Mens 1980s,20.99
4,T-Shirt,Mens,Blue,10.99
5,Socks,Mens,Purple,5.99
6,Socks,Womens,Pink,5.99
7,Skirt,Womens,White,19.99
8,Trousers,Womens,Black,20.99
```

config.properties contains the following:
```
hotkey.combinations.show=shift ctrl PLUS

app.skip.command.browse.if.only.one.command.configured=true

# Configure the command
app.commmands.openHomeDir.name=Display in StockControl
app.commmands.openHomeDir.predicate=true
app.commmands.openHomeDir.command=../stock-control-app.exe ${Id}
app.commmands.openHomeDir.description=Opens up the selected item in the stock control application

```
## General configuration
|property |description |
|---|---|
| `hotkey.combinations.show` |defines the hotkey to launch to show Tabular, see [the home page for more info](../../../README.md).|
| `app.skip.command.browse.if.only.one.command.configured` | because there is only one command defined, there is no need to show a list of commands to run. If you wish to run the command immediately when the user presses Enter when a row is selected, configure this to `true`. If you wish to still display the list (with one item in it), then configure this to `false` |

## Configuring the command
Choose a <command> string to use for each of the four command properties.  This must contain only alpha-numeric characters.

|property |description |
|---|---|
| `app.commmands.<command>.name` | Short name of the command |
| `app.commmands.<command>.predicate` | The predicate on whether to enable the command for the given row. If you wish to always allow this command irrespective of the selected data row, use `true`.  Otherwise the expression must be valid groovy code.  You can refer to any of the columns of the selected row using their names in ${columnName} format.  You can also refer to System variables, Environment variables, and variables defined in the config.properties file in the same way. |
| `app.commmands.<command>.command` | The command to run.  Again, you can refer to column values in the selected row, as has been done here by referring to the Id column. |
| `app.commmands.<command>.description` | Description of the command |
