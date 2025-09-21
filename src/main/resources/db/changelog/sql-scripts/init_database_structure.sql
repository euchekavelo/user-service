CREATE SCHEMA IF NOT EXISTS users_scheme;

CREATE TABLE IF NOT EXISTS users_scheme.photos (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	link CHARACTER VARYING NOT NULL,
    name CHARACTER VARYING NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    modification_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS users_scheme.users (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    last_name CHARACTER VARYING NOT NULL,
    first_name CHARACTER VARYING NOT NULL,
    middle_name CHARACTER VARYING,
    birth_date DATE,
    email CHARACTER VARYING NOT NULL UNIQUE,
    password CHARACTER VARYING,
    phone CHARACTER VARYING UNIQUE,
	photo_id UUID,
	sex	CHARACTER VARYING(6) NOT NULL,
	CHECK(sex = 'MALE' OR sex = 'FEMALE'),
	FOREIGN KEY (photo_id) REFERENCES users_scheme.photos (id) ON UPDATE CASCADE
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


----- Index creation section -----
CREATE INDEX index_sex ON users_scheme.users USING HASH(sex);
CREATE INDEX index_fk_photo_id ON users_scheme.users USING HASH(photo_id);

CREATE INDEX index_fk_source_user_id ON users_scheme.user_subscriptions USING HASH(source_user_id);
CREATE INDEX index_fk_destination_user_id ON users_scheme.user_subscriptions USING HASH(destination_user_id);