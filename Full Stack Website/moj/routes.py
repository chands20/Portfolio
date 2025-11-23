from moj import app
from flask import abort
from flask_login import login_user, logout_user, current_user, login_required
from flask import render_template, redirect, url_for, request, flash
from moj.models import User, Joke, Rating, UserAction
from moj import db
from moj.forms import LoginForm, RegistrationForm, JokeForm, ChangePasswordForm, RatingForm
from moj.forms import AdminJokeForm, AdminUserForm
from moj.decorators import admin_required, author_required


@app.route('/')
@app.route('/index')
@login_required
def index():
    """Renders the main index.html page with a feed of jokes."""
    # 1. Query the database
    jokes_list = Joke.query.order_by(Joke.timestamp.desc()).all()

    # 2. Pass the list to the template
    return render_template('index.html', title='Home', jokes=jokes_list)


@app.route('/login', methods=['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        return redirect(url_for('index'))

    form = LoginForm()   # <-- Instantiate the form
    if form.validate_on_submit():
        user = User.query.filter_by(username=form.username.data).first()
        if user is None or not user.check_password(form.password.data):
            flash('Invalid username or password')
            return redirect(url_for('login'))
        login_user(user, remember=form.remember_me.data)
        return redirect(url_for('index'))

    return render_template('login.html', title='Sign In', form=form)   # <-- Pass 'form'


@app.route('/register', methods=['GET', 'POST'])
def register():
    if current_user.is_authenticated:
        return redirect(url_for('index'))

    form = RegistrationForm()   # <-- Instantiate the form
    if form.validate_on_submit():
        user = User(username=form.username.data, email=form.email.data)
        user.set_password(form.password.data)
        db.session.add(user)
        db.session.commit()
        flash('Congratulations, you are now a registered user!')
        return redirect(url_for('login'))

    return render_template('register.html', title='Register', form=form)   # <-- Pass 'form'


@app.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('login'))    # Send them to login page


@app.route('/staff_lounge')
@login_required   # This is the AuthN (authentication) check
def staff_lounge():
    return "Welcome to the staff lounge, {}!".format(current_user.username)


@app.route('/admin_panel')
@login_required
@admin_required
def admin_panel():
    # Get data for the dashboard
    users = User.query.order_by(User.username).all()
    jokes = Joke.query.order_by(Joke.timestamp.desc()).all()

    # A clean, fast, and maintainable query
    admin_types = [UserAction.ADMIN_EDIT_JOKE, UserAction.ADMIN_EDIT_USER]

    actions = UserAction.query.filter(
        UserAction.action_type.in_(admin_types)  # <-- Using .in_()
    ).order_by(UserAction.timestamp.desc()).all()

    return render_template('admin_panel.html', title="Admin Panel", users=users, jokes=jokes,
                           actions=actions)


@app.route('/submit_joke', methods=['GET', 'POST'])
@login_required
def submit_joke():
    form = JokeForm()
    if form.validate_on_submit():
        joke = Joke(body=form.body.data, author=current_user)
        db.session.add(joke)
        db.session.commit()
        flash('Your joke has been submitted!')
        return redirect(url_for('index'))

    return render_template('submit_joke.html', title='Submit Joke', form=form)


@app.route('/profile/<username>')   # <-- This is a dynamic route!
@login_required
def profile(username):
    """
    Shows a user's profile page, complete with their jokes.
    """
    # 1. Query for the user, or return a 404
    #    This is the "R" in CRUD for the User model.
    user = User.query.filter_by(username=username).first_or_404()

    # 2. Query for that user's jokes
    #    This is the "R" in CRUD for the Joke model, filtered!
    jokes = Joke.query.filter_by(author=user).order_by(Joke.timestamp.desc()).all()

    # 3. Pass the user and their jokes to the template
    return render_template('profile.html', user=user, jokes=jokes)


@app.route('/change_password', methods=['GET', 'POST'])
@login_required
def change_password():
    form = ChangePasswordForm()
    if form.validate_on_submit():
        # 1. Verify old password
        if not current_user.check_password(form.old_password.data):
            flash('Incorrect old password. Please try again.')
            return redirect(url_for('change_password'))

        # 2. Set and save new password
        current_user.set_password(form.new_password.data)
        db.session.commit()
        flash('Your password has been changed successfully!')
        return redirect(url_for('index'))

    return render_template('change_password.html', title='Change Password', form=form)


@app.route('/edit_joke/<int:joke_id>', methods=['GET', 'POST'])
@login_required
@author_required
def edit_joke(joke_id):
    # 1. Find the joke or return 404
    joke = Joke.query.get_or_404(joke_id)

    # 2. Authorization check — only the author can edit
    if joke.author != current_user:
        abort(403)

    # 3. Reuse the JokeForm
    form = JokeForm()

    # 4. Handle POST: update and save
    if form.validate_on_submit():
        joke.body = form.body.data
        db.session.commit()
        flash('Your joke has been updated!')
        return redirect(url_for('profile', username=current_user.username))

    # 5. Handle GET: pre-populate the form
    elif request.method == 'GET':
        form.body.data = joke.body

    # 6. Render the edit page
    return render_template('edit_joke.html', title='Edit Joke', form=form)


@app.route('/rate_joke/<int:joke_id>', methods=['GET', 'POST'])
@login_required
def rate_joke(joke_id):
    joke = Joke.query.get_or_404(joke_id)
    form = RatingForm()

    # --- RBAC / Business Logic Checks ---
    # 1. You can't rate your own joke
    if joke.author == current_user:
        flash("You cannot rate your own joke.", "error")
        return redirect(url_for('index'))

    # 2. You can only rate a joke once
    existing_rating = Rating.query.filter_by(user_id=current_user.id,
                                             joke_id=joke.id).first()
    if existing_rating:
        flash("You have already rated this joke.", "info")
        return redirect(url_for('index'))
    # --- End Checks ---

    if form.validate_on_submit():
        new_rating = Rating(
            score=form.score.data,
            user_id=current_user.id,
            joke_id=joke.id
        )
        db.session.add(new_rating)
        db.session.commit()
        flash("Your rating has been submitted!", "success")
        return redirect(url_for('index'))

    return render_template('rate_joke.html', title='Rate Joke',
                           form=form, joke=joke)


@app.route('/delete_joke/<int:joke_id>', methods=['POST'])
@login_required
@author_required
def delete_joke(joke_id):
    # 1. Find the joke or return 404
    joke = Joke.query.get_or_404(joke_id)
    # 2. Delete the joke
    db.session.delete(joke)
    db.session.commit()
    flash('Joke deleted.')

    # 4. Redirect to the user's profile
    return redirect(url_for('profile', username=current_user.username))


@app.route('/admin/edit_joke/<int:joke_id>', methods=['GET', 'POST'])
@login_required
@admin_required
def admin_edit_joke(joke_id):
    joke = Joke.query.get_or_404(joke_id)
    form = AdminJokeForm()   # <-- Use the new admin form

    if form.validate_on_submit():
        # 2. Perform the action
        joke.body = form.body.data
        db.session.commit()

        # 2. "CONNECT THE WIRE"
        new_action = UserAction(
            user=current_user,
            action_type=UserAction.ADMIN_EDIT_JOKE,  # <-- Use the constant!
            target_type="Joke",
            target_id=joke.id,
            details=form.justification.data  # <-- Save the justification!
        )
        db.session.add(new_action)

        # 3. Commit both
        db.session.commit()

        flash('Admin edit successful. Action logged.')
        return redirect(url_for('admin_panel'))

    elif request.method == 'GET':
        form.body.data = joke.body

    return render_template('admin_edit_joke.html', title='Admin Edit Joke', form=form, joke=joke)


@app.route('/admin/edit_user/<int:user_id>', methods=['GET', 'POST'])
@login_required
@admin_required
def admin_edit_user(user_id):
    # --- 1. Query for the user being edited ---
    user = User.query.get_or_404(user_id)
    form = AdminUserForm()

    # --- 2. Handle POST ---
    if form.validate_on_submit():
        # Update only if there's a change
        if user.role != form.role.data:
            user.role = form.role.data
            db.session.commit()
            flash(f"User '{user.username}' role updated to '{user.role}'.", "success")
        else:
            flash("No changes made to the user's role.", "info")

        return redirect(url_for('admin_panel'))

    # --- 3. Handle GET: Pre-populate form with current role ---
    elif request.method == 'GET':
        form.username.data = user.username
        form.email.data = user.email
        form.role.data = user.role
        form.is_active.data = user.is_active

    # --- 4. Render Template ---
    return render_template('admin_edit_user.html', title='Edit User', form=form, user=user)