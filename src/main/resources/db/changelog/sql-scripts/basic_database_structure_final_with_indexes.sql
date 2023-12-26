CREATE TABLE IF NOT EXISTS users_scheme.towns (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(), 
	name CHARACTER VARYING NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users_scheme.users (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fullname CHARACTER VARYING NOT NULL,
    birth_date DATE,
    email CHARACTER VARYING NOT NULL,
    phone CHARACTER VARYING,
	town_id UUID,
	sex	CHARACTER VARYING(6) NOT NULL,
	CHECK(sex = 'MALE' OR sex = 'FEMALE'),
	FOREIGN KEY (town_id) REFERENCES users_scheme.towns (id) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS users_scheme.user_subscriptions (
	source_user_id UUID, 
	destination_user_id UUID,
	creation_time TIMESTAMP NOT NULL DEFAULT now(), 
	PRIMARY KEY(source_user_id, destination_user_id),
	CHECK(source_user_id != destination_user_id),
	FOREIGN KEY (source_user_id) REFERENCES users_scheme.users (id) ON DELETE CASCADE ON UPDATE CASCADE, 
	FOREIGN KEY (destination_user_id) REFERENCES users_scheme.users (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS users_scheme.groups (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(), 
	name CHARACTER VARYING NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users_scheme.users_groups (
	user_id UUID, 
	group_id UUID,
	PRIMARY KEY(user_id, group_id),
	FOREIGN KEY (user_id) REFERENCES users_scheme.users (id) ON DELETE CASCADE ON UPDATE CASCADE, 
	FOREIGN KEY (group_id) REFERENCES users_scheme.groups (id) ON DELETE CASCADE ON UPDATE CASCADE
);

----- Index creation section -----
CREATE INDEX index_sex ON users_scheme.users USING HASH(sex);
CREATE INDEX index_fk_town_id ON users_scheme.users USING HASH(town_id);
CREATE INDEX index_sex_fk_town_id ON users_scheme.users(sex, town_id);

CREATE INDEX index_fk_source_user_id ON users_scheme.user_subscriptions USING HASH(source_user_id);
CREATE INDEX index_fk_destination_user_id ON users_scheme.user_subscriptions USING HASH(destination_user_id);

CREATE INDEX index_fk_user_id ON users_scheme.users_groups USING HASH(user_id);
CREATE INDEX index_fk_group_id ON users_scheme.users_groups USING HASH(group_id);