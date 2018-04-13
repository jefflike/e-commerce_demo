'''
__title__ = 'like_josn_style.py'
__author__ = 'Jeffd'
__time__ = '4/13/18 4:21 PM'
__about__ = '使用json,dumps的方式返回api'
'''
import json
from flask import Flask, Response, abort
from api.api_manager import search_book, JSON_MIME_TYPE

app = Flask(__name__)

books = [{
    'id': 33,
    'title': 'The Raven',
    'author_id': 1
}]


@app.route('/book')
def book_list():
    response = Response(json.dumps(books),
                        status=200,
                        mimetype=JSON_MIME_TYPE)
    return response


@app.route('/book/<int:book_id>')
def book_detail(book_id):
    book = search_book(books, book_id)
    if book is None:
        abort(404)

    content = json.dumps(book)
    return content, 200, {'Content-Type': JSON_MIME_TYPE}


@app.errorhandler(404)
def not_found(e):
    return '没有查询到相关图书', 404