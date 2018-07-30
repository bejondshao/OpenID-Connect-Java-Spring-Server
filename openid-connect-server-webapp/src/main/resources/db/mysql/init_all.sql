--
-- Temporary tables used during the bootstrapping process to safely load users and clients.
-- These are not needed if you're not using the users.sql/clients.sql files to bootstrap the database.
--

CREATE TEMPORARY TABLE IF NOT EXISTS authorities_TEMP (
      username varchar(50) not null,
      authority varchar(50) not null,
      constraint ix_authority_TEMP unique (username,authority));
      
CREATE TEMPORARY TABLE IF NOT EXISTS users_TEMP (
      username varchar(50) not null primary key,
      password varchar(50) not null,
      enabled boolean not null);

CREATE TEMPORARY TABLE IF NOT EXISTS user_info_TEMP (
	sub VARCHAR(256) not null primary key,
	preferred_username VARCHAR(256),
	name VARCHAR(256),
	given_name VARCHAR(256),
	family_name VARCHAR(256),
	middle_name VARCHAR(256),
	nickname VARCHAR(256),
	profile VARCHAR(256),
	picture VARCHAR(256),
	website VARCHAR(256),
	email VARCHAR(256),
	email_verified BOOLEAN,
	gender VARCHAR(256),
	zone_info VARCHAR(256),
	locale VARCHAR(256),
	phone_number VARCHAR(256),
	address_id VARCHAR(256),
	updated_time VARCHAR(256),
	birthdate VARCHAR(256)
);

CREATE TEMPORARY TABLE IF NOT EXISTS client_details_TEMP (
	client_description VARCHAR(256),
	dynamically_registered BOOLEAN,
	id_token_validity_seconds BIGINT,
	
	client_id VARCHAR(256),
	client_secret VARCHAR(2048),
	access_token_validity_seconds BIGINT,
	refresh_token_validity_seconds BIGINT,
	allow_introspection BOOLEAN,
	
	client_name VARCHAR(256)
);

CREATE TEMPORARY TABLE IF NOT EXISTS client_scope_TEMP (
	owner_id VARCHAR(256),
	scope VARCHAR(2048)
);

CREATE TEMPORARY TABLE IF NOT EXISTS client_redirect_uri_TEMP (
	owner_id VARCHAR(256),
	redirect_uri VARCHAR(2048) 
);

CREATE TEMPORARY TABLE IF NOT EXISTS client_grant_type_TEMP (
	owner_id VARCHAR(256),
	grant_type VARCHAR(2000)
);

CREATE TEMPORARY TABLE IF NOT EXISTS system_scope_TEMP (
	scope VARCHAR(256),
	description VARCHAR(4096),
	icon VARCHAR(256),
	restricted BOOLEAN,
	default_scope BOOLEAN
);


--
-- Indexes for MySQL
--

CREATE INDEX at_tv_idx ON access_token(token_value(767));
CREATE INDEX ts_oi_idx ON token_scope(owner_id);
CREATE INDEX at_exp_idx ON access_token(expiration);
CREATE INDEX rf_ahi_idx ON refresh_token(auth_holder_id);
CREATE INDEX rf_tv_idx ON refresh_token(token_value(105));
CREATE INDEX cd_ci_idx ON client_details(client_id);
CREATE INDEX at_ahi_idx ON access_token(auth_holder_id);
CREATE INDEX aha_oi_idx ON authentication_holder_authority(owner_id);
CREATE INDEX ahe_oi_idx ON authentication_holder_extension(owner_id);
CREATE INDEX ahrp_oi_idx ON authentication_holder_request_parameter(owner_id);
CREATE INDEX ahri_oi_idx ON authentication_holder_resource_id(owner_id);
CREATE INDEX ahrt_oi_idx ON authentication_holder_response_type(owner_id);
CREATE INDEX ahs_oi_idx ON authentication_holder_scope(owner_id);
CREATE INDEX ac_ahi_idx ON authorization_code(auth_holder_id);
CREATE INDEX suaa_oi_idx ON saved_user_auth_authority(owner_id);



--
-- Turn off autocommit and start a transaction so that we can use the temp tables
--

SET AUTOCOMMIT = 0;

START TRANSACTION;

--
-- Insert client information into the temporary tables. To add clients to the HSQL database, edit things here.
-- 

INSERT INTO client_details_TEMP (client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection) VALUES
	('client', 'secret', 'Test Client', false, null, 3600, 600, true);

INSERT INTO client_scope_TEMP (owner_id, scope) VALUES
	('client', 'openid'),
	('client', 'profile'),
	('client', 'email'),
	('client', 'address'),
	('client', 'phone'),
	('client', 'offline_access');

