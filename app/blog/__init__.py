'''
__title__ = '__init__.py.py'
__author__ = 'Jeffd'
__time__ = '4/10/18 10:34 PM'
'''
from flask import Blueprint


web = Blueprint('web', __name__, template_folder='templates')

from app.blog import blog_view