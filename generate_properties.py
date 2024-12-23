#!/usr/bin/env python
"""Generate the Spring application.properties files."""
from frontend.rules.core import retrieve_setting

def write_props() -> None:
    """ This function will retrieve data from the secrets files and
        generate the Spring .properties files.
    """
    db_host = retrieve_setting('db_host')
    db_host_port = retrieve_setting('db_host_port')
    db_user = retrieve_setting('db_user_name')
    db_password = retrieve_setting('db_user_password')
    mail_host = retrieve_setting('mail_host')
    mail_host_port = retrieve_setting('mail_host_port')
    mail_username = retrieve_setting('mail_from_address')
    mail_password = retrieve_setting('mail_server_password')
    engine_reload_key = retrieve_setting('engine_reload_key')
    with(open(file='backend/engine/config/application-nc.properties',
              mode='w', encoding='utf-8')) as prop_file:
        prop_file.write('spring.application.name=Rules Engine\n')
        prop_file.write(f'spring.datasource.url=jdbc:mysql://{db_host}:{db_host_port}/rules\n')
        prop_file.write(f'spring.datasource.username={db_user}\n')
        prop_file.write(f'spring.datasource.password={db_password}\n')
        prop_file.write('spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect\n')
        prop_file.write('spring.jpa.open-in-view=false\n')
        prop_file.write(f'spring.mail.host={mail_host}\n')
        prop_file.write(f'spring.mail.port={mail_host_port}\n')
        prop_file.write(f'spring.mail.username={mail_username}\n')
        prop_file.write(f'spring.mail.password={mail_password}\n')
        prop_file.write(f'rules_engine.from_address={mail_username}\n')
        prop_file.write(f'rules_engine.reload_key={engine_reload_key}\n')
    with(open(file='backend/engine/config/application-demo.properties',
              mode='w', encoding='utf-8')) as prop_file:
        prop_file.write('spring.application.name=Rules Engine\n')
        prop_file.write(f'spring.datasource.url=jdbc:mysql://db:{db_host_port}/rules\n')
        prop_file.write(f'spring.datasource.username={db_user}\n')
        prop_file.write(f'spring.datasource.password={db_password}\n')
        prop_file.write('spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect\n')
        prop_file.write('spring.jpa.open-in-view=false\n')
        prop_file.write('spring.mail.host=email\n')
        prop_file.write(f'spring.mail.port={mail_host_port}\n')
        prop_file.write(f'spring.mail.username={mail_username}\n')
        prop_file.write(f'spring.mail.password={mail_password}\n')
        prop_file.write(f'rules_engine.from_address={mail_username}\n')
        prop_file.write(f'rules_engine.reload_key={engine_reload_key}\n')
    with(open(file='backend/engine/config/application-it.properties',
              mode='w', encoding='utf-8')) as prop_file:
        prop_file.write('spring.application.name=Rules Engine\n')
        prop_file.write(f'spring.datasource.url=jdbc:mysql://db_test:{db_host_port}/rules\n')
        prop_file.write(f'spring.datasource.username={db_user}\n')
        prop_file.write(f'spring.datasource.password={db_password}\n')
        prop_file.write('spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect\n')
        prop_file.write('spring.jpa.open-in-view=false\n')
        prop_file.write('spring.mail.host=email_test\n')
        prop_file.write(f'spring.mail.port={mail_host_port}\n')
        prop_file.write(f'spring.mail.username={mail_username}\n')
        prop_file.write(f'spring.mail.password={mail_password}\n')
        prop_file.write(f'rules_engine.from_address={mail_username}\n')
        prop_file.write(f'rules_engine.reload_key={engine_reload_key}\n')

if __name__ == '__main__':
    write_props()
