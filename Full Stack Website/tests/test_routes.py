from moj.models import User, Joke
from moj import db


def test_hello_world(client):
    """
    GIVEN a configured test client (from conftest.py)
    WHEN the '/' route is requested (GET)
    THEN check that the response is a 302 (redirect)
    """
    # The index route is now protected by @login_required
    # An unauthenticated client should be redirected.
    response = client.get('/')
    assert response.status_code == 302
    assert 'login' in response.location  # Check that it redirects to login


# -----------------------------------------------------
# TEST: Edit Joke (happy path)
# -----------------------------------------------------
def test_edit_joke(client, app):
    """
    GIVEN a logged-in user and an existing joke
    WHEN they POST to /edit_joke/<id> with new text
    THEN the joke's body is updated in the database
    """
    # Setup: create user and joke
    with app.app_context():
        user = User(username='user1', email='u1@example.com')
        user.set_password('password')
        db.session.add(user)
        db.session.commit()

        joke = Joke(body='Old joke text', author=user)
        db.session.add(joke)
        db.session.commit()
        joke_id = joke.id

    # Log in the user
    client.post('/login', data={'username': 'user1', 'password': 'password'}, follow_redirects=True)

    # POST updated joke text
    response = client.post(f'/edit_joke/{joke_id}', data={'body': 'Updated joke text'},
                           follow_redirects=True)
    assert response.status_code == 200

    # Verify the joke body was updated
    with app.app_context():
        updated_joke = Joke.query.get(joke_id)
        assert updated_joke.body == 'Updated joke text'


# -----------------------------------------------------
# TEST: Cannot Edit Another User's Joke (AuthZ)
# -----------------------------------------------------
def test_cannot_edit_others_joke(client, app):
    """
    GIVEN two users and a joke authored by user_a
    WHEN user_b tries to edit user_a's joke
    THEN a 403 Forbidden response is returned
    """
    with app.app_context():
        user_a = User(username='userA', email='a@example.com')
        user_a.set_password('password')
        user_b = User(username='userB', email='b@example.com')
        user_b.set_password('password')
        db.session.add_all([user_a, user_b])
        db.session.commit()

        joke = Joke(body='Original joke text', author=user_a)
        db.session.add(joke)
        db.session.commit()
        joke_id = joke.id

    # Log in as user_b
    client.post('/login', data={'username': 'userB', 'password': 'password'}, follow_redirects=True)

    # Attempt to edit user_a's joke
    response = client.post(f'/edit_joke/{joke_id}', data={'body': 'Hacked joke text'})
    assert response.status_code == 403

    # Ensure the joke body was not changed
    with app.app_context():
        unchanged_joke = Joke.query.get(joke_id)
        assert unchanged_joke.body == 'Original joke text'


# -----------------------------------------------------
# TEST: Delete Joke (happy path)
# -----------------------------------------------------
def test_delete_joke(client, app):
    """
    GIVEN a logged-in user and a joke
    WHEN they POST to /delete_joke/<id>
    THEN the joke is removed from the database
    """
    with app.app_context():
        user = User(username='deleter', email='d@example.com')
        user.set_password('password')
        db.session.add(user)
        db.session.commit()

        joke = Joke(body='Joke to delete', author=user)
        db.session.add(joke)
        db.session.commit()
        joke_id = joke.id

    # Log in as the joke's author
    client.post('/login', data={'username': 'deleter', 'password': 'password'},
                follow_redirects=True)

    # POST to delete route
    response = client.post(f'/delete_joke/{joke_id}', follow_redirects=True)
    assert response.status_code == 200

    # Verify the joke was deleted
    with app.app_context():
        deleted = Joke.query.get(joke_id)
        assert deleted is None


# -----------------------------------------------------
# TEST: Cannot Delete Another User's Joke (AuthZ)
# -----------------------------------------------------
def test_cannot_delete_others_joke(client, app):
    """
    GIVEN two users and a joke authored by user_a
    WHEN user_b tries to delete it
    THEN the response is 403 and the joke still exists
    """
    with app.app_context():
        user_a = User(username='alice', email='alice@example.com')
        user_a.set_password('password')
        user_b = User(username='bob', email='bob@example.com')
        user_b.set_password('password')
        db.session.add_all([user_a, user_b])
        db.session.commit()

        joke = Joke(body='Cannot delete this', author=user_a)
        db.session.add(joke)
        db.session.commit()
        joke_id = joke.id

    # Log in as user_b
    client.post('/login', data={'username': 'bob', 'password': 'password'}, follow_redirects=True)

    # Try to delete user_a's joke
    response = client.post(f'/delete_joke/{joke_id}')
    assert response.status_code == 403

    # Ensure joke is still in the database
    with app.app_context():
        still_there = Joke.query.get(joke_id)
        assert still_there is not None
        assert still_there.body == 'Cannot delete this'
