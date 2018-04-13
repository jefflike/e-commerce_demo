'''
__title__ = '__init__.py.py'
__author__ = 'Jeffd'
__time__ = '4/10/18 10:28 PM'
'''
from flask import Flask
from flask_restful import Api

from app.models.base import db
import config
from app.blog.blog_view import PostApi



app = Flask(__name__)

app.config.from_object(config.config['development'])

restful_api = Api()


def create_app(config_name):
    app = Flask(__name__)
    app.config.from_object(config_name)

    db.init_app(app)
    restful_api.add_resource(
        PostApi,
        '/api/posts',
        '/api/posts/<string:post_id>',
        endpoint='restful_api_post'
    )
    restful_api.init_app(app)
    register_blueprint(app)
    return app


def register_blueprint(app):
    from app.blog import web
    app.register_blueprint(web)

if __name__ == '__main__':
    app.run()