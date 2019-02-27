USE master;

-- SQL to drop the current EP database.
IF EXISTS ( SELECT [name] FROM sys.databases  WHERE [name] = '${data.population.schemaname}' )
DROP DATABASE [${data.population.schemaname}];

-- SQL to create the EP database
CREATE DATABASE [${data.population.schemaname}];

-- SQL to grant the necessary database permissions on a new EP database
USE [${data.population.schemaname}];
