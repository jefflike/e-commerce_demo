'''
__title__ = 'blog_view.py'
__author__ = 'Jeffd'
__time__ = '4/10/18 10:28 PM'
'''
from uuid import uuid4
from flask.ext.restful import Resource, fields, marshal_with
# from app.fields import fields as jf_fields

from datetime import datetime
from flask import render_template

from app import db
from app.forms.forms import CommentForm
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


@web.route('/post/<string:post_id>', methods=('GET', 'POST'))
def post(post_id):
    """View function for post page"""

    # Form object: `Comment`
    form = CommentForm()
    # form.validate_on_submit() will be true and return the
    # data object to form instance from user enter,
    # when the HTTP request is POST
    if form.validate_on_submit():
        new_comment = Comment()
        new_comment.id=str(uuid4())
        new_comment.name=form.name.data
        new_comment.text = form.text.data
        new_comment.date = datetime.now()
        new_comment.post_id = post_id
        db.session.add(new_comment)
        db.session.commit()

    post = Post.query.get_or_404(post_id)
    tags = post.tags
    comments = post.comments.order_by(Comment.date.desc()).all()
    recent = Post().new_post
    top_tags = Tag().hot_tag

    return render_template('post.html',
                           post=post,
                           tags=tags,
                           comments=comments,
                           form=form,
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


# String format output of tag
nested_tag_fields = {
    'id': fields.String(),
    'name': fields.String()}

# String format output of post
post_fields = {
    'author': fields.String(attribute=lambda x: x.user.username),
    'title': fields.String(),
    # 'text': jf_fields.HTMLField(),
    'tags': fields.List(fields.Nested(nested_tag_fields)),
    'publish_date': fields.DateTime(dt_format='iso8601')}


class PostApi(Resource):
    """Restful API of posts resource."""
    @marshal_with(post_fields)
    def get(self, post_id=None):
        """Can be execute when receive HTTP Method `GET`.
           Will be return the Dict object as post_fields.
        """
        if post_id:
            return {'post_id': post_id}
        return {'hello': 'world'}