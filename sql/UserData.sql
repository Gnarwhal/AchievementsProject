---------------------------------------
-- GET USER NAME AND STATS PROCEDURE --
---------------------------------------

CREATE PROCEDURE GetUserNameAndStats(
	@userId INT,
	@username VARCHAR(32) OUTPUT,
	@completed INT OUTPUT,
	@average INT OUTPUT,
	@perfect INT OUTPUT
)
AS
BEGIN TRANSACTION

SELECT @username = Username
FROM [User]
WHERE ID = @userId

IF @username IS NULL
BEGIN
	PRINT 'No user found with specified id'
	ROLLBACK TRANSACTION
	RETURN 1
END

DECLARE @progress TABLE (GameID INT, Completed INT, Total INT)
INSERT INTO @progress
	SELECT GameID, SUM(CASE WHEN Progress.Progress = Achievement.Stages THEN 1 ELSE 0 END) AS Completed, COUNT(AchievementID) AS Total
	FROM Achievement
	JOIN Progress ON
		Progress.UserID = @userId
		AND Progress.AchievementID = Achievement.ID
	GROUP BY GameID
COMMIT TRANSACTION

SELECT @completed = SUM(Completed)
FROM @progress

SELECT @average = AVG((Completed * 100) / Total)
FROM @progress

SELECT @perfect = COUNT(GameID)
FROM @progress
WHERE Completed = Total

RETURN 0
GO

----------------------------------
-- GET USER PLATFORMS PROCEDURE --
----------------------------------

CREATE PROCEDURE GetUserPlatforms(
	@userId INT
)
AS
SELECT [Platform].ID, [PlatformName], (CASE WHEN UserID IS NOT NULL THEN 1 ELSE 0 END) AS Connected
FROM [Platform]
LEFT JOIN IsOn ON IsOn.PlatformID = [Platform].ID
ORDER BY [Platform].ID
GO

--------------------------------
-- GET USER RATINGS PROCEDURE --
--------------------------------

CREATE PROCEDURE GetUserRatings(
	@userId INT
)
AS
SELECT Game.Name AS GameName, Achievement.Name AS AchievementName, Quality, Difficulty, Rating.[Description]
FROM Rating
JOIN Achievement ON Achievement.ID = Rating.AchievementID
JOIN Game ON Game.ID = Achievement.GameID
WHERE UserID = @userId
GO

------------------------------
-- GET USER IMAGE PROCEDURE --
------------------------------

CREATE PROCEDURE GetUserImage(
	@userId INT
)
AS
IF NOT EXISTS (SELECT * FROM [User] WHERE ID = @userId)
BEGIN
	PRINT 'No user with specified ID found'
	RETURN 1
END
SELECT PFP FROM [User] WHERE ID = @userId
RETURN 0
GO

------------------
-- SET USERNAME --
------------------

CREATE PROCEDURE SetUsername(
	@userId INT,
	@username VARCHAR(32)
)
AS
IF NOT EXISTS (SELECT * FROM [User] WHERE ID = @userId)
BEGIN
	PRINT 'No user with specified ID found'
	RETURN 1
END
UPDATE [User] SET Username = @username WHERE ID = @userId
RETURN 0
GO

------------------------------
-- SET USER IMAGE PROCEDURE --
------------------------------

CREATE PROCEDURE SetUserImage(
	@userId INT,
	@type VARCHAR(11)
)
AS
IF NOT EXISTS (SELECT * FROM [User] WHERE ID = @userId)
BEGIN
	PRINT 'No user with specified ID found'
	RETURN 1
END
UPDATE [User] SET PFP = @type WHERE ID = @userId
RETURN 0
GO

---------------------------
-- ADD USER TO PROCEDURE --
---------------------------

CREATE PROCEDURE AddPlatform(
	@userId INT,
	@platformId INT,
	@platformUserID VARCHAR(32)
)
AS
IF NOT EXISTS (SELECT * FROM [User] WHERE ID = @userId)
BEGIN
	PRINT 'No user with specified ID found'
	RETURN 1
END
IF NOT EXISTS (SELECT * FROM [Platform] WHERE ID = @platformId)
BEGIN
	PRINT 'No platform with specified ID found'
	RETURN 2
END
IF EXISTS (SELECT * FROM IsOn WHERE UserID = @userId AND PlatformID = @platformId)
BEGIN
	PRINT 'User already exists on platform'
	RETURN 3
END
INSERT INTO IsOn VALUES (@userId, @platformId, @platformUserId)
RETURN 0
GO

--------------------------------
-- REMOVE USER FROM PROCEDURE --
--------------------------------

CREATE PROCEDURE RemovePlatform(
	@userId INT,
	@platformId INT
)
AS
IF NOT EXISTS (SELECT * FROM [User] WHERE ID = @userId)
BEGIN
	PRINT 'No user with specified ID found'
	RETURN 1
END
IF NOT EXISTS (SELECT * FROM [Platform] WHERE ID = @platformId)
BEGIN
	PRINT 'No platform with specified ID found'
	RETURN 2
END
IF NOT EXISTS (SELECT * FROM IsOn WHERE UserID = @userId AND PlatformID = @platformId)
BEGIN
	PRINT 'User does not exist on platform'
	RETURN 3
END
DELETE FROM IsOn WHERE UserID = @userId AND PlatformID = @platformId
RETURN 0
GO
