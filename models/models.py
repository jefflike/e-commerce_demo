'''
__title__ = 'models.py'
__author__ = 'Jeffd'
__time__ = '4/10/18 11:02 AM'
'''
from flask_sqlalchemy import SQLAlchemy
from flask_restful_blog import app
db = SQLAlchemy(app)


posts_tags = db.Table('posts_tags',
    db.Column('post_id', db.String(45), db.ForeignKey('posts.id')),
    db.Column('tag_id', db.String(45), db.ForeignKey('tags.id')))


class User(db.Model):
    """
    用户模型类
    """
    __tablename__ = 'users'
    id = db.Column(db.String(45), primary_key=True)
    username = db.Column('昵称', db.String(255))
    password = db.Column('密码', db.String(255))
    posts = db.relationship(
        'Post',
        backref='users',
        lazy='dynamic')

    def __repr__(self):
        return "<Model User `{}`>".format(self.username)


class Post(db.Model):
    __tablename__ = 'posts'
    id = db.Column(db.String(45), primary_key=True)
    title = db.Column(db.String(255))
    text = db.Column(db.Text())
    publish_date = db.Column(db.DateTime)
    # 外键，每个文章都只能对应一名用户，一个用户可以有多篇文章
    user_id = db.Column(db.String(45), db.ForeignKey('users.id'))
    comments = db.relationship(
        'Comment',
        backref='posts',
        lazy='dynamic')
    # seconddary会告知 SQLAlchemy 该many to many的关联保存在posts_tags表中
    # backref：声明表之间的关系是双向 one to many 中的 backref 是一个普通的对象，而在 many to many 中的 backref 是一个 List 对象
    tags = db.relationship(
        'Tag',
        secondary=posts_tags,
        backref=db.backref('posts', lazy='dynamic'))

    def __repr__(self):
        return "<Model Post `{}`>".format(self.title)


class Comment(db.Model):
    __tablename__ = 'comments'
    id = db.Column(db.String(45), primary_key=True)
    name = db.Column(db.String(255))
    text = db.Column(db.Text())
    date = db.Column(db.DateTime())
    post_id = db.Column(db.String(45), db.ForeignKey('posts.id'))

    def __repr__(self):
        return '<Model Comment `{}`>'.format(self.name)


class Tag(db.Model):
    __tablename__ = 'tags'
    id = db.Column(db.String(45), primary_key=True)
    name = db.Column(db.String(255))

    def __repr__(self):
        return "<Model Tag `{}`>".format(self.name)