INSERT INTO client_redirect_uri_TEMP (owner_id, redirect_uri) VALUES
	('client', 'http://localhost/'),
	('client', 'http://localhost:8080/');
	
INSERT INTO client_grant_type_TEMP (owner_id, grant_type) VALUES
	('client', 'authorization_code'),
	('client', 'urn:ietf:params:oauth:grant_type:redelegate'),
	('client', 'implicit'),
	('client', 'refresh_token');
	
--
-- Merge the temporary clients safely into the database. This is a two-step process to keep clients from being created on every startup with a persistent store.
--

INSERT INTO client_details (client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection)
  SELECT client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection FROM client_details_TEMP
  ON DUPLICATE KEY UPDATE client_details.client_id = client_details.client_id;

INSERT INTO client_scope (owner_id, scope)
  SELECT id, scope FROM client_scope_TEMP, client_details WHERE client_details.client_id = client_scope_TEMP.owner_id
  ON DUPLICATE KEY UPDATE client_scope.owner_id = client_scope.owner_id;

INSERT INTO client_redirect_uri (owner_id, redirect_uri)
  SELECT id, redirect_uri FROM client_redirect_uri_TEMP, client_details WHERE client_details.client_id = client_redirect_uri_TEMP.owner_id
  ON DUPLICATE KEY UPDATE client_redirect_uri.owner_id = client_redirect_uri.owner_id;

INSERT INTO client_grant_type (owner_id, grant_type)
  SELECT id, grant_type FROM client_grant_type_TEMP, client_details WHERE client_details.client_id = client_grant_type_TEMP.owner_id
  ON DUPLICATE KEY UPDATE client_grant_type.owner_id = client_grant_type.owner_id;
    
-- 
-- Close the transaction and turn autocommit back on
-- 
    
COMMIT;

SET AUTOCOMMIT = 1;



--
-- Turn off autocommit and start a transaction so that we can use the temp tables
--

SET AUTOCOMMIT = 0;

START TRANSACTION;

--
-- Insert scope information into the temporary tables.
-- 

INSERT INTO system_scope_TEMP (scope, description, icon, restricted, default_scope) VALUES
  ('openid', 'log in using your identity', 'user', false, true),
  ('profile', 'basic profile information', 'list-alt', false, true),
  ('email', 'email address', 'envelope', false, true),
  ('address', 'physical address', 'home', false, true),
  ('phone', 'telephone number', 'bell', false, true),
  ('offline_access', 'offline access', 'time', false, false);
  
--
-- Merge the temporary scopes safely into the database. This is a two-step process to keep scopes from being created on every startup with a persistent store.
--

INSERT INTO system_scope (scope, description, icon, restricted, default_scope) 
  SELECT scope, description, icon, restricted, default_scope FROM system_scope_TEMP
  ON DUPLICATE KEY UPDATE system_scope.scope = system_scope.scope;

COMMIT;

SET AUTOCOMMIT = 1;



--
-- Turn off autocommit and start a transaction so that we can use the temp tables
--

SET AUTOCOMMIT = 0;

START TRANSACTION;

--
-- Insert user information into the temporary tables. To add users to the HSQL database, edit things here.
-- 

INSERT INTO users_TEMP (username, password, enabled) VALUES
  ('admin','password',true),
  ('user','password',true);


INSERT INTO authorities_TEMP (username, authority) VALUES
  ('admin','ROLE_ADMIN'),
  ('admin','ROLE_USER'),
  ('user','ROLE_USER');
    
-- By default, the username column here has to match the username column in the users table, above
INSERT INTO user_info_TEMP (sub, preferred_username, name, email, email_verified) VALUES
  ('90342.ASDFJWFA','admin','Demo Admin','admin@example.com', true),
  ('01921.FLANRJQW','user','Demo User','user@example.com', true);

 
--
-- Merge the temporary users safely into the database. This is a two-step process to keep users from being created on every startup with a persistent store.
--

INSERT INTO users (username, password, enabled)
  SELECT username, password, enabled FROM users_TEMP
  ON DUPLICATE KEY UPDATE users.username = users.username;

INSERT INTO authorities (username,authority)
  SELECT username, authority FROM authorities_TEMP
  ON DUPLICATE KEY UPDATE authorities.username = authorities.username;

INSERT INTO user_info (sub, preferred_username, name, email, email_verified)
  SELECT sub, preferred_username, name, email, email_verified FROM user_info_TEMP
  ON DUPLICATE KEY UPDATE user_info.preferred_username = user_info.preferred_username;
    
-- 
-- Close the transaction and turn autocommit back on
-- 
    
COMMIT;

SET AUTOCOMMIT = 1;


