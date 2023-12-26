ALTER TABLE users_scheme.towns
ADD deleted boolean DEFAULT false;

ALTER TABLE users_scheme.users
ADD deleted boolean DEFAULT false;

ALTER TABLE users_scheme.user_subscriptions
ADD deleted boolean DEFAULT false;

ALTER TABLE users_scheme.groups
ADD deleted boolean DEFAULT false;

ALTER TABLE users_scheme.users_groups
ADD deleted boolean DEFAULT false;