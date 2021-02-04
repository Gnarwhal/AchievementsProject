CREATE PROCEDURE [dbo].[CreateUser] (
	@email varchar(254),
	@username varchar(32),
	@password char(256)
) AS
BEGIN TRANSACTION
IF EXISTS (SELECT Email FROM [User] WHERE Email = @email)
BEGIN
	RAISERROR ('Email is already registered with an account.', 14, 1)
	ROLLBACK TRANSACTION
	RETURN 1
END

INSERT INTO [User] VALUES (@email, @username, @password)
COMMIT TRANSACTION

RETURN 0

