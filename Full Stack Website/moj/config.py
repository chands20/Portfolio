import os


basedir = os.path.abspath(os.path.dirname(__file__))


class Config:
    """Set Flask configuration variables."""

    # CRITICAL: Flask-WTF (forms) requires a SECRET_KEY
    # This key is used to prevent CSRF attacks.
    # Load the secret key from the .env file.
    # The app will crash if this is not set, which is good.
    SECRET_KEY = os.environ.get('SECRET_KEY')

    # Read from .env, but use the old path as a fallback
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL') or \
        'sqlite:///' + os.path.join(basedir, 'moj.db')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
