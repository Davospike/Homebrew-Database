
# Homebrew-Database
An SQL database system built from scratch. This database works with the SQL syntax in BNF form shown below.


##  Usage
```
$ java DBServer

$ java DBClient
```
Once initialised, SQL queries can be entered on the command line, e.g. :
```
CREATE DATABASE coursework;

Server response: OK

USE coursework;

Server response: OK

CREATE TABLE marks (name, mark);

Server response: OK
```
Once a table is populated with data further queries can be executed, e.g. :
```
SELECT * FROM marks;

Server response:

id name mark

1 Steve 65

2 Dave 55

3 Bob 35

4 Clive 20

DELETE FROM marks WHERE name == 'Dave';

Server response: OK
```
The system can also handle JOIN queries and multiple WHERE conditions.

##  Syntax
```
<sqlCompiler.sqlCommands> ::= <CommandType>;

<CommandType> ::= <Use> | <Create> | <Drop> | <Alter> | <Insert> |

<Select> | <Update> | <Delete> | <Join>

<Use> ::= USE <DatabaseName>

<Create> ::= <CreateDatabase> | <CreateTable>

<CreateDatabase> ::= CREATE DATABASE <DatabaseName>

<CreateTable> ::= CREATE TABLE <TableName> | CREATE TABLE <TableName> ( <AttributeList> )

<Drop> ::= DROP <Structure> <StructureName>

<Structure> ::= DATABASE | TABLE

<Alter> ::= ALTER TABLE <TableName> <AlterationType> <AttributeName>

<Insert> ::= INSERT INTO <TableName> VALUES ( <ValueList> )

<Select> ::= SELECT <WildAttribList> FROM <TableName> |

SELECT <WildAttribList> FROM <TableName> WHERE <Condition>

<Update> ::= UPDATE <TableName> SET <NameValueList> WHERE <Condition>

<Delete> ::= DELETE FROM <TableName> WHERE <Condition>

<Join> ::= JOIN <TableName> AND <TableName> ON <AttributeName> AND <AttributeName>

<NameValueList> ::= <NameValuePair> | <NameValuePair> , <NameValueList>

<NameValuePair> ::= <AttributeName> = <Value>

<AlterationType> ::= ADD | DROP

<ValueList> ::= <Value> | <Value> , <ValueList>

<Value> ::= '<StringLiteral>' | <BooleanLiteral> | <FloatLiteral> | <IntegerLiteral>

<BooleanLiteral> ::= true | false

<WildAttribList> ::= <AttributeList> | *

<AttributeList> ::= <AttributeName> | <AttributeName> , <AttributeList>

<Condition> ::= ( <Condition> ) AND ( <Condition> ) |

( <Condition> ) OR ( <Condition> ) |

<AttributeName> <Operator> <Value>

<Operator> ::= == | > | < | >= | <= | != | LIKE
```
