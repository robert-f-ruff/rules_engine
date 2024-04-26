"""This module contains functions used in multiple scripts."""
import os

def retrieve_setting(setting: str) -> str:
    """ Retrieve the specified setting from the corresponding secret file.
    """
    running_in_docker = os.environ.get('running_in_docker', '')
    if running_in_docker == 'no':
        path = os.path.abspath(__file__).split(os.path.sep)
        secrets_path = os.path.sep.join(path[0:len(path) - 2]) + os.path.sep + 'secret-'
        extension = '.txt'
    else:
        secrets_path = '/run/secrets/'
        extension = ''
    file = f'{secrets_path}{setting}{extension}'
    with open(file=file, mode='r', encoding='utf-8') as secret_file:
        return secret_file.read()
