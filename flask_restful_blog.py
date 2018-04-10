'''
__title__ = 'flask_restful_blog.py'
__author__ = 'Jeffd'
__time__ = '4/10/18 11:55 PM'
'''
from flask_migrate import Migrate, MigrateCommand
from flask_script import Manager

from app import create_app
from app.models.models import db
from config import Config


app = create_app(Config)

manager = Manager(app)
migrate = Migrate(app, db)
manager.add_command('db', MigrateCommand)

if __name__ == '__main__':
    manager.run()