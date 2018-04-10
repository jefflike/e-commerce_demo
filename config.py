'''
__title__ = 'config.py'
__author__ = 'Jeffd'
__time__ = '4/10/18 10:40 AM'
'''
import os


class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'hard to guess string'
    SSL_DISABLE = False
    SQLALCHEMY_COMMIT_ON_TEARDOWN = True
    SQLALCHEMY_RECORD_QUERIES = True
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    MAIL_SERVER = 'smtp.sina.com'
    MAIL_PORT = 25
    MAIL_USE_TLS = True
    MAIL_USERNAME = 'emailforproject@sina.com'
    MAIL_PASSWORD = 'admin@123'
    SQLALCHEMY_DATABASE_URI = 'mysql+cymysql://root:root@localhost:3306/blogdev'
    ADMINS = ['emailforproject@sina.com']

    @staticmethod
    def init_app(app):
        pass


class DevelopmentConfig(Config):
    DEBUG = True
    SQLALCHEMY_DATABASE_URI = 'mysql+cymysql://root:root@localhost:3306/blogdev'


config = {
    'development': DevelopmentConfig,
    'default': DevelopmentConfig
}