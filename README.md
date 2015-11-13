# 15619TeamProject

## MySQL
### q2
####  Q2MySql 导入Mysql 配套命令
``` sql
% q2 mysql lodaing
load data  infile 'q2mysqltmp.csv' replace into table testTable fields terminated by '\t' escaped by '"' enclosed by '"' lines terminated by '\n' (searchKey, tweetID, score, mosaicText) ;
```


### q3
####  mysql 建表
``` sql
create table q3( searchKey varchar(50) , tweetID varchar(50), score int, text varchar(65534) BINARY );
```



#### 导入数据
``` sql
% q3 mysql lodaing
load data  infile 'part-00005-reducer-results.csv' replace into table q3 fields terminated by '\t' escaped by '"' enclosed by '"' lines terminated by '\n' (searchKey, tweetID, score, text) ;
```


### q4
``` sql
load data  infile 'part-00005-reducer-result.csv' replace into table query3 fields terminated by '\t' escaped by '"' enclosed by '"' lines terminated by '\n' (searchKey, valueOne, valueTwo) ;
```

#### 转换字符集 version1.
[how to convert to utf8mb41](https://mathiasbynens.be/notes/mysql-utf8mb4) 
[link2](http://segmentfault.com/a/1190000000616820)
[link3](http://info.michael-simons.eu/2013/01/21/java-mysql-and-multi-byte-utf-8-support/
)

``` sql

# For each database:
ALTER DATABASE database_name CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
# For each table:
ALTER TABLE table_name CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# For each column:
ALTER TABLE table_name CHANGE column_name column_name VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# (Don’t blindly copy-paste this! The exact statement depends on the column type, maximum length, and other properties. The above line is just an example for a `VARCHAR` column.)
```


#### 转换字符集 version2. 
*应该可以识别大小写*

```sql
ALTER DATABASE database_name CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

ALTER TABLE table_name CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE table_name CHANGE column_name column_name VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

```


###outdated command
*	set date when bulk loading

```sql
% 注意 %h和%H的区别！
load data  infile 'part-00000' replace into table tweet fields terminated by ' ' escaped by '' enclosed by '"' lines terminated by '\n' (userID, tweetID, @datevar, score, mosaicText) SET date = STR_TO_DATE(@datevar, '%M-%d-%Y-%H:%i:%s');

select * from tweet where date = '2014-05-15 09:02:20'
```
[Stackoverflow solution](http://stackoverflow.com/questions/6460635/how-to-convert-date-in-csv-file-into-sql-format-before-mass-insertion)
*	STR\_TO\_DATE

``` sql
SELECT STR_TO_DATE('May 15 2014','%M %d %Y');

SELECT STR_TO_DATE('09:02:20','%h:%i:%s');
```
