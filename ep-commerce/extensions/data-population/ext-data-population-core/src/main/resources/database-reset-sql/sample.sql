-- Store your database initialization scripts in this directory path
-- SQL to drop the current EP database.
DROP DATABASE IF EXISTS ${data.population.schemaname};

-- SQL to create the EP database
CREATE DATABASE ${data.population.schemaname};

-- SQL to grant the necessary database permissions on a new EP database
GRANT ALL on ${data.population.schemaname}.* TO '${data.population.username}'@'%' IDENTIFIED BY '${data.population.password}';
