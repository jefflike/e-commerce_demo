'''
__title__ = 'forms.py'
__author__ = 'Jeffd'
__time__ = '4/11/18 12:31 AM'
'''
from flask_wtf import Form
from wtforms import StringField, TextField
from wtforms.validators import DataRequired, Length


class CommentForm(Form):
    """Form vaildator for comment."""

    # Set some field(InputBox) for enter the data.
    # patam validators: setup list of validators
    name = StringField(
        'Name',
        validators=[DataRequired(), Length(max=255)])

    text = TextField('Comment', validators=[DataRequired()])