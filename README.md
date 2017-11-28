# tiddle
Property file named 'tiddle.properties' needs to exist in the working directory.

It should contain something like:

```
wiki.url=my-tiddly-wiki.html
hotkey.combinations.show=shift ctrl PLUS
```

**wiki.url**: can be either relative or absolute. (or a valid url).

**hotkey.combinations.show**: must be something parsable by `javax.swing.KeyStroke.getKeyStroke(<String>)`