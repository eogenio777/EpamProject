-- читатели
CREATE TABLE followers
(
    follower_id SERIAL primary key ,
    follower_name VARCHAR(50) not null
);

-- издания
CREATE TABLE periodicals
(
    periodical_id SERIAL primary key,
    periodical_name VARCHAR(50) not null,
	about VARCHAR(500)
);


-- подписки - читатель-издание
CREATE TABLE subscriptions
(
	follower_id INTEGER REFERENCES followers(follower_id) ON DELETE CASCADE,
	periodical_id INTEGER REFERENCES periodicals(periodical_id) ON DELETE CASCADE
);