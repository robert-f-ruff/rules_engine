#!/usr/bin/env python
"""Verify the specified database is running and accepting connections before running Django."""
import MySQLdb
from dotenv import load_dotenv, find_dotenv
from rules.core import retrieve_setting

load_dotenv(find_dotenv())
db_host = retrieve_setting('db_host')
db_user = retrieve_setting('db_user_name')
db_password = retrieve_setting('db_user_password')
print('Verifying database connectivity...', end='', flush=True)
while True:
    try:
        database = MySQLdb.connect(host=db_host, user=db_user, password=db_password,
                                   database='rules', connect_timeout=2)
        break
    except MySQLdb.OperationalError:
        pass
database.close()
print('[ OK ]')
