from functools import wraps
from flask import abort
from flask_login import current_user
from moj.models import Joke


def admin_required(f):
    """
    A decorator to restrict access to admin users.
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        # 1. Check if the current_user's role is NOT 'admin'
        # 2. If it's not, call abort(403)
        if not current_user.is_authenticated or current_user.role != 'admin':
            abort(403)
        # If the check passes, run the original route function
        return f(*args, **kwargs)
    return decorated_function


def author_required(f):
    """
    A decorator to restrict access to the author of a resource (Joke).
    This expects the route to have a `joke_id` parameter (either as a kwarg
    or positional arg). It will 404 if the joke doesn't exist and 403
    if the current_user is not the author.
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        # Try to obtain joke_id from kwargs first, then fall back to args[0]
        joke_id = kwargs.get('joke_id', None)
        if joke_id is None:
            # If user used a positional arg (typical in routes), assume first arg
            if len(args) > 0:
                joke_id = args[0]
        # If we still don't have a joke_id, it's a bad usage of this decorator
        if joke_id is None:
            abort(400)

        # Load the joke (get_or_404 behaviour is useful; import used above)
        joke = Joke.query.get_or_404(joke_id)

        # Only author may proceed
        if joke.author != current_user:
            abort(403)

        # If all good, continue to the route function
        return f(*args, **kwargs)
    return decorated_function
