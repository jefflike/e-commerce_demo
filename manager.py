'''
__title__ = 'manager.py'
__author__ = 'Jeffd'
__time__ = '4/10/18 10:48 AM'
'''
from flask.ext.migrate import Migrate, MigrateCommand
from flask.ext.script import Manager
import flask_restful_blog
from models import models
manager = Manager(flask_restful_blog.app)
migrate = Migrate(flask_restful_blog.app, models.db)
manager.add_command("db", MigrateCommand)


@manager.shell
def make_shell_context():
    # 确保有导入 Flask app object，否则启动的 CLI 上下文中仍然没有 app 对象
    return dict(app=flask_restful_blog.app,
                db=models.db,
                User=models.User,
                Post=models.Post,
                Comment=models.Comment,
                Tag=models.Tag,)


if __name__ == '__main__':
    manager.run()