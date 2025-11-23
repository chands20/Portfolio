import pytest
from moj import db
from moj.models import User


@pytest.fixture
def admin_user(app):
    """Create and return an admin user."""
    admin = User(username='admin_user', email='admin@example.com', role='admin')
    admin.set_password('AdminPassword123!')
    db.session.add(admin)
    db.session.commit()
    return admin


@pytest.fixture
def regular_user(app):
    """Create and return a regular user."""
    user = User(username='regular_user', email='user@example.com', role='user')
    user.set_password('UserPassword123!')
    db.session.add(user)
    db.session.commit()
    return user


def login(client, username, password):
    """Helper to log in a user."""
    return client.post('/login', data={
        'username': username,
        'password': password
    }, follow_redirects=True)


def test_admin_can_edit_user_role(client, app, admin_user, regular_user):
    """
    GIVEN a regular user and a logged-in admin_user
    WHEN the admin_user POSTs to /admin/edit_user/<user_id> with a new role
    THEN the user's role is changed in the database
    """
    # Log in as admin
    login(client, 'admin_user', 'AdminPassword123!')

    # Send POST request to change the regular user's role
    response = client.post(f'/admin/edit_user/{regular_user.id}', data={
        'username': regular_user.username,
        'email': regular_user.email,
        'role': 'moderator',
        'is_active': 'y',  # checkbox equivalent
        'submit': True
    }, follow_redirects=True)

    # Check response (should be 200 after redirect)
    assert response.status_code == 200
    assert b"role updated" in response.data or b"success" in response.data

    # Confirm the role was updated in the database
    updated_user = db.session.get(User, regular_user.id)
    assert updated_user.role == 'moderator'


def test_user_cannot_edit_user_role(client, app, regular_user):
    """
    GIVEN a regular user_A and a logged-in user_B
    WHEN user_B attempts to POST to /admin/edit_user/<user_A_id>
    THEN the response is 403 Forbidden, and user_A.role is unchanged
    """
    # Create a second non-admin user
    user_b = User(username='other_user', email='other@example.com', role='user')
    user_b.set_password('OtherPassword123!')
    db.session.add(user_b)
    db.session.commit()

    # Log in as user_b
    login(client, 'other_user', 'OtherPassword123!')

    # Attempt to edit user_A (regular_user)
    response = client.post(f'/admin/edit_user/{regular_user.id}', data={
        'role': 'admin'
    })

    # Should be forbidden
    assert response.status_code == 403

    # Ensure role was NOT changed
    unchanged_user = db.session.get(User, regular_user.id)
    assert unchanged_user.role == 'user'
