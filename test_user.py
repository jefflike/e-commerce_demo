'''
__title__ = 'test_user.py'
__author__ = 'Jeffd'
__time__ = '4/11/18 12:02 PM'
'''
import random
import datetime
from uuid import uuid4

from app.models.models import db, User, Tag, Post

user = User()
user.id=str(uuid4())
user.username='no'
user.password='666'
db.session.add(user)
db.session.commit()

user = db.session.query(User).first()
tag_one = Tag(id=str(uuid4()), name='Python')
tag_two = Tag(id=str(uuid4()), name='Flask')
tag_three = Tag(id=str(uuid4()), name='SQLALchemy')
tag_four = Tag(id=str(uuid4()), name='RESTFUL')
tag_list = [tag_one, tag_two, tag_three, tag_four]

s = "EXAMPLE TEXT"

for i in range(100):
    new_post = Post()
    new_post.id=str(uuid4())
    new_post.title="Post" + str(i)
    new_post.user = user
    new_post.publish_date = datetime.datetime.now()
    new_post.text = s
    new_post.tags = random.sample(tag_list, random.randint(1, 3))
    db.session.add(new_post)

db.session.commit()