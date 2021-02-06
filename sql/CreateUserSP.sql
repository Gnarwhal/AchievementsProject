CREATE PROCEDURE [dbo].[CreateUser] (
	@email VARCHAR(254),
	@username VARCHAR(32),
	@salt CHAR(32),
	@password CHAR(64),
	@ID INT OUTPUT
) AS
BEGIN TRANSACTION
IF EXISTS (SELECT Email FROM [User] WHERE Email = @email)
BEGIN
	PRINT 'Email is already registered with an account.'
	ROLLBACK TRANSACTION
	RETURN 1
END

INSERT INTO [User](Email, Username, Salt, [Password]) VALUES (@email, @username, @salt, @password)
SET @ID = @@IDENTITY
COMMIT TRANSACTION

RETURN 0

