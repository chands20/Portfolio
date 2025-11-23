import re
from flask_wtf import FlaskForm
from wtforms import StringField, PasswordField, BooleanField, SubmitField, TextAreaField
from wtforms import SelectField
from wtforms.validators import DataRequired, ValidationError, Email, EqualTo, Length
from moj.models import User


class LoginForm(FlaskForm):
    """Form for user login."""
    username = StringField('Username', validators=[DataRequired()])
    password = PasswordField('Password', validators=[DataRequired()])
    remember_me = BooleanField('Remember Me')
    submit = SubmitField('Sign In')


class RegistrationForm(FlaskForm):
    """Form for new user registration."""
    username = StringField('Username', validators=[DataRequired()])
    email = StringField('Email', validators=[DataRequired(), Email()])
    password = PasswordField('Password', validators=[DataRequired()])
    password2 = PasswordField(
        'Repeat Password', validators=[DataRequired(), EqualTo('password')])
    submit = SubmitField('Register')

    # Custom validator
    def validate_username(self, username):
        user = User.query.filter_by(username=username.data).first()
        if user is not None:
            raise ValidationError('Please use a different username.')

    # Custom validator
    def validate_email(self, email):
        user = User.query.filter_by(email=email.data).first()
        if user is not None:
            raise ValidationError('Please use a different email address.')


class JokeForm(FlaskForm):
    """Form for submitting a new joke."""
    body = TextAreaField('Joke Body', validators=[
        DataRequired(), Length(min=1, max=280)])
    submit = SubmitField('Submit Joke')


class ChangePasswordForm(FlaskForm):
    """Form for changing user password."""
    old_password = PasswordField('Old Password', validators=[DataRequired()])
    new_password = PasswordField('New Password', validators=[DataRequired()])
    new_password2 = PasswordField(
        'Repeat New Password',
        validators=[DataRequired(), EqualTo('new_password', message='Passwords must match')]
    )
    submit = SubmitField('Change Password')

    def validate_new_password(self, new_password):
        """Custom validator to enforce password complexity rules."""
        password = new_password.data

        # Rule 1: At least 15 characters
        if len(password) < 15:
            raise ValidationError('Password must be at least 15 characters long.')

        # Rule 2: At least one digit
        if not re.search(r'\d', password):
            raise ValidationError('Password must contain at least one digit (0-9).')

        # Rule 3: At least one uppercase letter
        if not re.search(r'[A-Z]', password):
            raise ValidationError('Password must contain at least one uppercase letter (A-Z).')

        # Rule 4: At least one lowercase letter
        if not re.search(r'[a-z]', password):
            raise ValidationError('Password must contain at least one lowercase letter (a-z).')

        # Rule 5: At least one symbol
        if not re.search(r'[!@#$%^&*(),.?":{}|<>]', password):
            raise ValidationError(
                'Password must contain at least one special symbol (e.g., !@#$%^&*).')


class AdminJokeForm(JokeForm):
    """
    Extends the base JokeForm with a mandatory
    justification field for admin actions.
    """
    # We inherit 'body' and 'submit' from JokeForm

    # We just add the new field:
    justification = TextAreaField('Admin Justification', validators=[DataRequired(),
                                                                     Length(min=5, max=256)])


class RatingForm(FlaskForm):
    # We use 'coerce=int' to make sure the data comes back as a number
    score = SelectField('Rating (1-5)',
                        choices=[(1, '1 Star'), (2, '2 Stars'), (3, '3 Stars'),
                                 (4, '4 Stars'), (5, '5 Stars')],
                        coerce=int)
    submit = SubmitField('Submit Rating')


class AdminUserForm(FlaskForm):
    """
    Form for administrators to manage user accounts.
    """
    username = StringField('Username', validators=[DataRequired()])
    email = StringField('Email', validators=[DataRequired(), Email()])
    role = SelectField('Role', choices=[
        ('user', 'User'),
        ('moderator', 'Moderator'),
        ('admin', 'Admin')
    ], validators=[DataRequired()])
    is_active = BooleanField('Active')
    submit = SubmitField('Update User')