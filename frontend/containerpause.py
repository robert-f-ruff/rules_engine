#!/usr/bin/env python
"""Verify the specified database is running and accepting connections before running Django."""
import time

import MySQLdb
from dotenv import find_dotenv, load_dotenv
from rules.core import retrieve_setting

def connectivity_check() -> None:
    """ This function will check the database connection every 2 seconds. On the first connect,
        it will wait 4 seconds, and then begin checking again every 2 seconds, as the database
        starts, initializes, and then restarts before it is fully online. After the second
        connect, the function exits.
    """
    load_dotenv(find_dotenv())
    db_host = retrieve_setting('db_host')
    db_user = retrieve_setting('db_user_name')
    db_password = retrieve_setting('db_user_password')
    print('Verifying database connectivity...', end='', flush=True)
    times_verified = 0
    while True:
        try:
            database = MySQLdb.connect(host=db_host, user=db_user, password=db_password,
                                    database='rules', connect_timeout=2)
            times_verified += 1
            if times_verified == 2:
                break
            time.sleep(4)
        except MySQLdb.OperationalError:
            pass
    database.close()
    print('[ OK ]')

if __name__ == '__main__':
    connectivity_check()
