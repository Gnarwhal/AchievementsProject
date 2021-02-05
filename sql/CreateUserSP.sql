CREATE PROCEDURE [dbo].[CreateUser] (
	@email varchar(254),
	@username varchar(32),
	@salt char(32),
	@password char(64)
) AS
BEGIN TRANSACTION
IF EXISTS (SELECT Email FROM [User] WHERE Email = @email)
BEGIN
	PRINT 'Email is already registered with an account.'
	ROLLBACK TRANSACTION
	RETURN 1
END

INSERT INTO [User](Email, Username, Salt, [Password]) VALUES (@email, @username, @salt, @password)
COMMIT TRANSACTION

RETURN 0

