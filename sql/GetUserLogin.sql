CREATE PROCEDURE GetUserLogin(
	@email VARCHAR(254)
) AS
SELECT Id, Salt, [Password] FROM [User] WHERE Email = @email
