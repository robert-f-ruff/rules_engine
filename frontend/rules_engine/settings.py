"""
Django settings for rules_engine project.

Generated by 'django-admin startproject' using Django 4.1.4.

For more information on this file, see
https://docs.djangoproject.com/en/4.1/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/4.1/ref/settings/
"""

import os
from pathlib import Path
from typing import Any
from dotenv import load_dotenv, find_dotenv
from rules.core import retrieve_setting

# Build paths inside the project like this: BASE_DIR / 'subdir'.
BASE_DIR = Path(__file__).resolve().parent.parent


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/4.1/howto/deployment/checklist/

load_dotenv(find_dotenv())

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = retrieve_setting('django_secret_key')

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

ALLOWED_HOSTS = []


# Application definition

INSTALLED_APPS = [
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'rules.apps.RulesConfig',
]

MIDDLEWARE = [
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]

ROOT_URLCONF = 'rules_engine.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
            ],
        },
    },
]

WSGI_APPLICATION = 'rules_engine.wsgi.application'


# Database
# https://docs.djangoproject.com/en/4.1/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'rules',
        'USER': retrieve_setting('db_user_name'),
        'PASSWORD': retrieve_setting('db_user_password'),
        'HOST': retrieve_setting('db_host'),
        'PORT': retrieve_setting('db_host_port'),
    }
}


# Password validation
# https://docs.djangoproject.com/en/4.1/ref/settings/#auth-password-validators

AUTH_PASSWORD_VALIDATORS = [
    {
        'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator',
    },
]


# Internationalization
# https://docs.djangoproject.com/en/4.1/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'America/Los_Angeles'

USE_I18N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/4.1/howto/static-files/

STATIC_URL = 'static/'
STATIC_ROOT = 'web_server/static/'


# Default primary key field type
# https://docs.djangoproject.com/en/4.1/ref/settings/#default-auto-field

DEFAULT_AUTO_FIELD = 'django.db.models.BigAutoField'

def get_handlers() -> dict[str, Any]:
    """ Return the appropriate logging handler definition(s) for the current environment.
    """
    handlers = {}
    running_in_docker = os.environ.get('running_in_docker', '')
    if running_in_docker == '':
        handlers['file'] = {
            'class': 'logging.handlers.RotatingFileHandler',
            'formatter': 'detailed',
            'filename': '/app/log/rules_engine.log',
            'encoding': 'utf-8',
            'maxBytes': 104857600,
            'backupCount': 5,
        }
    handlers['console'] = {
        'class': 'logging.StreamHandler',
        'formatter': 'detailed',
    }
    return handlers

def get_handler_ids() -> list[str]:
    """ Return the appropriate list of logging handlers for the current environment.
    """
    running_in_docker = os.environ.get('running_in_docker', '')
    if running_in_docker == 'no':
        return ['console']
    return ['console', 'file']

LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'formatters': {
        'detailed': {
            'format': '{asctime} {levelname:<8s} [{module}.{funcName}] ({threadName}) {message}',
            'style': '{',
        },
    },
    'handlers': get_handlers(),
    'loggers': {
        'django': {
            'handlers': get_handler_ids(),
            'propagate': True,
            'level': 'INFO',
        },
        'rules_engine': {
            'handlers': get_handler_ids(),
            'level': os.environ.get('logging_level', 'INFO'),
        },
    },
}
