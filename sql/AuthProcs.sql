-----------------------
-- CREATE A NEW USER --
-----------------------

CREATE PROCEDURE [CreateUser] (
	@email VARCHAR(254),
	@username VARCHAR(32),
	@salt CHAR(32),
	@password CHAR(64),
	@ID INT OUTPUT,
	@Hue INT OUTPUT
) AS
IF EXISTS (SELECT Email FROM [User] WHERE Email = @email)
BEGIN
	PRINT 'Email is already registered with an account.'
	RETURN 1
END
IF @username IS NULL
BEGIN
	PRINT 'Username cannot be null'
	RETURN 2
END
IF @password IS NULL
BEGIN
	PRINT 'Password cannot be null'
	RETURN 3
END
IF @salt IS NULL
BEGIN
	PRINT 'Password salt cannot be null'
	RETURN 4
END

INSERT INTO [User](Email, Username, Salt, [Password]) VALUES (@email, @username, @salt, @password)
SET @ID = @@IDENTITY
SELECT @Hue = Hue FROM [User] WHERE ID = @ID

RETURN 0
GO

-------------------------
-- GET USER LOGIN INFO --
-------------------------

CREATE PROCEDURE GetUserLogin(
	@email VARCHAR(254)
) AS
IF NOT @email IN (SELECT Email FROM [User])
BEGIN
	PRINT 'No user exists with specified email'
	RETURN 1
END
SELECT Id, Salt, [Password], Hue, [Admin] FROM [User] WHERE Email = @email
RETURN 0
GO
