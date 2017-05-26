import logging
import logging.config
import os.path
from configparser import ConfigParser

import os

encoding = 'utf-8'


def load_config() -> ConfigParser:
    def load() -> ConfigParser:
        app_config = ConfigParser()
        app_config.read(os.path.join('resources', 'cfg', 'application.conf'), encoding=encoding)

        user_app_config = os.getenv('CONFIG_PATH', './application.conf')
        if os.path.exists(user_app_config) and os.path.isfile(user_app_config):
            app_config.read(user_app_config, encoding=encoding)

        return app_config

    def validate(conf: ConfigParser) -> ConfigParser:
        sections = {
            'bot': ['token']
        }

        for section, options in sections.items():
            if not conf.has_section(section):
                raise ValueError("Config is not valid!",
                                 "Section '{}' is missing!".format(section))
            for option in options:
                if not conf.has_option(section, option):
                    raise ValueError("Config is not valid!",
                                     "Option '{}' in section '{}' is missing!".format(option, section))

        return conf

    return validate(load())


def setup_logging():
    log_config = ConfigParser()
    log_config.read(os.path.join('resources', 'cfg', 'logging.conf'), encoding=encoding)

    user_log_config = os.getenv('LOGGING_CONFIG_PATH', './logging.conf')
    if os.path.exists(user_log_config) and os.path.isfile(user_log_config):
        log_config.read(user_log_config, encoding=encoding)

    logging.config.fileConfig(log_config)


setup_logging()
config = load_config()

# IOC
from .service.datasource import *

hdd_ds = HDDDataSource()
ram_ds = RAMDataSource()
web_ds = WebDataSource()

# from service import *
# image_retriever = ImageRetriever()