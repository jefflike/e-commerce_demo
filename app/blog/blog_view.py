'''
__title__ = 'blog_view.py'
__author__ = 'Jeffd'
__time__ = '4/10/18 10:28 PM'
'''
from flask import render_template

from app import db
from . import web
from app.models.models import User, Post, Comment, Tag


@web.route('/', methods=['GET', 'POST'])
@web.route('/<int:page>', methods=['GET', 'POST'])
def home(page=1):
    """View function for home page"""

    posts = Post.query.order_by(
        Post.publish_date.desc()
    ).paginate(page, 10)

    recent = Post().new_post
    top_tags = Tag().hot_tag

    return render_template('home.html',
                           posts=posts,
                           recent=recent,
                           top_tags=top_tags)


@web.route('/post/<string:post_id>')
def post(post_id):
    """View function for post page"""

    post = db.session.query(Post).get_or_404(post_id)
    tags = post.tags
    comments = post.comments.order_by(Comment.date.desc()).all()
    recent = Post.new_post
    top_tags = Tag.hot_tag

    return render_template('post.html',
                           post=post,
                           tags=tags,
                           comments=comments,
                           recent=recent,
                           top_tags=top_tags)


@web.route('/tag/<string:tag_name>')
def tag(tag_name):
    """View function for tag page"""

    tag = db.session.query(Tag).filter_by(name=tag_name).first_or_404()
    posts = tag.posts.order_by(Post.publish_date.desc()).all()
    recent = Post.new_post
    top_tags = Tag.hot_tag

    return render_template('tag.html',
                           tag=tag,
                           posts=posts,
                           recent=recent,
                           top_tags=top_tags)


@web.route('/user/<string:username>')
def user(username):
    """View function for user page"""
    user = db.session.query(User).filter_by(username=username).first_or_404()
    posts = user.posts.order_by(Post.publish_date.desc()).all()
    recent = Post.new_post
    top_tags = Tag.hot_tag

    return render_template('user.html',
                           user=user,
                           posts=posts,
                           recent=recent,
                           top_tags=top_tags)