'''
__title__ = '_02_other_response.py'
__author__ = 'Jeffd'
__time__ = '4/13/18 4:46 PM'
make_response,与01没什么差别只是使用了make_response函数
'''

import json
from flask import Flask, make_response, abort
from api.api_manager import JSON_MIME_TYPE, search_book

app = Flask(__name__)

books = [{
    'id': 33,
    'title': 'The Raven',
    'author_id': 1
}]


@app.route('/book')
def book_list():
    content = json.dumps(books)

    response = make_response(
        content, 200, {'Content-Type': JSON_MIME_TYPE})

    # 这里返回的是[{},]类型
    return response

@app.route('/book/<int:book_id>')
def book_detail(book_id):
    book = search_book(books, book_id)
    if book is None:
        abort(404)

    content = json.dumps(book)
    response = make_response(content, 200,
                             {'Content-Type': JSON_MIME_TYPE})
    return response


@app.errorhandler(404)
def not_found(e):
    return '没有查询到相关图书', 404