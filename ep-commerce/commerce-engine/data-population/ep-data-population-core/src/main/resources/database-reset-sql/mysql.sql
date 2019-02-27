-- SQL to drop the current EP database if exists.
DROP DATABASE IF EXISTS ${data.population.schemaname};

-- SQL to create the EP database
CREATE DATABASE ${data.population.schemaname} CHARACTER SET utf8;

-- SQL to grant the necessary database permissions on a new EP database
GRANT ALL on ${data.population.schemaname}.* TO '${data.population.username}'@'localhost' IDENTIFIED BY '${data.population.password}';
GRANT ALL on ${data.population.schemaname}.* TO '${data.population.username}'@'%' IDENTIFIED BY '${data.population.password}';
