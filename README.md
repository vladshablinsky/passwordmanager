Password Manager
=================

This is just a test Android app I made as a part of my course project at university.
I'm not sure about the practical use of this app, but I can say, that I would use this app instead
of the others available on the market just for fun. The app is not that functional as you might wanted it to be,
but it does what the simpler password manager does -- stores passwords.

Features
========

### Add new list of passwords.

List of passwords is a Sheet object, to enter it you need to know the password from the Sheet.
To make a Sheet you should specify its name, description and the password to be able to enter it.

![Alt text](/../screenshots/screenshots/add_sheet.gif)

If you fail to enter the password, you won't enter the Sheet.

### Add new entry to the list of password.

Each Sheet stores Entries, where Entry is one of your records in some Sheet. Adding entries is as simple as adding sheets
except you don't need any passwords to be able to see the entry's password.

### Search

You can easily search among your Sheets and Entries by typing in action bar:

![Alt text](/../screenshots/screenshots/search.gif)


### Random passwords

The feature I pretty like is that you can easily generate random password if you wish. Just click the lightbulb button
and you get a randomly generated password. Also, you can adjust the length of the password. However there is no options
left to the user regarding random strings (i.e. you cannot specify symbols you want or don't want to present in a random
password), it's not so bad.

![Alt text](/../screenshots/screenshots/random.gif)

### Remove/Update/Export

Of course you can remove both Entry and Sheet: simple tap and hold the element in the list.
You can update an Entry same way and change the password for it. 
You are also able to export an Entry to some app as a plain text.

![Alt text](/../screenshots/screenshots/delete.gif)

### Encryption

All the password stored in the database are encrypted with master password which is the password
you specify for a newly created Sheet. To be able to check that the password you try to enter with to the Sheet
matches the one you used when created the Sheet SHA hashes get compared and if they match, then you can enter
the Sheet.

# License

GPLv2.

All the icons are available here [here: https://design.google.com/icons/#ic_add](https://design.google.com/icons/#ic_add)
under the Apache License Version 2.0. 
