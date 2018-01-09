# Search across a table of data, select a row, search for an appropriate command, select a command to run.

This example shows how to setup Tabular to provide searching across a
table of data, select a row, browse for a command to run, and then run the selected command.

The csv file [table.csv](./table.csv) in this package contains a simple
CSV containing a dataset. Replace this with your own dataset. Remember,
the first row contains the column headings, and the subsequent rows
contain the table data.

```
Id,Item,Category,Color,Cost,NumInStock
1,Top Hat,Hats,Black,19.99,0
2,Dinner Jacket,Mens,Black,199.99,2
3,Denim Jacket,Mens 1980s,Blue,20.99,20
4,T-Shirt,Mens,Blue,10.99,5
5,Socks,Mens,Purple,5.99,0
6,Socks,Womens,Pink,5.99,2
7,Skirt,Womens,White,19.99,66
8,Trousers,Womens,Black,20.99,77
```

config.properties contains the following:
```
hotkey.combinations.show=shift ctrl PLUS

app.data.column.to.display.when.selected=Item

app.commmands.displayInStockControl.name=Display in stock control
app.commmands.displayInStockControl.predicate=true
app.commmands.displayInStockControl.command=../stock-control-app.exe --show ${Id}
app.commmands.displayInStockControl.description=Opens up the selected item in the stock control application

app.commmands.displayInWebsite.name=Display on website
app.commmands.displayInWebsite.predicate=true
app.commmands.displayInWebsite.command=firefox.exe 'http://www.acmeclothing.co.uk/item/${Id}'
app.commmands.displayInWebsite.description=Open the item in a browser

app.commmands.orderLowStock.name=Order for low stock
app.commmands.orderLowStock.predicate=${NumInStock} <= 5
app.commmands.orderLowStock.command=./stock-control-app.exe --order ${Id}
app.commmands.orderLowStock.description=Places order for items which are low in stock
```
## General configuration
|property |description |
|---|---|
| `hotkey.combinations.show` |defines the hotkey to launch to show Tabular, see [the home page for more info](../../../README.md).|
| `app.skip.command.browse.if.only.one.command.configured` | because there is only one command defined, there is no need to show a list of commands to run. If you wish to run the command immediately when the user presses Enter when a row is selected, configure this to `true`. If you wish to still display the list (with one item in it), then configure this to `false` |

## Configuring the commands
For each command, chose a <command> string that must be consistent across each of the four command properties.  This must contain only alpha-numeric characters.

|property |description |
|---|---|
| `app.commmands.<command>.name` | Short name of the command |
| `app.commmands.<command>.predicate` | The predicate on whether to enable the command for the given row. If you wish to always allow this command irrespective of the selected data row, use `true`.  Otherwise the expression must be valid groovy code.  You can refer to any of the columns of the selected row using their names in ${columnName} format.  You can also refer to System variables, Environment variables, and variables defined in the config.properties file in the same way. Note that for the last command in this example, the predicate is checking whether the item has stock count <= 5.  Only then will the command be displayed for a particular row.|
| `app.commmands.<command>.command` | The command to run.  Again, you can refer to column values in the selected row, as has been done here by referring to the Id column. |
| `app.commmands.<command>.description` | Description of the command |
